package org.pclg.fortunes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.runtime.RuntimeControl;
import org.pclg.tools.PropertiesHelper;
import org.pclg.xtras.ClassPathHacker;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages the fortunes (basically reads them)
 * @author Pablo
 * @since 12/04/14 19:09
 */
public class Manager {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final String COUNT_OF_FORTUNES
		= "SELECT COUNT(*) FROM Fortunes";
	private static final String SELECT_FORTUNE
		= "SELECT Fortune FROM Fortunes WHERE Clave = ?";

	private final Connection connection;

	public Manager() throws ClassNotFoundException, SQLException, InstantiationException,
			IllegalAccessException, IOException {
        final Properties properties = new Properties();
		PropertiesHelper.loadPropertiesFromFile(properties, "Fortunes.properties");
		final String applicationClassPath =
            properties.getProperty("FortunesDb.ClassPath");
		LOGGER.debug("FortunesDb.ClassPath = " + applicationClassPath);
		ClassPathHacker.addFiles(applicationClassPath.split("\\|"));
		connection = getConnection(properties);
        RuntimeControl.registerShutdownHook(() -> {
            try {
                connection.commit();
                connection.close();
                LOGGER.log(Level.INFO, "Fortunes stopped");
            } catch (final SQLException ex) {
                LOGGER.log(Level.ERROR, "Error en ShutdownHook", ex);
            }
        });
    }

	public int getFortunesCount() throws SQLException {
		try (final PreparedStatement pstmt =
				 connection.prepareStatement(COUNT_OF_FORTUNES)) {
			final ResultSet rset = pstmt.executeQuery();
			return rset.next() ? rset.getInt(1) : 0;
		}
	}

	/**
	 * Returns a random fortune.
	 * @param key the integer key of the fortune. (0 <= key < count of fortunes).
	 * @return a random fortune.
	 * @throws SQLException if some DB error occurs.
	 */
	public String getRandomFortune(final int key) throws SQLException {
		try (final PreparedStatement pstmt =
				 connection.prepareStatement(SELECT_FORTUNE)) {
			pstmt.setInt(1, key);
			final ResultSet rset = pstmt.executeQuery();
			return rset.next() ? rset.getString(1) : null;
		}
	}

	static Connection getConnection(final Properties appProperties)
		    throws InstantiationException, IllegalAccessException,
		    ClassNotFoundException, SQLException {
		final String driver = appProperties.getProperty("FortunesDb.driver");
		Class.forName(driver).newInstance();
		LOGGER.log(Level.INFO, "Loaded the driver: " + driver);
		final String connectString = appProperties.getProperty("FortunesDb.url");
		final Connection dbConnection = DriverManager
			.getConnection(connectString, appProperties);
		LOGGER.log(Level.INFO, "Connected to database.");
		dbConnection.setAutoCommit(false);
		return dbConnection;
	}
}
