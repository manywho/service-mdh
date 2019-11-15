package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.common.DateFilter;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityResponse;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordHistoryResponse;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryResponse;
import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import org.junit.Test;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

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
                .setIncludeSourceLinks(true)
                .setFilter(filter)
                .setSort(sort);

        StringWriter requestContent = new StringWriter();
        JAXB.marshal(request, requestContent);

        Input.Builder expected = Input.fromURL(Resources.getResource("testXmlMapperSerializesGoldenRecordQueryRequests.xml"));

        assertThat(requestContent.toString(), isSimilarTo(expected).ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testXmlMapperSerializesGoldenRecordUpdateRequests() {
        BatchUpdateRequest.Entity entityOne = new BatchUpdateRequest.Entity()
                .setFields(new HashMap<String, Object>() {
                    {
                        put("id", 1);
                        put("name", "bob");
                        put("city", "berwyn");
                        put("phones", Arrays.asList(
                                        new HashMap<String, Object>() {
                                            {
                                                put("phone", new HashMap<String, Object>() {
                                                    {
                                                        put("number", "311 555-1234");
                                                        put("type", "home");
                                                    }});
                                            }},
                                        new HashMap<String, Object>() {
                                            {
                                                put("phone", new HashMap<String, Object>() {
                                                    {
                                                        put("number", "311 555-4321");
                                                        put("type", "mobile");
                                                    }
                                                });
                                            }}));
                        put("email", "bob@gmail.com");
                    }})
                .setName("contact")
                .setOp(null);

        BatchUpdateRequest.Entity entityTwo = new BatchUpdateRequest.Entity()
                .setFields(new HashMap<String, Object>() {{
                                put("id", 2);
                                put("name", "sam");
                                put("city", "pottstown");
                                put("phones", Arrays.asList(
                                        new HashMap<String, Object>() {
                                            {
                                                put("phone", new HashMap<String, Object>() {
                                                    {
                                                        put("number", "311 555-8765");
                                                        put("type", "mobile");
                                                    }
                                                });
                                            }}));
                                put("email", "sam@gmail.com");
                                }})
                .setName("contact")
                .setOp("CREATE");

        BatchUpdateRequest request = new BatchUpdateRequest()
                .setSource("SF")
                .setEntities(Arrays.asList(entityOne, entityTwo));

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

        Multimap account0 = (Multimap) actual.getRecords().get(0).getFields().get("account").iterator().next();
        assertThat(actual.getRecords().get(0).getFields().get("account"), not(nullValue()));
        assertThat(account0.get("name").iterator().next(), equalTo("Dell Boomi"));
        assertThat(account0.get("description").iterator().next(), equalTo("Cloud based data management"));
        assertThat(account0.get("account_number").iterator().next(), equalTo("1234561234"));
        assertThat(account0.get("phone_number").iterator().next(), equalTo("(610) 111-1111"));
        assertThat(account0.get("fax").iterator().next(), equalTo("(610) 111-4444"));

        Multimap billingAddress0 = (Multimap) ((Multimap) actual.getRecords().get(0).getFields().get("account").iterator().next()).get("billing_address").iterator().next();

        assertThat(billingAddress0, instanceOf(ArrayListMultimap.class));
        assertThat(billingAddress0.get("billing_address").iterator().next(), equalTo("801 Cassat Rd."));
        assertThat(billingAddress0.get("billing_city").iterator().next(), equalTo("Berwyn"));
        assertThat(billingAddress0.get("billing_state").iterator().next(), equalTo("PA"));
        assertThat(billingAddress0.get("billing_postal_code").iterator().next(), equalTo("19312"));

        assertThat(((Multimap) actual.getRecords().get(0).getFields().get("account").iterator().next()).get("website").iterator().next(), equalTo("http://boomi.com"));
        assertThat(((Multimap) actual.getRecords().get(0).getFields().get("account").iterator().next()).get("number_of_employees").iterator().next(), equalTo("200"));

        assertThat(actual.getRecords().get(0).getLinks().get(0).getEntityId(), equalTo("d39bc927-e005-4157-b73c-4956bfa2acb1"));
        assertThat(actual.getRecords().get(0).getLinks().get(0).getSource(), equalTo("flow1"));
        assertThat(actual.getRecords().get(0).getLinks().get(0).getEstablishedDate().toString(), equalTo("2019-08-08T10:56:41Z"));
    }


    @Test
    public void testXmlMapperDeserializesGoldenRecordQueryResponsesRepeatableFields() {
        URL data1 = Resources.getResource("testXmlMapperDeserializesGoldenRecordQueryResponses2.xml");

        GoldenRecordQueryResponse actual1 = JAXB.unmarshal(data1, GoldenRecordQueryResponse.class);

        Iterator billingAddressIterator = ((Multimap)actual1.getRecords().get(0).getFields().get("account").iterator().next()).get("billing_address").iterator();

        assertThat(((Multimap)billingAddressIterator.next()).get("billing_address_street").iterator().next(), equalTo("801 Cassat Rd."));
        assertThat(((Multimap)billingAddressIterator.next()).get("billing_address_street").iterator().next(), equalTo("802 Cassat Rd."));


        URL data2 = Resources.getResource("testXmlMapperDeserializesGoldenRecordQueryResponses3.xml");

        GoldenRecordQueryResponse actual2 = JAXB.unmarshal(data2, GoldenRecordQueryResponse.class);
        Iterator billingAddressStreetIterator = ((Multimap)((Multimap)actual2.getRecords().get(0).getFields().get("account").iterator().next()).get("billing_address").iterator().next()).get("billing_address_street").iterator();

        assertThat(billingAddressStreetIterator.next(), equalTo("801 Cassat Rd."));
        assertThat(billingAddressStreetIterator.next(), equalTo("802 Cassat Rd."));
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
                .setFields(new HashMap<String, Object>() {
                    {
                        put("id", 1);
                        put("name", "bobby");
                        put("city", "berwyn");
                        put("phones", Arrays.asList(
                                new HashMap<String, Object>() {{
                                    put("phone", new HashMap<String, String>() {
                                        {
                                            put("number", "311 555-1234");
                                            put("type", "home");
                                        }});
                                }},
                                new HashMap<String, Object>() {
                                    {
                                        put("phone", new HashMap<String, String>() {
                                            {
                                                put("number", "311 555-4321");
                                                put("type", "mobile");
                                            }});
                                }}));
                        put("email", "bob@gmail.com");
                        put("spouse", "1001");
                    }})
                .setName("contact");

        BatchUpdateRequest.Entity entityTwo = new BatchUpdateRequest.Entity()
                .setFields(new HashMap<String, Object>() {
                    {
                        put("id", 2);
                        put("name", "mike");
                        put("city", "chesterbrook");
                        put("phones", Arrays.asList(
                                new HashMap<String, Object>() {
                                    {
                                        put("phone", new HashMap<String, Object>() {
                                            {
                                                put("number", "311 555-2345");
                                                put("type", "home");
                                            }});
                                    }},
                                new HashMap<String, Object>() {
                                    {
                                        put("phone", new HashMap<String, Object>() {
                                            {
                                                put("number", "311 555-5432");
                                                put("type", "mobile");
                                            }
                                        });
                                    }
                                })
                        );
                        put("email", "mike@gmail.com");
                        put("spouse", "1002");
                    }})
                .setName("contact");

        BatchUpdateRequest request = new BatchUpdateRequest()
                                .setSource("SF")
                                .setEntities(Arrays.asList(entityOne, entityTwo));

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

        Multimap contact0 = ((Multimap) actual.getMatchResults().get(0).getEntity().get("contact").iterator().next());

        assertThat( contact0.get("id").iterator().next(), equalTo("1"));
        assertThat( contact0.get("name").iterator().next(), equalTo("bobby"));
        assertThat( contact0.get("city").iterator().next(), equalTo("berwyn"));
        assertThat( contact0.get("email").iterator().next(), equalTo("bob@gmail.com"));
        assertThat( contact0.get("spouse").iterator().next(), equalTo("1001"));
        assertThat( contact0.get("phones").iterator().next(), notNullValue());
        assertThat( ((Multimap)((Multimap)contact0.get("phones").iterator().next()).get("phone").iterator().next()).get("number").iterator().next(), equalTo("311 555-1234"));
        assertThat( ((Multimap)((Multimap)contact0.get("phones").iterator().next()).get("phone").iterator().next()).get("type").iterator().next(), equalTo("home"));

        assertThat( ((Multimap)((Multimap)contact0.get("phones").iterator().next()).get("phone").iterator().next()).get("number").iterator().next(), equalTo("311 555-1234"));
        assertThat( ((Multimap)((Multimap)contact0.get("phones").iterator().next()).get("phone").iterator().next()).get("type").iterator().next(), equalTo("home"));

        Multimap matchContact0 = ((Multimap) actual.getMatchResults().get(0).getMatch().get(0).get("contact").iterator().next());

        assertThat(matchContact0.get("id").iterator().next(), equalTo("e6e1b847-d61a-46d9-a610-c678ba40ca41"));
        assertThat(matchContact0.get("name").iterator().next(), equalTo("bob"));
        assertThat(matchContact0.get("city").iterator().next(), equalTo("berwyn"));
        assertThat(matchContact0.get("email").iterator().next(), equalTo("bob@gmail.com"));
        assertThat(matchContact0.get("spouse").iterator().next(), equalTo("1001"));

        Multimap match0FuzzyMatchDetails = ((Multimap) actual.getMatchResults().get(0).getMatch().get(0).get("fuzzyMatchDetails").iterator().next());

        assertThat(match0FuzzyMatchDetails.get("field").iterator().next(), equalTo("name"));
        assertThat(match0FuzzyMatchDetails.get("first").iterator().next(), equalTo("BOBBY"));
        assertThat(match0FuzzyMatchDetails.get("second").iterator().next(), equalTo("BOB"));
        assertThat(match0FuzzyMatchDetails.get("method").iterator().next(), equalTo("jarowinkler"));
        assertThat(match0FuzzyMatchDetails.get("matchStrength").iterator().next(), equalTo("0.90666664"));
        assertThat(match0FuzzyMatchDetails.get("threshold").iterator().next(), equalTo("0.85"));

        Multimap duplicatedContact0 = ((Multimap) actual.getMatchResults().get(0).getDuplicate().get(0).get("contact").iterator().next());

        assertThat(duplicatedContact0.get("id").iterator().next(), equalTo("fc8cd5be-ac26-4e9a-9d0c-6b397a124172"));
        assertThat(duplicatedContact0.get("name").iterator().next(), equalTo("bob"));
        assertThat(duplicatedContact0.get("city").iterator().next(), equalTo("berwyn"));
        assertThat(duplicatedContact0.get("email").iterator().next(), equalTo("bob@gmail.com"));
        assertThat(duplicatedContact0.get("spouse").iterator().next(), equalTo("1001"));

        Multimap duplicate0FuzzyMatchDetails = ((Multimap) actual.getMatchResults().get(0).getMatch().get(0).get("fuzzyMatchDetails").iterator().next());


        assertThat(duplicate0FuzzyMatchDetails.get("field").iterator().next(), equalTo("name"));
        assertThat(duplicate0FuzzyMatchDetails.get("first").iterator().next(), equalTo("BOBBY"));
        assertThat(duplicate0FuzzyMatchDetails.get("second").iterator().next(), equalTo("BOB"));
        assertThat(duplicate0FuzzyMatchDetails.get("method").iterator().next(), equalTo("jarowinkler"));
        assertThat(duplicate0FuzzyMatchDetails.get("matchStrength").iterator().next(), equalTo("0.90666664"));
        assertThat(duplicate0FuzzyMatchDetails.get("threshold").iterator().next(), equalTo("0.85"));

        Multimap matchContact1 = ((Multimap) actual.getMatchResults().get(1).getEntity().get("contact").iterator().next());

        assertThat( actual.getMatchResults().get(1).getMatchRule(), nullValue());
        assertThat( actual.getMatchResults().get(1).getStatus(), equalTo("ALREADY_LINKED"));

        assertThat( matchContact1.get("id").iterator().next(), equalTo("2"));
        assertThat( matchContact1.get("name").iterator().next(), equalTo("mike"));
        assertThat( matchContact1.get("city").iterator().next(), equalTo("chesterbrook"));
        assertThat( matchContact1.get("email").iterator().next(), equalTo("mike@gmail.com"));
        assertThat( matchContact1.get("spouse").iterator().next(), equalTo("1002"));
    }
}
