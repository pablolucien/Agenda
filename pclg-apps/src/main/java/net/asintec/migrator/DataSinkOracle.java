// ********************* package
package net.asintec.migrator;


// ********************* imports

import org.pclg.tools.ToolBox;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.pclg.tools.StringTools.EMPTY_STRING_ARRAY;

/**
 * DataSinkOracle
 *
 * @author El Coyote Cojo
 * @version 2001.dec.27 12:52:07, CEST
 */
public abstract class DataSinkOracle implements DataSource, MigratorConstants {
    // ********************* Variables de clase
	/** Fuente de datos */
    private Connection srcConnection;

	/** El acceso a los registros */
    private ResultSet dataResultSet;

	/** Los campos seleccionados para el query */
    private String[] selectedFields;

	/** La tabla seleccionada para trabajar */
    private String selectedTable;

	/** Los nombres de las tablas de la base de datos */
    private String[] tables;

	/** Los nombres de los campos de la tabla seleccionada */
    private String[] fieldNames;

	/** Los tipos de los campos de la tabla */
    private int[] fieldTypes;

	/** Indica si el campo es un indice sin duplicados */
    private boolean[] uniqueFields;

	/** El nombre de usuario para acceder a la base de datos */
    private final String user = "telespace";

	/** La contrase�a para acceder a la base de datos */
    private final String pass = "tXj208pM";

    private final String serverIP = "192.168.0.101";
    private final String serverPort = "1521";
    private final String databaseName = "orcl";

    /**
     * Indica la base de datos con la que se desea trabajar. Cada implementacion sabr� qu� debe hacer
     *
     * @param databaseName Un identificador de la base de datos con la que se desea trabajar
     * @param password     La contrase�a a usar
     */
    @Override
    public void setDatabase(final String databaseName, final String password) {
    }

    /**
     * Devuelve la Connection a la base de datos con la que se est� trabajando
     *
     * @return la Connection a la base de datos con la que se est� trabajando
     * @since 2002.06.14
     */
    @Override
    public java.sql.Connection getConnection() {
        return srcConnection;
    }

    /**
     * Cierra la base de datos. Cada implementacion sabr� qu� debe hacer
     */
    @Override
    public void closeDatabase() {
    }

    /**
     * Abre la base de datos. Cada implementacion sabr� qu� debe hacer
     *
     * @return un identificador de la base de datos que abri� (a t�tulo de informaci�n)
     */
    @Override
    public void openDatabase() {
        try {
            // Cargar el driver Oracle JDBC
            // 1ra forma
            //Driver theDriver = new oracle.jdbc.driver.OracleDriver();

            // 2da forma
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // 3ra forma
            //Driver theDriver = (Driver) Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            //DriverManager.registerDriver(theDriver);

            // Mostrar la informacion del driver
            //System.out.println("Usando " + theDriver.toString()
            //	+ " version " + theDriver.getMajorVersion() + "." + theDriver.getMinorVersion()
            //	+ (theDriver.jdbcCompliant()? " ": " NOT ") + "jdbc Compliant");

            final String connectString = "jdbc:oracle:thin:" + user + "/" + pass + "@" + serverIP + ":" + serverPort + ":" + databaseName;
            final String catalogQuery = "select * from cat";


            // Conectarse a la database
            srcConnection = DriverManager.getConnection(connectString);

            // Cerrar la base de datos al salir
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    srcConnection.close();
                } catch (final SQLException ex) {
                    ToolBox.showInfo(ex);
                }
            }));


            // Crear un Statement
            final Statement stmt = srcConnection.createStatement();

            // Ver las tablas que hay en el tablespace
            final ResultSet dbMetaData = stmt.executeQuery(catalogQuery);

            final ResultSetMetaData tableInfo = dbMetaData.getMetaData();
