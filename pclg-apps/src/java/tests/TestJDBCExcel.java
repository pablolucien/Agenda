package tests;

import org.pclg.runtime.RuntimeControl;

import javax.swing.JFrame;
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

import static org.pclg.tools.StringTools.list2StringArray;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 18-jul-2007 11:24:45
 */
public class TestJDBCExcel {
	// ********************* Variables de clase
	private static final String INVALID_PASSWORD = "DataSource_INVALID_PASSWORD";

	private static String excelFile = "C:/tmp/ExcelFile.xls";

	// ********************* Variables de instancia
	private CallableStatement callableStatement;

	/** El nombre de la base de datos o la entrada ODBC con la que se va a trabajar */
	private String DSN;

	/** Fuente de datos */
	private Connection srcConnection;

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

	/** Los tamaños de los campos */
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

	/** La contraseña para acceder a la base de datos */
	private String pass         = "asintec72";
//	private String pass         = "masterkey";	// Interbase

	// ********************* Constructores

	// ********************* Metodos de instancia
	/**
		@return Una descripcion lo que puede hacer esta clase
	*/
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t Manejo de base de datos ODBC"
									;
		return(description);
	}

	/**
		Indica la base de datos con la que se desea trabajar. Cada implementacion sabrá qué debe hacer
		@param databaseName Un identificador de la base de datos con la que se desea trabajar
		@param password La contraseña a usar
	*/
	void setDatabase(final String databaseName, final String password) {
		DSN = databaseName;
		if(password != null) {
			pass = password;
		}
	}

	/**
		Devuelve el nombre de la base de datos con la que se está trabajando
		@return el nombre de la base de datos con la que se está trabajando
	*/
	public String getDatabaseName() {
		return(DSN);
	}

	/**
		Abre la base de datos
	*/
	String openDatabase() {
		if(DSN == null) {
			return(DSN);				// No podemos hacer nada si no sabemos contra que base de datos vamos a trabajar
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

			final String connectString;
			if(DSN.toLowerCase().endsWith(".mdb")) {
				// Suponemos que es un archivo de Access
				connectString = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + DSN + ";UID=" + user + ";PWD=" + pass;
			}
			else if(DSN.toLowerCase().endsWith(".xls")) {
				// Suponemos que es un archivo de Excel
				connectString = "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ=" + DSN + ";DriverID=22;READONLY=false" + ";UID=" + user + ";PWD=" + pass;
			}
			else {
				// Suponemos que es una entrada ODBC
				connectString = "jdbc:odbc:" + DSN + ";UID=" + user + ";PWD=" + pass;
			}


			final String catalogQuery = "select name, type from MSysObjects where type = 1 and flags = 0";

			// Conectarse a la database
			srcConnection = DriverManager.getConnection(connectString);

			// Cerrar la base de datos al salir
            RuntimeControl.registerShutdownHook(() -> {
                try {
                    srcConnection.close();
                } catch (final SQLException ex) {
                    ex.printStackTrace();
                }
            });

			// Crear un Statement
			final Statement MiSql = srcConnection.createStatement();

			// Ver las tablas que hay en el tablespace
			final ResultSet DBMetaData;

			final int interestingCol;

			final boolean usandoAccess = true; // ?????????????
			if(usandoAccess) {
				final DatabaseMetaData databaseInfo = srcConnection.getMetaData();

final ResultSet rsetCatalogs = databaseInfo.getCatalogs();
while (rsetCatalogs.next()) {
	System.out.println(rsetCatalogs.getString(1));
}



//*
final JFrame fr = new JFrame();
fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//fr.getContentPane().add(new driverPropertiesPanel(databaseInfo), java.awt.BorderLayout.CENTER);
fr.pack();
fr.setVisible(true);
//*/
				String[] tableTypes = {"TABLE", "SYNONYM"};		// SYNONYM = Tablas vinculadas, VIEW = Consultas almacenadas
tableTypes = null;
				DBMetaData = databaseInfo.getTables(null, null, "%", tableTypes);
				interestingCol = 3;
			}
			else {  // Oracle
				DBMetaData = MiSql.executeQuery(catalogQuery);
				interestingCol = 1;
			}

			final ResultSetMetaData TableInfo;
			TableInfo = DBMetaData.getMetaData();
//			int columnCount = TableInfo.getColumnCount();

			final List<String> tableVector = new ArrayList<>();

			while(DBMetaData.next()){
				// Agregar los nombres de las tablas del catalogo
				final String tableName = DBMetaData.getString(interestingCol) + " " + DBMetaData.getString(4);
				tableVector.add(tableName);
			}
			tables = list2StringArray(tableVector);
		}
		catch(final SQLException ex) {
			if(ex.getSQLState().equals("42000") && ex.getErrorCode() == -1905) {
				return(INVALID_PASSWORD);
			}
			ex.printStackTrace();
		}
		catch(final ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return(DSN);
	}

	/**
		Cierra la base de datos. Cada implementacion sabrá qué debe hacer.
	*/
	void closeDatabase() {
		if(srcConnection != null) {
			try {
				srcConnection.close();
			}
			catch(final SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
		@return Un arreglo con los nombres de las tablas en la base de datos.
	*/
	String[] getTables() {
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
		@return Un arreglo con los tipos de los campos de una tabla
	*/
	public int[] getFieldTypes() {
		final int[] dummy = new int[fieldTypes.length];
		System.arraycopy(fieldTypes, 0, dummy, 0, fieldTypes.length);
		return(dummy);
	}

	/**
		Devuelve un arreglo con los nombres de los tipos de los campos de la tabla que está abierta.
		@return Un arreglo con los nombres de los tipos de los campos de la tabla que está abierta.
	*/
	public String[] getFieldTypeNames() {
		final String[] dummy = new String[fieldTypeNames.length];
		System.arraycopy(fieldTypeNames, 0, dummy, 0, fieldTypeNames.length);
		return(dummy);
	}

	/**
		Devuelve un arreglo con los tamaños de los campos de la tabla que está abierta.
		@return Un arreglo con los tamaños de los campos de la tabla que está abierta.
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
			ex.printStackTrace();
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
		if(selectSentence == null)
			return(false);

		try {
			// Ejecutamos la peticion a la BD
			dataResultSet = srcConnection.createStatement().executeQuery(selectSentence);
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
			ex.printStackTrace();
			System.err.println("selectSentence: " + selectSentence);
			return(false);
		}
	}

	/** */
	private void debug(final String s) {
		//System.err.println(getClass().getName() + " -> " + s);
	}

	/**
		Abre una tabla de la base de datos
		@param tableName El nombre de la tabla que se desea abrir
	*/
	void openTable(final String tableName) {
		try {
			final Statement stmt = srcConnection.createStatement();
			final ResultSet metaData;

				queryAdHoc = null;
				// 2002.05.20 Access permite espacios el los nombres
//				if(tableName.indexOf(' ') == -1)
//					metaData = stmt.executeQuery("select * from " + tableName + " where 1 = 0");
//				else
					metaData = stmt.executeQuery("select * from [" + tableName + "] where 1 = 0");

			final ResultSetMetaData tableInfo = metaData.getMetaData();

			// La cantidad de campos
			final int columnCount = tableInfo.getColumnCount();

			// Los nombres de los campos
			fieldNames = new String[columnCount];

			// Los tipos de dato
			fieldTypes = new int[columnCount];

			// Los nombres de los tipos de dato
			fieldTypeNames = new String[columnCount];

			// Los tamaños
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
				System.out.println(fieldNames[i] + " -> " + fieldTypes[i]);
				System.out.println(fieldNames[i] + " -> " + fieldTypeNames[i]);
				System.out.println(fieldNames[i] + " -> " + fieldSizes[i]);
				System.out.println(fieldNames[i] + " -> " + fieldPrecisions[i]);
			}

			// Ahora vemos quien es en realidad indice unico
			// Solo lo hacemos si es una tabla normal
			if(queryAdHoc == null) {
				final DatabaseMetaData databaseInfo = srcConnection.getMetaData();
/*
JFrame fr = new JFrame();
fr.getContentPane().add(new driverPropertiesPanel(databaseInfo), java.awt.BorderLayout.CENTER);
fr.pack();
fr.setVisible(true);
//*/
				// De momento no hago nada, sólo ver si el driver tiene la capacidad de darme esta información
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
					System.err.println("El driver NO tiene la capacidad de darme getPrimaryKeys()");
					//ex.printStackTrace();
				}

				final ResultSet rsetIndex = databaseInfo.getIndexInfo(null, null, tableName, true, false);
				while(rsetIndex.next()) {
					final String indexName = rsetIndex.getString(6);
					final String indexFieldName = rsetIndex.getString(9);
					//System.out.println("indexName: " + indexName + "-> indexFieldName: " + indexFieldName);
					if(indexFieldName == null)
						continue;
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
			ex.printStackTrace();
		}
	}

	/**
		obtener las claves primarias de Access a lo bruto
		repite trabajo ya hecho arriba ?????
		@since 2003.09.21
	*/
	private String[] getAccessPrimaryKey(final DatabaseMetaData databaseInfo, final String tableName) throws SQLException {
		final List<String> indexVector = new ArrayList<>();
		final String key_colname = null;

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
		return list2StringArray(indexVector);
	}

	private void test(final String xls_sheet) {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		final String xls_address = excelFile;

		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			con = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)}; DBQ=" + xls_address + ";DriverID=22;READONLY=false", "", "");
			final String query = "select * from [" + xls_sheet + "]";
			final String emp_id = "";

			try {
				st = con.createStatement();

				rs = st.executeQuery(query);
				while (rs.next()) {
					//emp_id = rs.getString(2);
					System.out.println(rs.getString(1) + " -- " + rs.getString(2) + " -- " + rs.getString(3));
				}
			}//end of try
			catch (final Exception ex) {
				ex.printStackTrace();
			}
			finally {
				try {
					rs.close();
					st.close();
				}
				catch (final Exception e) {
				}

			}
		}
		catch (final Exception ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				con.close();
			}
			catch (final Exception e) {
			}
		}

	}


	public static void main(final String[] args) {
		if (args.length > 0) {
			excelFile = args[0];
		}
		final TestJDBCExcel testJDBCExcel = new TestJDBCExcel();
		testJDBCExcel.setDatabase(excelFile, "");
		final String result = testJDBCExcel.openDatabase();
		System.out.println(result);
		final String[] tables = testJDBCExcel.getTables();
		for (final String table : tables) {
			System.out.println(table);
		}

		testJDBCExcel.openTable("FF CHINA FOCUS A ACC HKD$");
		testJDBCExcel.openTable("Hoja1$");

        //testJDBCExcel.test("Hoja1$");
   		System.out.println();
        testJDBCExcel.test("FF CHINA FOCUS A ACC HKD$");
   		System.out.println();
//		testJDBCExcel.test("colas mq$");
//		System.out.println();
//		testJDBCExcel.test("Escenarios$");
//		System.out.println();
        System.exit(0);
	}
}
