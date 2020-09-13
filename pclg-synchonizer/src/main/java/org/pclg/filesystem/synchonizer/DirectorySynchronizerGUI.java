package org.pclg.filesystem.synchonizer;

import org.apache.log4j.Level;
import org.pclg.gui.FancyButtonPanel;
import org.pclg.gui.Kaleidoscope;
import org.pclg.gui.ManagedScrollPane;
import org.pclg.gui.VersatileComboBox;
import org.pclg.log.LoggerFactory;
import org.pclg.log.TextAreaAppender;
import org.pclg.log.TextAreaLogger;
import org.pclg.tools.Chrono;
import org.pclg.tools.Command;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.BorderLayout.WEST;
import static org.pclg.gui.FancyButtonPanel.Orientation.VERTICAL;
import static org.pclg.gui.ScrollBarManager.Position.BOTTOM;
import static org.pclg.gui.ScrollBarManager.Position.NOTHING;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * @author El Coyote Cojo
 * @since 6/09/18 18:20
 */
final class DirectorySynchronizerGUI {
    private static final TextAreaLogger LOGGER = new TextAreaLogger(LoggerFactory.makeLog4J());
    private static final String LAST_DIR_KEY = DirectorySynchronizer.BASENAME + ".lastDir";
    private static final String LAST_FILE_KEY = DirectorySynchronizer.BASENAME + ".lastFile";
    private static final String BUTTON_1_TEXT_KEY = DirectorySynchronizer.BASENAME + ".selectTargetsButton.text";
    private static final String BUTTON_2_TEXT_KEY = DirectorySynchronizer.BASENAME + ".selectFileButton.text";
    private static final String EXIT_BUTTON_TEXT_KEY = DirectorySynchronizer.BASENAME + ".exitButton.text";
    private static final String CLEAR_BUTTON_TEXT_KEY = DirectorySynchronizer.BASENAME + ".clearButton.text";
    private static final String CLEAR_ALL_BUTTON_TEXT_KEY = DirectorySynchronizer.BASENAME + ".clearAllButton.text";
    private static final String CLEANUP_BUTTON_TEXT_KEY = DirectorySynchronizer.BASENAME + ".cleanupButton.text";
    private static final String DIALOG_IMAGE_KEY = DirectorySynchronizer.BASENAME + ".dialogImage";
    private final JButton lastFileButton = new JButton();
    private final JTextArea msgTextArea = new JTextArea();
    private final JCheckBox testCheckBox = new JCheckBox("Test!", true);
    private String lastDir;
    private String lastFile;
    private static final String successSound = "/sounds/success_sound.wav";

    DirectorySynchronizerGUI() throws IOException {
        final Properties properties = new Properties();
        PropertiesHelper.loadProperties(properties, DirectorySynchronizer.BASENAME);
        final JFrame frame = new JFrame(DirectorySynchronizer.BASENAME);
        msgTextArea.setEditable(false);
        final TextAreaAppender newAppender = new TextAreaAppender(msgTextArea);
        LOGGER.addAppender(newAppender);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ImageTools.getImageIcon(getStringFromProperties(properties, DIALOG_IMAGE_KEY))
            .ifPresent(icon -> frame.setIconImage(icon.getImage()));
        DirectorySynchronizer.addAppender(newAppender);
        final DirectorySynchronizer directorySynchronizer = new DirectorySynchronizer();
        lastFileButton.setText(getStringFromProperties(properties, LAST_FILE_KEY));
        createLastFileButton(properties, directorySynchronizer, frame);

        final JTabbedPane tabbedPane = new JTabbedPane();
        final JPanel actionPane = createActionPane(properties, directorySynchronizer, frame);
        tabbedPane.addTab("Synchronize", actionPane);
        tabbedPane.addTab("Cleanup", createCleanupPane(properties, frame));
//tabbedPane.setSelectedIndex(1);
        frame.add(tabbedPane, CENTER);

        if (PropertiesHelper.getBooleanFromProperties(properties, "DirectorySynchronizer.showKaleidoscope", false)) {
            setUpKaleidoscope(frame, actionPane, msgTextArea);
        }

        GUITools.fancyShowWindow(properties, DirectorySynchronizer.BASENAME, new GUITools.WindowInfo(frame, "application", true));
    }

