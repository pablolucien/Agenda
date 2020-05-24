//El lado derecho debe ser una tabla. (como en Alter)
//La clase debe ser un panel para poder ponerla dentro de otras

// ******************************** package
package org.pclg.tools;

// ******************************** imports

import org.pclg.gui.LnFController;
import org.pclg.log.LoggerFactory;
import org.pclg.xtras.ClassPathHacker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import static org.pclg.tools.FileTools.NULL_FILE_ARRAY;

/**
	Comp
	Compara dos a dos todos los archivos de un arbol de directorios
	reportando cuales son binariamente iguales
 *
 * @author El Coyote cojo.
 * @version Unknown.
*/
// TODO: unificar las funcionalidades de esta clase y CompDel
public class Comp extends JFrame {

    private static final org.apache.log4j.Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = 5526824526669141780L;

    //???????  Eliminar
	class Output extends JTextArea {
        private static final long serialVersionUID = -2646421542119392987L;

        public void println() {
			setText(getText() + "\n");
System.out.println();
		}
		public void println(final String s) {
			setText(getText() + s + "\n");
System.out.println(s);
		}
	}

	/** Determina si lo hacemos recursivo o no */
	private final boolean recurse = true;

	private final Output out = new Output();
	private final JScrollPane scrollOut = new JScrollPane(out);
	private final Output err = new Output();
	private final JScrollPane scrollErr = new JScrollPane(err);

	/**
	*/
	public Comp(final File dir) {
		this(dir, false);
	}

	private Comp(File dir, final boolean standAlone) {
		super("Comp");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);  // La accion por omision es HIDE_ON_CLOSE
		addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(final WindowEvent ev) {
					if(standAlone) {
						GUITools.exitApplication(ev.getWindow(), true);
					}
					else {
						dispose();
					}
				}
			}
		);

        try {
            ClassPathHacker.addFile("C:/home/development/lib/lib_L&F");
            LnFController.setLAF(this, "com.incors.plaf.kunststoff.KunststoffLookAndFeel", "org.pclg.gui.temas.MonoTheme");
        } catch (IOException e) {
            LOGGER.error(LoggerFactory.ERROR_TAG, e);
        }

		// Setup de la GUI
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollErr, scrollOut);
		splitPane.setOneTouchExpandable(true);
		getContentPane().add(splitPane,   BorderLayout.CENTER);
		setSize(840, 480);
		setVisible(true);
		splitPane.setDividerLocation(0.5);  // hay que hacerlo despues del show para que tenga efecto

		// dir es el directorio base para la comparacion.
		if(dir == null) {		// Seleccionar el directorio para la comparacion
			final JFileChooser chooser = new JFileChooser(new File(".").getAbsoluteFile());
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogTitle("Seleccione el directorio para la comparacion");
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				dir = chooser.getSelectedFile();
			}
			else {
				System.exit(1);
			}
		}

		final int MILIS_POR_ARCHIVO = 58;  // mili segundos
		final int cron = Chrono.getChrono();

		// En un arreglo de File ponemos todos los archivos del arbol
		List<File> archivo;

		try {
			err.println("A pedir el dir:\t " + new Date());
			final Dir directorio = new Dir(Dir.NULL_EXCLUDE_LIST);
			archivo = directorio.listarArchivos(dir, recurse, false);
			Chrono.mark(cron);
			err.println("\nDuracion del dir: " + Chrono.timeDetail(Chrono.elapsed(cron)));
			setTitle("Comparando " + archivo.size() + " archivos en: " + dir.getAbsolutePath() + " |");
			err.println("\nComparando " + archivo.size() + " archivos en: " + dir.getAbsolutePath());
			err.println("Tiempo estimado: " + Chrono.timeDetail(archivo.size() * MILIS_POR_ARCHIVO));
		}
		catch(final IOException ex) {
			err.println("Si pasas el nombre de un directorio es mas de pinga");
			return;
		}

		err.println("\nComenzando la comparacion:\t " + new Date());

		// Comparamos cada archivo con los siguientes: si coinciden escribimos
		// un mensaje

		// ---------------------------------------------------------------
		// Calcular los digests
		err.println("A pedir el digest:\t " + new Date());
		String[] digest = DigestTools.generateDigest(archivo.toArray(NULL_FILE_ARRAY), true, true);
		final long  desperdicioDeEsteArchivo = 0; // ?????? implementarlo
		final long  repetidosDeEsteArchivo = 0;
		long  desperdicio = 0;
		long  repetidos = 0;
		final NumberFormat numberFormat = NumberFormat.getInstance();

//		for(int i = 0; i < digest.length; i++)
		try {int i = -1; while(true) { i++;
			// ??? este setTitle() debe consumir muchisimo: usar un StringBuffer
			setTitle("(" + (((i + 1) * 100) / archivo.size()) + "%): " + (i + 1) + "/" + archivo.size() + " - Comp");
			if(digest[i] == null) {
				continue;
			}
			int nroDeClones = 0;
//			for(int j = i + 1; j < digest.length; j++)
			try {int otroJ = i; while(true) { otroJ++;
				if(digest[otroJ] == null) {
					continue;
				}
				if(!digest[i].equals(digest[otroJ])) {
					continue;
				}
				if(FileTools.compareContents(archivo.get(i), archivo.get(otroJ))) {
					final long size = archivo.get(otroJ).length();
					desperdicio += size;
					nroDeClones++;
					if(nroDeClones == 1) {
						repetidos++;
						out.println(new StringBuilder().append("\n0 ")
                            .append(archivo.get(i)).append("\n").append(nroDeClones)
                            .append(" ").append(archivo.get(otroJ)).append("\t\t\t")
                            .append(numberFormat.format(size)).toString());
					}
					else {
						out.println(new StringBuilder().append(nroDeClones)
                            .append(' ').append(archivo.get(otroJ)).append("\t\t\t")
                            .append(numberFormat.format(size)).toString());
					}

					digest[otroJ] = null;    // Al clon no lo comparamos con mas nadie
				}
			}}
			catch(final ArrayIndexOutOfBoundsException ex) {
			}
		}}
		catch(final ArrayIndexOutOfBoundsException ex) {
		}

		Chrono.mark(cron);
		tee("Archivos repetidos: " + repetidos);
		tee("Desperdicio: " + numberFormat.format(desperdicio));
		err.println("Terminando:\t " + new Date() + "\nTiempo real: " + Chrono.timeDetail(Chrono.elapsed(cron)));
		// Liberar la memoria, que puede ser mogollon
		digest = null;
		archivo = null;
	}


	/**
		Escribe su entrada en out y err simultaneamente
	*/
	void tee(final String s) {
		out.println(s);
		err.println(s);
	}


	/**
		@param args [0] es el directorio base para la comparacion.
				 Si no se especifica, se supone el directorio de trabajo
	*/
	public static void main(final String [] args) {
		if(args.length > 0) {
			new Comp(new File(args[0]), true);
		}
		else {
			new Comp(null, true);
		}
	}
}
