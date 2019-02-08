package com.boomi.flow.services.boomi.mdh.records;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class GoldenRecordHistoryAdapter extends XmlAdapter<Element, GoldenRecordHistory> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GoldenRecordHistoryAdapter.class);

    @Override
    public Element marshal(GoldenRecordHistory m) throws Exception {
        throw new RuntimeException("Marshalling maps isn't supported yet");
    }

    @Override
    public GoldenRecordHistory unmarshal(Element wrapper) throws Exception {
        var pattern = DateTimeFormatter.ofPattern("MM-dd-yyyy'T'HH:mm:ss.SSS[xxx][xx][X]");

        try {
            var history = new GoldenRecordHistory();

            if (wrapper.hasAttribute("enddate")) {
                history.setEndDate(OffsetDateTime.parse(wrapper.getAttribute("enddate"), pattern));
            }

            if (wrapper.hasAttribute("startdate")) {
                history.setStartDate(OffsetDateTime.parse(wrapper.getAttribute("startdate"), pattern));
            }

            if (wrapper.hasAttribute("grid")) {
                history.setGrid(wrapper.getAttribute("grid"));
            }

            if (wrapper.hasAttribute("version")) {
                history.setVersion(Long.parseLong(wrapper.getAttribute("version")));
            }

            if (wrapper.hasAttribute("source")) {
                history.setSource(wrapper.getAttribute("source"));
            }

            if (wrapper.hasAttribute("enddatesource")) {
                history.setEndDateSource(wrapper.getAttribute("enddatesource"));
            }

            if (wrapper.hasAttribute("transactionId")) {
                history.setTransactionId(wrapper.getAttribute("transactionId"));
            }

            // Now we deserialize the body
            if (wrapper.hasChildNodes()) {
                var fields = new HashMap<String, Object>();

                for (int i = 0; i < wrapper.getChildNodes().getLength(); i++) {
                    var childNode = wrapper.getChildNodes().item(i);
                    if (childNode.hasChildNodes()) {

                        var fieldNode = childNode.getFirstChild();
                        if (fieldNode.hasChildNodes()) {
                            // TODO: We don't care about nested things yet
                        } else {
                            fields.put(childNode.getNodeName(), fieldNode.getNodeValue());
                        }

                    }
                }

                history.setFields(fields);
            }

            return history;
        } catch (Exception e) {
            LOGGER.error("Unable to unmarshal the golden record history object", e);

            throw new RuntimeException(e);
        }
    }
}

