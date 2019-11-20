package com.boomi.flow.services.boomi.mdh.records;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

import java.time.OffsetDateTime;

@Type.Element(name = GoldenRecordConstants.LINK)
public class GoldenRecordLink implements Type {
    @Type.Identifier
    @Type.Property(name = "Entity ID", contentType = ContentType.String)
    private String id;

    @Type.Property(name = "Source", contentType = ContentType.String)
    private String source;

    @Type.Property(name = "Established Date", contentType = ContentType.DateTime)
    private OffsetDateTime establishedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public OffsetDateTime getEstablishedDate() {
        return establishedDate;
    }

    public void setEstablishedDate(OffsetDateTime establishedDate) {
        this.establishedDate = establishedDate;
    }
}
