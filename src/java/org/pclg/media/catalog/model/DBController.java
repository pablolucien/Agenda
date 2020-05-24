package org.pclg.media.catalog.model;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @since 24/01/2020.
 */
public class DBController {
    private static final CharSequence PWD_PLACEHOLDER = "[[[PWD]]]";
    private Connection dbConnection;
    private Properties appProperties;

    public void initDb(final Properties appProperties) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        this.appProperties = appProperties;
//        if (appProperties instanceof ObservableProperties) {
//            ((ObservableProperties) appProperties).addChangeObserver(this);
//        }
        final Properties props = new Properties();
        final String password = appProperties.getProperty("VideoCatalog.password");
        props.put("user", appProperties.getProperty("VideoCatalog.user"));
        props.put("password", password);
        final String driverClassName = appProperties.getProperty("VideoCatalog.driver");
        final Driver driver = (Driver) Class.forName(driverClassName).newInstance();
//        LOGGER.warn(String.format("driver = %s -- %d.%d%n", driver.toString(), driver.getMajorVersion(), driver.getMinorVersion()));
        final String originalConnectString = appProperties.getProperty("VideoCatalog.url");
        final String connectString =
                originalConnectString.replace(PWD_PLACEHOLDER, password);
        final Connection connection = DriverManager.getConnection(connectString, props);
//        dbConnection = new VersatileConnection(connection, null, new AuditPostprocessor(connection));
        dbConnection = connection;
//        LOGGER.log(Level.INFO, "Connected to database: " + originalConnectString);
        dbConnection.setAutoCommit(false);
    }


}
