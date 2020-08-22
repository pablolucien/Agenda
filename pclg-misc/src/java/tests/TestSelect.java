package tests;/*
 * We should forget about small efficiencies, say about 97% of the time:
 * Premature optimization is the root of all evil. - Donald Knuth
 *
 * Creado el 13-Aug-2008
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * 
 * @author Un autor en busca de personajes.
 * @since 02-Jan-2009
 */
final class TestSelect {
    private static final int COUNT_2_COMMIT = 10000;
	private static final int MILLISECONDS = 1000;
	private static final Logger LOGGER = Logger.getLogger("tests.TestSelect");
	private static final Properties APP_PROPERTIES = new Properties();
	static {
	    APP_PROPERTIES.put("driverClassName", "COM.ibm.db2.jdbc.app.DB2Driver");
	    APP_PROPERTIES.put("dbURL", "jdbc:db2:arsystem");
	    APP_PROPERTIES.put("dbUser", "remdata");
	    APP_PROPERTIES.put("dbPwd", "remdata1");
	    APP_PROPERTIES.put("tabla", "REMDES.T408");
	    APP_PROPERTIES.put("campos", "C230000010 C240000007 C240000008 C240000012 "
	    		+ "C260000503");
	    APP_PROPERTIES.put("showCompanies", "true");
	    APP_PROPERTIES.put("showTimes", "false");
	    APP_PROPERTIES.put("showClobMaxLen", "false");
	    
	    LOGGER.setUseParentHandlers(false);
	    LOGGER.addHandler(new Handler() {

            @Override
			public void publish(final LogRecord record) {
                System.out.println("]]]]]]]]]]] " + record.getMessage());
            }

            @Override
			public void flush() {
            }

            @Override
			public void close() throws SecurityException {
            }
	        
	    });
	    final Handler[] handlers = LOGGER.getHandlers();
	    final XFormatter formatter = new XFormatter();
	    for (int ii = 0; ii < handlers.length; ii++) {
	        handlers[ii].setFormatter(formatter);
	    }
	}

	/**
	 * 
	 * @author Un autor en busca de personajes.
	 * @since 02-Jan-2009
	 */
    private static final class CountKeeper {
        private final String name;
        private int maxLen;
        private int minLen = Integer.MAX_VALUE;
        private int totLen;
        private int totalNotNull;
        private int totRecords;

        private double avg;
        private double sum;
	    private static final MessageFormat messageFormat = new MessageFormat(
	            "{0} -> Total records: {1}, maxLen: {2}, minLen: {3}, "
	            + "not null: {4}, Avg: {5}, Std dev: {6}");

        CountKeeper(final String name) {
            this.name = name;
        }

        /**
         * @param rs
         * @throws SQLException
         */
        void update(final ResultSet rs) throws SQLException {
            totRecords++;
            final String str = rs.getString(name);
            if (str != null) {
	            final int len = str.length();
	            totLen += len;
	            if (len > maxLen) {
	                maxLen = len;
	            }
	            if (len < minLen) {
	                minLen = len;
	            }
	            final double newavg = avg + (len - avg) / (totalNotNull + 1);
	            sum += (len - avg) * (len - newavg);
	            avg = newavg;
	            totalNotNull++;
            }
        }

        /**
         *
         */
    	void print() {
    	    if (minLen == Integer.MAX_VALUE) {
    	        minLen = 0;
    	    }

    	    final Object[] args = {name, new Integer(totRecords), new Integer(maxLen),
    	            new Integer(minLen), new Integer(totalNotNull), 
    	            new Integer((totalNotNull == 0 ? 0 : totLen / totalNotNull)), 
    	            new Double(Math.sqrt(sum / (totalNotNull - 1)))
    	    };
    	    LOGGER.info(messageFormat.format(args));
        }
    }

    /**
     * 
     * @author Un autor en busca de personajes.
     * @since 02-Jan-2009
     */
    private static final class XFormatter extends Formatter {
        /*
         * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
         */
        @Override
		public String format(final LogRecord record) {
            System.out.println(">>>>>>>>>> " + record.getMessage());
            return record.getMessage();
        }
        
    }
    
