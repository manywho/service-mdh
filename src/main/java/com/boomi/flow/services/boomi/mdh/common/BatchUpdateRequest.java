package com.boomi.flow.services.boomi.mdh.common;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@XmlRootElement(name = "batch")
public class BatchUpdateRequest {
    private String source;
    private List<Entity> entities;

    @XmlAttribute(name = "src")
    public String getSource() {
        return source;
    }

    public BatchUpdateRequest setSource(String source) {
        this.source = source;
        return this;
    }

    @XmlAnyElement()
    public List<Entity> getEntities() {
        return entities;
    }

    public BatchUpdateRequest setEntities(List<Entity> entities) {
        this.entities = entities;
        return this;
    }

    @XmlJavaTypeAdapter(BatchUpdateRequestEntityAdapter.class)
    public static class Entity {
        private String op;
        private String name;
        private Map<String, Object> fields;

        @XmlAttribute
        public String getOp() {
            return op;
        }

        public Entity setOp(String op) {
            this.op = op;
            return this;
        }

        public String getName() {
            return name;
        }

        public Entity setName(String name) {
            this.name = name;
            return this;
        }

        public Map<String, Object> getFields() {
            return fields;
        }

        public Entity setFields(Map<String, Object> fields) {
            this.fields = fields;
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

            Entity entity = (Entity) o;

            return Objects.equals(op, entity.op) &&
                    Objects.equals(name, entity.name) &&
                    Objects.equals(fields, entity.fields);
        }

        @Override
        public int hashCode() {
            return Objects.hash(op, name, fields);
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

        BatchUpdateRequest that = (BatchUpdateRequest) o;

        return Objects.equals(source, that.source) &&
                Objects.equals(entities, that.entities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, entities);
    }
}
