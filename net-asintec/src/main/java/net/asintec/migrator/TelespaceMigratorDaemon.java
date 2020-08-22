// ******************************** package
package net.asintec.migrator;


// ******************************** imports

import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
	TelespaceMigratorDaemon <BR>
	@author El Coyote Cojo
	@version 2002.oct.02
*/
public class TelespaceMigratorDaemon {
	// ******************************** Variables de clase
	private static final int TIME_TO_SLEEP = 30 * 60 * 1000;	// Halb Stunde: eine kleine Noppe, ein Schläfchen machen 

	// ******************************** Variables de instancia
	/** Las configuraciones a procesar. Debe estar sincronizado con targetFiles */
	private String[] targetConfs;

	/** Los archivos a procesar. Debe estar sincronizado con targetConfs */
	private String[] targetFiles;


	// ******************************** Constructores

	/**
		Constructor por omision
		@author El Coyote Cojo
		@version 2002.may.31 11:00:47, CEST
	*/
	private TelespaceMigratorDaemon() {
		final MigratorDaemon md = new MigratorDaemon();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_");
		final List<String> confVector = new ArrayList<>();
		final List<String> filesVector = new ArrayList<>();

		// Leer del archivo de configuracion
		try {
			System.err.println("TelespaceMigratorDaemon: Leyendo el Archivo de Configuracion ");
			final BufferedReader in = new BufferedReader(new FileReader(getClass().getName() + ".properties"));
			String linea;
			while((linea = in.readLine()) != null) {
				linea = linea.trim();
				if(linea.startsWith("#") || linea.equals("")) {
					continue;
				}
				confVector.add(linea.substring(0, linea.indexOf('|')));
				filesVector.add(linea.substring(linea.indexOf('|') + 1));
			}
			in.close();
			targetConfs = new String[confVector.size()];
			targetFiles = new String[filesVector.size()];
			confVector.toArray(targetConfs);
			filesVector.toArray(targetFiles);
		}
		catch(final IOException ex) {
			ToolBox.showInfo(ex);
			System.exit(1);
		}

		System.err.println("TelespaceMigratorDaemon: Listo para procesar ");
		while(true) {
			for(int i = 0; i < targetFiles.length; i++) {
				final String now = dateFormat.format(new Date());
				final File theFile = new File(targetFiles[i]);
				if(theFile.exists()) {
					System.err.println("TelespaceMigratorDaemon: procesando " + targetConfs[i]);
					md.migrate(targetConfs[i]);
					theFile.renameTo(new File(theFile.getParent() + "/backup", now + theFile.getName()));
				}
			}
			try {
				Thread.sleep(TIME_TO_SLEEP);
			} catch(final InterruptedException ex) {
				ToolBox.showInfo(ex);
				Thread.currentThread().interrupt();
			}
		}
	}


	// ******************************** Metodos estaticos

	/**
		Ejecuta la aplicación
		@author El Coyote Cojo
		@version 2002.may.31 11:00:47, CEST
	*/
	public static void main(final String[] args) {
		new TelespaceMigratorDaemon();
	}
}
