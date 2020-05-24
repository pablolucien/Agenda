package org.pclg.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

final class DbMannager {
/*
	String strUrl = "jdbc:datadirect:oracle://" + "129.158.229.21:1521;SID=ORCL9";
	String strUserId = "scott";
	String strPassword = "tiger";
	String className = "com.ddtek.jdbc.oracle.OracleDriver";
*/
//	String strUrl = "jdbc:odbc:Test";
	private static final String strUrl = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=C:\\home\\plucien\\TestRowSet\\test.mdb";
	private static final String strUserId = "";
	private static final String strPassword = "";
	private static final String driverClassName = "sun.jdbc.odbc.JdbcOdbcDriver";

	private DbMannager() {
	}

	public static void init() throws SQLException {
		try {
			Class.forName(driverClassName);
		} catch(final ClassNotFoundException ex) {
			throw new SQLException("ClassNotFoundException: " + ex.getMessage());
		}
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(strUrl, strUserId, strPassword);
	}

	public static void releaseConnection(final Connection conn) throws SQLException {
		conn.close();
	}
}