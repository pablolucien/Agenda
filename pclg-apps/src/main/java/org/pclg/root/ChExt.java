package org.pclg.root;

import org.pclg.tools.Dir;

import java.io.File;
import java.io.IOException;
import java.util.List;

/*
*/
public class ChExt {
	private static void usage() {
			System.err.println("Indicar el directorio a verificar y las opciones");
			System.exit(1);
	}

	public static void main(final String [] args) throws IOException {
		System.err.println("Mucho prometer antes de meter\ny despues de metido nada de lo prometido");

		boolean checkInvalidChars = true;
		boolean checkEmptyDirs = true;
		boolean checkEmptyFiles = true;
		boolean checkNoImages = true;

		int argNr = 0;			// la posicion del argumento que representa el directorio a analizar

		if(args.length == 0) {
			usage();
		}
		else if(args.length == 1) {
			// opciones estandard
			System.err.println("Usando opciones estandard");
		}
		else if(args.length == 2) {
			// usar opciones de usuario
			System.err.println("Usando opciones de usuario");
			checkInvalidChars = false;
			checkEmptyDirs = true;
			checkEmptyFiles = true;
			checkNoImages = false;
			argNr++;
		}
		else {
			usage();
		}

		final List<File> files = new Dir(Dir.NULL_EXCLUDE_LIST).listarArchivos(
			new File(args[argNr]), true, false);
		final char[] invalidChars = {' ', '!', '%'};
//		String invalidChars = "[ !%]";

		for (final File file : files) {
			final String name = file.getName().toLowerCase();

			// Ver los caracteres invalidos
			if (checkInvalidChars) {
				for (final char invalidChar : invalidChars) {
					if (name.indexOf(invalidChar) >= 0) {
						System.out.println("Caracteres inv�lidos " + file.getAbsolutePath());
						break;
					}
				}
//				if(name.matches(invalidChars)) {
//					System.out.println("Caracteres inv�lidos " + files[i].getAbsolutePath());
//					continue;
//				}
			}

			if (checkEmptyDirs) {
				if (file.isDirectory()) {
					if (file.list().length == 0) {
						System.out.println("Directorio vac�o " + file.getAbsolutePath());
					}
					continue;
				}
			}

			if (checkEmptyFiles) {
				if (file.length() == 0) {
					System.out.println("Archivo vac�o " + file.getAbsolutePath());
					continue;
				}
			}

			if (checkNoImages) {
				if (file.isFile()) {
					if (!name.endsWith(".jpg") && !name.endsWith(".gif")) {
						System.out.println(file.getAbsolutePath());
					}
				}
			}
		}
	}
}
