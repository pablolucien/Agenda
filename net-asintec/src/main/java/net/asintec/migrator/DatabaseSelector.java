// ******************************** package
package net.asintec.migrator;

import dbinfo.DatabaseInfo;
import org.pclg.tools.ArrayTools;
import org.pclg.tools.ToolBox;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;


/**
	@version 1.00
	@author El Coyote Cojo
*/
class DatabaseSelector extends JPanel implements MigratorConstants {
    private static final long serialVersionUID = -5861869364217578226L;
    /** La clase encargada de almacenar los datos */
	private DataSource dataSource = new DataSourceJDBC();

	/** para saber si soy origen o destino de datos */  // para determinar lo que hago, el titulo, etc.
	public static final int DST = 0;
	public static final int ORG = 1;

	/** Origen o destino (ORG o DST). */
	final int tipo;

	/** El dialogo para seleccionar la base de datos */
	private DatabaseFileChooser fileChooser;
	
	/** Usado para las QUERY AD HOC */
	private QueryDialog queryDialog;

	/** Para ver las tablas */
/*	protected*/ final DefaultListModel<String> tableListModel = new DefaultListModel<>();
	private final JList<String> tableList = new JList<>(tableListModel);

	/** La tabla seleccionada para trabajar */
	private String selectedTable;

	/** Utilizado cuando el acceso no es a una simple tabla */
	private String queryAdHoc;

	/** Los tipos de los campos de la tabla */
	private int[] tipos;

	/** Indica si el campo es un indice sin duplicados */
	private boolean[] isUnique;

	/** Fuente de datos */
	Connection srcConnection;

	/** El acceso a los registros */
	private ResultSet dataResultSet;

	/** Los tipos de los campos del dataResultSet */
	private int[] dataResultSetTypes;

	/** El numero de columnas del resultset */
	private int numCols;
	
	final String title;
	private final Migrator parent;
	final JLabel dbName = new JLabel();
	protected Object[] data;
	
	/** Los nombres de las tablas de la base de datos */
	String[] tables;

	/** Los nombres de los campos de la tabla seleccionada */
	private String[] fieldNames;

	/** Los campos seleccionados para el query */
	private String[] selectedFields;

	/** La sentencia para hacer los select */
	private String selectSentence;

	/** */
	private String user         = "Administrador";
	private String pass         = "asintec72";

	String driverName;
	private String dbURL;

