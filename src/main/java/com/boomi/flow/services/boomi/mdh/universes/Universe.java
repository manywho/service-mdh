package com.boomi.flow.services.boomi.mdh.universes;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Type.Element(name = "Universe")
@XmlAccessorType(XmlAccessType.FIELD)
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

        public Layout setModel(Model model) {
            this.model = model;
            return this;
        }

        public static class Model {
            private List<Element> elements = new ArrayList<>();

            @XmlElement(name = "element")
            public List<Element> getElements() {
                return elements;
            }

            public Model setElements(List<Element> elements) {
                this.elements = elements;
                return this;
            }

            public static class Element {
                private String uniqueId;
                private String name;
                private String prettyName;
                private String type;
                private boolean required;

                @XmlAttribute
                public String getUniqueId() {
                    return uniqueId;
                }

                public void setUniqueId(String uniqueId) {
                    this.uniqueId = uniqueId;
                }

                @XmlAttribute
                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                @XmlAttribute
                public String getPrettyName() {
                    return prettyName;
                }

                public void setPrettyName(String prettyName) {
                    this.prettyName = prettyName;
                }

                @XmlAttribute
                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                @XmlAttribute
                public boolean isRequired() {
                    return required;
                }
            }
        }
    }
}