    private void setUpKaleidoscope(final JFrame frame, final JPanel actionPane, final JTextArea msgTextArea) {
        final Kaleidoscope kaleidoscope = new Kaleidoscope(msgTextArea, 14);
        msgTextArea.setBorder(kaleidoscope);
        actionPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(final ComponentEvent e) {
                LOGGER.debug("componentShown");
                kaleidoscope.start();
            }

            @Override
            public void componentHidden(final ComponentEvent e) {
                LOGGER.debug("componentHidden");
                kaleidoscope.stop();
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            private boolean kaleidoscopeWasRunning;

            @Override
            public void windowActivated(final WindowEvent e) {
                LOGGER.debug("windowActivated: kaleidoscopeWasRunning = " + kaleidoscopeWasRunning);
                if (kaleidoscopeWasRunning) {
                    kaleidoscope.start();
                }
            }

            @Override
            public void windowDeactivated(final WindowEvent e) {
                kaleidoscopeWasRunning = kaleidoscope.isRunning();
                LOGGER.debug("windowDeactivated: kaleidoscopeWasRunning = " + kaleidoscopeWasRunning);
                kaleidoscope.stop();
            }
        });
    }

    private JPanel createActionPane(final Properties properties, final DirectorySynchronizer directorySynchronizer, final JFrame frame) {
        final JPanel actionPane = new JPanel(new BorderLayout());
        actionPane.add(new ManagedScrollPane(msgTextArea, NOTHING), CENTER);
        final FancyButtonPanel buttonPane = new FancyButtonPanel(VERTICAL,
                createSelectTargetsButton(properties, directorySynchronizer, frame),
                createSelectFileButton(properties, directorySynchronizer, frame),
                lastFileButton,
                createClearButton(properties, msgTextArea),
                createClearAllButton(properties, directorySynchronizer, msgTextArea),
                createExitButton(properties));
        buttonPane.setComponentsAlignment(SwingConstants.RIGHT);
        actionPane.add(buttonPane, WEST);
        return actionPane;
    }

    private JPanel createCleanupPane(final Properties properties, final JFrame frame) {
        final JTextArea cleanupTextArea = new JTextArea();
        final JPanel panel = new JPanel(new BorderLayout());
        final FancyButtonPanel buttonPane = new FancyButtonPanel(
                createCleanupButton(properties, frame, cleanupTextArea),
                createClearButton(properties, cleanupTextArea),
                createExitButton(properties));
        EraserHead.addAppender(new TextAreaAppender(cleanupTextArea));
        panel.add(testCheckBox, NORTH);
        final JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JTable(5, 1),
                new ManagedScrollPane(cleanupTextArea, BOTTOM));
        jSplitPane.setDividerLocation(-1);
        panel.add(jSplitPane, CENTER);
        panel.add(buttonPane, SOUTH);
        return  panel;
    }

    private JButton createCleanupButton(final Properties properties, final JFrame frame, final JTextArea textArea) {
        final JButton button = new JButton(PropertiesHelper.getStringFromProperties(properties, CLEANUP_BUTTON_TEXT_KEY));
        button.setToolTipText("What could possibly go wrong, eh?");
        final String[] baseDirs = properties.getProperty("DirectorySynchronizer.cleanup.baseDirs", "").split("\\|");
        button.addActionListener(e -> GUITools.executeWithWaitCursor(frame,
            () -> EraserHead.processStream(
                Arrays.stream(textArea.getText().split("\\r?\\n")), baseDirs,
                testCheckBox.isSelected())));
        return button;
    }

    private static JButton createClearButton(final Properties properties, final JTextArea textArea) {
        final JButton button = new JButton(PropertiesHelper.getStringFromProperties(properties, CLEAR_BUTTON_TEXT_KEY));
        button.addActionListener(e -> textArea.setText(""));
        return button;
    }

    private static JButton createClearAllButton(final Properties properties, final DirectorySynchronizer directorySynchronizer, final JTextArea textArea) {
        final JButton button = new JButton(PropertiesHelper.getStringFromProperties(properties, CLEAR_ALL_BUTTON_TEXT_KEY));
        button.addActionListener(e -> {
            directorySynchronizer.resetCounters();
            textArea.setText("");
        });
        return button;
    }

    private static JButton createExitButton(final Properties properties) {
        final JButton button = new JButton(PropertiesHelper.getStringFromProperties(properties, EXIT_BUTTON_TEXT_KEY));
        button.addActionListener(e -> System.exit(0));
        return button;
    }

    private void createLastFileButton(final Properties properties,
        final DirectorySynchronizer directorySynchronizer, final JFrame frame) {
        lastDir = getStringFromProperties(properties, LAST_DIR_KEY);
        lastFile = getStringFromProperties(properties, LAST_FILE_KEY);
        lastFileButton.setText(lastFile);
        lastFileButton.addActionListener(e -> {
            try {
                final File selectedFile = new File(new File(lastDir), lastFile);
                if (selectedFile.exists()) {
                    synchronize(directorySynchronizer, frame, selectedFile);
                } else {
                    LOGGER.warn(String.format("%s not found%n", selectedFile));
                }
            } catch (final Exception ex) {
                LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            }
        });
    }

    private JButton createSelectFileButton(final Properties properties,
            final DirectorySynchronizer directorySynchronizer, final JFrame frame) {
        final JButton button = new JButton(
            PropertiesHelper.getStringFromProperties(properties, BUTTON_2_TEXT_KEY));
        button.addActionListener(e -> {
            try {
                lastDir = getStringFromProperties(properties, LAST_DIR_KEY);
                final JFileChooser fileChooser = new JFileChooser(lastDir);
                fileChooser.setFileFilter(new FileNameExtensionFilter("", "json"));

                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    final File selectedFile = fileChooser.getSelectedFile();
                    lastDir = selectedFile.getParentFile().getAbsolutePath();
                    lastFile = selectedFile.getName();
                    final Properties customProperties = new Properties();
                    PropertiesHelper.loadCustomProperties(customProperties, DirectorySynchronizer.BASENAME);
                    customProperties.setProperty(LAST_DIR_KEY, lastDir);
                    customProperties.setProperty(LAST_FILE_KEY, lastFile);
                    PropertiesHelper.saveCustomProperties(customProperties, DirectorySynchronizer.BASENAME);
                    lastFileButton.setText(lastFile);
                    SwingUtilities.invokeLater(() -> synchronize(directorySynchronizer, frame, selectedFile));
                }
            } catch (final Exception ex) {
                LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            }
        });
        return button;
    }

    private void synchronize(final DirectorySynchronizer directorySynchronizer, final JFrame frame, final File selectedFile) {
//        final SplashWindow splashWindow = new SplashWindow(ImageTools.getImageIcon("/images/Please_stand_by_2.png").orElse(null), null);
//        splashWindow.setVisible(true);
        executeWithTiming(frame, () -> directorySynchronizer.synchronize(selectedFile), "Using targets file : " + selectedFile);
//        splashWindow.setVisible(false);
    }

    private static JButton createSelectTargetsButton(final Properties properties,
        final DirectorySynchronizer directorySynchronizer, final Component frame) {
        final JButton button = new JButton(
            PropertiesHelper.getStringFromProperties(properties, BUTTON_1_TEXT_KEY));
        final VersatileComboBox versatileComboBox = new VersatileComboBox(frame, properties, DirectorySynchronizer.BASENAME, 10);
        button.addActionListener(e -> {
            try {
                if (versatileComboBox.accept()) {
                    final String selectedItem = versatileComboBox.getSelectedItem();
                    if (selectedItem != null) {
                        final String[] targets = selectedItem.split("\\|");
                        if (targets.length != 2) {
                            throw new DirectorySynchronizerException("Invalid targets: ");
                        }
                        final Properties customProperties = new Properties();
                        PropertiesHelper.loadCustomProperties(customProperties, DirectorySynchronizer.BASENAME);
                        versatileComboBox.saveYourProperties(customProperties);
                        PropertiesHelper.saveCustomProperties(customProperties, DirectorySynchronizer.BASENAME);
                        executeWithTiming(frame, () -> {
                            try {
                                directorySynchronizer.synchronizeDirectories(new TargetDef(new File(targets[0]), new File(targets[1])));
                            } catch (final Exception ex) {
                                LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                            }
                        }, "Using targets: " + selectedItem);
                    }
                }
            } catch (final Exception ex) {
                LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            }
        });
        return button;
    }

    private static void executeWithTiming(final Component frame, final Command command, final String extraInfo) {
        final int chrono = Chrono.getChrono();
        try {
            LOGGER.log(Level.OFF, String.format("Start: %s. %s.%n", new Date().toString(), extraInfo));
            Chrono.start(chrono);
            GUITools.executeWithWaitCursor(frame, command);
        } finally {
            Chrono.mark(chrono);
            signalEndOfWork();
            LOGGER.log(Level.OFF, String.format("%nFinish: %s. Total time: %s%n", new Date().toString(),
                Chrono.timeDetail(Chrono.elapsed(chrono))));
        }
    }

    private static void signalEndOfWork() {
        final URL resource = DirectorySynchronizerGUI.class.getResource(successSound);
        if (resource != null) {
            AudioClip theSound;
//                try {
//                    theSound = Applet.newAudioClip(clipFile.toURL());
                theSound = Applet.newAudioClip(resource);
                theSound.play();
//                } catch (final MalformedURLException e) {
                // Ignore;
//                }
        }

    }
}
