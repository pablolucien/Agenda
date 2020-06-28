package org.pclg.alter;

import org.apache.log4j.Logger;
import org.pclg.filelist.FileListPanel;
import org.pclg.gui.JLabeledField;
import org.pclg.gui.JLabeledFieldGroup;
import org.pclg.gui.LnFController;
import org.pclg.log.LoggerFactory;
import org.pclg.runtime.RuntimeControl;
import org.pclg.tools.BoundsInfo;
import org.pclg.tools.Command;
import org.pclg.tools.Comp;
import org.pclg.tools.Consola;
import org.pclg.tools.FileTools;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.SortedProperties;
import org.pclg.tools.ToolBox;
import org.pclg.xtras.ClassPathHacker;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * Cambia el nombre de archivos segun ciertos patrones.
 *
 * @author El Coyote Cojo.
 * @version unknown.
 */
public final class Alter extends JFrame {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
	/** serialVersionUID. */
	private static final long serialVersionUID = 3182039769181547401L;
	private static final String SPLIT_PANE_LAST_DIVIDER_LOCATION = "splitPane.lastDividerLocation";

	/** El nombre del 'tema' del Look & Feel. */
	private String lafName;
	private String lafThemeName;

	/** Necesario para poder usar metodos como getResource() antes del constructor. */
	private final Class<? extends Alter> myClass = getClass();

	/** The name of this class. */
	private final String className = myClass.getSimpleName();

    /**
     * Usados para undo.
     */
    private final UndoRedoManager undoRedoManager = new UndoRedoManager(this::rename);
	private Action redoAction;
	private Action undoAction;

    private final Renamer renamer = new Renamer();

	/** Ultimo filtro usado. */
	private String lastFilter;

	/** posicion y tama?o de la ventana. */
//	private BoundsInfo applicationBounds;

	/** posicion y tama?o de la consola de errores. */
	private BoundsInfo errConsoleBounds;

	private JLabeledField prefixPattern;
	private JLabeledField start;
	private JLabeledField len;
	private JLabeledField relleno;
	private SampleTextField sample;

	/** Guarda las selecciones del usuario. */
	private StatusSaver statusSaver;

	private final ActionListener actionListener;

	private ActionListener createActionListener() {
        return new ActionListener() {

            final Map<Object, Command> actionsMap;
            {
				actionsMap = new HashMap<>(13);
				//Note: when adding or deleting actions adjust the capacity of the map.
				actionsMap.put(AlterCommand.ORDER, Alter.this::ordenar);
				actionsMap.put(AlterCommand.LOWER, Alter.this::toLowerCase);
				actionsMap.put(AlterCommand.COMPARE, Alter.this::startComparation);
				actionsMap.put(AlterCommand.UNDERSCORE, Alter.this::insertCharBeforeExtension);
				actionsMap.put(AlterCommand.UNDERSCORE2, Alter.this::changeSpaces);
				actionsMap.put(AlterCommand.DIR_NAME, Alter.this::ezReplace);

				actionsMap.put(AlterCommand.REPLACE, Alter.this::replace);
				actionsMap.put(start.getComponent(), Alter.this::replace);
				actionsMap.put(len.getComponent(), Alter.this::replace);
				actionsMap.put(relleno.getComponent(), Alter.this::replace);
				actionsMap.put(sample, Alter.this::replace);

				actionsMap.put(AlterCommand.QUIT, () -> GUITools.exitApplication(Alter.this, false));
				actionsMap.put(AlterCommand.EXIT, () -> GUITools.exitApplication(Alter.this, true));
            }

            @Override
          	public void actionPerformed(final ActionEvent event) {
          		final Object eventSource = event.getSource();
          		final Cursor oldCursor = getCursor();
          		setCursor(new Cursor(Cursor.WAIT_CURSOR));

          		try {
                      final Command alterCommand = actionsMap.get(eventSource);
                      if (alterCommand != null) {
                          alterCommand.execute();
          			}
          		} finally {
          			setCursor(oldCursor);
          		}
          	}
        };
    }


	private final StatusLine statusLine = new StatusLine();
	private final ButonPanel buttonPanel;

	/** El manejo de la lista de archivos. */
	private final FileListPanel fileListPanel;

	private final Grid myGrid;

	private JCheckBox testBox;
	private JCheckBox outputBox;
	private JCheckBox allFilesBox;

    private final ItemListener itemListener = new ItemListener() {
        @Override
        public void itemStateChanged(final ItemEvent event) {
            if (event.getSource() == outputBox) {
                errConsole.setVisible(outputBox.isSelected());
            }
        }
     };

