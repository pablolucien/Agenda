package org.pclg.agenda;

import org.pclg.tools.ObservableProperties;
import org.pclg.tools.PropertiesHelper;

import java.util.Properties;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * Class to encapsulate the management of the properties of the application,
 * since it's becoming a little convoluted.
 * @author El Coyote Cojo.
 * @since 2016.01.12.
 */
public class PropertiesManager {
    private static final String PATH_PLACEHOLDER = "[[[PATH]]]";

	private PropertiesManager() {
	}

    /** Carga todas propiedades de la aplicacion. */
	static ObservableProperties loadProperties(final String simpleName) {
		final ObservableProperties properties = new ObservableProperties();
		PropertiesHelper.loadProperties(properties, simpleName);
		resolveUrls(properties);

		final String secondaryPath = properties.getProperty("AgendaDb.secondary.path");
		final String secondaryURL = properties.getProperty("AgendaDb.secondary.url");
		final String secondaryShutdownUrl = properties.getProperty("AgendaDb.secondary.shutdown.url");
		if (!isEmptyOrBlank(secondaryPath)) {
            if (!isEmptyOrBlank(secondaryURL)) {
                properties.setProperty("AgendaDb.secondary.url",
                    secondaryURL.replace(PATH_PLACEHOLDER, secondaryPath));
            }
            if (!isEmptyOrBlank(secondaryShutdownUrl)) {
                properties.setProperty("AgendaDb.secondary.shutdown.url",
                    secondaryShutdownUrl.replace(PATH_PLACEHOLDER, secondaryPath));
            }
        }

		return properties;
	}

	public static void resolveUrls(final Properties properties) {
		final String dbPath = properties.getProperty("AgendaDb.path");
		final String dbURL = properties.getProperty("AgendaDb.url");
		final String dbShutdownUrl = properties.getProperty("AgendaDb.shutdown.url");
		if (!isEmptyOrBlank(dbPath)) {
			if (!isEmptyOrBlank(dbURL)) {
				properties.setProperty("AgendaDb.resolvedUrl",
					dbURL.replace(PATH_PLACEHOLDER, dbPath));
			}
			if (!isEmptyOrBlank(dbShutdownUrl)) {
				properties.setProperty("AgendaDb.shutdown.resolvedUrl",
					dbShutdownUrl.replace(PATH_PLACEHOLDER, dbPath));
			}
		}
	}
}
