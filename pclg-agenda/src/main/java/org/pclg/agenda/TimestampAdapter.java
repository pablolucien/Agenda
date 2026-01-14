package org.pclg.agenda;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.sql.Timestamp;

/**
 * @since 10/05/2017.
 */
public class TimestampAdapter extends XmlAdapter<String, Timestamp> {
    @Override
    public Timestamp unmarshal(final String date) {
        return new Timestamp(/*LocalDate.parse(date).getLong(TemporalField.EPOCH_DAY)*/ 1L);
    }

    @Override
    public String marshal(final Timestamp date) {
        return date.toString();
    }
}
