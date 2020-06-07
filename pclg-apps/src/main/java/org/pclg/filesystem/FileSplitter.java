package org.pclg.filesystem;

import org.pclg.tools.ToolBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Creada en una refactorizacion de Splitter (move method de splittFile()).
 * @author Pablo
 * @since 05-ene-2006 20:37:49
 */
public final class FileSplitter {
    /** Error code. */
    public static final int DONT_EXISTS = -1;

    /** Error code. */
    public static final int IS_DIR = -2;

    /** Error code. */
    public static final int SMALL_FILE = -3;

    /** El tamaño del buffer a usar.
     * Implementation note: was 1024 * 1024, now equals
     * Splitter.DISKETTE_FILE_SIZE for historical reasons, but can be
     * changed at will
     */
    private static final int BUFF_SIZE = 1440000;

    /**
     * Prevents instantiation.
     */
    private FileSplitter() {
    }

    /**
     * Este es el método que hace el trabajo. Puede ser llamado desde otras
     * clases.
     * @param fileName el nombre del archivo a dividir.
     * @param splitSize el tamaño deseado de los pedazos.
     * @return The number of chunks of the file or a (negative) error code.
     * @throws FileNotFoundException si no logra abrir el fichero.
     * @throws IOException si ha problemas deI/O.
     */
    public static int splittFile(final String fileName, final long splitSize)
            throws IOException {
        final File target = new File(fileName);
        if (!target.exists()) {
            return DONT_EXISTS;
        }

        // Comprobación agregada el 04/01/2006
        if (target.isDirectory()) {
            return IS_DIR;
        }

        final long targetLen = target.length();
        if (targetLen <= splitSize) {
            return SMALL_FILE;
        }

        int bytesRead;
        int chunkNr = 0;
        final int bufferSize = (int) Math.min(BUFF_SIZE, splitSize);
        final byte[] buffer = new byte[bufferSize];
        final RandomAccessFile inputFile = new RandomAccessFile(target, "r");
        final int padLen = (int) Math.log10(targetLen / splitSize) + 1;
        final int stringBuilderLen = fileName.length() + padLen + 1;
        RandomAccessFile outputFile = new RandomAccessFile(new File(
            new StringBuilder(stringBuilderLen).append(fileName).append('.')
                .append(ToolBox.leftPad(String.valueOf(chunkNr), padLen, '0'))
                .toString()), "rw");
        long bytesWritten = 0L;   // Lo que ya he escrito en este segmento

        while ((bytesRead = inputFile.read(buffer)) >= 0) {
            if (bytesWritten + bytesRead >= splitSize) {
                // The cast is safe because (splitSize - bytesWritten) <= bytesRead (int)
                final int chunkRemainder = (int) (splitSize - bytesWritten);
                outputFile.write(buffer, 0, chunkRemainder);
                outputFile.close();
                outputFile = new RandomAccessFile(new File(
					new StringBuilder(stringBuilderLen).append(fileName).append('.')
						.append(ToolBox.leftPad(String.valueOf(++chunkNr), 
							padLen, '0')).toString()), "rw");
                outputFile.write(buffer, chunkRemainder,
                        bytesRead - chunkRemainder);
                bytesWritten = bytesRead - chunkRemainder;
            } else {
                outputFile.write(buffer, 0, bytesRead);
                bytesWritten += bytesRead;
            }
        }

        outputFile.close();
        inputFile.close();

        return chunkNr;
    }
}
