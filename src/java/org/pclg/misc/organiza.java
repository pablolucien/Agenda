// ******************************** package
// 2003.11.29 puesto en un paquete para evitar que sean compilados por ant cada vez.
package org.pclg.misc;


// ******************************** imports

import java.io.File;

/**
 * organiza organiza archivos cuyo nombre sea de la forma (nombre de dir)nombre
 * de archivo.
 *
 * @author El coyote Cojo
 * @version 2001.ago.12 14:27:50, CEST
 */
public final class organiza {
    // ******************************** Variables de clase

    // ******************************** Variables de instancia

    // ******************************** Constructores

    /**
     * Constructor por omision.
     * --version 2001.ago.12 14:27:50, CEST
     */
    private organiza() {
        final File[] files = new File(".").listFiles();
        for (int ii = 0; ii < files.length; ii++) {
            if (!files[ii].isFile()) {
                continue;
            }
            String name = files[ii].getName();
            if (name.indexOf(')') == -1) {
                continue;
            }
            final String dir = name.substring(1, name.indexOf(')'));
            name = name.substring(name.indexOf(')') + 1).trim();
            System.out.print(dir + " " + name + " ... ");
            final File newDir = new File(dir);
            newDir.mkdir();
            if (files[ii].renameTo(new File(newDir, name))) {
                System.out.println(" Chachi piruli");
            } else {
                System.out.println(" No pude");
            }
        }
    }


    // ******************************** Metodos de instancia

    // ******************************** Metodos estaticos

    /**
     * Ayuda al usuario.
     * --version 2001.ago.12 14:27:50, CEST
     */
    public static void usage() {
        System.out.println("Usage: java organiza " + "");
        System.exit(0);
    }

    /**
     * Ejecuta la aplicación.
     * @param
     * --version 2001.ago.12 14:27:50, CEST
     */
    public static void main(final String[] args) {
        new organiza();
    }
}
