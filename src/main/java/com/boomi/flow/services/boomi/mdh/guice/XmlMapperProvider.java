package com.boomi.flow.services.boomi.mdh.guice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.inject.Provider;

public class XmlMapperProvider implements Provider<XmlMapper> {
    @Override
    public XmlMapper get() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return xmlMapper;
    }
}
