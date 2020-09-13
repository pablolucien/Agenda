package org.pclg.media.utils;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ajusta un fichero de subt�tulos (.srt) para que coincida con los labios del
 * parlante.
 *
 * @author El Coyote Cojo
 * @since 2014.06.25
 */
public class SrtAdjuster {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final String USAGE_MSG
		= "Usage: SrtAdjuster <souce file> [-]<miliseconds>";
	private static final String BYE_MSG = "That's all folks";
	private static final String SRT_REGEX= "(\\d\\d\\:\\d\\d\\:\\d\\d,\\d\\d\\d)"
		+ " --> (\\d\\d\\:\\d\\d\\:\\d\\d,\\d\\d\\d)(.*)";

	private SrtAdjuster(final String srcFilename, final String adjust)
			throws IOException, ParseException {
		final File srcFile = new File(srcFilename);
		if (!srcFile.exists()) {
			LOGGER.warn(srcFile + " no existe. �Hasta luego Lucas!");
			System.exit(-1);
		}
		final File tgtFile = File.createTempFile("tmp", ".srt", srcFile.getParentFile());

		final BufferedReader reader = new BufferedReader(new FileReader(srcFile));
		try {
			final PrintWriter writer = new PrintWriter(tgtFile);
			try {
				// 00:02:36,323 --> 00:02:39,450
				final Pattern pattern = Pattern.compile(SRT_REGEX);
				final int adjustMSecs = Integer.parseInt(adjust);
				LOGGER.warn("Echando " + (adjustMSecs < 0 ? "pa'tr�s " : "pa'lante ") + Math.abs(adjustMSecs) + " milisegundos");
				String line;
				while ((line = reader.readLine()) != null) {
					final Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) {
						writer.println(adjusted(matcher.group(1), matcher.group(2), adjustMSecs, matcher.group(3)));
					} else {
						writer.println(line);
					}
				}
			} finally {
				writer.close();
			}
		} finally {
			reader.close();
		}
		if (srcFile.delete()) {
			if (!tgtFile.renameTo(srcFile)) {
				LOGGER.error("no pude renombrar " + tgtFile + " a " + srcFile);
			}
		} else {
			LOGGER.error("no pude eliminar " + srcFile);
		}
		LOGGER.warn(BYE_MSG);
	}

	private static CharSequence adjusted(final String startStrTime, final String endStrTime,
		final int millisecs, final String restOfLine) throws ParseException {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss,SSS");
		final Calendar startTime = Calendar.getInstance();
		startTime.setTime(dateFormat.parse(startStrTime));
		startTime.add(Calendar.MILLISECOND, millisecs);
		final Calendar endTime = Calendar.getInstance();
		endTime.setTime(dateFormat.parse(endStrTime));
		endTime.add(Calendar.MILLISECOND, millisecs);
		return dateFormat.format(startTime.getTime()) + " --> "
			+ dateFormat.format(endTime.getTime()) + restOfLine;
	}

	/**
	 * @param args argumentos: <souce file> [-]<miliseconds>
	 */
	public static void main(final String[] args) {
		if (args.length != 2) {
			LOGGER.error(USAGE_MSG);
			System.exit(-1);
		}
		try {
			new SrtAdjuster(args[0], args[1]);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
}
