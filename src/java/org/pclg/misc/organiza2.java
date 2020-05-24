// ******************************** package
// 2003.11.29 puesto en un paquete para evitar que sean compilados por ant cada vez.
package org.pclg.misc;


// ******************************** imports
import org.pclg.tools.ToolBox;

import java.io.File;

/**
	organiza2
	Cambia los nombres de archivos sumandole un cierto valor a los digitos iniciales
	(para los CD 2, 3, etc)
	@author Der hinkender Heulwolf (Präriewolf)
	@version 2001.ago.12 14:27:50, CEST
*/
public class organiza2 {
	// ******************************** Variables de clase
	
	// ******************************** Variables de instancia
	
	// ******************************** Constructores
	
	/**
		Constructor por omision
		--version 2003.01.22 (Cumpleaños de Meredy)
	*/
	private organiza2(final String dirName, final int baseNr) {
		final File dir = new File(dirName);
		final File[] files = dir.listFiles();

		// Para el formato de la salida
		int maxFileNameLen = 0;
		for(int i = 0; i < files.length; i++) {
			final String name = files[i].getName();
			if(name.length() > maxFileNameLen) {
				maxFileNameLen = name.length();
			}
		}

		for(int i = 0; i < files.length; i++) {
			if(!files[i].isFile()) {
				continue;
			}
			final String name = files[i].getName();
			int nr = Integer.parseInt(name.substring(0, 2));
			nr += baseNr;
			final File newFile = new File(dir,
                ToolBox.leftPad(String.valueOf(nr), 2, '0') + name.substring(2));

			System.out.print(new StringBuilder().append(name)
                .append(ToolBox.leftPad(" ", maxFileNameLen - name.length() + 3, '>'))
                .append(newFile.getName())
                .append(ToolBox.leftPad(" ", maxFileNameLen - name.length() + 3, '.'))
                .toString());

			if(files[i].renameTo(newFile)) {
				System.out.println(" Chachi piruli");
			}
			else {
				System.out.println(" No pude");
			}
		}
	}

	// ******************************** Metodos de instancia
	
	// ******************************** Metodos estaticos
	
	/**
		Ayuda al usuario
		--version 2001.ago.12 14:27:50, CEST
	*/
	private static void usage() {
		System.out.println("Usage: java organiza2 " +  " <nombre dir> <nro a sumar>");
		System.exit(0);
	}
	
	/**
		Ejecuta la aplicación
		--version 2001.ago.12 14:27:50, CEST
	*/
	public static void main(final String [] args) {
		if(args.length != 2) {
			usage();
		}
		
		new organiza2(args[0], Integer.parseInt(args[1]));
	}
}
