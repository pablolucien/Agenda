// ********************* package
package net.asintec.migrator;


// ********************* imports

import org.pclg.tools.PCLGTools;
import org.pclg.tools.StringTools;
import org.pclg.tools.ToolBox;

import java.sql.CallableStatement;
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

import static org.pclg.tools.StringTools.EMPTY_STRING_ARRAY;

/**
	DataSourceJDBC
	@author El Coyote Cojo
	@version 2001.nov.28 17:52:07, CEST
*/
public class DataSourceJDBC implements DataSource, MigratorConstants {
	// ********************* Variables de clase

	// ********************* Variables de instancia
	private CallableStatement callableStatement;

	/** El nombre de la base de datos o la entrada ODBC con la que se va a trabajar */
	private String dbURL;

	/** Fuente de datos */
	private Connection connection;

	/** El acceso a los registros */
	private ResultSet dataResultSet;

	/** Los tipos de los campos del dataResultSet */
	private int[] dataResultSetTypes;

	/** El numero de columnas del resultset */
	private int numCols;
	
	/** Los campos seleccionados para el query */
	private String[] selectedFields;

	/** La sentencia para hacer los select */
	private String selectSentence;

	/** La tabla seleccionada para trabajar */
	private String selectedTable;

	/** Los nombres de las tablas de la base de datos */
	private String[] tables;

	/** Los nombres de los campos de la tabla seleccionada */
	private String[] fieldNames;

	/** Los tipos de los campos de la tabla */
	private int[] fieldTypes;

	/** Los nombres de los tipos de los campos de la tabla */
	private String[] fieldTypeNames;

	/** Los tama�os de los campos */
	private int[] fieldSizes;

	/** Las precisiones de los campos */
	private int[] fieldPrecisions;

	/** Los campos que forman la clave primaria */
	private String[] primaryKey;

	/** Indica si el campo es un indice sin duplicados */
	private boolean[] uniqueFields;

	/** Utilizado cuando el acceso no es a una simple tabla */
	private String queryAdHoc;

	/** El nombre de usuario para acceder a la base de datos */
	private final String user         = "Administrador";
//	private String user         = "SYSDBA";		// Interbase

	/** La contrase�a para acceder a la base de datos */
	private String pass         = "asintec72";
//	private String pass         = "masterkey";	// Interbase

	// ********************* Constructores

