package org.pclg.agenda.crypto;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Cifra todos lo campos VARCHAR de todas las tablas de la base de datos.
 * @author lucienpa
 *
 */
public class DatabaseEncrypter {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private final ResourceBundle appProperties = ResourceBundle.getBundle("Agenda");
	private final DummyCrypto crypto = new DummyCrypto(null);
	
	private DatabaseEncrypter(final String[] args) throws Exception {
		final String driver = appProperties.getString("AgendaDb.driver");
		Class.forName(driver).newInstance();
		LOGGER.log(Level.INFO, "Loaded the driver: " + driver);
		final String connectString = appProperties.getString("AgendaDb.url");
		final Properties props = new Properties();
		props.put("user", "root");
		props.put("password", "");
		final Connection connection = DriverManager.getConnection(connectString, props);
		LOGGER.log(Level.INFO, "Connected to database agendaDB.");
		connection.setAutoCommit(false);
		
		// TODO: Tomado de Migrator. Mutualizar!!!!
		final List<String> tableVector = getTables(connection);
		LOGGER.log(Level.INFO, "Tables: " + tableVector);
		for (final String table : tableVector) {
			crypt(connection, table);
		}
		connection.commit();
		connection.close();
		LOGGER.log(Level.INFO, "Committed transaction and closed connection");
		shutDown();
	}


	class FieldInfo {
		String fieldName;
		int fieldType;
		String fieldTypeName;	// FIXME: esto no va aqu�
		int fieldSize;
		int fieldPrecision;
		boolean isKeyField;
		@Override
		public String toString() {
			return fieldName + " " + fieldTypeName 
				+ (fieldType == Types.VARCHAR ? " (" + fieldSize + ")" : "");
		}
		
		
		
	}

	/**
	 * Cifra una tabla.
     * @param connection la conexi�n a la base de datos.
	 * @param tableName la tabla a cifrar.
	 * @throws SQLException si problemas haber.
	 */
	private void crypt(final Connection connection, final String tableName) throws SQLException {
		LOGGER.log(Level.INFO, "Cifrando: " + tableName);
		Statement stmt = connection.createStatement();
		final ResultSet metaData = stmt.executeQuery(
				"select * from " + tableName + " where 1 = 0");
		final ResultSetMetaData tableInfo = metaData.getMetaData();

		// La cantidad de campos
		int columnCount = tableInfo.getColumnCount();

		// Las informacion de los campos
		final FieldInfo[] fields = new FieldInfo[columnCount];

		for(int ii = 0; ii < columnCount; ii++) {
			fields[ii] = new FieldInfo();
			fields[ii].fieldName = tableInfo.getColumnName(ii + 1);
			fields[ii].fieldType = tableInfo.getColumnType(ii + 1);
			fields[ii].fieldTypeName = tableInfo.getColumnTypeName(ii + 1);
			fields[ii].fieldSize = tableInfo.getColumnDisplaySize(ii + 1);
			fields[ii].fieldPrecision = tableInfo.getPrecision(ii + 1);
			fields[ii].isKeyField = false;			// En principio suponemos que no es un indice unico
			LOGGER.log(Level.INFO, "\t" + fields[ii]);
		}
		stmt.close();
		final String selectSQL = obtenirSelect(tableName, fields);

        if (selectSQL != null) {
            // Aqu� hacemos el cifrado.
            stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = stmt.executeQuery(selectSQL);
            columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                for(int ii = 1; ii <= columnCount; ii++) {
                    rs.updateString(ii, crypto.cifrarXOR(rs.getString(ii)));
                }
                rs.updateRow();
            }
        }
    }


	private String obtenirSelect(final String tableName, final FieldInfo[] fields) {
		final StringBuilder builder = new StringBuilder("SELECT ");
		boolean somethingDone = false;
		for (final FieldInfo field : fields) {
			if (field.fieldType == Types.VARCHAR && !field.isKeyField) {
				builder.append(field.fieldName).append(',');
				somethingDone = true;
			}
		}
		if (!somethingDone) {
			return null;
		}
		
		builder.deleteCharAt(builder.length() - 1);
		builder.append(" FROM ").append(tableName);
		LOGGER.log(Level.INFO, builder);
		return builder.toString();
	}

	private List<String> getTables(final Connection connection) throws SQLException {
		final List<String> tableVector = new ArrayList<>();
		final DatabaseMetaData databaseInfo = connection.getMetaData();
		final String[] tableTypes = {"TABLE", "SYNONYM"};		// SYNONYM = Tablas vinculadas, VIEW = Consultas almacenadas
		final ResultSet dbMetaData = databaseInfo.getTables(null, null, "%", tableTypes);
		final int interestingCol = 3;

		while (dbMetaData.next()){
			final String tableName = dbMetaData.getString(interestingCol);
			tableVector.add(tableName);
		}
		return tableVector;
	}
	
	
	private static final String SHUTDOWN_SENTENCE = "jdbc:derby:;shutdown=true";	// FIXME: Eliminar esta dependencia de derby
	/**
	 * In embedded mode, an application should shut down Derby.
	 * If the application fails to shut down Derby explicitly,
	 * the Derby does not perform a checkpoint when the JVM shuts down, which
	 * means that the next connection will be slower.
	 * Explicitly shutting down Derby with the URL is preferred.
	 * This style of shutdown will always throw an "exception".
	 */
	private static void shutDown() {
		try {
			DriverManager.getConnection(SHUTDOWN_SENTENCE);
		} catch (final SQLException se) {
			// The XJ015 error (successful shutdown of the Derby engine)
			// and the 08006 error (successful shutdown of a single database)
			// are the only exceptions thrown by Derby that might indicate
			// that an operation succeeded. All other exceptions indicate
			// that an operation failed. You should check the log file
			// to be certain.
			final String sqlState = se.getSQLState();
			if (sqlState.equals("XJ015")) {
				LOGGER.info("Database shut down normally");
			} else {
				LOGGER.error("Database did not shut down normally. SQLState = "
					+ sqlState, se);
			}
		}
	}
	
	public static void main(final String[] args) throws Exception {
		new DatabaseEncrypter(args);
	}
}
