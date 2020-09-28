/*
 * Digest.java
 */

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ExtensionFiltro;
import org.pclg.tools.ToolBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.pclg.security.Digest.computeDigest;
import static org.pclg.security.Digest.getAlgorithms;
import static org.pclg.security.Digest.verifyDigest;

/**
 * Un acceso desde la l�nea de comandos a la clase
 * org.pclg.security.Digest
 *
 * @author El Coyote.
 */
public final class Digest {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

    /** Prevents instantiation. */
	private Digest() {
	}

	private static void usage() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Usage Digest [-h|-c|-a [--]] <filespec>\n")
                .append("\t-h ==> help (this help ;)\n")
                .append("\t-a ==> calculate the digests of all existing digest files\n")
                .append("\t-c ==> calculate the digests (instead of verify them)\n")
                .append("\t-- ==> calculate the digests of the rest of arguments\n\n")
                .append("\tAvailable algorithms are: ")
                .append(Arrays.toString(getAlgorithms()))
                .append('\n');
        LOGGER.warn(builder.toString());
    }

	/**
     * Verifies the digest of a set of files.
     * @param filenames contains the names of the files whose digests are to be
     * computed.
     * @param algorithm the algorithm to use to generate de digests.
     */
    public static void digest(final String[] filenames, final String algorithm) {
        for (final String filename : filenames) {
            digest(filename, algorithm);
        }
    }

	/**
     * Verifies the digest of a file.
     * @param filename the names of the file whose digests is to be
     * computed.
     * @param algorithm the algorithm to use to generate the digest.
     */
    private static void digest(final String filename, final String algorithm) {
        try {
            LOGGER.warn("File: " + filename);
            LOGGER.warn(algorithm + ": "
                    + (verifyDigest(filename, algorithm) ? "OK" : "WRONG!!") + System.lineSeparator());
        } catch (final FileNotFoundException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        } catch (final IOException ex) {
            ToolBox.showInfo(ex, true);
        }
    }

    private static void verifyTargets(final String[] args, final int firstArg, final boolean calculateDigests,
            final String[] algorithms) throws IOException {
        for (int ii = firstArg; ii < args.length; ii++) {
            boolean nothingDone = true;
            final File file = new File(args[ii]);
            if (!file.exists()) {
                LOGGER.warn("Nothing done for " + file + " (file not found).");
                continue;
            }
            for (final String algorithm : algorithms) {
                if (calculateDigests) {
                    LOGGER.warn(file + ": " + algorithm + " = "
                            + computeDigest(file, algorithm).orElse(""));
                    nothingDone = false;
                } else if (new File(args[ii] + '.' + algorithm).exists()) {
                    digest(args[ii], algorithm);
                    nothingDone = false;
                }
            }
            if (nothingDone) {
                LOGGER.warn("Nothing done for " + args[ii] + ": no digest file found.");
            }
        }
    }

    private static void verifyAllPossibleTargets(final String[] algorithms) {
        final File[] digestFiles = new File(".").listFiles(new ExtensionFiltro(algorithms, "", false));
        if (digestFiles != null) {
            Arrays.stream(digestFiles)
                    .map(file -> {
                        final String name = file.getName();
                        return name.substring(0, name.lastIndexOf('.'));
                    }).map(File::new).filter(File::exists).distinct()
                    .forEach(file -> Arrays.stream(algorithms)
                            .filter(algorithm -> new File(file.getName() + '.' + algorithm).exists())
                            .forEach(algorithm -> digest(file.getName(), algorithm)));
        } else {
            LOGGER.warn("Could not get directory content");
        }
    }

    /**
     * @param args the command line arguments.
     * @throws IOException if something goes wrong.
     */
    public static void main(final String[] args) throws IOException {
        if (args.length == 0 || args[0].equals("-h")) {
            usage();
            System.exit(1);
        }
        final int firstArg;
        final boolean calculateDigests;
        if (args[0].equals("-c") || args[0].equals("--")) {
            firstArg = 1;
            calculateDigests = true;
        } else {
            firstArg = 0;
            calculateDigests = false;
        }
        final String[] algorithms = getAlgorithms();
        switch (args[0]) {
        case "--":
            final String argument = Arrays.stream(args, firstArg, args.length).collect(Collectors.joining(" "));
            for (final String algorithm : algorithms) {
                LOGGER.warn(argument + ": " + algorithm + " = " + computeDigest(argument, algorithm).orElse(""));
            }
            break;
        case "-a":
            verifyAllPossibleTargets(algorithms);
            break;
        default:
            verifyTargets(args, firstArg, calculateDigests, algorithms);
            break;
        }
    }
}
