package com.boomi.flow.services.boomi.mdh.client;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class XmlMapAdapter extends XmlAdapter<XmlMapWrapper, Multimap<String, Multimap<String, Object>>> {

    @Override
    public Multimap<String, Multimap<String, Object>> unmarshal(XmlMapWrapper wrapper) throws Exception {
        Multimap<String, Multimap<String, Object>> map = ArrayListMultimap.create();

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

    @Override
    public XmlMapWrapper marshal(Multimap<String, Multimap<String, Object>> stringMultimapMap) throws Exception {
        throw new RuntimeException("Marshalling maps isn't supported yet");
    }

    private static Multimap<String, Object> createChildNodes(NodeList elements) {
        Multimap<String, Object> childMap = ArrayListMultimap.create();

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
