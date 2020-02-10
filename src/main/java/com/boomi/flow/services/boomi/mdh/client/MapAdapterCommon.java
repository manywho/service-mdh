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
    public static List<Property> createPropertiesModel(Node modelNode, NodeList map) {
        ArrayList<Property> properties = new ArrayList<>();

        for (int i = 0; i < map.getLength(); i++) {
            Node childNode = map.item(i);

            if (childNode.getNodeType() == ELEMENT_NODE && childNode.hasChildNodes()) {
                if (childNode.getChildNodes().getLength() == 1 &&
                        childNode.getFirstChild().getNodeType() == TEXT_NODE ) {

                    //this is a leaf with element text information
                    properties.add(new Property(childNode.getNodeName(), childNode.getFirstChild().getNodeValue()));

                } else if (childNode.getChildNodes().getLength() > 0 &&
                        childNode.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {

                    if (childNode.getFirstChild().hasChildNodes() &&
                            childNode.getFirstChild().getFirstChild().getNodeType() == ELEMENT_NODE) {

                        // this is a collection of repeatable field groups
                        properties.add(new Property(modelNode.getNodeName() + " - " + childNode.getFirstChild().getNodeName(), createListMobject(modelNode, childNode.getChildNodes())));
                    } else {
                        // this is a field group
                        properties.add(new Property(modelNode.getNodeName() + " - " + childNode.getNodeName(), createMobject(modelNode, childNode)));
                    }
                }
            }
        }

        return properties;
    }

    private static List<MObject> createListMobject(Node modelNode, NodeList nodes) {
        List<MObject> objects = new ArrayList<>();
        for(int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == ELEMENT_NODE) {
                objects.add(createMobject(modelNode, nodes.item(i)));
            }
        }

        return objects;
    }

    private static MObject createMobject(Node modelNode, Node childNode) {
        String developerName = modelNode.getNodeName() + " - " + childNode.getNodeName();
        MObject object = new MObject(developerName, UUID.randomUUID().toString(), createPropertiesModel(childNode, childNode.getChildNodes()));
        object.setTypeElementBindingDeveloperName(developerName);

        return object;
    }
}
