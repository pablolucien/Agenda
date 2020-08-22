/*
	Creada el 2005.09.09 (cumpleaños de Miriana) sacando los metodos que estaban en
	PCLGTools.
/*
1. hacerlo con gracia si no hay acceso a base de datos
2. incrementart el tamaño de los campos de la db si es necesa...
*/


// ******************************** package
package org.pclg.tools;

// ******************************** imports

import org.pclg.annotations.QuickAndDirty;
import org.pclg.security.Digest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Provee funcionalidad para el manejo de la base de datos de digests.
 * Creada el 2005.09.09 (cumpleaños de Miriana) sacando los metodos que estaban
 * en PCLGTools.
 *
 * @author El Coyote Cojo.
 * @version 1.0
 * @since 2005.09.09 (cumpleaños de Miriana)
 */
public final class DigestTools {
    /** Para el utilísimo "logueado". */
    private static final Logger LOGGER = Logger.getLogger("DigestTools");

    /** Contador de los digest nuevos generados. */
    private static int newDigests;

    /** Contador de los digest obtenidos de la base de datos. */
    private static int oldDigests;
    private static final String ALGORITHM_SHA_1 = "SHA-1";


	/**
     * Avoids instantiation.
     */
    private DigestTools() {
    }

    /**
     * Establece los parametros iniciales. ¿¿¿ Hack ??? debe ser llamado antes
     * que los metodos que usan la DB Esto no es thread safe ni mucho menos,
     * pero en realidad toda la clase no lo es. Ademas, todo este rollo es
     * especifico de CompDel, y no debería estar aquí.
     *
     * @since 2003.07.03
     */
    public static void init(final String dbURL, final String driverName,
			final String user, final String pwd, final String infoFile) {
        connURL = dbURL;
        driverClassName = driverName;
        databaseUser = user;
        databasePwd = pwd;
		dbConnnectionInfoFile = infoFile;
    }

