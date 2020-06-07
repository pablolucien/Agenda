package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * PkgOrganizer: crea la estructura de directorios y mueve los archivos segun lo
 * que diga la clausula 'package'.
 *
 * @author El Coyote Cojo.
 * @version 20010522.
 */
public final class PkgOrganizer {
    /**
     * Default constructor.
     */
    private PkgOrganizer() {
        try {
            final File dir = new File(".");
            final File[] files = dir.listFiles();
            BufferedReader in;
            outer:	for (int ii = 0; ii < files.length; ii++) {
                final String name = files[ii].getName();
                if (!name.endsWith(".java")) {
                    continue;
                }
                System.out.println(name.substring(0, name.length() - 5));
                in = new BufferedReader(new InputStreamReader(new FileInputStream(name)));
                String line;
                final String pack;
                while ((line = in.readLine()) != null) {
                    if (line.indexOf("package ") == 0) {
                        in.close();
                        pack = line.substring(8, line.length() - 1).replace('.', '/');
                        final File newDir = new File(pack);
                        if (newDir.mkdirs()) {
                            System.out.println("Creando " + pack);
                        }
                        if (newDir.exists() && newDir.isDirectory()) {
                            if (files[ii].renameTo(new File(newDir, name))) {
                                System.out.println("Movido " + name + " a " + pack);
                            } else {
                                System.out.println("No pude mover " + name + " a " + pack);
                            }
                        }
                        continue outer;
                        //System.exit(0);
                    }
                }
                in.close();
            }
        } catch (final FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Entry point of the application (for testing).
     * @param args paramaters to the application .
     */
    public static void main(final String[] args) {
        new PkgOrganizer();
    }
}
