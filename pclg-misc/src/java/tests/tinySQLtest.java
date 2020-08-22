package tests;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

class tinySQLtest {

    public static void main(final String[] argv) {

        try {
            // Register the textFileDriver.
            //
            Class.forName("ORG.as220.tinySQL.textFileDriver").newInstance();
        } catch (final InstantiationException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
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
                DriverManager.getConnection("jdbc:tinySQL", "", "");
   
            // get a Statement object from the Connection
            //
            final Statement stmt = con.createStatement();

            // Try to drop the table if it exists.
            // Ignore any exception for DROP TABLE;
            // it will most assuredly throw one if
            // the table does not exist
            //
            try {
                stmt.executeUpdate("DROP TABLE test");
            } catch (final Exception e) {
                // do nothing
            }

            // Create a table
            //
            stmt.executeUpdate("CREATE TABLE test (name CHAR(25), id INT)");
            System.err.println("Created the test table.");

            // Insert a couple of rows.
            stmt.executeUpdate("INSERT INTO test (name, id) VALUES('Brian', 1)");
            stmt.executeUpdate("INSERT INTO test (name, id) VALUES('Cletus',  2)");

            // Execute a query and get the result set.
            //
            final ResultSet rs = stmt.executeQuery("SELECT * FROM test");

            // Display column headers
            //
            System.out.println("Name                      Id ");
            System.out.println("========================= ===");

            // process each row
            //
            while(rs.next()) {

                // retrieve each column by name
                //
                final String name = rs.getString("name");
                final int    id   = rs.getInt("id");

                // display each row
                //
                System.out.println(name + " " + id);

            }

            final int cnt = stmt.getUpdateCount();
            System.out.println(cnt + " row(s) affected.");

            stmt.close();
            con.close();

        } catch( final Exception e ) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
  
