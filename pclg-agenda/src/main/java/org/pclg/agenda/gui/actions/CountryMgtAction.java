package org.pclg.agenda.gui.actions;

import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.gui.CountryMgtPanel;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Properties;

public final class CountryMgtAction extends AbstractAction {
    private static final long serialVersionUID = 1803144928332188480L;
    private final Frame parent;
	private final Properties properties;
	private final transient AgendaDb agendaDb;
    private final transient ActionCallback callback;
    private CountryMgtPanel countryMgtPanel;

	public CountryMgtAction(final Frame parent, final Properties properties,
		    final AgendaDb agendaDb, final ActionCallback callback) {
		this.parent = parent;
		this.properties = properties;
		this.agendaDb = agendaDb;
        this.callback = callback;
    }

	@Override
	public void actionPerformed(final ActionEvent ev) {
		if (countryMgtPanel == null) {
			countryMgtPanel = new CountryMgtPanel(properties, agendaDb);
		}
	   	if (JOptionPane.showOptionDialog(parent, countryMgtPanel,
			   PropertiesHelper.getStringFromProperties(properties,
				   "gestion.paises.title"), JOptionPane.DEFAULT_OPTION,
	   			JOptionPane.PLAIN_MESSAGE,
	   			ImageTools.getImageIcon(PropertiesHelper
					.getStringFromProperties(properties,
						"AgendaGUI.countryImageBig")).orElse(null),
	   			null, null) == JOptionPane.YES_OPTION) {
			countryMgtPanel.accept();
	   	} else {
			countryMgtPanel.cancel();
		}
        callback.call();
	}
}