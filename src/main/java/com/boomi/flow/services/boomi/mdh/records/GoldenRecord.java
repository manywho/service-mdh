package com.boomi.flow.services.boomi.mdh.records;

import com.boomi.flow.services.boomi.mdh.client.XmlMapAdapter;
import com.manywho.sdk.api.run.elements.type.MObject;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import com.boomi.flow.services.boomi.mdh.client.XmlFullDateAdapter;

public class GoldenRecord {
    private String createdDate;
    private String updatedDate;
    private String recordId;
    private MObject mObject;
    private List<Link> links = new ArrayList<>();

    @XmlAttribute
    @XmlJavaTypeAdapter(XmlFullDateAdapter.class)
    public String getCreatedDate() {
        return createdDate;
    }

    public GoldenRecord setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(XmlFullDateAdapter.class)
    public String getUpdatedDate() {
        return updatedDate;
    }

    public GoldenRecord setUpdatedDate(String updatedDate) {
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
    public MObject getMObject() {
        return mObject;
    }

    public GoldenRecord setMObject(MObject mObject) {
        this.mObject = mObject;
        return this;
    }

    @XmlElementWrapper
    @XmlElement(name = "link")
    public List<GoldenRecord.Link> getLinks() {
        return links;
    }

    public GoldenRecord setLinks(List<Link> links) {
        this.links = links;
        return this;
    }

    public static class Link {
        private String source;
        private String entityId;
        private String establishedDate;

        @XmlAttribute(name = "establishedDate")
        @XmlJavaTypeAdapter(XmlFullDateAdapter.class)
        public String getEstablishedDate() {
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

        public Link setEstablishedDate(String establishedDate) {
            this.establishedDate = establishedDate;
            return this;
        }
    }
}
