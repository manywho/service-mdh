package com.boomi.flow.services.boomi.mdh.records;

import com.migesok.jaxb.adapter.javatime.OffsetDateTimeXmlAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.OffsetDateTime;
import java.util.Map;

@XmlJavaTypeAdapter(GoldenRecordHistoryAdapter.class)
public class GoldenRecordHistory {
    private OffsetDateTime endDate;
    private String grid;
    private String source;
    private String endDateSource;
    private OffsetDateTime startDate;
    private long version;
    private String transactionId;
    private Map<String, Object> fields;

    @XmlElement(name = "enddate")
    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public GoldenRecordHistory setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    @XmlElement(name = "grid")
    public String getGrid() {
        return grid;
    }

    public GoldenRecordHistory setGrid(String grid) {
        this.grid = grid;
        return this;
    }

    @XmlElement(name = "source")
    public String getSource() {
        return source;
    }

    public GoldenRecordHistory setSource(String source) {
        this.source = source;
        return this;
    }

    @XmlElement(name = "enddatesource")
    public String getEndDateSource() {
        return endDateSource;
    }

    public GoldenRecordHistory setEndDateSource(String endDateSource) {
        this.endDateSource = endDateSource;
        return this;
    }

    @XmlElement(name = "startdate")
    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public GoldenRecordHistory setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    @XmlElement(name = "version")
    public long getVersion() {
        return version;
    }

    public GoldenRecordHistory setVersion(long version) {
        this.version = version;
        return this;
    }

    @XmlElement(name = "transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    public GoldenRecordHistory setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public GoldenRecordHistory setFields(Map<String, Object> fields) {
        this.fields = fields;
        return this;
    }
}
