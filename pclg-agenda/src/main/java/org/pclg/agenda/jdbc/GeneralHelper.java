package org.pclg.agenda.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23/07/17 11:31
 */
final class GeneralHelper {
    static final String ORDER_BY_SECUENCIA_CLAUSE = "ORDER BY Secuencia";
    static final int LIST_INITIAL_CAPACITY = 64;
    private final Connection dbConnection;

    GeneralHelper(final Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    /**
   	 * Obtiene la próxima clave a insertar, puesto que no podemos usar seqs.
   	 * Este método lo más probable es que no funcione en un entorno multi,
   	 * pero como dice la canción: ¿A quién le importa?
   	 * @param tableName La tabla cuya próxima clave queremos
   	 * @return la nueva clave
   	 * @throws java.sql.SQLException si hay errores de acceso a la base de datos.
   	 */
   	int obtainNextKey(final CharSequence tableName) throws SQLException {
   		try (final PreparedStatement pstmt = dbConnection.prepareStatement(
   				"SELECT MAX(CLAVE) FROM " + tableName);
   			 final ResultSet resultSet1 = pstmt.executeQuery()) {
   			return resultSet1.next() ? resultSet1.getInt(1) + 1 : 1;
   		}
   	}

   	/**
   	 * Obtiene la próxima version a insertar dada una clave, puesto que no
   	 * podemos usar seqs.
   	 * Este método lo más probable es que no funcione en un entorno multi,
   	 * pero como dice la canción: ¿A quién le importa?
   	 * @param tabla La tabla en la que estamos interesados.
   	 * @param clave la clave cuya nueva version queremos.
   	 * @return la nueva version.
   	 * @throws SQLException si hay errores de acceso a la base de datos.
   	 */
   	int obtainNextVersion(final CharSequence tabla, final int clave) throws SQLException {
   		//noinspection StringBufferReplaceableByString
   		try (final PreparedStatement pstmt = dbConnection.prepareStatement(
               new StringBuilder(128).append("SELECT MAX(VERSION) FROM ")
                   .append(tabla).append(" WHERE CLAVE = ?").toString())) {
   			pstmt.setInt(1, clave);
   			final ResultSet resultSet = pstmt.executeQuery();
   			return resultSet.next() ? resultSet.getInt(1) + 1 : 1;
   		}
   	}
}
