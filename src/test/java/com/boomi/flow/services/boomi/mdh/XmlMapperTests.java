package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.common.DateFilter;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityResponse;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordHistoryResponse;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryResponse;
import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.google.common.io.Resources;
import com.manywho.sdk.api.run.elements.type.MObject;
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
        assertThat(actual.getRecords().get(0).getCreatedDate(), equalTo(OffsetDateTime.parse("2013-09-23T00:00Z")));
        assertThat(actual.getRecords().get(0).getUpdatedDate(), equalTo(OffsetDateTime.parse("2014-02-04T15:59:21Z")));
        assertThat(actual.getRecords().get(0).getMObject(), not(nullValue()));

        MObject account0 = actual.getRecords().get(0).getMObject();
        assertThat(actual.getRecords().get(0).getMObject().getDeveloperName(), equalTo("account"));

        assertThat(account0.getProperties().get(0).getDeveloperName(), equalTo("name"));
        assertThat(account0.getProperties().get(0).getContentValue(), equalTo("Dell Boomi"));

        assertThat(account0.getProperties().get(1).getDeveloperName(), equalTo("description"));
        assertThat(account0.getProperties().get(1).getContentValue(), equalTo("Cloud based data management"));

        assertThat(account0.getProperties().get(2).getDeveloperName(), equalTo("account_number"));
        assertThat(account0.getProperties().get(2).getContentValue(), equalTo("1234561234"));

        assertThat(account0.getProperties().get(3).getDeveloperName(), equalTo("phone_number"));
        assertThat(account0.getProperties().get(3).getContentValue(), equalTo("(610) 111-1111"));

        assertThat(account0.getProperties().get(4).getDeveloperName(), equalTo("fax"));
        assertThat(account0.getProperties().get(4).getContentValue(), equalTo("(610) 111-4444"));

        MObject billingAddress0 = actual.getRecords().get(0).getMObject().getProperties().get(5).getObjectData().get(0);

        assertThat(billingAddress0.getProperties().get(0).getDeveloperName(), equalTo("billing_address"));
        assertThat(billingAddress0.getProperties().get(0).getContentValue(), equalTo("801 Cassat Rd."));
        assertThat(billingAddress0.getProperties().get(1).getDeveloperName(), equalTo("billing_city"));
        assertThat(billingAddress0.getProperties().get(1).getContentValue(), equalTo("Berwyn"));
        assertThat(billingAddress0.getProperties().get(2).getDeveloperName(), equalTo("billing_state"));
        assertThat(billingAddress0.getProperties().get(2).getContentValue(), equalTo("PA"));
        assertThat(billingAddress0.getProperties().get(3).getDeveloperName(), equalTo("billing_postal_code"));
        assertThat(billingAddress0.getProperties().get(3).getContentValue(), equalTo("19312"));

        assertThat(account0.getProperties().get(6).getDeveloperName(), equalTo("website"));
        assertThat(account0.getProperties().get(6).getContentValue(), equalTo("http://boomi.com"));
        assertThat(account0.getProperties().get(7).getDeveloperName(), equalTo("number_of_employees"));
        assertThat(account0.getProperties().get(7).getContentValue(), equalTo("200"));

        assertThat(actual.getRecords().get(0).getLinks().get(0).getEntityId(), equalTo("d39bc927-e005-4157-b73c-4956bfa2acb1"));
        assertThat(actual.getRecords().get(0).getLinks().get(0).getSource(), equalTo("flow1"));
        assertThat(actual.getRecords().get(0).getLinks().get(0).getEstablishedDate(), equalTo(OffsetDateTime.parse("2019-08-08T10:56:41Z")));
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

        assertThat(actual.getMatchResults().get(0).getEntity().getDeveloperName(), equalTo("contact"));

        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(0).getDeveloperName(), equalTo("id"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(0).getContentValue(), equalTo("1"));

        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(1).getDeveloperName(), equalTo("name"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(1).getContentValue(), equalTo("bobby"));

        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(2).getDeveloperName(), equalTo("city"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(2).getContentValue(), equalTo("berwyn"));

        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getDeveloperName(), equalTo("phone"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getContentValue(), nullValue());

        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getObjectData().get(0).getDeveloperName(), equalTo("contact - phone"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("number"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("311 555-1234"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("type"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("home"));

        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getObjectData().get(1).getDeveloperName(), equalTo("contact - phone"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getObjectData().get(1).getProperties().get(0).getDeveloperName(), equalTo("number"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getObjectData().get(1).getProperties().get(0).getContentValue(), equalTo("311 555-4321"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getObjectData().get(1).getProperties().get(1).getDeveloperName(), equalTo("type"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(3).getObjectData().get(1).getProperties().get(1).getContentValue(), equalTo("mobile"));

        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(4).getDeveloperName(), equalTo("email"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(4).getContentValue(), equalTo("bob@gmail.com"));

        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(5).getDeveloperName(), equalTo("spouse"));
        assertThat( actual.getMatchResults().get(0).getEntity().getProperties().get(5).getContentValue(), equalTo("1001"));

        // match entities
        assertThat(actual.getMatchResults().get(0).getStatus(), equalTo("SUCCESS"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getDeveloperName(), equalTo("contact"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(0).getDeveloperName(), equalTo("id"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(0).getContentValue(), equalTo("e6e1b847-d61a-46d9-a610-c678ba40ca41"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(1).getDeveloperName(), equalTo("name"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(1).getContentValue(), equalTo("bob"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(2).getDeveloperName(), equalTo("city"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(2).getContentValue(), equalTo("berwyn"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getDeveloperName(), equalTo("phone"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getContentValue(), nullValue());

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getObjectData().get(0).getDeveloperName(), equalTo("contact - phone"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("number"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("311 555-1234"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("type"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("home"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getObjectData().get(1).getDeveloperName(), equalTo("contact - phone"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getObjectData().get(1).getProperties().get(0).getDeveloperName(), equalTo("number"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getObjectData().get(1).getProperties().get(0).getContentValue(), equalTo("311 555-4321"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getObjectData().get(1).getProperties().get(1).getDeveloperName(), equalTo("type"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(3).getObjectData().get(1).getProperties().get(1).getContentValue(), equalTo("mobile"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(4).getDeveloperName(), equalTo("email"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(4).getContentValue(), equalTo("bob@gmail.com"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(5).getDeveloperName(), equalTo("spouse"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(5).getContentValue(), equalTo("1001"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getDeveloperName(), equalTo("Fuzzy Match Details"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getDeveloperName(), equalTo("Fuzzy Match Details"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("field"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("name"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("first"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("BOBBY"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(2).getDeveloperName(), equalTo("second"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(2).getContentValue(), equalTo("BOB"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(3).getDeveloperName(), equalTo("method"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(3).getContentValue(), equalTo("jarowinkler"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(4).getDeveloperName(), equalTo("matchStrength"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(4).getContentValue(), equalTo("0.90666664"));

        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(5).getDeveloperName(), equalTo("threshold"));
        assertThat( actual.getMatchResults().get(0).getMatch().get(0).getProperties().get(6).getObjectData().get(0).getProperties().get(5).getContentValue(), equalTo("0.85"));

        // already linked
        assertThat( actual.getMatchResults().get(1).getMatchRule(), nullValue());
        assertThat( actual.getMatchResults().get(1).getStatus(), equalTo("ALREADY_LINKED"));

        assertThat(actual.getMatchResults().get(1).getEntity().getDeveloperName(), equalTo("contact"));

        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(0).getDeveloperName(), equalTo("id"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(0).getContentValue(), equalTo("2"));

        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(1).getDeveloperName(), equalTo("name"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(1).getContentValue(), equalTo("mike"));

        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(2).getDeveloperName(), equalTo("city"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(2).getContentValue(), equalTo("chesterbrook"));

        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getDeveloperName(), equalTo("phone"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getContentValue(), nullValue());

        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getObjectData().get(0).getDeveloperName(), equalTo("contact - phone"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("number"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("311 555-2345"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("type"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("home"));

        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getObjectData().get(1).getDeveloperName(), equalTo("contact - phone"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getObjectData().get(1).getProperties().get(0).getDeveloperName(), equalTo("number"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getObjectData().get(1).getProperties().get(0).getContentValue(), equalTo("311 555-5432"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getObjectData().get(1).getProperties().get(1).getDeveloperName(), equalTo("type"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(3).getObjectData().get(1).getProperties().get(1).getContentValue(), equalTo("mobile"));

        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(4).getDeveloperName(), equalTo("email"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(4).getContentValue(), equalTo("mike@gmail.com"));

        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(5).getDeveloperName(), equalTo("spouse"));
        assertThat( actual.getMatchResults().get(1).getEntity().getProperties().get(5).getContentValue(), equalTo("1002"));
    }
}
