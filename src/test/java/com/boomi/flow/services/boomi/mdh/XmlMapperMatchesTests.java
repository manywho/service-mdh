package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.match.MatchEntityResponse;
import com.google.common.io.Resources;
import org.junit.Test;
import javax.xml.bind.JAXB;
import java.net.URL;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class XmlMapperMatchesTests {

    @Test
    public void testXMLPure() {
        URL goldenRecords = Resources.getResource("testXmlMapperDeserializesMatchEntitiesResponse.xml");

        MatchEntityResponse matchEntityResponse = JAXB.unmarshal(goldenRecords, MatchEntityResponse.class);
        List<MatchEntityResponse.MatchResult> matchResults = matchEntityResponse.getMatchResults();

        assertThat(matchResults, notNullValue());
    }
}
