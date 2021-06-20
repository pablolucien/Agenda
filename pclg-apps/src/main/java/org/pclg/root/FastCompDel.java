package org.pclg.root;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Afterburner de CompDel.
 * @author EL Coyote Cojo.
 * @since 2009 08 06. Aniversario del combardeo de Hiroshima.
 */
public class FastCompDel {
    private static final String IN_FILE = "D:/plucien/____hacer app que "
            + "compare y borre en el ord ppal__compdel20090802.txt";
    private static final String IN_DIRS =
            "D:/plucien/__________Dirs borrados__compdel20090802.txt";

    public static void main(final String[] args) throws IOException {
        cleanFiles();
        cleanDirs();
    }

    private static void cleanDirs() throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(IN_DIRS));
        final int index = "\tBorrado ".length();
        String line;
        while ((line = reader.readLine()) != null) {
            final File dir = new File(line.substring(index));
            if (!dir.isDirectory()) {
                throw new RuntimeException(dir + " NO ES UN DIRECTORIO");
            }
            System.out.println("BORRANDO " + dir);
            if (!dir.delete()) {
                System.out.println("NO PUDE BORRAR " + dir);
            }
        }
        reader.close();
    }

    private static void cleanFiles() throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(IN_FILE));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("-----  ")) {
                continue;
            }
            final String[] files = line.split(" == ");
            if (files.length != 2) {
                throw new RuntimeException("Algo mu malo en la l�nea: " + line);
            } else {
                final String[] files2 = files[0].split("\\d*\tBorrado par Co�o -> ");
                //System.out.println("files = " + files[0]);
                //System.out.println("files = " + files2[0]);
                if (files2.length != 2) {
                    // Ojo con los NO PUDE BORRAR
                    throw new RuntimeException("Algo mu malo en la l�nea[2]: " + files[0]);
                }
                System.out.println("files = " + files2[1]);
                System.out.println("files = " + files[1]);
                final File f1 = new File(files2[1]);
                final File f2 = new File(files[1]);
                checkExists(f1);
                checkExists(f2);
                if (f1.equals(f2)) {
                    System.out.println(f1  + " Y " + f2 + " SON EL MISMO ARCHIVO");
                } else if (identic(f1, f2)) {
                    System.out.println(f1  + " Y " + f2 + " SON IGUALES. PROCEDO A BORRAR " + f1);
                    if (!f1.delete()) {
                        System.out.println("NO PUDE BORRAR " + f1);
                    }
                }
                System.out.println();
            }
        }

        reader.close();
    }

    private static boolean identic(final File f1, final File f2) {
        return false;
    }

    private static void checkExists(final File f1) {
        if (!f1.exists()) {
            System.out.println(f1  + " NO EXISTE");
            System.exit(1);
        }
    }
}
