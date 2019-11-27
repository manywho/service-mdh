package com.boomi.flow.services.boomi.mdh.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.List;

public class XmlMapAdapterProto extends XmlAdapter<XmlMapWrapper, List<JAXBElement<String>>> {

    @Override
    public List<JAXBElement<String>> unmarshal(XmlMapWrapper wrapper) throws Exception {
        return wrapper.elements;
    }

    @Override
    public XmlMapWrapper marshal(List<JAXBElement<String>> stringMultimapMap) throws Exception {
        throw new RuntimeException("Marshalling maps isn't supported yet");
    }
}
