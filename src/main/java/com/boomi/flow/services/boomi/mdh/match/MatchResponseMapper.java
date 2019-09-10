package com.boomi.flow.services.boomi.mdh.match;

import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;

import java.util.*;
import java.util.stream.Collectors;

public class MatchResponseMapper {

    public static List<MObject> createMobjectsFromResults(Universe universe, String universeId, List<MatchEntityResponse.MatchResult> results) {
        return results.stream()
                .filter(result -> result.getEntity() != null && result.getEntity().get(universe.getName()) != null)
                .map(result -> MatchResponseMapper.createMobjectFromResult(universeId, universe, result))
                .collect(Collectors.toList());

    }

    private static MObject createMobjectFromResult(String universeId, Universe universe, MatchEntityResponse.MatchResult result) {
        var externalId = result.getEntity().get(universe.getName()).get(universe.getIdField()).toString();

        var propertiesMatched = new Property(FuzzyMatchDetailsConstants.MATCH, new ArrayList<>());
        var propertiesDuplicated = new Property(FuzzyMatchDetailsConstants.DUPLICATE, new ArrayList<>());
        var propertiesAlreadyLinked = new Property(FuzzyMatchDetailsConstants.ALREADY_LINKED, new ArrayList<>());
        var object = new MObject(universeId + "-match", externalId, Arrays.asList(propertiesMatched, propertiesDuplicated, propertiesAlreadyLinked));
        object.setTypeElementBindingDeveloperName(object.getDeveloperName());

        var properties =  new ArrayList<Property>();
        result.getEntity().get(universe.getName())
                .forEach((key, value) -> properties.add(new Property(key, (String) value)));

        properties.add(new Property(GoldenRecordConstants.SOURCE_ID_FIELD, result.getIdResource()));
        properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, (MObject) null));
        properties.addAll(object.getProperties());

        object.setProperties(properties);

        if ("SUCCESS".equals(result.getStatus())) {
            result.getMatch().forEach(match -> addMatchesToProperty(propertiesMatched, universe, match, universe.getIdField(), true));
            result.getDuplicate().forEach(match -> addMatchesToProperty(propertiesDuplicated, universe, match, universe.getIdField(), true));
        } else if ("ALREADY_LINKED".equals(result.getStatus())) {
            var entityH = result.getEntity().get(universe.getName());
            addAlreadyLinked(propertiesAlreadyLinked, universe, entityH, universe.getIdField());
        }

        return object;
    }


    private static void addAlreadyLinked(Property alreadyLinkedProperty, Universe universe, Map<String, Object> entity, String idField){
        var object = new MObject(universe.getId().toString() + "-match");
        object.setExternalId(entity.get(idField).toString());

        entity.forEach((key, value) -> {
            var property = new Property(key, value);
            object.getProperties().add(property);
        });

        alreadyLinkedProperty.getObjectData().add(object);
    }

    private static void addMatchesToProperty(Property propertyMatches, Universe universe, Map<String, Object> matchResults, String idField, boolean addFuzzyMatchDetails){
        var entity = (Map<String, Object>) matchResults.get(universe.getName());
        var object = new MObject(universe.getId().toString() + "-match");
        object.setTypeElementBindingDeveloperName(object.getDeveloperName());

        object.setExternalId(entity.get(idField).toString());

        entity.forEach((key, value) -> {
            var property = new Property(key, value);
            object.getProperties().add(property);
        });

        if (addFuzzyMatchDetails) {
            addFuzzyMatchDetails(object, (Map<String, Object>) matchResults.get("fuzzyMatchDetails"));
        }

        propertyMatches.getObjectData().add(object);
    }

    private static void addFuzzyMatchDetails(MObject object, Map<String, Object> result) {
        var fuzzyMatchEmpty = new MObject(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS);
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

        object.getProperties().add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, fuzzyMatchEmpty));
    }
}
