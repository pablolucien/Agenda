// ******************************** package
package net.asintec.migrator;

// ******************************** imports

import net.asintec.migrator.preprocessors.Preprocessor;
import org.pclg.gui.LnFController;
import org.pclg.tools.BoundsInfo;
import org.pclg.tools.Consola;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.SortedProperties;
import org.pclg.tools.ToolBox;
import org.pclg.xtras.EnhancedStringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Interfaz gr�fica en interactiva para el
 * Migrador gen�rico de datos
 * Copyright Asintec Gestion S.L. 2001
 *
 * @author El Coyote Cojo
 * @version 1.00
 */
public class Migrator extends JFrame implements ActionListener, ItemListener, MigratorConstants, MigratorUserInterface {

    private static final long serialVersionUID = -8508198829617490125L;
    /**
     * La extensi�n usual de los ficheros de configuracion
     */
    private static final String CONF_FILE_EXTENSION = ".conf";

//============================================ Estos deben ir a ToolBox	======================================================
    /**
     * Utilizado para cargar y guardar los ficheros de configuracion
     */
    private final FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(final File f) {
            if (f.isDirectory()) {
                return true;
            }
            if (f.getName().toLowerCase().endsWith(CONF_FILE_EXTENSION)) {
                return true;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Archivos de configuracion (*" + CONF_FILE_EXTENSION + ")";
        }
    };
//============================================ Estos deben ir a ToolBox	(hasta aqui) =========================================
    /**
     * La tabla (grafica) donde se presentan los datos
     */
    /*private*/ final MigratorGrid myGrid = new MigratorGrid(this);
    /**
     * The name of this class.
     */
    private final String className = getClass().getSimpleName();
    /**
     * Necesario para poder usar metodos como getResource() antes del constructor
     */
    private final Class myClass = getClass();
    /**
     * Utilizado para cargar y guardar los ficheros de configuracion
     */
    private final JFileChooser fileChooser = new JFileChooser(new File(".").getAbsolutePath());
    /**
     * Propiedades de configuracion de la aplicacion
     */
    private final Properties applicationProps = new SortedProperties();
    // ------------- Los botones de control de la aplicaci�n
    private final JPanel buttonPanel = new JPanel(new GridLayout(1, 0));

