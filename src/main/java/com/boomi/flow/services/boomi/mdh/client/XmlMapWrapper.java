package com.boomi.flow.services.boomi.mdh.client;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;

class XmlMapWrapper {
    @XmlAnyElement(lax = true)
    protected List<Element> elements;
}
