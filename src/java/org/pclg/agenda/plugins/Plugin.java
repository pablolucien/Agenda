package org.pclg.agenda.plugins;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@FunctionalInterface
public interface Plugin {
	void execute(final Properties properties, Connection conn, String... args) throws SQLException;
}
