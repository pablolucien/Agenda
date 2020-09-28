package org.pclg.agenda.plugins;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.media.image.ThumbnailCreator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class PopulateThumbnails implements Plugin {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	@Override
	public void execute(final Properties properties, final Connection conn, final String... args) throws SQLException {
		final String select = "SELECT I.clave, I.version, I.secuencia, I.imagen FROM root.IMAGEN I";
		final String insert = "INSERT INTO Thumbnail (Clave, Version, Secuencia, Thumbnail) VALUES (?, ?, ?, ?)";
		final String delete = "DELETE FROM Thumbnail";

		LOGGER.warn("start");
		try (final Statement selectStmt = conn.createStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			 final ResultSet rset = selectStmt.executeQuery(select);
			 final PreparedStatement insertStmt = conn.prepareStatement(insert);
			 final PreparedStatement deleteStmt = conn.prepareStatement(delete)) {
			deleteStmt.executeUpdate();
			final ThumbnailCreator thumbnailCreator = new ThumbnailCreator();
			while (rset.next()) {
				int ii = 0;
				final int clave = rset.getInt(++ii);
				final int version = rset.getInt(++ii);
				final int secuencia = rset.getInt(++ii);
				final String imagen = rset.getString(++ii);
				final File imageFile = new File(imagen);
				if (imageFile.exists()) {
					LOGGER.debug("creating thumbnail for " + imageFile);
					ii = 0;
					insertStmt.setInt(++ii, clave);
					insertStmt.setInt(++ii, version);
					insertStmt.setInt(++ii, secuencia);
					try (final InputStream stream = thumbnailCreator.getThumbnailAsStream(imageFile)) {
						insertStmt.setBlob(++ii, stream);
						final long count = insertStmt.executeUpdate();
						assert count == 1;
					}
				} else {
					LOGGER.warn(imageFile + " doesn't exist!");
				}
			}
		} catch (final IOException ex) {
			LOGGER.error(LoggerFactory.ERROR_TAG, ex);
		} finally {
			LOGGER.warn("end");
		}
	}
}
