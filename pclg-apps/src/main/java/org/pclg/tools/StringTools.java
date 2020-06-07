/*
 * Creado el 13-mar-2008
 */
package org.pclg.tools;

import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Un autor en busca de personajes.
 */
public final class StringTools {
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String LINE_SEPARATOR = "\n";
    private static final String DEFAULT_DELIMITERS = " \t\n\f\r";

	/**
	 * 
	 */
	private StringTools() {
	}

	/**
	 * Elimina las tildes de las vocales de una String.
	 * 
	 * @param string La String cuyas tildes se quieren eliminar.
	 * @return La String con las tildes eliminadas.
	 */
	public static String eliminateTildes(final String string) {
		final char[] chars = string.toCharArray();
		substituirChars(chars);
		return new String(chars);
	}

	/**
	 * Substituye todas las vocales con tilde por ellas misma sin las tildes.
	 * @param chars Un array de char cuyas tildes se quieren eliminar.
	 */
	private static void substituirChars(final char[] chars) {
		for (int ii = 0; ii < chars.length; ii++) {
			if (chars[ii] == 'á' || chars[ii] == 'à' || chars[ii] == 'â' || chars[ii] == 'ä') {
				chars[ii] = 'a';
			} else if (chars[ii] == 'é' || chars[ii] == 'è' || chars[ii] == 'ê' || chars[ii] == 'ë') {
				chars[ii] = 'e';
			} else if (chars[ii] == 'í' || chars[ii] == 'ì' || chars[ii] == 'î' || chars[ii] == 'ï') {
				chars[ii] = 'i';
			} else if (chars[ii] == 'ó' || chars[ii] == 'ò' || chars[ii] == 'ô' || chars[ii] == 'ö') {
				chars[ii] = 'o';
			} else if (chars[ii] == 'ú' || chars[ii] == 'ù' || chars[ii] == 'û' || chars[ii] == 'ü') {
				chars[ii] = 'u';
			} else if (chars[ii] == 'Á' || chars[ii] == 'À' || chars[ii] == 'Â' || chars[ii] == 'Ä') {
				chars[ii] = 'A';
			} else if (chars[ii] == 'É' || chars[ii] == 'È' || chars[ii] == 'Ê' || chars[ii] == 'Ë') {
				chars[ii] = 'E';
			} else if (chars[ii] == 'Í' || chars[ii] == 'Ì' || chars[ii] == 'Î' || chars[ii] == 'Ï') {
				chars[ii] = 'I';
			} else if (chars[ii] == 'Ó' || chars[ii] == 'Ò' || chars[ii] == 'Ô' || chars[ii] == 'Ö') {
				chars[ii] = 'O';
			} else if (chars[ii] == 'Ú' || chars[ii] == 'Ù' || chars[ii] == 'Û' || chars[ii] == 'Ü') {
				chars[ii] = 'U';
			}
		}
	}

	/**
	 * Devuelve una String con las primeras letras de cada palabra en
	 * mayúsculas y el resto en minúsculas.
	 * @param source El valor a convertir.
	 * @return una String con el valor convertido.
	 */
	public static String toTitleCase(final String source) {
		final StringBuilder buffer = new StringBuilder(source.length());
		final StringTokenizer tokenizer =
			new StringTokenizer(source, DEFAULT_DELIMITERS, true);
		while (tokenizer.hasMoreElements()) {
			final String word = tokenizer.nextToken();
			buffer.append(Character.toUpperCase(word.charAt(0)));
			if (word.length() > 1) {
				buffer.append(word.substring(1).toLowerCase());
			}
		}
		return buffer.toString();
	}

	/**
	 * Devuelve una CharSequence con el valor que se pasa repetido times veces.
	 * @param chars El valor a repetir.
	 * @param times Las veces que hay que repetirlo.
	 * @return una String con el valor que se pasa repetido times veces.
	 */
	private static <T extends CharSequence> CharSequence repeat(final T chars,
		final int times) {
		final StringBuilder buffer = new StringBuilder(chars.length() * times);
		for (int ii = 0; ii < times; ii++) {
			buffer.append(chars);
		}
		return buffer.toString();
	}

	/**
	 * Devuelve una String con el valor que se pasa repetido times veces.
	 * @param tschar El valor a repetir.
	 * @param times Las veces que hay que repetirlo.
	 * @return una String con el valor que se pasa repetido times veces.
	 */
	public static CharSequence repeat(final char tschar, final int times) {
		return repeat(String.valueOf(tschar), times);
	}

