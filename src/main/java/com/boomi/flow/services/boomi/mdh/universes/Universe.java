package com.boomi.flow.services.boomi.mdh.universes;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

import java.util.UUID;

@Type.Element(name = "Universe")
public class Universe implements Type {
    @Type.Identifier
    @Type.Property(name = "ID", contentType = ContentType.String)
    private UUID id;

    @Type.Property(name = "Version", contentType = ContentType.String)
    private UUID version;

    @Type.Property(name = "Name", contentType = ContentType.String)
    private String name;

    public UUID getId() {
        return id;
    }

    public UUID getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }
}
