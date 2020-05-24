package org.pclg.agenda.gui.actions;

import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.AgendaUtil;
import org.pclg.agenda.gui.AgendaGUI;
import org.pclg.agenda.gui.DataEntry;
import org.pclg.gui.JTabbedPaneWithCloseIcons;
import org.pclg.log.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * @author pacelucien.
 * @since 31/03/2017.
 */
public class VCardReaderAction extends AbstractAction {
    /** El logger. */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = -3652442130515868722L;
    private final JFileChooser fileChooser = new JFileChooser();
    private final AgendaGUI agendaGUI;
    private final Properties properties;
    private final AgendaDb agendaDb;
    private final JTabbedPaneWithCloseIcons tabbedPane;

    public VCardReaderAction(final AgendaGUI agendaGUI, final Properties properties, final AgendaDb agendaDb, final JTabbedPaneWithCloseIcons tabbedPane) {
        this.agendaGUI = agendaGUI;
        this.properties = properties;
        this.agendaDb = agendaDb;
        this.tabbedPane = tabbedPane;
        final FileNameExtensionFilter filter = new FileNameExtensionFilter("vCards", "vcf");
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(true);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final String dir = properties.getProperty("AgendaGUI.lastVCardsDirectory");
        if (dir != null) {
            fileChooser.setCurrentDirectory(new File(dir));
        }
        if (fileChooser.showOpenDialog(agendaGUI) == JFileChooser.APPROVE_OPTION) {
            properties.setProperty("AgendaGUI.lastVCardsDirectory",
                fileChooser.getCurrentDirectory().getAbsolutePath());
                final Cursor oldCursor = agendaGUI.getCursor();
                agendaGUI.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                for (final File file : fileChooser.getSelectedFiles()) {
                    final List<AgendaRecord> records = AgendaUtil.readVCards(file);
                    for (final AgendaRecord record : records) {
                        final DataEntry dataEntry = new DataEntry(properties, agendaDb,
                            tabbedPane, agendaGUI, null);
                        agendaGUI.add2TabbedPane(getStringFromProperties(properties,
                            "new.contact.tab.title"), dataEntry);
                        try {
                            dataEntry.fillData(record);
                        } catch (final SQLException ex) {
                            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                        }
                    }
                }
                agendaGUI.setCursor(oldCursor);
        }
    }
}
