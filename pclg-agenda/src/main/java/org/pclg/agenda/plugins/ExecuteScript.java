package org.pclg.agenda.plugins;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaUtil;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.StringTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * Ejecuta un script sql. Version inicial: en el script cada instruiccion debe estar en una línea (puede terminar en ; o no)
 * Es transaccional.
 */
public class ExecuteScript implements Plugin {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    @Override
    public void execute(final Properties properties, final Connection conn, final String... args) throws SQLException {
        try {
			if (args.length == 0) {
				showMessageDialog(null,
					getStringFromProperties(properties, "Plugins.ExecuteScript.parameter"),
					getStringFromProperties(properties, "Agenda.alert.title"),
					ERROR_MESSAGE);
				return;
			}
            final File file = new File(args[0]);
            if (!file.exists()) {
                throw new FileNotFoundException(args[0]);
            }
            final boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            final List<String> lines = Files.readAllLines(file.toPath());
            for (final String sql : lines) {
                LOGGER.log(Level.OFF, sql);
                final String trimmedSql = cleanSQL(sql);
                if (trimmedSql.length() == 0) {
                    LOGGER.log(Level.OFF, "Comment or empty line skipped");
                } else {
                    try (final Statement stmt = conn.createStatement()) {
                        final int count = stmt.executeUpdate(trimmedSql);
                        LOGGER.log(Level.OFF, "Update count: " + count);
                    } catch (final SQLException ex) {
                        final String errMsg = StringTools.wrapLine(AgendaUtil.printSQLError(ex));
                        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                        conn.rollback();
                        showMessageDialog(null, errMsg,
							getStringFromProperties(properties, "Agenda.alert.title"),
                            ERROR_MESSAGE);
                        break;
                    }
                }
            }
            conn.setAutoCommit(autoCommit);
        } catch (final Exception ex) {
			showMessageDialog(null, ex.toString(),
				getStringFromProperties(properties, "Agenda.alert.title"),
       			ERROR_MESSAGE);
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
    }

    private String cleanSQL(final String sql) {
        String trimmedSql = sql.trim();
        final int end = trimmedSql.length() - 1;
        if (trimmedSql.lastIndexOf(';') == end) {
            trimmedSql = trimmedSql.substring(0, end);
         }
        return trimmedSql.startsWith("--") || trimmedSql.length() == 0 ? "" : trimmedSql/*.replace("\\n", "\n")*/;
    }
}
