package org.pclg.agenda.plugins;

import org.apache.log4j.Logger;
import org.pclg.annotations.QuickAndDirty;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

@QuickAndDirty
public class FindPhotosPlugin implements Plugin {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	private static final String SELECT_FROM_IMAGEN_SENTENCE =
        "SELECT Imagen FROM IMAGEN";

	/**
	 * @param properties
	 * @param conn la conexion a la base de datos para leer las imágenes.
	 * @param args contiene la unidad donde leer la imagen, si no existe la
	 */
	@Override
	public void execute(final Properties properties, final Connection conn, final String... args)
			throws SQLException {
		// org.pclg.agenda.plugins.FindPhotosPlugin U:
		LOGGER.info("FindPhotosPlugin.execute() " + (args.length > 0 ? args[0] : ""));
		try (final PreparedStatement stmt = conn.prepareStatement(
				SELECT_FROM_IMAGEN_SENTENCE)) {
			final ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				checkAndCopy(rset.getString(1), args[0]);
			}
		}
	}

	private void checkAndCopy(final String name, final String targetDisk) {
		LOGGER.info(name);
		final File file1 = new File(name);
		if (!file1.exists()) {
            LOGGER.warn(file1 + " doesn't exists");
			final File file2 = new File(targetDisk + name.substring(2));
			if (file2.exists()) {
				try {
					FileTools.copyFile(file2, file1);
				} catch (final IOException ex) {
                    LOGGER.error("Error", ex);
				}
			} else {
                LOGGER.warn(file2 + " also doesn't exists");
            }
		}
	}
}
