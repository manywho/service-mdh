package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.database.MdhRawDatabase;
import com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetialsConstants;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityRepository;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityResponse;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
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

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseLoadMatchEntryTests {
    @Mock
    private MdhClient client;

    private ObjectDataType objectDataType = new ObjectDataType()
            .setDeveloperName("12fa66f9-e14d-f642-878f-030b13b64731 match");

    @Test
    public void testLoadMatchEntityObjectsReturnsObject() {
        // Make sure we return the expected universe layout for the test
        when(client.findUniverse(any(), any(), any(), eq("12fa66f9-e14d-f642-878f-030b13b64731")))
                .thenReturn(new Universe()
                    .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                    .setName("testing")
                    .setLayout(new Universe.Layout()
                            .setIdXPath("/item/id")
                            .setModel(new Universe.Layout.Model()
                                    .setName("testing"))));

        when(client.queryMatchEntity(any(), any(), any(), eq("12fa66f9-e14d-f642-878f-030b13b64731"), eq(createBatchUpdateRequest())))
                .thenReturn(createMatchEntityResponse());

        // Update using the incoming object
        List<MObject> result = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, null, List.of(createObjectToUpdate()));

        // Make sure we perform the update in MDH, with the request that we're expecting
        var expectedRequest = new BatchUpdateRequest()
                .setEntities(List.of(new BatchUpdateRequest.Entity()
                                .setName("testing")
                                .setFields(Map.ofEntries(
                                        Map.entry("id", "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"),
                                        Map.entry("field 1 1", "some value 1"),
                                        Map.entry("field 2 1", "some value 2"),
                                        Map.entry("field 3 1", "some value 3")
                                ))
                                .setOp(null)
                )).setSource("TESTING");

        verify(client)
                .queryMatchEntity(
                        TestConstants.CONFIGURATION.getAtomHostname(),
                        TestConstants.CONFIGURATION.getAtomUsername(),
                        TestConstants.CONFIGURATION.getAtomPassword(),
                        "12fa66f9-e14d-f642-878f-030b13b64731",
                        expectedRequest
                );

        assertThat(result.get(0), not(nullValue()));
        assertThat(result.get(0).getDeveloperName(), equalTo(objectDataType.getDeveloperName()));
        assertThat(result.get(0).getExternalId(), not(isEmptyOrNullString()));
        assertThat(result.get(0).getProperties(), hasSize(8));
        assertThat(result.get(0).getProperties().get(0).getDeveloperName(), equalTo("id"));
        assertThat(result.get(0).getProperties().get(0).getContentValue(), equalTo("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        assertThat(result.get(0).getProperties().get(1).getDeveloperName(), equalTo("field 1 1"));
        assertThat(result.get(0).getProperties().get(1).getContentValue(), equalTo("some value 1"));
        assertThat(result.get(0).getProperties().get(2).getDeveloperName(), equalTo("field 1 2"));
        assertThat(result.get(0).getProperties().get(2).getContentValue(), equalTo("some value 2"));
        assertThat(result.get(0).getProperties().get(3).getDeveloperName(), equalTo("field 1 3"));
        assertThat(result.get(0).getProperties().get(3).getContentValue(), equalTo("some value 3"));
        assertThat(result.get(0).getProperties().get(4).getDeveloperName(), equalTo("___sourceId"));
        assertThat(result.get(0).getProperties().get(4).getContentValue(), equalTo("TESTING"));

        assertThat(result.get(0).getProperties().get(6).getDeveloperName(), equalTo(FuzzyMatchDetialsConstants.DUPLICATE_FIELD));
        assertThat(result.get(0).getProperties().get(6).getObjectData().get(0).getDeveloperName(), equalTo("12fa66f9-e14d-f642-878f-030b13b64731 match"));
        assertThat(result.get(0).getProperties().get(6).getObjectData().get(0).getProperties().size(), equalTo(5));
        assertThat(result.get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("id"));
        assertThat(result.get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        assertThat(result.get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("field 1 1"));
        assertThat(result.get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("some value 1"));
        assertThat(result.get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(2).getDeveloperName(), equalTo("field 1 2"));
        assertThat(result.get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(2).getContentValue(), equalTo("some value 2"));
        assertThat(result.get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(3).getDeveloperName(), equalTo("field 1 3"));
        assertThat(result.get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(3).getContentValue(), equalTo("some value 3"));

        assertThat(result.get(0).getProperties().get(5).getDeveloperName(), equalTo(FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getDeveloperName(), equalTo(FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("field"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("name"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("first"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("field 1 1"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(2).getDeveloperName(), equalTo("second"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(2).getContentValue(), equalTo("field 1 2"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(3).getDeveloperName(), equalTo("method"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(3).getContentValue(), equalTo("k"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(4).getDeveloperName(), equalTo("matchStrength"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(4).getContentValue(), equalTo("0.90666664"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(5).getDeveloperName(), equalTo("threshold"));
        assertThat(result.get(0).getProperties().get(5).getObjectData().get(0).getProperties().get(5).getContentValue(), equalTo("0.85"));

        assertThat(result.get(0).getProperties().get(7).getDeveloperName(), equalTo(FuzzyMatchDetialsConstants.DUPLICATE_FIELD));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getDeveloperName(), equalTo("testing"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().size(), equalTo(5));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("id"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("field 1 1"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("some value 1"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(2).getDeveloperName(), equalTo("field 1 2"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(2).getContentValue(), equalTo("some value 2"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(3).getDeveloperName(), equalTo("field 1 3"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(3).getContentValue(), equalTo("some value 3"));

        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getDeveloperName(), equalTo(FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("field"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("name"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("first"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("field 1 1"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(2).getDeveloperName(), equalTo("second"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(2).getContentValue(), equalTo("field 1 2"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(3).getDeveloperName(), equalTo("method"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(3).getContentValue(), equalTo("k"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(4).getDeveloperName(), equalTo("matchStrength"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(4).getContentValue(), equalTo("0.90666664"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(5).getDeveloperName(), equalTo("threshold"));
        assertThat(result.get(0).getProperties().get(7).getObjectData().get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(5).getContentValue(), equalTo("0.85"));

        assertThat(result.get(0).getProperties().get(8).getDeveloperName(), equalTo(FuzzyMatchDetialsConstants.ALREADY_LINKED_FIELD));
        assertThat(result.get(0).getProperties().get(8).getObjectData().get(0).getProperties().size(), equalTo(4));
        assertThat(result.get(0).getProperties().get(8).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("id"));
        assertThat(result.get(0).getProperties().get(8).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        assertThat(result.get(0).getProperties().get(8).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("field 1 1"));
        assertThat(result.get(0).getProperties().get(8).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("some value 1"));
        assertThat(result.get(0).getProperties().get(8).getObjectData().get(0).getProperties().get(2).getDeveloperName(), equalTo("field 1 2"));
        assertThat(result.get(0).getProperties().get(8).getObjectData().get(0).getProperties().get(2).getContentValue(), equalTo("some value 2"));
        assertThat(result.get(0).getProperties().get(8).getObjectData().get(0).getProperties().get(3).getDeveloperName(), equalTo("field 1 3"));
        assertThat(result.get(0).getProperties().get(8).getObjectData().get(0).getProperties().get(3).getContentValue(), equalTo("some value 3"));
    }

    private MatchEntityResponse createMatchEntityResponse() {
        var matchEntityResponse = new MatchEntityResponse();
        var matchResult = new MatchEntityResponse.MatchResult();
        matchResult.setStatus("success");
        matchResult.setMatchRule("similar name found");

        List<Map<String, Object>> matches = new ArrayList<>();
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("id", "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        properties.put("field 1 1", "some value 1");
        properties.put("field 1 2", "some value 2");
        properties.put("field 1 3", "some value 3");
        var testing = new HashMap<String, Map<String, Object>>();
        testing.put("testing", properties);
        matchResult.setEntity(testing);

        var fuzzyProperties = new HashMap<String, Object>();
        fuzzyProperties.put("field", "name");
        fuzzyProperties.put("first", "field 1 1");
        fuzzyProperties.put("second", "field 1 2");
        fuzzyProperties.put("method", "k");
        fuzzyProperties.put("matchStrength", "0.90666664");
        fuzzyProperties.put("threshold", "0.85");

        testing.put(FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS, fuzzyProperties);


        matchResult.setMatch(matches);
        matchResult.setDuplicate(new ArrayList<>(matches));
        matchEntityResponse.setMatchResults(List.of(matchResult));

        return matchEntityResponse;
    }

    private BatchUpdateRequest createBatchUpdateRequest() {
        var updateRequest = new BatchUpdateRequest();
        updateRequest.setSource("TESTING");

        var entity = new BatchUpdateRequest.Entity();
        entity.setName("testing");
        var universeFields = new HashMap<String, Object>();
        universeFields.put("id", "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        entity.setFields(universeFields);

        var fields = entity.getFields();
        fields.put("id", "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        fields.put("field 1 1", "some value 1");
        fields.put("field 2 1", "some value 2");
        fields.put("field 3 1", "some value 3");
        updateRequest.getEntities().add(entity);
        return updateRequest;
    }

    private MObject createObjectToUpdate() {
        // Construct the incoming object
        MObject object = new MObject(objectDataType.getDeveloperName());
        object.setExternalId("4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c");
        object.getProperties().add(new Property("id", "4f23f8eb-984b-4e9b-9a52-d9ebaf11bb1c"));
        object.getProperties().add(new Property("___sourceId", "TESTING"));
        object.getProperties().add(new Property("field 1 1", "some value 1"));
        object.getProperties().add(new Property("field 2 1", "some value 2"));
        object.getProperties().add(new Property("field 3 1", "some value 3"));

        MObject fuzzyObject = new MObject(FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS);
        var fuzzyPropertiesResponse = new ArrayList<Property>();
        fuzzyPropertiesResponse.add(new Property("field", "name"));
        fuzzyPropertiesResponse.add(new Property("first", "field 1 1"));
        fuzzyPropertiesResponse.add(new Property("second", "field 1 2"));
        fuzzyPropertiesResponse.add(new Property("method", "k"));
        fuzzyPropertiesResponse.add(new Property("matchStrength", "0.90666664"));
        fuzzyPropertiesResponse.add(new Property("threshold", "0.85"));

        fuzzyObject.setProperties(fuzzyPropertiesResponse);
        object.getProperties().add(new Property(FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS, fuzzyObject));

        return object;
    }


}