	public DatabaseSelector(final Migrator parent, final String title,
			final String DSN, final int tipo) {
		this.parent = parent;
		this.title = title;
		this.tipo = tipo;

		setLayout(new BorderLayout());
		add(dbName, BorderLayout.NORTH);
		JScrollPane tableListScrollPane = new JScrollPane(tableList);
		add(tableListScrollPane, BorderLayout.CENTER);

		if(DSN != null) {
			dataSource.setDatabase(DSN, null);
			openDatabase();
		}


		tableList.addMouseListener(
			new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					if(e.getClickCount() == 2) {
						selectedTable = tableList.getSelectedValue();
						openTable(selectedTable);
					}
				}
			}
		);
	}

	/**
		Selecciona una la base de datos
		@return true si se seleccionó algo, false de lo contrario
	*/
	public boolean selectDatabase() {
		if(fileChooser == null) {
			fileChooser = new DatabaseFileChooser(new File(".").getAbsolutePath());
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		}
		if(fileChooser.showDialog(this, "Abrir " + title) == JFileChooser.APPROVE_OPTION) {
			final File selection = fileChooser.getSelectedFile();
			if(selection.exists()) {
				construirDbURL(selection.getAbsolutePath());
			}
			else {
				construirDbURL(selection.getName());
			}
			return(true);
		}
		return(false);
	}

	/**
	 * FIXME: Construye la URL de conexion según el fichero (mdb, xls, etc)
	 * seleccionado.
	 * @param name
	 */
	private void construirDbURL(final String name) {
		dataSource.setDatabase(name, null);
	}


	/**
		Abre una tabla de la base de datos
	*/
	void openTable(final String tableName) {
		final Cursor oldCursor = getCursor();
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try {
if(parent != null) {
			if(tipo == ORG) {
				parent.myGrid.comboBox.removeAllItems();
				parent.myGrid.comboBox.addItem(VACIO);
				parent.myGrid.comboBox.addItem(CONSTANTE);
				parent.myGrid.comboBox.addItem(AUTO);
				for(int j = 0; j < parent.myGrid.getRowCount(); j++) {
					parent.myGrid.setValueAt("", j, 1);
					parent.myGrid.setValueAt("", j, 2);
				}
			}
			else {
				parent.myGrid.clear();
			}
}
//            if(srcConnection != null) (p.e. Clarion
//			final Statement stmt = srcConnection.createStatement();
//			ResultSet metaData;

			if(tipo == ORG && tableName.equals(QUERY_AD_HOC)) {
				// Presentar un dialogo donde se pueda introducir el query o seleccionarlo de un fichero
				if(queryDialog == null) {
					queryDialog = new QueryDialog(parent);
				}

				queryDialog.setVisible(true);
				if(queryDialog.isAccepted()) {
					queryAdHoc = queryDialog.getText();
					dataSource.setQuery(queryDialog.getText());
//					metaData = stmt.executeQuery(queryAdHoc/* + " where 1 = 0"*/);
				}
				else {
					setCursor(oldCursor);
					return;
				}
			}
			else {
				queryAdHoc = null;
				// 2002.05.20 Access permite espacios en los nombres
				if(tableName.indexOf(' ') == -1) {
//					metaData = stmt.executeQuery(
//							"select * from " + tableName + " where 1 = 0");
				}
				else {
//					metaData = stmt.executeQuery(
//							"select * from \"" + tableName + "\" where 1 = 0");
				}
			}
/* Esto estaba en DatabaseSelectorTarget, en vez de lo que sigue
// revisar 2003.01.05
metaData = stmt.executeQuery("select * from " + tableName + " where 1 = 0");

			final ResultSetMetaData tableInfo = metaData.getMetaData();		// ????? getMetaData() lo vuelvo a llamar despues. REVISAR
			final int columnCount = tableInfo.getColumnCount();
			tipos = new int[columnCount];
			fieldNames = new String[columnCount];						// Utilizado luego para los indices
*/
			dataSource.setConnection(srcConnection);
			dataSource.openTable(tableName);
			fieldNames = dataSource.getFieldNames();
			tipos = dataSource.getFieldTypes();
			isUnique = dataSource.getUniqueFields();

			// Agregar los nombres de los campos de la tablas
			for(int ii = 0; ii < fieldNames.length; ii++) {
				if(tipo == ORG) {
					parent.myGrid.comboBox.addItem(fieldNames[ii]);
					// poner valores por omision
					for(int j = 0; j < parent.myGrid.getRowCount(); j++) {
						final String dstField = (String)parent.myGrid.getValueAt(j, 0);
						if(fieldNames[ii].equalsIgnoreCase(dstField)) {
							parent.myGrid.setValueAt(fieldNames[ii], j, 1);
							break;
						}
					}
				}
				else {
					final Object[] row = new Object[5];
					row[0] = fieldNames[ii];
if(parent != null) {
					parent.myGrid.addRow(row);
}
				}

			}

if(parent != null) {
			parent.myGrid.refresh();
}
		}
//		catch(SQLException ex) {
//			ToolBox.showInfo(ex);
//		}
		finally {
			setCursor(oldCursor);
		}
	}

	/**
		Obtiene los inices unicos de una tabla de la base de datos
		debe implementarlo el que sepa como sacar la informacion de la base de datos
		@since 2002.06.27
	*/
	protected void getUniqueIndexes() {
	}

	public Connection getConnection() {
		return srcConnection;
	}

	public String getSelectedTable() {
		if(queryAdHoc != null) {
			return (QUERY_AD_HOC + queryAdHoc);
		}
		return selectedTable;
	}

