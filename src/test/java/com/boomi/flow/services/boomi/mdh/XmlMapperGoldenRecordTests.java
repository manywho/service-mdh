package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.records.GoldenRecord;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryResponse;
import com.google.common.io.Resources;
import com.manywho.sdk.api.run.elements.type.MObject;
import org.junit.Test;
import javax.xml.bind.JAXB;
import java.net.URL;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class XmlMapperGoldenRecordTests {

    @Test
    public void testXMLPure() {
        URL goldenRecords = Resources.getResource("testXmlMapperDeserializesGoldenRecordQueryResponsesRepeatables.xml");

        GoldenRecordQueryResponse goldenRecordQueryResponse = JAXB.unmarshal(goldenRecords, GoldenRecordQueryResponse.class);
        GoldenRecord goldenRecordProto = goldenRecordQueryResponse.getRecords().get(0);

        MObject object2 = goldenRecordProto.getMObject();
        assertThat(object2.getProperties(), hasSize(4));
        assertThat(object2.getProperties().get(0).getDeveloperName(), equalTo("single_value"));
        assertThat(object2.getProperties().get(0).getContentValue(), equalTo("single value 2-1"));

        assertThat(object2.getProperties().get(1).getDeveloperName(), equalTo("billing_address_collection_wrapper"));
        assertThat(object2.getProperties().get(1).getContentValue(), nullValue());
        assertThat(object2.getProperties().get(1).getObjectData(), hasSize(2));
        assertThat(object2.getProperties().get(1).getObjectData().get(0).getDeveloperName(), equalTo("billing_address_rename-child"));

        assertThat(object2.getProperties().get(2).getDeveloperName(), equalTo("other_single_value"));
        assertThat(object2.getProperties().get(2).getContentValue(), equalTo("other single value 2"));

        assertThat(object2.getProperties().get(3).getDeveloperName(), equalTo("nest2contact"));
        assertThat(object2.getProperties().get(3).getContentValue(), nullValue());
        assertThat(object2.getProperties().get(3).getObjectData(), hasSize(1));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getDeveloperName(), equalTo("nest2contact-child"));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getProperties(), hasSize(2));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("nest2_contact_name"));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("contatc name 2"));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("nest2_contact_phone"));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("contact phone 2"));
    }
}