	/** Nuestro indicador de errores. */
	private ErrorIndicator errorIndicator;

	private File[] targetFiles;

	private final Consola errConsole;


	/**
	 * Construye un Alter posicionado en un directorio.
	 */
	private Alter() throws IOException {
		setWindowListener();

		/* Propiedades persistentes. */
		final Properties applicationProps = new Properties();
		final Properties applicationCustomProps = new SortedProperties();

		// Cargamos las propiedades persistentes
		loadProperties(applicationProps, applicationCustomProps);

        ClassPathHacker.addFiles(applicationProps.getProperty("Application.ClassPath"));
		final String applicationTitle = getStringFromProperties(applicationProps, "Application.Title");
		setTitle(applicationTitle);

		createFields(applicationProps);
		actionListener = createActionListener();
		setJMenuBar(new AlterMenu(applicationProps, actionListener));
		buttonPanel = new ButonPanel(actionListener, applicationProps);

        ImageTools.getImageIcon(getStringFromProperties(applicationProps, "mars.image"))
			.ifPresent(imageIcon -> setIconImage(imageIcon.getImage()));

		fileListPanel = new FileListPanel(this, applicationProps);
		fileListPanel.addFileListObserver(listPanel -> {
               updateStatusLine();
               try {
                   final String canonicalPath =
                       fileListPanel.getCurrentDirectory().getCanonicalPath();
                   setTitle(applicationTitle + " - " + canonicalPath);
               } catch (final IOException ex) {
                   ToolBox.showInfo(ex);
               }
               lastFilter = fileListPanel.getFilter();
               targetFiles = new File[fileListPanel.elementCount()];
               for (int i = 0; i < targetFiles.length; i++) {
                   targetFiles[i] = fileListPanel.elementAt(i);
               }
           });
		myGrid = new Grid(applicationProps);
		createGUI(applicationProps, applicationCustomProps);
		selectLAF();
        errConsole = Consola.getConsola(this,
      			getStringFromProperties(applicationProps, "erroresLbl"));
        System.setErr(errConsole);
		GUITools.fancyShowWindow(applicationCustomProps, "Alter",
            new GUITools.WindowInfo(this, "application", true),
            new GUITools.WindowInfo(errConsole.getWindow(), "errConsole", false));

		// Antes de salir de la aplicacion salvamos las propiedades persistentes
        RuntimeControl.registerShutdownHook(() -> saveProperties(applicationCustomProps));
	}

	/**
	 * Selects the look & feel of the application.
	 */
	private void selectLAF() {
		// Seleccionar el L & F
        LnFController.setLAF(this, lafName);
	}


	/**
	 * Creates the GUI of the application.
	 */
	private void createGUI(final Properties properties, final Properties customProperties) {
		final JPanel northPanel = createNorthPanel(properties);
		final JPanel southPanel = new JPanel(new BorderLayout());
		fileListPanel.setFilter(lastFilter);

		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			fileListPanel, myGrid);
		splitPane.setOneTouchExpandable(true);
		final String lastDividerLocation = properties.getProperty(SPLIT_PANE_LAST_DIVIDER_LOCATION);
		if (lastDividerLocation != null && ToolBox
				.isInteger(lastDividerLocation)) {
			splitPane.setDividerLocation(Integer.parseInt(lastDividerLocation));
		} else {
			splitPane.setDividerLocation(fileListPanel.getMinimumSize().width);
		}
		splitPane.addPropertyChangeListener(evt -> {
			if (evt.getPropertyName()
				.equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
				customProperties.setProperty(SPLIT_PANE_LAST_DIVIDER_LOCATION,
					String.valueOf(splitPane.getDividerLocation()));
			}
		});

		final JPanel mainPanel = new JPanel(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(splitPane, BorderLayout.CENTER);
		southPanel.add(buttonPanel, BorderLayout.NORTH);
		southPanel.add(statusLine, BorderLayout.SOUTH);
		mainPanel.add(southPanel, BorderLayout.SOUTH);

		getContentPane().add(createToolbar(properties), BorderLayout.WEST);
	}

