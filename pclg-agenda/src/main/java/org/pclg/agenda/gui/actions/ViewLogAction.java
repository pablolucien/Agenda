package org.pclg.agenda.gui.actions;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.pclg.agenda.gui.AgendaGUI;
import org.pclg.gui.JTabbedPaneWithCloseIcons;
import org.pclg.gui.ManagedScrollPane;
import org.pclg.gui.ScrollBarManager;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * @since 15/03/2018.
 */
public class ViewLogAction extends AbstractAction {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final AgendaGUI agendaGUI;
    private final JTabbedPaneWithCloseIcons tabbedPane;

    public ViewLogAction(final AgendaGUI agendaGUI, final JTabbedPaneWithCloseIcons tabbedPane) {
        this.agendaGUI = agendaGUI;
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void actionPerformed(final ActionEvent ev) {
        final JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        final Enumeration<?> allAppenders = Logger.getRootLogger().getAllAppenders();
        final StringBuilder fileNames = new StringBuilder();
        class FileControl {
            final File file;
            long lastModified;

            FileControl(final File file) {
                this.file = file;
                lastModified = Long.MIN_VALUE;
            }

            public File getFile() {
                return file;
            }
        }
        final List<FileControl> logFiles = new ArrayList<>();
        while (allAppenders.hasMoreElements()) {
            final Appender appender = (Appender) allAppenders.nextElement();
            if (appender instanceof FileAppender) {
                final String fileName = ((FileAppender) appender).getFile();
                logFiles.add(new FileControl(new File(fileName)));
                fileNames.append(fileName).append(", ");
            }
        }

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final boolean[] somethingMustBeDone = {false};
                logFiles.forEach(fileControl -> {
                    final long lastModified = fileControl.file.lastModified();
                    if (fileControl.lastModified != lastModified) {
                        fileControl.lastModified = lastModified;
                        somethingMustBeDone[0] = true;
                    }
                });
                if (somethingMustBeDone[0]) {
                    showLogs(textArea, logFiles.stream().map(FileControl::getFile).collect(Collectors.toList()));
                    somethingMustBeDone[0] = false;
                }
            }
            
            private void showLogs(final JTextArea textArea, final List<File> logFiles) {
                    textArea.setText("");
                    logFiles.forEach(file -> {
                        final String fileName = file.getAbsolutePath();
                        textArea.append("File: " + fileName + '\n');
                        try {
                            textArea.append(new String(FileTools.readFromFile(fileName)) + '\n');
                        } catch (final IOException ex) {
                            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                            textArea.append(ex.toString() + '\n');
                        }
                        textArea.append("=================================================\n");
                    });
                }
        }, 100, 1000);

        class ClosableJScrollPane extends ManagedScrollPane implements ActionListener {
            private ClosableJScrollPane(final Component textArea) {
                super(textArea, ScrollBarManager.Position.BOTTOM);
            }

            /** Action a ejecutar por el aspa del TabPanel. */
            @Override
            public void actionPerformed(final ActionEvent e) {
                timer.cancel();
                tabbedPane.remove(this);
            }
        }

        agendaGUI.add2TabbedPane(fileNames.toString(), new ClosableJScrollPane(textArea));
    }
}
