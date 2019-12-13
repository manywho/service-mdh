package com.boomi.flow.services.boomi.mdh.unitest;

import com.boomi.flow.services.boomi.mdh.common.EngineCompatibleDates;
import com.manywho.sdk.api.run.elements.type.Property;
import org.junit.Assert;
import org.junit.Test;

import java.time.OffsetDateTime;

public class EngineCompatibleDatesTest {
    @Test
    public void testCompatibleDateEngine() {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2019-11-27T00:00:00Z");

        // without compatible parser
        Property property1 = new Property("", offsetDateTime);
        Assert.assertNotEquals("2019-11-27T00:00:00Z", property1.getContentValue());

        // with compatible parser
        Property property2 = new Property("", EngineCompatibleDates.format(offsetDateTime));
        Assert.assertEquals("2019-11-27T00:00:00Z", property2.getContentValue());
    }
}
