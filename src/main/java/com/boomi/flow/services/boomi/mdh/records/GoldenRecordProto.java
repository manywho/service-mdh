package com.boomi.flow.services.boomi.mdh.records;

import com.boomi.flow.services.boomi.mdh.client.XmlMapAdapterProto;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.migesok.jaxb.adapter.javatime.OffsetDateTimeXmlAdapter;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class GoldenRecordProto {
    private OffsetDateTime createdDate;
    private OffsetDateTime updatedDate;
    private String recordId;
    private MObject fields;
    private List<Link> links = new ArrayList<>();

    @XmlAttribute
    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public GoldenRecordProto setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    public OffsetDateTime getUpdatedDate() {
        return updatedDate;
    }

    public GoldenRecordProto setUpdatedDate(OffsetDateTime updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    @XmlAttribute
    public String getRecordId() {
        return recordId;
    }

    public GoldenRecordProto setRecordId(String recordId) {
        this.recordId = recordId;
        return this;
    }

    @XmlElement(name = "Fields")
    @XmlJavaTypeAdapter(XmlMapAdapterProto.class)
    public MObject getMObject() {
        return fields;
    }

    public GoldenRecordProto setMObject(MObject fields) {
        this.fields = fields;
        return this;
    }

    @XmlElementWrapper
    @XmlElement(name = "link")
    public List<GoldenRecordProto.Link> getLinks() {
        return links;
    }

    public GoldenRecordProto setLinks(List<Link> links) {
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