    // ------------- Propiedades de configuracion de la aplicacion
    private final JButton targetBt = GUITools.addButton(this, buttonPanel, "Destino de datos");
    private final JButton sourceBt = GUITools.addButton(this, buttonPanel, "Origen de datos");
    private final JButton acceptBt = GUITools.addButton(this, buttonPanel, "Migrar", Color.red, "Ejecuta la migracion");
    // Menus
    private final JMenuBar mb = new JMenuBar();
    // Menu 'Archivos'
    private final JMenu fileMenu = new JMenu("Archivo");
    private final JMenuItem kkMenuItem = GUITools.addMenuItem(this, fileMenu, "&Seleccionar Origen de Datos", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Import16.gif"));
    private final JMenuItem loadMenuItem = GUITools.addMenuItem(this, fileMenu, "-&Cargar Configuraci�n", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Open16.gif"));
    private final JMenuItem loadLastMenuItem = GUITools.addMenuItem(this, fileMenu, "Cargar configuracion &previa", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Open16.gif"));
    private final JMenuItem saveMenuItem = GUITools.addMenuItem(this, fileMenu, "&Guardar Configuraci�n", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Save16.gif"));
    private final JMenuItem editMenuItem = GUITools.addMenuItem(this, fileMenu, "-&Editar el archivo de configuraci�n", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Edit16.gif"));
    private final JMenuItem exitMenuItem = GUITools.addMenuItem(this, fileMenu, "-&Salir", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Stop16.gif"));
    // Menu 'Ver'
    private final JMenu viewMenu = new JMenu("Ver");
    private JMenuItem adjustMenuItem = GUITools.addMenuItem(this, viewMenu, "-&Ajustar las ventanas", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Stop16.gif"));
    private final JCheckBoxMenuItem outputMenuItem = new JCheckBoxMenuItem("Consola de Salida", false);
    private final JCheckBoxMenuItem errorsMenuItem = new JCheckBoxMenuItem("Consola de Errores", false);
    private final JCheckBoxMenuItem kkDataSinkMenuItem = new JCheckBoxMenuItem("-Oracle", false);
    // Menu 'Herramientas'
    private final JMenu utilMenu = new JMenu("Herramientas");
    private final JMenuItem browserMenuItem = GUITools.addMenuItem(this, utilMenu, "ResultSetBrowser", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "History16.gif"));
    private final JMenuItem creationMenuItem = GUITools.addMenuItem(this, utilMenu, "-Sentencia de creaci�n", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "TipOfTheDay16.gif"));
    private final JMenuItem duplMenuItem = GUITools.addMenuItem(this, utilMenu, "Duplicar tabla", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Copy16.gif"));
    private JMenuItem dbUpdateMenuItem = GUITools.addMenuItem(null, utilMenu, "-dbUpdate", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Edit16.gif"));
    private final JMenuItem viewPreprocessorsMenuItem = GUITools.addMenuItem(this, utilMenu, "Ver preprocesadores", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Find16.gif"));
    private final JMenuItem saveStructMenuItem = GUITools.addMenuItem(this, utilMenu, "saveStruct", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Save16.gif"));
    private final JMenuItem optionsMenuItem = GUITools.addMenuItem(this, utilMenu, "-Opciones...", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Preferences16.gif"));
    // Menu 'Help'
    private final JMenu helpMenu = new JMenu("Ayuda");
    private final JMenuItem helpContentMenuItem = GUITools.addMenuItem(this, helpMenu, "Contenido", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "Help16.gif"));
    private final JMenuItem aboutMenuItem = GUITools.addMenuItem(this, helpMenu, "-Acerca de ...", ImageTools.getImageIcon("/toolbarButtonGraphics/general/" + "About16.gif"));
    /**
     * La consola de salida standard
     */
    private final Consola out;
    /**
     * La consola de salida de errores
     */
    private final Consola err;
    /**
     * El se�or que realiza la migracion
     */
    private MigratorArbeiter currentMigrator;
    /**
     * Controla un poco la cantidad de mensajes que damos
     *
     * @since 2002.oct.02
     */
    private int debugLevel;
    /**
     * posicion y tama�o de la ventana
     */
    private Rectangle bounds;
    /**
     * posicion y tama�o de la ventana de salida
     */
    private Rectangle outputBounds;
    /**
     * posicion y tama�o de la ventana de errores
     */
    private Rectangle errorsBounds;
    /**
     * Indica si est� visible la ventana de salida
     */
    private boolean outputVisible;
    /**
     * Indica si est� visible la ventana de errores
     */
    private boolean errorsVisible;
    /**
     * El nombre del Look & Feel
     */
    private String lafName;
    /**
     * El nombre del 'tema' del Look & Feel
     */
    private String lafThemeName;
    /**
     * Para las pruebas
     */
    private boolean probandoOracle;
    /**
     * El ultimo archivo de configuracion usado
     */
    private String lastConfigurationFile;
    /**
     * El Editor de textos a usar (Aplicacion externa)
     */
    private String textEditor;
    /**
     * ??? Debe ser almacenada para usarla en ejecuciones subsiguientes
     */
    private int lastSplitPaneDividerLocation = 150;
    /**
     * El selector de la Base de datos destino
     */
    private DatabaseSelector targetSelector;
    /**
     * El selector de la Base de datos origen
     */
    private DatabaseSelector sourceSelector;
    /**
     * El nombre del archivo de configuracion que se est� usando actualmente
     */
    private String currentConfigurationFile;

    // ===================================================================================================================================
    /**
     * Un sitio donde ponemos mensajes al usuario
     */
    private JDialog messageDialog;

    /**
     * Constructor sin parametros
     */
    public Migrator() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);    // La accion por omision es HIDE_ON_CLOSE
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent ev) {
                saveProperties();
                GUITools.exitApplication(ev.getWindow(), false);
            }

            // Quiero tener el foco, aunque se presenten las ventanas hijas despues
            @Override
            public void windowActivated(final WindowEvent e) {
                ((Component) e.getSource()).requestFocus();
            }
        });

        loadProperties();    // Cargar la ultima configuracion del programa


        kkDataSinkMenuItem.setState(probandoOracle);


        setTitle(className);
        final URL imgResource = myClass.getResource("/images/create.gif");
        if (imgResource != null) {
            setIconImage(Toolkit.getDefaultToolkit().getImage(imgResource));
        } else {
            System.err.println("No puedo obtener el icono de la aplicacion");
        }

        acceptBt.setMnemonic(KeyEvent.VK_M);
        sourceBt.setMnemonic(KeyEvent.VK_O);
        targetBt.setMnemonic(KeyEvent.VK_D);

        createMenu();

        createSourceConnection();
        createTargetConnection();

        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, targetSelector, sourceSelector);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(.5);

        final JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane, myGrid);
        splitPane2.setOneTouchExpandable(true);

        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane2, BorderLayout.CENTER);
//		getContentPane().add(new MemMonitor(), BorderLayout.SOUTH);

        out = Consola.getConsola(this, "Output");
        System.setOut(out);
        out.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent ev) {
                outputMenuItem.doClick();
            }
        });

        err = Consola.getConsola(this, "Errores");
        System.setErr(err);
        err.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent ev) {
                errorsMenuItem.doClick();
            }
        });

        if (bounds == null) {
            System.err.println("centrando la ventana por omision");
            pack();
            GUITools.center(this, null);   // Centrar en la pantalla
        } else {
            setBounds(bounds);            // Ultima posicion guardada de la pantalla
        }

