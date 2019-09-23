package com.boomi.flow.services.boomi.mdh.match;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

import javax.xml.bind.annotation.XmlElement;
import java.util.UUID;

import static com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS;

@Type.Element(name = FUZZY_MATCH_DETAILS)
public class FuzzyMatchDetails implements Type {

    @Type.Identifier
    @Type.Property(name="ID", contentType = ContentType.String)
    private UUID id;

    @Type.Property(name="Field", contentType = ContentType.String)
    private String field;

    @Type.Property(name="First", contentType = ContentType.String)
    private String first;

    @Type.Property(name="Second", contentType = ContentType.String)
    private String second;

    @Type.Property(name="Method", contentType = ContentType.String)
    private String method;

    @Type.Property(name="Match Strength", contentType = ContentType.String)
    private String matchStrength;

    @Type.Property(name="Threshold", contentType = ContentType.String)
    private String threshold;

    public UUID getId() {
        return id;
    }

    @XmlElement
    public String getField() {
        return field;
    }

    @XmlElement
    public String getFirst() {
        return first;
    }

    @XmlElement
    public String getSecond() {
        return second;
    }

    @XmlElement
    public String getMethod() {
        return method;
    }

    @XmlElement
    public String getMatchStrength() {
        return matchStrength;
    }

    @XmlElement
    public String getThreshold() {
        return threshold;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setMatchStrength(String matchStrength) {
        this.matchStrength = matchStrength;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }
}