	private TestSelect() {
	}

	/**
	 * @param args
	 */
    public static void main(final String[] args) {
        try {
            final String driverClassName = APP_PROPERTIES
            	.getProperty("driverClassName");
            final String dbURL = APP_PROPERTIES.getProperty("dbURL");
            final String user = APP_PROPERTIES.getProperty("dbUser");
            final String pwd = APP_PROPERTIES.getProperty("dbPwd");
            Class.forName(driverClassName);
            final Connection con = DriverManager.getConnection(dbURL,
                    user, pwd);
            if (Boolean.valueOf(APP_PROPERTIES.getProperty("showCompanies")).booleanValue()) {
                showCompanies(con);
            }
            if (Boolean.valueOf(APP_PROPERTIES.getProperty("showTimes")).booleanValue()) {
                showTimes(con);
            }
            if (Boolean.valueOf(APP_PROPERTIES.getProperty("showClobMaxLen")).booleanValue()) {
                showClobMaxLen(con);
            }
            con.close();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param con
     * @throws SQLException
     */
    private static void showClobMaxLen(final Connection con) throws SQLException {
        final String tabla = APP_PROPERTIES.getProperty("tabla");
        final String[] fields = APP_PROPERTIES.getProperty("campos").split(" ");
        String query = "SELECT ";
        final CountKeeper[] countKeepers = new CountKeeper[fields.length];
        for (int ii = 0; ii < fields.length; ii++) {
            countKeepers[ii] = new CountKeeper(fields[ii]);
            query += fields[ii] + (ii < fields.length - 1 ? ", " : "");
        }
        query += " FROM " + tabla;// + " FETCH FIRST 30 ROWS ONLY";

        // Es necesario hacer commit periodicamente para evitar la excepción:
        // COM.ibm.db2.jdbc.DB2Exception: [IBM][CLI Driver][DB2/6000] SQL0429N
        // The maximum number of concurrent LOB locators has been exceeded.
        // SQLSTATE=54028
        con.setAutoCommit(false);
        int count = 0;
        final Statement statement = con.createStatement();
        final ResultSet rs = statement.executeQuery(query);
        try {
	        while (rs.next()) {
	            count++;
	            for (int ii = 0; ii < countKeepers.length; ii++) {
	                countKeepers[ii].update(rs);
	            }
	            if (count == COUNT_2_COMMIT) {
	                con.commit();
	                count = 0;
	            }
	        }
        } finally {
            for (int ii = 0; ii < countKeepers.length; ii++) {
                countKeepers[ii].print();
            }
            con.commit();
	        rs.close();
	        statement.close();
        }
    }


    /**
     * @param con
     * @throws SQLException
     */
    private static void showTimes(final Connection con) throws SQLException {
        final String query = "SELECT C1, C2, C3, C6 FROM REMDES.T408 WHERE c1 IN("
        	+ "'HD0000000095312', 'HD0000000096598', "
        	+ "'HD0000000096792', 'HD0000000096798', 'HD0000000096978', "
        	+ "'HD0000000096979', 'HD0000000096981', 'HD0000000097103')";
        final Statement statement = con.createStatement();
        final ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            final String f1 = rs.getString(1);
            final String f2 = rs.getString(2);
            final long f3 = rs.getLong(3) * MILLISECONDS;
            final long f4 = rs.getLong(4) * MILLISECONDS;
            LOGGER.info("cod: " + f1 + ", desc: " + f2
            	+ ", creaci¢n: " + new Timestamp(f3)
            	+ ", modificaci¢n " + new Timestamp(f4));
        }
        rs.close();
        statement.close();
    }


    /**
     * @param con
     * @throws SQLException
     */
    private static void showCompanies(final Connection con) throws SQLException {
        final String query = "SELECT C600000400, C600000402 FROM REMDES.T961";
        final Statement statement = con.createStatement();
        final ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            final String f1 = rs.getString(1);
            final String f2 = rs.getString(2);
            LOGGER.info("cod: " + f1 + ", desc: " + f2);
        }
        rs.close();
        statement.close();
    }
}