private String DSN;  // flechazo

	public void setDatabaseName(final String DSN) {
		this.DSN = DSN;
		dataSource.setDatabase(DSN, null);
	}

	public String getDatabaseName() {
		return DSN;
	}

	/**
		@return Una descripcion lo que puede hacer esta clase
	*/
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t Manejo de base de datos ODBC"
									;
		return(description);
	}


	void printException(final Throwable ex) {
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter);
		ex.printStackTrace(printWriter);
	}


	/**
		Abre la base de datos
	*/
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
			if (driverName != null && driverName.trim().length() > 0) {
				final Driver theDriver =
					(Driver) Class.forName(driverName).newInstance();

				// Mostrar la informacion del driver
				System.out.println(": Usando " + theDriver.toString()
					+ " version " + theDriver.getMajorVersion() + "." + theDriver.getMinorVersion()
					+ (theDriver.jdbcCompliant()? " ": " NOT ") + "jdbc Compliant");
			} else {
				System.out.println("Sin usar driver. Espero que la data source no lo necesite");
			}

			if (dbURL != null) {   	// FIXME: chapucilla par ver si puedo eliminar lo del else
			    connectString = dbURL;
				user = "root";
				pass = "";
			    final Properties props = new Properties();
			    props.put("user", user);
			    props.put("password", pass);
			    srcConnection = DriverManager.getConnection(connectString, props);
			}


            if(srcConnection != null) {
/* 2002.06.29 */
System.err.println("Prueba de DatabaseInfo");
final DatabaseInfo dbi = new DatabaseInfo(srcConnection);
dbi.printDBMetaData(System.err);
final String[] tables = dbi.getTables();
ArrayTools.printArray(System.err, tables);
if(tables.length > 0) {
ArrayTools.printArray(System.err, dbi.getFields(tables[0]));
}
System.err.println("FIN: Prueba de DatabaseInfo");
/* 2002.06.29 */
            }

