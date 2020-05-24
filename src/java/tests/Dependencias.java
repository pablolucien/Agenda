package tests;// ******************************** package

// ******************************** imports

import org.pclg.tools.ExtensionFiltro;
import org.pclg.tools.FileComparator;
import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


/**
	Dependencias <BR>
	Trata de determinar las dependencias de una clase leyendo el archivo fuente (y en un futuro el .class)
	(para ponerlas en el makefile)
	@author El Coyote Cojo
	@version 2003.jul.05 16:45:17, CEST
*/
public class Dependencias {
	// ******************************** Variables de clase
	/** Los packages de los que no es necesario informar */
	private static final String[] ignorePackages = {"java.", "javax." };

	// ******************************** Variables de instancia

	// ******************************** Inicializacion estática
	
	// ******************************** Constructores

	/**
		Constructor por omision
		--author El Coyote Cojo
		--version 2003.jul.05 16:45:17, CEST
	*/
	private Dependencias() {
		final File dir = new File("C:\\home\\pablo\\java\\pclg");
		final File[] files = dir.listFiles(new ExtensionFiltro(new String[] {"java"}, "Acepta sólo '*.java'", false));
		Arrays.sort(files, new FileComparator());
        for (final File file : files) {
            System.out.println("** " + file + " ******************");
            checkDependencies(file);
        }
	}

	// ******************************** Metodos de instancia
	void checkDependencies(final File file) {
		try {
			final BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
reading:	while((line = in.readLine()) != null) {
				line = line.trim();
				if(line.startsWith("import ")) {
					final String importClause = line.substring(7, line.lastIndexOf(';')).trim();	// 7 == "import ".length()
                    for (final String ignorePackage : ignorePackages) {
                        if (importClause.startsWith(ignorePackage)) {
                            continue reading;
                        }
                    }
					System.out.println("      " + importClause);
				}
			}
			in.close();
		}
		catch(final IOException ex) {
			ToolBox.showInfo(ex);
		}
	}


	// ******************************** Metodos estaticos

	/**
		Ayuda al usuario
		Al ser un método publico, puede ser llamado por cualquier otra clase, en particular un sistema de ayuda
		en este caso, abortRun debería ser 'false'
		param abortRun Indica si se debe abortar el programa
		--author El Coyote Cojo
		--version 2003.jul.05 16:45:17, CEST
	*/
	public static String usage(final boolean abortRun) {
		final String msg = "Usage: java tests.Dependencias " +  "";
		System.err.println(msg);
		if(abortRun) {
			System.exit(1);
		}
		return(msg);
	}

	/**
		Ejecuta la aplicación
		--author El Coyote Cojo
		--version 2003.jul.05 16:45:17, CEST
	*/
	public static void main(final String[] args) {
		new Dependencias();
	}
}
