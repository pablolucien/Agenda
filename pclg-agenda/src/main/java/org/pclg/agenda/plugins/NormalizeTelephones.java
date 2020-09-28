package org.pclg.agenda.plugins;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


/**
 * Quita al campo numero de los tel�fonos todo lo que no sean d�gitos. Paso previo al uso de
 * las mascaras en el renderer. Previo a esto hicimos:
 *
 * alter table root.telefono add numero_old VARCHAR(15);
 * update root.telefono set NUMERO_OLD = NUMERO;
 */
public class NormalizeTelephones implements Plugin {
	  private static final Logger LOGGER = LoggerFactory.makeLog4J();

	@Override
	public void execute(final Properties properties, final Connection conn, final String... args)
		    throws SQLException {
		final String sql = "SELECT numero FROM root.telefono";

		try (final Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
		        ResultSet.CONCUR_UPDATABLE);
			 final ResultSet rset = stmt.executeQuery(sql)) {
			while (rset.next()) {
				final String numero = rset.getString(1);
				rset.updateString(1, normalize(numero));
				rset.updateRow();
			}
		}
	}

	private String normalize(final String numero) {
		final int length = numero.length();
		final StringBuilder builder = new StringBuilder(length);
		for (int ii = 0; ii < length; ii++) {
			final char ch = numero.charAt(ii);
			if (Character.isDigit(ch)) {
				builder.append(ch);
			}
		}
		final String result = builder.toString();
		LOGGER.debug(numero + " -> " + result);
		return result;
	}
}
