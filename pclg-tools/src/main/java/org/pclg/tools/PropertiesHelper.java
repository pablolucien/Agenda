package org.pclg.tools;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;

import javax.swing.JComponent;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

/**
 * @author Pablo
 * @since 13/01/16 20:12
 */
public class PropertiesHelper {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final String TECHNICAL_SUFFIX = "_technical.properties";
	private static final String CUSTOM_SUFFIX = "_custom.properties";
	private static final String SQL_SUFFIX = "_SQL.properties";
	private static final String ICONS_SUFFIX = "_icons";
	private static final String BOUNDS_X = "Bounds.x";
	private static final String BOUNDS_Y = "Bounds.y";
	private static final String BOUNDS_W = "Bounds.w";
	private static final String BOUNDS_H = "Bounds.h";
	private static final String BOUNDS_MAXIMIZED = "MAXIMIZED";
	private static final String BOUNDS_MINIMIZED = "MINIMIZED";

    private static class PropertiesFileHolder {
        private final File propertiesFile;
        private final Properties properties;
        private final long lastModified;

        private PropertiesFileHolder(final File propertiesFile) throws IOException {
            this.propertiesFile = propertiesFile;
            try (final FileInputStream in = new FileInputStream(propertiesFile)) {
                (properties = new Properties()).load(in);
            }
            lastModified = propertiesFile.lastModified();
        }

        private boolean isDirty() {
            return propertiesFile.lastModified() != lastModified;
        }

        @Override
        protected void finalize() throws Throwable {
            LOGGER.debug("Finalizing PropertiesFileHolder for " + propertiesFile);
            super.finalize();
        }
    }

    private static final Map<File, PropertiesFileHolder> propertiesFileHolderMap = new WeakHashMap<>();

    private PropertiesHelper() {
    }

	public static void loadProperties(final Properties properties, final String baseName) {
		loadPropertiesFromFileIfExists(properties, baseName + TECHNICAL_SUFFIX);
		// _custom cargarla despu?s de _technical para que sus valores tengan preferencia.
		loadCustomProperties(properties, baseName);
		loadPropertiesFromFileIfExists(properties, baseName + SQL_SUFFIX);
		final String forcedLanguage = properties.getProperty("Application.ForceLanguage");
		if (StringTools.isEmptyOrBlank(forcedLanguage)) {
			final ResourceBundle  i18nProperties = ResourceBundle.getBundle(baseName);
			for (final String key : i18nProperties.keySet()) {
				properties.setProperty(key, i18nProperties.getString(key));
			}
			try {
				final ResourceBundle  i18nIconsProperties = ResourceBundle.getBundle(baseName + ICONS_SUFFIX);
				for (final String key : i18nIconsProperties.keySet()) {
					properties.setProperty(key, i18nIconsProperties.getString(key));
				}
			} catch (final MissingResourceException ex) {
				LOGGER.warn(LoggerFactory.ERROR_TAG, ex);
			}
		} else {
			loadProperties4Language(baseName, properties, forcedLanguage);
		}
	}

	/**
     * Obtener propiedades desde un archivo
     */
    public static String getProperty(final String filename, final String propertyName) throws IOException {
        return getProperty(filename, propertyName, null);
    }

    /**
     * Obtener propiedades desde un archivo
     */
    public static String getProperty(final String filename,
            final String propertyName, final String defaultValue) throws IOException {
        final File propertiesFile = new File(filename);
        PropertiesFileHolder propertiesFileHolder = propertiesFileHolderMap.get(propertiesFile);
        if (propertiesFileHolder == null || propertiesFileHolder.isDirty()) {
            propertiesFileHolder = new PropertiesFileHolder(propertiesFile);
            propertiesFileHolderMap.put(propertiesFile, propertiesFileHolder);
        }
        return propertiesFileHolder.properties.getProperty(propertyName, defaultValue);
    }

    /**
	 * Obtiene un String de las properties. Si no la encuentra, devuelve la key.
	 *
	 * @param properties d?nde se van a buscar los recursos.
	 * @param key        La clave para buscar.
	 * @return El valor asociado a la clave o esta si aquel no existe.
	 */
	public static String getStringFromProperties(
		final Properties properties, final String key) {
		if (key == null) {
			LOGGER.warn("Error tratando de obtener recurso con una clave nula");
			return "<null>";
		}
		final String property = properties.getProperty(key);
		if (property == null) {
			LOGGER.warn("Error tratando de obtener recurso: " + key);
			return key;
		}
		return property;
	}

