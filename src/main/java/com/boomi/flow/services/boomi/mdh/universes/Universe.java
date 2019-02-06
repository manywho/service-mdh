package com.boomi.flow.services.boomi.mdh.universes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

import java.util.List;
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

    private Layout layout;

    public UUID getId() {
        return id;
    }

    public UUID getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public Layout getLayout() {
        return layout;
    }

    public static class Layout {
        private Model model;

        public Model getModel() {
            return model;
        }

        public static class Model {
            @JacksonXmlProperty(localName = "element")
            private List<Element> elements;

            public List<Element> getElements() {
                return elements;
            }

            public static class Element {
                @JacksonXmlProperty(isAttribute = true)
                private String uniqueId;

                @JacksonXmlProperty(isAttribute = true)
                private String name;

                @JacksonXmlProperty(isAttribute = true)
                private String prettyName;

                @JacksonXmlProperty(isAttribute = true)
                private String type;

                @JacksonXmlProperty(isAttribute = true)
                private boolean required;

                public String getUniqueId() {
                    return uniqueId;
                }

                public void setUniqueId(String uniqueId) {
                    this.uniqueId = uniqueId;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getPrettyName() {
                    return prettyName;
                }

                public void setPrettyName(String prettyName) {
                    this.prettyName = prettyName;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public boolean isRequired() {
                    return required;
                }
            }
        }
    }
}
