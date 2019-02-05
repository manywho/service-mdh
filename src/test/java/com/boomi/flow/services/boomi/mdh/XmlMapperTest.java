package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.guice.XmlMapperProvider;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.io.Resources;
import org.junit.Test;
import org.xmlunit.builder.Input;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

public class XmlMapperTest {
    @Test
    public void testXmlMapperSerializesCorrectly() throws JsonProcessingException {
        var xmlMapper = new XmlMapperProvider().get();

        var filter = new QuarantineQueryRequest.Filter()
                .setCauses(List.of("cause 1", "cause 2"))
                .setCreatedDate(new QuarantineQueryRequest.DateFilter()
                        .setFrom(OffsetDateTime.parse("2013-01-01T00:00Z"))
                        .setTo(OffsetDateTime.parse("2015-12-31T00:00Z"))
                )
                .setEndDate(new QuarantineQueryRequest.DateFilter()
                        .setFrom(OffsetDateTime.parse("2018-01-01T00:00Z"))
                        .setTo(OffsetDateTime.parse("2018-12-31T00:00Z"))
                )
                .setResolutions(List.of("resolution 1", "resolution 2"))
                .setSourceEntityId("a source entity id")
                .setSourceId("a source id");

        var request = new QuarantineQueryRequest()
                .setFilter(filter)
                .setIncludeData(true)
                .setType("a type");

        var expected = Input.fromURL(Resources.getResource("testXmlMapperSerializesCorrectly.xml"));

        assertThat(xmlMapper.writeValueAsString(request), isIdenticalTo(expected).ignoreWhitespace());
    }
}
