package com.boomi.flow.services.boomi.mdh.match;

import com.boomi.flow.services.boomi.mdh.client.XmlMapAdapter;
import com.manywho.sdk.api.run.elements.type.MObject;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

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
        private MObject entity;
        private List<MObject> match = new ArrayList<>();
        private List<MObject> duplicate = new ArrayList<>();

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
        public MObject getEntity() {
            return entity;
        }

        public void setEntity(MObject entity) {
            this.entity = entity;
        }

        @XmlElement(name = "match")
        @XmlJavaTypeAdapter(XmlMapAdapter.class)
        public List<MObject> getMatch() {
            return match;
        }

        public void setMatch(List<MObject> match) {
            this.match = match;
        }

        @XmlElement(name = "duplicate")
        @XmlJavaTypeAdapter(XmlMapAdapter.class)
        public List<MObject> getDuplicate() {
            return duplicate;
        }

        public void setDuplicate(List<MObject> duplicate) {
            this.duplicate = duplicate;
        }
    }
}