    /**
     * genera el digest de un byte[] y lo devuelve como String.
     *
     * @return the digest.
     */
    private static String generateDigest(final byte[] buf) {
        final byte[] bytes = Digest.generateByteArrayDigest(buf, ALGORITHM_SHA_1);
        if (bytes == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder(2 * bytes.length);
		for (final byte aByte : bytes) {
			sb.append(String.format("%02x", aByte));
		}
        return sb.toString();
    }

    /**
     * genera el digest de un String.

     * @return the digest.
     */
    public static String generateDigest(final String text) {
        try {
            final byte[] buf = text.getBytes("ISO-8859-1");
            return generateDigest(buf);
        } catch (final UnsupportedEncodingException ex) {
            // No pasa por aqui: "ISO-8859-1" es obligatoria en todas las
            // implementaciones de Java
            assert false;	//mi primer uso de las API de 1.4: 2002.09.09 22:13
            ToolBox.showInfo(ex);
        }
        return null;
    }

    /**
     * genera el digest de un archivo. <BR><B> Reobtenerlo de la DB, porque
     * parece que sufre una transformacion al almacenarlo. Esta 'feature' hay
     * que arreglarla. </B>
     *
     * @param file El archivo cuyo digest queremos.
     * @param ignoreSaved should we ignore the database?
     * @return El digest o 'null' si no es un archivo o tiene longitud 0.
     * @since 2001.09.09 15:35
     */
    private static String generateDigest(final File file, final boolean ignoreSaved) {
        String digest = null;
        if (!ignoreSaved && (digest = getDigestFromDB(file)) != null) {
            oldDigests++;
            return digest;
        }

        try {
            final Optional<String> computeDigest = Digest.computeDigest(file, ALGORITHM_SHA_1);
            if (computeDigest.isPresent()) {
            	digest = computeDigest.get();
				newDigests++;
				putDigestInDB(file, digest);
				// Reobtenerlo de la DB, porque parece que sufre una transformacion
				// al almacenarlo.
				// TODO: Esta 'feature' hay que arreglarla
				if (!ignoreSaved) {
					digest = getDigestFromDB(file);
				} 
			}
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
            LOGGER.log(Level.WARNING, "DigestTools.generateDigest(): "
                + file.getAbsolutePath());
        } catch (final OutOfMemoryError ex) {
            LOGGER.log(Level.WARNING, "DigestTools.generateDigest(): "
                    + ex
                    + " al pedir buffer para: "
                    + file.getAbsolutePath() + " de "
                    + ToolBox.formatNumber(file.length())
                    + " bytes.\nContin\u00FAo sin procesarlo.");
        }
        return digest;
    }

    /**
     * Checks is a File is an allowed candidate to generate a digest.
     * @param file  the File to check.
     * @return true if is an allowed candidate.
     */
    private static boolean allowedFile(final File file) {
        boolean allowed = true;

        if (file == FileTools.NULL_FILE || file.isDirectory()) {	// silenciosamente no lo consideramos
            allowed = false;
        }
        else if (!file.exists()) {			// no lo consideramos
            LOGGER.log(Level.WARNING, "DigestTools.generateDigest(): "
                + file.getAbsolutePath() + " no existe");
            allowed = false;
        }
        else if (!file.isFile()) {			// no lo consideramos
            LOGGER.log(Level.WARNING, "DigestTools.generateDigest(): "
                + file.getAbsolutePath() + " no es un archivo normal");
            allowed = false;
        }
        else if (file.length() == 0) {	// no lo consideramos
            LOGGER.log(Level.WARNING, "DigestTools.generateDigest(): "
                + file.getAbsolutePath() + " est\u00E1 vacio");
            allowed = false;
        }
        return allowed;
    }

    /**
     * Genera los digests de un conjunto de archivos.
     */
    public static String[] generateDigest(final File[] archivo,
            final boolean showDuration, final boolean ignoreSaved) {
        int chronHandle = -1;
        if (showDuration) {
            chronHandle = Chrono.getChrono();
            Chrono.start(chronHandle);
        }
        final String[] digest = new String[archivo.length];

        newDigests = 0;
        oldDigests = 0;

        // optimización del for 2003.08.21
        int ii = -1;
        while (true) {
            try {
                ii++;
                if (allowedFile(archivo[ii])) {
                    digest[ii] = generateDigest(archivo[ii], ignoreSaved);
                }
            } catch (final ArrayIndexOutOfBoundsException ex) {
                break;
            }
        }

		// TODO: Poner esto en otro sitio. probablemente cuando se agrega un directorio a la base de datos
		try {
			setUpDB();
			DatabaseTools.actualizaDirectorios(conn);
		} catch (final SQLException ex) {
			ToolBox.showInfo(ex, true);
		}

		if (showDuration) {
			Chrono.mark(chronHandle);
            LOGGER.log(Level.INFO, "digest de " + digest.length + " dur\u00F3 "
					+ Chrono.timeDetail(Chrono.elapsed(chronHandle))
					+ " (nuevos " + newDigests + ", viejos " + oldDigests + ')');
		}
        return digest;
    }


//    /**
//     * TODO: Hacer este metodo.
//     */
//    public static String[] generateDigestArray(final File[] archivos, final boolean saveInFile) {
//        return null;
//    }
    /**
     * Usado por putDigestInDB() y getDigestFromDB().
     */
    private static Driver theDriver;

    /**
     * Usado por putDigestInDB() y getDigestFromDB().
     */
    private static String driverClassName;

    /**
     * Usado por putDigestInDB() y getDigestFromDB()
     */
    private static String connURL;

    private static String databasePwd;

    private static String databaseUser;

	private static String dbConnnectionInfoFile;

    /**
     * Usado por putDigestInDB() y getDigestFromDB().
     */
    private static Connection conn;

    /**
     * Usado por putDigestInDB().
     */
    private static Timestamp now;

    /**
     * Usado por putDigestInDB() y getDigestFromDB().
     */
    private static PreparedStatement pstmtInsert;

    /**
     * Usado por putDigestInDB().
     */
    private static PreparedStatement pstmtInsertSeries;

    /**
     * Usado por getDigestFromDB().
     */
    private static PreparedStatement pstmtSelect;

    /**
     * No usado de momento.
     */
    //private static PreparedStatement pstmtSelectSeries;

    /**
     * Pone el digest en la base de datos. <BR> Codigo tomado de
     * FileDataBase.java.
     *
     * @since 2002.07.06
     */
    private static void putDigestInDB(final File file, final String digest) {
        try {
            if (pstmtInsert == null) {
                setUpDB();
                pstmtInsert = conn.prepareStatement("INSERT INTO Archivos "
                        + " (camino, nombre, tamanho, fechaModificacion, extension, digest, fechaIngreso) "
                        + " VALUES (?, ?, ?, ?, ?, ?, ?) ");
            }

            if (pstmtInsertSeries == null) {
                setUpDB();
                pstmtInsertSeries = conn.prepareStatement("INSERT INTO Series "
                        + " (directorio, nombreSerie, thumbnail) "
                        + " VALUES (?, ?, ?)");
            }

            if (now == null) {
                now = new Timestamp(new Date().getTime());
            }

            if (getDigestFromDB(file) != null) {
                // si ya está, no lo insertamos. TODO: deberíamos hacer un update
                LOGGER.log(Level.WARNING, file.getAbsolutePath()
                    + " ya est\u00E1 en la base de datos.");
                return;
            }

            int pos = 0;

            final String name = file.getName();
            final String parent = file.getParent();
            pstmtInsert.setInt(++pos, (int) DatabaseTools.getKey(conn, "Directorios", "Camino", parent));		// camino
            pstmtInsert.setString(++pos, name);	            // nombre
            //pstmtInsert.setLong(++pos, file.length());	// tamaño debería ser esta, pero no está implementada en Access
            pstmtInsert.setInt(++pos, (int) file.length());	// tamaño
            pstmtInsert.setTimestamp(++pos, new Timestamp(file.lastModified()));	// fecha modificacion

            final int lastDot;
            if ((lastDot = name.lastIndexOf('.')) >= 0) {
                pstmtInsert.setInt(++pos, (int) DatabaseTools.getKey(conn, "Extensiones", "Extension", name.substring(lastDot)));	// extension
            } else {
                pstmtInsert.setInt(++pos, (int) DatabaseTools.getKey(conn, "Extensiones", "Extension", ""));	// extension
            }

            pstmtInsert.setString(++pos, digest);											// digest
            pstmtInsert.setTimestamp(++pos, now);											// fecha ingreso

            pstmtInsert.executeUpdate();

            if (name.toLowerCase().endsWith("01.jpg")) {											// Posible inicio de serie
                pos = 0;
                pstmtInsertSeries.setInt(++pos, (int) DatabaseTools.getKey(conn, "Directorios", "Camino", parent));		// camino
                pstmtInsertSeries.setString(++pos, name);			// nombreSerie
                pstmtInsertSeries.setString(++pos, "Thumbnail");	// Thumbnail
                pstmtInsertSeries.executeUpdate();
            }
        } catch (final SQLException ex) {
            ToolBox.showInfo(ex, true);
            LOGGER.log(Level.WARNING, file.getAbsolutePath()
                + " este es el causante del enredo");
            System.err.println(file.getAbsolutePath()
                + " este es el causante del enredo");
        }
    }

    /**
     * Obtiene el digest de la base de datos.
     *
     * @param file        El archivo cuyo digest queremos obtener.
     * @since 2002.07.06
     * @return El digest del archivo según figura en la BD.
     */
    private static String getDigestFromDB(final File file) {
        String digest = null;
        try {
            if (pstmtSelect == null) {
                setUpDB();

                // Si cambio la clausula WHERE de esto, cambiar también el indice en la bd
                pstmtSelect = conn.prepareStatement("SELECT digest FROM Archivos "
                        + " WHERE camino = ? AND nombre = ? AND tamanho = ? AND fechaModificacion = ?");
            }

            int pos = 0;

            pstmtSelect.setInt(++pos, (int) DatabaseTools.getKey(conn, "Directorios", "Camino", file.getParent()));		// camino
            pstmtSelect.setString(++pos, file.getName());										// nombre
            //pstmtSelect.setLong(++pos, file.length());											// tamaño 	debería ser esta, pero no está implementada en Access
            pstmtSelect.setInt(++pos, (int) file.length());										// tamaño
            pstmtSelect.setTimestamp(++pos, new Timestamp(file.lastModified()));			// fecha modificacion
            final ResultSet rset = pstmtSelect.executeQuery();
            if (rset.next()) {
                digest = rset.getString(1);
            }
            rset.close();
        } catch (final SQLException ex) {
            ToolBox.showInfo(ex, true);
        }
        return digest;
    }

    /**
     * Elimina de la base de datos los registros que no corresponden a archivos
     * existentes.
     *
     * @since 2003.01.21 Cumpleaños de Josefina Bajo.
     */
    public static void cleanDigestDB(final boolean supportsCompactTable)
			throws SQLException {
        LOGGER.log(Level.WARNING, "Ejecutando en un hilo aparte. "
            + "No hacer nada hasta que termine");
        LOGGER.log(Level.WARNING, "Estoy eliminando los registros "
            + "correspondientes a diskette sin verificar");
        setUpDB();
        new DigestToolsCleaner(conn, supportsCompactTable).start();
    }

    /**
	 * FIXME: no tiene sentido que esto sea llamado a cada rato.
     * Establece la comunicacion con la base de datos
     *
     * @since 2003.01.07
     */
    private static void setUpDB() throws SQLException {
        try {
            if (theDriver == null) {
                theDriver = (Driver) Class.forName(driverClassName).newInstance();
            }
            if (conn == null) {
                conn = DriverManager.getConnection(connURL, databaseUser, databasePwd);
				if (Boolean.parseBoolean(PropertiesHelper
						.getProperty(dbConnnectionInfoFile, "createTables"))) {
					createTables(conn);
				}
            }
        } catch (final ClassNotFoundException ex) {
            ToolBox.showInfo(ex, true);
            throw new SQLException(driverClassName + ": ClassNotFoundException");
        } catch (final InstantiationException ex) {
            ToolBox.showInfo(ex, true);
            throw new SQLException(driverClassName + ": InstantiationException");
        } catch (final IllegalAccessException ex) {
            ToolBox.showInfo(ex, true);
            throw new SQLException(driverClassName + ": IllegalAccessException ");
        } catch (final IOException e) {
			LOGGER.log(Level.SEVERE, "Error", e);
		}
	}

	/**
	 * Crea las tablas necesarias para la correcta operación de la aplicación.
	 *
	 * @param conn conexión a la base de datos.
	 * @throws SQLException si peta.
	 */
	private static void createTables(final Connection conn) throws SQLException {
		String sqlCreateSentence = null;
		try (Statement stmt = conn.createStatement()) {
			dropTable(stmt, "Series");
			dropTable(stmt, "Archivos");
			dropTable(stmt, "Directorios");
			dropTable(stmt, "Extensiones");

			sqlCreateSentence = PropertiesHelper.getProperty(dbConnnectionInfoFile, "createExtensiones");
			stmt.execute(sqlCreateSentence);
			sqlCreateSentence = PropertiesHelper.getProperty(dbConnnectionInfoFile, "createDirectorios");
			stmt.execute(sqlCreateSentence);
			sqlCreateSentence = PropertiesHelper.getProperty(dbConnnectionInfoFile, "createArchivos");
			stmt.execute(sqlCreateSentence);
			sqlCreateSentence = PropertiesHelper.getProperty(dbConnnectionInfoFile, "createSeries");
			stmt.execute(sqlCreateSentence);
		} catch (final SQLException ex) {
			LOGGER.log(Level.SEVERE, sqlCreateSentence, ex);
			throw ex;
		} catch (final IOException ex) {
			LOGGER.log(Level.SEVERE, sqlCreateSentence, ex);
			throw new SQLException("createTables : IOException ", ex);
		}
	}

	/** Elimina una tabla tragándose las excepciones. Para usarla con DBs que
	 * no aceptan 'IF EXISTS'.
	 * @param stmt un statement conectado a la DB interfecta.
	 * @param tableName  el nombre de la tabla.
	 */
	@QuickAndDirty
	private static void dropTable(final Statement stmt, final String tableName)
			throws SQLException {
		try {
			stmt.execute("drop table " + tableName);
		} catch (final SQLSyntaxErrorException ex) {
			LOGGER.log(Level.INFO,
				"dropTable(): Table <" + tableName + "> doesn't exist");
		}
	}
}