package com.boomi.flow.services.boomi.mdh.match;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.Entities;
import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
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

    public List<MObject> matchEntity(ApplicationConfiguration configuration, String universeId, List<MObject> objects) {
        var universe = client.findUniverse(configuration.getHubHostname(), configuration.getHubUsername(), configuration.getHubToken(), universeId);

        var objectsBySource = objects.stream()
                .map(object -> Entities.setRandomUniqueIdIfEmpty(object, universe.getIdField()))
                .collect(Collectors.groupingBy(object ->
                        object.getProperties()
                        .stream()
                        .filter(property -> property.getDeveloperName().equals(GoldenRecordConstants.SOURCE_ID_FIELD))
                        .map(Property::getContentValue)
//                        .filter(source -> source != null && source.isEmpty() == false)
                        .findFirst()
                        .orElseThrow(() -> new ServiceProblemException(400, "No Source ID was given for the record to update"))));

        List<List<MatchEntityResponse.MatchResult>> resultsList = new ArrayList<>();
        for (var sourceGroup : objectsBySource.entrySet()) {
            // TODO: Check if we should be setting this to a default value, or error if no source was set
            var sourceId = sourceGroup.getKey().isBlank()
                    ? GoldenRecordConstants.DEFAULT_SOURCE_ID
                    : sourceGroup.getKey();

            var entities = sourceGroup.getValue().stream()
                    .map(entity -> createUpdateEntity(universe, entity))
                    .collect(Collectors.toList());

            resultsList.add(getResults(configuration, entities, sourceId, universe));
        }

        return resultsList.stream()
                .flatMap(Collection::stream)
                .filter(result -> result.getEntity() != null && result.getEntity().get(universe.getName()) != null)
                .map(matchResult -> Entities.createMatchMObject(universeId, universe, matchResult))
                .collect(Collectors.toList());
    }

    private List<MatchEntityResponse.MatchResult> getResults(ApplicationConfiguration configuration,
                                                       List<BatchUpdateRequest.Entity> entities, String sourceId,
                                                       Universe universe) {

        // Now we ned to load the match entity information
        var updateRequest = new BatchUpdateRequest()
                .setSource(sourceId)
                .setEntities(entities);

        MatchEntityResponse matchResponse = client.queryMatchEntity(configuration.getHubHostname(),
                configuration.getHubUsername(), configuration.getHubToken(), universe.getId().toString(),
                updateRequest);

        for (var result: matchResponse.getMatchResults()) {
            result.setIdResource(sourceId);
        }

        return matchResponse.getMatchResults();
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
