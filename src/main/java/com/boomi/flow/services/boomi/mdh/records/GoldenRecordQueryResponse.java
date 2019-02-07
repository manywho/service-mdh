package com.boomi.flow.services.boomi.mdh.records;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class GoldenRecordQueryResponse {
    @JacksonXmlProperty(isAttribute = true)
    private Integer resultCount;

    @JacksonXmlProperty(isAttribute = true)
    private Integer totalCount;

    @JacksonXmlProperty(isAttribute = true)
    private String offsetToken;

    @JacksonXmlProperty(localName = "Record")
    private List<GoldenRecord> records;

    public Integer getResultCount() {
        return resultCount;
    }

    public GoldenRecordQueryResponse setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
        return this;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public GoldenRecordQueryResponse setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public String getOffsetToken() {
        return offsetToken;
    }

    public GoldenRecordQueryResponse setOffsetToken(String offsetToken) {
        this.offsetToken = offsetToken;
        return this;
    }

    public List<GoldenRecord> getRecords() {
        return records;
    }

    public GoldenRecordQueryResponse setRecords(List<GoldenRecord> records) {
        this.records = records;
        return this;
    }
}
