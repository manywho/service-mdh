package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.DateFilter;
import com.boomi.flow.services.boomi.mdh.database.MdhRawDatabase;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityRepository;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntry;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryRequest;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryResponse;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordRepository;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseLoadQuarantineEntryTests {
    @Mock
    private MdhClient client;

    private ObjectDataType objectDataType = new ObjectDataType()
            .setDeveloperName("universe-name-quarantine");

    private QuarantineQueryResponse response = new QuarantineQueryResponse()
            .setEntries(
                    List.of(
                            createQuarantineEntry(1),
                            createQuarantineEntry(2)
                    )
            );

    @Test
    public void testLoadReturnsCorrectlyFormattedMObjects() {
        when(client.queryQuarantineEntries(any(), any(), any(), any(), any()))
                .thenReturn(response);

        List<MObject> objects = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, null, null);

        assertThat(objects, not(nullValue()));
        assertThat(objects, hasSize(2));
        assertThat(objects.get(0).getDeveloperName(), equalTo("universe-name-quarantine"));
        assertThat(objects.get(0).getExternalId(), equalTo("a transaction ID 1"));
        assertThat(objects.get(0).getProperties(), hasSize(11));
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

        assertThat(objects.get(0).getProperties().get(4).getDeveloperName(), equalTo("___cause"));
        assertThat(objects.get(0).getProperties().get(4).getContentValue(), equalTo("a cause 1"));
        assertThat(objects.get(0).getProperties().get(5).getDeveloperName(), equalTo("___createdDate"));
        assertThat(objects.get(0).getProperties().get(5).getContentValue(), equalTo("2018-02-04T12:34Z"));
        assertThat(objects.get(0).getProperties().get(6).getDeveloperName(), equalTo("___endDate"));
        assertThat(objects.get(0).getProperties().get(6).getContentValue(), equalTo("2018-02-05T13:57Z"));
        assertThat(objects.get(0).getProperties().get(7).getDeveloperName(), equalTo("___reason"));
        assertThat(objects.get(0).getProperties().get(7).getContentValue(), equalTo("a reason 1"));
        assertThat(objects.get(0).getProperties().get(8).getDeveloperName(), equalTo("___resolution"));
        assertThat(objects.get(0).getProperties().get(8).getContentValue(), equalTo("a resolution 1"));
        assertThat(objects.get(0).getProperties().get(9).getDeveloperName(), equalTo("___transactionId"));
        assertThat(objects.get(0).getProperties().get(9).getContentValue(), equalTo("a transaction ID 1"));
        assertThat(objects.get(0).getProperties().get(10).getDeveloperName(), equalTo("___sourceEntityId"));
        assertThat(objects.get(0).getProperties().get(10).getContentValue(), equalTo("a source entity ID 1"));
    }

    @Test
    public void testLoadWithNoFilter() {
        var query = new QuarantineQueryRequest()
                .setFilter(new QuarantineQueryRequest.Filter())
                .setIncludeData(true);

        // Actual test is below here
        when(client.queryQuarantineEntries(any(), any(), any(), any(), any()))
                .thenReturn(response);

        List<MObject> objects = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, null, null);

        verify(client)
                .queryQuarantineEntries(
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
        var wheres = new ArrayList<ListFilterWhere>();
        wheres.add(createWhere("___status", CriteriaType.Equal, "ACTIVE"));
        wheres.add(createWhere("___sourceId", CriteriaType.Equal, "a source ID"));
        wheres.add(createWhere("___sourceEntityId", CriteriaType.Equal, "a source entity ID"));
        wheres.add(createWhere("___createdDate", CriteriaType.GreaterThan, "2013-01-01T00:00:00.0000000+00:00"));
        wheres.add(createWhere("___createdDate", CriteriaType.LessThanOrEqual, "2019-02-28T00:00:00.0000000+00:00"));
        wheres.add(createWhere("___endDate", CriteriaType.GreaterThanOrEqual, "2019-02-01T00:00:00.0000000+00:00"));
        wheres.add(createWhere("___endDate", CriteriaType.LessThan, "2019-02-14T00:00:00.0000000+00:00"));
        wheres.add(createWhere("___cause", CriteriaType.Equal, "AMBIGUOUS_MATCH"));
        wheres.add(createWhere("___cause", CriteriaType.Equal, "MULTIPLE_MATCHES"));
        wheres.add(createWhere("___cause", CriteriaType.Equal, "REQUIRED_FIELD"));
        wheres.add(createWhere("___resolution", CriteriaType.Equal, "GRID_DELETED"));
        wheres.add(createWhere("___resolution", CriteriaType.Equal, "USER_APPROVED"));

        var listFilter = new ListFilter();
        listFilter.setComparisonType(ComparisonType.And);
        listFilter.setLimit(123);
        listFilter.setWhere(wheres);

        var query = new QuarantineQueryRequest()
                .setFilter(new QuarantineQueryRequest.Filter()
                        .setCauses(List.of("AMBIGUOUS_MATCH", "MULTIPLE_MATCHES", "REQUIRED_FIELD"))
                        .setCreatedDate(new DateFilter()
                                .setFrom(OffsetDateTime.parse("2013-01-01T00:00Z"))
                                .setTo(OffsetDateTime.parse("2019-02-28T00:00Z"))
                        )
                        .setEndDate(new DateFilter()
                                .setFrom(OffsetDateTime.parse("2019-02-01T00:00Z"))
                                .setTo(OffsetDateTime.parse("2019-02-14T00:00Z"))
                        )
                        .setResolutions(List.of("GRID_DELETED", "USER_APPROVED"))
                        .setSourceEntityId("a source entity ID")
                        .setSourceId("a source ID")
                )
                .setIncludeData(true)
                .setType("ACTIVE");

        // Actual test is below here
        when(client.queryQuarantineEntries(any(), any(), any(), any(), any()))
                .thenReturn(response);

        new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client), new MatchEntityRepository(client))
                .findAll(TestConstants.CONFIGURATION, objectDataType, null, listFilter, null);

        verify(client)
                .queryQuarantineEntries(
                        TestConstants.CONFIGURATION.getHubHostname(),
                        TestConstants.CONFIGURATION.getHubUsername(),
                        TestConstants.CONFIGURATION.getHubToken(),
                        "universe-name",
                        query
                );
    }

    private static QuarantineEntry createQuarantineEntry(int number) {
        Map<String, Object> entityWrapper = new  HashMap<String, Object>();
        entityWrapper.put("field 1 " + number, "field 1 value " + number);
        entityWrapper.put("field 2 " + number, "field 2 value " + number);
        entityWrapper.put("field 3 " + number, "field 3 value " + number);
        entityWrapper.put("field 4 " + number, Map.ofEntries(Map.entry("field 4 " + number + " property", "value property 4 value 1 " + number)));

        Map<String, Map<String, Object>> entity = new HashMap<>();
        entity.put("dunno", entityWrapper);

        return new QuarantineEntry()
                .setCause("a cause " + number)
                .setCreatedDate(OffsetDateTime.parse("2018-02-04T12:34Z"))
                .setEndDate(OffsetDateTime.parse("2018-02-05T13:57Z"))
                .setEntity(entity)
                .setReason("a reason " + number)
                .setResolution("a resolution " + number)
                .setSourceEntityId("a source entity ID " + number)
                .setSourceId("a source ID " + number)
                .setTransactionId("a transaction ID " + number);
    }

    private static ListFilterWhere createWhere(String columnName, CriteriaType criteriaType, String value) {
        var where = new ListFilterWhere();

        where.setColumnName(columnName);
        where.setCriteriaType(criteriaType);
        where.setContentValue(value);

        return where;
    }
}
