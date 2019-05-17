package com.boomi.flow.services.boomi.mdh.common;

import com.migesok.jaxb.adapter.javatime.OffsetDateTimeXmlAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.OffsetDateTime;
import java.util.Objects;

public class DateFilter {
    private OffsetDateTime from;
    private OffsetDateTime to;

    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    public OffsetDateTime getFrom() {
        return from;
    }

    public DateFilter setFrom(OffsetDateTime from) {
        this.from = from;
        return this;
    }

    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    public OffsetDateTime getTo() {
        return to;
    }

    public DateFilter setTo(OffsetDateTime to) {
        this.to = to;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DateFilter that = (DateFilter) o;

        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
