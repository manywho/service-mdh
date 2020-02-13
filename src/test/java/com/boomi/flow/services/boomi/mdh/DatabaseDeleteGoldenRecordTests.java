package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.database.MdhRawDatabase;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityRepository;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordRepository;
import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.google.common.collect.ImmutableMap;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseDeleteGoldenRecordTests {
    @Mock
    private MdhClient client;

    private ObjectDataType objectDataType = new ObjectDataType()
            .setDeveloperName("12fa66f9-e14d-f642-878f-030b13b64731-golden-record");

    private List<Universe.Layout.Model.Element> createElements(List<String> uniqueIds, List<String> names) {
        List<Universe.Layout.Model.Element> elements = new ArrayList<>();

        for(int i=0; i<uniqueIds.size(); i++) {
            String name = names.get(i);
            String uniqueId = uniqueIds.get(i);
            Universe.Layout.Model.Element element = new Universe.Layout.Model.Element();
            element.setUniqueId(uniqueId);
            element.setName(name);

            elements.add(element);
        }

        return elements;
    }

    @Test
    public void testDeleteWithSingleNewObjectWorks() {

        List<String> uniqueIds = Arrays.asList(
                "field 1 1",
                "field 2 1",
                "field 3 1"
        );
        List<String> names = Arrays.asList(
                "field 1 1",
                "field 2 1",
                "field 3 1"
        );

        // Make sure we return the expected universe layout for the test
        when(client.findUniverse(any(), any(), any(), eq("12fa66f9-e14d-f642-878f-030b13b64731")))
                .thenReturn(new Universe()
                        .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                        .setName("testing")
                        .setLayout(new Universe.Layout()
                                .setIdXPath("/item/id")
                                .setModel(new Universe.Layout.Model()
                                        .setName("testing")
                                        .setElements(createElements(uniqueIds, names)))));

        // Construct the incoming object
        MObject object = new MObject(objectDataType.getDeveloperName());
        object.setExternalId("28cd81e7-c3f4-4174-824b-b1f5176fc64a");
        object.getProperties().add(new Property(GoldenRecordConstants.ENTITY_ID_FIELD, "28cd81e7-c3f4-4174-824b-b1f5176fc64a"));
        object.getProperties().add(new Property("___sourceId", "TESTING"));
        object.getProperties().add(new Property("field 1 1", "some value 1"));
        object.getProperties().add(new Property("field 2 1", "some value 2"));
        object.getProperties().add(new Property("field 3 1", "some value 3"));

        // Delete the incoming object
        new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .delete(TestConstants.CONFIGURATION, objectDataType, object);

        // Make sure we perform the delete in MDH, with the request that we're expecting
        BatchUpdateRequest expectedRequest = new BatchUpdateRequest()
                .setEntities(Arrays.asList(
                        new BatchUpdateRequest.Entity()
                                .setName("testing")
                                .setFields(ImmutableMap.<String, Object>builder()
                                        .put("id", "28cd81e7-c3f4-4174-824b-b1f5176fc64a")
                                        .put("field 1 1", "some value 1")
                                        .put("field 2 1", "some value 2")
                                        .put("field 3 1", "some value 3")
                                        .build()
                                )
                                .setOp("DELETE")
                ))
                .setSource("TESTING");

        verify(client)
                .updateGoldenRecords(
                        TestConstants.CONFIGURATION.getHubHostname(),
                        TestConstants.CONFIGURATION.getHubUsername(),
                        TestConstants.CONFIGURATION.getHubToken(),
                        "12fa66f9-e14d-f642-878f-030b13b64731",
                        expectedRequest
                );
    }
}
