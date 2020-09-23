package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.DateFilter;
import com.boomi.flow.services.boomi.mdh.database.MdhRawDatabase;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityRepository;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
import com.boomi.flow.services.boomi.mdh.records.*;
import com.manywho.sdk.api.ComparisonType;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.run.elements.type.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.OffsetDateTime;
import java.util.*;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseLoadGoldenRecordTests {
    @Mock
    private MdhClient client;

    private ObjectDataType objectDataType = new ObjectDataType()
            .setDeveloperName("12fa66f9-e14d-f642-878f-030b13b64731-golden-record");

    private GoldenRecordQueryResponse response = new GoldenRecordQueryResponse()
            .setResultCount(2)
            .setRecords(
                    Arrays.asList(
                            createGoldenRecord(1),
                            createGoldenRecord(2)
                    )
            );

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
    public void testLoadReturnsCorrectlyFormattedMObjects() {
        when(client.queryGoldenRecords(any(), any(), any(), any(), any()))
                .thenReturn(response);

        List<String> uniqueIds = Arrays.asList(
                "field 1 1",
                "field 2 1",
                "field 3 1",
                "field 4 1",
                "field 4 1 property",
                "field 1 2",
                "field 2 2",
                "field 3 2",
                "field 4 2",
                "field 4 2 property"
        );
        List<String> names = Arrays.asList(
                "field 1 1",
                "field 2 1",
                "field 3 1",
                "field 4 1",
                "field 4 1 property",
                "field 1 2",
                "field 2 2",
                "field 3 2",
                "field 4 2",
                "field 4 2 property"
        );

        // Make sure we return the expected universe layout for the test
        when(client.findUniverse(any(), any(), any(), any()))
                .thenReturn(new Universe()
                        .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                        .setName("testing")
                        .setLayout(new Universe.Layout()
                                .setIdXPath("/item/id")
                                .setModel(new Universe.Layout.Model()
                                        .setName("testing")
                                        .setElements(createElements(uniqueIds, names)))));

        List<MObject> objects = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, null, null);

        assertThat(objects, not(nullValue()));
        assertThat(objects, hasSize(2));
        assertThat(objects.get(0).getDeveloperName(), equalTo("12fa66f9-e14d-f642-878f-030b13b64731-golden-record"));
        assertThat(objects.get(0).getExternalId(), equalTo("record ID 1"));
        assertThat(objects.get(0).getProperties(), hasSize(8));
        assertThat(objects.get(0).getProperties().get(0).getDeveloperName(), equalTo("field 1 1"));
        assertThat(objects.get(0).getProperties().get(0).getContentValue(), equalTo("field 1 value 1"));
        assertThat(objects.get(0).getProperties().get(1).getDeveloperName(), equalTo("field 2 1"));
        assertThat(objects.get(0).getProperties().get(1).getContentValue(), equalTo("field 2 value 1"));
        assertThat(objects.get(0).getProperties().get(2).getDeveloperName(), equalTo("field 3 1"));
        assertThat(objects.get(0).getProperties().get(2).getContentValue(), equalTo("field 3 value 1"));
        assertThat(objects.get(0).getProperties().get(3).getContentValue(), nullValue());
        assertThat(objects.get(0).getProperties().get(3).getObjectData().get(0).getDeveloperName(), equalTo("testing - field 4 1"));
        assertThat(objects.get(0).getProperties().get(3).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("field 4 1 property"));
        assertThat(objects.get(0).getProperties().get(3).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("value property 4 value 1 1"));
        assertThat(objects.get(0).getProperties().get(4).getDeveloperName(), equalTo(GoldenRecordConstants.LINKS_FIELD));
        assertThat(objects.get(0).getProperties().get(4).getObjectData().get(0).getDeveloperName(), equalTo(GoldenRecordConstants.LINK));
        assertThat(objects.get(0).getProperties().get(4).getObjectData().get(0).getExternalId(), notNullValue());
        assertThat(objects.get(0).getProperties().get(4).getObjectData().get(0).getProperties(), hasSize(3));
        assertThat(objects.get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("Source"));
        assertThat(objects.get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("source link 1"));
        assertThat(objects.get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("Entity ID"));
        assertThat(objects.get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("source entity 1"));
        assertThat(objects.get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(2).getDeveloperName(), equalTo("Established Date"));
        assertThat(objects.get(0).getProperties().get(4).getObjectData().get(0).getProperties().get(2).getContentValue(), equalTo("2016-03-04T23:45:10Z"));
        assertThat(objects.get(0).getProperties().get(5).getDeveloperName(), equalTo("___recordId"));
        assertThat(objects.get(0).getProperties().get(5).getContentValue(), equalTo("record ID 1"));
        assertThat(objects.get(0).getProperties().get(6).getDeveloperName(), equalTo("___filterCreatedDate"));
        assertThat(objects.get(0).getProperties().get(6).getContentValue(), equalTo("2015-01-02T12:34:00Z"));
        assertThat(objects.get(0).getProperties().get(7).getDeveloperName(), equalTo("___filterUpdatedDate"));
        assertThat(objects.get(0).getProperties().get(7).getContentValue(), equalTo("2017-03-04T23:45:00Z"));
    }

    @Test
    public void testLoadWithNoFilter() {
        GoldenRecordQueryRequest query = new GoldenRecordQueryRequest();

        List<String> uniqueIds = Arrays.asList(
                "field 1 1",
                "field 2 1",
                "field 3 1",
                "field 4 1",
                "field 4 1 property",
                "field 1 2",
                "field 2 2",
                "field 3 2",
                "field 4 2",
                "field 4 2 property"
        );
        List<String> names = Arrays.asList(
                "field 1 1",
                "field 2 1",
                "field 3 1",
                "field 4 1",
                "field 4 1 property",
                "field 1 2",
                "field 2 2",
                "field 3 2",
                "field 4 2",
                "field 4 2 property"
        );

        // Make sure we return the expected universe layout for the test
        when(client.findUniverse(any(), any(), any(), any()))
                .thenReturn(new Universe()
                        .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                        .setName("testing")
                        .setLayout(new Universe.Layout()
                                .setIdXPath("/item/id")
                                .setModel(new Universe.Layout.Model()
                                        .setName("testing")
                                        .setElements(createElements(uniqueIds, names)))));

        // Actual test is below here
        when(client.queryGoldenRecords(any(), any(), any(), any(), any()))
                .thenReturn(response);

        List<MObject> objects = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, null, null);

        verify(client)
                .queryGoldenRecords(
                        TestConstants.CONFIGURATION.getHubHostname(),
                        TestConstants.CONFIGURATION.getHubUsername(),
                        TestConstants.CONFIGURATION.getHubToken(),
                        "12fa66f9-e14d-f642-878f-030b13b64731",
                        query
                );

        assertThat(objects, not(nullValue()));
    }

    @Test
    public void testLoadWithComprehensiveFilter() {
        List<String> uniqueIds = Arrays.asList(
                "field 1 1",
                "field 2 1",
                "field 3 1",
                "field 4 1",
                "field 4 1 property",
                "field 1 2",
                "field 2 2",
                "field 3 2",
                "field 4 2",
                "field 4 2 property"
        );
        List<String> names = Arrays.asList(
                "field 1 1",
                "field 2 1",
                "field 3 1",
                "field 4 1",
                "field 4 1 property",
                "field 1 2",
                "field 2 2",
                "field 3 2",
                "field 4 2",
                "field 4 2 property"
        );

        // Make sure we return the expected universe layout for the test
        when(client.findUniverse(any(), any(), any(), any()))
                .thenReturn(new Universe()
                        .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                        .setName("testing")
                        .setLayout(new Universe.Layout()
                                .setIdXPath("/item/id")
                                .setModel(new Universe.Layout.Model()
                                        .setName("testing")
                                        .setElements(createElements(uniqueIds, names)))));

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
        wheres.add(createWhere("field 2", CriteriaType.IsEmpty, "true"));
        wheres.add(createWhere("field 1", CriteriaType.LessThan, "less than something"));
        wheres.add(createWhere("field 2", CriteriaType.LessThanOrEqual, "less than or equal to something"));
        wheres.add(createWhere("field 1", CriteriaType.NotEqual, "not equal to something"));
        wheres.add(createWhere("field 2", CriteriaType.StartsWith, "starts with"));

        ListFilter listFilter = new ListFilter();
        listFilter.setComparisonType(ComparisonType.And);
        listFilter.setWhere(wheres);
        listFilter.setLimit(3);
        listFilter.setOffset(2);

        listFilter.addOrderBy(new ListFilter.OrderBy("a field ID", "ASC"));

        GoldenRecordQueryRequest query = new GoldenRecordQueryRequest()
                .setLimit("3")
                .setOffsetToken("2")
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
                                                .setValue(null),
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
                                        .setFieldId("A FIELD ID")
                        ))
                );

        // Actual test is below here
        when(client.queryGoldenRecords(any(), any(), any(), any(), any()))
                .thenReturn(response);

        new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, listFilter, null);

        verify(client)
                .queryGoldenRecords(
                        TestConstants.CONFIGURATION.getHubHostname(),
                        TestConstants.CONFIGURATION.getHubUsername(),
                        TestConstants.CONFIGURATION.getHubToken(),
                        "12fa66f9-e14d-f642-878f-030b13b64731",
                        query
                );
    }

    @Test
    public void testLoadWithSimpleOrdering() {
        List<String> uniqueIds = Arrays.asList(
                "field"
        );
        List<String> names = Arrays.asList(
                "field"
        );

        // Make sure we return the expected universe layout for the test
        when(client.findUniverse(any(), any(), any(), any()))
                .thenReturn(new Universe()
                        .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                        .setName("testing")
                        .setLayout(new Universe.Layout()
                                .setIdXPath("/item/id")
                                .setModel(new Universe.Layout.Model()
                                        .setName("testing")
                                        .setElements(createElements(uniqueIds, names)))));

        ListFilter listFilter = new ListFilter();
        listFilter.setComparisonType(ComparisonType.And);
        listFilter.setLimit(0);

        // simple ordering on one property/column
        listFilter.setOrderByPropertyDeveloperName("some property");
        listFilter.setOrderByDirectionType(ListFilter.OrderByDirectionType.Ascending);

        GoldenRecordQueryRequest query = new GoldenRecordQueryRequest()
                .setSort(new GoldenRecordQueryRequest.Sort()
                        .setFields(Arrays.asList(
                                new GoldenRecordQueryRequest.Sort.Field()
                                        .setFieldId("SOME PROPERTY")
                                        .setDirection("ASC")
                        ))
                );

        new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, listFilter, null);

        verify(client)
                .queryGoldenRecords(
                        TestConstants.CONFIGURATION.getHubHostname(),
                        TestConstants.CONFIGURATION.getHubUsername(),
                        TestConstants.CONFIGURATION.getHubToken(),
                        "12fa66f9-e14d-f642-878f-030b13b64731",
                        query
                );
    }

    @Test
    public void testLoadWithCombinedOrdering() {
        List<String> uniqueIds = Arrays.asList(
                "field"
        );
        List<String> names = Arrays.asList(
                "field"
        );

        // Make sure we return the expected universe layout for the test
        when(client.findUniverse(any(), any(), any(), any()))
                .thenReturn(new Universe()
                        .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                        .setName("testing")
                        .setLayout(new Universe.Layout()
                                .setIdXPath("/item/id")
                                .setModel(new Universe.Layout.Model()
                                        .setName("testing")
                                        .setElements(createElements(uniqueIds, names)))));

        ListFilter listFilter = new ListFilter();
        listFilter.setComparisonType(ComparisonType.And);
        listFilter.setLimit(0);

        // combined ordering on two properties/columns
        listFilter.setOrderByPropertyDeveloperName("some property");
        listFilter.setOrderByDirectionType(ListFilter.OrderByDirectionType.Ascending);
        listFilter.addOrderBy(new ListFilter.OrderBy("other property", "DESC"));

        GoldenRecordQueryRequest query = new GoldenRecordQueryRequest()
                .setSort(new GoldenRecordQueryRequest.Sort()
                        .setFields(Arrays.asList(
                                new GoldenRecordQueryRequest.Sort.Field()
                                        .setFieldId("SOME PROPERTY")
                                        .setDirection("ASC"),
                                new GoldenRecordQueryRequest.Sort.Field()
                                        .setFieldId("OTHER PROPERTY")
                                        .setDirection("DESC")
                        ))
                );

        new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, listFilter, null);

        verify(client)
                .queryGoldenRecords(
                        TestConstants.CONFIGURATION.getHubHostname(),
                        TestConstants.CONFIGURATION.getHubUsername(),
                        TestConstants.CONFIGURATION.getHubToken(),
                        "12fa66f9-e14d-f642-878f-030b13b64731",
                        query
                );
    }

    @Test
    public void testLoadWithSkippedDuplicatedOrdering() {
        List<String> uniqueIds = Arrays.asList(
                "field"
        );
        List<String> names = Arrays.asList(
                "field"
        );

        // Make sure we return the expected universe layout for the test
        when(client.findUniverse(any(), any(), any(), any()))
                .thenReturn(new Universe()
                        .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                        .setName("testing")
                        .setLayout(new Universe.Layout()
                                .setIdXPath("/item/id")
                                .setModel(new Universe.Layout.Model()
                                        .setName("testing")
                                        .setElements(createElements(uniqueIds, names)))));

        ListFilter listFilter = new ListFilter();
        listFilter.setComparisonType(ComparisonType.And);
        listFilter.setLimit(0);

        // duplicated ordering on same property/column
        listFilter.setOrderByPropertyDeveloperName("some property");
        listFilter.setOrderByDirectionType(ListFilter.OrderByDirectionType.Ascending);
        listFilter.addOrderBy(new ListFilter.OrderBy("some property", "DESC"));

        GoldenRecordQueryRequest query = new GoldenRecordQueryRequest()
                .setSort(new GoldenRecordQueryRequest.Sort()
                        .setFields(Arrays.asList(
                                new GoldenRecordQueryRequest.Sort.Field()
                                        .setFieldId("SOME PROPERTY")
                                        .setDirection("ASC")
                        ))
                );

        new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, listFilter, null);

        verify(client)
                .queryGoldenRecords(
                        TestConstants.CONFIGURATION.getHubHostname(),
                        TestConstants.CONFIGURATION.getHubUsername(),
                        TestConstants.CONFIGURATION.getHubToken(),
                        "12fa66f9-e14d-f642-878f-030b13b64731",
                        query
                );
    }

    private static GoldenRecord createGoldenRecord(int number) {
        List<Property> mObjectProperties = new ArrayList<>();
        mObjectProperties.add(new Property("field 1 " + number, "field 1 value " + number));
        mObjectProperties.add(new Property("field 2 " + number, "field 2 value " + number));
        mObjectProperties.add(new Property("field 3 " + number, "field 3 value " + number));

        MObject mObjectField4 = new MObject("testing - field 4 " + number, Collections.singletonList(new Property("field 4 " + number + " property", "value property 4 value 1 " + number)));
        Property property = new Property("testing - field 4 " + number, mObjectField4);
        property.setContentType(ContentType.Object);
        mObjectProperties.add(property);
        MObject mObject = new MObject("12fa66f9-e14d-f642-878f-030b13b64731", mObjectProperties);

        List<GoldenRecord.Link> links = new ArrayList<>();
        GoldenRecord.Link link = new GoldenRecord.Link();
        link.setSource("source link " + number);
        link.setEntityId("source entity " + number);
        link.setEstablishedDate(OffsetDateTime.parse("2016-03-04T23:45:10Z"));
        links.add(link);

        return new GoldenRecord()
                .setCreatedDate(OffsetDateTime.parse("2015-01-02T12:34Z"))
                .setMObject(mObject)
                .setLinks(links)
                .setRecordId("record ID " + number)
                .setUpdatedDate(OffsetDateTime.parse("2017-03-04T23:45Z"));
    }

    private static ListFilterWhere createWhere(String columnName, CriteriaType criteriaType, String value) {
        ListFilterWhere where = new ListFilterWhere();

        where.setColumnName(columnName);
        where.setCriteriaType(criteriaType);
        where.setContentValue(value);

        return where;
    }
}