	/**
	 * Devuelve una String con el valor que se pasa repetido times veces.
	 * @param value El valor a repetir.
	 * @param times Las veces que hay que repetirlo.
	 * @return una String con el valor que se pasa repetido times veces.
	 */
	public static CharSequence repeat(final byte value, final int times) {
		return repeat(String.valueOf(value), times);
	}

	/**
	 * Devuelve una String con el valor que se pasa repetido times veces.
	 * @param value El valor a repetir.
	 * @param times Las veces que hay que repetirlo.
	 * @return una String con el valor que se pasa repetido times veces.
	 */
	public static CharSequence repeat(final short value, final int times) {
		return repeat(String.valueOf(value), times);
	}

	/**
	 * Devuelve una String con el valor que se pasa repetido times veces.
	 * @param value El valor a repetir.
	 * @param times Las veces que hay que repetirlo.
	 * @return una String con el valor que se pasa repetido times veces.
	 */
	public static CharSequence repeat(final int value, final int times) {
		return repeat(String.valueOf(value), times);
	}

	/**
	 * Devuelve una String con el valor que se pasa repetido times veces.
	 * @param value El valor a repetir.
	 * @param times Las veces que hay que repetirlo.
	 * @return una String con el valor que se pasa repetido times veces.
	 */
	public static CharSequence repeat(final long value, final int times) {
		return repeat(String.valueOf(value), times);
	}

	/**
	 * Devuelve una String con el valor que se pasa repetido times veces.
	 * @param value El valor a repetir.
	 * @param times Las veces que hay que repetirlo.
	 * @return una String con el valor que se pasa repetido times veces.
	 */
	public static CharSequence repeat(final float value, final int times) {
		return repeat(String.valueOf(value), times);
	}

	/**
	 * Devuelve una String con el valor que se pasa repetido times veces.
	 * @param value El valor a repetir.
	 * @param times Las veces que hay que repetirlo.
	 * @return una String con el valor que se pasa repetido times veces.
	 */
	public static CharSequence repeat(final double value, final int times) {
		return repeat(String.valueOf(value), times);
	}

    /**
     * Determina si el parámetro es un comentario o está en blanco. Se consideran
     * caracteres en blanco a " \t\n\r\f". Se considera un comentario si los
     * primeros caracteres no blanco son '#' '--' o "//".
     *
     * @param line la línea a comprobar.
     * @return <code>true</code> si es comentario, <code>false</code> si no.
     */
    public static boolean isCommentOrBlank(String line) {
        return line == null || (line = line.trim()).length() == 0
        	|| line.charAt(0) == '#' || line.startsWith("--")
        	|| line.startsWith("//");
    }

	/**
	 * Codifica una string para que pueda usarse bien en un URL. De momento
	 * sólo reemplaza espacios. (Debe de haber algo mejor por allí en "la red".)
	 *
	 * @param datum el dato a codificar.
	 *
	 * @return a string with the URL encoded.
	 */
	public static String urlEncode(final String datum) {
		return datum.replaceAll(" ", "%20");
	}

	/**
	 * Tries to validate the format of a string as an email.
	 * @param email the string to be validated.
	 * @return <code>true</code> if valid, <code>false</code> if not.
	 */
	public static final  boolean validEmail(final String email) {
		final int indexOfAt;
		// a@b.c
		return email != null && (indexOfAt = email.indexOf('@')) > 0
			&& indexOfAt == email.lastIndexOf('@')
			&& email.lastIndexOf('.') > indexOfAt && email.length() >= 5;
	}

	public static boolean startsWithIgnoreCase(final String string, final String target) {
		return string.toLowerCase().startsWith(target.toLowerCase());
	}

	/**
	 * Capitalizes a String.
	 *
	 * @param string the String to capitalize.
	 * @return the String capitalized.
	 */
	public static String capitalize(final String string) {
		return capitalize(string, " \t\n\r\f,;");
	}

