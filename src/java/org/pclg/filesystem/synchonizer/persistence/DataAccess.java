package org.pclg.filesystem.synchonizer.persistence;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.dbutil.DbManager;
import org.pclg.log.LoggerFactory;
import org.pclg.runtime.RuntimeControl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * @since 27/05/2020.
 */
public class DataAccess {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final String PATH_PLACEHOLDER = "[[[PATH]]]";
    private Connection dbConnection;
    private boolean initialized;

    public DataAccess(final Properties properties) {
        try {
            init(properties);
            initialized = true;
            RuntimeControl.registerShutdownHook(() -> {
                try {
                    LOGGER.log(Level.OFF, "Going to stop database");
                    shutdown(properties);
                } catch (final SQLException ex) {
                    LOGGER.log(Level.ERROR, "Error en ShutdownHook", ex);
                }
            });
        } catch (final Exception ex) {
            LOGGER.log(Level.ERROR, "Error en init(). Going ahead without disturbing my client.", ex);
        }
    }

    private void init(final Properties properties) throws Exception {
        final DbManager dbManager = new DbManager("DirectorySynchronizer");
        final String connectString = properties.getProperty("DirectorySynchronizer.url")
                .replace(PATH_PLACEHOLDER, properties.getProperty("DirectorySynchronizer.path"));
        dbConnection = dbManager.getConnection(properties.getProperty("DirectorySynchronizer.driver"),
                connectString, "", "");
        LOGGER.log(Level.OFF, "Connected to database: " + connectString);
        dbManager.checkAndCreateTables(properties);
    }

    private void shutdown(final Properties properties) throws SQLException {
        if (!initialized) {
            return;
        }
        if (dbConnection != null) {
            dbConnection.commit();
            dbConnection.close();
            LOGGER.log(Level.INFO, "Committed transaction and closed connection");
        }
        try {
            final String shutdownUrl = properties.getProperty("DirectorySynchronizer.shutdown.url")
                    .replace(PATH_PLACEHOLDER, properties.getProperty("DirectorySynchronizer.path"));;
            if (!isEmptyOrBlank(shutdownUrl)) {
                try (final Connection ignored = DriverManager.getConnection(shutdownUrl)) {
                    // Just need the call to close the database.
                }
            }
        } catch (final SQLException se) {
            // The XJ015 error (successful shutdown of the Derby engine)
            // and the 08006 error (successful shutdown of a single database)
            // are the only exceptions thrown by Derby that might indicate
            // that an operation succeeded. All other exceptions indicate
            // that an operation failed. You should check the log file
            // to be certain.
            final String sqlState = se.getSQLState();
            if (sqlState.equals("08006")) {
                LOGGER.info("Database shut down normally");
            } else {
                LOGGER.error("Database did not shut down normally. SQLState = "
                        + sqlState, se);
            }
        }
//        if (killDerby) {
//            shutDown();
//        }
        LOGGER.log(Level.INFO, getClass().getName() + " stopped");
    }

    public void saveInformation(final File file) {
        if (!initialized) {
            return;
        }
        try {
            final String select = "SELECT path, lastModified FROM Files WHERE path = ?";
            try (final PreparedStatement stmt = dbConnection.prepareStatement(
                    select,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE)) {
                final String absolutePath = file.getAbsolutePath();
                stmt.setString(1, absolutePath);
                final ResultSet rset = stmt.executeQuery();
                if (rset.next()) {
                    rset.updateLong(2, file.lastModified());
                    rset.updateRow();
                } else {
                    rset.moveToInsertRow();
                    rset.updateString(1, absolutePath);
                    rset.updateLong(2, file.lastModified());
                    rset.insertRow();
                }
            }
        } catch (final SQLException se) {
            final String sqlState = se.getSQLState();
            LOGGER.error("Error!! SQLState = " + sqlState, se);
        }
    }

    public boolean areNotModified(final File file1, final File file2) {
        if (!initialized) {
            return false;
        }
        final String sql = "SELECT lastModified FROM Files WHERE path IN (?, ?)";
        try (final PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
            pstmt.setString(1, file1.getAbsolutePath());
            pstmt.setString(2, file2.getAbsolutePath());
            final ResultSet resultSet = pstmt.executeQuery();
            final long storedLastModified1 = resultSet.next() ? resultSet.getLong(1) : -1;
            final long storedLastModified2 = resultSet.next() ? resultSet.getLong(1) : -1;
            return storedLastModified1 == file1.lastModified() && storedLastModified2 == file2.lastModified();
        } catch (final SQLException se) {
            final String sqlState = se.getSQLState();
            LOGGER.error("Error!! SQLState = " + sqlState, se);
        }
        return false;
    }
}
