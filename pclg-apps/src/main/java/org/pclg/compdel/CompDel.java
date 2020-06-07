package org.pclg.compdel;

import org.pclg.log.LoggerFactory;
import org.pclg.runtime.RuntimeControl;
import org.pclg.tools.BoundsInfo;
import org.pclg.tools.Consola;
import org.pclg.tools.DigestTools;
import org.pclg.tools.GUITools;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.SortedProperties;
import org.pclg.tools.ToolBox;
import org.pclg.xtras.ClassPathHacker;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.MAXIMIZED_BOTH;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

/**
 * Compara dos a dos todos los archivos de un arbol de directorios reportando
 * y opcionalmente borrando los que son binariamente iguales.
 *
 * @author El Coyote Cojo.
 * @version 2.0
 */
public final class CompDel {
	private static final Logger LOGGER = LoggerFactory.make();

    final CompDelFrame frame = new CompDelFrame(this);

    /** The name of this class. */
    final String className = getClass().getSimpleName();


	final JTextField excludeListField = new JTextField();


	/** Almacenamos el stream de errores para restaurarlo al final:
	 * fundamentalente para debug (2003.11.05). */
	private final PrintStream system_err = System.err;

	/** La consola de errores (aquí se redirige System.err). */
	final Consola errConsole = Consola.getConsola(frame, "Errores");

	/** Propiedades persistentes. */
	private final Properties applicationProps = new SortedProperties();

	/** posicion y tamaño de la ventana de la mensajes. */
	private BoundsInfo errConsoleBounds;
	private String lastFirstDir;
	private String lastSecondDir;
    private String undoFileLocation;
	boolean supportsCompactTable;

	CompDelWorker slave;

	/** Usado para desactivar el frame completo. */
	//private JComponent myGlassPane = new JLabel();

//	private Cursor controlCursor;			??? crearlo: ver quasimodo\cursor.java
/*
A mouse pointer, I believe is nothing but a cursor. Here's the code to make your own cursors.

Image cursorImage = Toolkit.getDefaultToolkit().getImage(url);

Point hotSpot = new Point(x,y);
//x,y are coordinates for displaying the cursor

String name = "Some name for the cursor";

Cursor cursor = Toolkit.createCustomCursor(cursorImage, hotSpot ,name);

myPanel.setCursor(cursor);

requestFocus();
toFront();
*/

	final boolean standAlone;

	/**
	* Creates a CompDel.
	*/
   public CompDel() {
	   this(false);
   }

	/**
	 * Creates a CompDel.
	 * @param dir1 the first directory to compare.
	 * @param dir2 the second directory to compare.
	 */
	public CompDel(final String dir1, final String dir2) {
		this(dir1, dir2, false);
	}

	public CompDel(final boolean standAlone) {
		this(null, null, standAlone);
	}

	public CompDel(String dir1, String dir2, final boolean standAlone) {
		try {
			frame.setTitle(className);
			loadProperties();
			this.standAlone = standAlone;

			//setGlassPane(myGlassPane);

			if (dir1 == null) {
				dir1 = lastFirstDir;
			}
			if (dir2 == null) {
				dir2 = lastSecondDir;
			}

			setListeners(standAlone);
			lastFirstDir = dir1;
			lastSecondDir = dir2;
			frame.setUpGUI(dir1, dir2);
			setErrConsole();
			frame.requestFocus();
			init();
		} catch (Exception e) {
			throw e;
		}
	}

	private void setErrConsole() {
        final Rectangle bounds = errConsoleBounds.getBounds();
        if (bounds != null) {
            errConsole.setBounds(bounds);
        }
        System.setErr(errConsole);
		errConsole.setVisible(true);
	}

