package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.database.FieldMapper;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.google.common.io.Resources;
import com.manywho.sdk.api.jackson.ObjectMapperFactory;
import com.manywho.sdk.api.run.elements.type.MObject;
import org.junit.Test;
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.util.Map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class FieldMapperTests {
    @Test
    public void testCreateHashFromMobject() throws IOException {
        MObject mObject = ObjectMapperFactory
                .create()
                .readValue(Resources.getResource("field-map/mobject.json"), MObject.class);

        Universe universe = JAXB.unmarshal(Resources.getResource("field-map/universe.xml"), Universe.class);
        Map<String, Object> map = FieldMapper.createMapFromModelMobject(mObject, universe);

        assertThat(map.get("accountnest2"), notNullValue());
        Map<String, Object> accountnest2 = (Map<String, Object>) map.get("accountnest2");
        assertThat(accountnest2.get("single_value").toString(), equalTo("single value 2-1"));
        Map<String, Object> billing_address_collection_name = (Map<String, Object>) accountnest2.get("billing_address_collection_name");

        assertThat(accountnest2.get("billing_address_collection_name"), notNullValue());

    }
}
