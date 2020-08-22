package org.pclg.filesystem;

import org.pclg.tools.FileTools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Crea un archivo de determinado tamaño en el filesystem.
 *
 * @author Pablo
 * @since 24-dic-2005 14:59:28
 */
public final class FileCreator {
    private static final String MSG_YA_EXISTE =
            " ya existe. Elimínelo y vuelva a ejecutar el programa, "
                    + "s'il vous plait";
    private static final String MSG_SIZE =
            "Sólo puedo crear archivos entre 0 y " + Long.MAX_VALUE + " bytes";

    /** Avoids instantiation */
    private FileCreator() {
    }

    /**
     * Creates a file.
     * @param nome the name of the file.
     * @param size the size of the file.
     * @param fillRandomly if <code>true</code> fills the newly created file
     * with random bytes.
     * @throws IOException when some I/O error occurs.
     */
    public static void createFile(final String nome, final long size,
            final boolean fillRandomly) throws IOException {
        if(size < 0) {
            throw new IOException(MSG_SIZE);
        }

        final File file = new File(nome);
        if(file.exists()) {
            throw new IOException(nome + MSG_YA_EXISTE);
        }

		try (final RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			raf.setLength(size);
		}

        if (fillRandomly) {
            FileTools.overwriteFile(file);
        }
    }
}
