//redirigir las salidas a consolas
// ******************************** package
package org.pclg.beans;

// ******************************** imports

import javax.swing.JPanel;

/**
 * BinaryDisplay <BR>
 * Un Panel que muestra un número en binario: se le pasa el número y se
 * encienden o apagan "LED's".
 *
 * @author El Coyote Cojo
 * @version 2003.mar.12 21:22:42, CET
 */
public class BinaryDisplay extends JPanel {
    private static final long serialVersionUID = 4926832181529880274L;
    // ******************************** Variables de clase

    // ******************************** Variables de instancia

    // ******************************** Constructores

    /**
     * Constructor por omision
     * --author El Coyote Cojo
     * --version 2003.mar.12 21:22:42, CET
     */
	private BinaryDisplay() {
    }

    // ******************************** Metodos de instancia

    // ******************************** Metodos estaticos

    /**
     * Ayuda al usuario
     * --author El Coyote Cojo
     * --version 2003.mar.12 21:22:42, CET
     */
    public static void usage(final String[] args) {
        System.err.println("Usage: java BinaryDisplay " + "");
        System.exit(1);
    }

    /**
     Ejecuta la aplicación
     --author El Coyote Cojo
     --version 2003.mar.12 21:22:42, CET
     */
    public static void main(final String[] args) {
        new BinaryDisplay();
	}
}
