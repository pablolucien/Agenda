// ******************************** package
package org.pclg.tools;

// ******************************** imports

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Metodos utiles para manejo de bases de datos.
 * @author El Coyote cojo.
 * @version Unknown.
 * @since 2003.08.10
 */
final class DatabaseTools {
	/** Para el utilísimo "logueado". */
	private static final Logger LOGGER = Logger.getLogger("DatabaseTools");

	/**
	 * El constructor por omision es privado para evitar que a algun capullo
	 * se le ocurra hacer new DatabaseTools().
	 */
	private DatabaseTools() {
	}

	/**
	 * Devuelve la clave de una tabla de tipo codigo, descripcion,
	 * donde 'codigo' es int (o long) y descripcion es String
	 * Si el registro no existe, es insertado.
	 *
	 * @param linkField     El nombre del campo descripcion en la tabla.
	 * @param linkFieldData El contenido del campo descripcion en la tabla.
	 * @return El codigo del registro.
	 * @since 2003.08.10
	 */
	public static long getKey(final Connection conn, final String sourceTable,
			final String linkField, final String linkFieldData) throws SQLException {
		final StringBuilder queryBuffer = new StringBuilder(Constants.BUFFER_SIZE);
		queryBuffer.append("SELECT Codigo FROM ").append(sourceTable)
				.append(" WHERE ").append(linkField).append(" = ?");
		final PreparedStatement pstmtSelectRelated =
				conn.prepareStatement(queryBuffer.toString());
		pstmtSelectRelated.setString(1, linkFieldData);
		final ResultSet rset = pstmtSelectRelated.executeQuery();
		if (!rset.next()) {			// Si no está en la tabla lo insertamos
			rset.close();
			pstmtSelectRelated.close();
			queryBuffer.setLength(0);
			queryBuffer.append("INSERT INTO ").append(sourceTable).append(" (")
					.append(linkField).append(") VALUES (?)");
			final PreparedStatement pstmtInsertRelated =
					conn.prepareStatement(queryBuffer.toString());
			pstmtInsertRelated.setString(1, linkFieldData);
            try {
                pstmtInsertRelated.executeUpdate();
            } catch (final SQLException ex) {
                System.err.println("getKey(): Error insertando " + linkFieldData
                        + " en " + sourceTable);
                // getKey(): Error insertando .js;jsessionid=21FB231706FD6196A4A6F1AC34F1F76E en Extensiones
                throw ex;
            }
            pstmtInsertRelated.close();
			if (!conn.getAutoCommit()) {
				conn.commit();
			}
			// Un poco de recursividad por tocar las narices
			return getKey(conn, sourceTable, linkField, linkFieldData);
		}
		final long recCode = rset.getLong(1);
		rset.close();
		pstmtSelectRelated.close();
		return recCode;
	}

	/**
	 * Devuelve el contenido del campo descripcion de una tabla de tipo codigo,
	 * descripcion, donde 'codigo' es int (o long) y descripcion es String.
	 *
	 * @param linkField     El nombre del campo descripcion en la tabla.
	 * @param linkFieldData El contenido del campo codigo en la tabla.
	 * @return El contenido del registro.
	 * @since 2003.08.10
	 */
	public static String getDesc(final Connection conn, final String sourceTable,
			final String linkField, final long linkFieldData) throws SQLException {
		final StringBuilder queryBuffer = new StringBuilder(Constants.BUFFER_SIZE);
		queryBuffer.append("SELECT ").append(linkField).append(" FROM ")
				.append(sourceTable).append(" WHERE Codigo = ?");
		final PreparedStatement pstmtSelectRelated =
				conn.prepareStatement(queryBuffer.toString());
		pstmtSelectRelated.setInt(1, (int) linkFieldData);
		final ResultSet rset = pstmtSelectRelated.executeQuery();
		String retData = null;
		if (rset.next()) {
			retData = rset.getString(1);
		}
		rset.close();
		pstmtSelectRelated.close();
		return retData;
	}

	/**
	 *
	 * @param conn
	 * @throws SQLException
	 */
	public static void actualizaDirectorios(final Connection conn)
			throws SQLException {
		final PreparedStatement pstmtSelect = conn.prepareStatement(
				"SELECT Camino FROM Directorios ORDER BY Codigo");
		// TODO: está comenttado para no usarlo todo el tiempo
//		final ResultSet rset = pstmtSelect.executeQuery();
//		while (rset.next()) {
//			doActualizaDirectorios(conn, rset.getString(1));
//		}
//		rset.close();
		pstmtSelect.close();
	}

	/**
	 * recursivo
	 *
	 * @param conn
	 * @param dirName
	 * @throws SQLException
	 */
	private static void doActualizaDirectorios(final Connection conn,
			final String dirName) throws SQLException {
//		LOGGER.log(Level.INFO, "doActualizaDirectorios(" + dirName + "");
		final File file = new File(dirName);
		final String parentName = file.getParent();
		final PreparedStatement pstmtSelect = conn.prepareStatement(
				"SELECT Codigo FROM Directorios WHERE Camino = ?");
		final PreparedStatement pstmtUpdate = conn.prepareStatement(
				"UPDATE Directorios SET Padre = ? WHERE Camino = ?");
		pstmtSelect.setString(1, parentName);
		final ResultSet rset = pstmtSelect.executeQuery();
		if (rset.next()) {
			final int parentCode = rset.getInt(1);
			pstmtUpdate.setInt(1, parentCode);
			pstmtUpdate.setString(2, dirName);
			pstmtUpdate.executeUpdate();
		}
		rset.close();
		pstmtUpdate.close();
		pstmtSelect.close();
	}

	/**
	 * recursivo
	 *
	 * @param conn
	 * @param dirCode
	 * @param separator
	 * @return el path completo
	 * @throws SQLException
	 */
	private static String getFullPath(final Connection conn,
			final int dirCode, final String separator) throws SQLException {
		final PreparedStatement pstmtSelect = conn.prepareStatement(
				"SELECT Camino, Padre FROM DirectoriosNew WHERE Codigo = ?");
		pstmtSelect.setInt(1, dirCode);
		final ResultSet rset = pstmtSelect.executeQuery();
		if (!rset.next()) {
			throw new SQLException("El codigo " + dirCode + " no está en la base de datos");
		}
		String camino = rset.getString(1);
		int parentCode = rset.getInt(2);	// verificar null: if the value is SQL NULL, the value returned is 0
		if (rset.wasNull()) {
			parentCode = -1;
		}
		rset.close();
		pstmtSelect.close();

		// si este señor tiene padre, hay que buscarlo
		if (parentCode != -1) {
			camino = getFullPath(conn, parentCode, separator) + separator + camino;
		}

		// assert camino != null
		return camino;
	}
}
