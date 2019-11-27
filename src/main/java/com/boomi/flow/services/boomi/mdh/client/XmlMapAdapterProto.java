package com.boomi.flow.services.boomi.mdh.client;

import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

public class XmlMapAdapterProto extends XmlAdapter<XmlMapWrapper, MObject> {

    @Override
    public MObject unmarshal(XmlMapWrapper wrapper) throws Exception {
        Element element = getElementFromWrapper(wrapper);
        String name = element.getNodeName();
        String id = "default-id";
        List<Property> properties = createPropertiesModel(getElementFromWrapper(wrapper).getChildNodes());
        properties.add(new Property(GoldenRecordConstants.RECORD_ID_FIELD, id));

        return new MObject( "default-golden-record", id, properties);
    }

    @Override
    public XmlMapWrapper marshal(MObject stringMultimapMap) throws Exception {
        throw new RuntimeException("Marshalling maps isn't supported yet");
    }

    private static List<Property> createPropertiesModel(NodeList map) {
        ArrayList<Property> properties = new ArrayList<>();

        for (int i = 0; i < map.getLength(); i++) {
            Node childNode = map.item(i);

            if (childNode.getNodeType() == ELEMENT_NODE &&
                    childNode.getChildNodes() != null &&
                    childNode.getChildNodes().getLength() == 1 &&
                    childNode.getFirstChild().getNodeType() == TEXT_NODE ) {

                //this is a leave with element text information
                properties.add(new Property(childNode.getNodeName(), childNode.getFirstChild().getNodeValue()));
            } else if (childNode.getNodeType() == ELEMENT_NODE) {

                // this is a node of nodes
                MObject object = new MObject(childNode.getNodeName() + "-child", createPropertiesModel(childNode.getChildNodes()));
                object.setTypeElementBindingDeveloperName(childNode.getNodeValue() + "-child");
                object.setExternalId(UUID.randomUUID().toString());
                properties.add(new Property(childNode.getNodeName(), Collections.singletonList(object)));
            }
        }

        return properties;
    }

    private Element getElementFromWrapper(XmlMapWrapper wrapper) {
        if(wrapper.elements.size() == 1) {
            Object fieldsObject = wrapper.elements.get(0);
            if (fieldsObject instanceof Element) {

                return (Element) fieldsObject;
            } else {
                throw new RuntimeException("Fields is not an Element Instance");
            }
        } else {
            throw new RuntimeException("Fields should have one entity");
        }
    }
}
