/*
 * Creado el 13-mar-2008
 */
package org.pclg.dictionary;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Un autor en busca de personajes.
 */
final class DictionaryPropertites {
	private static final Properties properties = new Properties();
	static {
		try {
			final InputStream resourceStream = ClassLoader.getSystemClassLoader()
				.getResourceAsStream("org/pclg/dictionary/resources/Dictionary.properties");
			properties.load(resourceStream);
			resourceStream.close();
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 
	 */
	private DictionaryPropertites() {
	}

	/**
	 * @param string
	 * @return
	 */
	public static String getProperty(final String key) {
		String property = properties.getProperty(key);
		if (property == null) {
			property = key;
		}
		return property;
	}

}
