package org.pclg.agenda.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.pclg.Globals;
import org.pclg.agenda.Agenda;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.AgendaRecordUtil;
import org.pclg.agenda.AgendaUtil;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.gui.actions.CountryMgtAction;
import org.pclg.agenda.gui.actions.GroupsMgtAction;
import org.pclg.agenda.gui.actions.PhoneTypeMgtAction;
import org.pclg.agenda.gui.actions.PluginAction;
import org.pclg.agenda.gui.actions.VCardReaderAction;
import org.pclg.agenda.gui.actions.VCardSaverAction;
import org.pclg.agenda.gui.actions.ViewLogAction;
import org.pclg.agenda.gui.print.PrintForm;
import org.pclg.fortunes.FortuneTeller;
import org.pclg.fortunes.Manager;
import org.pclg.gui.FancyButtonPanel;
import org.pclg.gui.I18NManager;
import org.pclg.gui.JTabbedPaneWithCloseIcons;
import org.pclg.gui.LnFController;
import org.pclg.gui.SplashWindow;
import org.pclg.gui.VersatileJMenu;
import org.pclg.log.LoggerFactory;
import org.pclg.runtime.RuntimeControl;
import org.pclg.tools.BoundsInfo;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;
import org.pclg.tools.ObservableProperties;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.SortedProperties;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;


import static org.pclg.gui.FancyButtonPanel.Orientation.VERTICAL;
import static org.pclg.tools.PropertiesHelper.getIntFromProperties;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * @author El Coyote Cojo
 * @since 25-sep-2007 17:46:11
 */
@SuppressWarnings("serial")
public final class AgendaGUI extends JFrame {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final int FRAME_WIDTH = 800;
	private static final int FRAME_HEIGHT = 600;
	private final transient I18NManager i18nManager;
	private final JTabbedPaneWithCloseIcons
		tabbedPane = new JTabbedPaneWithCloseIcons();
	private final transient AgendaDb agendaDb;
	private final int numberOfPlugins;
	private final ListadoPanelController listadoPanelController;
    private transient Action exitAction;
	private transient Action newRecordAction;
	private transient Action copyRecordAction;
	private transient Action closeAllRecordsAction;
	private transient Action saveAllRecordsAction;
	private transient Action pasteRecordAction;
	private transient Action saveVCardsAction;
	private transient Action readVCardsAction;

	/** Los posibles criterios de listado de los contactos. */
	public enum ListCriterium {
        byName, byDate, byMark, byBirthday, deleted, byGroup, recentlyModified,
        filtered, byInterestingDate, customQuery
	}

    private final ListadoPanel listadoPane;

	private final Properties properties;

    /** Propiedades persistentes. */
   	private final Properties guiCustomProperties = new SortedProperties();

	private final StatusPanel statusPanel;


    /** Para cut -n- paste con el clipboard global. */
	private final transient Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    /** Para cut -n- paste sin el clipboard global. */
    //final Clipboard clipboard = new Clipboard("Agenda.clipboard");

