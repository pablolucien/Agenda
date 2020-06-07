package misc.sopra.eci;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/*
 * We should forget about small efficiencies, say about 97% of the time: 
 * Premature optimization is the root of all evil. — Donald Knuth
 * 
 * Creado el 06-Aug-2008
 */

/**
 * @author Un autor en busca de personajes.
 * @since 06-Aug-2008
 */
final class MaestroFileConverter {
    private static final int BUFFER_SIZE = 10 * 1024;

    /**
     * 
     * @param name name of the file to process.
     * @throws IOException if something goes south with the I/O.
     */
    private static void processFileNIO(final String name) throws IOException {
	    final File inFile = new File(name);
	    final File outFile = File.createTempFile("kkk", ".tmp",
	            inFile.getParentFile());
	    boolean processOk = false;
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try {
        	fin = new FileInputStream(inFile);
        	fout = new FileOutputStream(outFile);
            final FileChannel fcIn = fin.getChannel();
            final FileChannel fcOut = fout.getChannel();
            final ByteBuffer bufferIn = ByteBuffer.allocateDirect(BUFFER_SIZE);
            final ByteBuffer bufferOut = ByteBuffer.allocateDirect(BUFFER_SIZE);
            int bytesRead;
            while ((bytesRead = fcIn.read(bufferIn)) > 0) {
				bufferIn.flip();
				bufferOut.clear();
				for (int ii = 0; ii < bytesRead; ii++) {
                    bufferOut.put(convertChar(bufferIn.get()));
                }
				bufferIn.clear();
				bufferOut.flip();
				fcOut.write(bufferOut);
            }
            processOk = true;
        } finally {
            if (fin != null) {
                fin.close();
            }
            if (fout != null) {
                fout.close();
            }
            if (processOk) {
	            System.out.println("Eliminado el antiguo: " + inFile.delete());
	            System.out.println("Renombrado el nuevo: " + outFile.renameTo(inFile));
            }
        }
    }
    

    /**
     * Convierte un caracter de la codificacion de Maestro.
     * @param c el caracter a convertir.
     * @return  el caracter convertido.
     */
    private static byte convertChar(final int c) {
        final byte k;
        switch (c) {
        case -60:	// Ä
        case -51:	// Í
            k = (byte) '-';
            break;
        case -77:	// ³
            k = (byte) '|';
            break;
        case -38:	// Ú
        case -39:	// Ù
        case -40:	// Ø
        case -43:	// Õ
        case -44:	// Ô
        case -47:	// Ñ
        case -49:	// Ï
        case -58:	// Æ
        case -64:	// À
        case -65:	// ¿
        case -66:	// ¾
        case -72:	// ¸
        case -75:	// µ
            k = (byte) '+';
            break;
        default:
            k = (byte) c;
        }
        return k;
    }

    public static void main(final String[] args) throws IOException {
        for (int ii = 0; ii < args.length; ii++) {
            System.out.println(args[ii]);
            processFileNIO(args[ii]);
        }
    }
}
