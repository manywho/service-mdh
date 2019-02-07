package com.boomi.flow.services.boomi.mdh.records;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "RecordQueryRequest")
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
        @JacksonXmlProperty(localName = "fieldId")
        private List<String> fields = new ArrayList<>();

        public List<String> getFields() {
            return fields;
        }
    }

    public static class Sort {
        @JacksonXmlProperty(localName = "sortField")
        private List<Field> fields = new ArrayList<>();

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

        @JacksonXmlProperty(localName = "fieldValue")
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

            public OffsetDateTime getFrom() {
                return from;
            }

            public DateFilter setFrom(OffsetDateTime from) {
                this.from = from;
                return this;
            }

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
