package com.boomi.flow.services.boomi.mdh.match;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.Entities;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.google.common.base.Strings;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class MatchEntityRepository {

    private final MdhClient client;

    @Inject
    public MatchEntityRepository(MdhClient mdhClient) {
        this.client = mdhClient;
    }

    public List<MObject> matchEntity(ApplicationConfiguration configuration, String universeId, List<MObject> objects)  {
        var universe = client.findUniverse(configuration.getAtomHostname(), configuration.getAtomUsername(), configuration.getAtomPassword(), universeId);
        List<MatchEntityResponse.MatchResult> results = new ArrayList<>();

        objects.stream()
                .filter(object -> Strings.isNullOrEmpty(object.getExternalId()))
                .forEach(object -> Entities.AddRandomUniqueId(object, universe.getIdField()));

        var objectsBySource = objects.stream()
                .collect(Collectors.groupingBy(object -> object.getProperties()
                        .stream()
                        .filter(property -> property.getDeveloperName().equals(GoldenRecordConstants.SOURCE_ID_FIELD))
                        .map(Property::getContentValue)
//                        .filter(source -> source != null && source.isEmpty() == false)
                        .findFirst()
                        .orElseThrow(() -> new ServiceProblemException(400, "No Source ID was given for the record to update"))));

        for (var sourceGroup : objectsBySource.entrySet()) {
            // TODO: Check if we should be setting this to a default value, or error if no source was set
            var sourceId = sourceGroup.getKey().isBlank()
                    ? GoldenRecordConstants.DEFAULT_SOURCE_ID
                    : sourceGroup.getKey();

            var entities = sourceGroup.getValue().stream()
                    .map(entity -> {
                        // Map all the properties to fields, except our "internal" ones
                        var fields = entity.getProperties().stream()
                                .filter(property -> property.getDeveloperName().startsWith("___") == false)
                                .filter(property -> property.getContentValue() != null)
                                .collect(Collectors.toMap(
                                        Property::getDeveloperName,
                                        property -> (Object) property.getContentValue()
                                ));

                        fields.put(universe.getIdField(), entity.getExternalId());

                        return new BatchUpdateRequest.Entity()
                                .setOp(null)
                                .setName(universe.getLayout().getModel().getName())
                                .setFields(fields);
                    })
                    .collect(Collectors.toList());

            // Now we ned to load the match entity information
            var updateRequest = new BatchUpdateRequest()
                    .setSource(sourceId)
                    .setEntities(entities);


            MatchEntityResponse matchResponse = client.queryMatchEntity(configuration.getAtomHostname(),
                    configuration.getAtomUsername(), configuration.getAtomPassword(), universe.getId().toString(),
                    updateRequest);

            List<MatchEntityResponse.MatchResult> matchesResult = matchResponse.getMatchResults();
            matchesResult.forEach(result -> result.setIdResource(sourceId));
            results.addAll(matchesResult);
        }

        return results.stream()
                .filter(result -> result.getEntity() != null && result.getEntity().get(universe.getName()) != null)
                .map(result -> {
                    var externalId = result.getEntity().get(universe.getName()).get(universe.getIdField()).toString();

                    var propertiesMatched = new Property(FuzzyMatchDetialsConstants.MATCH_FIELD, new ArrayList<>());
                    var propertiesDuplicated = new Property(FuzzyMatchDetialsConstants.DUPLICATE_FIELD, new ArrayList<>());
                    var propertiesAlreadyLinked = new Property(FuzzyMatchDetialsConstants.ALREADY_LINKED_FIELD, new ArrayList<>());


                    var object = new MObject(universeId + " match", externalId, Arrays.asList(propertiesMatched, propertiesDuplicated, propertiesAlreadyLinked));

                    var properties =  new ArrayList<Property>();
                    result.getEntity().get(universe.getName())
                            .forEach((key, value) -> properties.add(new Property(key, (String) value)));

                    properties.add(new Property(GoldenRecordConstants.SOURCE_ID_FIELD, result.getIdResource()));
                    properties.addAll(object.getProperties());

                    object.setProperties(properties);

                    result.getMatch().forEach(match -> addMatchesToProperty(propertiesMatched, universe, match, universe.getIdField(), true));
                    result.getDuplicate().forEach(match -> addMatchesToProperty(propertiesDuplicated, universe, match, universe.getIdField(), true));
                    result.getDuplicate().forEach(match -> addMatchesToProperty(propertiesAlreadyLinked, universe, match, universe.getIdField(), false));

                    return object;
                }).collect(Collectors.toList());
    }

    private static void addMatchesToProperty(Property propertyMatches, Universe universe, Map<String, Object> matchResults, String idField, boolean addFuzzyMatchDetails){
            var entity = (Map<String, Object>)matchResults.get(universe.getName());
            var object = new MObject(universe.getId().toString() + " match");
            object.setExternalId(entity.get(idField).toString());
            entity.forEach((key, value) -> {
                var property = new Property(key, value);
                object.getProperties().add(property);
            });

            if (addFuzzyMatchDetails) {
                addFuzzyMatchDetails(object, (HashMap<String, Object>) matchResults.get("fuzzyMatchDetails"));
            }
            propertyMatches.getObjectData().add(object);
    }

    private static void addFuzzyMatchDetails(MObject object, Map<String, Object> result) {
        var fuzzyMatchEmpty = new MObject(FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS);
        fuzzyMatchEmpty.setExternalId(UUID.randomUUID().toString());
        var properties = new ArrayList<Property>();
        if (result != null) {
            properties.add(new Property("Field", result.get("field")));
            properties.add(new Property("First", result.get("first")));
            properties.add(new Property("Second", result.get("second")));
            properties.add(new Property("Method", result.get("method")));
            properties.add(new Property("Match Strength", result.get("matchStrength")));
            properties.add(new Property("Threshold", result.get("threshold")));
        }
        fuzzyMatchEmpty.setProperties(properties);

        object.getProperties().add(new Property(FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS, fuzzyMatchEmpty));
    }
}
