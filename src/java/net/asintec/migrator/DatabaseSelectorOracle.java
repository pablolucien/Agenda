// ******************************** package
package net.asintec.migrator;

import org.pclg.tools.ToolBox;

import java.awt.Cursor;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;


/**
	@version 1.00
	@author El Coyote Cojo
*/
public class DatabaseSelectorOracle extends DatabaseSelector {
    private static final long serialVersionUID = 3151526005747450605L;
    /** */
	// Datos de la conexion
	private String user         = "telespace";				// ??? OJO: Tomar usuario y password de otro sitio
	private String pass         = "tXj208pM";			// ??? OJO: Tomar usuario y password de otro sitio
	private final String serverIP     = "192.168.0.101";
	private final String serverPort   = "1521";
	private final String databaseName = "orcl";
	private final OracleDialog oracleDialog;

	public DatabaseSelectorOracle(final Migrator parent, final String title, final int tipo) {
		super(parent, title, null, tipo);
		oracleDialog = new OracleDialog(parent);
		driverName = "oracle.jdbc.driver.OracleDriver";
	}

	/**
		Selecciona una la base de datos
		@return true si se seleccionó algo, false de lo contrario
	*/
	@Override
	public boolean selectDatabase() {
		oracleDialog.setVisible(true);
		if(oracleDialog.accepted()) {
			user = oracleDialog.userField.getText();
			pass = oracleDialog.passField.getText();
			return(true);
		}
		return(false);
	}

	// ****************************************  Implementacion de DataSource   ************************************************************
	/**
		@return Una descripcion lo que puede hacer esta clase
	*/
	@Override
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t Manejo de base de datos Oracle"
									;
		return(description);
	}

	/**
		Abre la base de datos
	*/
	@Override
	public void openDatabase() {
		final String connectString;

		final Cursor oldCursor = getCursor();
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		// Si hay una conexion abierta, la cerramos
		if(srcConnection != null) {
			try {
				srcConnection.close();
			}
			catch(final SQLException ex) {
				ToolBox.showInfo(ex);
			}
		}


		try {
			final Driver theDriver =
				(Driver) Class.forName(driverName).newInstance();

			// Mostrar la informacion del driver
			System.out.println(": Usando " + theDriver.toString()
				+ " version " + theDriver.getMajorVersion() + "." + theDriver.getMinorVersion()
				+ (theDriver.jdbcCompliant()? " ": " NOT ") + "jdbc Compliant");


			connectString = "jdbc:oracle:thin:" + user + "/" + pass + "@"
				+ serverIP + ":" + serverPort + ":" + databaseName;

			final String catalogQuery;
//			catalogQuery = "select * from cat";			// Esto da tambien las 'secuencias' y supongo que los triggers, etc.
			catalogQuery = "select * from user_tables";

			// Conectarse a la database
			srcConnection = DriverManager.getConnection(connectString);

srcConnection.setAutoCommit(true);
System.out.println("DatabaseSelectorOracle.connection.getAutoCommit(): " + srcConnection.getAutoCommit());

			dbName.setText("<HTML><FONT FACE=\"Helvetica,Arial,sans-serif\" COLOR=\"#996600\">" + title + "</FONT><BR>" + "</HTML>");

			// Cerrar la base de datos al salir
			Runtime.getRuntime().addShutdownHook(new Thread() {
								@Override
								public void run() {
									try {
										srcConnection.close();
									}
									catch(final SQLException ex) {
										ToolBox.showInfo(ex);
									}
								}
							});



			// Crear un Statement
			final Statement MiSql = srcConnection.createStatement();

			// Ver las tablas que hay en el tablespace
			final ResultSet DBMetaData;

			final int interestingCol;

			DBMetaData = MiSql.executeQuery(catalogQuery);
			interestingCol = 1;


			tableListModel.removeAllElements();
			if(tipo == ORG) {
				tableListModel.addElement(QUERY_AD_HOC);
			}
			while(DBMetaData.next()){
				// Agregar los nombres de las tablas del catalogo
				final String tableName = DBMetaData.getString(interestingCol);
				tableListModel.addElement(tableName);
			}

			//** Necesario para DataSource
			tables = new String[tableListModel.getSize()];
			for(int ii = 0; ii < tables.length; ii++) {
				tables[ii] = (String) tableListModel.elementAt(ii);
			}
		}
		catch(final SQLException ex) {
			ToolBox.showInfo(ex);
		}
		catch(final ClassNotFoundException ex) {
			ToolBox.showInfo(ex);
		}
		catch(final InstantiationException ex) {
			ToolBox.showInfo(ex);
		}
		catch(final IllegalAccessException ex) {
			ToolBox.showInfo(ex);
		}
		finally {
			setCursor(oldCursor);
		}
		return;
	}

	/**
		Genera la senencia SQL para crear una tabla en Oracle
	*/
	protected String getTableCreationCommand(String tableName, final String[] fieldNames, final int[] fieldTypes, final String[] fieldTypeNames, final int[] fieldSizes) {
		tableName = ToolBox.normalizeName(tableName);
		for(int i = 0; i < fieldNames.length; i++) {
			fieldNames[i] = ToolBox.normalizeName(fieldNames[i]);
		}

		String command = "CREATE TABLE " + tableName + " (";

//	ALTER TABLE Meta_Tipos_Datos ADD PRIMARY KEY (Codigo)

		for(int i = 0; i < fieldNames.length; i++) {
			command += "\t";
			switch(fieldTypes[i]) {
			case Types.BIT:
				command += fieldNames[i] + " " + "NUMBER(1, 0)";
				break;
			case Types.TINYINT:
				command += fieldNames[i] + " " + "NUMBER(3, 0)";
				break;
			case Types.SMALLINT:
				command += fieldNames[i] + " " + "NUMBER(5, 0)";
				break;
//			case Types.CURRENCY:          ????????
// NUMERIC sirve para los CURRENCY de Access, pero Access usa el mismo codigo para CURRENCY y DECIMAL ??????
			case Types.NUMERIC:
				command += fieldNames[i] + " " + "NUMBER(15, 4)";
				break;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				command += fieldNames[i] + " " + "DATE";
				break;
			case Types.DOUBLE:
				command += fieldNames[i] + " " + "FLOAT(126)";
				break;
			case Types.VARCHAR:
				command += fieldNames[i] + " " + fieldTypeNames[i] + "(" + fieldSizes[i] + ")";
				break;
			case Types.LONGVARCHAR:
				command += fieldNames[i] + " " + "CLOB";		// Debería ser NCLOB, pero Oracle de momento no lo soporta
				break;
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				command += fieldNames[i] + " " + "LONG RAW";		// Debería ser BLOB, pero Oracle de momento no lo soporta
				break;
			default:
				command += fieldNames[i] + " " + fieldTypeNames[i];
System.out.println(fieldNames[i] + " " + fieldTypeNames[i]  + ", " + fieldTypes[i]  + ", " + fieldSizes[i]);
				break;
			}
			command += ", \n";
		}
		//command = command.substring(0, command.length() - 2);	// Eliminar una ', ' que	me sobra.
		command = command.substring(0, command.length() - 3);	// Eliminar una ', \n' que	me sobra.
		command += ")";
		return(command);
	}

	/** */
	private void debug(final String s) {	
		System.err.println(getClass().getName() + " -> " + s);
	}
}
