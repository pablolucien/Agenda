package org.pclg.agenda.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.gui.GroupMgtPanel;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;

public final class GroupsMgtAction extends AbstractAction {
    private static final long serialVersionUID = -2266916929505992732L;
    private final Frame parent;
	private final Properties properties;
	private final transient AgendaDb agendaDb;
    private final transient ActionCallback callback;
    private GroupMgtPanel groupMgtPanel;

	public GroupsMgtAction(final Frame parent, final Properties properties,
		    final AgendaDb agendaDb, final ActionCallback callback) {
		this.parent = parent;
		this.properties = properties;
		this.agendaDb = agendaDb;
        this.callback = callback;
    }


	@Override
	public void actionPerformed(final ActionEvent ev) {
		if (groupMgtPanel == null) {
			groupMgtPanel = new GroupMgtPanel(properties, agendaDb);
		}
	   	if (JOptionPane.showOptionDialog(parent, groupMgtPanel,
			   PropertiesHelper.getStringFromProperties(properties,
				   "gestion.grupos.title"), JOptionPane.DEFAULT_OPTION,
	   			JOptionPane.PLAIN_MESSAGE,
	   			ImageTools.getImageIcon(PropertiesHelper
					.getStringFromProperties(properties,
                        "AgendaGUI.byGroupImageBig")).orElse(null),
	   			null, null) == JOptionPane.YES_OPTION) {
			groupMgtPanel.accept();
		}
        callback.call();
	}
}

