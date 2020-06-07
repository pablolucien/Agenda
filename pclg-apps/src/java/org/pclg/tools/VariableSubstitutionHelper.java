package org.pclg.tools;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tomado de https://stackoverflow.com/questions/9526291/java-regex-replace-var y modificado.
 * @since 04/03/2019.
 */
public class VariableSubstitutionHelper {
    private static final Pattern SUBSTITUTION_PATTERN = Pattern.compile("\\$\\{(.+?)}");

    private VariableSubstitutionHelper() {}

    public static String replace(final String expression, final Map<String, String> variablesMapping ) {
        final Matcher matcher = SUBSTITUTION_PATTERN.matcher(expression);

        final StringBuilder builder = new StringBuilder();
        int currPosition = 0;
        while (matcher.find()) {
            final String replacement = variablesMapping.get(matcher.group(1));
            builder.append(expression, currPosition, matcher.start());
            if (replacement == null) {
                builder.append(matcher.group(0));
            } else {
                builder.append(replacement);
            }
            currPosition = matcher.end();
        }
        builder.append(expression.substring(currPosition));
        return builder.toString();
    }
}