srcConnection.setAutoCommit(true);
System.out.println("DatabaseSelector.connection.getAutoCommit(): " + srcConnection.getAutoCommit());

			dbName.setText("<HTML><FONT FACE=\"Helvetica,Arial,sans-serif\" COLOR=\"#996600\">"
				+ title + "</FONT><BR>" + "<FONT FACE=\"Helvetica,Arial,sans-serif\" COLOR=\"#006699\">" + "</FONT></HTML>");

			// Cerrar la base de datos al salir
			Runtime.getRuntime().addShutdownHook(new Thread() {
								@Override
								public void run() {
									try {
                                        if(srcConnection != null) {
										    srcConnection.close();
                                        }
									}
									catch(final SQLException ex) {
										ToolBox.showInfo(ex);
									}
								}
							});



			// Ver las tablas que hay en el tablespace
			final ResultSet DBMetaData;

			final int interestingCol;

			final DatabaseMetaData databaseInfo = srcConnection.getMetaData();
			final String[] tableTypes = {"TABLE", "SYNONYM"};		// SYNONYM = Tablas vinculadas, VIEW = Consultas almacenadas
			DBMetaData = databaseInfo.getTables(null, null, "%", tableTypes);
			interestingCol = 3;


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
    }

	/**
		@return Un arreglo con los nombres de las tablas en la base de datos
	*/
	public String[] getTables() {
		openDatabase();
		return(tables);
//		return(dataSource.getTables());
	}

	/**
		@param tabla La tabla cuyos campos queremos
		@return Un arreglo con los nombres de los campos de una tabla
	*/
	public String[] getFieldNames(final String tabla) {
		if(fieldNames == null) {
			return(null);
		}

		final String[] dummy = new String[fieldNames.length];
		System.arraycopy(fieldNames, 0, dummy, 0, fieldNames.length);
		return(dummy);
	}

	/**
		@param tabla La tabla cuyos campos queremos
		@return Un arreglo con los tipos de los campos de una tabla
	*/
	public int[] getFieldTypes(final String tabla) {
		debug("getFieldTypes()");
		if(tipos == null) {
			return(null);
		}

//????????  Devolver los tipos de los campos que hay en el grid, en el orden en que están
		final int[] dummy = new int[tipos.length];
		System.arraycopy(tipos, 0, dummy, 0, tipos.length);
		return(dummy);
	}

	/**
		@return Un arreglo que indica cuales de los campos de una tabla son indices unicos
	*/
	public boolean[] getUnique() {
		if(isUnique == null) {
			return(null);
		}

		final boolean[] dummy = new boolean[isUnique.length];
		System.arraycopy(isUnique, 0, dummy, 0, isUnique.length);
		return(dummy);
	}


	/** Indica sobre que tabla vamos a trabajar */
	public void setSelectedTable(final String table) {
		selectedTable = table;
		tableList.setSelectedValue(table, true);
		openTable(table);
	}

	/** Devuelve el siguente registro o null */
	public Object[] getNextRecord() {
		if(dataResultSet == null) {
			return (null);
		}
		try {
			if(dataResultSet.next()) {
				final Object[] data = new Object[numCols];
				for(int ii = 0; ii < numCols; ii++) {
					switch(dataResultSetTypes[ii]) {
					case Types.VARBINARY:
					case Types.LONGVARBINARY:
						data[ii] = dataResultSet.getBytes(ii + 1);
						break;
					default:
						data[ii] = dataResultSet.getString(ii + 1);
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
		return(true);
	}

	/** Establece el query a usar para acceder a la base de datos */
	public void setQuery(final String query) {
		selectSentence = query;
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
			dataResultSet = srcConnection.createStatement().executeQuery(selectSentence);
			// Obtenemos info sobre el resultado
			final ResultSetMetaData metaData = dataResultSet.getMetaData();
			// ... cuantas columnas
			numCols = metaData.getColumnCount();
			// ... y los tipos de datos
			dataResultSetTypes = new  int[numCols];
			for(int ii = 0; ii < numCols; ii++) {
				dataResultSetTypes[ii] = metaData.getColumnType(ii + 1);
			}
			return(true);
		}
		catch(final SQLException ex) {
			ToolBox.showInfo(ex);
			return(false);
		}
	}

	/**
		Cierra la base de datos. Cada implementacion sabrá qué debe hacer
	*/
	void closeDatabase() {
		dataSource.closeDatabase();
	}

	/**
		@return La clase encargada de almacenar y proveer los datos
	*/
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
		@return La clase encargada de proveer los datos
	*/
	public void setDataSource(final String dataSourceName) {
		closeDatabase();
		try {
			dataSource = (DataSource) Class.forName(dataSourceName).newInstance();
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
	}

	/**
		Muestra la sentencia de creación de una tabla
		@since 2003.09.20
	*/
	public void showCreateTable(final String tableName, final String[] fieldNames, final int[] fieldTypes,
										final String[] fieldTypeNames, final int[] fieldSizes, final String[] primaryKey) {

		final String command = getTableCreationCommand(tableName, fieldNames, fieldTypes, fieldTypeNames, fieldSizes, primaryKey);
		System.out.println(command);
	}

	/**
		Crea una tabla
	*/
	public void createTable(final String tableName, final String[] fieldNames,
			final int[] fieldTypes, final String[] fieldTypeNames,
			final int[] fieldSizes, final String[] primaryKey) {

		final String command = getTableCreationCommand(tableName, fieldNames, fieldTypes, fieldTypeNames, fieldSizes, primaryKey);
		System.out.println(command);
		try {
			final Statement stmt = srcConnection.createStatement();
			stmt.executeUpdate(command);
			stmt.close();
			openDatabase();
			setSelectedTable(tableName);
		}
		catch(final SQLException ex) {
			ToolBox.showInfo(ex);
		}
	}

	/**
		Genera la senencia SQL para crear una tabla
		@since 2003.09.20
	*/
	String getTableCreationCommand(String tableName,
		final String[] fieldNames, final int[] fieldTypes,
		final String[] fieldTypeNames, final int[] fieldSizes,
		final String[] primaryKey) {

		tableName = ToolBox.normalizeName(tableName);
		final StringBuilder command = new StringBuilder();
		command.append("CREATE TABLE ").append(tableName).append(" (\n");
		for(int ii = 0; ii < fieldNames.length; ii++) {
			command.append("\t");
			switch(fieldTypes[ii]) {
			case Types.VARCHAR:
				command.append(fieldNames[ii]).append(" ")
					.append(fieldTypeNames[ii]).append("(")
					.append(fieldSizes[ii]).append(")");
				break;
			case Types.TINYINT:
				command.append(fieldNames[ii]).append(" BOOLEAN");
				break;
			case Types.INTEGER:
				command.append(fieldNames[ii]).append(" INTEGER");
				break;
			default:
				command.append(fieldNames[ii]).append(" ").append(
					fieldTypeNames[ii]);
				break;
			}
			command.append(", \n");
		}

		command.delete(command.length() - 3, command.length());	// Eliminar una ', \n' que	me sobra.

		// Si hay clave, la agregamos
		if(primaryKey != null && primaryKey.length > 0) {
			command.append(",\n\tCONSTRAINT PK_").append(tableName).append(" PRIMARY KEY(");
			for(int ii = 0; ii < primaryKey.length; ii++) {
				command.append(primaryKey[ii]).append((ii < primaryKey.length - 1 ? ", " : ""));
			}
			command.append(")\n");
		}

		command.append(")\n");
		return command.toString();
	}

	/** */
	private void debug(final String s) {
		System.err.println(getClass().getName() + " -> " + s);
	}

	public void setDriverName(final String driverName) {
		this.driverName = driverName;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDbURL(final String dbURL) {
		this.dbURL = dbURL;
	}

	public String getDbURL() {
		return dbURL;
	}
}
