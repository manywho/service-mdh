package com.boomi.flow.services.boomi.mdh.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;

class XmlMapWrapper {

    @XmlAnyElement(lax = true)
    protected List<JAXBElement<String>> elements;
}
