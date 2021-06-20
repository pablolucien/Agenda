package org.pclg.root;// ******************************** package

// ******************************** imports

import org.pclg.tools.ToolBox;

import java.io.DataInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * KazaaAppendix <BR> Lee la informacion de los archivos dat de KaZaA.
 *
 * @author El Coyote Cojo
 * @version 2003.may.17 17:49:40, CEST
 */
public final class KazaaAppendix {
    // ******************************** Variables de clase
    private static final byte[] KAZAA_MAGIC = {
        (byte) 'K', (byte) 'A', (byte) 'Z', (byte) 'A'
    };

    /**
     * 10 bytes desde el final del archivo
     */
    private static final long MAGIC_POS = 10;


    // ******************************** Variables de instancia

    // ******************************** Constructores

    /**
     * Constructor por omision
     *
     * -author El Coyote Cojo
     * -version 2003.may.17 17:49:40, CEST
     */
    private KazaaAppendix(final File targetFile) {
        if (targetFile.isDirectory()) {
            final File[] files = targetFile.listFiles();
            for (int ii = 0; ii < files.length; ii++) {
                readAppendix(files[ii]);
                System.out.println("===========================================");
            }
        } else {
            final byte[] appendix = readAppendix(targetFile);
        }
    }

    // ******************************** Metodos de instancia
    private static byte[] readAppendix(final File targetFile) {
        try {
            final RandomAccessFile fis = new RandomAccessFile(targetFile, "r");
            final byte[] magic = new byte[KAZAA_MAGIC.length];
            final long len = targetFile.length();
            final long offset = len - MAGIC_POS;
            fis.seek(offset);
            fis.read(magic);
            if (!Arrays.equals(magic, KAZAA_MAGIC)) {
                System.out.println(targetFile.getName() + " NO es de KaZaA");
                fis.close();
                return null;
            }

//System.out.println(targetFile.getName() + " es de KaZaA");

            // Obtenemos el tama�o del sector de info ...
            final short appendixLen = readLEShort(fis);
            System.out.println(appendixLen);

            // ... vamos al principio del mismo ...
            fis.seek(len - appendixLen - MAGIC_POS);

            // ... y comenzamos a leer.

            final int datState = readLEInt(fis);
            System.out.println("datState: " + datState);

            final int datSourceCount = readLEInt(fis);
            System.out.println("datSourceCount: " + datSourceCount);

            for (int ii = 0; ii < datSourceCount; ii++) {
                final String srcRemoteFileName = readANSIZString(fis);
                System.out.println(srcRemoteFileName);
                final String srcDownloadURL = readANSIZString(fis);
                System.out.println(srcDownloadURL);


                final int srcFileID = readLEInt(fis);
                System.out.println(srcFileID);

// leer otras cosas
                final byte[] basura1 = new byte[44];
                fis.read(basura1);

                final byte[] srcName = new byte[69];

                for (int jj = 1; jj < srcName.length; jj++) {
                    if (srcName[jj - 1] == 0) {
                        srcName[jj] = 0;
                    }
                }
                System.out.println(new String(srcName));

                final byte[] basura2 = new byte[26];
                fis.read(basura2);

            }

// volver a chequear que estamos en la KAZAA_MAGIC

            fis.close();
            return null;
        } catch (final FileNotFoundException ex) {
            ToolBox.showInfo(ex);
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
        }
        return null;
    }

    private static short readLEShort(final DataInput in) throws IOException {
        final byte[] bytes = new byte[2];
        in.readFully(bytes);
        return (short) (bytes[0] + 256 * bytes[1]);
    }

    private static int readLEInt(final DataInput in) throws IOException {
        final byte[] bytes = new byte[4];
        in.readFully(bytes);
        return bytes[0] + 256 * bytes[1] + 256 * 256 * bytes[2] + 256 * 256 * 256 * bytes[3];	// ??? comprobar
    }

    private static String readANSIZString(final DataInput in) throws IOException {
        final byte[] buffer = new byte[256];
        int count = 0;
        byte bb;
        do {
            bb = in.readByte();
            buffer[count] = bb;
            count++;
        } while (bb != 0);
        return new String(buffer, 0, count);
    }

    // ******************************** Metodos estaticos

    /**
     * Ayuda al usuario Al ser un m�todo publico, puede ser llamado por
     * cualquier otra clase, en particular un sistema de ayuda
     *
     * -author El Coyote Cojo
     * -version 2003.may.17 17:49:40, CEST
     */
    private static void usage(final String[] args) {
        System.err.println("Usage: java KazaaAppendix " + "<filename|dirname>");
        System.exit(1);
    }

    /**
     * Ejecuta la aplicaci�n
     *
     * -author El Coyote Cojo
     * -version 2003.may.17 17:49:40, CEST
     */
    public static void main(final String[] args) {
        if (args.length != 1) {
            usage(args);
        }

        final File targetFile = new File(args[0]);
        new KazaaAppendix(targetFile);
    }
}
