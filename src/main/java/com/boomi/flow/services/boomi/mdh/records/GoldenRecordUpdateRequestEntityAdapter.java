package com.boomi.flow.services.boomi.mdh.records;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GoldenRecordUpdateRequestEntityAdapter extends XmlAdapter<Object, GoldenRecordUpdateRequest.Entity> {
    private final DocumentBuilder documentBuilder;

    public GoldenRecordUpdateRequestEntityAdapter() {
        try {
            this.documentBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object marshal(GoldenRecordUpdateRequest.Entity entity) {
        Document document = documentBuilder.newDocument();

        Element element = document.createElement("contact");

        if (entity.getOp() != null) {
            element.setAttribute("op", entity.getOp());
        }

        for (var entry : entity.getFields().entrySet()) {
            var entryElement = document.createElement(entry.getKey());

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

            for (var nestedEntity : nestedEntities) {
                var nestedEntryElement = document.createElement(nestedEntity.getKey());

                element.appendChild(createElement(document, nestedEntryElement, nestedEntity.getValue()));
            }
        }
        else {
            element.setTextContent(String.valueOf(value));
        }

        return element;
    }

    @Override
    public GoldenRecordUpdateRequest.Entity unmarshal(Object wrapper) {
        throw new RuntimeException("Unmarshalling maps isn't supported in this class");
    }
}

