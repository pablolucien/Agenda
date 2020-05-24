package org.pclg.agenda.plugins;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 11/02/17 11:11
 */
public final class TipoTelefonoUpdater implements Plugin {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    @Override
    public void execute(final Properties properties, final Connection conn, final String... args) throws SQLException {
        try {
            updateTipoTelefono(conn, args[0]);
        } catch (final IOException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
    }

    /**
   	 * Actualiza lo tipos de los teléfonos a partir de una lista de tipo
   	 * <nro de teléfono>:<tipo> contenida en un fichero (uno por línea).
   	 *
   	 *
     *
     * @param dbConnection la conexión a usar.
     * @param fileName nombre del fichero.
   	 * @throws java.io.IOException  si hay errores de I/O.
	 * @throws SQLException si hay errores de acceso a la base de datos.
   	 *
   	 * @ throws IOException si problemas de IO :)
   	 * @ throws SQLException si problemas de SQL :)
   	 */
	private static void updateTipoTelefono(final Connection dbConnection, final String fileName)
            throws IOException, SQLException {
		final String sql = "UPDATE TELEFONOS SET Tipo = ? WHERE Numero = ? "
			+ "AND Version = (SELECT MAX(Version) FROM TELEFONOS WHERE Numero = ?)";
		try (final PreparedStatement stmt = dbConnection.prepareStatement(sql);
			 final BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = reader.readLine()) != null) {
				final String[] data = line.split(":");

				if (data.length == 2) {
                    LOGGER.debug(data[0] + " ---- " + data[1]);
					stmt.setInt(1, Integer.parseInt(data[1]));
					stmt.setString(2, data[0]);
					stmt.setString(3, data[0]);
                    stmt.execute();
				} else {
					LOGGER.warn("Wrong data: " + line);
				}
			}
            dbConnection.commit();
		}
   	}
}
