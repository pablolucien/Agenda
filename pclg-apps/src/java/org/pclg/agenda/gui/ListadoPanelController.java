package org.pclg.agenda.gui;

import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.gui.actions.QueryAction;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ImageTools;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Properties;

import static org.pclg.tools.StringTools.isEmptyOrBlank;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * @since 26/07/2017.
 */
public class ListadoPanelController {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final ListadoPanel listadoPanel;
    final QueryAction customQueryAction;
    final Action filteredAction;
    final Action groupAction;
    private final GroupSelectionPanel groupSelectionPanel = new GroupSelectionPanel();


    ListadoPanelController(AgendaGUI owner, AgendaDb agendaDb, Properties properties) {
        listadoPanel = new ListadoPanel(owner, agendaDb, properties);

        customQueryAction = new QueryAction(owner, properties, listadoPanel);

        filteredAction = new AbstractAction() {
            private static final long serialVersionUID = 8602021256095842434L;
            /**
             * Último filtro usado.
             */
            private String lastFilter = null;

            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
                    final String input = JOptionPane.showInputDialog(owner,
                            getStringFromProperties(properties,
                                    "AgendaGUI.filterPrompt"), lastFilter);
                    if (!isEmptyOrBlank(input)) {
                        lastFilter = input;
                        listadoPanel.setRecordFilter(input);
                        listadoPanel.showData(AgendaGUI.ListCriterium.filtered);
                    }
                } catch (final SQLException ex) {
                    LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                }
            }
        };

        final JOptionPane groupOptionsPane = new JOptionPane(groupSelectionPanel,
                JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION,
                ImageTools.getImageIcon(getStringFromProperties(properties,
                        "AgendaGUI.byGroupImageBig")).orElse(null), null, null);
        final JDialog groupOptionsDialog = groupOptionsPane.createDialog(owner,
                getStringFromProperties(properties, "select.grups.title"));

        listadoPanel.setGroupSelectionPanel(groupSelectionPanel);

        groupSelectionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() > 1) {
                    try {
                        groupOptionsDialog.setVisible(false);
                        listadoPanel.showData(AgendaGUI.ListCriterium.byGroup);
                    } catch (final SQLException ex) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                    }
                }
            }
        });

        groupAction = new AbstractAction() {
            private static final long serialVersionUID = -4048235833080314305L;

            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                groupSelectionPanel.refresh();
                groupOptionsDialog.setVisible(true);
                if (Integer.valueOf(JOptionPane.YES_OPTION).equals(
                        groupOptionsPane.getValue())) {
                    try {
                        listadoPanel.showData(AgendaGUI.ListCriterium.byGroup);
                    } catch (final SQLException ex) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                    }
                }
            }
        };
    }

    ListadoPanel getListadoPanel() {
        return listadoPanel;
    }

    void saveYourProperties(final Properties guiProperties) {
        listadoPanel.saveYourProperties(guiProperties);
        customQueryAction.saveYourProperties(guiProperties);
    }
}
