package com.boomi.flow.services.boomi.mdh.records;

import com.boomi.flow.services.boomi.mdh.client.XmlMapAdapter;
import com.migesok.jaxb.adapter.javatime.OffsetDateTimeXmlAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoldenRecord {
    private OffsetDateTime createdDate;
    private OffsetDateTime updatedDate;
    private String recordId;
    private Map<String, Map<String, Object>> fields = new HashMap<>();
    private List<Link> links = new ArrayList<>();

    @XmlAttribute
    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public GoldenRecord setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    public OffsetDateTime getUpdatedDate() {
        return updatedDate;
    }

    public GoldenRecord setUpdatedDate(OffsetDateTime updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    @XmlAttribute
    public String getRecordId() {
        return recordId;
    }

    public GoldenRecord setRecordId(String recordId) {
        this.recordId = recordId;
        return this;
    }

    @XmlElement(name = "Fields")
    @XmlJavaTypeAdapter(XmlMapAdapter.class)
    public Map<String, Map<String, Object>> getFields() {
        return fields;
    }

    public GoldenRecord setFields(Map<String, Map<String, Object>> fields) {
        this.fields = fields;
        return this;
    }
    @XmlElementWrapper
    @XmlElement(name = "link")
    public List<Link> getLinks() {
        return links;
    }

    public GoldenRecord setLinks(List<Link> links) {
        this.links = links;
        return this;
    }

    public static class Link {
        private String source;
        private String entityId;
        private OffsetDateTime establishedDate;

        @XmlAttribute(name = "establishedDate")
        @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
        public OffsetDateTime getEstablishedDate() {
            return establishedDate;
        }

        @XmlAttribute(name = "source")
        public String getSource() {
            return source;
        }

        @XmlAttribute(name = "entityId")
        public String getEntityId() {
            return entityId;
        }

        public Link setSource(String source) {
            this.source = source;
            return this;
        }

        public Link setEntityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public Link setEstablishedDate(OffsetDateTime establishedDate) {
            this.establishedDate = establishedDate;
            return this;
        }
    }
}
