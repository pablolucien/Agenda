package org.pclg.agenda.plugins;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase temporal para cambiar los charsets de la base de datos.
 * @author Pablo
 * @since 19/04/14 18:23
 */
class CharsetUpdater implements Plugin {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private Charset defaultCharset;


	@Override
	public void execute(final Properties properties, final Connection conn, final String... args) throws SQLException {
		final String charsetName = "IBM850";
		try {
			defaultCharset = Charset.forName(charsetName);
		} catch (final Exception e) {
			LOGGER.warn("Error getting defaultCharset " + charsetName
				+ ". No translation will be made", e);
			return;
		}
		updateCharsetContactos(conn);
		updateCharsetDirecciones(conn);
		updateCharsetPaises(conn);
	}

	private void updateCharsetContactos(final Connection dbConnection) throws SQLException {
		try (final PreparedStatement selectStmt = dbConnection.prepareStatement(
			"SELECT Clave, Version, Nombre, Apellido, Notas"
				+ " FROM CONTACTOS");
			 PreparedStatement updateStmt = dbConnection.prepareStatement(
				"UPDATE CONTACTOS SET Nombre = ?, Apellido = ?, Notas = ? "
					+ "WHERE Clave = ? AND Version = ?")) {
			final ResultSet resultSet = selectStmt.executeQuery();
			while (resultSet.next()) {
				updateStmt.setString(1, convertCharset(resultSet.getString(3)));
				updateStmt.setString(2, convertCharset(resultSet.getString(4)));
				updateStmt.setString(3, convertCharset(resultSet.getString(5)));
				updateStmt.setInt(4, resultSet.getInt(1));
				updateStmt.setInt(5, resultSet.getInt(2));
				updateStmt.executeUpdate();
			}
			defaultCharset = null;
		}
	}

	private void updateCharsetPaises(final Connection dbConnection) throws SQLException {
		try (PreparedStatement selectStmt = dbConnection.prepareStatement(
			"SELECT Nombre, CODIGO FROM PAISES");
			 PreparedStatement updateStmt = dbConnection.prepareStatement(
				"UPDATE PAISES SET Nombre = ? WHERE CODIGO = ?")) {
			final ResultSet resultSet = selectStmt.executeQuery();
			while (resultSet.next()) {
				updateStmt.setString(1,
					convertCharset(resultSet.getString(1)));
				updateStmt.setString(2, resultSet.getString(2));
				updateStmt.executeUpdate();
			}
			defaultCharset = null;
		}
	}

	private void updateCharsetDirecciones(final Connection dbConnection) throws SQLException {
		try (PreparedStatement selectStmt = dbConnection.prepareStatement(
			"SELECT Direccion, Clave, Version, Secuencia FROM DIRECCION");
			 PreparedStatement updateStmt = dbConnection.prepareStatement(
				"UPDATE DIRECCION SET Direccion = ? "
					+ "WHERE Clave = ? AND Version = ? AND Secuencia = ?")) {
			final ResultSet resultSet = selectStmt.executeQuery();
			while (resultSet.next()) {
				updateStmt.setString(1, convertCharset(resultSet.getString(1)));
				updateStmt.setInt(2, resultSet.getInt(2));
				updateStmt.setInt(3, resultSet.getInt(3));
				updateStmt.setInt(4, resultSet.getInt(4));
				updateStmt.executeUpdate();
			}
			defaultCharset = null;
		}
	}

	/**
	 * Convierte los strings con caracterers "raros" del formato de Clarion.
	 * (Probablemente lo mejor ser�a hacer esto de una vez por todas en
	 * la BD)
	 * @param string a convertir.
	 * @return string convertido.
	 */
	private String convertCharset(final String string) {
		return defaultCharset == null ? string :
			string == null ? null :
		    new String(string.getBytes(), defaultCharset);
	}
}
