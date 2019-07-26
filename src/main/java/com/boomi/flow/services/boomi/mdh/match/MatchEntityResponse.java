package com.boomi.flow.services.boomi.mdh.match;

import com.boomi.flow.services.boomi.mdh.client.XmlMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "MatchEntitiesResponse")
public class MatchEntityResponse {
    private List<MatchResult> matchResults = new ArrayList<>();

    @XmlElement(name = "MatchResult")
    public List<MatchResult> getMatchResults() {
        return matchResults;
    }

    public void setMatchResults(List<MatchResult> matchResults) {
        this.matchResults = matchResults;
    }

    public static class MatchResult {
        private String status;
        private String matchRule;
        private Map<String, Map<String, Object>> entity = new HashMap<>();
        private List<Map<String, Object>> match = new ArrayList<>();
        private List<Map<String, Object>> duplicate = new ArrayList<>();

        @XmlAttribute
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @XmlAttribute (name = "matchRule")
        public String getMatchRule() {
            return matchRule;
        }

        public void setMatchRule(String matchRule) {
            this.matchRule = matchRule;
        }

        @XmlElement(name = "entity")
        @XmlJavaTypeAdapter(XmlMapAdapter.class)
        public Map<String, Map<String, Object>> getEntity() {
            return entity;
        }

        public void setEntity(Map<String, Map<String, Object>> entity) {
            this.entity = entity;
        }

        @XmlElement(name = "match")
        @XmlJavaTypeAdapter(XmlMapAdapter.class)
        public List<Map<String, Object>> getMatch() {
            return match;
        }

        public void setMatch(List<Map<String, Object>> match) {
            this.match = match;
        }

        @XmlElement(name = "duplicate")
        @XmlJavaTypeAdapter(XmlMapAdapter.class)
        public List<Map<String, Object>> getDuplicate() {
            return duplicate;
        }

        public void setDuplicate(List<Map<String, Object>> duplicate) {
            this.duplicate = duplicate;
        }
    }
}
