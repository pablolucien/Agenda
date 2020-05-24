package tests;
/* INVOKE:  java QueryDbf "SELECT * FROM people"
 *
 * Test the dbfFileDriver
 *
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class QueryDbf {

  public static void main(final String[] argv) {


    // Uncomment the next line to get noisy messages
    // during the test
    //
    // java.sql.DriverManager.setLogStream(System.out);

    try {

      // load the driver
      //
      Class.forName("ORG.as220.tinySQL.dbfFileDriver").newInstance();

      // the url to the tinySQL data source
      //
      final String url = "jdbc:dbfFile:.";

      // get the connection
      //
      final Connection con = DriverManager.getConnection(url, "", "");

      // create a statement and execute a query
      //
      final Statement stmt = con.createStatement();

      String str = null;
      if (argv.length == 1) {
		  str = argv[0];
	  }
      else {
		  str = "SELECT * FROM people WHERE LAST_NAME = 'Jepson'";
	  }

      System.out.println("\nSQL: " + str + "\n");
      final long startTime = System.currentTimeMillis();
      final ResultSet rs = stmt.executeQuery(str);
      final long endTime = System.currentTimeMillis();

      final int numCols = displayResults(rs);

      final long diffTime = endTime - startTime;
      if (numCols > 0) {
		  System.out.println("\n    -> Elapsed time: " + diffTime + " msecs ["
				  + diffTime / numCols + " msecs/tuple]");
	  }
      else {
		  System.out.println("\n    -> Elapsed time: " + diffTime
				  + " msecs, no results found");
	  }

      stmt.close();
      con.close();

    } catch( final Exception e ) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }

  }

  /**
  Formatted output to stdout
  @return number of tuples
  */
  static int displayResults(final ResultSet rs) throws java.sql.SQLException
  {
    if (rs == null) {
      System.err.println("ERROR in displayResult(): No data in ResulSet");
      return 0;
    }

    int numCols = 0;

    final ResultSetMetaData meta = rs.getMetaData();
    final int cols = meta.getColumnCount();
    final int[] width = new int[cols];

    // To Display column headers
    //
    boolean first=true;
    final StringBuilder head = new StringBuilder();
    final StringBuilder line = new StringBuilder();

    // fetch each row
    //
    while (rs.next()) {

      // get the column, and see if it matches our expectations
      //
      String text = new String();
      for (int ii=0; ii<cols; ii++) {
        final String value = rs.getString(ii+1);

        if (first) {
          width[ii] = 0;
          if (value != null) {
			  width[ii] = value.length();
		  }
          if (meta.getColumnName(ii+1).length() > width[ii]) {
			  width[ii] = meta.getColumnName(ii + 1).length();
		  }

          head.append(forceToSize(meta.getColumnName(ii+1), width[ii], " "));
          head.append(" ");
          line.append(forceToSize(null, width[ii], "="));
          line.append(" ");
        }

        text += forceToSize(value, width[ii], " ");
        text += " ";   // the gap between the columns
      }

      if (first) {
        System.out.println(head.toString());
        System.out.println(line.toString());
        first = false;
      }
      System.out.println(text);
      numCols++;
    }

    return numCols;
  }

  /**
  Cut or padd the string to the given size
  @param a string
  @param size the wanted length
  @param padChar char to use for padding (must be of length()==1!)
  @return the string with correct lenght, padded with pad if necessary
  */
  private static String forceToSize(final String str, final int size,
	  final String padChar)
  {
    if (str != null && str.length() == size) {
		return str;
	}

    final StringBuffer tmp;
    if (str == null) {
		tmp = new StringBuffer(size);
	}
    else {
		tmp = new StringBuffer(str);
	}

    if (tmp.length() > size) {
      return tmp.toString().substring(0, size);  // do cutting
    }
    else {
      // or add some padding to the end of the string
      final StringBuilder pad = new StringBuilder(size);
      final int numBlanks = size - tmp.length();
      for (int p = 0; p < numBlanks; p++) {
        pad.append(padChar);
      }
      return tmp.append(pad).toString();
    }
  }


}

