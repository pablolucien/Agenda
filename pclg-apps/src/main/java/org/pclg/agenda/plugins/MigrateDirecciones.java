package org.pclg.agenda.plugins;

import org.apache.log4j.Logger;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.log.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


/**
 * Migra las direcciones del formato antiguo en un solo tolete a varios pedazos pequeños.
 * Previo a esto hicimos:
 * rename table root.DIRECCION to DIRECCION_OLD;
 * y al ejecutar la aplicación se genera la tabla con el nuevo tamaño de tolete.
 */
public class MigrateDirecciones implements Plugin {
	  private static final Logger LOGGER = LoggerFactory.makeLog4J();

	@Override
	public void execute(final Properties properties, final Connection conn, final String... args)
		    throws SQLException {
		final String sqlSelect = "SELECT Clave, Version, Direccion FROM root.Direccion_old";
		final String sqlInsert = "INSERT INTO root.Direccion (Clave, Version, Secuencia, Direccion) VALUES (?, ?, ?, ?) ";

		try (final Statement stmtSelect = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
		        ResultSet.CONCUR_UPDATABLE);
			 final ResultSet rset = stmtSelect.executeQuery(sqlSelect);
			 final PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
			while (rset.next()) {
				int ii = 0;
				final int clave = rset.getInt(++ii);
				final int version = rset.getInt(++ii);
				final String direccion = rset.getString(++ii);
				final int length = direccion.length();
				final int nrChunks = (int) Math.ceil((double) length / AgendaRecord.ADDRESS_FIELD_LEN);
				LOGGER.debug("Actualizando: " + direccion);
				for (int seq = 1; seq <= nrChunks; seq++) {
					stmtInsert.setInt(1, clave);
					stmtInsert.setInt(2, version);
					stmtInsert.setInt(3, seq);
					stmtInsert.setString(4, direccion.substring((seq - 1) * AgendaRecord.ADDRESS_FIELD_LEN,
						Math.min(seq * AgendaRecord.ADDRESS_FIELD_LEN, length)));
					stmtInsert.executeUpdate();
				}
			}
		}
	}
}