//		setVisible(true);


        if (outputBounds != null) {
            out.setBounds(outputBounds);
        }

        if (errorsBounds == null) {
            err.setBounds(new Rectangle(0, 440, 458, 408));
        } else {
            err.setBounds(errorsBounds);
        }

        if (outputVisible) {
            outputMenuItem.doClick();
        }

        if (errorsVisible) {
            errorsMenuItem.doClick();
        }

        // Esto hay que hacerlo una sola vez para no tener entradas repetidas
        // en el combo box de tipos de archivo
        // NO, EL PROBLEMA ES CUANDO CAMBIO DE L&F
        fileChooser.setFileFilter(fileFilter);

        // Intentamos que el foco est� en la ventana principal y no en las consolas
        requestFocus();
    }

    public static void main(final String[] args) {
        final Migrator migrator = new Migrator();
        migrator.setVisible(true);
        if (args.length > 0) {
            final File f = new File(args[0]);
            if (!f.exists()) {
                System.out.println(args[0] + " no es un archivo ni un directorio");
                System.exit(1);
            }

            if (f.isDirectory()) {
                final String[] files = f.list();
                for (int i = 0; i < files.length; i++) {
                    files[i] = new File(f, files[i]).getAbsolutePath();
                }
                migrator.migrate(files);
            } else {
                migrator.migrate(f.getAbsolutePath());
            }
        }
    }

    /**
     * Determina de donde se est� ejecutando la aplicacion: un directorio o un jar
     */
    File getExecutionPath() {
        final Class myClass = getClass();
        String myName = myClass.getName();
        myName = myName.substring(myName.lastIndexOf('.') + 1);

        final URL appURL = myClass.getResource(myName + ".class");
        if (appURL == null) {
            System.err.println("No puedo determinar el punto de ejecucion");
            return null;
        }

        // Determinar que jar o directorio se est� ejecutando
        final String path = appURL.getPath();
        int end = path.lastIndexOf('!');        // Si es un jar, termina con ! y el nombre de la clase
        if (end == -1) {
            end = path.lastIndexOf('/');        // Si es un directorio, termina con / y el nombre de la clase
        }
        final String baseDir = path.substring(path.indexOf('/') + 1, end);
        return new File(baseDir);
    }


    // Migracion automatica ==============================================================================================================

    /**
     * Crea la conexion con la fuente de datos
     */
    private void createSourceConnection() {
        sourceSelector = new DatabaseSelector(this, "Origen de datos",
			/*sourceDatabase*/ null, DatabaseSelector.ORG);
    }

    /**
     * Crea la conexion con el destino de datos
     */
    private void createTargetConnection() {
        if (probandoOracle) {
            targetSelector = new DatabaseSelectorOracle(this,
                "Destino de datos", DatabaseSelector.DST);
        } else {
            targetSelector = new DatabaseSelector(this,
                "Destino de datos", null, DatabaseSelector.DST);
        }
    }


    // Carga y almacenamiento de archivos de configuracion ===============================================================================

    /**
     * Crea el menu de la aplicacion
     */
    private void createMenu() {
        viewMenu.add(outputMenuItem);
        viewMenu.add(errorsMenuItem);
        viewMenu.add(kkDataSinkMenuItem);
        ImageTools.getImageIcon("/toolbarButtonGraphics/development/" + "Host16.gif").ifPresent(icon -> {
            outputMenuItem.setIcon(icon);
            errorsMenuItem.setIcon(icon);
        });
        outputMenuItem.addItemListener(this);
        errorsMenuItem.addItemListener(this);
        kkDataSinkMenuItem.addItemListener(this);

        fileMenu.setMnemonic(KeyEvent.VK_A);
        viewMenu.setMnemonic(KeyEvent.VK_V);

        utilMenu.setMnemonic(KeyEvent.VK_H);

        helpMenu.setMnemonic(KeyEvent.VK_Y);
        aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

        mb.add(fileMenu);
        mb.add(viewMenu);
        mb.add(LnFController.getLnFMenu(this));
        mb.add(utilMenu);

        //      mb.setHelpMenu(helpMenu);  // ??? setHelpMenu() not yet implemented.
        // mb.add(Box.createHorizontalGlue());		// Pero esto funciona, pero no es una buena idea
        mb.add(helpMenu);

        mb.setBorderPainted(true);
        setJMenuBar(mb);

        if (lastConfigurationFile != null) {
            loadLastMenuItem.setText(lastConfigurationFile);
        } else {
            loadLastMenuItem.setEnabled(false);
        }

        if (textEditor == null) {
            editMenuItem.setEnabled(false);
        }
    }

    /**
     * Realiza una migracion automatizada
     *
     * @param configFiles Un arreglo con los nombres de los archivos de configuracion que tienen los datos de la migracion
     */
    void migrate(final String[] configFiles) {
        for (final String configFile : configFiles) {
            migrate(configFile);
        }
    }

    /**
     * Realiza una migracion automatizada
     *
     * @param configFile El nombre del archivo de configuracion que tiene los datos de la migracion
     */
    void migrate(final String configFile) {
        while (MigratorArbeiter.migrating) {
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println(configFile);
        loadConfiguration(configFile);
        acceptBt.doClick();
    }

    /**
     * Almacena la configuracion de una migracion de datos
     */
    private void saveConfiguration() {
//		fileChooser.setFileFilter(fileFilter);
        if (currentConfigurationFile == null) {
            currentConfigurationFile = targetSelector.getSelectedTable() + CONF_FILE_EXTENSION;
        }
        fileChooser.setSelectedFile(new File(currentConfigurationFile));
        if (fileChooser.showDialog(this, "Guardar como ...") == JFileChooser.APPROVE_OPTION) {
            try {
                final File file = fileChooser.getSelectedFile();

                // ??? OJO Si no termina en CONF_FILE_EXTENSION, agregarselo
				/*
				if(!file.getName().toLowerCase().endsWith(CONF_FILE_EXTENSION.toLowerCase())) {
					file = new File(file.getAbsolutePath() + CONF_FILE_EXTENSION);
				}
				*/

                if (file.exists()) {
                    final int res = JOptionPane.showConfirmDialog(this, file.getName() + " ya existe \n �Machacar el fichero?", file.getName() + " ya existe", JOptionPane.YES_NO_OPTION);
                    if (res != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                final PrintWriter pw = new PrintWriter(new FileWriter(file));
                pw.println("#");
                pw.println("# Source configuration");
                pw.println("#");
                pw.println("dataSource=" + sourceSelector.getDataSource().getClass().getName());
                pw.println("sourceDriverName=" + sourceSelector.getDriverName());
                pw.println("sourceDbURL=" + sourceSelector.getDbURL());
                pw.println("sourceDatabase=" + sourceSelector.getDatabaseName());
                // FIXME: Esto debe estar despues de los otros para poder abridla :(
                pw.println("sourceTable=" + sourceSelector.getSelectedTable());
                pw.println("#");
                pw.println("# Target configuration");
                pw.println("#");
                pw.println("dataSink=" + targetSelector.getDataSource().getClass().getName());
                pw.println("targetDriverName=" + targetSelector.getDriverName());
                pw.println("targetDbURL=" + targetSelector.getDbURL());
                // FIXME: Esto debe estar despues de los otros para poder abridla :(
                pw.println("targetTable=" + targetSelector.getSelectedTable());

                pw.println("#");
                pw.println("# Fields correspondence");
                pw.println("#");
                for (int i = 0; i < myGrid.getRowCount(); i++) {
                    for (int j = 0; j < myGrid.getColumnCount(); j++) {
                        Object datum = myGrid.getValueAt(i, j);
                        if (datum == null) {
                            datum = "";
                        }
                        final String field = datum.toString();
                        pw.print(field + DELIMITER);
                    }
                    pw.println();
                }

                pw.close();
                lastConfigurationFile = file.getAbsolutePath();
                loadLastMenuItem.setText(lastConfigurationFile);
                currentConfigurationFile = file.getName();
                setTitle(className + " - " + currentConfigurationFile);
            } catch (final IOException ex) {
                ToolBox.showInfo(ex);
                errorsMenuItem.setState(true);
            } catch (final Exception ex) {
                ToolBox.showInfo(ex);
                errorsMenuItem.setState(true);
            }
        }
    }

    /**
     * Selecciona un archivo de configuracion de una migracion de datos y lo carga
     */
    private void loadConfiguration() {
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
//		fileChooser.setFileFilter(fileFilter);
        if (fileChooser.showDialog(this, "Abrir") == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();
            loadConfiguration(file.getAbsolutePath());
        }
    }

    // ============== implementacion de ActionListener ===================================================================================================

    /**
     * Carga un archivo de configuracion de una migracion de datos
     *
     * @param fileName El nombre del archivo que queremos cargar
     */
    private void loadConfiguration(final String fileName) {
        final File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("El archivo <" + fileName + "> no existe");
            errorsMenuItem.setState(true);
            return;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            final ConfigurationData configurationData = new ConfigurationData();
            String linea;
            while ((linea = in.readLine()) != null) {
                linea = linea.trim();
                if (linea.length() == 0 || linea.charAt(0) == '#') {
                    continue;
                }
                if (linea.startsWith("dataSource=")) {
                    configurationData.dataSource =
                        linea.substring(linea.indexOf('=') + 1);
                    continue;
                }
                if (linea.startsWith("sourceDatabase=")) {
                    configurationData.sourceDatabase =
                        linea.substring(linea.indexOf('=') + 1);
                    continue;
                }
                if (linea.startsWith("sourceDriverName=")) {
                    configurationData.sourceDriverName =
                        linea.substring(linea.indexOf('=') + 1);
                    continue;
                }
                if (linea.startsWith("sourceDbURL=")) {
                    configurationData.sourceDbURL =
                        linea.substring(linea.indexOf('=') + 1);
                    continue;
                }
                if (linea.startsWith("sourceTable=")) {
                    configurationData.sourceTable = linea.substring(linea.indexOf('=') + 1);
                    continue;
                }
                if (linea.startsWith("dataSink=")) {
                    configurationData.dataSink = linea.substring(linea.indexOf('=') + 1);
                    continue;
                }
                if (linea.startsWith("targetDbURL=")) {
                    configurationData.targetDbURL = linea.substring(linea.indexOf('=') + 1);
                    continue;
                }
                if (linea.startsWith("targetDriverName=")) {
                    configurationData.targetDriverName = linea.substring(linea.indexOf('=') + 1);
                    continue;
                }
                if (linea.startsWith("targetTable=")) {
                    configurationData.targetTable = linea.substring(linea.indexOf('=') + 1);
                    continue;
                }
                configurationData.fieldMigrationInfo.add(linea);
            }
            sourceSelector.setDataSource(configurationData.dataSource);
            sourceSelector.setDatabaseName(configurationData.sourceDatabase);
            sourceSelector.setDriverName(configurationData.sourceDriverName);
            sourceSelector.setDbURL(configurationData.sourceDbURL);
            // Hay que abrir la db antes de abrir la tabla
            if (!configurationData.dataSource.contains("DataSourceClarion")) { // FIXME: Super flechazo
                sourceSelector.openDatabase();
            }
            sourceSelector.setSelectedTable(configurationData.sourceTable);

            // FIXME: De momento est� a pelo en el c�digo
            //targetSelector.setDataSource(configurationData.dataSink);
            targetSelector.setDbURL(configurationData.targetDbURL);
            targetSelector.setDriverName(configurationData.targetDriverName);
            // Hay que abrir la db antes de abrir la tabla
            targetSelector.openDatabase();
            targetSelector.setSelectedTable(configurationData.targetTable);

            //myGrid.clear();
            int row = 0;
            for (final String fieldAssignementInfo : configurationData.fieldMigrationInfo) {
                final EnhancedStringTokenizer st =
                    new EnhancedStringTokenizer(fieldAssignementInfo, DELIMITER,
                        EnhancedStringTokenizer.NO_CONSECUTIVE_DELIMS);
                int col = 0;
                while (st.hasMoreTokens()) {
                    final String datum = st.nextToken();
                    myGrid.setValueAt(datum, row, col);
                    col++;
                }
                row++;
            }

            lastConfigurationFile = file.getAbsolutePath();
            loadLastMenuItem.setText(lastConfigurationFile);
            currentConfigurationFile = file.getName();
            setTitle(className + " - " + currentConfigurationFile);
        } catch (final FileNotFoundException ex) {
            System.err.println("El archivo no existe");
            System.err.println(ex.getMessage());
            errorsMenuItem.setState(true);
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
            errorsMenuItem.setState(true);
        } catch (final Exception ex) {
            ToolBox.showInfo(ex);
            errorsMenuItem.setState(true);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException ex) {
            }
        }
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final Object eventSource = event.getSource();

        final String targetSelectedTable = targetSelector.getSelectedTable();
        if (eventSource == browserMenuItem) {
            new ResultSetBrowser(this, targetSelector.getConnection(),
                targetSelectedTable);
        } else if (eventSource == saveStructMenuItem) {
            // Esto debe hacerlo el editor de textos
//			TextDatabaseEditor.saveStruct(sourceSelector.getSelectedTable());
            TextDatabaseEditor.saveStruct("Cli_migr.txt", "Cli_migr.struct");
        } else if (eventSource == exitMenuItem) {
            saveProperties();
            GUITools.exitApplication(this, false);
        } else if (eventSource == loadMenuItem) {
            loadConfiguration();
        } else if (eventSource == saveMenuItem) {
            saveConfiguration();
        } else if (eventSource == editMenuItem) {
            if (textEditor != null && lastConfigurationFile != null) {
                try {
                    final String[] cmdarray = new String[2];
                    cmdarray[0] = textEditor;
                    cmdarray[1] = lastConfigurationFile;
                    Runtime.getRuntime().exec(cmdarray);
                } catch (final IOException ex) {
                    ToolBox.showInfo(ex);
                    errorsMenuItem.setState(true);
                }
            }
        } else if (eventSource == kkDataSinkMenuItem) {
            final DataSelector d = new DataSelector(this);
            d.setVisible(true);
        } else if (eventSource == kkMenuItem) {
            final DataSelector d = new DataSelector(this);
            d.setVisible(true);
        } else if (eventSource == sourceBt) {
            if (sourceSelector.selectDatabase()) {
                sourceSelector.openDatabase();
            }
        } else if (eventSource == targetBt) {
            if (targetSelector.selectDatabase()) {
                targetSelector.openDatabase();
            }
        } else {
            final String sourceSelectedTable = sourceSelector.getSelectedTable();
            final DataSource sourceDataSource = sourceSelector.getDataSource();
            if (eventSource == duplMenuItem) {
                final String tableName = (String) JOptionPane.showInputDialog(this, "Introduzca el nombre de la tabla",
                    "Nombre de la tabla", JOptionPane.QUESTION_MESSAGE, null, null,
                    sourceSelectedTable);
                if (tableName != null) {
                    targetSelector.createTable(tableName,
                        sourceDataSource.getFieldNames(),
                        sourceDataSource.getFieldTypes(),
                        sourceDataSource.getFieldTypeNames(),
                        sourceDataSource.getFieldSizes(),
                        sourceDataSource.getPrimaryKey()
                    );
                }
            } else if (eventSource == creationMenuItem) {
                final String tableName =
                    (String) JOptionPane.showInputDialog(this,
                        "Introduzca el nombre de la tabla",
                        "Nombre de la tabla", JOptionPane.QUESTION_MESSAGE,
                        null, null, sourceSelectedTable);
                if (tableName != null) {
                    targetSelector.showCreateTable(tableName,
                        sourceDataSource.getFieldNames(),
                        sourceDataSource.getFieldTypes(),
                        sourceDataSource.getFieldTypeNames(),
                        sourceDataSource.getFieldSizes(),
                        sourceDataSource.getPrimaryKey()
                    );
                }
            } else if (eventSource == optionsMenuItem) {
                final OptionsDialog d = new OptionsDialog(this);
                d.setVisible(true);
            } else if (eventSource == acceptBt) {
                if (acceptBt.getText().equals("Migrar")) {
                    acceptBt.setText("Detener");
                    acceptBt.setMnemonic(KeyEvent.VK_T);
                    acceptBt.setToolTipText("Detiene la migracion");
                    currentMigrator = new MigratorArbeiterStandard();
                    currentMigrator.setDebugLevel(debugLevel);
                    final DataSource targetDataSource =
                        targetSelector.getDataSource();
                    currentMigrator.migrate(this, sourceDataSource,
                        targetDataSource,
                        sourceSelectedTable, targetSelectedTable,
                        targetSelector.getFieldTypes(targetSelectedTable),
                        targetSelector.getUnique(),
                        myGrid);
                } else {
                    acceptBt.setText("Migrar");
                    acceptBt.setMnemonic(KeyEvent.VK_M);
                    acceptBt.setToolTipText("Ejecuta la migracion");
                    if (currentMigrator != null) {
                        currentMigrator.abort();
                    }
                }
            } else if (eventSource == aboutMenuItem) {
                JOptionPane.showConfirmDialog(this, "Migrator versi\u00F3n 1.00\nCopyright \u00A9 2.001 Asintec Gesti\u00F3n S.L.",
                    "Acerca de Migrator",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            } else if (eventSource == helpContentMenuItem) {
	/*
				JOptionPane.showConfirmDialog(this, "No es necesaria niguna ayuda\n"
													+ "Este programa es absolutamente intuitivo\n"
													+ "Si usted cree que necesita ayuda es porque\n"
													+ "no sabe utilizar ordenadores\n"
													+ "�Ded\u00EDquese a otra profesi\u00F3n!",
													"Acerca de Migrator",
													JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
	*/
                JOptionPane.showConfirmDialog(this, "<HTML>"
                        + "<I>"
                        + "C�mo realizar una migracion de datos:\n"
                        + "* Seleccione un origen y un destino de datos \n"
                        + "* Seleccione la tabla de destino y la de origen\n"
                        + "* Relacione los campos fuente con los de destino\n"
                        + "* Si lo desea puede dejar campos vacios, o con un valor constante\n"
                        + "* En caso necesario, hay preprocesadores disponibles\n"
                        + "    para preparar los datos\n"
                        + "* Cuando est� todo listo, pulse el boton de migrar\n"
                        + "* Si lo desea puede guardar la configuracion que ha creado\n"
                        + "    para ser usada en el futuro\n"
                        + "\n",
                    "Ayuda de Migrator",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            } else if (eventSource == viewPreprocessorsMenuItem) {
                final File path = getExecutionPath();
                System.out.println(">> Preprocesadores disponibles en " + path + " <<");
                final Class[] clases = ToolBox.findClasses(Preprocessor.class, path);
                for (int i = 0; i < clases.length; i++) {
                    try {
                        final Preprocessor preprocessor = (Preprocessor) clases[i].newInstance();
                        //System.out.println(clases[i]);
                        System.out.println(preprocessor.getDescription());
                    } catch (final IllegalAccessException ex) {
                        ToolBox.showInfo(ex);
                    } catch (final InstantiationException ex) {
                        ToolBox.showInfo(ex);
                    }
                }
                outputMenuItem.setState(true);
            } else if (eventSource == loadLastMenuItem) {
                if (lastConfigurationFile != null) {
                    loadConfiguration(lastConfigurationFile);
                }
            } else {
                System.err.println(className
                    + ".actionPerformed(): Evento desconocido "
                    + eventSource.toString());
            }
        }
    }

    // Carga y almacenamiento de las propiedades de la aplicacion ========================================================================

    @Override
    public void itemStateChanged(final ItemEvent event) {
        if (event.getSource() == outputMenuItem) {
            out.setVisible(outputMenuItem.getState());
            outputVisible = outputMenuItem.getState();
        } else if (event.getSource() == errorsMenuItem) {
            err.setVisible(errorsMenuItem.getState());
            errorsVisible = errorsMenuItem.getState();
        } else if (event.getSource() == kkDataSinkMenuItem) {
            probandoOracle = kkDataSinkMenuItem.getState();
//	createTargetConnection();			// Hay que ponerlo en el SplitPane
            saveProperties();
        }
    }

    /**
     * Guarda una propiedad en el archivo de configuracion
     */
    void saveProperty(final String key, final String value) {
        applicationProps.setProperty(key, value);

//		saveProperties();
        try {
            final FileOutputStream out = new FileOutputStream(className
                + ".properties");
            applicationProps.store(out, "Configuracion de " + className);
            out.close();
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
        }
    }

    /**
     * Guarda todas propiedades en el archivo de configuracion
     */
    void saveProperties() {
        bounds = getBounds();
        PropertiesHelper.saveBounds(new BoundsInfo(bounds), applicationProps, "application");

        if (out != null) {
            outputBounds = out.getBounds();
            PropertiesHelper.saveBounds(new BoundsInfo(outputBounds), applicationProps, "output");
        }

        if (err != null) {
            errorsBounds = err.getBounds();
            PropertiesHelper.saveBounds(new BoundsInfo(errorsBounds), applicationProps, "errors");
        }

        if (lastConfigurationFile != null) {
            applicationProps.setProperty("lastConfigurationFile",
                lastConfigurationFile);
        }

        applicationProps.setProperty("outputVisible", Boolean.toString(outputVisible));
        applicationProps.setProperty("errorsVisible", Boolean.toString(errorsVisible));
        applicationProps.setProperty("probandoOracle", Boolean.toString(probandoOracle));

        applicationProps.setProperty("debugLevel", Integer.toString(debugLevel));

        if (lafThemeName != null) {
            applicationProps.setProperty("lafThemeName", lafThemeName);
        }

        try (final FileOutputStream out = new FileOutputStream(className + ".properties")) {
            applicationProps.store(out, "Configuracion de " + className);
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
        }
    }

    /**
     * Carga todas propiedades de la aplicacion
     */
    void loadProperties() {
        try {
            final FileInputStream in = new FileInputStream(className
                + ".properties");
            applicationProps.load(in);
            in.close();

            // Posicion y tama�o
            bounds = loadBounds("applicationBounds");
            outputBounds = loadBounds("outputBounds");
            errorsBounds = loadBounds("errorsBounds");
            outputVisible = Boolean.valueOf(applicationProps.getProperty("outputVisible")).booleanValue();
            errorsVisible = Boolean.valueOf(applicationProps.getProperty("errorsVisible")).booleanValue();
            probandoOracle = Boolean.valueOf(applicationProps.getProperty("probandoOracle")).booleanValue();
            lastConfigurationFile = applicationProps.getProperty("lastConfigurationFile");
            textEditor = applicationProps.getProperty("textEditor");

            // Look & Feel
            lafName = applicationProps.getProperty("lafName");
            lafThemeName = applicationProps.getProperty("lafThemeName");
            try {
                debugLevel = Integer.parseInt(applicationProps.getProperty("debugLevel"));
            } catch (final NumberFormatException ex) {
                ToolBox.showInfo(ex, true);
            }
        } catch (final FileNotFoundException ex) {
            System.err.println("No existe el archivo de propiedades");
        } catch (final IOException ex) {
            ToolBox.showInfo(ex, true);
        }
    }


    // === Implementacion de MigratorUserInterface =======================================================================================

    Rectangle loadBounds(final String boundName) {
        try {
            return new Rectangle(
                Integer.parseInt(applicationProps.getProperty(boundName + ".x")),
                Integer.parseInt(applicationProps.getProperty(boundName + ".y")),
                Integer.parseInt(applicationProps.getProperty(boundName + ".w")),
                Integer.parseInt(applicationProps.getProperty(boundName + ".h"))
            );
        } catch (final NumberFormatException ex) {
            ToolBox.showInfo(ex, true);
            return null;
        }
    }

    /**
     * Da un mensaje al usuario
     *
     * @param msg El mensaje
     */
    public void showMsg(final String title, final String msg) {
        messageDialog = new JDialog(this, title);
        messageDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        messageDialog.getContentPane().add(new JLabel(msg), BorderLayout.CENTER);
        messageDialog.pack();
        messageDialog.setVisible(true);
        GUITools.center(messageDialog, this);
    }

    /**
     * Notificacion de fin de la migracion
     */
    public void endMsg() {
        if (messageDialog != null) {
            messageDialog.setVisible(false);
            messageDialog.dispose();
            messageDialog = null;
        }
        acceptBt.setText("Migrar");
        acceptBt.setToolTipText("Ejecuta la migracion");
        Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Da un mensaje de informacion al usuario
     *
     * @param msg El mensaje
     */
    public void showInfo(final String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informaci�n", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Da un mensaje de alerta al usuario
     *
     * @param msg El mensaje
     */
    public void showAlert(final String msg) {
        JOptionPane.showMessageDialog(this, msg, "Alerta", JOptionPane.ERROR_MESSAGE);
    }

    // ===================================================================================================================================

    class ConfigurationData {
        final List<String> fieldMigrationInfo = new ArrayList<>();
        String dataSource;
        String sourceDatabase;
        String sourceDriverName;
        String sourceDbURL;
        String sourceTable;
        String dataSink;
        String targetDbURL;
        String targetDriverName;
        String targetTable;
    }
}
