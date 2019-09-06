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

        List<MatchEntityResponse.MatchResult> results = new ArrayList<>();
        for (var sourceGroup : objectsBySource.entrySet()) {
            // TODO: Check if we should be setting this to a default value, or error if no source was set
            var sourceId = sourceGroup.getKey().isBlank()
                    ? GoldenRecordConstants.DEFAULT_SOURCE_ID
                    : sourceGroup.getKey();

            var entities = sourceGroup.getValue().stream()
                    .map(entity -> createUpdateEntity(universe, entity))
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

        return MatchResponseMapper.createMobjectsFromResults(universe, universeId, results);
    }

    private BatchUpdateRequest.Entity createUpdateEntity(Universe universe, MObject entity) {
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
    }
}
