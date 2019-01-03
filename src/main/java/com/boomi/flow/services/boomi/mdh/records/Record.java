package com.boomi.flow.services.boomi.mdh.records;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

import java.time.OffsetDateTime;

@Type.Element(name = "Record")
public class Record implements Type {
    @Type.Identifier
    @Type.Property(name = "ID", contentType = ContentType.String)
    private String id;

    @Type.Property(name = "Created At", contentType = ContentType.DateTime)
    private OffsetDateTime createdDate;

    @Type.Property(name = "Updated At", contentType = ContentType.DateTime)
    private OffsetDateTime updatedDate;

    @Type.Element(name = "Record Field")
    public static class Field implements Type {
        private 
    }
}
