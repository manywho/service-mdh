package com.boomi.flow.services.boomi.mdh.universes;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class UniversesResponse {
    private List<Universe> universes;

    @XmlElement(name = "universe")
    public List<Universe> getUniverses() {
        return universes;
    }

    public UniversesResponse setUniverses(List<Universe> universes) {
        this.universes = universes;
        return this;
    }
}
