package com.boomi.flow.services.boomi.mdh.universes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.time.OffsetDateTime;
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

    @Type.Property(name = "Sources", contentType = ContentType.List)
    private List<Source> sources;

    @Type.Property(name = "Layout ID", contentType = ContentType.String)
    private UUID layoutId;

    @Type.Property(name = "Layout", contentType = ContentType.Object)
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

    public List<Source> getSources() {
        return sources;
    }

    public UUID getLayoutId() {
        return layoutId;
    }

    public Layout getLayout() {
        return layout;
    }

    @Type.Element(name = "Source")
    public static class Source implements Type {
        @JacksonXmlProperty(isAttribute = true)
        @Type.Property(name = "Code", contentType = ContentType.String, bound = false)
        private String code;

        @JacksonXmlProperty(isAttribute = true)
        @Type.Property(name = "State", contentType = ContentType.String, bound = false)
        private String state;

        @JacksonXmlProperty(isAttribute = true)
        @Type.Property(name = "Allow Contribute?", contentType = ContentType.Boolean, bound = false)
        private boolean allowContribute;

        @Type.Property(name = "Channel", contentType = ContentType.Object, bound = false)
        private Channel channel;

        public String getCode() {
            return code;
        }

        public String getState() {
            return state;
        }

        public boolean isAllowContribute() {
            return allowContribute;
        }

        public Channel getChannel() {
            return channel;
        }

        @Type.Element(name = "Channel")
        public static class Channel implements Type {
            @Type.Property(name = "ID", contentType = ContentType.String, bound = false)
            private String id;

            @Type.Property(name = "Update Type", contentType = ContentType.String, bound = false)
            private String updateType;

            @Type.Property(name = "State", contentType = ContentType.String, bound = false)
            private String state;

            @Type.Property(name = "Is Primary?", contentType = ContentType.Boolean, bound = false)
            private boolean isPrimary;

            public String getId() {
                return id;
            }

            public String getUpdateType() {
                return updateType;
            }

            public String getState() {
                return state;
            }

            public boolean isPrimary() {
                return isPrimary;
            }
        }
    }

    @Type.Element(name = "Layout")
    public static class Layout implements Type {
        @JacksonXmlProperty(isAttribute = true)
        @Type.Property(name = "ID XPath", contentType = ContentType.String, bound = false)
        private String idXPath;

        @Type.Property(name = "Model", contentType = ContentType.Object, bound = false)
        private Model model;

        // TODO: <deletedElements />

        @JacksonXmlElementWrapper(useWrapping=false)
        @Type.Property(name = "Fields", contentType = ContentType.List, bound = false)
        private List<Field> field;

        public String getIdXPath() {
            return idXPath;
        }

        public Model getModel() {
            return model;
        }

        public List<Field> getFields() {
            return field;
        }

        @Type.Element(name = "Model")
        public static class Model implements Type {
            @JacksonXmlProperty(isAttribute = true)
            @Type.Property(name = "Name", contentType = ContentType.String, bound = false)
            private String name;

            @Type.Property(name = "Elements", contentType = ContentType.List, bound = false)
            private List<Element> elements;

            public String getName() {
                return name;
            }

            public List<Element> getElements() {
                return elements;
            }

            @Type.Element(name = "Element")
            public static class Element implements Type {
                @JacksonXmlProperty(isAttribute = true)
                @Type.Property(name = "Unique ID", contentType = ContentType.String, bound = false)
                private String uniqueId;

                @JacksonXmlProperty(isAttribute = true)
                @Type.Property(name = "Name", contentType = ContentType.String, bound = false)
                private String name;

                @JacksonXmlProperty(isAttribute = true)
                @Type.Property(name = "Pretty Name", contentType = ContentType.String, bound = false)
                private String prettyName;

                @JacksonXmlProperty(isAttribute = true)
                @Type.Property(name = "Type", contentType = ContentType.String, bound = false)
                private String type;

                @JacksonXmlProperty(isAttribute = true)
                @Type.Property(name = "Required?", contentType = ContentType.Boolean, bound = false)
                private boolean required;

                @JacksonXmlProperty(isAttribute = true)
                @Type.Property(name = "Enforce Integrity?", contentType = ContentType.Boolean, bound = false)
                private boolean enforceIntegrity;

                @JacksonXmlProperty(isAttribute = true)
                @Type.Property(name = "Incoming Reference Integrity?", contentType = ContentType.Boolean, bound = false)
                private boolean incomingReferenceIntegrity;

                @JacksonXmlProperty(isAttribute = true)
                @Type.Property(name = "Repeatable?", contentType = ContentType.Boolean, bound = false)
                private boolean repeatable;

                public String getUniqueId() {
                    return uniqueId;
                }

                public String getName() {
                    return name;
                }

                public String getPrettyName() {
                    return prettyName;
                }

                public String getType() {
                    return type;
                }

                public boolean isRequired() {
                    return required;
                }

                public boolean isEnforceIntegrity() {
                    return enforceIntegrity;
                }

                public boolean isIncomingReferenceIntegrity() {
                    return incomingReferenceIntegrity;
                }

                public boolean isRepeatable() {
                    return repeatable;
                }
            }
        }

        @Type.Element(name = "Field")
        public static class Field implements Type {
            @Type.Property(name = "XPath", contentType = ContentType.String, bound = false)
            private String xpath;

            @JacksonXmlProperty(isAttribute = true)
            @Type.Property(name = "Unique ID", contentType = ContentType.String, bound = false)
            private String uniqueId;

            @JacksonXmlProperty(isAttribute = true)
            @Type.Property(name = "Pretty Name", contentType = ContentType.String, bound = false)
            private String prettyName;

            @JacksonXmlProperty(isAttribute = true)
            @Type.Property(name = "Type", contentType = ContentType.String, bound = false)
            private String type;

            @JacksonXmlProperty(isAttribute = true)
            @Type.Property(name = "Required?", contentType = ContentType.Boolean, bound = false)
            private boolean required;

            @JacksonXmlProperty(isAttribute = true)
            @Type.Property(name = "Enforce Integrity?", contentType = ContentType.Boolean, bound = false)
            private boolean enforceIntegrity;

            @JacksonXmlProperty(isAttribute = true)
            @Type.Property(name = "Incoming Reference Integrity?", contentType = ContentType.Boolean, bound = false)
            private boolean incomingReferenceIntegrity;

            @JacksonXmlProperty(isAttribute = true)
            @Type.Property(name = "Title Field?", contentType = ContentType.Boolean, bound = false)
            private boolean titleField;

            public String getXpath() {
                return xpath;
            }

            public String getUniqueId() {
                return uniqueId;
            }

            public String getPrettyName() {
                return prettyName;
            }

            public String getType() {
                return type;
            }

            public boolean isRequired() {
                return required;
            }

            public boolean isEnforceIntegrity() {
                return enforceIntegrity;
            }

            public boolean isIncomingReferenceIntegrity() {
                return incomingReferenceIntegrity;
            }

            public boolean isTitleField() {
                return titleField;
            }
        }
    }
}
