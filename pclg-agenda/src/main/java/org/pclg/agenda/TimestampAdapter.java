package org.pclg.agenda;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.sql.Timestamp;

/**
 * @since 10/05/2017.
 */
public class TimestampAdapter extends XmlAdapter<String, Timestamp> {
    @Override
    public Timestamp unmarshal(String date) throws Exception {
        return new Timestamp(/*LocalDate.parse(date).getLong(TemporalField.EPOCH_DAY)*/ 1L);
    }

    @Override
    public String marshal(Timestamp date) throws Exception {
        return date.toString();
    }
}