	/**
	 * Obtiene un String de las properties. Si no la encuentra, devuelve el defaultValue.
	 *
	 * @param properties d?nde se van a buscar los recursos.
	 * @param key        La clave para buscar.
	 * @param defaultValue El valor por defecto.
	 * @return El valor asociado a la clave o esta si aquel no existe.
	 */
	public static String getStringFromProperties(final Properties properties,
            final String key, final String defaultValue) {
		if (key == null) {
			LOGGER.warn("Error tratando de obtener recurso con una clave nula");
			return "<null>";
		}
		final String property = properties.getProperty(key);
		if (property == null) {
			LOGGER.warn("Error tratando de obtener recurso: " + key);
			return defaultValue;
		}
		return property;
	}

	/**
	 * Obtiene un boolean de las properties. Si no lo encuentra, devuelve el defaultValue.
	 *
	 * @param properties d?nde se van a buscar los recursos.
	 * @param key        La clave para buscar.
	 * @param defaultValue El valor por defecto.
	 * @return El valor asociado a la clave o esta si aquel no existe.
	 */
	public static boolean getBooleanFromProperties(final Properties properties,
            final String key, final boolean defaultValue) {
		if (key == null) {
			LOGGER.warn("Error tratando de obtener recurso con una clave nula");
			return defaultValue;
		}
		final String property = properties.getProperty(key);
		if (property == null) {
			LOGGER.warn("Error tratando de obtener recurso: " + key);
			return defaultValue;
		}
		return Boolean.valueOf(property).booleanValue();
	}

	/**
	 * Obtiene un int de las properties. Si no lo encuentra, devuelve el defaultValue.
	 *
	 * @param properties d?nde se van a buscar los recursos.
	 * @param key        La clave para buscar.
	 * @param defaultValue El valor por defecto.
	 * @return El valor asociado a la clave o esta si aquel no existe.
	 */
	public static int getIntFromProperties(final Properties properties, final String key, final int defaultValue) {
		if (key == null) {
			throw new IllegalArgumentException("Error tratando de obtener recurso con una clave nula");
		}
		final String property = properties.getProperty(key);
		if (property == null) {
			LOGGER.warn("Error tratando de obtener recurso [" + key + "]");
			return defaultValue;
		}
		int retVal = defaultValue;
		try {
			retVal = Integer.valueOf(property);
		} catch (final NumberFormatException ex) {
			LOGGER.warn("Error tratando de convertir en int el valor [" + property + "]");
		}
		return retVal;
	}

	/**
	 * Hace lo mismo que loadPropertiesFromFile() pero se traga los errores.
	 */
	public static void loadPropertiesFromFileIfExists(final Properties properties,
        	final String fileName) {
		try {
			final File propsFile = new File(fileName);
			if (propsFile.exists()) {
				loadPropertiesFromFile(properties, fileName);
			} else {
				LOGGER.warn(LoggerFactory.WARN_TAG + ": " + fileName + " doesn't exist.");
			}
		} catch (final IOException ex) {
			LOGGER.warn(LoggerFactory.WARN_TAG, ex);
		}
	}

	public static void loadPropertiesFromFile(final Properties properties,
			final String fileName) throws IOException {
		final File propsFile = new File(fileName);
		//noinspection HardCodedStringLiteral
		LOGGER.debug("Properties File = " + propsFile.getAbsolutePath());
		try (final InputStream in = new FileInputStream(propsFile)) {
			properties.load(in);
		}
	}

	public static void loadCustomProperties(final Properties properties, final String baseName) {
		loadPropertiesFromFileIfExists(properties, baseName + CUSTOM_SUFFIX);
	}

	public static void saveCustomProperties(final Properties properties, final String owner)
			throws IOException {
		final File propsFile = new File(owner + CUSTOM_SUFFIX);
		//noinspection HardCodedStringLiteral
		LOGGER.debug("Properties File = " + propsFile.getAbsolutePath());
		try (final FileOutputStream outputFile = new FileOutputStream(propsFile)) {
            properties.store(outputFile, "Configuracion de " + owner);
        }
	}

