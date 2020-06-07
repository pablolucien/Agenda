package org.pclg.agenda;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.agenda.entities.Grupo;
import org.pclg.agenda.entities.Pais;
import org.pclg.agenda.entities.TipoTelefono;
import org.pclg.agenda.gui.AgendaGUI;
import org.pclg.gui.SplashWindow;
import org.pclg.log.LoggerFactory;
import org.pclg.runtime.RuntimeControl;
import org.pclg.security.PasswordDialog;
import org.pclg.tools.ImageTools;
import org.pclg.tools.ObservableProperties;
import org.pclg.tools.PropertiesHelper;
import org.pclg.xtras.ClassPathHacker;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.Properties;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.pclg.Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;
import static org.pclg.tools.StringTools.isEmptyOrBlank;

public final class Agenda {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    public static final String PWD_PLACEHOLDER = "[[[PWD]]]";
    public static final String PROCESS_ID =
        ManagementFactory.getRuntimeMXBean().getName();

    private final AgendaDb agendaDb;
    private final ObservableProperties properties;

    private Agenda() throws IOException {
		Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            if (!(ex instanceof ThreadDeath)) {
                LOGGER.error("UncaughtException in Thread: " + thread , ex);
                System.exit(-THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING);
            }
        });
		properties = PropertiesManager.loadProperties(getClass().getSimpleName());
		properties.addChangeObserver(this::objectChanged);

		// First thing to do: make sure we have all we need in the classpath.
		updateClassPath();

		// Necesitamos un frame para hacer de padre putativo de los di�logos
		// hasta que se cree el la ventana de verdad. SplashWindow cumple esa funci�n
        final SplashWindow splashWindow = new SplashWindow(
            ImageTools.getImageIcon(getStringFromProperties(properties, "Agenda.SplashWindow.image")).orElse(null),
            "/sounds/sbcdrop.wav"
        );
        splashWindow.setStatusColor(Color.white);
        splashWindow.setStatus("inicializando");
        splashWindow.setTitle(getStringFromProperties(properties, "AgendaDb.title"));
		ImageTools.getImageIcon(getStringFromProperties(properties, "Agenda.image"))
			.ifPresent(icon -> splashWindow.setIconImage(icon.getImage()));
		if (Boolean.parseBoolean(properties.getProperty("Agenda.show.SplashWindow"))) {
			splashWindow.setVisible(true);
		}

        final String dbURL = properties.getProperty("AgendaDb.resolvedUrl");
        AgendaLogin.setPassword(dbURL.contains(PWD_PLACEHOLDER) ? getPasswordOrExit(splashWindow) : parsePassword(dbURL));
        properties.setProperty("AgendaDb.user", AgendaLogin.getUsername());
        properties.setProperty("AgendaDb.password", AgendaLogin.getPassword());
		agendaDb = AgendaDbFactory.getAgendaDb(properties);
        RuntimeControl.registerShutdownHook(() -> {
            try {
                LOGGER.log(Level.OFF, "Going to stop database");
                agendaDb.stopDb(true, properties.getProperty("AgendaDb.shutdown.resolvedUrl"));
            } catch (final SQLException ex) {
                //noinspection HardCodedStringLiteral
                LOGGER.log(Level.ERROR, "Error en ShutdownHook", ex);
            }
        });
        startApp(properties, splashWindow);
	}

    /**
     * Parses the password from a derby URL. The password can't contain the 
     * character ';' 'cause it breaks the parsing mechanism (mine yes, but
     * also that from Derby ;)
     * 
     * @param dbURL the URL.
     * @return the password or "" if not parsed.
     */
	private static String parsePassword(final String dbURL) {
		String retVal = "";
		final String[] elements = dbURL.split(";");
		for (final String element : elements) {
			if (element.startsWith("bootPassword")) {
				retVal = element.split("=", 2)[1];
			}
		}
		return retVal;
	}

	private String getPasswordOrExit(final SplashWindow parent) {
        final PasswordDialog passwordDialog = new PasswordDialog(parent, properties);
		parent.setStatus("Getting password");

        if (passwordDialog.showDialog() != OK_OPTION) {
            System.exit(0);
        }
		parent.setStatus("Inicializando");

		final String pwd = new String(passwordDialog.getPassword());
        if (pwd.length() == 0) {
            final String msgNoPassword =
                getStringFromProperties(properties, "Agenda.pwd.errMsg");
            showMessageDialog(parent, msgNoPassword,
                getStringFromProperties(properties, "Agenda.alert.title"),
                ERROR_MESSAGE);
            LOGGER.log(Level.ERROR, msgNoPassword);
            System.exit(1);
        }
		return pwd;
	}

	private void updateClassPath() throws IOException {
		final String applicationClassPath =
			properties.getProperty("Application.ClassPath");
		//noinspection HardCodedStringLiteral
		LOGGER.debug("Application.ClassPath = " + applicationClassPath);
		if (!isEmptyOrBlank(applicationClassPath)) {
			ClassPathHacker.addFiles(applicationClassPath.split("\\|"));
		}
	}

	private void startApp(final Properties appProperties, final SplashWindow parent) {
		try {
            final boolean createTables = Boolean.parseBoolean(appProperties.getProperty("AgendaDb.createTables"));
            if (createTables && showConfirmDialog(parent,
                "�Borrar las tablas?", "Cuidaito compae gallo",
                YES_NO_OPTION, WARNING_MESSAGE)
                    == OK_OPTION) {
                agendaDb.initDb(appProperties, true, true);
            } else {
                agendaDb.initDb(appProperties, true, false);
            }
			try {
                parent.setStatus("checkLastUpdated");
				if (!agendaDb.checkLastUpdated(properties, parent)) {
					System.exit(1);
				}
			} catch (final SQLException ex) {
				LOGGER.log(Level.ERROR, "Error checking last update. Going ahead fearlesslly", ex);
			} finally {
				initDbPhase2(appProperties, parent);
			}
		} catch (final Throwable e) {
			//noinspection HardCodedStringLiteral
			LOGGER.error("exception thrown:");
            final String errMsg;
			if (e instanceof SQLException) {
				final SQLException sqlException = (SQLException) e;
                final String sqlState = sqlException.getSQLState();
                if ("XBM06".equals(sqlState)) {
					errMsg = getStringFromProperties(appProperties, "Agenda.pwd.errMsg");
					showMessageDialog(parent, errMsg,
                        getStringFromProperties(appProperties, "Agenda.alert.title"),
                        ERROR_MESSAGE);
					LOGGER.log(Level.ERROR, errMsg);
				} else {
                    errMsg = AgendaUtil.printSQLError(sqlException);
                }
			} else {
				//noinspection HardCodedStringLiteral
				LOGGER.log(Level.ERROR, "Error en startApp()", e);
                errMsg = e.toString();
			}
            showMessageDialog(parent, errMsg,
                getStringFromProperties(appProperties, "Agenda.alert.title"),
                ERROR_MESSAGE);
			System.exit(-1);
		}
	}

	private void initDbPhase2(final Properties appProperties, final SplashWindow splashWindow)
			throws SQLException {
		Pais.setUnknownCountry(
			appProperties.getProperty("AgendaGUI.paisDesconocido"));
		agendaDb.loadCountries();
		Pais.setDefaultCountry(Pais.getInstance(
            getStringFromProperties(appProperties, "Agenda.default.country")));
		Grupo.init(agendaDb.getListaGrupos());
		TipoTelefono.init(agendaDb.getListaTiposTelefono());
        splashWindow.setStatus("initDbPhase2");
		SwingUtilities.invokeLater(() -> {
			try {
                new AgendaGUI(appProperties, agendaDb, splashWindow);
				//noinspection HardCodedStringLiteral
				LOGGER.log(Level.INFO, "Agenda started");
			} catch (final SQLException e) {
				AgendaUtil.printSQLError(e);
			}
		});
	}

	private void objectChanged(final ObservableProperties observableProperties) {
		if (observableProperties == properties) {
	        LOGGER.log(Level.TRACE, "properties modificada");
			Pais.setDefaultCountry(Pais.getInstance(
				getStringFromProperties(properties,
                    "Agenda.default.country")));
			try {
				// evitamos generar infinitas notificaciones al cambiar de idioma.
				observableProperties.enableNotifications(false);
				PropertiesHelper.loadProperties4Language(
					getClass().getSimpleName(), properties,
					getStringFromProperties(properties,
						"Application.ForceLanguage"));
			} finally {
				observableProperties.enableNotifications(true);
			}
		}
	}

	public static void main(final String[] args) {
        LOGGER.log(Level.OFF, "************* Starting Agenda. My process ID is probably: "
            + PROCESS_ID);
		try {
			new Agenda();
		} catch (final MissingResourceException ex) {
			//noinspection HardCodedStringLiteral
			LOGGER.error("ERROR obteniendo recursos: " + ex);
		} catch (final Throwable throwable) {
			//noinspection HardCodedStringLiteral
			LOGGER.log(Level.ERROR, "Error inicializando Agenda", throwable);
			System.exit(-1);
		}
	}
}
