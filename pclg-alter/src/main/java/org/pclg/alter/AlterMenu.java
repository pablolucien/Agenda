package org.pclg.alter;

import org.pclg.gui.LnFController;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import java.awt.event.ActionListener;
import java.util.Properties;

import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * @author El Coyote Cojo
 * @since 6/01/18 12:23
 */
final class AlterMenu extends JMenuBar {
	private static final long serialVersionUID = 4201947833450286509L;

	AlterMenu(final Properties properties, final ActionListener listener) {
		final ListenerHelper listenerHelper = new ListenerHelper(listener);
		final Class<? extends AlterMenu> myClass = getClass();
		final JMenu fileMenu = new JMenu(
			PropertiesHelper.getStringFromProperties(properties, "menu.file"));
		GUITools.addMenuItem(e -> listenerHelper.notifyEvent(e, AlterCommand.EXIT), fileMenu,
			PropertiesHelper.getStringFromProperties(properties, "exitLbl"),
			ImageTools.getImageIcon(PropertiesHelper.getStringFromProperties(properties, "stop.image")).orElse(null));

		/* Menu 'Lenguaje'. */
		final ButtonGroup localeGroup = new ButtonGroup();

		final JMenu localeMenu = new JMenu(
			PropertiesHelper.getStringFromProperties(properties, "idiomaLbl"));
		GUITools.addRadioButtonMenuItem(listener, localeMenu, localeGroup,
			PropertiesHelper.getStringFromProperties(properties, "espanaLbl"),
			new ImageIcon(myClass.getResource(
				getStringFromProperties(properties, "flgspain1.image"))));
		GUITools.addRadioButtonMenuItem(listener, localeMenu, localeGroup,
				PropertiesHelper.getStringFromProperties(properties, "espanaLbl"),
				new ImageIcon(myClass.getResource(
					PropertiesHelper
						.getStringFromProperties(properties, "espanaRepublicana.image"))));
		GUITools.addRadioButtonMenuItem(listener, localeMenu, localeGroup,
				PropertiesHelper.getStringFromProperties(properties, "deutschlandLbl"),
				new ImageIcon(myClass.getResource(
					PropertiesHelper.getStringFromProperties(properties, "deutschland.image"))));
		GUITools.addRadioButtonMenuItem(listener, localeMenu, localeGroup,
				PropertiesHelper.getStringFromProperties(properties, "englandLbl"),
				new ImageIcon(myClass.getResource(
					PropertiesHelper.getStringFromProperties(properties, "uk.image"))));
		GUITools.addRadioButtonMenuItem(listener, localeMenu, localeGroup,
				PropertiesHelper.getStringFromProperties(properties, "franceLbl"),
				new ImageIcon(myClass.getResource(
					PropertiesHelper.getStringFromProperties(properties, "france.image"))));

		//lafMenu.setMnemonic(KeyEvent.VK_R);
		add(fileMenu);
		add(LnFController.getLnFMenu(this));
		add(localeMenu);
		setBorderPainted(true);
	}
}
