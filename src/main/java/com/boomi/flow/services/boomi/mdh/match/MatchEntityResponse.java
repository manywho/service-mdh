package com.boomi.flow.services.boomi.mdh.match;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "MatchEntitiesResponse")
public class MatchEntityResponse {
    private List<MatchResult> matchResults;
    private String matchRule;
    private String status;

    public static class MatchResult {
        public Map<String, Map<String, Object>> getEntity() {
            return entity;
        }

        public MatchResult setEntity(Map<String, Map<String, Object>> entity) {
            this.entity = entity;
            return this;
        }

        @XmlElement(name = "entity")
        private Map<String, Map<String, Object>> entity;

        @XmlElement(name = "entity")
        private Map<String, Map<String, Object>> match;

        @XmlElement(name = "entity")
        private Map<String, Map<String, Object>> duplicate;
    }
}
