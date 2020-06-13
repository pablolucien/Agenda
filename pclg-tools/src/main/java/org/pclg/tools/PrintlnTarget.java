package org.pclg.tools;

/**
 * Una interfaz para cosas que tienen println(). Probablemente dure poco.
 * @author El Coyote Cojo
 * @since 9/09/18 17:06
 */
public interface PrintlnTarget {
	void println();
	void println(final String text);
}
