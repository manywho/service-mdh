package com.boomi.flow.services.boomi.mdh.quarantine;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class QuarantineQueryResponse {
    @XmlAttribute
    private Integer resultCount;

    @XmlAttribute
    private Integer totalCount;

    @XmlAttribute
    private String offsetToken;

    @XmlElement(name = "QuarantineEntry")
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
