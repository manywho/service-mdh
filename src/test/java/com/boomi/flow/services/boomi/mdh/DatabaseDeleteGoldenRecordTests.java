package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.database.MdhRawDatabase;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityRepository;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
import com.boomi.flow.services.boomi.mdh.records.ElementIdFinder;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordRepository;
import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
            .setDeveloperName("universe-name-golden-record");

    @Test
    public void testDeleteWithSingleNewObjectWorks() {
        // Make sure we return the expected universe layout for the test
        when(client.findUniverse(any(), any(), any(), eq("universe-name")))
                .thenReturn(new Universe()
                        .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                        .setLayout(new Universe.Layout()
                                .setIdXPath("/item/id")
                                .setModel(new Universe.Layout.Model()
                                        .setName("testing")
                                )
                        )
                );

        // Construct the incoming object
        MObject object = new MObject(objectDataType.getDeveloperName());
        object.setExternalId("28cd81e7-c3f4-4174-824b-b1f5176fc64a");
        object.getProperties().add(new Property("id", "28cd81e7-c3f4-4174-824b-b1f5176fc64a"));
        object.getProperties().add(new Property("___sourceId", "TESTING"));
        object.getProperties().add(new Property("field 1 1", "some value 1"));
        object.getProperties().add(new Property("field 2 1", "some value 2"));
        object.getProperties().add(new Property("field 3 1", "some value 3"));

        // Delete the incoming object
        new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client, new ElementIdFinder(null)), new MatchEntityRepository(client))
                .delete(TestConstants.CONFIGURATION, objectDataType, object);

        // Make sure we perform the delete in MDH, with the request that we're expecting
        BatchUpdateRequest expectedRequest = new BatchUpdateRequest()
                .setEntities(Arrays.asList(
                        new BatchUpdateRequest.Entity()
                                .setName("testing")
                                .setFields(Map.ofEntries(
                                        Map.entry("id", "28cd81e7-c3f4-4174-824b-b1f5176fc64a"),
                                        Map.entry("field 1 1", "some value 1"),
                                        Map.entry("field 2 1", "some value 2"),
                                        Map.entry("field 3 1", "some value 3")
                                ))
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
