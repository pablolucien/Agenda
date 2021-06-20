package org.pclg.root;/*
 * @(#)Grep.java	1.3 01/12/13
 * Search a list of files for lines that match a given regular-expression
 * pattern.  Demonstrates NIO mapped byte buffers, charsets, and regular
 * expressions.
 */

import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public final class GrepNew {
	private static final Logger LOGGER = LoggerFactory.make();

	// Charset and decoder for ISO-8859-15
	private static final Charset CHARSET = Charset.forName("ISO-8859-15");
	private static final CharsetDecoder DECODER = CHARSET.newDecoder();

	// Pattern used to parse lines
	private static final Pattern LINE_PATTERN = Pattern.compile(".*\r?\n");

	// The input pattern that we're looking for
	private final Pattern pattern;
	private static final String MSG_ERROR_PROCESANDO_ARCHIVO =
			"Error procesando archivo: {0}";
	private static final int CAPACITY = 1024;
	private boolean ignoreCase;
	private boolean recurseDir;
	private boolean onlyLineNr;

	private GrepNew(final String[] args) {
		int firstArg = 0;
		if (args[firstArg].charAt(0) == '-') {
			if (args[firstArg].indexOf('i') >= 0) {
				ignoreCase = true;
			}
			if (args[firstArg].indexOf('l') >= 0) {
				onlyLineNr = true;
			}
			if (args[firstArg].indexOf('d') >= 0) {
				recurseDir = true;
			}
			firstArg++;
		}
		LOGGER.info("ignoreCase = " + ignoreCase + ", recurseDir = "
				+ recurseDir + ", onlyLineNr = " + onlyLineNr);
		try {
			pattern = Pattern.compile(args[firstArg++]);
		} catch (final PatternSyntaxException x) {
			LOGGER.log(Level.SEVERE, "Error:", x);
			throw x;
		}

		for (int ii = firstArg; ii < args.length; ii++) {
			final File file = new File(args[ii]);
			if (file.isDirectory()) {
				if (recurseDir) {
					recurseGrep(file);
				}
				continue;
			}
			try {
				grep(file);
			} catch (final IOException x) {
				LOGGER.log(Level.SEVERE, MessageFormat.format(
						MSG_ERROR_PROCESANDO_ARCHIVO, file), x);
			}
		}
	}

	private void recurseGrep(final File dir) {
		LOGGER.info("Task Procesando recursivamente: " + dir);
		final File[] files = dir.listFiles();
		for (final File file : files) {
			if (file.isDirectory()) {
				if (recurseDir) {
					recurseGrep(file);
				}
				continue;
			}
			try {
				grep(file);
			} catch (final IOException x) {
				LOGGER.log(Level.SEVERE, MessageFormat.format(
						MSG_ERROR_PROCESANDO_ARCHIVO, file), x);
			}
		}
	}


	// Use the LINE_PATTERN to break the given CharBuffer into lines, applying
	// the input pattern to each line to see if we have a match
	//
	private void grep(final File file, final CharBuffer charBuffer) {
		final Matcher lineMatcher = LINE_PATTERN.matcher(charBuffer);
		Matcher patternMatcher = null;
		int lines = 0;
		final StringBuilder builder = new StringBuilder(CAPACITY);
		while (lineMatcher.find()) {
			lines++;
			final CharSequence cs = lineMatcher.group();	 // The current line
			if (patternMatcher == null) {
				patternMatcher = pattern.matcher(cs);
			} else {
				patternMatcher.reset(cs);
			}
			if (patternMatcher.find()) {
				builder.setLength(0);
				if (onlyLineNr) {
					LOGGER.info(builder.append(file).append(':').append(lines)
							.toString());
				} else {
					LOGGER.info(builder.append(file).append(':').append(lines)
							.append(':').append(cs).toString());
				}
			}
			if (lineMatcher.end() == charBuffer.limit()) {
				break;
			}
		}
	}

	// Search for occurrences of the input pattern in the given file
	//
	private void grep(final File file) throws IOException {
		if (file.isFile()) {
			FileInputStream stream = null;
			FileChannel channel = null;
			try {
				// Open the file and then get a channel from the stream
				stream = new FileInputStream(file);
				channel = stream.getChannel();

				// Get the file's size and then map it into memory
				final int size = (int) channel.size();
				final MappedByteBuffer byteBuffer = channel.map(
						FileChannel.MapMode.READ_ONLY, 0, size);

				// Decode the file into a char buffer
				final CharBuffer charBuffer = DECODER.decode(byteBuffer);

				// Perform the search
				grep(file, charBuffer);
			} finally {
				// Close the channel and the stream
				if (channel != null) {
					channel.close();
				}
				if (stream != null) {
					stream.close();
				}
			}
		} else {
			LOGGER.info(file + " is not a File");
		}
	}

	public static void main(final String[] args) {
		if (args.length < 2) {
			LOGGER.info("Usage: java GrepNew [-ild] pattern file...");
		} else {
			new GrepNew(args);
		}
	}
}