package com.boomi.flow.services.boomi.mdh.records;

import com.migesok.jaxb.adapter.javatime.OffsetDateTimeXmlAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "RecordQueryRequest")
public class GoldenRecordQueryRequest {
    private View view;
    private Sort sort;
    private Filter filter;

    public View getView() {
        return view;
    }

    public GoldenRecordQueryRequest setView(View view) {
        this.view = view;
        return this;
    }

    public Sort getSort() {
        return sort;
    }

    public GoldenRecordQueryRequest setSort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public Filter getFilter() {
        return filter;
    }

    public GoldenRecordQueryRequest setFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    public static class View {
        private List<String> fields = new ArrayList<>();

        @XmlElement(name = "fieldId")
        public List<String> getFields() {
            return fields;
        }
    }

    public static class Sort {
        private List<Field> fields = new ArrayList<>();

        @XmlElement(name = "sortField")
        public List<Field> getFields() {
            return fields;
        }

        public Sort setFields(List<Field> fields) {
            this.fields = fields;
            return this;
        }

        public static class Field {
            private String fieldId;
            private String direction;

            public String getFieldId() {
                return fieldId;
            }

            public Field setFieldId(String fieldId) {
                this.fieldId = fieldId;
                return this;
            }

            public String getDirection() {
                return direction;
            }

            public Field setDirection(String direction) {
                this.direction = direction;
                return this;
            }
        }
    }

    public static class Filter {
        private String creatingSourceId;
        private DateFilter createdDate;
        private DateFilter updatedDate;
        private List<FieldValue> fieldValues = new ArrayList<>();

        public String getCreatingSourceId() {
            return creatingSourceId;
        }

        public Filter setCreatingSourceId(String creatingSourceId) {
            this.creatingSourceId = creatingSourceId;
            return this;
        }

        public DateFilter getCreatedDate() {
            return createdDate;
        }

        public Filter setCreatedDate(DateFilter createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public DateFilter getUpdatedDate() {
            return updatedDate;
        }

        public Filter setUpdatedDate(DateFilter updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        @XmlElement(name = "fieldValue")
        public List<FieldValue> getFieldValues() {
            return fieldValues;
        }

        public Filter setFieldValues(List<FieldValue> fieldValues) {
            this.fieldValues = fieldValues;
            return this;
        }

        public static class FieldValue {
            private String fieldId;
            private String operator;
            private String value;

            public String getFieldId() {
                return fieldId;
            }

            public FieldValue setFieldId(String fieldId) {
                this.fieldId = fieldId;
                return this;
            }

            public String getOperator() {
                return operator;
            }

            public FieldValue setOperator(String operator) {
                this.operator = operator;
                return this;
            }

            public String getValue() {
                return value;
            }

            public FieldValue setValue(String value) {
                this.value = value;
                return this;
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
        }
    }
}
