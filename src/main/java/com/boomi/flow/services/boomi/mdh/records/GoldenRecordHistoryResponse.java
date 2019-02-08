package com.boomi.flow.services.boomi.mdh.records;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "RecordHistoryResponse")
public class GoldenRecordHistoryResponse {
    private Integer resultCount;
    private Integer totalCount;
    private String grid;
    private List<GoldenRecordHistory> records;

    @XmlAttribute
    public Integer getResultCount() {
        return resultCount;
    }

    public GoldenRecordHistoryResponse setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
        return this;
    }

    @XmlAttribute
    public Integer getTotalCount() {
        return totalCount;
    }

    public GoldenRecordHistoryResponse setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    @XmlAttribute
    public String getGrid() {
        return grid;
    }

    public GoldenRecordHistoryResponse setGrid(String grid) {
        this.grid = grid;
        return this;
    }

    @XmlAnyElement
    public List<GoldenRecordHistory> getRecords() {
        return records;
    }

    public GoldenRecordHistoryResponse setRecords(List<GoldenRecordHistory> records) {
        this.records = records;
        return this;
    }
}