	/**
	 * Capitalizes a String.
	 *
	 * @param string the String to capitalize.
	 * @param delimiters the delimiters to know when a word begins.
	 * @return the String to capitalized.
	 */
	private static String capitalize(final String string,
									 final String delimiters) {
		final StringBuilder newStr = new StringBuilder(string.length());
		final StringTokenizer st = new StringTokenizer(string, delimiters, true);
		while (st.hasMoreElements()) {
			final String token = st.nextToken();
			if (token.length() == 1) {
				newStr.append(Character.toUpperCase(token.charAt(0)));
			} else {
				newStr.append(Character.toUpperCase(token.charAt(0)))
					.append(token.toLowerCase().substring(1));
			}
		}
		return newStr.toString();
	}

	/**
	 * Convierte un List en un String[].
	 *
	 * @param list la List. Se supone que todos sus elementos son strings.
	 * @return a String[] with the contents of the List
	 * @since 2003.09.21
	 * --version 2004.11.19 changed vector2Array(Vector v)
	 *                     to list2StringArray(List list)
	 * --version 2004.11.19 changed to use list.toArray(). Did it existed & I know it in 2003 or 2004?
	 */
	public static String[] list2StringArray(final List<String> list) {
		return list.toArray(EMPTY_STRING_ARRAY);
	}

	/**
	 * Internaliza un array de Strings. Util para poder usar == en vez de equals.
	 *
	 * @param target el array de Strings a internalizar.
	 * @since 2004.04.04
	 */
	public static void intern(final String[] target) {
		for (int ii = target.length - 1; ii >= 0; ii--) {
			if (target[ii] != null) {
				target[ii] = target[ii].intern();
			}
		}
	}

	/**
	 * Verifies if two Strings are equal or both are 'empty' in the sense
	 * that they are <code>null</code> or contain only blanks.
	 *
	 * @param str1 the first string to compare.
	 * @param str2 the second string to compare.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	public static boolean equalEmptyOrBlank(final String str1, final String str2) {
		return isEmptyOrBlank(str1) && isEmptyOrBlank(str2)
			|| str1 != null && str1.equals(str2);
	}

	/**
	 * Verifies if a String is 'empty' in the sense that it's <code>null</code>
	 * or contains only blanks.
	 *
	 * @param str the string to verify.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	public static boolean isEmptyOrBlank(final String str) {
		return str == null || str.trim().length() == 0;
	}

    public static String wrapLine(final String line, final int len, final String indentPad) {
        boolean continuation;
		final int length = line.length();
		final StringBuilder outputLine = new StringBuilder(length * 2);
        continuation = false;
        if (length <= len) {
            outputLine.append(line).append(LINE_SEPARATOR);
        } else {    // Dividimos la linea
            final LineParser parser = new LineParser();
            parser.startLine(line);
            String word = parser.nextWord();
            final StringBuilder segment = new StringBuilder(length);
            while (word.length() != 0) {
                if (word.length() >= len) {    // ni modo
                    outputLine.append(word).append(LINE_SEPARATOR);
                    word = parser.nextWord();
                } else {
                    segment.setLength(0);
                    while (word.length() != 0 && segment.length()
                        + word.length() < len) {
                        segment.append(word);
                        word = parser.nextWord();
                    }
                    if (continuation) {
                        outputLine.append(indentPad);
                    }
                    outputLine.append(segment).append(LINE_SEPARATOR);
                    continuation = true;
                }
            }
        }
        return outputLine.toString();
    }

    public static String wrapLine(final String message) {
        return wrapLine(message, 80, "");
    }

    /**
     * LineParser
     * sirve para ir obteniendo las palabras de una linea
     * (a saber cuando escribí esta clase originalmente,
     * pero evidentemente no conocía StringTokenizer).
     * (o tal vez si lo conocía, pero necesitaba que me devolviera los blancos ;-)
     */
	private static final class LineParser {
        private String line;
        private int pos;

        void startLine(final String theLine) {
            line = theLine;
            pos = 0;
        }

        /**
         * Devuelve la siguiente palabra de una linea.
         *
         * @return la siguiente palabra de una linea.
         */
        String nextWord() {
            final StringBuilder builder = new StringBuilder();

            for (; pos < line.length(); pos++) {
                builder.append(line.charAt(pos));
                if (Character.isWhitespace(line.charAt(pos))) {
                    pos++;
                    while (pos < line.length() && Character.isWhitespace(line.charAt(pos))) {
                        builder.append(line.charAt(pos));
                        pos++;
                    }
                    break;
                }
            }
            return builder.toString();
        }
    }
}
