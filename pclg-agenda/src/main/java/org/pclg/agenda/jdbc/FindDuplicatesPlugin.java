package org.pclg.agenda.jdbc;

import org.apache.logging.log4j.Logger;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.plugins.Plugin;
import org.pclg.log.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.pclg.agenda.jdbc.AgendaDbJDBC.BASE_SELECT_RECORDS_SENTENCE;

public class FindDuplicatesPlugin implements Plugin {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    @Override
    public void execute(final Properties properties, final Connection conn, final String... args) throws SQLException {
        LOGGER.error("------ Start");
        final String sqlSelect = BASE_SELECT_RECORDS_SENTENCE + "ORDER BY clave, version";

        final GeneralHelper generalHelper = new GeneralHelper(conn);
        final DbEngine dbEngine = new DbEngine(new TelephoneHelper(conn, generalHelper), new NoteHelper(conn, generalHelper),
			new GroupHelper(conn, generalHelper), new AddressHelper(conn, generalHelper),
			new ImageHelper(conn, generalHelper, properties.getProperty("Agenda.images.root")),
            new EmailHelper(conn, generalHelper));
		dbEngine.setAutoretrieve(true);
        try (final PreparedStatement statement = conn.prepareStatement(sqlSelect)) {
            final List<AgendaRecord> records = dbEngine.executeTheQuery(statement);
            findDuplicates(records);
        }
        LOGGER.error("------ End");
    }

    private void findDuplicates(final List<AgendaRecord> records) {
        final List<AgendaRecord> duplicates = new ArrayList<>();
        for (int ii = 0, size = records.size(); ii < size; ii++) {
            final AgendaRecord record = records.get(ii);
            final List<AgendaRecord> subList = records.subList(ii + 1, size);
            if (subList.contains(record)) {
                duplicates.add(record);
                duplicates.addAll(subList.stream().filter(record::equals).collect(Collectors.toList()));
            }
        }

        LOGGER.error("duplicates: " + duplicates.size());

        int lastKey = -1;
        for (final AgendaRecord duplicate : duplicates) {
            final int key = duplicate.getKey();
            if (lastKey != key) {
                lastKey = key;
                LOGGER.error("------>  " + key);
            }
            LOGGER.error(String.format("Clave = [%d], version = [%d], qui�n = [%s]",  key, duplicate.getVersion(), duplicate.fullName()));
        }
    }
}
