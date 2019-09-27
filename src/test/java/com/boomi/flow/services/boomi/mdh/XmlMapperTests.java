package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.common.DateFilter;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityResponse;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordHistoryResponse;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryResponse;
import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.google.common.io.Resources;
import org.junit.Test;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class XmlMapperTests {
    @Test
    public void testXmlMapperSerializesQuarantineQueryRequests() {
        QuarantineQueryRequest.Filter filter = new QuarantineQueryRequest.Filter()
                .setCauses(Arrays.asList("cause 1", "cause 2"))
                .setCreatedDate(new DateFilter()
                        .setFrom(OffsetDateTime.parse("2013-01-01T00:00Z"))
                        .setTo(OffsetDateTime.parse("2015-12-31T00:00Z"))
                )
                .setEndDate(new DateFilter()
                        .setFrom(OffsetDateTime.parse("2018-01-01T00:00Z"))
                        .setTo(OffsetDateTime.parse("2018-12-31T00:00Z"))
                )
                .setResolutions(Arrays.asList("resolution 1", "resolution 2"))
                .setSourceEntityId("a source entity id")
                .setSourceId("a source id");

        QuarantineQueryRequest request = new QuarantineQueryRequest()
                .setFilter(filter)
                .setIncludeData(true)
                .setType("a type");

        StringWriter requestContent = new StringWriter();
        JAXB.marshal(request, requestContent);

        Input.Builder expected = Input.fromURL(Resources.getResource("testXmlMapperSerializesQuarantineQueryRequests.xml"));

        assertThat(requestContent.toString(), isSimilarTo(expected).ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testXmlMapperSerializesGoldenRecordQueryRequests() {
        GoldenRecordQueryRequest.Filter.FieldValue fieldValueOne = new GoldenRecordQueryRequest.Filter.FieldValue()
                .setFieldId("field 1")
                .setOperator("LESS_THAN")
                .setValue("some value");

        GoldenRecordQueryRequest.Filter.FieldValue fieldValueTwo = new GoldenRecordQueryRequest.Filter.FieldValue()
                .setFieldId("field 2")
                .setOperator("EQUAL")
                .setValue("another value");

        GoldenRecordQueryRequest.Filter filter = new GoldenRecordQueryRequest.Filter()
                .setCreatedDate(new DateFilter()
                        .setFrom(OffsetDateTime.parse("2013-01-01T00:00Z"))
                        .setTo(OffsetDateTime.parse("2015-12-31T00:00Z"))
                )
                .setCreatingSourceId("12345")
                .setFieldValues(Arrays.asList(fieldValueOne, fieldValueTwo))
                .setUpdatedDate(new DateFilter()
                        .setFrom(OffsetDateTime.parse("2018-01-01T00:00Z"))
                        .setTo(OffsetDateTime.parse("2018-12-31T00:00Z"))
                );

        GoldenRecordQueryRequest.Sort.Field sortFieldOne = new GoldenRecordQueryRequest.Sort.Field()
                .setFieldId("field 1")
                .setDirection("DESC");

        GoldenRecordQueryRequest.Sort.Field sortFieldTwo = new GoldenRecordQueryRequest.Sort.Field()
                .setFieldId("field 2")
                .setDirection("ASC");

        GoldenRecordQueryRequest.Sort sort = new GoldenRecordQueryRequest.Sort()
                .setFields(Arrays.asList(sortFieldOne, sortFieldTwo));

        GoldenRecordQueryRequest request = new GoldenRecordQueryRequest()
                .setFilter(filter)
                .setSort(sort);

        StringWriter requestContent = new StringWriter();
        JAXB.marshal(request, requestContent);

        Input.Builder expected = Input.fromURL(Resources.getResource("testXmlMapperSerializesGoldenRecordQueryRequests.xml"));

        assertThat(requestContent.toString(), isSimilarTo(expected).ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testXmlMapperSerializesGoldenRecordUpdateRequests() throws JAXBException {
        BatchUpdateRequest.Entity entityOne = new BatchUpdateRequest.Entity()
                .setFields(Map.ofEntries(
                        Map.entry("id", 1),
                        Map.entry("name", "bob"),
                        Map.entry("city", "berwyn"),
                        Map.entry("phones", Arrays.asList(
                                Map.entry("phone", Map.ofEntries(
                                        Map.entry("number", "311 555-1234"),
                                        Map.entry("type", "home")
                                )),
                                Map.entry("phone", Map.ofEntries(
                                        Map.entry("number", "311 555-4321"),
                                        Map.entry("type", "mobile")
                                ))
                        )),
                        Map.entry("email", "bob@gmail.com")
                ))
                .setName("contact")
                .setOp(null);

        BatchUpdateRequest.Entity entityTwo = new BatchUpdateRequest.Entity()
                .setFields(Map.ofEntries(
                        Map.entry("id", 2),
                        Map.entry("name", "sam"),
                        Map.entry("city", "pottstown"),
                        Map.entry("phones", Arrays.asList(
                                Map.entry("phone", Map.ofEntries(
                                        Map.entry("number", "311 555-8765"),
                                        Map.entry("type", "mobile")
                                ))
                        )),
                        Map.entry("email", "sam@gmail.com")
                ))
                .setName("contact")
                .setOp("CREATE");

        BatchUpdateRequest request = new BatchUpdateRequest()
                .setSource("SF")
                .setEntities(Arrays.asList(
                        entityOne,
                        entityTwo
                ));

        StringWriter requestContent = new StringWriter();
        JAXB.marshal(request, requestContent);

        Input.Builder expected = Input.fromURL(Resources.getResource("testXmlMapperSerializesGoldenRecordUpdateRequests.xml"));

        assertThat(requestContent.toString(), isSimilarTo(expected).ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testXmlMapperDeserializesGoldenRecordQueryResponses() throws IOException {
        URL data = Resources.getResource("testXmlMapperDeserializesGoldenRecordQueryResponses.xml");

        GoldenRecordQueryResponse actual = JAXB.unmarshal(data, GoldenRecordQueryResponse.class);

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
//        assertThat(actual.getRecords().get(0).getFields().get("account").get("billing_address"), instanceOf(Map.class));
//        assertThat(((Map<String, Object>) actual.getRecords().get(0).getFields().get("account").get("billing_address")).get("billing_address"), equalTo("801 Cassat Rd."));
//        assertThat(((Map<String, Object>) actual.getRecords().get(0).getFields().get("account").get("billing_address")).get("billing_city"), equalTo("Berwyn"));
//        assertThat(((Map<String, Object>) actual.getRecords().get(0).getFields().get("account").get("billing_address")).get("billing_state"), equalTo("PA"));
//        assertThat(((Map<String, Object>) actual.getRecords().get(0).getFields().get("account").get("billing_address")).get("billing_postal_code"), equalTo("19312"));
        assertThat(actual.getRecords().get(0).getFields().get("account").get("website"), equalTo("http://boomi.com"));
        assertThat(actual.getRecords().get(0).getFields().get("account").get("number_of_employees"), equalTo("200"));
    }

    @Test
    public void testXmlMapperDeserializesGoldenRecordHistoryResponse() throws IOException {
        URL data = Resources.getResource("testXmlMapperDeserializesGoldenRecordHistoryResponse.xml");

        GoldenRecordHistoryResponse actual = JAXB.unmarshal(data, GoldenRecordHistoryResponse.class);

        assertThat(actual, not(nullValue()));
        assertThat(actual.getResultCount(), equalTo(23));
        assertThat(actual.getTotalCount(), equalTo(823));
        assertThat(actual.getRecords(), hasSize(2));
        assertThat(actual.getRecords().get(0).getEndDate(), equalTo(OffsetDateTime.parse("2015-02-08T14:17:06.000-04:00")));
        assertThat(actual.getRecords().get(0).getEndDateSource(), equalTo("SF"));
        assertThat(actual.getRecords().get(0).getFields(), not(nullValue()));
        assertThat(actual.getRecords().get(0).getFields().get("id"), equalTo("d5742c16-5318-4ba7-8815-3267a7a55358"));
        assertThat(actual.getRecords().get(0).getFields().get("name"), equalTo("bob"));
        assertThat(actual.getRecords().get(0).getFields().get("city"), equalTo("malvern"));
        assertThat(actual.getRecords().get(0).getFields().get("email"), equalTo("bob@gmail.com"));
        assertThat(actual.getRecords().get(0).getGrid(), equalTo("d5742c16-5318-4ba7-8815-3267a7a55358"));
        assertThat(actual.getRecords().get(0).getSource(), equalTo("SF"));
        assertThat(actual.getRecords().get(0).getStartDate(), equalTo(OffsetDateTime.parse("2015-02-07T08:36:37.000-04:00")));
        assertThat(actual.getRecords().get(0).getTransactionId(), equalTo("12345678-9abc-def0-1234-56789abcdef0"));
        assertThat(actual.getRecords().get(0).getVersion(), equalTo(801L));

        assertThat(actual.getRecords().get(1).getEndDate(), nullValue());
        assertThat(actual.getRecords().get(1).getEndDateSource(), nullValue());
        assertThat(actual.getRecords().get(1).getFields(), not(nullValue()));
        assertThat(actual.getRecords().get(1).getFields().get("id"), equalTo("d5742c16-5318-4ba7-8815-3267a7a55358"));
        assertThat(actual.getRecords().get(1).getFields().get("name"), equalTo("bob"));
        assertThat(actual.getRecords().get(1).getFields().get("city"), equalTo("berwyn"));
        assertThat(actual.getRecords().get(1).getFields().get("email"), equalTo("bob@gmail.com"));
        assertThat(actual.getRecords().get(1).getGrid(), equalTo("d5742c16-5318-4ba7-8815-3267a7a55358"));
        assertThat(actual.getRecords().get(1).getSource(), equalTo("SF"));
        assertThat(actual.getRecords().get(1).getStartDate(), equalTo(OffsetDateTime.parse("2016-03-14T14:17:07.000-04:00")));
        assertThat(actual.getRecords().get(1).getTransactionId(), equalTo("01234567-89ab-cdef-0123-456789abcdef"));
        assertThat(actual.getRecords().get(1).getVersion(), equalTo(823L));
    }

    @Test
    public void testXmlMapperSerializesMatchEntityQueryRequests() {
        BatchUpdateRequest.Entity entityOne = new BatchUpdateRequest.Entity()
                .setFields(Map.ofEntries(
                        Map.entry("id", 1),
                        Map.entry("name", "bobby"),
                        Map.entry("city", "berwyn"),
                        Map.entry("phones", Arrays.asList(
                                Map.entry("phone", Map.ofEntries(
                                        Map.entry("number", "311 555-1234"),
                                        Map.entry("type", "home")
                                )),
                                Map.entry("phone", Map.ofEntries(
                                        Map.entry("number", "311 555-4321"),
                                        Map.entry("type", "mobile")
                                ))
                        )),
                        Map.entry("email", "bob@gmail.com"),
                        Map.entry("spouse", "1001")
                ))
                .setName("contact");

        BatchUpdateRequest.Entity entityTwo = new BatchUpdateRequest.Entity()
                .setFields(Map.ofEntries(
                        Map.entry("id", 2),
                        Map.entry("name", "mike"),
                        Map.entry("city", "chesterbrook"),
                        Map.entry("phones", Arrays.asList(
                                Map.entry("phone", Map.ofEntries(
                                        Map.entry("number", "311 555-2345"),
                                        Map.entry("type", "home")
                                )),
                                Map.entry("phone", Map.ofEntries(
                                        Map.entry("number", "311 555-5432"),
                                        Map.entry("type", "mobile")
                                ))
                        )),
                        Map.entry("email", "mike@gmail.com"),
                        Map.entry("spouse", "1002")
                ))
                .setName("contact");

        BatchUpdateRequest request = new BatchUpdateRequest()
                .setSource("SF")
                .setEntities(Arrays.asList(
                        entityOne,
                        entityTwo
                ));

        StringWriter requestContent = new StringWriter();
        JAXB.marshal(request, requestContent);

        Input.Builder expected = Input.fromURL(Resources.getResource("testXmlMapperSerializesMatchEntityQueryRequests.xml"));

        assertThat(requestContent.toString(), isSimilarTo(expected).ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testXmlMapperDeserializesMatchEntityResponse() {
        URL data = Resources.getResource("testXmlMapperDeserializesMatchEntitiesResponse.xml");
        MatchEntityResponse actual = JAXB.unmarshal(data, MatchEntityResponse.class);

        assertThat(actual, not(nullValue()));

        assertThat( actual.getMatchResults().get(0).getMatchRule(), equalTo("Incoming name is similar to (Jaro-Winkler) Existing name"));
        assertThat( actual.getMatchResults().get(0).getStatus(), equalTo("SUCCESS"));
        assertThat( actual.getMatchResults().get(0).getEntity().get("contact").get("id"), equalTo("1"));
        assertThat( actual.getMatchResults().get(0).getEntity().get("contact").get("name"), equalTo("bobby"));
        assertThat( actual.getMatchResults().get(0).getEntity().get("contact").get("city"), equalTo("berwyn"));
        assertThat( actual.getMatchResults().get(0).getEntity().get("contact").get("email"), equalTo("bob@gmail.com"));
        assertThat( actual.getMatchResults().get(0).getEntity().get("contact").get("spouse"), equalTo("1001"));
        //assertThat( actual.getMatchResults().get(0).getEntity().get("contact").get("phones"), notNull); // not working properly need to be fixed for child elements

        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("contact")).get("id"), equalTo("e6e1b847-d61a-46d9-a610-c678ba40ca41"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("contact")).get("name"), equalTo("bob"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("contact")).get("city"), equalTo("berwyn"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("contact")).get("email"), equalTo("bob@gmail.com"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("contact")).get("spouse"), equalTo("1001"));

        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("fuzzyMatchDetails")).get("field"), equalTo("name"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("fuzzyMatchDetails")).get("first"), equalTo("BOBBY"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("fuzzyMatchDetails")).get("second"), equalTo("BOB"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("fuzzyMatchDetails")).get("method"), equalTo("jarowinkler"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("fuzzyMatchDetails")).get("matchStrength"), equalTo("0.90666664"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getMatch().get(0).get("fuzzyMatchDetails")).get("threshold"), equalTo("0.85"));

        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("contact")).get("id"), equalTo("fc8cd5be-ac26-4e9a-9d0c-6b397a124172"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("contact")).get("name"), equalTo("bob"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("contact")).get("city"), equalTo("berwyn"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("contact")).get("email"), equalTo("bob@gmail.com"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("contact")).get("spouse"), equalTo("1001"));

        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("fuzzyMatchDetails")).get("field"), equalTo("name"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("fuzzyMatchDetails")).get("first"), equalTo("BOBBY"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("fuzzyMatchDetails")).get("second"), equalTo("BOB"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("fuzzyMatchDetails")).get("method"), equalTo("jarowinkler"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("fuzzyMatchDetails")).get("matchStrength"), equalTo("0.90666664"));
        assertThat(((HashMap)actual.getMatchResults().get(0).getDuplicate().get(0).get("fuzzyMatchDetails")).get("threshold"), equalTo("0.85"));

        assertThat( actual.getMatchResults().get(1).getMatchRule(), nullValue());
        assertThat( actual.getMatchResults().get(1).getStatus(), equalTo("ALREADY_LINKED"));
        assertThat( actual.getMatchResults().get(1).getEntity().get("contact").get("id"), equalTo("2"));
        assertThat( actual.getMatchResults().get(1).getEntity().get("contact").get("name"), equalTo("mike"));
        assertThat( actual.getMatchResults().get(1).getEntity().get("contact").get("city"), equalTo("chesterbrook"));
        assertThat( actual.getMatchResults().get(1).getEntity().get("contact").get("email"), equalTo("mike@gmail.com"));
        assertThat( actual.getMatchResults().get(1).getEntity().get("contact").get("spouse"), equalTo("1002"));
    }
}
