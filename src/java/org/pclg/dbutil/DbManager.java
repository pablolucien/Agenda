package org.pclg.dbutil;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.agenda.AuditPostprocessor;
import org.pclg.log.LoggerFactory;

import java.sql.*;
import java.util.Properties;

public final class DbManager {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final String DERBY_TABLE_INEXISTENT = "42X05";
	private static final String DERBY_SCHEMA_INEXISTENT = "42Y07";
	private static Connection dbConnection;
	private String clientId;

/*
	String strUrl = "jdbc:datadirect:oracle://" + "129.158.229.21:1521;SID=ORCL9";
	String strUserId = "scott";
	String strPassword = "tiger";
	String className = "com.ddtek.jdbc.oracle.OracleDriver";
*/
//	String strUrl = "jdbc:odbc:Test";
//	private static final String strUrl = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=C:\\home\\plucien\\TestRowSet\\test.mdb";
//	private static final String strUserId = "";
//	private static final String strPassword = "";
//	private static final String driverClassName = "sun.jdbc.odbc.JdbcOdbcDriver";

	/**
	 * @param clientId prefix to be used when getting information of the properties.
	 */
	public DbManager(String clientId) {
		this.clientId = clientId;
	}

	public Connection getConnection(String driverClassName, String connectString, String user, String pwd)
			throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		final Driver driver = (Driver) Class.forName(driverClassName).newInstance();
		LOGGER.warn(String.format("driver = %s -- %d.%d%n", driver.toString(), driver.getMajorVersion(), driver.getMinorVersion()));
		final Properties props = new Properties();
		props.put("user", user);
		props.put("password", pwd);
		final Connection connection = DriverManager.getConnection(connectString, props);
		dbConnection = new VersatileConnection(connection, null, new AuditPostprocessor(connection));
		return dbConnection;
	}

	public void createTables(final Properties properties) throws SQLException {
		final String[] tables = properties.getProperty(clientId + ".tables").split(";");
		try (final Statement stmt = dbConnection.createStatement()) {
			for (final String table : tables) {
				final String sql = String.format("DROP TABLE %s", table);
				LOGGER.log(Level.INFO, String.format("Ejecutando: %s", sql));
				try {
					stmt.execute(sql);
				} catch (final SQLException ex) {
					final String state = ex.getSQLState();
					final int errCode = ex.getErrorCode();
					LOGGER.log(Level.ERROR, String.format(
							"ex.getSQLState() = %s ex.getErrorCode() = %d. SQL = %s",
							state, errCode, sql));
				}
			}
			dbConnection.commit();
			checkAndCreateTables(properties);
		}
	}

	/**
	 * Verifica la existencia de las tablas de la aplicación y las crea si no
	 * existen.
	 * @param properties de donde sacar las tablas, las sentencias SQL, etc.
	 */
	public void checkAndCreateTables(final Properties properties)
			throws SQLException {
		final String[] tables =
				properties.getProperty(clientId + ".tables").split(";");
		for (final String table : tables) {
			if (!checkTable(dbConnection, table)) {
				LOGGER.warn(String.format("%s no existe. Voy a crearla", table));
				createTable(dbConnection, table, properties);
			} else {
				LOGGER.debug(String.format("%s si existe.", table));
			}
		}
	}

	/**
	 * Verifica la existencia de una tabla.
	 *
	 * @param conn Conexión a la base de datos.
	 * @param tableName tabla a verificar.
	 * @return <code>true</code> si existe, <code>false</code> si no.
	 * @throws SQLException si pasan cosas malas.
	 */
	private static boolean checkTable(final Connection conn, final String tableName)
			throws SQLException {
		LOGGER.debug("Checking the existence of " + tableName);
		try (final Statement stmt = conn.createStatement()) {
			//noinspection JDBCResourceOpenedButNotSafelyClosed
			stmt.executeQuery(String.format("select 42 from %s", tableName));
			return true;
		} catch (final SQLException ex) {
			final String sqlState = ex.getSQLState();
			LOGGER.debug("sqlState = " + sqlState);
			if (!sqlState.equals(DERBY_SCHEMA_INEXISTENT)
					&& !sqlState.equals(DERBY_TABLE_INEXISTENT)) {
				throw ex;
			}
		}
		return false;
	}

	/**
	 * Crea una tabla y eventualmente la inicializa con datos.
	 *
	 * @param conn la conexión a la base de datos.
	 * @param tableName la tabla a crear.
	 * @param properties Propertiees que contiene las sentencias a usar en la
	 *        forma AgendaDb.create<tableName>, AgendaDb.populate<tableName> y
	 *        AgendaDb.createINDEX_<tableName><num>.
	 * @throws SQLException si hay errores de acceso a la base de datos.
	 */
	private void createTable(final Connection conn, final String tableName,
									final Properties properties) throws SQLException {
		try (final Statement stmt = conn.createStatement()) {
			String sqlSentence =
					properties.getProperty(clientId + ".create" + tableName);
			LOGGER.warn(sqlSentence);
			stmt.execute(sqlSentence);
			sqlSentence =
					properties.getProperty(clientId + ".populate" + tableName);
			if (sqlSentence != null) {
				LOGGER.warn(sqlSentence);
				stmt.execute(sqlSentence);
			}
			int idxNr = 0;
			while ((sqlSentence = properties.getProperty(clientId + ".createINDEX_"
					+ tableName + ++idxNr)) != null) {
				LOGGER.warn(sqlSentence);
				stmt.execute(sqlSentence);
			}
		}
	}

	public static void releaseConnection(final Connection conn) throws SQLException {
		conn.close();
	}
}