	private void createFields(final Properties properties) {
		final JLabeledFieldGroup fieldGroup = new JLabeledFieldGroup();
		prefixPattern = new JLabeledField(
			PropertiesHelper.getStringFromProperties(properties, "prefixLbl"));
		fieldGroup.adjustGroupSize(prefixPattern);
		start = new JLabeledField(PropertiesHelper.getStringFromProperties(properties, "startLbl"));
		fieldGroup.adjustGroupSize(start);
		len = new JLabeledField(PropertiesHelper.getStringFromProperties(properties, "lengthLbl"));
		fieldGroup.adjustGroupSize(len);
		relleno = new JLabeledField(
			PropertiesHelper.getStringFromProperties(properties, "rellenoLbl"));
		fieldGroup.adjustGroupSize(relleno);
		sample = new SampleTextField(start, len);
		statusSaver = new StatusSaver(
			new JLabeledField[]{start, len, relleno});


		testBox = new TestBox(
			PropertiesHelper.getStringFromProperties(properties, "TestBox.realThingLbl"),
			PropertiesHelper.getStringFromProperties(properties, "TestBox.testLbl"),
			false);
		outputBox = new JCheckBox(
			PropertiesHelper.getStringFromProperties(properties, "consoleLbl"), false);
		allFilesBox = new JCheckBox(
			PropertiesHelper.getStringFromProperties(properties, "allFilesLbl"), false);
		errorIndicator = new ErrorIndicator(
			PropertiesHelper.getStringFromProperties(properties, "erroresLbl"),
			PropertiesHelper.getStringFromProperties(properties, "erroresSound"));
	}

