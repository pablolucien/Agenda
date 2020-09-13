/*
 * Digest.java
 *
 * Created on 9 de diciembre de 2005, 9:16
 */

package org.pclg.security;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Verifica los digests de archivos.
 *
 * @author El Coyote.
 */
public final class Digest {
    /** Supported algorithms. */
    private static final String[] ALGORITHMS = {"md5", "sha1", "sha-256", "sha-512"};

	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final int BUFFER_SIZE = 1024 << 10;

    /** Prevents instantiation. */
	private Digest() {
	}

    /**
     * Verifies if the digest of a File match with the supposed one which will
     * be stored in a text file with the same path as the original file but
     * ending in .&lt;algorithm&gt;.toLowerCase.
     *
     * @param filename the file whose digest is to be computed.
     * @param algorithm the digest algorithm to be used.
     * @return true if match, false otherwise.
     * @throws FileNotFoundException if the file can't be opened.
     * @throws IOException if there is a problem with the I/O.
     */
    public static boolean verifyDigest(final String filename, final String algorithm) throws IOException {
        final File digestFile = new File(filename + '.' + algorithm.toLowerCase());
        try (BufferedReader reader = new BufferedReader(new FileReader(digestFile))) {
            final String line = reader.readLine();
            if (line == null) {
                LOGGER.error(digestFile + " doesn't contain a valid digest");
                return false;
            }
            final int posOfSpace = line.indexOf(' ');
            final String originalDigest = line.substring(0, posOfSpace >= 0 ? posOfSpace : line.length()).toLowerCase();
            final File targetFile = new File(filename);
            final Optional<String> optionalComputedDigest = computeDigest(targetFile, algorithm);
            final boolean areEqual;
            if (optionalComputedDigest.isPresent()) {
                final String computedDigest = optionalComputedDigest.get();
                LOGGER.debug(String.format("algorithm %s: computed = %s, original = %s", algorithm, computedDigest, originalDigest));
                areEqual = computedDigest.equals(originalDigest);
            } else {
                LOGGER.warn(String.format("Could not compute digest for %s using algorithm %s", filename, algorithm));
                areEqual = false;
            }
            return areEqual;
        }
    }

    /**
     * Computes the digest of a String.
     *
     * @param string the string whose digest is to be computed.
     * @param algorithm  the digest algorithm to be used.
     * @return the digest of the string.
     * @throws IOException if there is a problem with the I/O.
     */
    public static Optional<String> computeDigest(final String string,
            final String algorithm) throws IOException {
        try (final InputStream fis = new ByteArrayInputStream(string.getBytes())) {
            return computeDigest(fis, algorithm);
        }
    }

    /**
     * Computes the digest of a File.
     *
     * @param targetFile the file whose digest is to be computed.
     * @param algorithm  the digest algorithm to be used.
     * @return the digest of the File.
     * @throws IOException if there is a problem with the I/O.
     */
    public static Optional<String> computeDigest(final File targetFile, final String algorithm) throws IOException {
        try (final InputStream fis = new FileInputStream(targetFile)) {
            return computeDigest(fis, algorithm);
        }
    }

    private static Optional<String> computeDigest(final InputStream stream, final String algorithm) throws IOException {
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            final byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            while ((count = stream.read(buffer)) > 0) {
                md.update(buffer, 0, count);
            }

            final byte[] digest = md.digest();
            final StringBuilder sb = new StringBuilder(2 * digest.length);
            for (final byte bite : digest) {
                sb.append(String.format("%02x", Byte.valueOf(bite)));
            }
            return Optional.of(sb.toString());
        } catch (final NoSuchAlgorithmException ex) {
            ToolBox.showInfo(ex, true);
            return Optional.empty();
        }
    }

    /**
     * Genera el digest de un byte[] y lo devuelve como byte[].
     * @param buf the byte[] whose digest is to be computed.
     * @param algorithm the algorithm to use.
     *
     * @return the digest as a byte[].
     * @since 2003.05.25	Dia de elecciones municipales en el Estado Espa�ol.
     */
    public static byte[] generateByteArrayDigest(final byte[] buf, final String algorithm) {
        try {
            final MessageDigest msgDigest = MessageDigest.getInstance(algorithm);
            msgDigest.reset();
            msgDigest.update(buf);
            return msgDigest.digest();
        } catch (final NoSuchAlgorithmException ex) {
            ToolBox.showInfo(ex, true);
        }
        return null;
    }

	/**
	 * @return the algorithms
	 */
	public static String[] getAlgorithms() {
		final String[] copy = new String[ALGORITHMS.length];
		System.arraycopy(ALGORITHMS, 0, copy, 0, ALGORITHMS.length);
		return copy;
	}
}
