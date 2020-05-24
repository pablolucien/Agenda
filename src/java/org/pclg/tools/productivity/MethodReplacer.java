package org.pclg.tools.productivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author paceLucien
 * @since 28-abr-2011 13:33:37
 */
public class MethodReplacer implements RegexReplacer {
	public enum MethodName {
        GETINTANNEE {
            @Override
			public String toString() {
                return "getIntAnnee";
            }
        },
        GETINTMOIS {
            @Override
			public String toString() {
                return "getIntMois";
            }
        },
        GETINTJOUR {
            @Override
			public String toString() {
                return "getIntJour";
            }
        }
    }

    private final Pattern pattern;
    private final String methodName;

    public MethodReplacer(final MethodName methodName) {
        final String regexIn= new StringBuilder()
            .append(".*[\\(\\s\\&\\|]!*((\\w+)\\.").append(methodName)
            .append("\\s*\\(\\s*\\)).*").toString();
        pattern = Pattern.compile(regexIn);
        this.methodName = methodName.toString();
    }

    @Override
    @SuppressWarnings({"AssignmentToMethodParameter"})
	public String replace(String line) {
        final Matcher matcher = pattern.matcher(line);
        while (matcher.matches()) {
            line = line.replace(matcher.group(1), new StringBuilder()
                .append("DateUtil.").append(methodName).append('(')
                .append(matcher.group(2)).append(')').toString());

             matcher.reset(line);
        }
        return line;
    }
}