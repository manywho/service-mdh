package com.boomi.flow.services.boomi.mdh;


import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryResponse;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryResponseProto;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import org.junit.Test;
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class XmlMapperGoldenRecordProtoTests {


    @Test
    public void testXmlMapperDeserializesGoldenRecordQueryResponses() throws IOException {
        URL data = Resources.getResource("testXmlMapperDeserializesGoldenRecordQueryResponses.xml");

        GoldenRecordQueryResponseProto actual = JAXB.unmarshal(data, GoldenRecordQueryResponseProto.class);

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

        GoldenRecordQueryResponseProto actual1 = JAXB.unmarshal(data1, GoldenRecordQueryResponseProto.class);

        Iterator billingAddressIterator = ((Multimap)actual1.getRecords().get(0).getFields().get("account").iterator().next()).get("billing_address").iterator();

        assertThat(((Multimap)billingAddressIterator.next()).get("billing_address_street").iterator().next(), equalTo("801 Cassat Rd."));
        assertThat(((Multimap)billingAddressIterator.next()).get("billing_address_street").iterator().next(), equalTo("802 Cassat Rd."));


        URL data2 = Resources.getResource("testXmlMapperDeserializesGoldenRecordQueryResponses3.xml");

        GoldenRecordQueryResponse actual2 = JAXB.unmarshal(data2, GoldenRecordQueryResponse.class);
        Iterator billingAddressStreetIterator = ((Multimap)((Multimap)actual2.getRecords().get(0).getFields().get("account").iterator().next()).get("billing_address").iterator().next()).get("billing_address_street").iterator();

        assertThat(billingAddressStreetIterator.next(), equalTo("801 Cassat Rd."));
        assertThat(billingAddressStreetIterator.next(), equalTo("802 Cassat Rd."));
    }
}
