package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.database.MdhRawDatabase;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityRepository;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordRepository;
import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

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
    public void testSaveWithSingleExistingObjectReturnsObject() {
        List<String> uniqueIds = Arrays.asList(
                "field 1 1",
                "field 2 1",
                "field 3 1",
                "object field 4",
                "property 4 1"
        );
        List<String> names = Arrays.asList(
                "field 1 1",
                "field 2 1",
                "field 3 1",
                "object field 4",
                "property 4 1"
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
        object.setExternalId("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        object.getProperties().add(new Property(GoldenRecordConstants.ENTITY_ID_FIELD, "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        object.getProperties().add(new Property("___sourceId", "TESTING"));
        object.getProperties().add(new Property("field 1 1", "some value 1"));
        object.getProperties().add(new Property("field 2 1", "", ContentType.DateTime));
        object.getProperties().add(new Property("field 3 1", "2019-09-10T15:45:00+01:00", ContentType.DateTime));

        MObject objectField4 = new MObject("testing - object field 4");
        objectField4.setExternalId("123");
        objectField4.setProperties(Arrays.asList(new Property("property 4 1", "value property 4 1")));

        object.getProperties().add(new Property("testing - object field 4", objectField4, ContentType.Object));

        // Update using the incoming object
        MObject result = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .update(TestConstants.CONFIGURATION, objectDataType, object);

        // Make sure we perform the update in MDH, with the request that we're expecting
        Map<String, Object> expectedFieldsRequest = new HashMap<>();
        expectedFieldsRequest.put("id", "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        expectedFieldsRequest.put("field 1 1", "some value 1");
        expectedFieldsRequest.put("field 3 1", "2019-09-10T14:45:00Z");
        Map<String, Object> field41 = new HashMap<>();
        field41.put("property 4 1", "value property 4 1");
        expectedFieldsRequest.put("object field 4", field41);

        BatchUpdateRequest expectedRequest = new BatchUpdateRequest()
                .setEntities(Arrays.asList(
                        new BatchUpdateRequest.Entity()
                                .setName("testing")
                                .setFields(expectedFieldsRequest)
                                .setOp(null)
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

        assertThat(result, not(nullValue()));
        assertThat(result.getDeveloperName(), equalTo(objectDataType.getDeveloperName()));
        assertThat(result.getExternalId(), not(isEmptyOrNullString()));
        assertThat(result.getProperties(), hasSize(6));
        assertThat(result.getProperties().get(0).getDeveloperName(), equalTo("___entityId"));
        assertThat(result.getProperties().get(0).getContentValue(), equalTo("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        assertThat(result.getProperties().get(1).getDeveloperName(), equalTo("___sourceId"));
        assertThat(result.getProperties().get(1).getContentValue(), equalTo("TESTING"));
        assertThat(result.getProperties().get(2).getDeveloperName(), equalTo("field 1 1"));
        assertThat(result.getProperties().get(2).getContentValue(), equalTo("some value 1"));
        assertThat(result.getProperties().get(3).getDeveloperName(), equalTo("field 2 1"));
        assertThat(result.getProperties().get(3).getContentValue(), equalTo(""));
        assertThat(result.getProperties().get(4).getDeveloperName(), equalTo("field 3 1"));
        assertThat(result.getProperties().get(4).getContentValue(), equalTo("2019-09-10T15:45:00+01:00"));
        assertThat(result.getProperties().get(5).getContentValue(), nullValue());
        assertThat(result.getProperties().get(5).getDeveloperName(), equalTo("testing - object field 4"));
        assertThat(result.getProperties().get(5).getObjectData().get(0).getDeveloperName(), equalTo("testing - object field 4"));
        assertThat(result.getProperties().get(5).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("property 4 1"));
        assertThat(result.getProperties().get(5).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("value property 4 1"));
    }

    @Test
    public void testSaveGoldenRecordWithNullSourceId() {
        List<String> uniqueIds = Arrays.asList(
                "field 1 1"
        );
        List<String> names = Arrays.asList(
                "field 1 1"
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
        object.setExternalId("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        object.getProperties().add(new Property(GoldenRecordConstants.ENTITY_ID_FIELD, "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        object.getProperties().add(new Property("___sourceId", (String) null));
        object.getProperties().add(new Property("field 1 1", "some value 1"));


        // Update using the incoming object
        MObject result = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .update(TestConstants.CONFIGURATION, objectDataType, object);

        // Make sure we perform the update in MDH, with the request that we're expecting
        Map<String, Object> expectedFieldsRequest = new HashMap<>();
        expectedFieldsRequest.put("id", "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        expectedFieldsRequest.put("field 1 1", "some value 1");

        BatchUpdateRequest expectedRequest = new BatchUpdateRequest()
                .setEntities(Arrays.asList(
                        new BatchUpdateRequest.Entity()
                                .setName("testing")
                                .setFields(expectedFieldsRequest)
                                .setOp(null)
                ))
                .setSource("flow");

        // we are verifying that source is flow
        verify(client)
                .updateGoldenRecords(
                        TestConstants.CONFIGURATION.getHubHostname(),
                        TestConstants.CONFIGURATION.getHubUsername(),
                        TestConstants.CONFIGURATION.getHubToken(),
                        "12fa66f9-e14d-f642-878f-030b13b64731",
                        expectedRequest
                );
    }

    @Test
    public void testSaveGoldenRecordWithEmptySourceId() {
        List<String> uniqueIds = Arrays.asList(
                "field 1 1"
        );
        List<String> names = Arrays.asList(
                "field 1 1"
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
        object.setExternalId("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        object.getProperties().add(new Property(GoldenRecordConstants.ENTITY_ID_FIELD, "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        object.getProperties().add(new Property("___sourceId", ""));
        object.getProperties().add(new Property("field 1 1", "some value 1"));


        // Update using the incoming object
        MObject result = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .update(TestConstants.CONFIGURATION, objectDataType, object);

        // Make sure we perform the update in MDH, with the request that we're expecting
        Map<String, Object> expectedFieldsRequest = new HashMap<>();
        expectedFieldsRequest.put("id", "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        expectedFieldsRequest.put("field 1 1", "some value 1");

        BatchUpdateRequest expectedRequest = new BatchUpdateRequest()
                .setEntities(Arrays.asList(
                        new BatchUpdateRequest.Entity()
                                .setName("testing")
                                .setFields(expectedFieldsRequest)
                                .setOp(null)
                ))
                .setSource("flow");

        // we are verifying that source is flow
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
