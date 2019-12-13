package com.boomi.flow.services.boomi.mdh.common;
import java.time.OffsetDateTime;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class EngineCompatibleDates {

    public static String parse(OffsetDateTime offsetDateTime) {
        if (offsetDateTime != null) {
                return offsetDateTime
                        .format(ISO_OFFSET_DATE_TIME);
        }

        return null;
    }
}
