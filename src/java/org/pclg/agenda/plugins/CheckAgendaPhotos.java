package org.pclg.agenda.plugins;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


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
		
        LOGGER.warn("Starting");
		try (final Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			 final ResultSet rset = stmt.executeQuery(sql)/*;
//             final PreparedStatement pstmt = conn.prepareStatement("UPDATE root.imagen SET imagePath = ? WHERE clave = ? AND version = ?")*/) {
			while (rset.next()) {
				int ii = 0;
				final int clave = rset.getInt(++ii);
				final int version = rset.getInt(++ii);
				final String nombre = rset.getString(++ii);
				final String apellido = rset.getString(++ii);
				final String imagen = rset.getString(++ii);
				if (!new File(imagen).exists()) {
					LOGGER.warn(String.format("[%d/%d] %s %s - %s", clave, version, nombre, apellido, imagen));
//                    final int indexOf = imagen.lastIndexOf('\\');
//                    final String newPath = imagen.substring(0, indexOf) + "\\WhatsAppProfiles" + imagen.substring(indexOf);
//                    final String msg = String.format("UPDATE root.imagen SET imagen = '%s' WHERE clave = %d AND version = %d;", newPath, clave, version);
//                    LOGGER.warn(msg);
//                    ii = 0;
//                    pstmt.setString(++ii, newPath);
//                    pstmt.setInt(++ii, clave);
//                    pstmt.setInt(++ii, version);
//                    final int count = pstmt.executeUpdate();
//                    LOGGER.warn(String.format("Updated: %d", count));
				}
			}
		}
        LOGGER.warn("Ending");
	}
}
