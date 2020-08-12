package org.pclg.agenda.plugins;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

import static org.pclg.tools.PropertiesHelper.getStringFromProperties;
 

public class CheckAgendaPhotos implements Plugin {
	  private static final Logger LOGGER = LoggerFactory.makeLog4J();

	@Override
	public void execute(final Properties properties, final Connection conn, final String... args)
		    throws SQLException {
		final String sql = "SELECT DISTINCT C.Clave, C.Version, C.Nombre, C.Apellido, I.imagePath"
				+ " FROM root.contacto C"
				+ " INNER JOIN root.imagen I"
				+ " ON C.Clave = I.Clave AND C.VersionImagen = I.Version"
				+ " ORDER BY C.Clave, C.Version";
				
		final String imagesRoot = Objects.requireNonNull(getStringFromProperties(properties, "Agenda.images.root"), "The property 'Agenda.images.root' is missing");
		
        LOGGER.log(Level.OFF, "Starting");
		try (final Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			 final ResultSet rset = stmt.executeQuery(sql)) {
			while (rset.next()) {
				int ii = 0;
				final int clave = rset.getInt(++ii);
				final int version = rset.getInt(++ii);
				final String nombre = rset.getString(++ii);
				final String apellido = rset.getString(++ii);
				final String imagen = imagesRoot + rset.getString(++ii);
				if (!new File(imagen).exists()) {
					LOGGER.log(Level.OFF, String.format("[%d/%d] %s %s - %s", clave, version, nombre, apellido, imagen));
				}
			}
		}
        LOGGER.log(Level.OFF, "Ending");
	}
}
