package dbinfo;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DatabaseInfo <BR>
 * Encapsula la informacion de la base de datos.
 *
 * @author El Coyote Cojo
 * @version 2002.jun.29 14:20:51, CEST
 */
public final class DatabaseInfo {
    // ******************************** Variables de clase

    // ******************************** Variables de instancia
    /**
     * La conexion con la db.
     */
    private final Connection conn;

    /**
     * Acceso a la informacion sobre la base de datos.
     */
    private final DatabaseMetaData databaseMetaData;

    /**
     * Las tablas de la db.
     */
    private final String[] tables;

    /**
     * Almacena la informacion de las tablas de esta db.
     */
    private final Map<String,String[]> htTables = new HashMap<>();


    // ******************************** Constructores

    /**
     * Constructor por omision.
     *
     * @param conn una conexion con la base de datos.
     * @throws SQLException si hay problemas.
     * --author El Coyote Cojo
     * --version 2002.jun.29 14:20:51, CEST
     */
    public DatabaseInfo(final Connection conn) throws SQLException {

        System.err.println("Creando un DatabaseInfo\n\tDeberia haber un metodo estatico que de la informacion si ya la tengo");

        this.conn = conn;
        databaseMetaData = conn.getMetaData();
        final String[] tableTypes = {"TABLE", "SYNONYM"};		// SYNONYM = Tablas vinculadas, VIEW = Consultas almacenadas
        final ResultSet dbMetaData = databaseMetaData.getTables(null, null, "%", tableTypes);
        final List<String> tableList = new ArrayList<>();
        while (dbMetaData.next()) {
            tableList.add(dbMetaData.getString(3)/* + " " + dbMetaData.getString(4)*/);
        }
        tables = new String[tableList.size()];
        tableList.toArray(tables);
        dbMetaData.close();
    }


    // ******************************** Metodos de instancia
    /**
     * Muestra informacion sobre la base de datos.
     *
     * @param out el sitio donde debe escribir.
     * @throws SQLException si hay problemas.
     * --author El Coyote Cojo
     * --since 2002.ago.01
     */
    public void printDBMetaData(final PrintStream out) throws SQLException {
        // Mostrar la informacion del driver
//			Driver theDriver = DriverManager.getDriver(url);
        out.println("Usando " + databaseMetaData.getDriverName()
                + " version " + databaseMetaData.getDriverVersion() + '\n'
                + " version " + databaseMetaData.getDriverMajorVersion() + '.'
                + databaseMetaData.getDriverMinorVersion()
//			    + (theDriver.jdbcCompliant()? " ": " NOT ") + "JDBC Compliant.\nConectado a " + DSN
        );

        // Obtener informacion de la base de datos
        out.println("=========== Informacion de la base de datos ============");
        out.println("getURL(): " + databaseMetaData.getURL());
        out.println("DatabaseProductName: " + databaseMetaData.getDatabaseProductName());
        out.println("DatabaseProductVersion: " + databaseMetaData.getDatabaseProductVersion());
        out.println("UserName: " + databaseMetaData.getUserName());
        out.println();
        out.println("supportsStoredProcedures(): " + databaseMetaData.supportsStoredProcedures());
        out.println("supportsAlterTableWithAddColumn(): " + databaseMetaData.supportsAlterTableWithAddColumn());
        out.println("supportsAlterTableWithDropColumn(): " + databaseMetaData.supportsAlterTableWithDropColumn());
        out.println("supportsColumnAliasing(): " + databaseMetaData.supportsColumnAliasing());
        out.println("getMaxConnections(): " + databaseMetaData.getMaxConnections());
        out.println("getMaxBinaryLiteralLength(): " + databaseMetaData.getMaxBinaryLiteralLength());
        out.println("getMaxColumnsInTable(): " + databaseMetaData.getMaxColumnsInTable());
        out.println("getMaxColumnsInSelect(): " + databaseMetaData.getMaxColumnsInSelect());
        out.println("getMaxColumnsInOrderBy(): " + databaseMetaData.getMaxColumnsInOrderBy());
        out.println("getMaxColumnsInIndex(): " + databaseMetaData.getMaxColumnsInIndex());
        out.println("getMaxColumnNameLength(): " + databaseMetaData.getMaxColumnNameLength());
        out.println("getMaxRowSize(): " + databaseMetaData.getMaxRowSize());
        out.println("doesMaxRowSizeIncludeBlobs(): " + databaseMetaData.doesMaxRowSizeIncludeBlobs());
        out.println("getMaxStatements() (open statements): " + databaseMetaData.getMaxStatements());
        out.println("getMaxStatementLength(): " + databaseMetaData.getMaxStatementLength());
        out.println("getMaxTableNameLength(): " + databaseMetaData.getMaxTableNameLength());
        out.println("getMaxTablesInSelect(): " + databaseMetaData.getMaxTablesInSelect());
        out.println("getSearchStringEscape(): " + databaseMetaData.getSearchStringEscape());
        out.println("getCatalogSeparator(): " + databaseMetaData.getCatalogSeparator());
        out.println("The \"extra\" characters that can be used in unquoted \n identifier names (those beyond a-z, A-Z, 0-9 and _):\n        "
                + databaseMetaData.getExtraNameCharacters());
        out.println("supportsBatchUpdates(): " + databaseMetaData.supportsBatchUpdates());

        out.println("supportsConvert(): " + databaseMetaData.supportsConvert());
        out.println("supportsMultipleResultSets(): " + databaseMetaData.supportsMultipleResultSets());
        out.println("supportsSelectForUpdate(): " + databaseMetaData.supportsSelectForUpdate());
        out.println("supportsTransactions(): " + databaseMetaData.supportsTransactions());
        out.println("usesLocalFiles(): " + databaseMetaData.usesLocalFiles());
        out.println("usesLocalFilePerTable(): " + databaseMetaData.usesLocalFilePerTable());
        out.println();

        // Terminología del vendedor
        out.println("What's the database vendor's preferred term for \"procedure\"? " + databaseMetaData.getProcedureTerm());
        out.println("What's the database vendor's preferred term for \"schema\"? " + databaseMetaData.getSchemaTerm());
        out.println("What's the database vendor's preferred term for \"catalog\"? " + databaseMetaData.getCatalogTerm());
        out.println();

        ResultSet rset;
        try {
            out.println("   --- Catalogs ---");
            rset = databaseMetaData.getCatalogs();
            while (rset.next()) {
                out.println(rset.getString(1));
            }
            rset.close();
        } catch (final SQLException ex) {
            out.println(ex.getMessage());
        }
        out.println();

        try {
            out.println("   --- Schemas ---");
            rset = databaseMetaData.getSchemas();
            while (rset.next()) {
                out.println(rset.getString(1));
            }
            rset.close();
        } catch (final SQLException ex) {
            out.println(ex.getMessage());
        }
        out.println();
    }