	private JPanel createNorthPanel(final Properties properties) {
		final JPanel northPanel = new JPanel(new GridLayout(2, 1));
		final JPanel northPanelUp = new JPanel(new GridLayout(1, 0));
		final JPanel northPanelDown = new JPanel(new BorderLayout());
		prefixPattern.setEditable(true);
		northPanelUp.add(errorIndicator);
		northPanelUp.add(prefixPattern);

		northPanelDown.add(new JLabel(PropertiesHelper.getStringFromProperties(properties, "sampleLbl")),
			BorderLayout.WEST);
		northPanelDown.add(sample, BorderLayout.CENTER);
		fileListPanel.addMouseListener(new MouseAdapter() {
			/**
			 * Invoked when a mouse button has been released on a component.
			 */
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					final File selected = (File) fileListPanel
						.getSelectedValue();
					if (selected.isFile()) {
						sample.setText(selected.getName());
						sample.requestFocus();
						fileListPanel.clearSelection();
					}
				}
			}
		});

		start.setInteger();
		start.setEditable(true);
		start.setForeground(Color.red);
		start.addActionListener(actionListener);
		northPanelUp.add(start);

		len.setNatural();
		len.setEditable(true);
		len.addActionListener(actionListener);
		northPanelUp.add(len);

		relleno.setEditable(true);
		relleno.addActionListener(actionListener);
		northPanelUp.add(relleno);

		northPanelUp.add(testBox);

		northPanelUp.add(outputBox);
		northPanelUp.add(allFilesBox);
		outputBox.addItemListener(itemListener);

		sample.addActionListener(actionListener);

		northPanel.add(northPanelUp);
		northPanel.add(northPanelDown);
		return northPanel;
	}


	private void setWindowListener() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent ev) {
				GUITools.exitApplication(ev.getWindow(), false);
			}
		});
	}

	/**
	 * Creacion del toolbar.
	 */
	private JToolBar createToolbar(final Properties properties) {
		/* Toolbar de la aplicacion. */
		final JToolBar toolbar = new AlterToolBar();
		JButton btn;

		final Action clearAction = new AbstractAction(
			PropertiesHelper.getStringFromProperties(properties, "clear.label"),
			ImageTools.getImageIcon(getStringFromProperties(properties, "new.image")).orElse(null)) {
			/** serialVersionUID. */
			private static final long serialVersionUID = -7665104392178534000L;

			/**
			 * Limpia todos los campos.
			 */
			@Override
			public void actionPerformed(final ActionEvent event) {
				prefixPattern.setText("");
				start.setText("");
				len.setText("");
				relleno.setText("");
				sample.setText("");
				clearResults();
				fileListPanel.clear();
				updateStatusLine();
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		};
		btn = toolbar.add(clearAction);
		btn.setText("");
		btn.setToolTipText(
			PropertiesHelper.getStringFromProperties(properties, "clear.tooltip.text"));

		undoAction = new AbstractAction(
			PropertiesHelper.getStringFromProperties(properties, "undo.label"),
			ImageTools.getImageIcon(getStringFromProperties(properties, "undo24.image")).orElse(null)) {
			/** serialVersionUID. */
			private static final long serialVersionUID = 5487046736004252005L;

			@Override
			public void actionPerformed(final ActionEvent event) {
				clearResults();
				undoRedoManager.undoLastAction();
				fileListPanel.scan();
				updateUndoRedoActionsEnabledStatus();
			}
		};
		undoAction.setEnabled(false);
		btn = toolbar.add(undoAction);
		btn.setText("");
		btn.setToolTipText(
			PropertiesHelper.getStringFromProperties(properties, "undo.tooltip.text"));

		redoAction = new AbstractAction(
			PropertiesHelper.getStringFromProperties(properties, "redo.label"),
			ImageTools.getImageIcon(getStringFromProperties(properties, "redo.image")).orElse(null)) {
			/** serialVersionUID. */
			private static final long serialVersionUID = -4409677395710275897L;

			@Override
			public void actionPerformed(final ActionEvent event) {
				clearResults();
				undoRedoManager.redoLastAction();
				fileListPanel.scan();
				updateUndoRedoActionsEnabledStatus();
			}
		};
		redoAction.setEnabled(false);
		btn = toolbar.add(redoAction);
		btn.setText("");
		btn.setToolTipText(PropertiesHelper.getStringFromProperties(properties, "redo.tooltip.text"));

		final Action digitAction = new AbstractAction(
			PropertiesHelper.getStringFromProperties(properties, "zero.label"),
			ImageTools.getImageIcon(getStringFromProperties(properties, "zero.image")).orElse(null)) {
			/** serialVersionUID. */
			private static final long serialVersionUID = 7242761555020376222L;

			/**
			 * Agrega '0' antes de los digitos menores que 10.
			 */
            @Override
            public void actionPerformed(final ActionEvent event) {
                clearResults();
				final List<File> eligibleFiles = AlterUtil.getEligibleFiles(targetFiles, allFilesBox.isSelected(), fileListPanel.getSelectedValues(), prefixPattern.getText());
				final PairRepository pairRepository = new PairRepository(eligibleFiles.size());
                for (final File targetFile : eligibleFiles) {
                    final String name = targetFile.getName();

					// El manejo nuestro de agregar un '0' antes del ultimo digito
                    final int indexOfDot = name.lastIndexOf((int) '.');
					// quiero que sea de la forma '*.*'
                    if (indexOfDot == -1) {
                        continue;
                    }
					// quiero que antes del punto haya un digito y no lo sea el anterior si hay al menos dos caracteres
                    // antes del punto
                    if (indexOfDot >= 2 &&
                        (!Character.isDigit(name.charAt(indexOfDot - 1)) ||
                            Character.isDigit(name.charAt(indexOfDot - 2)))) {
                        continue;
                    }

					// ponemos un cero antes del digito
                    final String newName = ToolBox
                        .replace(name, indexOfDot - 1, 0, "0");

					// Al fin: Renombrar el archivo
                    pairRepository.addPair(targetFile, new File(targetFile.getParentFile(), newName));
                }
                rename(pairRepository);
            }
        };
        btn = toolbar.add(digitAction);
        btn.setText("");
        btn.setToolTipText(PropertiesHelper.getStringFromProperties(properties, "zero.tooltip.text"));

		final Action testAction = new AbstractAction(
			PropertiesHelper.getStringFromProperties(properties, "test.label"),
			ImageTools.getImageIcon(getStringFromProperties(properties, "question.image")).orElse(null)) {
			/** serialVersionUID. */
			private static final long serialVersionUID = 4302371081056498171L;

            /**
			 * Hace como si cambiara los archivos.
			 */
			@Override
			public void actionPerformed(final ActionEvent event) {
				final boolean oldTestMode = testBox.isSelected();
				testBox.setSelected(true);
				replace();
				testBox.setSelected(oldTestMode);
			}
		};
		btn = toolbar.add(testAction);
		btn.setText("");
		btn.setToolTipText(
			PropertiesHelper.getStringFromProperties(properties, "test.tooltip.text"));

		final Action exitAction = new AbstractAction(
			PropertiesHelper.getStringFromProperties(properties, "exit.label"),
			ImageTools.getImageIcon(getStringFromProperties(properties, "stop24.image")).orElse(null)) {
			/** serialVersionUID. */
			private static final long serialVersionUID = -5703530048192613224L;

			@Override
			public void actionPerformed(final ActionEvent event) {
				GUITools.exitApplication(
					GUITools.getFirstParent((Component) event.getSource()),
					true);
			}
		};
		btn = toolbar.add(exitAction);
		btn.setText("");
		btn.setToolTipText(
			PropertiesHelper.getStringFromProperties(properties, "exit.tooltip.text"));
		return toolbar;
	}

	private void updateUndoRedoActionsEnabledStatus() {
		undoAction.setEnabled(undoRedoManager.isUndoable());
		redoAction.setEnabled(undoRedoManager.isRedoable());
	}

	/**
	 * De 0 pa'rriba o de arriba pa'bajo, descontando el nro. de directorios.
	 */
	private void ordenar() {
		int sequence = buttonPanel.isAlfabetico() ? 0 :
			buttonPanel.isAscendente() ? buttonPanel.getOrderStart() - 1
			: targetFiles.length - fileListPanel.dirQty() +  buttonPanel.getOrderStart() - 1;
		final String prefix = prefixPattern.getText();
		clearResults();
		final List<File> eligibleFiles = AlterUtil.getEligibleFiles(targetFiles, allFilesBox.isSelected(), fileListPanel.getSelectedValues(), prefixPattern.getText());
		final PairRepository pairRepository = new PairRepository(eligibleFiles.size());
		for (final File targetFile : eligibleFiles) {
			final String name = targetFile.getName();

			// Al fin: Renombrar el archivo
			final FileTools.SplittedName splittedName = FileTools.splittName(name);
			final String extension = splittedName.extension.equals("") ? "" : '.' + splittedName.extension;
			sequence += buttonPanel.isAscendente() ? 1 : -1;
			final String newName;
			if (buttonPanel.isAlfabetico()) {
				//noinspection StringBufferReplaceableByString
				newName = new StringBuilder().append(prefix)
					.append((char) ('A' + sequence - 1)).append(extension)
					.toString();
			} else {
				//noinspection StringBufferReplaceableByString
				newName = new StringBuilder().append(prefix)
					.append(ToolBox.leftPad(String.valueOf(sequence), 2, '0'))
					.append(extension).toString();
			}
			pairRepository.addPair(targetFile, new File(targetFile.getParentFile(), newName));
		}
		rename(pairRepository);
	}

    /**
     * Cambia la capitalizacion de los nombres de los archivos.
     */
    private void toLowerCase() {
        clearResults();
        final CaseChoicer.Option option = buttonPanel.getCaseChoice();
		final List<File> eligibleFiles = AlterUtil.getEligibleFiles(targetFiles, allFilesBox.isSelected(), fileListPanel.getSelectedValues(), prefixPattern.getText());
		final PairRepository pairRepository = new PairRepository(eligibleFiles.size());
        for (final File targetFile : eligibleFiles) {
            final String name = targetFile.getName();
            final String newName = option.modify(name);

            if (!newName.equals(name)) {    //	Si no hay cambios, no nos molestamos en gastar ciclos de CPU
                pairRepository.addPair(targetFile, new File(targetFile.getParentFile(), newName));
            }
        }
        rename(pairRepository);
    }

    /**
     * Cambia los espacios en blanco por un 'underscore'.
     */
    private void changeSpaces() {
        clearResults();
		final List<File> eligibleFiles = AlterUtil.getEligibleFiles(targetFiles, allFilesBox.isSelected(), fileListPanel.getSelectedValues(), prefixPattern.getText());
        final PairRepository pairRepository = new PairRepository(eligibleFiles.size());
        for (final File targetFile : eligibleFiles) {
            final String name = targetFile.getName();
            final String newName = name.replace(' ', '_');
            pairRepository.addPair(targetFile, new File(targetFile.getParentFile(), newName));
        }
        rename(pairRepository);
    }

    private void insertCharBeforeExtension() {
        clearResults();
		final List<File> eligibleFiles = AlterUtil.getEligibleFiles(targetFiles, allFilesBox.isSelected(), fileListPanel.getSelectedValues(), prefixPattern.getText());
		final PairRepository pairRepository = new PairRepository(eligibleFiles.size());
        for (final File targetFile : eligibleFiles) {
            final String name = targetFile.getName();
			final String newName = AlterUtil.insertCharsBeforeExtension(name, buttonPanel.getRelleno());
            pairRepository.addPair(targetFile, new File(targetFile.getParentFile(), newName));
        }
        rename(pairRepository);
    }

	private void replace() {
        final int localStart = AlterUtil.getIntFromTextField(start);
        final int localLength = AlterUtil.getIntFromTextField(len);

        clearResults();
		final List<File> eligibleFiles = AlterUtil.getEligibleFiles(targetFiles, allFilesBox.isSelected(), fileListPanel.getSelectedValues(), prefixPattern.getText());
		final PairRepository pairRepository = new PairRepository(eligibleFiles.size());
        for (final File targetFile : eligibleFiles) {
            final String name = targetFile.getName();
            pairRepository.addPair(targetFile, new File(targetFile.getParentFile(),
                ToolBox.replace(name, localStart, localLength, relleno.getText())));
        }
        rename(pairRepository);
    }


	/**
     * Renames a File checking if it really should be done and reporting the
     * result.
     *
     * @param file        the File to rename.
     * @param renamedFile the new name.
     */
	private void rename(final File file, final File renamedFile) {
		Boolean result = Boolean.FALSE;
		if (!testBox.isSelected()) {
			if (file.renameTo(renamedFile)) {
				result = Boolean.TRUE;
			} else {
				errorIndicator.activate();
			}
		}
		myGrid.addRow(new Grid.RowInfo(file.getName(), renamedFile.getName(), result));
	}

	/**
     * Renames a set of files checking if it really should be done and reporting the result.
     *
     * @param pairRepository contains the files to rename.
     */
	private void rename(final PairRepository pairRepository) {
        final boolean testing = testBox.isSelected();
        if (!testing) {
            renamer.rename(pairRepository);
            undoRedoManager.startBatch(pairRepository);
            updateUndoRedoActionsEnabledStatus();
        }
        pairRepository.forEach(pair -> {
            myGrid.addRow(new Grid.RowInfo(pair.sourceFile.getName(), pair.targetFile.getName(), pair.renamed));
            if (!testing && !pair.renamed) {
                errorIndicator.activate();
            }
        });
		fileListPanel.scan();
	}

    /**
	 * Antepone el nombre del directorio.
	 */
	private void ezReplace() {
		statusSaver.save();
		start.setText("0");
		len.setText("0");
		relleno.setText(fileListPanel.getCurrentDirectory().getName());
		replace();
		statusSaver.restore();
	}

	/** Iniciar un comparador en otro thread. */
	private void startComparation() {
		new Thread(() -> new Comp(fileListPanel.getCurrentDirectory())).start();
	}

	/**
	 * Limpia el display de resultados.
	 */
	private void clearResults() {
		errorIndicator.deactivate();
		myGrid.clear();
	}

	/** Actualiza la l?nea de estado. */
	private void updateStatusLine() {
		statusLine.updateStatus(fileListPanel.dirQty(), fileListPanel.fileQty(),
			fileListPanel.otherQty(), fileListPanel.getSelectedValues().size());
	}


	/**
	 * Carga todas propiedades de la aplicacion.
	 */
	private void loadProperties(final Properties applicationProps, final Properties customProperties) {
        PropertiesHelper.loadProperties(applicationProps, className);
        PropertiesHelper.loadCustomProperties(customProperties, className);
        lafName = applicationProps.getProperty("lafName");
        lafThemeName = applicationProps.getProperty("lafThemeName");
        lastFilter = applicationProps.getProperty("lastFilter");
        errConsoleBounds = PropertiesHelper.getBounds(applicationProps, "errConsole");
	}

	/**
	 * Guarda todas propiedades en el archivo de configuracion.
	 */
	private void saveProperties(final Properties customProperties) {

        //PropertiesHelper.loadCustomProperties(customProperties, customPropertiesBaseName);
        // FIXME: Tengo que guardar aqu? applicationBounds para no sobrescribir con el valor antiguo el que guarda fancy...
		errConsoleBounds.setBounds(errConsole.getBounds());
		PropertiesHelper.saveBounds(errConsoleBounds, customProperties, "errConsole");
		customProperties.setProperty("lastFilter", lastFilter);

		if (lafName != null) {
			customProperties.setProperty("lafName", lafName);
		}

		if (lafThemeName != null) {
			customProperties.setProperty("lafThemeName", lafThemeName);
		}

		fileListPanel.saveProperties(customProperties);
		myGrid.saveProperties(customProperties);

		try {
            PropertiesHelper.saveCustomProperties(customProperties, className);
		} catch (final IOException ex) {
			ToolBox.showInfo(ex);
		}
	}

	/**
	 * Cambia el nombre de archivos segun ciertos patrones.
	 */
	public static void main(final String[] args) throws IOException {
        try {
            new Alter();
        } catch (final Throwable throwable) {
            LOGGER.error(LoggerFactory.ERROR_TAG, throwable);
        }
    }
}
