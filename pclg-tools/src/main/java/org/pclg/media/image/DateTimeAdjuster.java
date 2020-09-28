package org.pclg.media.image;

import org.apache.logging.log4j.Logger;
import org.pclg.dbutil.VersatileConnection;
import org.pclg.log.LoggerFactory;
import org.pclg.xtras.ClassPathHacker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

import static org.pclg.Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;

/**
 * Class tu adjust the dates and times of the images (it can be used in other files) when these
 * are changed by Windoze when one rotates or otherwise manipulates the.
 *
 * @author El Coyote Cojo
 * @since 6/08/16 17:38
 */
final class DateTimeAdjuster {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	private DateTimeAdjuster() {
		try {
			/* Propiedades persistentes. */
			final Properties properties = new Properties();
			loadProperties(properties);
			final String classpathEntries = properties.getProperty("classpathEntries");
			ClassPathHacker.addFiles(classpathEntries);
			final File dir = new File("C:/home/img/fotos/2016_Gen�ve");

			try (final Connection connection = openDatabase(properties)) {
				//grabState(connection, dir);
				restoreState(connection, dir);
			}
		} catch (final ClassNotFoundException | IOException | SQLException ex) {
			LOGGER.error(LoggerFactory.ERROR_TAG, ex);
			System.exit(-THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING);
		}
	}

	/**
	 * Grabs and stores in the database the names and modification dates of the files contained
	 * in dir.
	 * @param conn the connection to the database.
	 * @param dir the directory where the files are stored.
	 *
	 * @throws SQLException if something goes awry with the database.
	 * @throws FileNotFoundException if dir is not an existent directory.
	 */
	private void grabState(final Connection conn, final File dir)
			throws FileNotFoundException, SQLException {
		final String absolutePath = dir.getAbsolutePath();
		if (!dir.exists() || !dir.isDirectory()) {
			throw new FileNotFoundException(absolutePath);
		}

		int dirCode = getKeyFor(conn, absolutePath);

		if (dirCode < 0) {
			final String insertDir = "INSERT INTO Directorios (Camino) VALUES (?)";
			try (final PreparedStatement pstmtInsert = conn.prepareStatement(insertDir)) {
				pstmtInsert.setString(1, absolutePath);
				pstmtInsert.executeUpdate();
				dirCode = getKeyFor(conn, absolutePath);
				if (dirCode < 0) {
					throw new SQLException("Couldn't get dir code for " + absolutePath);
				}
			}
		}

		final String insertFile = "INSERT INTO Archivos (Camino, Nombre, FechaModificacion) VALUES (?, ?, ?)";
		try (final PreparedStatement pstmtInsert = conn.prepareStatement(insertFile)) {
			for (final File file : dir.listFiles()) {
				int ii = 0;
				pstmtInsert.setInt(++ii, dirCode);
				pstmtInsert.setString(++ii, file.getName());
				pstmtInsert.setTimestamp(++ii, new Timestamp(file.lastModified()));
				pstmtInsert.executeUpdate();
			}
		}

	}

	/**
	 * Grabs and stores in the database the names and modification dates of the files contained
	 * in dir.
	 * @param conn the connection to the database.
	 * @param dir the directory where the files are stored.
	 *
	 * @throws SQLException if something goes awry with the database.
	 * @throws FileNotFoundException if dir is not an existent directory.
	 */
	private void restoreState(final Connection conn, final File dir)
			throws FileNotFoundException, SQLException {
		final String absolutePath = dir.getAbsolutePath();
		if (!dir.exists() || !dir.isDirectory()) {
			throw new FileNotFoundException(absolutePath);
		}

		final int dirCode = getKeyFor(conn, absolutePath);
		if (dirCode < 0) {
			throw new SQLException("Couldn't get dir code for " + absolutePath);
		}

		// FIXME: �Es mejor empezar por el filesystem o por la base de datos? Creo que por la db porque hago una sola SELECT.
		final String selectFiles = "SELECT Nombre, FechaModificacion FROM Archivos WHERE Camino = ?";
		try (final PreparedStatement pstmtDirCode = conn.prepareStatement(selectFiles)) {
			pstmtDirCode.setInt(1, dirCode);
			final ResultSet rset = pstmtDirCode.executeQuery();
			while (rset.next()) {
				final File file = new File(absolutePath, rset.getString(1));
				final Timestamp timestamp = rset.getTimestamp(2);
				final long lastModified = file.lastModified();
				if (lastModified != timestamp.getTime()) {	// Esta comparacion funciona siempre que al almacenar el valor no se almacenen los nanos.
					LOGGER.debug("Modificado: " + file);
				}
			}
		}

	}

	/**
	 * Gets the key in the database for the path provided.
	 * @param conn the connection to the database.
	 * @param path the directory whose code is wanted.
	 * @return the code or -1 if not found;
	 * @throws SQLException if something goes awry with the database.
	 */
	private static int getKeyFor(final Connection conn, final String path)
			throws SQLException {
		final int dirCode;
		final String getDirCode = "SELECT Codigo FROM Directorios WHERE Camino = ?";
		try (final PreparedStatement pstmtDirCode = conn.prepareStatement(getDirCode)) {
			pstmtDirCode.setString(1, path);
			final ResultSet rset = pstmtDirCode.executeQuery();
			if (rset.next()) {
				dirCode = rset.getInt(1);
			} else {
				dirCode = -1;
			}
		}
		return dirCode;
	}

	private Connection openDatabase(final Properties properties)
			throws ClassNotFoundException, SQLException {
		final String driverName = properties.getProperty("db.driverName");
		LOGGER.debug("db.driverName: [" + driverName + "]");
		final String dbURL = properties.getProperty("db.URL");
		final String databaseUser = properties.getProperty("db.user", "");
		final String databasePwd = properties.getProperty("db.pwd", "");
		Class.forName(driverName);
		return new VersatileConnection(DriverManager.getConnection(dbURL, databaseUser, databasePwd));
	}

	/**
	 * Carga todas propiedades de la aplicacion.
	 * @param properties where to load the properties.
	 */
	private void loadProperties(final Properties properties) throws IOException {
		final String fileName = getClass().getSimpleName() + ".properties";
		final File propsFile = new File(fileName);
		try (final FileInputStream in = new FileInputStream(propsFile)) {
			LOGGER.trace("propsFile = " + propsFile.getAbsolutePath());
			properties.load(in);
		}
	}

	/**
	 * Applications entry point.
	 */
	public static void main(final String[] args) {
		new DateTimeAdjuster();
	}
}
