package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.DateFilter;
import com.boomi.flow.services.boomi.mdh.database.MdhRawDatabase;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityRepository;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
import com.boomi.flow.services.boomi.mdh.records.*;
import com.google.common.collect.ImmutableMap;
import com.manywho.sdk.api.ComparisonType;
import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.OffsetDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseLoadGoldenRecordTests {
    @Mock
    private MdhClient client;

    @Mock
    private ElementIdFinder elementIdFinder;

    private ObjectDataType objectDataType = new ObjectDataType()
            .setDeveloperName("universe-name-golden-record");

    private GoldenRecordQueryResponse response = new GoldenRecordQueryResponse()
            .setResultCount(2)
            .setRecords(
                    Arrays.asList(
                            createGoldenRecord(1),
                            createGoldenRecord(2)
                    )
            );

    @Test
    public void testLoadReturnsCorrectlyFormattedMObjects() {
        when(client.queryGoldenRecords(any(), any(), any(), any(), any()))
                .thenReturn(response);

        List<MObject> objects = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client, new ElementIdFinder(null)), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, null, null);

        assertThat(objects, not(nullValue()));
        assertThat(objects, hasSize(2));
        assertThat(objects.get(0).getDeveloperName(), equalTo("universe-name-golden-record"));
        assertThat(objects.get(0).getExternalId(), equalTo("record ID 1"));
        assertThat(objects.get(0).getProperties(), hasSize(5));
        assertThat(objects.get(0).getProperties().get(0).getDeveloperName(), equalTo("field 1 1"));
        assertThat(objects.get(0).getProperties().get(0).getContentValue(), equalTo("field 1 value 1"));
        assertThat(objects.get(0).getProperties().get(1).getDeveloperName(), equalTo("field 2 1"));
        assertThat(objects.get(0).getProperties().get(1).getContentValue(), equalTo("field 2 value 1"));
        assertThat(objects.get(0).getProperties().get(2).getDeveloperName(), equalTo("field 3 1"));
        assertThat(objects.get(0).getProperties().get(2).getContentValue(), equalTo("field 3 value 1"));
        assertThat(objects.get(0).getProperties().get(3).getContentValue(), nullValue());
        assertThat(objects.get(0).getProperties().get(3).getObjectData().get(0).getDeveloperName(), equalTo("field 4 1-child"));
        assertThat(objects.get(0).getProperties().get(3).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("field 4 1 property"));
        assertThat(objects.get(0).getProperties().get(3).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("value property 4 value 1 1"));
        assertThat(objects.get(0).getProperties().get(4).getDeveloperName(), equalTo("___recordId"));
        assertThat(objects.get(0).getProperties().get(4).getContentValue(), equalTo("record ID 1"));
    }

    @Test
    public void testLoadWithNoFilter() {
        GoldenRecordQueryRequest query = new GoldenRecordQueryRequest();

        // Actual test is below here
        when(client.queryGoldenRecords(any(), any(), any(), any(), any()))
                .thenReturn(response);

        List<MObject> objects = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client, new ElementIdFinder(null)), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, null, null);

        verify(client)
                .queryGoldenRecords(
                        TestConstants.CONFIGURATION.getHubHostname(),
                        TestConstants.CONFIGURATION.getHubUsername(),
                        TestConstants.CONFIGURATION.getHubToken(),
                        "universe-name",
                        query
                );

        assertThat(objects, not(nullValue()));
    }

    @Test
    public void testLoadWithComprehensiveFilter() {
        List<ListFilterWhere> wheres = new ArrayList<>();
        wheres.add(createWhere("___filterCreatedDate", CriteriaType.GreaterThan, "2013-01-01T00:00:00.0000000+00:00"));
        wheres.add(createWhere("___filterCreatedDate", CriteriaType.LessThanOrEqual, "2019-02-28T00:00:00.0000000+00:00"));
        wheres.add(createWhere("___filterUpdatedDate", CriteriaType.GreaterThanOrEqual, "2019-02-01T00:00:00.0000000+00:00"));
        wheres.add(createWhere("___filterUpdatedDate", CriteriaType.LessThan, "2019-02-14T00:00:00.0000000+00:00"));
        wheres.add(createWhere("field 1", CriteriaType.Contains, "contains something"));
        wheres.add(createWhere("field 2", CriteriaType.EndsWith, "ends with something"));
        wheres.add(createWhere("field 1", CriteriaType.Equal, "equals something"));
        wheres.add(createWhere("field 2", CriteriaType.GreaterThan, "greater than something"));
        wheres.add(createWhere("field 1", CriteriaType.GreaterThanOrEqual, "greater than or equal to something"));
        wheres.add(createWhere("field 2", CriteriaType.IsEmpty, "is empty"));
        wheres.add(createWhere("field 1", CriteriaType.LessThan, "less than something"));
        wheres.add(createWhere("field 2", CriteriaType.LessThanOrEqual, "less than or equal to something"));
        wheres.add(createWhere("field 1", CriteriaType.NotEqual, "not equal to something"));
        wheres.add(createWhere("field 2", CriteriaType.StartsWith, "starts with"));

        ListFilter listFilter = new ListFilter();
        listFilter.setComparisonType(ComparisonType.And);
        listFilter.setLimit(123);
        listFilter.setWhere(wheres);

        listFilter.addOrderBy(new ListFilter.OrderBy("a field ID", "ASC"));

        GoldenRecordQueryRequest query = new GoldenRecordQueryRequest()
                .setFilter(new GoldenRecordQueryRequest.Filter()
//                        .setCreatingSourceId("a creating source ID")
                                .setCreatedDate(new DateFilter()
                                        .setFrom(OffsetDateTime.parse("2013-01-01T00:00Z"))
                                        .setTo(OffsetDateTime.parse("2019-02-28T00:00Z"))
                                )
                                .setFieldValues(Arrays.asList(
                                        new GoldenRecordQueryRequest.Filter.FieldValue()
                                                .setFieldId("FIELD 1")
                                                .setOperator("CONTAINS")
                                                .setValue("contains something"),
                                        new GoldenRecordQueryRequest.Filter.FieldValue()
                                                .setFieldId("FIELD 2")
                                                .setOperator("ENDS_WITH")
                                                .setValue("ends with something"),
                                        new GoldenRecordQueryRequest.Filter.FieldValue()
                                                .setFieldId("FIELD 1")
                                                .setOperator("EQUALS")
                                                .setValue("equals something"),
                                        new GoldenRecordQueryRequest.Filter.FieldValue()
                                                .setFieldId("FIELD 2")
                                                .setOperator("GREATER_THAN")
                                                .setValue("greater than something"),
                                        new GoldenRecordQueryRequest.Filter.FieldValue()
                                                .setFieldId("FIELD 1")
                                                .setOperator("GREATER_THAN_EQUAL")
                                                .setValue("greater than or equal to something"),
                                        new GoldenRecordQueryRequest.Filter.FieldValue()
                                                .setFieldId("FIELD 2")
                                                .setOperator("IS_NULL")
                                                .setValue("is empty"),
                                        new GoldenRecordQueryRequest.Filter.FieldValue()
                                                .setFieldId("FIELD 1")
                                                .setOperator("LESS_THAN")
                                                .setValue("less than something"),
                                        new GoldenRecordQueryRequest.Filter.FieldValue()
                                                .setFieldId("FIELD 2")
                                                .setOperator("LESS_THAN_EQUAL")
                                                .setValue("less than or equal to something"),
                                        new GoldenRecordQueryRequest.Filter.FieldValue()
                                                .setFieldId("FIELD 1")
                                                .setOperator("NOT_EQUAL_TO")
                                                .setValue("not equal to something"),
                                        new GoldenRecordQueryRequest.Filter.FieldValue()
                                                .setFieldId("FIELD 2")
                                                .setOperator("STARTS_WITH")
                                                .setValue("starts with")
                                ))
                                .setUpdatedDate(new DateFilter()
                                        .setFrom(OffsetDateTime.parse("2019-02-01T00:00Z"))
                                        .setTo(OffsetDateTime.parse("2019-02-14T00:00Z"))
                                )
                )
                .setSort(new GoldenRecordQueryRequest.Sort()
                        .setFields(Arrays.asList(
                                new GoldenRecordQueryRequest.Sort.Field()
                                        .setDirection("ASC")
                                        .setFieldId("a field ID")

                        ))
                );

        // Actual test is below here
        when(client.queryGoldenRecords(any(), any(), any(), any(), any()))
                .thenReturn(response);


        when(elementIdFinder.findIdFromNameOfElement(any(), any(), eq("field 1"))).thenReturn("FIELD 1");
        when(elementIdFinder.findIdFromNameOfElement(any(), any(), eq("field 2"))).thenReturn("FIELD 2");


        new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client, elementIdFinder), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, listFilter, null);

        verify(client)
                .queryGoldenRecords(
                        TestConstants.CONFIGURATION.getHubHostname(),
                        TestConstants.CONFIGURATION.getHubUsername(),
                        TestConstants.CONFIGURATION.getHubToken(),
                        "universe-name",
                        query
                );
    }

    private static GoldenRecord createGoldenRecord(int number) {
        Map<String, Object> fieldsWrapper = new HashMap<String, Object>();
        fieldsWrapper.put("field 1 " + number, "field 1 value " + number);
        fieldsWrapper.put("field 2 " + number, "field 2 value " + number);
        fieldsWrapper.put("field 3 " + number, "field 3 value " + number);
        fieldsWrapper.put("field 4 " + number, ImmutableMap.<String, Object>builder()
                                                    .put("field 4 " + number + " property", "value property 4 value 1 " + number)
                                                    .build());

        Map<String, Map<String, Object>> fields = new HashMap<>();
        fields.put("universe-name", fieldsWrapper);

        return new GoldenRecord()
                .setCreatedDate(OffsetDateTime.parse("2015-01-02T12:34:56Z"))
                .setFields(fields)
                .setRecordId("record ID " + number)
                .setUpdatedDate(OffsetDateTime.parse("2017-03-04T23:45:10Z"));
    }

    private static ListFilterWhere createWhere(String columnName, CriteriaType criteriaType, String value) {
        ListFilterWhere where = new ListFilterWhere();

        where.setColumnName(columnName);
        where.setCriteriaType(criteriaType);
        where.setContentValue(value);

        return where;
    }
}
