package org.pclg.log;

import org.pclg.tools.PropertiesHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LoggerFactory {
	/** WARNING: The declaration of the  SecurityManager must come before that of the Logger. */
	private static final SecurityManager MANAGER = new SecurityManager();
	/** El logger de esta clase. */
	private static final Logger LOGGER = make();

	public static final String ERROR_TAG = "Whitney, we have a problem!";
	public static final String WARN_TAG = "?Como vaya yo y lo encuentre...!";
    public static final String ENTER_METHOD = "Enter method";
    public static final String EXIT_METHOD = "Exit method";
	private static final String MSG_CREATE = "Creando logger para: ";
	private static String[] ERR_MESSAGES = null;
    static final String COULD_NOT_LOAD_CUSTOM_MESSAGES = "Could not load custom messages";

    static {
        try {
			configure();
		} catch (final IOException ignore) {
		}
	}

	/** Prevents instantiation. */
	private LoggerFactory() {
	}

	/**
	 * Crea un logger adecuado para la clase que llama a este m?todo.
	 * @return un logger.
     *
     * @deprecated Este m?todo es demasiado costoso. Usar make()
	 */
    @Deprecated
	public static Logger make2() {
		final StackTraceElement directCaller =
                new Throwable().getStackTrace()[1];
		final String className = directCaller.getClassName();
        reportCreation(className);
        return Logger.getLogger(className);
	}

    /**
	 * Crea un logger adecuado para la clase que llama a este m?todo.
	 * @return un logger.
	 */
	public static Logger make() {
        final String className = getTargetClassName();
        return Logger.getLogger(className);
	}

	/**
	 * Crea un logger (de Log4j) adecuado para la clase que llama a este m?todo.
	 *
	 * @return un logger.
	 */
	public static org.apache.logging.log4j.Logger makeLog4J() {
        final String className = getTargetClassName();
        return org.apache.logging.log4j.LogManager.getLogger(className);
	}

	/**
	 * Crea un logger simple para la clase que llama a este m?todo.
	 * @return un logger.
	 * @deprecated utilizar <code>make()</code> y <code>configure()</code>
	 */
	@Deprecated
    public static Logger makeSimpleLogger() {
        final String className = getTargetClassName();
        final Logger logger = Logger.getLogger(className);
        logger.setUseParentHandlers(false);
        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);
//        final Handler[] handlers = logger.getHandlers();
//        for (final Handler handler : handlers) {
//            LOGGER.severe("handler.getClass() = " + handler.getClass());
//            handler.setFormatter(new SimpleFormatter());
//        }
        return logger;
    }

    private static String getTargetClassName() {
        final Class<?>[] clases = MANAGER.listClasses();
        String className = null;
        // clases[0] == MANAGER.class
        for (int ii = 1; ii < clases.length; ii++) {
            if (clases[ii] == LoggerFactory.class) {
                continue;
            }
            className = clases[ii].getName();
            break;
        }
        reportCreation(className);
        return className;
    }

    /**
     * Reports the creation of the logger a class.
     * @param className the name of the class for which the logger is being
	 *                  created.
     */
    private static void reportCreation(final String className) {
        if (LOGGER != null) {
            LOGGER.log(Level.FINER, MSG_CREATE + className);
        }
    }

	/**
	 * Tries to configure the logging system using the
	 * file <pre>pclg.default.logging.properties</pre>
	 * which should be in the execution directory. Swallows exceptions if this
	 * file doesn't exist.
	 *
	 */
	public static void configure() throws IOException {
        // TODO: ver si deber?a ser llamado en un bloque static.
      	// TODO: intentarlo tambien con un recurso en el classpath.
		final String logPropertiesFile = "pclg.default.logging.properties";
		try {
			configure(logPropertiesFile);
		} catch (final FileNotFoundException ignored) {
//			LOGGER.log(Level.SEVERE, "Log properties file <"
//				+ logPropertiesFile + "> not found. Using default configuration");
		}
	}

	/**
	 * Tries to configure the logging system using the supplied file.
	 *
	 * @param logPropertiesFile the configuration file.
	 * @throws IOException if something goes awry.
	 */
	private static void configure(final String logPropertiesFile)
			throws IOException {
		System.setProperty("java.util.logging.config.file", logPropertiesFile);
		final LogManager logManager = LogManager.getLogManager();
		logManager.readConfiguration();
	}

    public static String stackTrace2String(final Throwable thrown) {
        final StringWriter sw = new StringWriter();
        try (final PrintWriter pw = new PrintWriter(sw)) {
            thrown.printStackTrace(pw);
            return sw.toString();
        }
    }

	public static String getRandomErrorMessage() {
        if (ERR_MESSAGES == null || ERR_MESSAGES.length == 1) {
            final Properties properties = new Properties();
            final String fileName = "LoggerFactory_messages.properties";
            try {
                PropertiesHelper.loadPropertiesFromClasspath(properties, fileName);
            } catch (final IOException ex) {
                LOGGER.log(Level.SEVERE, fileName, ex);
            }
            final int size = properties.size();
            if (size > 0) {
                ERR_MESSAGES = new String[size];
                properties.values().toArray(ERR_MESSAGES);
            }
        }
		return ERR_MESSAGES == null ? COULD_NOT_LOAD_CUSTOM_MESSAGES : ERR_MESSAGES[new Random().nextInt(ERR_MESSAGES.length)];
	}
}