	private void setListeners(final boolean standAlone) {
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent ev) {
                if (standAlone) {
                    GUITools.exitApplication(ev.getWindow(), false);
                } else {
                    frame.dispose();
                }
            }
        });

		// Antes de salir de la aplicacion salvamos las propiedades persistentes
        RuntimeControl.registerShutdownHook(() -> {
            System.setErr(system_err);
            saveProperties();
        });
    }

	void init() {
		slave = new CompDelWorker(this, frame.out, frame.err, frame.controlPanel);
		lastFirstDir = frame.directoriesPanel.getFirstDir();
		lastSecondDir = frame.directoriesPanel.getSecondDir();
		slave.init(lastFirstDir, lastSecondDir, undoFileLocation);
	}

	/**
	 * Carga todas propiedades de la aplicacion.
	 */
	private void loadProperties() {
		// TODO: Si el archivo .properties no existe, o es de tamaño 0, grabar y usar el que esté en el .jar
		try {
			PropertiesHelper.loadProperties(applicationProps, className);

            frame.applicationBounds = PropertiesHelper.getBounds(applicationProps, "application");
			errConsoleBounds = PropertiesHelper.getBounds(applicationProps, "errConsole");
			ClassPathHacker.addFiles(applicationProps.getProperty("classpathEntries"));
			lastFirstDir = applicationProps.getProperty("lastFirstDir", ".");
			lastSecondDir = applicationProps.getProperty("lastSecondDir", ".");
			excludeListField.setText(applicationProps.getProperty("excludeList", ""));
            frame.errScrollBarPosition = applicationProps.getProperty("errScrollBarPosition");
            frame.outScrollBarPosition = applicationProps.getProperty("outScrollBarPosition");
			undoFileLocation = applicationProps.getProperty("undoFileLocation", System.getProperty("java.io.tmpdir"));

			// obtener propiedades desde otro archivo
            setUpDatabaseConnnection(applicationProps.getProperty("dbConnnectionInfoFile"));
		} catch (final IOException ex) {
			ToolBox.showInfo(ex);
		}
	}


    /**
	 * Guarda todas propiedades en el archivo de configuracion.
	 */
	private void saveProperties() {
		final Properties customProperties = new SortedProperties();
        final BoundsInfo mainBoundsInfo = new BoundsInfo();
   		mainBoundsInfo.setBounds(frame.getBounds());
   		mainBoundsInfo.setMinimized(frame.getExtendedState() == ICONIFIED);
   		mainBoundsInfo.setMaximized(frame.getExtendedState() == MAXIMIZED_BOTH);
		PropertiesHelper.saveBounds(mainBoundsInfo, customProperties, "application");
		PropertiesHelper.saveBounds(new BoundsInfo(errConsole.getBounds()), customProperties, "errConsole");

		customProperties.setProperty("lastFirstDir", lastFirstDir);
		customProperties.setProperty("lastSecondDir", lastSecondDir);
		customProperties.setProperty("errScrollBarPosition", frame.scrollErr.getScrollBarPosition());
		customProperties.setProperty("outScrollBarPosition", frame.scrollOut.getScrollBarPosition());

//*
		try {
			PropertiesHelper.saveCustomProperties(customProperties, className);
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
            LOGGER.log(Level.SEVERE, "Error accessing: " + className, ex);
        } catch (final Throwable throwable) {
            ToolBox.showInfo(throwable);
            LOGGER.log(Level.SEVERE, "Error: " + className, throwable);
		}
//*/
	}

	private void setUpDatabaseConnnection(final String dbConnnectionInfoFile) throws IOException {
		if (dbConnnectionInfoFile != null) {
			ClassPathHacker.addFiles(PropertiesHelper.getProperty(dbConnnectionInfoFile, "classpathEntries"));
			final String driverName = PropertiesHelper.getProperty(dbConnnectionInfoFile, "driverName");
			final String dbURL = PropertiesHelper.getProperty(dbConnnectionInfoFile, "dbURL");
			final String databaseUser = PropertiesHelper.getProperty(dbConnnectionInfoFile, "user", "");
			final String databasePwd = PropertiesHelper.getProperty(dbConnnectionInfoFile, "pwd", "");
			supportsCompactTable = Boolean.parseBoolean(PropertiesHelper.getProperty(dbConnnectionInfoFile, "supportsCompactTable"));
			DigestTools.init(dbURL, driverName, databaseUser, databasePwd, dbConnnectionInfoFile);
			LOGGER.log(Level.INFO, "driverName: " + driverName);
			LOGGER.log(Level.INFO, "dbURL: " + dbURL);
		} else {
			throw new InternalError("Can't do anything without a Database Connnection Info File");
		}
	}


	/**
	 * Señala que algo mu malo pasó.
	 */
	public void alert() {
		JOptionPane.showMessageDialog(frame,"Algo mu malo ha pasao",
				"Whitney we've a problem", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Entry point of the application.
	 * @param args arg[0] y arg[1] son los directorios base para la comparacion.
	 * Si no se especifican, se supone el directorio de trabajo.
     * @throws java.io.IOException si hay problemas de IO silver.
	 */
	public static void main(final String[] args) throws IOException {
		final boolean standAlone = true;
		switch (args.length) {
		case 0:
			new CompDel(standAlone);
			break;
		case 1:
			new CompDel(args[0], args[0], standAlone);
			break;
		case 2:
			new CompDel(args[0], args[1], standAlone);
			break;
		default:
			System.err.println("Uso: java CompDel [dir1 [dir2]]");
			System.exit(1);
		}
	}
}