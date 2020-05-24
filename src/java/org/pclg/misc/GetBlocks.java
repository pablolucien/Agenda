// ******************************** package
// 2003.11.29 puesto en un paquete para evitar que sean compilados por ant
// cada vez.
package org.pclg.misc;

// ******************************** imports

import org.pclg.tools.ToolBox;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * GetBlocks <BR> Obtiene los bloques necesarios para reconstruir un archivo
 * segun las diferencias dadas por Diff -dig_comp.
 *
 * @author El Coyote Cojo
 * @version 2003.jun.30 20:23:41, CEST
 */
public final class GetBlocks {
    // ******************************** Variables de clase
    /** The size of the block: 1 Mb. */
    private static final int BLOCK_SIZE = 1024 * 1024;

    // ******************************** Variables de instancia
    /** The buffer to use. */
    private final byte[] buffer = new byte[BLOCK_SIZE];

    // ******************************** Inicializacion estática

    // ******************************** Constructores

    /**
     * Constructor por omision.
     *
     * @param args The arguments to the application.
     * --author El Coyote Cojo
     * --version 2003.jun.30 20:23:41, CEST
     */
    private GetBlocks(final String[] args) {
        final String command = args[0];
        final String filename = args[1];
        final int[] blocks = new int[args.length - 2];
        for (int ii = 0; ii < blocks.length; ii++) {
            blocks[ii] = Integer.parseInt(args[ii + 2]);
        }

        try {
            if (command.equals("-get")) {
                getAllBlocks(filename, blocks);
            } else if (command.equals("-put")) {
                putAllBlocks(filename, blocks);
            } else {
                throw new RuntimeException(command + " es un comando invalido");
            }
        } catch (final IOException ex) {
           ToolBox.showInfo(ex);
        }
    }

    // ******************************** Metodos de instancia
    /**
     * Gets all the blocks of a file.
     * @param filename
     * @param blocks
     * @throws IOException
     */
    private void getAllBlocks(final String filename, final int[] blocks) throws IOException {
        final File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException(filename);
        }

        final RandomAccessFile in = new RandomAccessFile(file, "r");
        for (int ii = 0; ii < blocks.length; ii++) {
            getBlock(in, filename, blocks[ii]);
        }
        in.close();
    }

    /**
     *
     * @param in
     * @param baseFilename
     * @param blockNr
     * @throws IOException
     */
    private void getBlock(final RandomAccessFile in, final String baseFilename,
            final int blockNr) throws IOException {
        in.seek(blockNr * BLOCK_SIZE);
        final int count = in.read(buffer);
        if (count <= 0) {
            throw new EOFException(baseFilename + " blockNr = " + blockNr);
        }
        final BufferedOutputStream blockOut = new BufferedOutputStream(
                new FileOutputStream(baseFilename + "--" + blockNr));
        blockOut.write(buffer, 0, count);
        blockOut.close();
    }

    /**
     *
     * @param filename
     * @param blocks
     * @throws IOException
     */
    private void putAllBlocks(final String filename, final int[] blocks)
            throws IOException {
        final File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException(filename);
        }

        final RandomAccessFile out = new RandomAccessFile(file, "rw");
        for (int ii = 0; ii < blocks.length; ii++) {
            putBlock(out, filename, blocks[ii]);
        }
        out.close();
    }

    /**
     *
     * @param out
     * @param baseFilename
     * @param blockNr
     * @throws IOException
     */
    private void putBlock(final RandomAccessFile out, final String baseFilename,
            final int blockNr) throws IOException {
        final BufferedInputStream blockIn = new BufferedInputStream(
                new FileInputStream(baseFilename + "--" + blockNr));
        final int count = blockIn.read(buffer);
        blockIn.close();
        if (count <= 0) {
            throw new EOFException(baseFilename + " blockNr = " + blockNr);
        }
        out.seek(blockNr * BLOCK_SIZE);
        out.write(buffer, 0, count);
    }


    // ******************************** Metodos estaticos

    /**
     * Ayuda al usuario Al ser un método publico, puede ser llamado por
     * cualquier otra clase, en particular un sistema de ayuda en este caso,
     * abortRun debería ser 'false'.
     *
     * @param args Los parametros que se pasaron al programa.
     * @param abortRun Indica si se debe abortar el programa.
     *
     * --author El Coyote Cojo
     * --version 2003.jun.30 20:23:41, CEST
     */
    private static String usage(final String[] args, final boolean abortRun) {
        final String msg = "Usage: java GetBlocks "
                + "-get|-put <filename> <blockNr_1> <blockNr_2> {...} <blockNr_n>";
        System.err.println(msg);
        if (abortRun) {
            System.exit(1);
        }
        return msg;
    }

    /**
     * Ejecuta la aplicación.
     *
     * @param args The arguments to the application.
     * --author El Coyote Cojo
     * --version 2003.jun.30 20:23:41, CEST
     */
    public static void main(final String[] args) {
        if (args.length < 3) {
            usage(args, true);
        }
        new GetBlocks(args);
    }
}
