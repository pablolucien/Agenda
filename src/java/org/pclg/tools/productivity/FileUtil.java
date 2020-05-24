package org.pclg.tools.productivity;

import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

/**
 * @author paceLucien
 * @since 28-abr-2011 13:37:14
 */
final class FileUtil {
    private static final Logger LOGGER = LoggerFactory.makeSimpleLogger();
    private static final int CR_CHAR = 0x0d;
    private static final int LF_CHAR = 0x0a;

	private FileUtil() {
	}

	static void substituteFiles(final File newFile, final File oldFile) {
        if (!oldFile.delete()) {
            LOGGER.severe(new StringBuilder().append("No pude borrar ")
                .append(oldFile).append(". Los cambios están en ")
                .append(newFile).toString());
        } else if (!newFile.renameTo(oldFile)) {
            LOGGER.severe(new StringBuilder().append("No pude renombrar ")
                .append(newFile).append(" a ").append(oldFile).toString());
        }
    }

    static void replaceFile(final File inFile, final File outFile,
            final boolean changed) throws IOException {
        if (changed) {
            LOGGER.fine("Cambiada " + inFile);
            normalizeFile(outFile, inFile);
            substituteFiles(outFile, inFile);
        } else if (!outFile.delete()) {
            LOGGER.severe("Borrar manualmente " + outFile);
        }
    }

    /**
     * Q & D
     * If last 2 bytes of outFile are 0D 0A and in inFile they are not,
     * supprime them. (Los xml de dico no parecen tenerlos en la última línea,
     * y prefiero dejarlos así de momento.
     *
     * @param outFile outFile
     * @param inFile  inFile
     * @throws IOException if something goes south.
     */
    private static void normalizeFile(final File outFile, final File inFile)
            throws IOException {
        final RandomAccessFile inRaf = new RandomAccessFile(inFile, "r");
        inRaf.seek(inFile.length() - 2);
        final byte[] inBytes = new byte[2];
        inRaf.read(inBytes);
        if (inBytes[0] != CR_CHAR || inBytes[1] != LF_CHAR) {
            final RandomAccessFile outRaf = new RandomAccessFile(outFile, "rw");
            outRaf.setLength(outFile.length() - 2);
            outRaf.close();
        }
        inRaf.close();
    }
}
