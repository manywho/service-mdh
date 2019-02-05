package com.boomi.flow.services.boomi.mdh.quarantine;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class QuarantineQueryResponse {
    @JacksonXmlProperty(isAttribute = true)
    private Integer resultCount;

    @JacksonXmlProperty(isAttribute = true)
    private Integer totalCount;

    @JacksonXmlProperty(isAttribute = true)
    private String offsetToken;

    @JacksonXmlProperty(localName = "QuarantineEntry")
    private List<QuarantineEntry> entries;

    public Integer getResultCount() {
        return resultCount;
    }

    public QuarantineQueryResponse setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
        return this;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public QuarantineQueryResponse setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public String getOffsetToken() {
        return offsetToken;
    }

    public QuarantineQueryResponse setOffsetToken(String offsetToken) {
        this.offsetToken = offsetToken;
        return this;
    }

    public List<QuarantineEntry> getEntries() {
        return entries;
    }

    public QuarantineQueryResponse setEntries(List<QuarantineEntry> entries) {
        this.entries = entries;
        return this;
    }
}