	// ********************* Metodos de instancia
	/**
		@return Una descripcion lo que puede hacer esta clase
	*/
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t Manejo de base de datos JDBC"
									;
		return(description);
	}

	/**
		Indica la base de datos con la que se desea trabajar. Cada implementacion sabr� qu� debe hacer
		@param databaseName Un identificador de la base de datos con la que se desea trabajar
		@param password La contrase�a a usar
	*/
	public void setDatabase(final String databaseName, final String password) {
		if(databaseName == null) {
			return;
		}
		if(password != null) {
			pass = password;
		}
		if(databaseName.toLowerCase().endsWith(".mdb")) {
			// Suponemos que es un archivo de Access
			dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + databaseName + ";UID=" + user + ";PWD=" + pass;
		}
		else if(databaseName.toLowerCase().endsWith(".xls")) {
			// Suponemos que es un archivo de Excel
			dbURL = "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ=" + databaseName + ";DriverID=22;READONLY=false" + ";UID=" + user + ";PWD=" + pass;
		}
		else {
			// Suponemos que es una entrada ODBC
			dbURL = "jdbc:odbc:" + databaseName + ";UID=" + user + ";PWD=" + pass;
		}
	}

	/**
		Abre la base de datos
	*/
	public void openDatabase() {
		if (dbURL == null) {
			return;				// No podemos hacer nada si no sabemos contra que base de datos vamos a trabajar
		}

		// Si hay una conexion abierta, la cerramos
		closeDatabase();

		try {
			// Cargar el driver JDBC-ODBC
			// 1ra forma
			//Driver theDriver = new sun.jdbc.odbc.JdbcOdbcDriver();

			// 2da forma
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

			// 3ra forma
			//Driver theDriver = (Driver) Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
			//DriverManager.registerDriver(theDriver);
			// Mostrar la informacion del driver
			//System.out.println(DSN + ": Usando " + theDriver.toString()
			//	+ " version " + theDriver.getMajorVersion() + "." + theDriver.getMinorVersion()
			//	+ (theDriver.jdbcCompliant()? " ": " NOT ") + "jdbc Compliant");

			final String catalogQuery = "select name, type from MSysObjects where type = 1 and flags = 0";

			// Conectarse a la database
			connection = DriverManager.getConnection(dbURL);

			// Cerrar la base de datos al salir
			Runtime.getRuntime().addShutdownHook(new Thread() {
								public void run() {
									try {
										connection.close();
									}
									catch(final SQLException ex) {
										ToolBox.showInfo(ex);
									}
								}
							});



			// Crear un Statement
			final Statement MiSql = connection.createStatement();

			// Ver las tablas que hay en el tablespace
			final ResultSet DBMetaData;

			final int interestingCol;

			final boolean usandoAccess = true; // ?????????????
			if(usandoAccess) {
				final DatabaseMetaData databaseInfo = connection.getMetaData();
				final String[] tableTypes = {"TABLE", "SYNONYM"};		// SYNONYM = Tablas vinculadas, VIEW = Consultas almacenadas
//tableTypes = null;
				DBMetaData = databaseInfo.getTables(null, null, "%", tableTypes);
				interestingCol = 3;
			}
			else {  // Oracle
				DBMetaData = MiSql.executeQuery(catalogQuery);
				interestingCol = 1;
			}

			final ResultSetMetaData tableInfo;
			tableInfo = DBMetaData.getMetaData();
//			int columnCount = tableInfo.getColumnCount();

			final List<String> tableVector = new ArrayList<>();
			tableVector.add(QUERY_AD_HOC);
			while(DBMetaData.next()){
				// Agregar los nombres de las tablas del catalogo
				final String tableName = DBMetaData.getString(interestingCol)/* + " " + DBMetaData.getString(4)*/;
				tableVector.add(tableName);
			}
			tables = StringTools.list2StringArray(tableVector);
		}
		catch(final SQLException ex) {
			if(ex.getSQLState().equals("42000") && ex.getErrorCode() == -1905) {
				return;
			}
			ToolBox.showInfo(ex);
		}
		catch(final ClassNotFoundException ex) {
			ToolBox.showInfo(ex);
		}

		return;
	}

	/**
		Cierra la base de datos. Cada implementacion sabr� qu� debe hacer.
	*/
	public void closeDatabase() {
		if(connection != null) {
			try {
				connection.close();
			}
			catch(final SQLException ex) {
				ToolBox.showInfo(ex);
			}
		}
	}

	/**
		@return Un arreglo con los nombres de las tablas en la base de datos.
	*/
	public String[] getTables() {
		return(tables);
	}

	/**
		@return Un arreglo con los nombres de los campos de una tabla.
	*/
	public String[] getFieldNames() {
		final String[] dummy = new String[fieldNames.length];
		System.arraycopy(fieldNames, 0, dummy, 0, fieldNames.length);
		return(dummy);
	}

	/**
		@return Un arreglo con los tipos de los campos de la tabla
	*/
	public int[] getFieldTypes() {
		final int[] dummy = new int[fieldTypes.length];
		System.arraycopy(fieldTypes, 0, dummy, 0, fieldTypes.length);
		return(dummy);
	}

	/**
		Devuelve un arreglo con los nombres de los tipos de los campos de la tabla que est� abierta.
		@return Un arreglo con los nombres de los tipos de los campos de la tabla que est� abierta.
	*/
	public String[] getFieldTypeNames() {
		final String[] dummy = new String[fieldTypeNames.length];
		System.arraycopy(fieldTypeNames, 0, dummy, 0, fieldTypeNames.length);
		return(dummy);
	}

	/**
		Devuelve un arreglo con los tama�os de los campos de la tabla que est� abierta.
		@return Un arreglo con los tama�os de los campos de la tabla que est� abierta.
	*/
	public int[] getFieldSizes() {
		final int[] dummy = new int[fieldSizes.length];
		System.arraycopy(fieldSizes, 0, dummy, 0, fieldSizes.length);
		return(dummy);
	}

	/** Devuelve los campos que son indices sin duplicados */
	public boolean[] getUniqueFields() {
		return(uniqueFields);
	}

	/**
		Devuelve un arreglo con los nombres de los campos de la tabla que forman la clave primaria o null.
		@return Un arreglo con los nombres de los campos de la tabla que forman la clave primaria o null.
		@since 2003.09.21 00:24.
	*/
	public String[] getPrimaryKey() {
		return(primaryKey);
	}
	
	/** Indica sobre que tabla vamos a trabajar */
	public void setSelectedTable(final String table) {
		selectedTable = table;
		openTable(table);
	}

	/** Indica sobre que campos vamos a trabajar */
	public void setSelectedFields(final String[] fields) {
		selectedFields = fields;
	}

	/** Devuelve el siguente registro o null */
	public Object[] getNextRecord()  {
		if(dataResultSet == null) {
			return (null);
		}
		try {
			if(dataResultSet.next()) {
				final Object[] data = new Object[numCols];
				for(int i = 0; i < numCols; i++) {
					switch(dataResultSetTypes[i]) {
					case Types.VARBINARY:
					case Types.LONGVARBINARY:
						data[i] = dataResultSet.getBytes(i + 1);
						break;
  /* Esta mierda funciona???
  */
					case Types.LONGVARCHAR:
					case Types.VARCHAR:
					case Types.CHAR:
					case Types.CLOB:
						data[i] = dataResultSet.getObject(i + 1);		// En los campos memo si son de u solo caracter, no devuelve nada
//debug((String) data[i]);	// Parece que no, dita sea
						break;
					default:
						data[i] = dataResultSet.getString(i + 1);
					}
				}
				return(data);
			}
		}
		catch(final SQLException ex) {
			ToolBox.showInfo(ex);
		}
		return(null);
	}

	/** @return true si estamos en el final de la tabla */
	public boolean eof() {
		return(false);
	}

	/** Establece el query a usar para acceder a la base de datos */
	public void setQuery(final String query) {
		selectSentence = query;
		queryAdHoc = query;									// ???????
		debug("selectSentence: " + selectSentence);
	}

	/** Inicia el proceso de obtencion de datos */
	public boolean executeQuery() {
		debug("executeQuery(): selectSentence = " + selectSentence);
		if(selectSentence == null) {
			return (false);
		}

		try {
			// Ejecutamos la peticion a la BD
			dataResultSet = connection.createStatement().executeQuery(selectSentence);
			// Obtenemos info sobre el resultado
			final ResultSetMetaData metaData = dataResultSet.getMetaData();
			// ... cuantas columnas
			numCols = metaData.getColumnCount();
			// ... y los tipos de datos
			dataResultSetTypes = new  int[numCols];
			for(int i = 0; i < numCols; i++) {
				dataResultSetTypes[i] = metaData.getColumnType(i + 1);
			}
			return(true);
		}
		catch(final SQLException ex) {
			ToolBox.showInfo(ex);
			System.err.println("selectSentence: " + selectSentence);
			return(false);
		}
	}

	/** */
	private void debug(final String s) {	
		System.err.println(getClass().getName() + " -> " + s);
	}

	/**
		Abre una tabla de la base de datos
		@param tableName El nombre de la tabla que se desea abrir
	*/
	public void openTable(final String tableName) {
		try {
			final Statement stmt = connection.createStatement();
			final ResultSet metaData;

//System.err.println("tableName:  " + tableName);
//System.err.println("queryAdHoc: " + queryAdHoc);

			if(tableName.startsWith(QUERY_AD_HOC)) {
				queryAdHoc = tableName.substring(QUERY_AD_HOC.length());
//System.err.println("queryAdHoc: " + queryAdHoc);

				// Hacemos el intento de optimizar la busqueda de la meta info
				if(queryAdHoc.toUpperCase().indexOf("WHERE") == -1) {
					metaData = stmt.executeQuery(queryAdHoc + " WHERE 1 = 0");
				}
				else {
					metaData = stmt.executeQuery(queryAdHoc);
				}
			}
			else {
				queryAdHoc = null;
				// 2002.05.20 Access permite espacios el los nombres 
				if(tableName.indexOf(' ') == -1) {
					metaData = stmt.executeQuery(
							"select * from " + tableName + " where 1 = 0");
				}
				else {
					metaData = stmt.executeQuery(
							"select * from [" + tableName + "] where 1 = 0");
				}
			}

			final ResultSetMetaData tableInfo = metaData.getMetaData();

			// La cantidad de campos
			final int columnCount = tableInfo.getColumnCount();

			// Los nombres de los campos
			fieldNames = new String[columnCount];

			// Los tipos de dato
			fieldTypes = new int[columnCount];

			// Los nombres de los tipos de dato
			fieldTypeNames = new String[columnCount];

			// Los tama�os
			fieldSizes = new int[columnCount];

			// Las precisiones
			fieldPrecisions = new int[columnCount];

			// Los indices unicos
			uniqueFields = new boolean[columnCount];

			// Agregar los nombres de los campos, etc.
			for(int i = 0; i < columnCount; i++) {
				fieldNames[i] = tableInfo.getColumnName(i + 1);
				fieldTypes[i] = tableInfo.getColumnType(i + 1);
				fieldTypeNames[i] = tableInfo.getColumnTypeName(i + 1);
				fieldSizes[i] = tableInfo.getColumnDisplaySize(i + 1);
				fieldPrecisions[i] = tableInfo.getPrecision(i + 1);
				uniqueFields[i] = false;			// En principio suponemos que no es un indice unico
//				System.out.println(fieldNames[i] + " -> " + fieldTypes[i]);				
//				System.out.println(fieldNames[i] + " -> " + fieldTypeNames[i]);				
//				System.out.println(fieldNames[i] + " -> " + fieldSizes[i]);				
//				System.out.println(fieldNames[i] + " -> " + fieldPrecisions[i]);				
			}

			// Ahora vemos quien es en realidad indice unico
			// Solo lo hacemos si es una tabla normal
			if(queryAdHoc == null) {
				final DatabaseMetaData databaseInfo = connection.getMetaData();
/*
JFrame fr = new JFrame();
fr.getContentPane().add(new driverPropertiesPanel(databaseInfo), java.awt.BorderLayout.CENTER);
fr.pack();
fr.setVisible(true);
//*/
				// De momento no hago nada, s�lo ver si el driver tiene la capacidad de darme esta informaci�n
				try {
/*	obtener las claves primarias de Access a lo bruto */
primaryKey = getAccessPrimaryKey(databaseInfo, tableName);

					ResultSet rsetPrimaryKeys = databaseInfo.getPrimaryKeys(null, null, tableName);
					System.err.println("El driver tiene la capacidad de darme getPrimaryKeys()");
					rsetPrimaryKeys = databaseInfo.getImportedKeys(null, null, tableName);
					System.err.println("El driver tiene la capacidad de darme getImportedKeys()");
					rsetPrimaryKeys = databaseInfo.getExportedKeys(null, null, tableName);
					System.err.println("El driver tiene la capacidad de darme getExportedKeys()");
				}
				catch(final SQLException ex) {
					PCLGTools.trace(ex, "El driver NO tiene la capacidad de darme getPrimaryKeys()");
					//ToolBox.showInfo(ex);
				}
				
				final ResultSet rsetIndex = databaseInfo.getIndexInfo(null, null, tableName, true, false);
				while(rsetIndex.next()) {
					final String indexName = rsetIndex.getString(6);
					final String indexFieldName = rsetIndex.getString(9);
					//System.out.println("indexName: " + indexName + "-> indexFieldName: " + indexFieldName);
					if(indexFieldName == null) {
						continue;
					}
					for(int i = 0; i < columnCount; i ++) {
						if(indexFieldName.equalsIgnoreCase(fieldNames[i])) {
							uniqueFields[i] = true;
							break;
						}
					}
				}
				rsetIndex.close();
			}
		}
		catch(final SQLException ex) {
			ToolBox.showInfo(ex);
		}
	}

	/**
		obtener las claves primarias de Access a lo bruto
		repite trabajo ya hecho arriba ?????
		@since 2003.09.21
	*/
	private String[] getAccessPrimaryKey(final DatabaseMetaData databaseInfo, final String tableName) throws SQLException {
		final List<String> indexVector = new ArrayList<>();

		// get the primary key information
		final ResultSet rset = databaseInfo.getIndexInfo(null,null, tableName, true,true);
		while(rset.next()) {
			final String idx = rset.getString(6);
			if(idx != null) {		//Note: index "PrimaryKey" is Access DB specific other db server has diff. index syntax.
				if(idx.equalsIgnoreCase("PrimaryKey")) {
					indexVector.add(rset.getString(9));
				}
			}
		}
		rset.close();
		return indexVector.toArray(EMPTY_STRING_ARRAY);
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public void setConnection(final Connection connection) {
		this.connection = connection;
	}

	/**
		Agrega un registro a la base de datos.
		@return true si la operaci�n fu� exitosa
	*/
	public boolean addRecord(final Object[] record) {
		return(false);
	}
}
