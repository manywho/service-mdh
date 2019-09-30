package com.boomi.flow.services.boomi.mdh.client;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class XmlMapAdapter extends XmlAdapter<XmlMapWrapper, Map<String, Map<String, Object>>> {

    @Override
    public XmlMapWrapper marshal(Map<String, Map<String, Object>> m) throws Exception {
        throw new RuntimeException("Marshalling maps isn't supported yet");
    }

    @Override
    public Map<String, Map<String, Object>> unmarshal(XmlMapWrapper wrapper) throws Exception {
        Map<String, Map<String, Object>> map = new HashMap<>();

        if (wrapper == null || wrapper.elements == null || wrapper.elements.isEmpty()) {
            return map;
        }

        for (Object object : wrapper.elements) {
            Element element = (Element) object;

            if (element.hasChildNodes()) {
                map.put(element.getNodeName(), createChildNodes(element.getChildNodes()));
            }
        }

        return map;
    }

    private static Map<String, Object> createChildNodes(NodeList elements) {
        Map<String, Object> childMap = new HashMap<>();

        for (int i = 0; i < elements.getLength(); i++) {
            Node childNode = elements.item(i);

            if (childNode.hasChildNodes() == false) {
                continue;
            }

            if (childNode.getFirstChild().getNodeType() != 1) {
                childMap.put(childNode.getNodeName(), childNode.getFirstChild().getNodeValue());
            } else {
                // it is a list
                childMap.put(childNode.getNodeName(), createChildNodes(childNode.getChildNodes()));
            }
        }

        return childMap;
    }
}
