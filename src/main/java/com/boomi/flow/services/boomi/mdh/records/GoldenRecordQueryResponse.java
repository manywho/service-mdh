package com.boomi.flow.services.boomi.mdh.records;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class GoldenRecordQueryResponse {
    private Integer resultCount;
    private Integer totalCount;
    private String offsetToken;
    private List<GoldenRecord> records;

    @XmlAttribute
    public Integer getResultCount() {
        return resultCount;
    }

    public GoldenRecordQueryResponse setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
        return this;
    }

    @XmlAttribute
    public Integer getTotalCount() {
        return totalCount;
    }

    public GoldenRecordQueryResponse setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    @XmlAttribute
    public String getOffsetToken() {
        return offsetToken;
    }

    public GoldenRecordQueryResponse setOffsetToken(String offsetToken) {
        this.offsetToken = offsetToken;
        return this;
    }

    @XmlElement(name = "Record")
    public List<GoldenRecord> getRecords() {
        return records;
    }

    public GoldenRecordQueryResponse setRecords(List<GoldenRecord> records) {
        this.records = records;
        return this;
    }
}
