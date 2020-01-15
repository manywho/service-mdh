package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.boomi.flow.services.boomi.mdh.database.FieldMapper;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.manywho.sdk.api.jackson.ObjectMapperFactory;
import com.manywho.sdk.api.run.elements.type.MObject;
import org.junit.Assert;
import org.junit.Test;
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

public class FieldMapperTests {
    @Test
    public void testCreateHashFromMobjectWithRepeatablesFieldGroup() throws IOException {
        MObject mObject = ObjectMapperFactory
                .create()
                .readValue(Resources.getResource("field-map/repeatable/mobject.json"), MObject.class);
        Universe universe = JAXB.unmarshal(Resources.getResource("field-map/repeatable/universe.xml"), Universe.class);

        Map<String, Object> fields = FieldMapper.createMapFromModelMobject(mObject, universe);

        BatchUpdateRequest.Entity entity = new BatchUpdateRequest.Entity()
                .setOp(null)
                .setName(universe.getLayout().getModel().getName())
                .setFields(fields);

        BatchUpdateRequest updateRequest = new BatchUpdateRequest()
                .setSource("flow")
                .setEntities(Collections.singletonList(entity));

        StringWriter bodyContent = new StringWriter();
        JAXB.marshal(updateRequest, bodyContent);

        String expected = Resources.toString(Resources.getResource("field-map/repeatable/record-update-request-deserialized.xml"), Charsets.UTF_8);

        Assert.assertEquals(expected, bodyContent.toString());
    }

    @Test
    public void testCreateHashFromMobjectWithNestedFieldGroup() throws IOException {
        MObject mObject = ObjectMapperFactory
                .create()
                .readValue(Resources.getResource("field-map/nested-fieldgroup/mobject.json"), MObject.class);
        Universe universe = JAXB.unmarshal(Resources.getResource("field-map/nested-fieldgroup/universe.xml"), Universe.class);

        Map<String, Object> fields = FieldMapper.createMapFromModelMobject(mObject, universe);

        BatchUpdateRequest.Entity entity = new BatchUpdateRequest.Entity()
                .setOp(null)
                .setName(universe.getLayout().getModel().getName())
                .setFields(fields);

        BatchUpdateRequest updateRequest = new BatchUpdateRequest()
                .setSource("flow")
                .setEntities(Collections.singletonList(entity));

        StringWriter bodyContent = new StringWriter();
        JAXB.marshal(updateRequest, bodyContent);

        String expected = Resources.toString(Resources.getResource("field-map/nested-fieldgroup/record-update-request-deserialized.xml"), Charsets.UTF_8);

        Assert.assertEquals(expected, bodyContent.toString());
    }
}
