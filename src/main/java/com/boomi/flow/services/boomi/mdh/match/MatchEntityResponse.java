package com.boomi.flow.services.boomi.mdh.match;

import com.boomi.flow.services.boomi.mdh.client.XmlMapAdapter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
        private String idResource;
        private Multimap<String, Object> entity = ArrayListMultimap.create();
        private List<Multimap<String, Object>> match = new ArrayList<>();
        private List<Multimap<String, Object>> duplicate = new ArrayList<>();

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

        public String getIdResource() {
            return idResource;
        }

        public void setIdResource(String idResource) {
            this.idResource = idResource;
        }

        @XmlElement(name = "entity")
        @XmlJavaTypeAdapter(XmlMapAdapter.class)
        public Multimap<String, Object> getEntity() {
            return entity;
        }

        public void setEntity(Multimap<String, Object> entity) {
            this.entity = entity;
        }

        @XmlElement(name = "match")
        @XmlJavaTypeAdapter(XmlMapAdapter.class)
        public List<Multimap<String, Object>> getMatch() {
            return match;
        }

        public void setMatch(List<Multimap<String, Object>> match) {
            this.match = match;
        }

        @XmlElement(name = "duplicate")
        @XmlJavaTypeAdapter(XmlMapAdapter.class)
        public List<Multimap<String, Object>> getDuplicate() {
            return duplicate;
        }

        public void setDuplicate(List<Multimap<String, Object>> duplicate) {
            this.duplicate = duplicate;
        }
    }
}
