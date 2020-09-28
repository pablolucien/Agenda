package org.pclg.runtime;

import org.apache.logging.log4j.Logger;
import org.pclg.gui.InfoPanel;
import org.pclg.log.LoggerFactory;
import org.pclg.security.Crypto;
import org.pclg.security.PasswordDialog;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.StringTools;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.Properties;

import static javax.swing.JOptionPane.OK_OPTION;
import static org.pclg.Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;
import static org.pclg.tools.PropertiesHelper.getBooleanFromProperties;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * @author El Coyote Cojo
 * @since 3/08/19 10:36
 */
final class ProcessLauncher {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private InfoPanel infoPanel;
	/** In some environnements (launching by PStart on Windows 7 in Manoteras, showing the InfoPanel causes the JVM to crash. */
	private boolean showInfoPanel = true;

	private void start(final String bundleBaseName) throws IOException, GeneralSecurityException {
		LOGGER.debug(LoggerFactory.ENTER_METHOD);
		final Properties properties = new Properties();
        PropertiesHelper.loadProperties(properties, bundleBaseName);
		final File targetFile = new File(getStringFromProperties(properties, "ProcessLauncher.FilePath"));
		final String title = PropertiesHelper.getStringFromProperties(properties, "ProcessLauncher.title");
		if (!targetFile.exists()) {
			JOptionPane.showMessageDialog(null, MessageFormat.format(getStringFromProperties(
					properties, "ProcessLauncher.file.not.found"), targetFile),
				title, JOptionPane.ERROR_MESSAGE);
			System.exit(THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING);
		}
		final String password = getPassword(properties);
		showInfoPanel = PropertiesHelper.getBooleanFromProperties(properties, "ProcessLauncher.showInfoPanel", true);
		if (showInfoPanel) {
			infoPanel = new InfoPanel(title, "");
		}
		LOGGER.debug("Going to check encrypted");
		if (isEncrypted(targetFile)) {
			LOGGER.debug("Going to decrypt");
			decrypt(targetFile, password, properties);
		}

        final Executioner executioner = new Executioner(
            bundleBaseName,
            getBooleanFromProperties(properties, "ProcessLauncher.show.stdout", true),
            getBooleanFromProperties(properties,"ProcessLauncher.show.stderr", true),
            getStringFromProperties(properties, "ProcessLauncher.CommandPath"),  targetFile.getAbsolutePath());

        executioner.launchAndWait();

		encryptAllNotEncrypted(targetFile.getParentFile(), password, properties);
		LOGGER.debug(LoggerFactory.EXIT_METHOD);
		System.exit(0);
	}

	private static String getPassword(final Properties properties) {
		String password = getStringFromProperties(properties, "ProcessLauncher.Password");
		if (StringTools.isEmptyOrBlank(password)) {
			final PasswordDialog passwordDialog = new PasswordDialog(null, properties);

			if (passwordDialog.showDialog() != OK_OPTION) {
				System.exit(0);
			}
			password = new String(passwordDialog.getPassword());
		}
		return password;
	}

	private static boolean isEncrypted(final File file) {
		try {
			final boolean encrypted = Crypto.isEncrypted(file);
			LOGGER.debug(LoggerFactory.EXIT_METHOD);
			return encrypted;
		} catch (final IOException ex) {
			LOGGER.error(LoggerFactory.ERROR_TAG, ex);
			throw new RuntimeException(ex);
		}
	}

	private void decrypt(final File file, final String password, final Properties properties)
			throws GeneralSecurityException, IOException {
		LOGGER.debug(LoggerFactory.ENTER_METHOD);
		final String message = PropertiesHelper.getStringFromProperties(properties, "InfoPanel.deciphering");
		showMessage(message);
		final Crypto crypto = new Crypto(password, true);
		crypto.processFile(file, -1, message);
		cleanMessage();
		LOGGER.debug(LoggerFactory.EXIT_METHOD);
	}

	private void encryptAllNotEncrypted(final File dir, final String password, final Properties properties)
			throws GeneralSecurityException, IOException {
		LOGGER.debug(LoggerFactory.ENTER_METHOD);
		final String message = PropertiesHelper.getStringFromProperties(properties, "InfoPanel.enciphering");
		showMessage(message);
		final Crypto crypto = new Crypto(password, false);
		final File[] files = dir.listFiles(f -> !isEncrypted(f));
		if (files != null) {
			for (final File file : files) {
				crypto.processFile(file, -1, message);
			}
		}
		cleanMessage();
		LOGGER.debug(LoggerFactory.EXIT_METHOD);
	}

	private void showMessage(final String message) {
		if (showInfoPanel) {
			infoPanel.setMessage(message);
			infoPanel.setVisible(true);
		}
	}

	private void cleanMessage() {
		if (showInfoPanel) {
			infoPanel.setVisible(false);
		}
	}

	/**
	 * Applications entry point.
	 */
	public static void main(final String[] args) {
		try {
			new ProcessLauncher().start(args.length > 0 ? args[0] : ProcessLauncher.class.getSimpleName());
		} catch (final Exception ex) {
            final String message = ex.getLocalizedMessage() + '\n' + LoggerFactory.stackTrace2String(ex);
            LOGGER.error(message);
            JOptionPane.showMessageDialog(null, message, LoggerFactory.getRandomErrorMessage(), JOptionPane.ERROR_MESSAGE);
			System.exit(THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING);
		}
	}
}
