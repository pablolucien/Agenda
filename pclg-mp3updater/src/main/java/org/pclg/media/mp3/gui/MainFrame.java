package org.pclg.media.mp3.gui;

import org.apache.logging.log4j.Logger;
import org.pclg.gui.DirSelectorField;
import org.pclg.gui.JLabeledField;
import org.pclg.gui.JLabeledFieldGroup;
import org.pclg.log.LoggerFactory;
import org.pclg.media.Album;
import org.pclg.media.id3.ID3Genre;
import org.pclg.media.mp3.MP3Update;
import org.pclg.runtime.RuntimeControl;
import org.pclg.tools.BoundsInfo;
import org.pclg.tools.Consola;
import org.pclg.tools.FileTools;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.SortedProperties;
import org.pclg.tools.ToolBox;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.MAXIMIZED_BOTH;
import static org.pclg.Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;
import static org.pclg.media.mp3.MP3Update.process;
import static org.pclg.media.mp3.MP3Update.renameFiles;
import static org.pclg.tools.BoundsInfo.NULL_RECTANGLE;

/**
 * @author Pablo
 * @since 02-feb-2012 19:49:22
 */
public class MainFrame {
	/**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    public static final int WIDTH = 800;
	public static final int HEIGHT = 500;

    /**
     * The GUI.
     */
    private final JFrame mainFrame;

    private final DirSelectorField baseDir;

	// FIXME: estos hay que crearlos en otro sitio para que sean usados por los dos tipos de proceso.
	private JLabeledField replaceCharsFld;
	private JCheckBox deleteId3V2Choice;

	/** La consola (aqu� se redirigen System.out y System.err). */
	private final Consola errConsole;

	/** Propiedades de i18n. */
	private final Properties i18nProperties = new Properties();
	/** Propiedades persistentes. */
	private final Properties customProperties = new SortedProperties();

    /** posicion y tama�o de la ventana de la aplicacion. */
    private BoundsInfo applicationBounds;

    /** posicion y tama�o de la ventana de la mensajes. */
    private BoundsInfo errConsoleBounds;

