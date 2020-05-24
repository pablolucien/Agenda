package org.pclg.fortunes;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.PropertiesHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Pablo
 * @since 12/04/14 18:01
 */
public class Loader {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	public static void main(final String[] args) {
		try {
			new Loader().loadFortunes();
		} catch (final Throwable throwable) {
			LOGGER.error("Error", throwable);
		}
	}

	/**
	 * Loads the fortunes Unix-style into the database.
	 */
	private void loadFortunes() throws IOException, ClassNotFoundException,
			IllegalAccessException, InstantiationException, SQLException {
		final Properties appProperties = new Properties();
        PropertiesHelper.loadPropertiesFromFile(appProperties, "Fortunes.properties");
		try (final Connection connection = Manager.getConnection(appProperties)) {
			final File sourceDir = new File(appProperties.getProperty("FortunesDb.sourceDir"));
			if (!sourceDir.isDirectory()) {
				LOGGER.warn(sourceDir + " no existe.");
				System.exit(-1);
			}
			final File[] files = sourceDir.listFiles();
			for (final File file : files) {
				loadFortunes(connection, file);
			}
			connection.commit();
		}
	}

	private static void loadFortunes(final Connection connection,
			final File file) throws IOException, SQLException {
		LOGGER.info("To load fortunes from file: " + file);
		final BufferedReader reader = new BufferedReader(new FileReader(file));
		try (final PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO FORTUNES (FORTUNE) VALUES (?)")) {
			final StringBuilder builder = new StringBuilder();
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				if (line.trim().equals("%")) {
					statement.setString(1, builder.toString());
					statement.execute();
					count++;
					builder.setLength(0);
				} else {
					builder.append(line).append('\n');
				}
			}
			LOGGER.info("   loaded " + count + " fortunes");
		}
	}
}
