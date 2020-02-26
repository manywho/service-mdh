package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.common.Entities;
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

        assertThat(object2.getProperties().get(1).getDeveloperName(), equalTo("accountnest2 - billing_address_rename"));
        assertThat(object2.getProperties().get(1).getContentValue(), nullValue());
        assertThat(object2.getProperties().get(1).getObjectData(), hasSize(2));
        assertThat(object2.getProperties().get(1).getObjectData().get(0).getDeveloperName(), equalTo("accountnest2 - billing_address_rename"));

        assertThat(object2.getProperties().get(2).getDeveloperName(), equalTo("other_single_value"));
        assertThat(object2.getProperties().get(2).getContentValue(), equalTo("other single value 2"));

        assertThat(object2.getProperties().get(3).getDeveloperName(), equalTo("accountnest2 - nest2contact"));
        assertThat(object2.getProperties().get(3).getContentValue(), nullValue());
        assertThat(object2.getProperties().get(3).getObjectData(), hasSize(1));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getDeveloperName(), equalTo("accountnest2 - nest2contact"));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getProperties(), hasSize(2));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("nest2_contact_name"));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("contatc name 2"));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("nest2_contact_phone"));
        assertThat(object2.getProperties().get(3).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("contact phone 2"));
    }

    @Test
    public void testAddingPropertiesToGoldenRecordObject() {
        URL goldenRecords = Resources.getResource("testXmlMapperDeserializesGoldenRecordQueryResponsesRepeatables.xml");
        GoldenRecordQueryResponse goldenRecordQueryResponse = JAXB.unmarshal(goldenRecords, GoldenRecordQueryResponse.class);
        GoldenRecord goldenRecordProto = goldenRecordQueryResponse.getRecords().get(0);

        MObject mObject = Entities.createGoldenRecordMObject("universeId", goldenRecordProto);

        assertThat(mObject.getProperties(), hasSize(8));
        assertThat(mObject.getProperties().get(4).getDeveloperName(), equalTo("___links"));
        assertThat(mObject.getProperties().get(4).getObjectData(), hasSize(1));
        assertThat(mObject.getProperties().get(4).getObjectData().get(0).getProperties().get(0).getDeveloperName(), equalTo("Source"));
        assertThat(mObject.getProperties().get(4).getObjectData().get(0).getProperties().get(0).getContentValue(), equalTo("flow"));

        assertThat(mObject.getProperties().get(4).getObjectData().get(0).getProperties().get(1).getDeveloperName(), equalTo("Entity ID"));
        assertThat(mObject.getProperties().get(4).getObjectData().get(0).getProperties().get(1).getContentValue(), equalTo("2"));

        assertThat(mObject.getProperties().get(4).getObjectData().get(0).getProperties().get(2).getDeveloperName(), equalTo("Established Date"));
        assertThat(mObject.getProperties().get(4).getObjectData().get(0).getProperties().get(2).getContentValue(), equalTo("2020-01-27T14:17:00Z"));

        assertThat(mObject.getProperties().get(5).getDeveloperName(), equalTo("___recordId"));
        assertThat(mObject.getProperties().get(5).getContentValue(), equalTo("9799d1e7-e3da-4c90-9853-68be3cdfdca4"));

        assertThat(mObject.getProperties().get(6).getDeveloperName(), equalTo("___filterCreatedDate"));
        assertThat(mObject.getProperties().get(6).getContentValue(), equalTo("2020-01-27T14:17:00Z"));

        assertThat(mObject.getProperties().get(7).getDeveloperName(), equalTo("___filterUpdatedDate"));
        assertThat(mObject.getProperties().get(7).getContentValue(), equalTo("2020-01-27T14:17:01Z"));
    }
}
