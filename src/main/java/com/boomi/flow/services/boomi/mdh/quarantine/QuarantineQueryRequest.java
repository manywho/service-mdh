package com.boomi.flow.services.boomi.mdh.quarantine;

import com.migesok.jaxb.adapter.javatime.OffsetDateTimeXmlAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "QuarantineQueryRequest")
public class QuarantineQueryRequest {
    private Filter filter;
    private boolean includeData;
    private String type;

    public Filter getFilter() {
        return filter;
    }

    public QuarantineQueryRequest setFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    @XmlAttribute
    public boolean isIncludeData() {
        return includeData;
    }

    public QuarantineQueryRequest setIncludeData(boolean includeData) {
        this.includeData = includeData;
        return this;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public QuarantineQueryRequest setType(String type) {
        this.type = type;
        return this;
    }

    public static class Filter {
        private String sourceId;
        private String sourceEntityId;
        private DateFilter createdDate;
        private DateFilter endDate;
        private List<String> resolutions;
        private List<String> causes;

        public String getSourceId() {
            return sourceId;
        }

        public Filter setSourceId(String sourceId) {
            this.sourceId = sourceId;
            return this;
        }

        public String getSourceEntityId() {
            return sourceEntityId;
        }

        public Filter setSourceEntityId(String sourceEntityId) {
            this.sourceEntityId = sourceEntityId;
            return this;
        }

        public DateFilter getCreatedDate() {
            return createdDate;
        }

        public Filter setCreatedDate(DateFilter createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public DateFilter getEndDate() {
            return endDate;
        }

        public Filter setEndDate(DateFilter endDate) {
            this.endDate = endDate;
            return this;
        }

        @XmlElement(name = "resolution")
        public List<String> getResolutions() {
            return resolutions;
        }

        public Filter setResolutions(List<String> resolutions) {
            this.resolutions = resolutions;
            return this;
        }

        @XmlElement(name = "cause")
        public List<String> getCauses() {
            return causes;
        }

        public Filter setCauses(List<String> causes) {
            this.causes = causes;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Filter filter = (Filter) o;

            return Objects.equals(sourceId, filter.sourceId) &&
                    Objects.equals(sourceEntityId, filter.sourceEntityId) &&
                    Objects.equals(createdDate, filter.createdDate) &&
                    Objects.equals(endDate, filter.endDate) &&
                    Objects.equals(resolutions, filter.resolutions) &&
                    Objects.equals(causes, filter.causes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceId, sourceEntityId, createdDate, endDate, resolutions, causes);
        }
    }

    public static class DateFilter {
        private OffsetDateTime from;
        private OffsetDateTime to;

        @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
        public OffsetDateTime getFrom() {
            return from;
        }

        public DateFilter setFrom(OffsetDateTime from) {
            this.from = from;
            return this;
        }

        @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
        public OffsetDateTime getTo() {
            return to;
        }

        public DateFilter setTo(OffsetDateTime to) {
            this.to = to;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            DateFilter that = (DateFilter) o;

            return Objects.equals(from, that.from) &&
                    Objects.equals(to, that.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QuarantineQueryRequest that = (QuarantineQueryRequest) o;

        return includeData == that.includeData &&
                Objects.equals(filter, that.filter) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter, includeData, type);
    }
}
