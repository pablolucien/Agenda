package org.pclg.security;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;
import org.pclg.tools.ObservableProperties;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.ToolTipManager;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import static javax.swing.JOptionPane.CLOSED_OPTION;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 27/12/17 17:41
 */
public final class PasswordDialog {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final JPasswordField passwordField = new JPasswordField();
    private final JDialog dialog;
    private final JOptionPane pane;
    private final String toolTipText;
    private final String iconPath;

    /**
	 * Shows a dialog to get a password.
	 *
	 * @param parent The component that acts as parent of the dialog.
	 * @param properties the properties containing the texts shown by the dialog:
	 *                   <ul>
	 *                   <li>PasswordDialog.pwd.prompt</li>
	 *                   <li>PasswordDialog.warning.icon</li>
	 *                   <li>PasswordDialog.alert.capsLock</li>
	 *                   </ul>
	 */
    public PasswordDialog(final Component parent, final Properties properties) {
        pane = new JOptionPane(passwordField, PLAIN_MESSAGE, OK_CANCEL_OPTION);
        dialog = pane.createDialog(parent, getStringFromProperties(properties, "PasswordDialog.pwd.prompt"));
        iconPath = getStringFromProperties(properties, "PasswordDialog.warning.icon");
        toolTipText = getStringFromProperties(properties, "PasswordDialog.alert.capsLock");
        // Es m�s c�modo para el usuario que el passwordField tenga el foco inicialmente.
        GUITools.requestFocusInDialog(passwordField);
    }

    public int showDialog() {
        final Timer timer = new Timer(true);
      		timer.schedule(new TimerTask() {
      			@Override
      			public void run() {
                    LOGGER.debug("Checking CapsLock");
                    if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
                        ImageTools.getImageIcon(iconPath).ifPresent(pane::setIcon);
                        passwordField.setToolTipText(toolTipText);
                        ToolTipManager.sharedInstance().mouseMoved(
                            new MouseEvent(passwordField, 0, 0, 0,
                                0, 0, // X-Y of the mouse for the tool tip
                                0, false));
                    } else {
                        pane.setIcon(null);
                        passwordField.setToolTipText("");
                        dialog.repaint();
                    }
                    passwordField.requestFocus();
      			}
      		}, 100, 1000);
        dialog.setVisible(true);
        final Object value = pane.getValue();
        timer.cancel();
        return value instanceof Integer ? ((Integer) value).intValue() : CLOSED_OPTION;
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public static void main(final String[] args) throws IOException {
        final ObservableProperties properties = new ObservableProperties();
        properties.setProperty("PasswordDialog.alert.capsLock", "Bloquear may\u00A1sculas activado");
        properties.setProperty("PasswordDialog.pwd.prompt", "Enter password");
        properties.setProperty("PasswordDialog.warning.icon", "/toolbarButtonGraphics/com_incors_plaf_alloy_icons/Warn.png");
        final PasswordDialog passwordDialog = new PasswordDialog(null, properties);
        if (passwordDialog.showDialog() == OK_OPTION) {
            LOGGER.warn(new String(passwordDialog.getPassword()));
        }
        System.exit(0);
    }
}
