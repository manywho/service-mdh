package com.boomi.flow.services.boomi.mdh.client;

import com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetailsConstants;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import org.w3c.dom.Element;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XmlMapAdapter extends XmlAdapter<XmlMapWrapper, MObject> {

    @Override
    public MObject unmarshal(XmlMapWrapper wrapper) throws Exception {
        if (wrapper == null || wrapper.elements == null || wrapper.elements.isEmpty()) {
            return null;
        }

        return new MObject(firstNodeName(wrapper), "", getProperties(wrapper));
    }

    @Override
    public XmlMapWrapper marshal(MObject stringMultimapMap) throws Exception {
        throw new RuntimeException("Marshalling maps isn't supported yet");
    }

    private String firstNodeName(XmlMapWrapper wrapper) {
        for (Element element : wrapper.elements) {
            return element.getNodeName();
        }

        return "";
    }

    private List<Property> getProperties(XmlMapWrapper wrapper) {
        List<Property> properties = new ArrayList<>();

        if(wrapper.elements.size() > 0) {
            Element fieldsEntity = wrapper.elements.get(0);
            properties =  MapAdapterCommon.createPropertiesModel(fieldsEntity, fieldsEntity.getChildNodes());
        }

        if (wrapper.elements.size() == 2) {
            // specific for FuzzyMatchDetails
            Element fieldsFuzzyDetails = wrapper.elements.get(1);
            List<Property> fuzzyProperties = MapAdapterCommon.createPropertiesModel(wrapper.elements.get(1), fieldsFuzzyDetails.getChildNodes());

            if (fuzzyProperties.size() ==  6) {
                fuzzyProperties.stream()
                        .filter(property -> property.getDeveloperName().equals("matchStrength"))
                        .findFirst()
                        .ifPresent(property -> property.setDeveloperName("Match Strength"));

                MObject fuzzyMatchDetailsObject = new MObject(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, fuzzyProperties);
                fuzzyMatchDetailsObject.setExternalId(UUID.randomUUID().toString());
                properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, fuzzyMatchDetailsObject));
            } else {
                Element element = wrapper.elements.get(0);
                String modelName = element.getNodeName();

                throw new RuntimeException("The model " + modelName + " in your MDH repository cannot have a property called fuzzyMatchDetails, as it is a reserved property name");
            }
        }

        return properties;
    }
}