//			int columnCount = tableInfo.getColumnCount();

            final List<String> tableVector = new ArrayList<>();
            final int interestingCol = 1;
            while (dbMetaData.next()) {
                // Agregar los nombres de las tablas del catalogo
                final String tableName = dbMetaData.getString(interestingCol)/* + " " + dbMetaData.getString(4)*/;
                tableVector.add(tableName);
            }
            tables = tableVector.toArray(EMPTY_STRING_ARRAY);
        } catch (final SQLException | ClassNotFoundException ex) {
            ToolBox.showInfo(ex);
        }
    }

    /**
     * Abre una tabla de la base de datos
     * <BR> <b> Este m�todo est� tomado directamente de DatabaseSelectorTarget. Habr�a que quitarlo de all� </b> <BR>
     *
     * @param tableName El nombre de la tabla que se desea abrir
     */
    @Override
    public void openTable(final String tableName) {
//System.out.println(getClass().getName() + ".openTable() " + tableName);
        try {
            final Statement stmt = srcConnection.createStatement();
            final ResultSet metaData;

            // 2002.05.20 Access permite espacios en los nombres
            if (tableName.indexOf(' ') == -1) {
                metaData = stmt.executeQuery(
                    "select * from " + tableName + " where 1 = 0");
            } else {
                metaData = stmt.executeQuery(
                    "select * from \"" + tableName + "\" where 1 = 0");
            }

            final ResultSetMetaData tableInfo = metaData.getMetaData();        // ????? getMetaData() lo vuelvo a llamar despues. REVISAR
            final int columnCount = tableInfo.getColumnCount();
            fieldTypes = new int[columnCount];
            fieldNames = new String[columnCount];                        // Utilizado luego para los indices

            // Agregar los nombres de los campos de la tablas
            for (int i = 0; i < columnCount; i++) {
                final String fieldName = tableInfo.getColumnName(i + 1);
                fieldNames[i] = fieldName;                                    // Utilizado luego para los indices
                fieldTypes[i] = tableInfo.getColumnType(i + 1);
//System.out.println(getClass().getName() + ".openTable() " + fieldNames[i] + " " + fieldTypes[i] + " " + tableInfo.getColumnTypeName(i + 1));
                final Object[] row = new Object[5];
                row[0] = fieldName;
            }

            // Los indices unicos
            uniqueFields = new boolean[columnCount];
            for (int i = 0; i < columnCount; i++) {
                uniqueFields[i] = false;
            }

            final DatabaseMetaData databaseInfo = srcConnection.getMetaData();
            final ResultSet rsetIndex = databaseInfo.getIndexInfo(null, null, tableName, true, false);
            while (rsetIndex.next()) {
                final String indexName = rsetIndex.getString(9);
                if (indexName == null) {
                    continue;
                }
                for (int i = 0; i < columnCount; i++) {
                    if (indexName.equalsIgnoreCase(fieldNames[i])) {
                        uniqueFields[i] = true;
                        break;
                    }
                }
            }
            rsetIndex.close();
//ArrayTools.printArray(System.out, uniqueFields);

/*
                // De momento no hago nada, s�lo ver si el driver tiene la capacidad de darme esta informaci�n
				try {
					ResultSet rsetPrimaryKeys = databaseInfo.getPrimaryKeys(null, null, tableName);
					System.err.println("El driver tiene la capacidad de darme getPrimaryKeys()");
					rsetPrimaryKeys = databaseInfo.getImportedKeys(null, null, tableName);
					System.err.println("El driver tiene la capacidad de darme getImportedKeys()");
					rsetPrimaryKeys = databaseInfo.getExportedKeys(null, null, tableName);
					System.err.println("El driver tiene la capacidad de darme getExportedKeys()");
				}
				catch(SQLException ex) {
					PCLGTools.trace(ex, "El driver NO tiene la capacidad de darme getPrimaryKeys()");
					//ToolBox.showInfo(ex);
				}
*/

        } catch (final SQLException ex) {
            ToolBox.showInfo(ex);
        }
    }

    /**
     * @return Una descripcion lo que puede hacer esta clase
     */
    @Override
    public String getDescription() {
        final String description = getClass().getName()
            + "\n\t Manejo de base de datos Oracle";
        return description;
    }

    /**
     * @return Un arreglo con los nombres de las tablas en la base de datos
     */
    @Override
    public String[] getTables() {
        return tables;
    }

    /**
     * @return Un arreglo con los nombres de los campos de una tabla
     */
    @Override
    public String[] getFieldNames() {
        final String[] dummy = new String[fieldNames.length];
        System.arraycopy(fieldNames, 0, dummy, 0, fieldNames.length);
        return dummy;
    }

    /**
     * @return Un arreglo con los tipos de los campos de una tabla
     */
    @Override
    public int[] getFieldTypes() {
        final int[] dummy = new int[fieldTypes.length];
        System.arraycopy(fieldTypes, 0, dummy, 0, fieldTypes.length);
        return dummy;
    }

    /**
     * Devuelve los campos que son indices sin duplicados
     */
    @Override
    public boolean[] getUniqueFields() {
        return uniqueFields;
    }

    /**
     * Indica sobre que tabla vamos a trabajar
     */
    @Override
    public void setSelectedTable(final String table) {
        selectedTable = table;
        openTable(table);
    }

    /**
     * Indica sobre que campos vamos a trabajar
     */
    @Override
    public void setSelectedFields(final String[] fields) {
        selectedFields = fields;
    }

    /**
     * Agrega un registro a la base de datos.
     *
     * @return true si la operaci�n fu� exitosa
     */
    @Override
    public boolean addRecord(final Object[] record) {
        return false;
    }
}
