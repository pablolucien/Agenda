// ******************************** package

// ******************************** imports
import org.pclg.tools.FileTools;
import org.pclg.tools.ToolBox;

import java.io.File;
import java.io.IOException;

/**
	NoURL <BR>
	Elimina los intentos automáticos de abrir un sitio web que hacen algunos mpg, etc.
	@author El Coyote Cojo
	@version 2003.ene.26 17:53:00, CET
*/
public class NoURL {
	// ******************************** Variables de clase

	// ******************************** Variables de instancia

	// ******************************** Constructores

	/**
		Constructor por omision
		-author El Coyote Cojo
		-version 2003.ene.26 17:53:00, CET
	*/
	private NoURL() {
		System.err.println("Que hacer si algo falla? Eso nunca lo sabremos");
		final File dir = new File(".");

		final File destDir = new File(dir, "procesados");
		destDir.mkdir();

		final File[] files = dir.listFiles();

		try {
			for(int ii = 0; ii < files.length; ii++) {
				if(!files[ii].isFile()) {
					continue;
				}
				System.err.print(files[ii]);
				final byte[] b = FileTools.readFromFile(files[ii].getName());
				if(findAndDeleteURL(b)) {
					FileTools.writeToFile(b, destDir.getName() + File.separator + files[ii].getName(), false);
					files[ii].delete();
					System.err.println("\t\tprocesado ");
				}
				else {
					System.err.println("\t\tno ");
				}
			}
		}
		catch(final IOException ex) {
			ToolBox.showInfo(ex);
		}
	}

	// ******************************** Metodos de instancia
	/**
		@return true si hizo algún cambio
		El cambio (por lo que he visto) es siempre en la posicion 381
	*/
	private boolean findAndDeleteURL(final byte[] b) {
//optimizar con try (Seguramente es inutil, porque el tiempo se debe perder en la lectura del archivo y no aqui,
//a menos que haga la verificacion hasta el final del arreglo
		//for(int ii = 0; ii < b.length - 6; ii++) {
		try {
			int ii = -1;
			while(true) {
			ii++;
			if(b[ii + 0] == 0x00 && b[ii + 1] == 0x55			// U
				&& b[ii + 2] == 0x00 && b[ii + 3] == 0x52		// R
				&& b[ii + 4] == 0x00 && b[ii + 5] == 0x4c) {	// L
				b[ii + 1] = 0x58;										// X convertir la U a X
				return true;
			}
			}
		}
		catch(final ArrayIndexOutOfBoundsException ex) {
		}
		//}
		return false;
	}


	// ******************************** Metodos estaticos

	/**
		Ayuda al usuario
		-author El Coyote Cojo
		-version 2003.ene.26 17:53:00, CET
	*/
	public static void usage(final String[] args) {
		System.err.println("Usage: java NoURL " +  "");
		System.exit(1);
	}

	/**
		Ejecuta la aplicación
		-author El Coyote Cojo
		-version 2003.ene.26 17:53:00, CET
	*/
	public static void main(final String[] args) {
		new NoURL();
	}
}
