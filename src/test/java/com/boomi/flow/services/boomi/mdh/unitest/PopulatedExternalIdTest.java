package com.boomi.flow.services.boomi.mdh.unitest;

import com.boomi.flow.services.boomi.mdh.common.Entities;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PopulatedExternalIdTest {
    @Test
    public void setRandomUniqueIdIfExternalIdEmpty() {
        List<Property> propertyList = new ArrayList<>();
        propertyList.add(new Property("id", ""));

        MObject object = new MObject("test", null, propertyList);
        Entities.setRandomUniqueIdIfEmpty(object, "id", true);

        Assert.assertNotNull(object.getExternalId());
        Assert.assertNotNull(object.getProperties().get(0).getContentValue());
    }

    @Test
    public void notToSetRandomUniqueIdIfExternalIdNotEmpty() {
        List<Property> propertyList = new ArrayList<>();
        propertyList.add(new Property("id", "1234"));

        MObject object = new MObject("test", "1", propertyList);
        Entities.setRandomUniqueIdIfEmpty(object, "id", true);

        Assert.assertEquals("1", object.getExternalId());
        Assert.assertEquals("1234", object.getProperties().get(0).getContentValue());
    }

    @Test
    public void notToSetRandomIdIfExternalIdIsNotEmpty() {
        List<Property> propertyList = new ArrayList<>();
        propertyList.add(new Property("id", (String) null));

        MObject object = new MObject("test", "123", propertyList);
        Entities.setRandomUniqueIdIfEmpty(object, "id", true);

        Assert.assertEquals("123", object.getExternalId());
        Assert.assertNull(object.getProperties().get(0).getContentValue());
    }

    @Test
    public void setRandomUniqueIdForChildObjects() {
        List<Property> propertyList = new ArrayList<>();
        propertyList.add(new Property("id", "123"));
        List<Property> childProperty = new ArrayList<>();

        propertyList.add(new Property("other", new MObject("child",null, childProperty)));

        MObject object = new MObject("test", "123", propertyList);
        Entities.setRandomUniqueIdIfEmpty(object, "id", true);

        Assert.assertEquals("123", object.getExternalId());
        Assert.assertNotNull(object.getProperties().get(0).getContentValue());
        Assert.assertNotNull(object.getProperties().get(1).getObjectData().get(0).getExternalId());
    }
}
