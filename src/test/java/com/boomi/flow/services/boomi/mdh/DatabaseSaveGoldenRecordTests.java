package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.database.MdhRawDatabase;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordRepository;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordUpdateRequest;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseSaveGoldenRecordTests {
    @Mock
    private MdhClient client;

    private ObjectDataType objectDataType = new ObjectDataType()
            .setDeveloperName("golden-record-12fa66f9-e14d-f642-878f-030b13b64731");

    @Test
    public void testSaveWithSingleExistingObjectReturnsObject() {
        // Make sure we return the expected universe layout for the test
        when(client.findUniverse(any(), any(), any(), eq("12fa66f9-e14d-f642-878f-030b13b64731")))
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
        object.setExternalId("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        object.getProperties().add(new Property("id", "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        object.getProperties().add(new Property("___sourceId", "TESTING"));
        object.getProperties().add(new Property("field 1 1", "some value 1"));
        object.getProperties().add(new Property("field 2 1", "some value 2"));
        object.getProperties().add(new Property("field 3 1", "some value 3"));

        // Update using the incoming object
        MObject result = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client))
                .update(TestConstants.CONFIGURATION, objectDataType, object);

        // Make sure we perform the update in MDH, with the request that we're expecting
        var expectedRequest = new GoldenRecordUpdateRequest()
                .setEntities(List.of(
                        new GoldenRecordUpdateRequest.Entity()
                                .setName("testing")
                                .setFields(Map.ofEntries(
                                        Map.entry("id", "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"),
                                        Map.entry("field 1 1", "some value 1"),
                                        Map.entry("field 2 1", "some value 2"),
                                        Map.entry("field 3 1", "some value 3")
                                ))
                                .setOp(null)
                ))
                .setSource("TESTING");

        verify(client)
                .updateGoldenRecords(
                        TestConstants.CONFIGURATION.getAtomHostname(),
                        TestConstants.CONFIGURATION.getAtomUsername(),
                        TestConstants.CONFIGURATION.getAtomPassword(),
                        "12fa66f9-e14d-f642-878f-030b13b64731",
                        expectedRequest
                );

        assertThat(result, not(nullValue()));
        assertThat(result.getDeveloperName(), equalTo(objectDataType.getDeveloperName()));
        assertThat(result.getExternalId(), not(isEmptyOrNullString()));
        assertThat(result.getProperties(), hasSize(5));
        assertThat(result.getProperties().get(0).getDeveloperName(), equalTo("id"));
        assertThat(result.getProperties().get(0).getContentValue(), equalTo("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        assertThat(result.getProperties().get(1).getDeveloperName(), equalTo("___sourceId"));
        assertThat(result.getProperties().get(1).getContentValue(), equalTo("TESTING"));
        assertThat(result.getProperties().get(2).getDeveloperName(), equalTo("field 1 1"));
        assertThat(result.getProperties().get(2).getContentValue(), equalTo("some value 1"));
        assertThat(result.getProperties().get(3).getDeveloperName(), equalTo("field 2 1"));
        assertThat(result.getProperties().get(3).getContentValue(), equalTo("some value 2"));
        assertThat(result.getProperties().get(4).getDeveloperName(), equalTo("field 3 1"));
        assertThat(result.getProperties().get(4).getContentValue(), equalTo("some value 3"));
    }
}
