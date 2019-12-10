package com.boomi.flow.services.boomi.mdh.client;
import com.google.common.base.Strings;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * We should be returning an OffsetDateTime but it is causing an issue when the time is trimmed, this need to be fixed in
 * engine before we can return this type
 */
public class XmlFullDateAdapter extends XmlAdapter<String, String> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    @Override
    public String unmarshal(String stringValue) throws Exception {
        return (Strings.isNullOrEmpty(stringValue))?null: OffsetDateTime.parse(stringValue, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'[HH][:mm][:ss]X")).format(formatter);
    }

    @Override
    public String marshal(String stringValue) throws Exception {
        throw new RuntimeException("unmarshal not supported");
    }

}
