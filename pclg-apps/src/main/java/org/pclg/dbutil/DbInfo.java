package org.pclg.dbutil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

final class DbInfo {

	private DbInfo() {
	}
	
	public static void showDataTypes(final Connection conn) throws SQLException {
		final String format = "%-20s%10s%10s%10s %-20s\n";
		System.out.printf(format, "Tipo", "Precision", "Min scale", "Max scale",
			"Create Params");
		final DatabaseMetaData metaData = conn.getMetaData();
		final ResultSet typesSet = metaData.getTypeInfo();
		while (typesSet.next()) {
			System.out.printf(format, typesSet.getString(1), typesSet.getInt(3),
				typesSet.getInt(14), typesSet.getInt(15), typesSet.getString(6));
		}
		typesSet.close();
	}
}