package com.boomi.flow.services.boomi.mdh.quarantine;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.OffsetDateTime;
import java.util.Map;

public class QuarantineEntry {
    @JacksonXmlProperty(isAttribute = true)
    private OffsetDateTime createdDate;

    @JacksonXmlProperty(isAttribute = true)
    private OffsetDateTime endDate;

    @JacksonXmlProperty(isAttribute = true)
    private String sourceId;

    @JacksonXmlProperty(isAttribute = true)
    private String sourceEntityId;

    @JacksonXmlProperty(isAttribute = true)
    private String transactionId;

    private String cause;
    private String reason;
    private String resolution;
    private Map<String, Map<String, Object>> entity;

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public QuarantineEntry setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public QuarantineEntry setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getSourceId() {
        return sourceId;
    }

    public QuarantineEntry setSourceId(String sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    public String getSourceEntityId() {
        return sourceEntityId;
    }

    public QuarantineEntry setSourceEntityId(String sourceEntityId) {
        this.sourceEntityId = sourceEntityId;
        return this;
    }

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

    public Map<String, Map<String, Object>> getEntity() {
        return entity;
    }

    public QuarantineEntry setEntity(Map<String, Map<String, Object>> entity) {
        this.entity = entity;
        return this;
    }
}
