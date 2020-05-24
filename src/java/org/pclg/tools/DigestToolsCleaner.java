package org.pclg.tools;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilizada por DigestTools para borrar de la base de datos los registros
 * que no tienen realidad en el filesystem.
 *
 * @author Pablo
 * @since 22-jul-2006 10:59:31
 */
final class DigestToolsCleaner extends Thread {
    /** Para el utilísimo "logueado". */
    private static final Logger LOGGER = Logger.getLogger("DigestToolsCleaner");

    /** El nombre de la tabla de archivos. */
    private static final String ARCHIVOS_TABLE_NAME = "Archivos";

    /** El nombre de la tabla de directorios. */
    private static final String DIRECTORIOS_TABLE_NAME = "Directorios";

    /** La conexión con la db. */
    private final Connection conn;

    /** Usado para seleccionar todos los archivos. */
    private static PreparedStatement pstmtSelectAllFiles;

    /** Usado para borrar un archivo. */
    private static PreparedStatement pstmtDeleteOneFile;

    /** Usado para seleccionar todos los directorios. */
    private static PreparedStatement pstmtSelectAllDirectories;

    /** Usado para borrar un directorio. */
    private static PreparedStatement pstmtDeleteOneDirectory;
	private static final int LIMIT = 10000;

	private final boolean supportsCompactTable;

    /** Inicializa este objeto.
     *
     * @param conn La conexion con la base de datos.
     * @param supportsCompactTable Indica si se puede compactar la bd.
     */
	public DigestToolsCleaner(final Connection conn,
			final boolean supportsCompactTable) {
        this.conn = conn;
		this.supportsCompactTable  = supportsCompactTable;
    }


    @Override
    public void run() {
        final boolean showDuration = true;
        final boolean showWeAreWorking = true;
        int chronHandle;
        if (showDuration) {
            chronHandle = Chrono.getChrono();
            Chrono.start(chronHandle);
        }

		final String optimizeTableSentence = "OPTIMIZE TABLE "
				+ ARCHIVOS_TABLE_NAME;
        try {
            setupStatements();
            final boolean compactar = cleanFilesTable(showWeAreWorking);
            cleanDirectoriesTable(showWeAreWorking);

            // ??? Esto de momento sólo funciona en MySQL
            // ??? Una opción portable es crear una tabla temporal, copiar los registros y cambiarle en nombre
            if (compactar && supportsCompactTable) {
                LOGGER.log(Level.WARNING, "Estoy compactando la DB");
                conn.createStatement().execute(optimizeTableSentence);
            }
        } catch (final SQLException ex) {
			LOGGER.log(Level.SEVERE, "La sentencia ejecutada: ["
					+ optimizeTableSentence + ']', ex);
		}

        if (showDuration) {
            Chrono.mark(chronHandle);
            LOGGER.log(Level.INFO, "Limpieza de la base de datos dur\u00F3 "
                + Chrono.timeDetail(Chrono.elapsed(chronHandle)));
        }
    }

    /**
     * Borra registros de la tabla de directorios que no están en el disco.
     * @param showWeAreWorking indica si debemos dar algún feedback de que
     * no estamos colgaos.
     * @throws SQLException Si hay problemas con la base de datos.
     */
    private static void cleanDirectoriesTable(final boolean showWeAreWorking)
            throws SQLException {
        final ResultSet rset = pstmtSelectAllDirectories.executeQuery();
        int regnr = 0;
        int total = 0;
        int borrados = 0;

        while (rset.next()) {
            if (showWeAreWorking) {
                if (regnr % LIMIT == 0) {
                    LOGGER.log(Level.INFO, "Directorios procesados hasta ahora: "
                        + Integer.toString(regnr));
                }
                regnr++;
            }
            total++;
            final int codigo = rset.getInt(1);
            final String name = rset.getString(2);
            final File file = new File(name);

            if (name.toLowerCase().startsWith("a:") || !file.exists()) {
                pstmtDeleteOneDirectory.setInt(1, codigo);
                borrados += pstmtDeleteOneDirectory.executeUpdate();
                //borrados++;
                //System.err.println("Borrado de la DB: " + name);
            }
        }
        rset.close();
        LOGGER.log(Level.INFO, "Total de directorios = " + total
				+ ", borrados = " + borrados);
    }

