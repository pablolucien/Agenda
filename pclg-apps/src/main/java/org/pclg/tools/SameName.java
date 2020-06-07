package org.pclg.tools; /**
 */

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Busca archivos con el mismo nombre.
 */
public class SameName {
	/**
	 * @param args [0] es el directorio base para la comparacion.
	 * @throws IOException si hay problemas.
	 */
    public static void main(final String[] args) throws IOException {
        int i, j;

        // dir es el directorio base para la comparacion.
        final String dir = args.length == 0 ? "." : args[0];

        // En un arreglo de String ponemos todos los archivo del arbol con su path
        System.err.println("A pedir el dir: " + new java.util.Date());
        final List<File> files;

        try {
            files = (new Dir(Dir.NULL_EXCLUDE_LIST)).listarArchivos(new File(dir), true, false);
        }
        catch (final NotADirException ex) {
            System.err.println("Si pasas el nombre de un directorio es mas de pinga");
            return;
        }

        System.err.println("Comparando NOMBRES de archivos en: " + (new File(dir).getAbsolutePath()));
        System.err.println("Comenzando: " + new java.util.Date());

        // Comparamos cada archivo con el siguiente: si coinciden escribimos
        // un mensaje

        final Chismoso out = new Chismoso();

        out.dile("Verificar no borrar un archivo totalmente si est  repetido");
        out.dile(" m s de una vez. ¨Hacer un programa que chequee eso y los borre");
        out.dile(" preguntando a usuario (yo)");
        out.dile();
        out.dile();
        out.dile("Estos archivos coinciden en NOMBRE");
        out.dile();

        String nombre1;
		String nombre2;

		final int length = files.size();
        for (i = 0; i < length; i++) {
            if (isIgnorable(files.get(i))) {
                continue;
            }
            for (j = i + 1; j < length; j++) {
                if (isIgnorable(files.get(j))) {
                    continue;
                }
                nombre1 = files.get(i).getName();
                nombre2 = files.get(j).getName();
                if (nombre1.equalsIgnoreCase(nombre2)) {
                    out.dile(System.out, "Son iguales\n\t1 " + files.get(i) + "\n\t2 " + files.get(j));
                }
            }
        }
        System.err.println("Fin de la comparacion: " + new java.util.Date());
    }

    private static boolean isIgnorable(final File file) {
        final String name = file.getAbsolutePath();
        return !file.isFile() || name.endsWith(".class")
			|| name.contains("proguard4.7")
				|| name.contains("\\CVS\\")
//				|| name.contains("\\src_other\\")
				|| name.contains("\\clarion\\")
				;
    }
}
