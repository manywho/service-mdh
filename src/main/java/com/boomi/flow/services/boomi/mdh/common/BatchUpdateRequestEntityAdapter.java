package com.boomi.flow.services.boomi.mdh.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BatchUpdateRequestEntityAdapter extends XmlAdapter<Object, BatchUpdateRequest.Entity> {
    private final DocumentBuilder documentBuilder;

    public BatchUpdateRequestEntityAdapter() {
        try {
            this.documentBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object marshal(BatchUpdateRequest.Entity entity) {
        Document document = documentBuilder.newDocument();

        Element element = document.createElement(entity.getName());

        if (entity.getOp() != null) {
            element.setAttribute("op", entity.getOp());
        }

        for (Map.Entry<String, Object> entry : entity.getFields().entrySet()) {
            Element entryElement = document.createElement(entry.getKey());

            element.appendChild(createElement(document, entryElement, entry.getValue()));
        }

        return element;
    }

    /**
     * This is a very hardcoded method that creates DOM elements from a given scalar, map or list.
     *
     * @param document
     * @param element
     * @param value
     * @return
     */
    private Element createElement(Document document, Element element, Object value) {
        if (value instanceof Map || value instanceof List) {
            Collection<Map.Entry<String, Object>> nestedEntities;

            if (value instanceof Map) {
                nestedEntities = ((Map<String, Object>) value).entrySet();
            } else {
                nestedEntities = (List<Map.Entry<String, Object>>) value;
            }

            for (Object nestedEntity : nestedEntities) {
                element.appendChild(createFromMapOrEntry(document, nestedEntity));
            }
        }
        else {
            element.setTextContent(String.valueOf(value));
        }

        return element;
    }

    private Element createFromMapOrEntry(Document document, Object mapOrEntry) {
        if (mapOrEntry instanceof Map.Entry) {
            Map.Entry<String, Object> entity = (Map.Entry<String, Object>) mapOrEntry;
            Element nestedEntryElement = document.createElement(entity.getKey());

            return createElement(document, nestedEntryElement, entity.getValue());

        } else {
            Map<String, Object> mapEntity = (Map<String, Object>) mapOrEntry;
            Element nestedEntryElement = document.createElement(mapEntity.entrySet().iterator().next().getKey());

            return createElement(document, nestedEntryElement, mapEntity.entrySet().iterator().next().getValue());
        }
    }

    @Override
    public BatchUpdateRequest.Entity unmarshal(Object wrapper) {
        throw new RuntimeException("Unmarshalling maps isn't supported in this class");
    }
}

