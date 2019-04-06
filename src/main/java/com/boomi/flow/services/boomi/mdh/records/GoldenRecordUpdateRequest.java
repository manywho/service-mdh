package com.boomi.flow.services.boomi.mdh.records;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "batch")
public class GoldenRecordUpdateRequest {
    private String source;
    private List<Entity> entities;

    @XmlAttribute(name = "src")
    public String getSource() {
        return source;
    }

    public GoldenRecordUpdateRequest setSource(String source) {
        this.source = source;
        return this;
    }

    @XmlAnyElement()
    public List<Entity> getEntities() {
        return entities;
    }

    public GoldenRecordUpdateRequest setEntities(List<Entity> entities) {
        this.entities = entities;
        return this;
    }

    @XmlJavaTypeAdapter(GoldenRecordUpdateRequestEntityAdapter.class)
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
    }
}
