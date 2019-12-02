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

    String firstNodeName(XmlMapWrapper wrapper) {
        for (Object object : wrapper.elements) {
            Element element = (Element) object;
            return element.getNodeName();
        }

        return "";
    }

    private List<Property> getProperties(XmlMapWrapper wrapper) {
        List<Property> properties = new ArrayList<>();

        if(wrapper.elements.size() > 0) {
            Object fieldsEntity = wrapper.elements.get(0);
            if (fieldsEntity instanceof Element) {
                properties =  MapAdapterCommon.createPropertiesModel(((Element) fieldsEntity).getChildNodes());
            }
        }

        if (wrapper.elements.size() == 2 && properties != null) {
            // specific for FuzzyMatchDetails
            Object fieldsFuzzyDetails = wrapper.elements.get(1);
            if (fieldsFuzzyDetails instanceof Element) {
                List<Property> fuzzyProperties = MapAdapterCommon.createPropertiesModel(((Element) fieldsFuzzyDetails).getChildNodes());

                if (fuzzyProperties != null && fuzzyProperties.size() ==  6) {
                    MObject fuzzyMatchDetailsObject = new MObject(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, fuzzyProperties);
                    properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, fuzzyMatchDetailsObject));
                } else {
                    throw new RuntimeException("fuzzyMatchDetails is a reserved name");
                }
            }
        }

        return properties;
    }
}
