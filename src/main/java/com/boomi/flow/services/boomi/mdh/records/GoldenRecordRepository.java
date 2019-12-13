package com.boomi.flow.services.boomi.mdh.records;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.*;
import com.boomi.flow.services.boomi.mdh.database.FieldMapper;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.google.common.base.Strings;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class GoldenRecordRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(GoldenRecordRepository.class);
    private final MdhClient client;
    private final GoldenRecordRequestBuilder goldenRecordRequestBuilder;

    @Inject
    public GoldenRecordRepository(MdhClient client, GoldenRecordRequestBuilder goldenRecordFilter)
    {
        this.client = client;
        this.goldenRecordRequestBuilder = goldenRecordFilter;
    }

    public void delete(ApplicationConfiguration configuration, String universeId, List<MObject> objects) {
        update(configuration, objects, universeId, "DELETE");
    }

    public List<MObject> findAll(ApplicationConfiguration configuration, String universe, ListFilter filter) {
        LOGGER.info("Loading golden records for the universe {} from the Atom at {} with the username {}", universe, configuration.getHubHostname(), configuration.getHubUsername());

        GoldenRecordQueryRequest request = goldenRecordRequestBuilder.build(configuration, universe, filter);

        GoldenRecordQueryResponse result = client.queryGoldenRecords(configuration.getHubHostname(), configuration.getHubUsername(), configuration.getHubToken(), universe, request);
        if (result == null || result.getRecords() == null || result.getResultCount() == 0) {
            return new ArrayList<>();
        }

        return result.getRecords().stream()
                .map(record -> Entities.createGoldenRecordMObject(universe, record.getRecordId(), record.getMObject(), record.getLinks()))
                .collect(Collectors.toList());
    }

    public List<MObject> update(ApplicationConfiguration configuration, String universeId, List<MObject> objects) {
        return update(configuration, objects, universeId, null);
    }

    private List<MObject> update(ApplicationConfiguration configuration, List<MObject> objects, String universeId, String operation) {
        Universe universe = client.findUniverse(configuration.getHubHostname(), configuration.getHubUsername(), configuration.getHubToken(), universeId);

        Map<String, List<MObject>> objectsBySource = objects.stream()
                .map(object -> Entities.setRandomUniqueIdIfEmpty(object, universe.getIdField()))
                .collect(Collectors.groupingBy(object -> object.getProperties()
                        .stream()
                        .filter(property -> property.getDeveloperName().equals(GoldenRecordConstants.SOURCE_ID_FIELD))
                        .map(Property::getContentValue)
//                        .filter(source -> source != null && source.isEmpty() == false)
                        .findFirst()
                        .orElseThrow(() -> new ServiceProblemException(400, "No Source ID was given for the record to update"))));

        for (Map.Entry<String, List<MObject>> sourceGroup : objectsBySource.entrySet()) {
            // TODO: Check if we should be setting this to a default value, or error if no source was set
            String sourceId;
            if (sourceGroup.getKey().isEmpty()){
                sourceId = GoldenRecordConstants.DEFAULT_SOURCE_ID;
            } else{
                sourceId = sourceGroup.getKey();
            }

            List<BatchUpdateRequest.Entity> entities = sourceGroup.getValue().stream()
                    .map(entity -> {
                        // Map all the properties to fields, except our "internal" ones
                        Map<String, Object> fields = FieldMapper.createMapFromModelMobject(entity, universe);

                        String randomUuid = UUID.randomUUID().toString();

                        // we are adding Golden Record Entity ID because has been removed with the rest of special properties
                        // the id is mandatory so if there is not Entity ID, we create a new random UUID
                        String entityId = entity.getProperties().stream()
                                .filter(p -> GoldenRecordConstants.ENTITY_ID_FIELD.equals(p.getDeveloperName()) && Strings.isNullOrEmpty(p.getContentValue()) == false)
                                .findFirst()
                                .map(Property::getContentValue)
                                .orElse(randomUuid);

                        fields.put(universe.getIdField(), entityId);

                        return new BatchUpdateRequest.Entity()
                                .setOp(operation)
                                .setName(universe.getLayout().getModel().getName())
                                .setFields(fields);
                    })
                    .collect(Collectors.toList());

            // Now we can save the records into the Hub
            BatchUpdateRequest updateRequest = new BatchUpdateRequest()
                    .setSource(sourceId)
                    .setEntities(entities);

            // NOTE: The endpoint returns a 202, not returning any created objects directly... how will this map? Do we care about creating golden records?
            client.updateGoldenRecords(
                    configuration.getHubHostname(),
                    configuration.getHubUsername(),
                    configuration.getHubToken(),
                    universe.getId().toString(),
                    updateRequest
            );
        }

        return objects;
    }
}
