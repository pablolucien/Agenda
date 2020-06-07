package org.pclg.tools.productivity;

import org.pclg.log.LoggerFactory;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author paceLucien
 * @since 28-abr-2011 13:13:50
 */
public class NegationsReplacer implements RegexReplacer {
    private static final Logger LOGGER = LoggerFactory.makeSimpleLogger();
    /** Pattern para reemplazar las negaciones. */
    static final String REGEX_IN1 = ".*(!([\\w.]+\\(*\\)*) == null).*";
    private static final Pattern PATTERN1 = Pattern.compile(REGEX_IN1);

    /** Pattern para reemplazar las negaciones. OJO: Esta parece estar bien*/
    private static final String REGEX_IN2 = ".*(!\\((\\w+) == null\\)).*";
    private static final Pattern PATTERN2 = Pattern.compile(REGEX_IN2);

    @Override
	@SuppressWarnings({"AssignmentToMethodParameter"})
	public String replace(String line) {
        Matcher matcher = PATTERN2.matcher(line);
        while (matcher.matches()) {
            LOGGER.info("3-" + line);
            line = line.replace(matcher.group(1), matcher.group(2) + " != null");
            matcher.reset(line);
            LOGGER.info("4-" + line);
        }

        matcher = PATTERN1.matcher(line);
        while (matcher.matches()) {
            LOGGER.fine("5-" + line);
            line = line.replace(matcher.group(1), matcher.group(2) + " != null");
            matcher.reset(line);
            LOGGER.fine("6-" + line);
        }
        return line;
    }
}
