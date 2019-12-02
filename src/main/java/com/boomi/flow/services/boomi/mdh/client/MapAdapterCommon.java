package com.boomi.flow.services.boomi.mdh.client;

import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

public class MapAdapterCommon {
    public static List<Property> createPropertiesModel(NodeList map) {
        ArrayList<Property> properties = new ArrayList<>();

        for (int i = 0; i < map.getLength(); i++) {
            Node childNode = map.item(i);

            if (childNode.getNodeType() == ELEMENT_NODE && childNode.getChildNodes() != null) {
                if (childNode.getChildNodes().getLength() == 1 &&
                        childNode.getFirstChild().getNodeType() == TEXT_NODE ) {

                    //this is a leave with element text information
                    properties.add(new Property(childNode.getNodeName(), childNode.getFirstChild().getNodeValue()));

                } else if (childNode.getChildNodes().getLength() > 0 &&
                        childNode.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {

                    if (childNode.getFirstChild().getFirstChild() != null &&
                            childNode.getFirstChild().getFirstChild().getNodeType() == ELEMENT_NODE) {

                        // this is a collection of repeatable field groups
                        properties.add(new Property(childNode.getNodeName(), createListMobject(childNode.getChildNodes())));
                    } else {
                        // this is a field group
                        properties.add(new Property(childNode.getNodeName(), createMobject(childNode)));
                    }
                }
            }
        }

        if(properties.isEmpty()) {
            return  null;
        }

        return properties;
    }

    private static List<MObject> createListMobject(NodeList nodes) {
        List<MObject> objects = new ArrayList<>();
        for(int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == ELEMENT_NODE) {
                objects.add(createMobject(nodes.item(i)));
            }
        }

        return objects;
    }

    private static MObject createMobject(Node childNode) {
        MObject object = new MObject(childNode.getNodeName() + "-child", UUID.randomUUID().toString(), createPropertiesModel(childNode.getChildNodes()));
        object.setTypeElementBindingDeveloperName(childNode.getNodeName() + "-child");

        return object;
    }
}
