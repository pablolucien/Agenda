package org.pclg.root;

import org.pclg.tools.FileTools;

import java.io.IOException;

/**
 * Dw.java
 * Se trae un archivo (o un conjunto de ellos) dado su URL.
 * 971028
 * 980806
 * 010207 Clases internas, cambios de nombres
 * Este dedito es el que decide que es lo que se va a bajar
 */
public final class Dw {
	public static void main(final String [] args) throws IOException {
		if (args.length == 0) {
			System.err.println("Uso: java Dw -help | -f <file> | <URL>");
			System.exit(1);
		} else if (args[0].equals("-help")) {
			FileTools.catFile("/org/pclg/root/Dw.help", System.err);
			System.exit(1);
		}

		new org.pclg.net.Dw(args);
	}
}