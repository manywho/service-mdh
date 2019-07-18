package com.boomi.flow.services.boomi.mdh.match;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;
import java.util.UUID;

import static com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS;

@Type.Element(name = FUZZY_MATCH_DETAILS)
public class FuzzyMatchDetails implements Type {
    @Type.Identifier
    @Type.Property(name="ID", contentType = ContentType.String, bound = false)
    private UUID id;

    @Type.Property(name="Field", contentType = ContentType.String, bound = false)
    private String field;

    @Type.Property(name="First", contentType = ContentType.String, bound = false)
    private String first;

    @Type.Property(name="Second", contentType = ContentType.String, bound = false)
    private String second;

    @Type.Property(name="Method", contentType = ContentType.String, bound = false)
    private String method;

    @Type.Property(name="Match Strength", contentType = ContentType.String, bound = false)
    private String matchStrength;

    @Type.Property(name="Threshold", contentType = ContentType.String, bound = false)
    private String threshold;

    public UUID getId() {
        return id;
    }

    public String getField() {
        return field;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public String getMethod() {
        return method;
    }

    public String getMatchStrength() {
        return matchStrength;
    }

    public String getThreshold() {
        return threshold;
    }
}
