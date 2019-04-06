package com.boomi.flow.services.boomi.mdh.quarantine;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class QuarantineQueryResponse {
    private Integer resultCount;
    private Integer totalCount;
    private String offsetToken;
    private List<QuarantineEntry> entries;

    @XmlAttribute
    public Integer getResultCount() {
        return resultCount;
    }

    public QuarantineQueryResponse setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
        return this;
    }

    @XmlAttribute
    public Integer getTotalCount() {
        return totalCount;
    }

    public QuarantineQueryResponse setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    @XmlAttribute
    public String getOffsetToken() {
        return offsetToken;
    }

    public QuarantineQueryResponse setOffsetToken(String offsetToken) {
        this.offsetToken = offsetToken;
        return this;
    }

    @XmlElement(name = "QuarantineEntry")
    public List<QuarantineEntry> getEntries() {
        return entries;
    }

    public QuarantineQueryResponse setEntries(List<QuarantineEntry> entries) {
        this.entries = entries;
        return this;
    }
}
