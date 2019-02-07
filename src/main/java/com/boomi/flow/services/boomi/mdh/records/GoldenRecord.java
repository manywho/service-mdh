package com.boomi.flow.services.boomi.mdh.records;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.OffsetDateTime;
import java.util.Map;

public class GoldenRecord {
    @JacksonXmlProperty(isAttribute = true)
    private OffsetDateTime createdDate;

    @JacksonXmlProperty(isAttribute = true)
    private OffsetDateTime updatedDate;

    @JacksonXmlProperty(isAttribute = true)
    private String recordId;

    @JacksonXmlProperty(localName = "Fields")
    private Map<String, Map<String, Object>> fields;

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public GoldenRecord setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public OffsetDateTime getUpdatedDate() {
        return updatedDate;
    }

    public GoldenRecord setUpdatedDate(OffsetDateTime updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    public String getRecordId() {
        return recordId;
    }

    public GoldenRecord setRecordId(String recordId) {
        this.recordId = recordId;
        return this;
    }

    public Map<String, Map<String, Object>> getFields() {
        return fields;
    }

    public GoldenRecord setFields(Map<String, Map<String, Object>> fields) {
        this.fields = fields;
        return this;
    }
}
