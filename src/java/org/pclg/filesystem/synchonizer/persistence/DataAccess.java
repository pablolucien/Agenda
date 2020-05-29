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
import java.sql.SQLException;
import java.util.Properties;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * @since 27/05/2020.
 */
public class DataAccess {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final Properties properties;
    private Connection dbConnection;
    private boolean initialized;

    public DataAccess(final Properties properties) {
        this.properties = properties;
        try {
            init();
            initialized = true;
            RuntimeControl.registerShutdownHook(() -> {
                try {
                    LOGGER.log(Level.OFF, "Going to stop database");
                    shutdown();
                } catch (final SQLException ex) {
                    LOGGER.log(Level.ERROR, "Error en ShutdownHook", ex);
                }
            });
        } catch (final Exception ex) {
            LOGGER.log(Level.ERROR, "Error en init(). Going ahead without disturbing my client.", ex);
        }
    }

    public void init() throws Exception {
        final DbManager dbManager = new DbManager("DirectorySynchronizer");
        final String connectString = properties.getProperty("DirectorySynchronizer.url");
        dbConnection = dbManager.getConnection(properties.getProperty("DirectorySynchronizer.driver"),
                connectString, "", "");
        LOGGER.log(Level.OFF,"Connected to database: " + connectString);
        dbManager.checkAndCreateTables(properties);
    }

    public void shutdown() throws SQLException {
        if (!initialized) {
            return;
        }
        if (dbConnection != null) {
            dbConnection.commit();
            dbConnection.close();
            LOGGER.log(Level.INFO, "Committed transaction and closed connection");
        }
        try {
            final String shutdownUrl = properties.getProperty("DirectorySynchronizer.shutdown.url");
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

    public void saveInformation(File file, File tgtFile) {
        if (!initialized) {
            return;
        }
        try {
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO Files(path, lastModified) VALUES (?, ?)");
        } catch (SQLException se) {
            final String sqlState = se.getSQLState();
            LOGGER.error("Error!! SQLState = " + sqlState, se);
        }
    }

    public boolean areNotModified(File file1, File file2) {
        if (!initialized) {
            return false;
        }
        return false;
    }
}
