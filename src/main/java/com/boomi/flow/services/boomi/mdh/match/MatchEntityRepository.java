package com.boomi.flow.services.boomi.mdh.match;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.google.common.base.Strings;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MatchEntityRepository {

    private final MdhClient client;

    @Inject
    public MatchEntityRepository(MdhClient mdhClient) {
        this.client = mdhClient;
    }

    public List<MObject> matchEntity(ApplicationConfiguration configuration, String universeId, List<MObject> objects)  {


        var universe = client.findUniverse(configuration.getAtomHostname(), configuration.getAtomUsername(), configuration.getAtomPassword(), universeId);

        // TODO: This isn't correct - it would be great to be able to get the actual ID field name (or make a global standard named one)
        String idField = universe.getLayout().getIdXPath()
                .split("/")
                [2];

        for (var object : objects) {
            if (Strings.isNullOrEmpty(object.getExternalId())) {
                // We're creating this object so let's collectTypes an ID
                var id = UUID.randomUUID().toString();

                // Set the ID property, so it can be referenced in a Flow
                for (var property : object.getProperties()) {
                    if (property.getDeveloperName().equals(idField)) {
                        property.setContentValue(id);
                    }
                }

                // Set the object's external ID too, which is only used inside Flow itself
                object.setExternalId(id);
            }
        }

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

                        fields.put(idField, entity.getExternalId());

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

            var matchEntityResponse = client.queryMatchEntity(
                    configuration.getAtomHostname(),
                    configuration.getAtomUsername(),
                    configuration.getAtomPassword(),
                    universe.getId().toString(),
                    updateRequest
            );

            // add match Response to objects
        }



        return objects;
    }
}
