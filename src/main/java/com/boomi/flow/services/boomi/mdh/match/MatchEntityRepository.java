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
        Universe universe = client.findUniverse(configuration.getHubHostname(), configuration.getHubUsername(), configuration.getHubToken(), universeId);

        Map<String, List<MObject>> objectsBySource = objects.stream()
                .map(object -> Entities.setRandomUniqueIdIfEmpty(object, universe.getIdField(), true))
                .collect(Collectors.groupingBy(object ->
                        object.getProperties()
                        .stream()
                        .filter(property -> property.getDeveloperName().equals(GoldenRecordConstants.SOURCE_ID_FIELD))
                        .map(Property::getContentValue)
                        .findFirst()
                        .orElseThrow(() -> new ServiceProblemException(400, "No Source ID was given for the record to update"))));

        List<List<MatchEntityResponse.MatchResult>> resultsList = new ArrayList<>();
        for (Map.Entry<String, List<MObject>> sourceGroup : objectsBySource.entrySet()) {
            // TODO: Check if we should be setting this to a default value, or error if no source was set
            String sourceId = sourceGroup.getKey().isEmpty()
                    ? GoldenRecordConstants.DEFAULT_SOURCE_ID
                    : sourceGroup.getKey();

            List<BatchUpdateRequest.Entity> entities = sourceGroup.getValue().stream()
                    .map(mObject -> createUpdateEntity(universe, mObject))
                    .collect(Collectors.toList());

            resultsList.add(getResults(configuration, entities, sourceId, universe));
        }

        return resultsList.stream()
                .flatMap(Collection::stream)
                .filter(result -> result.getEntity() != null)
                .map(matchResult -> Entities.createMatchMObject(universeId, universe, matchResult))
                .collect(Collectors.toList());
    }

    private List<MatchEntityResponse.MatchResult> getResults(ApplicationConfiguration configuration,
                                                       List<BatchUpdateRequest.Entity> entities, String sourceId,
                                                       Universe universe) {

        // Now we ned to load the match entity information
        BatchUpdateRequest updateRequest = new BatchUpdateRequest()
                .setSource(sourceId)
                .setEntities(entities);

        MatchEntityResponse matchResponse = client.queryMatchEntity(configuration.getHubHostname(),
                configuration.getHubUsername(), configuration.getHubToken(), universe.getId().toString(),
                updateRequest);

        for (MatchEntityResponse.MatchResult result: matchResponse.getMatchResults()) {
            result.setIdResource(sourceId);
        }

        return matchResponse.getMatchResults();
    }

    private BatchUpdateRequest.Entity createUpdateEntity(Universe universe, MObject entity) {
        // Map all the properties to fields, except our "internal" ones
        Map<String, Object> fields = mObjectToMap(entity);
        fields.put(universe.getIdField(), extractFieldIdValue(entity));

        return new BatchUpdateRequest.Entity()
                .setOp(null)
                .setName(universe.getLayout().getModel().getName())
                .setFields(fields);
    }

    private String extractFieldIdValue(MObject object) {
        return object.getProperties()
                .stream()
                .filter(p -> GoldenRecordConstants.ENTITY_ID_FIELD.equals(p.getDeveloperName()))
                .findFirst()
                .map(Property::getContentValue)
                .orElse("");
    }

    private Map<String, Object> mObjectToMap(MObject mObject) {
        HashMap<String, Object> map = new HashMap<>();
        for (Property property: mObject.getProperties()) {
            if (property.getDeveloperName().startsWith("___")) {
                continue;
            }
            if (property.getContentValue() != null) {
                map.put(property.getDeveloperName(), property.getContentValue());
            } else if (property.getObjectData() != null && property.getObjectData().size() == 1) {
                map.put(property.getDeveloperName(), mObjectToMap(property.getObjectData().get(0)));
            }
        }

        return map;
    }
}