	public AgendaGUI(final Properties properties, final AgendaDb agendaDb, SplashWindow splashWindow)
            throws SQLException {
		super(getStringFromProperties(properties, "AgendaDb.title"));
        splashWindow.setStatus("AgendaGUI");
		this.properties = properties;
		this.agendaDb = agendaDb;
		if (properties instanceof ObservableProperties) {
			((ObservableProperties) properties).addChangeObserver(this::objectChanged);
		}
		i18nManager = I18NManager.getInstance(properties);
		ImageTools.getImageIcon(getStringFromProperties(properties, "Agenda.image"))
            .ifPresent(icon -> setIconImage(icon.getImage()));

		try {
			UIManager.setLookAndFeel(properties.getProperty("Agenda.L&F"));
		} catch (final Exception ex) {
			LOGGER.warn("Cannot set L&F", ex);
		}

		numberOfPlugins = getIntFromProperties(properties,"PluginAction.maxHistory", 10);

		// Dar la oportunidad de salvar los cambios al pobre usuario (yo)
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
                orderlyExit();
			}
		});

		final JPanel mainPane = new JPanel(new BorderLayout());
		listadoPanelController = new ListadoPanelController(this, agendaDb, properties);
		listadoPane = listadoPanelController.getListadoPanel();
		mainPane.add(listadoPane, BorderLayout.CENTER);

		setUpActions();	// setUpActions() depende de listadoPane y createButtonPanel() depende de setUpActions()

		mainPane.add(createButtonPanel(), BorderLayout.EAST);

		tabbedPane.setTabPlacement(SwingConstants.TOP);
		tabbedPane.addPlainTab(getStringFromProperties(properties,
                "listado.tab.title"), mainPane);
		tabbedPane.setIconAt(0, ImageTools.getImageIcon(
            getStringFromProperties(properties, "AgendaGUI.i18n.emblem")).orElse(
			null));
		tabbedPane.setCloseButtonToolTip(getStringFromProperties(properties,
                "TabbedPane.closeButtonToolTip"));

        final Container contentPane = getContentPane();
        contentPane.add(tabbedPane, BorderLayout.CENTER);
		statusPanel = new StatusPanel(agendaDb, properties);
        contentPane.add(statusPanel, BorderLayout.SOUTH);

		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		createMenu();
        loadCustomProperties();
		/* posicion y tamaño de la ventana de la aplicacion. */
 		final BoundsInfo applicationBounds = PropertiesHelper.getBounds(guiCustomProperties, "application");
		GUITools.setBounds(this, applicationBounds);

		// Antes de salir de la aplicacion salvamos las propiedades persistentes
        RuntimeControl.registerShutdownHook(this::saveCustomProperties);

		setVisible(true);
        splashWindow.dispose();
        requestFocus();
		if (Boolean.parseBoolean(properties.getProperty("Agenda.FortunesAtStartup"))) {
			showFortune();
		}
		if (Boolean.parseBoolean(properties.getProperty("Agenda.InterestingDatesAtStartup"))) {
			alertSignificantDates();
		}
        listadoPane.showData(AgendaGUI.ListCriterium.byBirthday);
        statusPanel.update();  //FIXME: chapucilla para que se muestre.
	}

	/**
	 * Show a message informing about people whose birhdays meet an interesting
	 * point.
	 */
	private void alertSignificantDates() {
		try {
			final List<AgendaRecord> records = agendaDb.selectContactsByInterestingDates();
			if (!records.isEmpty()) {
				final String titleFechasInteresantes =
                    getStringFromProperties(properties,
                        "AgendaGUI.byInterestingDatesTitle");
				final StringBuilder message = new StringBuilder();
				message.append(titleFechasInteresantes);
				final Locale locale = new Locale(
                    getStringFromProperties(properties, "Application.ForceLanguage"));
				final MessageFormat format = new MessageFormat(
                    getStringFromProperties(properties,
                        "AgendaGUI.byInterestingDatesFormat"), locale);
				for (final AgendaRecord record : records) {
                  	final AgendaUtil.AgeInfo ageInfo =
                        AgendaUtil.computeAgeAndDays(record.getDay(), record.getMonth() - 1, record.getYear());
					final Object[] formatArgs = {record.getFirstname(),
						record.getLastname(),
                        String.valueOf(ageInfo.getDays())};
					message.append(format.format(formatArgs));
				}
				final JTextArea textArea = new JTextArea(message.toString());
				textArea.setEditable(false);
				
	            final JOptionPane pane = new JOptionPane(textArea,
	            	JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
	            final JDialog dialog = pane.createDialog(this,
	            	UIManager.getString("OptionPane.messageDialogTitle", locale));
	            dialog.setModalityType(Dialog.ModalityType.MODELESS);
                GUITools.setDialogResizable(pane);
				SwingUtilities.invokeLater(() -> dialog.setVisible(true));
			}
		} catch (final SQLException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
		}
	}

	private void setUpActions() {
		closeAllRecordsAction = new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				closeAll();
			}
		};
		i18nManager.configureI18NAction(closeAllRecordsAction,
			"AgendaGUI.closeAllRecords", "AgendaGUI.closeAllRecordsImage");

		saveAllRecordsAction = new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent event) {
		        for (final Component component : tabbedPane.getComponents()) {
		            if (component instanceof DataEntry) {
		                // El fucking compilator debería saber que no tengo que hacer el cast
		                ((DataEntry) component).saveData();
		            }
		        }
			}
	  	};
		i18nManager.configureI18NAction(saveAllRecordsAction,
			"AgendaGUI.saveAllRecords", "AgendaGUI.saveAllRecordsImage");

        saveVCardsAction = new VCardSaverAction(this, properties, listadoPane);
		i18nManager.configureI18NAction(saveVCardsAction,
			"AgendaGUI.SaveVcard", "AgendaGUI.SaveVCardImage");

        readVCardsAction = new VCardReaderAction(this, properties, agendaDb, tabbedPane);
		i18nManager.configureI18NAction(readVCardsAction, "AgendaGUI.ReadVcard", "AgendaGUI.ReadVCardImage");

		newRecordAction = new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				final DataEntry dataEntry = new DataEntry(properties, agendaDb,
					tabbedPane, AgendaGUI.this, null);
				add2TabbedPane(getStringFromProperties(properties,
                    "new.contact.tab.title"), dataEntry);
			}
	  	};
		i18nManager.configureI18NAction(newRecordAction,
			"AgendaGUI.addButton", "new.record.image");

		copyRecordAction = new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				final Component selectedComponent = tabbedPane.getSelectedComponent();
				if (selectedComponent instanceof DataEntry) {
					final DataEntry dataEntry = (DataEntry) selectedComponent;
					final String xmlRecord = dataEntry.getData().toXML();
					LOGGER.debug("Copy record: " + xmlRecord);
					clipboard.setContents(new StringSelection(xmlRecord), null);
				}
			}
	  	};
		i18nManager.configureI18NAction(copyRecordAction,
			"AgendaGUI.menu.copy_record", "copy.record.image");

		pasteRecordAction = new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				final Component selectedComponent = tabbedPane.getSelectedComponent();
				 if (selectedComponent instanceof DataEntry) {
					 final DataEntry dataEntry = (DataEntry) selectedComponent;
					 //odd: the Object param of getContents is not currently used
					 final Transferable contents = clipboard.getContents(null);
					 if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						try {
							final String xmlRecord = (String) contents.getTransferData(DataFlavor.stringFlavor);
							LOGGER.debug("Paste record: " + xmlRecord);
							dataEntry.fillData(AgendaRecordUtil.xml2AgendaRecord(xmlRecord));
						//highly unlikely since we are using a standard DataFlavor
						} catch (UnsupportedFlavorException | IOException
							| ParserConfigurationException | SAXException
							| IllegalAccessException | NoSuchFieldException
							| IllegalArgumentException | SecurityException
							| DOMException | NoSuchMethodException
							| InvocationTargetException | SQLException ex) {
								LOGGER.error(LoggerFactory.ERROR_TAG, ex);
						}
					 }
				}
			}
		};
		i18nManager.configureI18NAction(pasteRecordAction,
			"AgendaGUI.menu.paste.record", "paste.record.image");
	}

	private void orderlyExit() {
        if (closeAll()) {
			try {
				setVisible(false);
				if (agendaDb.checkLastUpdated(properties, this)) {
			        dispose();
			        System.exit(0);
				}
				setVisible(true);
			} catch (final SQLException ex) {
				setVisible(true);
				LOGGER.error(LoggerFactory.ERROR_TAG, ex);
			}
        }
    }

    /**
     * Tries to close all the DataEntry tabs.
     * @return <code>true</code> if all are closed, <code>false</code> otherwise.
     */
	private boolean closeAll() {
		boolean allAreClosed = true;
		for (final Component component : tabbedPane.getComponents()) {
            if (component instanceof DataEntry) {
                // El fucking compilator debería saber que no tengo que hacer el cast
                final DataEntry dataEntry = (DataEntry) component;
                tabbedPane.setSelectedComponent(dataEntry);
                if (dataEntry.willClose()) {
                    tabbedPane.remove(dataEntry);
                } else {
                	allAreClosed = false;
                }
            }
        }
		return allAreClosed;
	}

    private JPanel createButtonPanel() {
		final JButton addButton = new JButton(newRecordAction);
		addButton.setToolTipText(getStringFromProperties(properties,
            "AgendaGUI.addButton"));

		i18nManager.configureI18NAction(listadoPanelController.groupAction,
            "AgendaGUI.byGroupButton", "AgendaGUI.byGroupImage");
		final JButton groupButton = new JButton(listadoPanelController.groupAction);
		groupButton.setToolTipText(getStringFromProperties(properties,
            "AgendaGUI.byGroupButton"));
		i18nManager.configureI18NAction(listadoPanelController.filteredAction,
			"AgendaGUI.filteredButton", "AgendaGUI.filteredImage");
		final JButton filteredButton = new JButton(listadoPanelController.filteredAction);
		filteredButton.setToolTipText(getStringFromProperties(properties,
            "AgendaGUI.filteredButton"));

		i18nManager.configureI18NAction(listadoPanelController.customQueryAction,
			"AgendaGUI.customQueryButton", "AgendaGUI.customQueryImage");
		final JButton customQueryButton = new JButton(listadoPanelController.customQueryAction);
		customQueryButton.setToolTipText(getStringFromProperties(properties,
            "AgendaGUI.customQueryButton"));

		final Action printAction = new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				final PrintForm printForm = new PrintForm(properties, agendaDb, tabbedPane);
				add2TabbedPane(getStringFromProperties(properties,
                    "phone.list.title"), printForm);
			}
        };
		i18nManager.configureI18NAction(printAction,
			"AgendaGUI.printButton", "AgendaGUI.printImage");
		final JButton printButton = new JButton(printAction);
		printButton.setToolTipText(getStringFromProperties(properties,
            "AgendaGUI.printButton"));

        exitAction = new AbstractAction() {
             @Override
             public void actionPerformed(final ActionEvent ev) {
                 orderlyExit();
             }
         };
 		i18nManager.configureI18NAction(exitAction,
 			"AgendaGUI.menu.exit", "exit.system.image");

        final FancyButtonPanel buttonPane = new FancyButtonPanel(VERTICAL,
            addButton,
            new SimpleQueryActionButton(ListCriterium.byBirthday, listadoPane, i18nManager, properties),
            new SimpleQueryActionButton(ListCriterium.byDate, listadoPane, i18nManager, properties),
            new SimpleQueryActionButton(ListCriterium.byInterestingDate, listadoPane, i18nManager, properties),
            new SimpleQueryActionButton(ListCriterium.byName, listadoPane, i18nManager, properties),
            new SimpleQueryActionButton(ListCriterium.byMark, listadoPane, i18nManager, properties),
            groupButton,
            new SimpleQueryActionButton(ListCriterium.recentlyModified, listadoPane, i18nManager, properties),
            filteredButton,
            customQueryButton,
            new SimpleQueryActionButton(ListCriterium.deleted, listadoPane, i18nManager, properties),
            printButton,
            new JButton(exitAction));
        buttonPane.setComponentsAlignment(SwingConstants.LEFT);

		return buttonPane;
	}

	public void add2TabbedPane(final String name, final Component component) {
		tabbedPane.addTab(name, component);
		tabbedPane.setSelectedComponent(component);
		final Component tabComponentAt = tabbedPane
			.getTabComponentAt(tabbedPane.getSelectedIndex());
		if (tabComponentAt instanceof JTabbedPaneWithCloseIcons.CloseTabPanel
				&& component instanceof ActionListener) {
			final JTabbedPaneWithCloseIcons.CloseTabPanel closeTabPanel
				= (JTabbedPaneWithCloseIcons.CloseTabPanel) tabComponentAt;
			closeTabPanel.addActionListener((ActionListener) component);
		}
	}

	/**
     * Muestra unas DataEntry con los registros seleccionados en la tabla.
     * @param records los registros a mostrar.
     */
    void showSelectedRecords(final AgendaRecord[] records) {
        for (final AgendaRecord record : records) {
            showRecord(record);
        }
    }

    void showRecord(final AgendaRecord record) {
        // Si el registro ya está en alguna pestaña, seleccionamos esta.
        for (int ii = 0, tabCount = tabbedPane.getTabCount(); ii < tabCount; ii++) {
            final Component component = tabbedPane.getComponentAt(ii);
            if (component instanceof DataEntry) {
                final DataEntry dataEntry = (DataEntry) component;
                if (dataEntry.getRecordId() == record.getKey()) {
                    tabbedPane.setSelectedIndex(ii);
                    return;
                }
            }
        }

        // Si no está, lo agregamos.
        final DataEntry dataEntry = new DataEntry(properties, agendaDb,
            tabbedPane, this, record);
        final String title = beautify(record.getFirstname()) + ' ' + beautify(record.getLastname());
        add2TabbedPane(title.trim().length() > 0 ? title :
            getStringFromProperties(properties, "no.name.tab.title"), dataEntry);
    }

    private static String beautify(final String str) {
		return str == null ? "" : str.trim();
	}

	private PluginAction pluginAction;
	
	private void createMenu() {
		final JMenuBar menuBar = new JMenuBar();
		final JMenu menuFile = new VersatileJMenu();
		i18nManager.configureI18NComponent(menuFile, "AgendaGUI.menu.file");

      	final Action groupsMgtAction = new GroupsMgtAction(this, properties, agendaDb, statusPanel::update);
		i18nManager.configureI18NAction(groupsMgtAction,
			"AgendaGUI.menu.gestion.grupos", "AgendaGUI.byGroupImage");
		menuFile.add(groupsMgtAction);
      	
      	final Action countryMgtAction = new CountryMgtAction(this, properties, agendaDb, statusPanel::update);
		i18nManager.configureI18NAction(countryMgtAction,
			"AgendaGUI.menu.gestion.paises", "AgendaGUI.countryImage");
		menuFile.add(countryMgtAction);

      	final Action phoneTypeMgtAction = 
      		new PhoneTypeMgtAction(this, properties, agendaDb, statusPanel::update);
        i18nManager.configureI18NAction(phoneTypeMgtAction,
			"AgendaGUI.menu.gestion.phoneType", "AgendaGUI.phoneTypeImage");
    	menuFile.add(phoneTypeMgtAction);
    			
        menuFile.addSeparator();
        pluginAction = new PluginAction(this, properties, agendaDb, numberOfPlugins, statusPanel::update);
		i18nManager.configureI18NAction(pluginAction,
			"AgendaGUI.menu.file.plugin", "AgendaGUI.menu.file.plugin.image");
		menuFile.add(pluginAction);

		menuFile.addSeparator();
        
		final Action copyDBAction = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent ev) {
                try {
    				agendaDb.checkLastUpdated(properties, AgendaGUI.this);
                } catch (final SQLException ex) {
                    LOGGER.error(LoggerFactory.ERROR_TAG, ex);
				}
            }
        };
		i18nManager.configureI18NAction(copyDBAction,
			"AgendaGUI.menu.file.copy.database",
			"AgendaGUI.menu.file.copy.database.image");
		menuFile.add(copyDBAction);
		
		final Action changeDBAction = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent ev) {
				if (closeAll()) {
	                try {
						agendaDb.changeDatabase(properties, AgendaGUI.this);
						listadoPane.showData(ListCriterium.byBirthday);
					} catch (final SQLException ex) {
    	                LOGGER.error(LoggerFactory.ERROR_TAG, ex);
					}
				}
            }
        };
		i18nManager.configureI18NAction(changeDBAction,
			"AgendaGUI.menu.file.change.database",
			"AgendaGUI.menu.file.change.database.image");
		menuFile.add(changeDBAction);

        menuFile.addSeparator();
        menuFile.add(saveAllRecordsAction).setAccelerator(
        	KeyStroke.getKeyStroke(KeyEvent.VK_S, 
        		InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
        menuFile.add(closeAllRecordsAction).setAccelerator(
        	KeyStroke.getKeyStroke(KeyEvent.VK_Q, 
        		InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));

        menuFile.addSeparator();

		menuFile.add(exitAction).setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));

		final JMenu menuEdit = new VersatileJMenu();
		i18nManager.configureI18NComponent(menuEdit, "AgendaGUI.menu.edit");

		menuEdit.add(newRecordAction).setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK));
		menuEdit.add(copyRecordAction).setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));
		menuEdit.add(pasteRecordAction).setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK));
        menuEdit.addSeparator();
        menuEdit.add(readVCardsAction).setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK));
        menuEdit.add(saveVCardsAction).setAccelerator(
      			KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK));

		final JMenu menuHelp = new VersatileJMenu();
		i18nManager.configureI18NComponent(menuHelp, "AgendaGUI.menu.info");

		final Action javaVersionAction = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent ev) {
                JOptionPane.showMessageDialog(AgendaGUI.this,
					Globals.COPYRIGHT_MSG + '\n' +
					getStringFromProperties(properties, "version.java.lbl")
                        + System.getProperty("java.runtime.version")
                        + '\n'
                        + getStringFromProperties(properties, "process.id.lbl")
                        + Agenda.PROCESS_ID
						+ '\n'
                        + getStringFromProperties(properties, "db.path.lbl")
                        + getStringFromProperties(properties, "AgendaDb.path")
						+ '\n'
                        + getStringFromProperties(properties, "db.path.backup.lbl")
                        + getStringFromProperties(properties, "AgendaDb.secondary.path"),
					getStringFromProperties(properties, "informacion.title"),
                    JOptionPane.INFORMATION_MESSAGE);
               }
    	};
		i18nManager.configureI18NAction(javaVersionAction,
			"AgendaGUI.menu.system.info", "AgendaGUI.systemInfoImage");
		menuHelp.add(javaVersionAction).setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.ALT_DOWN_MASK));

		final Action fortunesAction = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent ev) {
                showFortune();
            }
        };
		i18nManager.configureI18NAction(fortunesAction,
			"AgendaGUI.menu.show.fortune", "AgendaGUI.fortunesImage");
		menuHelp.add(fortunesAction).setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.ALT_DOWN_MASK));

        menuHelp.addSeparator();
		final Action settingsAction = new AbstractAction() {
			final transient SettingsEditor editor = new SettingsEditor(AgendaGUI.this,
				properties, guiCustomProperties);
            @Override
            public void actionPerformed(final ActionEvent ev) {
				editor.editSettings();
				saveCustomProperties();
            }
        };
		i18nManager.configureI18NAction(settingsAction,
			"AgendaGUI.menu.info.settings", "AgendaGUI.settingsImage");
		menuHelp.add(settingsAction).setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK));

        menuHelp.addSeparator();
		final Action viewLogAction = new ViewLogAction(this, tabbedPane);
		i18nManager.configureI18NAction(viewLogAction,
			"AgendaGUI.menu.info.viewLog", "AgendaGUI.viewLogImage");
		menuHelp.add(viewLogAction).setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_DOWN_MASK));

		menuBar.add(menuFile);
		menuBar.add(menuEdit);
        final JMenu lnFMenu = LnFController.getLnFMenu(this);
        i18nManager.configureI18NComponent(lnFMenu, "AgendaGUI.menu.LnF");
        menuBar.add(lnFMenu);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);
	}

	private transient FortuneTeller fortuneTeller;
    private void showFortune() {
        try {
			if (fortuneTeller == null) {
				fortuneTeller = new FortuneTeller(this, new Manager());
			}
			SwingUtilities.invokeLater(fortuneTeller::showRandomFortune);
        } catch (final Throwable throwable) {
            LOGGER.warn("Ignoring error showing fortunes", throwable);
        }
    }


	void refreshRecord(final AgendaRecord record) {
        listadoPane.refreshRecord(record);
        statusPanel.update();
	}

    /**
     * Carga todas propiedades de la aplicacion.
     */
    private void loadCustomProperties() {
        try (final FileInputStream in = new FileInputStream("Agenda_custom.properties")) {
			guiCustomProperties.load(in);
        } catch (final IOException ex) {
            LOGGER.warn(ex);
        }
    }

    /**
     * Guarda todas propiedades en el archivo de configuracion.
     */
    private void saveCustomProperties() {
    	final BoundsInfo applicationBounds = new BoundsInfo();
		applicationBounds.setBounds(getBounds());
		applicationBounds.setMinimized(getExtendedState() == ICONIFIED);
		applicationBounds.setMaximized(getExtendedState() == MAXIMIZED_BOTH);
		PropertiesHelper.saveBounds(applicationBounds, guiCustomProperties, "application");

		listadoPanelController.saveYourProperties(guiCustomProperties);

        guiCustomProperties.setProperty("DataEntry.lastImagesDirectory",
			properties.getProperty("DataEntry.lastImagesDirectory", ""));
		guiCustomProperties.setProperty("AgendaGUI.lastVCardsDirectory",
			properties.getProperty("AgendaGUI.lastVCardsDirectory", ""));
		guiCustomProperties.setProperty("AgendaGUI.lastVCardsDirectory",
			properties.getProperty("AgendaGUI.lastVCardsDirectory", ""));

		pluginAction.saveYourProperties(guiCustomProperties);

		try {
            final FileOutputStream outputFile = new FileOutputStream("Agenda_custom.properties");
            guiCustomProperties.store(outputFile, "Configuracion de AgendaGUI");
            outputFile.close();
        } catch (final IOException ex) {
            LOGGER.warn(ex);
        }
    }

	public void objectChanged(final ObservableProperties observableProperties) {
		SwingUtilities.invokeLater(() -> {
			setTitle(getStringFromProperties(properties, "AgendaDb.title"));
			ImageTools.getImageIcon(getStringFromProperties(properties,
                "AgendaGUI.i18n.emblem"))
                .ifPresent(icon -> tabbedPane.setIconAt(0, icon));
			tabbedPane.setCloseButtonToolTip(getStringFromProperties(properties,
                "TabbedPane.closeButtonToolTip"));
			statusPanel.update();
		});
	}
}
