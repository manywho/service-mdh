package com.boomi.flow.services.boomi.mdh.records;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class GoldenRecordQueryResponseProto {
    private int resultCount;
    private int totalCount;
    private String offsetToken;
    private List<GoldenRecordProto> records;

    @XmlAttribute
    public Integer getResultCount() {
        return resultCount;
    }

    public GoldenRecordQueryResponseProto setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
        return this;
    }

    @XmlAttribute
    public Integer getTotalCount() {
        return totalCount;
    }

    public GoldenRecordQueryResponseProto setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    @XmlAttribute
    public String getOffsetToken() {
        return offsetToken;
    }

    public GoldenRecordQueryResponseProto setOffsetToken(String offsetToken) {
        this.offsetToken = offsetToken;
        return this;
    }

    @XmlElement(name = "Record")
    public List<GoldenRecordProto> getRecords() {
        return records;
    }

    public GoldenRecordQueryResponseProto setRecords(List<GoldenRecordProto> records) {
        this.records = records;
        return this;
    }
}