    /**
     * Devuelve los nombres de las tablas de la base de datos.
     * @return Un String[] con los nombres de las tablas
     */
    public String[] getTables() {
        final String[] dummy = new String[tables.length];
        System.arraycopy(tables, 0, dummy, 0, tables.length);
        return dummy;
    }

    /**
     * Devuelve los nombres de los campos de una tabla.
     * @param tableName El nombre de la tabla
     * @return Un String[] con los nombres de los campos de una tabla
     * @throws SQLException si hay problemas.
     */
    public String[] getFields(final String tableName) throws SQLException {
//		return(((TableInfo) htTables.get(tableName)).getFields());
        String[] fields = htTables.get(tableName);
        if (fields == null) {
            final Statement stmt = conn.createStatement();
            final ResultSet metaData;
            // 2002.05.20 Access permite espacios en los nombres
            // FileMaker necesita " alrededor del nombre de la "tabla", espero que eso sea SQL estandard
            //metaData = stmt.executeQuery("select * from \"" + tableName + "\" where 1 = 0");
            metaData = stmt.executeQuery("select * from " + tableName + " where 1 = 0");

            final ResultSetMetaData tableInfo = metaData.getMetaData();
            final int columnCount = tableInfo.getColumnCount();
            fields = new String[columnCount];
            final int[] tipos = new int[columnCount];
            for (int ii = 0; ii < columnCount; ii++) {
                fields[ii] = tableInfo.getColumnName(ii + 1);
                tipos[ii] = tableInfo.getColumnType(ii + 1);
            }
            htTables.put(tableName, fields);
            metaData.close();
            stmt.close();
        }

        final String[] dummy = new String[fields.length];
        System.arraycopy(fields, 0, dummy, 0, fields.length);
        return dummy;
    }


    // ******************************** Metodos estaticos

}
