package com.boomi.flow.services.boomi.mdh.quarantine;

import com.boomi.flow.services.boomi.mdh.client.XmlFullDateAdapter;
import com.boomi.flow.services.boomi.mdh.client.XmlMapAdapter;
import com.manywho.sdk.api.run.elements.type.MObject;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class QuarantineEntry {
    private String createdDate;
    private String endDate;
    private String sourceId;
    private String sourceEntityId;
    private String transactionId;
    private String cause;
    private String reason;
    private String resolution;
    private MObject entity;

    @XmlAttribute
    @XmlJavaTypeAdapter(XmlFullDateAdapter.class)
    public String getCreatedDate() {
        return createdDate;
    }

    public QuarantineEntry setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(XmlFullDateAdapter.class)
    public String getEndDate() {
        return endDate;
    }

    public QuarantineEntry setEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    @XmlAttribute
    public String getSourceId() {
        return sourceId;
    }

    public QuarantineEntry setSourceId(String sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    @XmlAttribute
    public String getSourceEntityId() {
        return sourceEntityId;
    }

    public QuarantineEntry setSourceEntityId(String sourceEntityId) {
        this.sourceEntityId = sourceEntityId;
        return this;
    }

    @XmlAttribute
    public String getTransactionId() {
        return transactionId;
    }

    public QuarantineEntry setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getCause() {
        return cause;
    }

    public QuarantineEntry setCause(String cause) {
        this.cause = cause;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public QuarantineEntry setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getResolution() {
        return resolution;
    }

    public QuarantineEntry setResolution(String resolution) {
        this.resolution = resolution;
        return this;
    }

    @XmlJavaTypeAdapter(XmlMapAdapter.class)
    public MObject getEntity() {
        return entity;
    }

    public QuarantineEntry setEntity(MObject entity) {
        this.entity = entity;
        return this;
    }
}
