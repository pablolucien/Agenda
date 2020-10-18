package org.pclg.agenda.plugins;

import org.apache.logging.log4j.Level;
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
import java.util.Properties;


public class PopulateThumbnails implements Plugin {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final String BASE_SELECT = "SELECT C.clave, C.versionImagen, I.imagePath "
		+ "FROM root.CONTACTO C LEFT JOIN root.IMAGEN I "
		+ "ON I.clave = C.clave AND I.version = C.versionImagen ";
	private static final String WHERE_CLAVE = "WHERE C.clave = ?";
	private static final String WHERE_CLAVE_AND_VERSION = "WHERE C.clave = ? AND C.version = ?";
	private static final String UPDATE = "UPDATE root.IMAGEN SET thumbnail = ? WHERE clave = ? AND version = ?";

	@Override
	public void execute(final Properties properties, final Connection conn, final String... args) throws SQLException {
		final String imagesRoot = properties.getProperty("Agenda.images.root");
		final String select;
		switch (args.length) {
			case 0:
				select = BASE_SELECT;
				break;
			case 1:
				select = BASE_SELECT + WHERE_CLAVE;
				break;
			case 2:
				select = BASE_SELECT + WHERE_CLAVE_AND_VERSION;
				break;
			default:
				throw new IllegalArgumentException("Usage is: " + getInfo());
		}
		LOGGER.log(Level.OFF, "start with query: " + select);
		try (final PreparedStatement selectStmt = conn.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			 final PreparedStatement updateStmt = conn.prepareStatement(UPDATE)) {
			if (args.length == 1) {
				selectStmt.setString(1, args[0]);
			} else if (args.length == 2) {
				selectStmt.setString(1, args[0]);
				selectStmt.setString(2, args[1]);
			}
			final ResultSet rset = selectStmt.executeQuery();
			final ThumbnailCreator thumbnailCreator = new ThumbnailCreator();
			while (rset.next()) {
				int ii = 0;
				final int clave = rset.getInt(++ii);
				final int version = rset.getInt(++ii);
				final String imagePath = rset.getString(++ii);
				final File imageFile = new File(imagesRoot + imagePath);
				if (imageFile.exists()) {
					LOGGER.debug("creating thumbnail for " + imageFile);
					ii = 0;
					try (final InputStream stream = thumbnailCreator.getThumbnailAsStream(imageFile)) {
						updateStmt.setBlob(++ii, stream);
						updateStmt.setInt(++ii, clave);
						updateStmt.setInt(++ii, version);
						final long count = updateStmt.executeUpdate();
						assert count == 1;
					}
				} else {
					LOGGER.warn(imageFile + " doesn't exist!");
				}
			}
		} catch (final IOException ex) {
			LOGGER.error(LoggerFactory.ERROR_TAG, ex);
		} finally {
			LOGGER.log(Level.OFF, "end");
		}
	}

	@Override
	public String getInfo() {
		return getClass().getName() + " [clave [version]]";
	}
}