    /**
     * Default constructor.
     */
    public MainFrame() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(final Thread thread, final Throwable ex) {
				if (!(ex instanceof ThreadDeath)) {
					LOGGER.error("UncaughtException in Thread: " + thread , ex);
					System.exit(-THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING);
				}
			}
		});
		loadProperties();
		mainFrame = new JFrame(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.title"));
		baseDir = new DirSelectorField(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.targetDir"));
		final String targetDir = customProperties.getProperty("targetDir", System.getProperty("user.home"));
  		baseDir.setText(targetDir);
		errConsole = Consola.getConsola(mainFrame, PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.consoleTitle"));
		// Must set System.err before the logger is used.
		System.setOut(errConsole);
		System.setErr(errConsole);
        final JPanel leftPanel = createLeftPane();
        leftPanel.setBorder(BorderFactory.createEtchedBorder());
        final JPanel rightPanel = createRightPane();
        rightPanel.setBorder(BorderFactory.createEtchedBorder());
        final JPanel centerPane = new JPanel();
        centerPane.setLayout(new GridLayout(1, 2));
        centerPane.add(leftPanel);
        centerPane.add(rightPanel);
		final Container contentPane = mainFrame.getContentPane();
		contentPane.add(baseDir, BorderLayout.NORTH);
        contentPane.add(centerPane, BorderLayout.CENTER);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		ImageTools.getImageIcon(PropertiesHelper
			.getStringFromProperties(i18nProperties, "MP3Update.image"))
			.ifPresent(
				imageIcon -> mainFrame.setIconImage(imageIcon.getImage()));

        // Antes de salir de la aplicacion salvamos las propiedades persistentes
        RuntimeControl.registerShutdownHook(this::saveProperties);
    }

    /**
     * Carga todas propiedades de la aplicacion.
     */
    private void loadProperties() {
        try {
            final String baseName = MP3Update.class.getSimpleName();
			final ResourceBundle bundle = ResourceBundle.getBundle(baseName);
			for (final String key : bundle.keySet()) {
				i18nProperties.setProperty(key, bundle.getString(key));
			}
            PropertiesHelper.loadCustomProperties(customProperties, baseName);
            applicationBounds = PropertiesHelper.getBounds(customProperties, "application");
            errConsoleBounds = PropertiesHelper.getBounds(customProperties, "errConsole");
        } catch (final MissingResourceException ex) {
            ToolBox.showInfo(ex);
        }
    }

    /**
     * Guarda todas propiedades en el archivo de configuracion.
     */
    private void saveProperties() {
        final BoundsInfo mainBoundsInfo = new BoundsInfo();
   		mainBoundsInfo.setBounds(mainFrame.getBounds());
   		mainBoundsInfo.setMinimized(mainFrame.getExtendedState() == ICONIFIED);
   		mainBoundsInfo.setMaximized(mainFrame.getExtendedState() == MAXIMIZED_BOTH);
        PropertiesHelper.saveBounds(mainBoundsInfo, customProperties, "application");
        PropertiesHelper.saveBounds(new BoundsInfo(errConsole.getBounds()), customProperties, "errConsole");

        customProperties.setProperty("targetDir", baseDir.getText());

        final String className = MP3Update.class.getSimpleName();
        try {
            PropertiesHelper.saveCustomProperties(customProperties, className);
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
            LOGGER.error("Error accessing: " + className, ex);
        } catch (final Throwable throwable) {
            ToolBox.showInfo(throwable);
            LOGGER.error("Error: " + className, throwable);
        }
    }

    public final void show() {
        //GUITools.scrollToCenter(mainFrame, null);
        GUITools.setBounds(mainFrame, applicationBounds);
        final Rectangle bounds = errConsoleBounds.getBounds();
        if (bounds == null || bounds.equals(NULL_RECTANGLE)) {
            final Rectangle mainframeBounds = mainFrame.getBounds();
            errConsole.setBounds(new Rectangle(mainframeBounds.x, mainframeBounds.y + mainframeBounds.height, mainframeBounds.width, 100));
        } else {
            errConsole.setBounds(bounds);
        }

		errConsole.setVisible(true);
        mainFrame.setVisible(true);
    }

    private JPanel createLeftPane() {
        final JPanel leftPanel = new JPanel(new BorderLayout());

        // Center
        final JPanel centerPane = new JPanel();
        centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
        final JCheckBox recurseChoice = new JCheckBox(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.recurse"), true);
        centerPane.add(recurseChoice);
        final JCheckBox overwriteChoice = new JCheckBox(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.overwrite"), true);
        centerPane.add(overwriteChoice);
        deleteId3V2Choice = new JCheckBox(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.deleteId3v2"), true);
        centerPane.add(deleteId3V2Choice);
        final JCheckBox internalNameChoice = new JCheckBox(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.useInternalName"));
        centerPane.add(internalNameChoice);
        internalNameChoice.setEnabled(false);
        final JCheckBox nrTrackChoice = new JCheckBox(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.trackNrFromPrefix"));
        centerPane.add(nrTrackChoice);
        final JCheckBox testChoice = new JCheckBox(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.testOnly"));
        centerPane.add(testChoice);
        final JCheckBox attribChoice = new JCheckBox(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.preserveAttributes"), true);
        centerPane.add(attribChoice);
		final JLabeledFieldGroup fieldGroup = new JLabeledFieldGroup();
		centerPane.add(fieldGroup);
        final JLabeledField yearFld = new JLabeledField(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.year"), "0");
        yearFld.setToolTipText(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.yearTT"));
        yearFld.setInteger();
        yearFld.setEnabled(!recurseChoice.isSelected());
		fieldGroup.add(yearFld);
        final JLabeledField prefixLenFld = new JLabeledField(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.prefixLen"), "0");
        prefixLenFld.setToolTipText(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.prefixLenTT"));
        prefixLenFld.setInteger();
		fieldGroup.add(prefixLenFld);
        final JLabeledField suffixLenFld = new JLabeledField(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.suffixLen"), "4");
        suffixLenFld.setToolTipText(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.suffixLenTT"));
        suffixLenFld.setInteger();
		fieldGroup.add(suffixLenFld);
        replaceCharsFld = new JLabeledField(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.replaceChars"),
        	customProperties.getProperty("replaceChars", MP3Update.DEFAULT_REPLACEMENT_CHARS));
        replaceCharsFld.setToolTipText(
			PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.replaceCharsTT"));
		fieldGroup.add(replaceCharsFld);
        final JComboBox<String> genre = new JComboBox<>(ID3Genre.getGenreList());
        final Dimension minSize = genre.getMinimumSize();
        final Dimension maxSize = genre.getMaximumSize();
        genre.setMaximumSize(new Dimension(maxSize.width, minSize.height));
        genre.setEnabled(!recurseChoice.isSelected());
        centerPane.add(genre);
        centerPane.add(Box.createVerticalGlue());

        recurseChoice.addActionListener(e -> {
            final boolean recurseChoiceSelected = recurseChoice.isSelected();
            genre.setEnabled(!recurseChoiceSelected);
            yearFld.setEnabled(!recurseChoiceSelected);
        });

        leftPanel.add(centerPane, BorderLayout.CENTER);

        // South
        final Action action = new AbstractAction(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.goAction")) {
            private static final long serialVersionUID = -708316247822069194L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                final MP3Update.Options options = new MP3Update.Options();
                options.year = yearFld.toInt();
                options.prefixLen = prefixLenFld.toInt();
                options.useTrackNrFromPrefix = nrTrackChoice.isSelected();
                options.genre = ID3Genre.getGenreId(genre.getItemAt(genre.getSelectedIndex()));
                options.testMode = testChoice.isSelected();
                options.preserveAttributes = attribChoice.isSelected();
                options.overwrite =  overwriteChoice.isSelected();
                options.deleteId3V2 = deleteId3V2Choice.isSelected();

                final boolean recurse = recurseChoice.isSelected();
                final String dirName = baseDir.getText();
				final int suffixLen = suffixLenFld.toInt();
                LOGGER.info("goByFileSystemBt: " + dirName
                        + ", Recurse = " + recurse
                        + ", overwrite = " + options.overwrite
                        + ", useTrack = " + options.useTrackNrFromPrefix
                        + ", prefixLen = " + options.prefixLen
                        + ", suffixLen = " + suffixLen
                        + ", year = " + options.year
                        + ", genre = " + genre.getSelectedItem()
                );
                final String replaceCharsText = replaceCharsFld.getText();
				final String[] replaceChars = replaceCharsText.split(MP3Update.REPLACE_CHARS_TEXT_DELIMITER);
                customProperties.setProperty("replaceChars", replaceCharsText);

                if (recurse) {
                    try {
                        MP3Update.processRecursive(dirName, options, replaceChars);
                    } catch (final IOException e1) {
                        LOGGER.error("", e1);
                    }
                } else {
                    try {
                        process(dirName, null, options, replaceChars);
                    } catch (IOException e1) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, e1);
                        throw new RuntimeException(e1);
                    }
                }
            }
        };
        final JButton goByFileSystemBt = new JButton(action);
        leftPanel.add(goByFileSystemBt, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createRightPane() {
        final JPanel rightPanel = new JPanel(new BorderLayout());

        final JPanel choosePane = new JPanel();
        choosePane.setLayout(new BoxLayout(choosePane, BoxLayout.X_AXIS));
        final JTextField chooseField = new JTextField();
        final JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
        final Action chooseAction = new AbstractAction(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.configFileAction")) {
            private static final long serialVersionUID = 8529607676640504134L;
            JFileChooser fileChooser;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser(baseDir.getText());
                    final FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        PropertiesHelper.getStringFromProperties(i18nProperties,
							"MP3Update.textFiles"), "txt", "text", "info");
                    fileChooser.setFileFilter(filter);
                } else {
					fileChooser.setCurrentDirectory(new File(baseDir.getText()));
				}
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    final File selectedFile = fileChooser.getSelectedFile();
					final String path = selectedFile.getAbsolutePath();
					chooseField.setText(path);
                    try {
                        textArea.setText(new String(FileTools.readFromFile(selectedFile)));
					} catch (final IOException ex) {
						LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                    }
                }
            }
        };
        final JButton chooseButton = new JButton(chooseAction);

		final Action sampleAction = new AbstractAction(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.sampleConfigFileAction")) {
			@Override
			public void actionPerformed(final ActionEvent event) {
				final InputStream inputStream = getClass().getResourceAsStream("/MP3Update.info");
				if (inputStream != null) {
					textArea.setText("");
					final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					try {
						while ((line = bufferedReader.readLine()) != null) {
							textArea.append(line + System.lineSeparator());
						}
					} catch (final IOException ex) {
						LOGGER.error(LoggerFactory.ERROR_TAG, ex);
					}
				} else {
					textArea.setText("Sample file not found");
				}
			}
		};
		final JButton sampleButton = new JButton(sampleAction);


        choosePane.add(chooseButton);
        choosePane.add(chooseField);
        choosePane.add(sampleButton);
        rightPanel.add(choosePane, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        final Action action = new AbstractAction(PropertiesHelper.getStringFromProperties(i18nProperties, "MP3Update.goAction")) {
            private static final long serialVersionUID = -2120156988218948037L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    final String albumInfoFilePath = chooseField.getText();
                    final File albumInfoFile = new File(albumInfoFilePath);
                    if (albumInfoFile.exists()) {
                        final Album album = Album.createAlbumFromFile(albumInfoFilePath);
                        final String baseDirPath = albumInfoFile.getParentFile().getAbsolutePath();
                        renameFiles(baseDirPath, album, true);
                        final MP3Update.Options options = new MP3Update.Options();
                        options.year = Integer.valueOf(album.getYear());
                        options.prefixLen = MP3Update.DEFAULT_PREFIX_LEN;
                        options.useTrackNrFromPrefix = true;
                        options.genre = album.getGenre();
                        options.testMode = false;
                        options.preserveAttributes = true;
                        options.overwrite = true;
                        options.deleteId3V2 = deleteId3V2Choice.isSelected();
                        process(baseDirPath, album, options, replaceCharsFld.getText().split(MP3Update.REPLACE_CHARS_TEXT_DELIMITER));
                    } else {
                        LOGGER.warn(LoggerFactory.WARN_TAG + ". Unknown albumInfoFile: " + albumInfoFilePath);
                    }
                } catch (final IOException e1) {
                    LOGGER.error(LoggerFactory.ERROR_TAG, e1);
                }
            }
        };
        final JButton goByConfigFileBt = new JButton(action);
        rightPanel.add(goByConfigFileBt, BorderLayout.SOUTH);
        return rightPanel;
	}
}
