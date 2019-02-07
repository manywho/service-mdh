package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.guice.XmlMapperProvider;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.io.Resources;
import org.junit.Test;
import org.xmlunit.builder.Input;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

public class XmlMapperTest {
    @Test
    public void testXmlMapperSerializesQuarantineQueryRequests() throws JsonProcessingException {
        var xmlMapper = new XmlMapperProvider().get();

        var filter = new QuarantineQueryRequest.Filter()
                .setCauses(List.of("cause 1", "cause 2"))
                .setCreatedDate(new QuarantineQueryRequest.DateFilter()
                        .setFrom(OffsetDateTime.parse("2013-01-01T00:00Z"))
                        .setTo(OffsetDateTime.parse("2015-12-31T00:00Z"))
                )
                .setEndDate(new QuarantineQueryRequest.DateFilter()
                        .setFrom(OffsetDateTime.parse("2018-01-01T00:00Z"))
                        .setTo(OffsetDateTime.parse("2018-12-31T00:00Z"))
                )
                .setResolutions(List.of("resolution 1", "resolution 2"))
                .setSourceEntityId("a source entity id")
                .setSourceId("a source id");

        var request = new QuarantineQueryRequest()
                .setFilter(filter)
                .setIncludeData(true)
                .setType("a type");

        var expected = Input.fromURL(Resources.getResource("testXmlMapperSerializesQuarantineQueryRequests.xml"));

        assertThat(xmlMapper.writeValueAsString(request), isIdenticalTo(expected).ignoreWhitespace());
    }

    @Test
    public void testXmlMapperSerializesGoldenRecordQueryRequests() throws JsonProcessingException {
        var xmlMapper = new XmlMapperProvider().get();

        var fieldValueOne = new GoldenRecordQueryRequest.Filter.FieldValue()
                .setFieldId("field 1")
                .setOperator("LESS_THAN")
                .setValue("some value");

        var fieldValueTwo = new GoldenRecordQueryRequest.Filter.FieldValue()
                .setFieldId("field 2")
                .setOperator("EQUAL")
                .setValue("another value");

        var filter = new GoldenRecordQueryRequest.Filter()
                .setCreatedDate(new GoldenRecordQueryRequest.Filter.DateFilter()
                        .setFrom(OffsetDateTime.parse("2013-01-01T00:00Z"))
                        .setTo(OffsetDateTime.parse("2015-12-31T00:00Z"))
                )
                .setCreatingSourceId("12345")
                .setFieldValues(List.of(fieldValueOne, fieldValueTwo))
                .setUpdatedDate(new GoldenRecordQueryRequest.Filter.DateFilter()
                        .setFrom(OffsetDateTime.parse("2018-01-01T00:00Z"))
                        .setTo(OffsetDateTime.parse("2018-12-31T00:00Z"))
                );

        var sortFieldOne = new GoldenRecordQueryRequest.Sort.Field()
                .setFieldId("field 1")
                .setDirection("DESC");

        var sortFieldTwo = new GoldenRecordQueryRequest.Sort.Field()
                .setFieldId("field 2")
                .setDirection("ASC");

        var sort = new GoldenRecordQueryRequest.Sort()
                .setFields(List.of(sortFieldOne, sortFieldTwo));

        var request = new GoldenRecordQueryRequest()
                .setFilter(filter)
                .setSort(sort);

        var expected = Input.fromURL(Resources.getResource("testXmlMapperSerializesGoldenRecordQueryRequests.xml"));

        assertThat(xmlMapper.writeValueAsString(request), isIdenticalTo(expected).ignoreWhitespace());
    }

    @Test
    public void testXmlMapperDeserializesGoldenRecordQueryResponses() throws IOException {
        var xmlMapper = new XmlMapperProvider().get();

        var data = Resources.getResource("testXmlMapperDeserializesGoldenRecordQueryResponses.xml");

        var actual = xmlMapper.readValue(data, GoldenRecordQueryResponse.class);

        assertThat(actual, not(nullValue()));
        assertThat(actual.getResultCount(), equalTo(2));
        assertThat(actual.getTotalCount(), equalTo(2));
        assertThat(actual.getRecords(), hasSize(2));
        assertThat(actual.getRecords().get(0).getRecordId(), equalTo("02a8284d-0ef3-4091-acef-85f23d80cf0d"));
        assertThat(actual.getRecords().get(0).getCreatedDate(), equalTo(OffsetDateTime.parse("2013-09-23T14:07:31Z")));
        assertThat(actual.getRecords().get(0).getUpdatedDate(), equalTo(OffsetDateTime.parse("2014-02-04T15:59:21Z")));
        assertThat(actual.getRecords().get(0).getFields(), not(nullValue()));
        assertThat(actual.getRecords().get(0).getFields().get("account"), not(nullValue()));
        assertThat(actual.getRecords().get(0).getFields().get("account").get("name"), equalTo("Dell Boomi"));
        assertThat(actual.getRecords().get(0).getFields().get("account").get("description"), equalTo("Cloud based data management"));
        assertThat(actual.getRecords().get(0).getFields().get("account").get("account_number"), equalTo("1234561234"));
        assertThat(actual.getRecords().get(0).getFields().get("account").get("phone_number"), equalTo("(610) 111-1111"));
        assertThat(actual.getRecords().get(0).getFields().get("account").get("fax"), equalTo("(610) 111-4444"));
        assertThat(actual.getRecords().get(0).getFields().get("account").get("billing_address"), instanceOf(Map.class));
        assertThat(((Map<String, Object>) actual.getRecords().get(0).getFields().get("account").get("billing_address")).get("billing_address"), equalTo("801 Cassat Rd."));
        assertThat(((Map<String, Object>) actual.getRecords().get(0).getFields().get("account").get("billing_address")).get("billing_city"), equalTo("Berwyn"));
        assertThat(((Map<String, Object>) actual.getRecords().get(0).getFields().get("account").get("billing_address")).get("billing_state"), equalTo("PA"));
        assertThat(((Map<String, Object>) actual.getRecords().get(0).getFields().get("account").get("billing_address")).get("billing_postal_code"), equalTo("19312"));
        assertThat(actual.getRecords().get(0).getFields().get("account").get("website"), equalTo("http://boomi.com"));
        assertThat(actual.getRecords().get(0).getFields().get("account").get("number_of_employees"), equalTo("200"));

    }
}
