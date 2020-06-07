package org.pclg.agenda.jdbc;

import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaDbException;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.AgendaRecordImpl;
import org.pclg.agenda.entities.Pais;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.Chrono;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 27/08/17 20:15
 */
public final class DbEngine {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final List<AgendaRecord> records = new ArrayList<>();
	private final TelephoneHelper telephoneHelper;
	private final NoteHelper noteHelper;
    private final Set<FieldManagerHelper> otherFieldManagerHelpers;
    private final Set<FieldManagerHelper> allFieldManagerHelpers;
	/** Set to <code>true</code> if the record are to be retreived completely is disregard of if the getters will be called or not.*/
	private boolean autoretrieve;

    DbEngine(final TelephoneHelper telephoneHelper, final NoteHelper noteHelper,
             final FieldManagerHelper... otherFieldManagerHelpers) {
		this.telephoneHelper = telephoneHelper;
		this.noteHelper = noteHelper;
		this.otherFieldManagerHelpers = new HashSet<>(Arrays.asList(otherFieldManagerHelpers));
		allFieldManagerHelpers = new HashSet<>(this.otherFieldManagerHelpers);
		allFieldManagerHelpers.add(telephoneHelper);
		allFieldManagerHelpers.add(noteHelper);
	}

    List<AgendaRecord> executeTheQuery(final PreparedStatement statement) throws SQLException {
        try (final ResultSet rset = statement.executeQuery()) {
            final int cron = Chrono.getChrono();
            Chrono.start(cron);
            records.clear();
            AgendaRecord record;
            while ((record = getNextRecord(rset)) != AgendaRecord.NULL_AGENDA_RECORD) {
                records.add(record);
            }
            Chrono.mark(cron);
            LOGGER.debug("autoretrieve: " + autoretrieve + " -> Tiempo: " + Chrono.timeDetail(Chrono.elapsed(cron)));
            return Collections.unmodifiableList(records);
        }
    }

    AgendaRecord getNextRecord(final ResultSet rset) throws SQLException {
        final AgendaRecord record;
        if (rset.next()) {
            record = new AgendaRecordImpl(telephoneHelper, noteHelper, otherFieldManagerHelpers);
            int index = 0;
            record.setKey(rset.getInt(++index))
                .setVersion(rset.getInt(++index))
                .setFirstname(rset.getString(++index))
                .setLastname(rset.getString(++index))
                .setSex(rset.getString(++index))
                .setCountry(Pais.getInstance(rset.getString(++index)))
                .setVersionTelephone(rset.getInt(++index))
                .setVersionAddress(rset.getInt(++index))
                .setVersionEmail(rset.getInt(++index))
                .setVersionGroup(rset.getInt(++index))
                .setVersionImage(rset.getInt(++index))
                .setDay(rset.getInt(++index))
                .setMonth(rset.getInt(++index))
                .setYear(rset.getInt(++index))
                .setMark(rset.getString(++index))
                .setUpdateTimestamp(rset.getTimestamp(++index))
                .setCreationTimestamp(rset.getTimestamp(++index))
                .setListTelephones(rset.getBoolean(++index))
                .setVersionNotes(rset.getInt(++index))
                .setDeleted(rset.getBoolean(++index));
            if (autoretrieve) {
                allFieldManagerHelpers.forEach(h -> {
					try {
						h.retrieve(record);
					} catch (SQLException e) {
						throw new AgendaDbException(e);
					}
				});
            }
        } else {
            record = AgendaRecord.NULL_AGENDA_RECORD;
        }
        return record;
    }

    public boolean isAutoretrieve() {
        return autoretrieve;
    }

	/** Set to <code>true</code> if the record are to be retreived completely is disregard of if the getters will be called or not.*/
	public void setAutoretrieve(final boolean autoretrieve) {
        this.autoretrieve = autoretrieve;
	}
}