    /**
     * Borra registros de la tabla de archivos que no representa un archivo
     * en disco.
     * @param showWeAreWorking indica si debemos dar algún feedback de que
     * no estamos colgaos.
     * @return  true si ha borrado más del 10% de los registros.
     * @throws SQLException Si hay problemas con la base de datos.
     */
    private boolean cleanFilesTable(final boolean showWeAreWorking)
            throws SQLException {
        final ResultSet rset = pstmtSelectAllFiles.executeQuery();
        int regnr = 0;
        int total = 0;
        int borrados = 0;

        while (rset.next()) {
            if (showWeAreWorking) {
                if (regnr % LIMIT == 0) {
                    LOGGER.log(Level.INFO, "Archivos procesados hasta ahora: "
                        + Integer.toString(regnr));
                }
                regnr++;
            }
            total++;

            final int parentKey = rset.getInt(1);
            final String parent = DatabaseTools.getDesc(conn, "Directorios",
                "Camino", parentKey);	// obtenido de la tabla relacionada
            final String name = rset.getString(2);
            if (parent == null) {
                LOGGER.log(Level.WARNING, "No tiene padre: " + name);
            }
            final int length = rset.getInt(3);
            final Timestamp fecha = rset.getTimestamp(4);
            final File file = new File(parent, name);
            final Timestamp fechaArchivo = new Timestamp(file.lastModified());
            fechaArchivo.setNanos(0);

//System.err.println(file + "\t\t\t\t\t: " + (parent == null) + ", "
//                + parent.toLowerCase().startsWith("a:")
//                + ", " + !file.exists() + ", " + (file.length() != length)
//                + ", " + (fechaArchivo.getTime() != fecha.getTime())
//                + ", " + fechaArchivo.getTime() + ", " +  fecha.getTime());

            if (parent == null || parent.toLowerCase().startsWith("a:")
                || !file.exists() || file.length() != length
                || fechaArchivo.getTime() != fecha.getTime()) {
                int pos = 0;
                pstmtDeleteOneFile.setInt(++pos, parentKey);		// camino
                pstmtDeleteOneFile.setString(++pos, name);
                pstmtDeleteOneFile.setInt(++pos, length);
                pstmtDeleteOneFile.setTimestamp(++pos, fecha);
                borrados += pstmtDeleteOneFile.executeUpdate();
                //System.err.println("Borrado de la DB: " + parent + "/" + name);
            }
        }
        rset.close();
        LOGGER.log(Level.INFO, "Total de archivos = " + total + ", borrados = " + borrados);
        return (double) borrados / (double) total >= 0.1;
    }

    private void setupStatements() throws SQLException {
        pstmtSelectAllFiles = conn.prepareStatement(
            "SELECT camino, nombre, tamanho, fechaModificacion FROM "
                + ARCHIVOS_TABLE_NAME);

        pstmtDeleteOneFile = conn.prepareStatement(
            "DELETE FROM " + ARCHIVOS_TABLE_NAME
                + " WHERE camino = ? AND nombre = ? "
                + "AND tamanho = ? AND fechaModificacion = ?");

        pstmtSelectAllDirectories = conn.prepareStatement(
            "SELECT codigo, camino FROM " + DIRECTORIOS_TABLE_NAME
			+ " ORDER BY padre DESC");

        pstmtDeleteOneDirectory = conn.prepareStatement(
            "DELETE FROM " + DIRECTORIOS_TABLE_NAME + " WHERE codigo = ?");

    }
}