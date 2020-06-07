// ******************************** package
package org.pclg.migrator.clarion;

// ******************************** imports

import java.io.IOException;
import java.io.PrintStream;

/**
 * ClarionKey
 *
 * @author El Coyote Cojo
 * @version 2002.ene.16 20:15:07, CEST
 */
final class ClarionKey {
    private final PrintStream miSalida = System.out;

    /**
     * El numero de componentes de la clave
     */
	private byte numcomps;

    private String keyName;
    private byte comptype;
    private byte complen;

    /**
     * La definicion de la clave
     */
	private byte[] keyBytes;

    public ClarionKey(final ClarionDataInputStream inputStream) throws IOException {
        numcomps = inputStream.readByte();
        final byte[] dummy = new byte[16];
		if (inputStream.read(dummy) != dummy.length) {
			throw new IOException("Unexpected end of Stream");
		}
        keyName = new String(dummy, 0, 16);
        comptype = inputStream.readByte();
        complen = inputStream.readByte();

        // La definicion de la clave la descarto por el momento
        keyBytes = new byte[6 * numcomps];
		if (inputStream.read(keyBytes) != keyBytes.length) {
			throw new IOException("Unexpected end of Stream");
		}
    }

    public void print() {
        miSalida.println("KEY: \n" + numcomps + "	/* number of components for key */");
        miSalida.println("   Nombre: " + keyName);
        miSalida.println("---------------------");
    }
}
