package org.pclg.agenda.gui.actions;

import org.apache.logging.log4j.Logger;
import org.pclg.agenda.AgendaUtil;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.gui.AgendaGUI;
import org.pclg.agenda.gui.ListadoPanel;
import org.pclg.gui.DirTree;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author El Coyote Cojo.
 * @since 31/03/2017.
 */
public class VCardSaverAction extends AbstractAction {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = -949169696361099127L;
    private final AgendaGUI agendaGUI;
    private final Properties properties;
    private final ListadoPanel listadoPane;
    private DirTree dirTree;

    public VCardSaverAction(AgendaGUI agendaGUI, Properties properties, ListadoPanel listadoPane) {
        this.agendaGUI = agendaGUI;
        this.properties = properties;
        this.listadoPane = listadoPane;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final AgendaRecord[] selectedRecords = listadoPane.getSelectedRecords();
        if (selectedRecords.length == 0) {
            return;
        }

        if (dirTree == null) {
            final JDialog dialog = new JDialog(agendaGUI,
                Dialog.ModalityType.APPLICATION_MODAL);
            dirTree = new DirTree(dialog);
            GUITools.center(dialog, agendaGUI);
        }
        final String dir = properties.getProperty("AgendaGUI.lastVCardsDirectory");
        if (dir != null) {
            dirTree.setFile(new File(dir));
        }
        dirTree.setVisible(true);

        if (dirTree.accepted()) {
            dirTree.getFile().ifPresent(ff -> {
                properties.setProperty("AgendaGUI.lastVCardsDirectory",
                    ff.getAbsolutePath());
                for (final AgendaRecord record : selectedRecords) {
                    try {
                        AgendaUtil.saveVCard(record, ff);
                    } catch (final IOException ex) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                        JOptionPane.showMessageDialog(agendaGUI,
                            ex.getMessage(), LoggerFactory.ERROR_TAG,
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
    }
}
