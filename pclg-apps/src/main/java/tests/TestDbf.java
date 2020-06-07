package tests;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

class TestDbf {

    private static void executeUpdate(final Statement stmt, final String sql) throws java.sql.SQLException
    {
      System.out.println("  " + sql);
      final long startTime = System.currentTimeMillis();
      stmt.executeUpdate(sql);
      System.out.println("    -> Elapsed time: "+ (System.currentTimeMillis() - startTime) + " msecs");
    }

    private static ResultSet executeQuery(final Statement stmt,
		final String sql) throws java.sql.SQLException
    {
      System.out.println("  " + sql);
      final long startTime = System.currentTimeMillis();
      final ResultSet rs = stmt.executeQuery(sql);
      System.out.println("    -> Elapsed time: "+ (System.currentTimeMillis() - startTime) + " msecs");
      return rs;
    }

    public static void main(final String[] argv) {

        try {
            // Register the textFileDriver.
            //
            Class.forName("ORG.as220.tinySQL.dbfFileDriver");
        } catch (final ClassNotFoundException e) {
            System.err.println(
                "I could not find the tinySQL classes. Did you install\n" +
                "them as directed in the README file?");
            e.printStackTrace();
        }

        try { // watch out for SQLExceptions!

            // Make a connection to the tinySQL Driver.
            //
            final Connection con =
                DriverManager.getConnection("jdbc:dbfFile:.", "", "");

            // get a Statement object from the Connection
            //
            final Statement stmt = con.createStatement();

            try {
                executeUpdate(stmt, "DROP TABLE cars");
                executeUpdate(stmt, "DROP TABLE people");
            } catch (final Exception e) {
                // do nothing
            }

            System.out.println("");
            System.out.println("CREATE TABLE cars ...");
            System.out.println("=====================");
            executeUpdate(stmt, "CREATE TABLE cars (name CHAR(25), id INT)");

            System.out.println("");
            System.out.println("CREATE TABLE people ...");
            System.out.println("=======================");
            executeUpdate(stmt, 
                "CREATE TABLE people (pe_name CHAR(25), pe_id NUMERIC(8), car_id INT)");
            System.err.println("Created the tables.");

            System.out.println("");
            System.out.println("INSERT INTO cars ...");
            System.out.println("====================");
            executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Fiat', 1)");
            executeUpdate(stmt, "insert into cars (name, id) values('Pinto', 2)");
            executeUpdate(stmt, "INSerT inTO cars (name, id) VALueS('Thing', 3)");
            executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Bug', 4)");
            executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Newport', 5)");
            executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Rangerover', 6)");
            executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Jeep', 7)");
            executeUpdate(stmt, "INSERT INTO cars (name, id) VALUES('Hummer', 8)");

            System.out.println("");
            System.out.println("INSERT INTO people ...");
            System.out.println("======================");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Irwin Garden', 1, 2)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Mr. Fiction', 2, 7)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Sal Paradise', 3, 8)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Dean Moriarty', 4, 3)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Bull Lee', 5, 7)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Jack Chip', 6, 1)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Glen Runciter', 7, 4)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Horselover Fat', 8, 2)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Gnossos Pappadopoulos', 9, 7)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Hef', 10, 6)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Matty Groves', 11, 7)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('Raoul Frodus', 12, 5)");
            executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('H‰gar Boﬂ', 12, 5)");
/*
for(int i = 100; i < 1000; i++) {
   executeUpdate(stmt, "INSERT INTO people (pe_name, pe_id, car_id) VALUES('H‰gar Boﬂ', " + i + ", 5)");
}
*/

            System.out.println("");
            System.out.println("UPDATE Mr. Garden ...");
            System.out.println("=====================");
            executeUpdate(stmt, "UPDATE people SET car_id=6 WHERE pe_name=\'Irwin Garden\'");

            // Execute a query and get the result set.
            //
            System.out.println("");
            System.out.println("SELECT cars, people ...");
            System.out.println("=======================");
            ResultSet rs = executeQuery(stmt, "SELECT pe_name, name FROM cars, people WHERE car_id = id");
            QueryDbf.displayResults(rs);

            System.out.println("");
            System.out.println("ALTER TABLE ...");
            System.out.println("===============");
            executeUpdate(stmt, "ALTER TABLE people ADD (nation CHAR(25), age INT)");
            rs = executeQuery(stmt, "SELECT * FROM people");
            QueryDbf.displayResults(rs);

            System.out.println("");
            executeUpdate(stmt, "ALTER TABLE people DROP COLUMN age");
            rs = executeQuery(stmt, "SELECT * FROM people");
            QueryDbf.displayResults(rs);

            System.out.println("");
            executeUpdate(stmt, "ALTER TABLE people DROP COL nation");
            rs = executeQuery(stmt, "SELECT * FROM people");
            QueryDbf.displayResults(rs);

            System.out.println("");
            executeUpdate(stmt, "ALTER TABLE people RENAME car_id TO carID");
            rs = executeQuery(stmt, "SELECT * FROM people");
            QueryDbf.displayResults(rs);


            System.out.println("");
            System.out.println("Checking Meta data ...");
            System.out.println("======================");
            final DatabaseMetaData dmd = con.getMetaData();

            System.out.println("");
            System.out.println("*** Type Info: [DATA_TYPE is from java.sql.Types]");
            rs = dmd.getTypeInfo();
            QueryDbf.displayResults(rs);

            System.out.println("");
            System.out.println("*** Table Info:");
            final String tableName = "%";
            // all found *.DBF files in the directory given as connection URL ...
            rs = dmd.getTables(null, null, tableName, null);
            QueryDbf.displayResults(rs);

            System.out.println("");
            System.out.println("*** Column Info:");
            final String columnName = "%";
            rs = dmd.getColumns(null, null, columnName, null);
            QueryDbf.displayResults(rs);

            stmt.close();
            con.close();

            System.out.println("\nGood bye");

        } catch( final Exception e ) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}