	public static void loadPropertiesFromClasspath(final Properties properties,
			final String fileName) throws IOException {
		//noinspection HardCodedStringLiteral
		LOGGER.debug("Properties File = " + fileName);
		try (final InputStream in = ClassLoader.getSystemResourceAsStream(fileName)) {
			properties.load(in);
		} catch (final Exception ex) {
			LOGGER.error("Error loading properties from file " + fileName, ex);
		}
	}

	public static void loadProperties4Language(final String baseName,
			final Properties properties, final String forcedLanguage) {
		final Locale locale = new Locale(forcedLanguage);
		JComponent.setDefaultLocale(locale);

		final ResourceBundle i18nProperties = ResourceBundle.getBundle(
			baseName, Locale.forLanguageTag(forcedLanguage));
		for (final String key : i18nProperties.keySet()) {
			properties.setProperty(key, i18nProperties.getString(key));
		}

		try {
			final ResourceBundle  i18nIconsProperties = ResourceBundle.getBundle(
				baseName + ICONS_SUFFIX, Locale.forLanguageTag(forcedLanguage));
			for (final String key : i18nIconsProperties.keySet()) {
				properties.setProperty(key, i18nIconsProperties.getString(key));
			}
		} catch (final MissingResourceException ex) {
			LOGGER.warn(LoggerFactory.ERROR_TAG, ex);
		}
	}

	/**
	 * Saves the bounds of a rectangle to a properties object.
	 * @param boundsInfo the info to save (never null).
	 * @param properties the object where to save.
	 * @param prefix the prefix of the save property; it gives [prefix]Bounds.x, [prefix]Bounds.y,
	 *                  [prefix]Bounds.w, [prefix]Bounds.h
	 */
	public static void saveBounds(final BoundsInfo boundsInfo, final Properties properties, final String prefix) {
		final Rectangle bounds = boundsInfo.getBounds();
		if (!bounds.equals(BoundsInfo.NULL_RECTANGLE)) {        // Si no est?n todos en cero
            properties.setProperty(prefix + BOUNDS_X, String.valueOf(bounds.x));
            properties.setProperty(prefix + BOUNDS_Y, String.valueOf(bounds.y));
            properties.setProperty(prefix + BOUNDS_W, String.valueOf(bounds.width));
            properties.setProperty(prefix + BOUNDS_H, String.valueOf(bounds.height));
        }
		properties.setProperty(prefix + BOUNDS_MAXIMIZED, String.valueOf(boundsInfo.isMaximized()));
		properties.setProperty(prefix + BOUNDS_MINIMIZED, String.valueOf(boundsInfo.isMinimized()));
	}

	/**
	 * Gets the bounds of a rectangle from a properties object.
	 * @param properties the object where to get the bounds.
	 * @param prefix  the prefix of the save property.
	 * @return the BoundsInfo (never null).
	 */
	public static BoundsInfo getBounds(final Properties properties, final String prefix) {
		final BoundsInfo boundsInfo = new BoundsInfo();
		boundsInfo.setMaximized(Boolean.valueOf(properties.getProperty(prefix + BOUNDS_MAXIMIZED)));
		boundsInfo.setMinimized(Boolean.valueOf(properties.getProperty(prefix + BOUNDS_MINIMIZED)));
		try {
			final Rectangle rectangle = new Rectangle(Integer.parseInt(properties.getProperty(prefix + BOUNDS_X)),
					Integer.parseInt(properties.getProperty(prefix + BOUNDS_Y)),
					Integer.parseInt(properties.getProperty(prefix + BOUNDS_W)),
					Integer.parseInt(properties.getProperty(prefix + BOUNDS_H)));
			boundsInfo.setBounds(rectangle);
		} catch (final NumberFormatException ex) {
			LOGGER.warn("Error loading bounds", ex);
		}
		return boundsInfo;
	}

	public static List<Pair<String, String>> getPropertiesToPersist(final Properties properties, final String key) {
		final var property = properties.getProperty(key);
		if (property != null) {
			final var keysAndvalues = property.split("\\|");
			final List<Pair<String, String>> propertiesToPersist = new ArrayList<>(keysAndvalues.length);
			for (final String string : keysAndvalues) {
				final var split = string.split(":");
				if (split.length > 1) {
					propertiesToPersist.add(new Pair<>(split[0], split[1]));
				} else {
					propertiesToPersist.add(new Pair<>(split[0], ""));
				}
			}
			return propertiesToPersist;
		}
		return Collections.emptyList();
	}
}
