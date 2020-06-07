package org.pclg.tools.productivity;

import org.pclg.log.LoggerFactory;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pablo
 * @since 30-abr-2011 19:30:20
 */
public class DataLayerConverterReplacer implements RegexReplacer {
	private static final Logger LOGGER = LoggerFactory.makeSimpleLogger();
	/** Pattern para !DataLayerConverter.isDate*Vide(var). */
	private static final String REGEX_IN3 =
			//".*(!*DataLayerConverter\\.isDate.*Vide)(\\(([\\w.]+)\\)).*"
			".*?!*(DataLayerConverter\\.isDate.*?Vide)(\\(([\\w.]+)\\)).*";
	private static final Pattern PATTERN3 = Pattern.compile(REGEX_IN3);

	/** Pattern para !DataLayerConverter.isDate*Vide(var.met()). */
	static final String REGEX_IN4 =
			".*(!*DataLayerConverter\\.isDate.*Vide)(\\(([\\w.]+\\(\\))\\)).*";
	private static final Pattern PATTERN4 = Pattern.compile(REGEX_IN4);
	private static final String BEGIN_REGEX_QUOTE = "\\Q";
	private static final String END_REGEX_QUOTE = "\\E";


	@Override
	@SuppressWarnings({"AssignmentToMethodParameter"})
	public String replace(String line) {
		Matcher matcher = PATTERN4.matcher(line);
		while (matcher.matches()) {
			LOGGER.fine("1-" + line);
			line = line.replaceFirst(BEGIN_REGEX_QUOTE + matcher.group(1)
					+ END_REGEX_QUOTE, "").replaceFirst(BEGIN_REGEX_QUOTE
					+ matcher.group(2) + END_REGEX_QUOTE,
					matcher.group(3) + " == null");
			matcher.reset(line);
			LOGGER.fine("2-" + line);
		}

		matcher = PATTERN3.matcher(line);
		while (matcher.matches()) {
			LOGGER.fine("1-" + line);
			line = line.replaceFirst(
					BEGIN_REGEX_QUOTE + matcher.group(1) + END_REGEX_QUOTE, "")
					.replaceFirst(BEGIN_REGEX_QUOTE + matcher.group(2)
							+ END_REGEX_QUOTE, matcher.group(3) + " == null");
			matcher.reset(line);
			LOGGER.fine("2-" + line);
		}
		return line;
	}
}
