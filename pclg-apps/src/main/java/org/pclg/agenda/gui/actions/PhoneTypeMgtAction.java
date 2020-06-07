package org.pclg.agenda.gui.actions;

import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.gui.PhoneTypeMgtPanel;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Properties;

public final class PhoneTypeMgtAction extends AbstractAction {
    private static final long serialVersionUID = -5748906021119254125L;
    private final Frame parent;
    private final Properties properties;
    private final transient AgendaDb agendaDb;
    private final transient ActionCallback callback;
    private PhoneTypeMgtPanel phoneTypeMgtPanel;

    public PhoneTypeMgtAction(final Frame parent, final Properties properties,
            final AgendaDb agendaDb, final ActionCallback callback) {
        this.parent = parent;
        this.properties = properties;
        this.agendaDb = agendaDb;
        this.callback = callback;
    }

    @Override
    public void actionPerformed(final ActionEvent ev) {
        if (phoneTypeMgtPanel == null) {
            phoneTypeMgtPanel = new PhoneTypeMgtPanel(properties, agendaDb);
        }
        JOptionPane.showOptionDialog(parent, phoneTypeMgtPanel,
            PropertiesHelper.getStringFromProperties(properties,
                "gestion.phoneType.title"), JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            ImageTools.getImageIcon(PropertiesHelper
                .getStringFromProperties(properties,
                    "AgendaGUI.phoneTypeImageBig")).orElse(null),
            null, null);
        callback.call();
    }
}
