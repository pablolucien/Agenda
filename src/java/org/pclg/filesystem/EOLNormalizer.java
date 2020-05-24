package org.pclg.filesystem;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.Dir;
import org.pclg.tools.FileTools;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Changes the EOL
 *
 * @since 30/04/2020.
 */
public final class EOLNormalizer {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    static String baseDirName = "E:\\workspace_EBU\\youtube-connector_2";

    public static void main(final String[] args) throws IOException {
        final List<File> files = new Dir(Dir.NULL_EXCLUDE_LIST)
                .listarArchivos(new File(baseDirName), true, false, Dir.AcceptableType.FILE);
        for (final File inFile : files) {
            if (isPossiblyTextFile(inFile)) {
                FileTools.convertFileInPlace(inFile, replaceEOL);
            }
        }
    }

    private static final BiConsumer<File, File> replaceEOL = (inFile, outFile) -> {
        try {
            final String original = new String(FileTools.readFromFile(inFile));
            final String replace = original.replace("\n", "\r\n");
            final byte[] convertedBytes = replace.getBytes();
            FileTools.writeToFile(convertedBytes, outFile, true);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    };

    private static boolean isPossiblyTextFile(final File inFile) {
        final String name = inFile.getName();
        return name.endsWith(".java")
            || name.endsWith(".xml")
            || name.endsWith(".txt")
            || name.endsWith(".json")
            || name.endsWith(".yaml")
            || name.endsWith(".html");
    }
}
