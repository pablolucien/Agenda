package org.pclg.root;

import org.pclg.filesystem.FileSplitter;
import org.pclg.tools.Chrono;
import org.pclg.tools.ToolBox;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Splitter.java
 * Divide un archivo en pedazos mas chicos
 * para poder meterlos en diskettes.
 *
 * @author El Coyote Cojo
 * @version 1.0
 */
@SuppressWarnings({"ClassWithoutPackageStatement"})
public final class Splitter {
    /** Indica si estamos en modo de debug. */
    private static final boolean DEBUG = true;

    /** El tama�o de un archivo para que quepa en un diskette. */
    private static final int DISKETTE_FILE_SIZE = 1457664;

	/** Comilla doble. */
	private static final char QUOTE = '\"';

	/** Prevents instantiation. */
	private Splitter() {
	}

    /**
     * Muestra un mensaje de ayuda.
     */
    private static void usage() {
        emit("Uso: Splitter [-size <n>[b|k|m|g|t] | -{f|F} X:] <archivo(s)>");
        emit("       donde <n> es el tama�o del archivo");
        emit("         (b: bytes, k: kilobytes, m: megabytes, g: gigabytes, t: terabytes)");
        emit("         (por omision bytes)");
        emit("       si no se especifica el parametro -size, se asumen "
                        + DISKETTE_FILE_SIZE + " bytes");
        emit("");
        emit("       -f y -F indican usar como tama�o el espacio");
        emit("          en la unidad X: (-f usa un 10% menos)");
        emit("");
        emit("       <archivo(s)> el nombre del o los archivos a dividir");
        System.exit(1);
    }

	/** Wrapper para System.out.println(). */
	private static void emit(final String msg) {
		System.out.println(msg);
	}

	/**
     * Entry point to the app.
     *
     * @param args the parameters to the app.
     */
    public static void main(final String[] args) {
        if (args.length == 0) {
            usage();
        }

        int firstArg = 0;
        long size = 0;

        if (args[0].equalsIgnoreCase("-size")) {
            try {
                size = ToolBox.parseSize(args[1]);
                if (DEBUG) {
                    emit(MessageFormat.format(
							"Utilizando un tama�o de {0} bytes", size));
                }
                firstArg = 2;
            } catch (final NumberFormatException ex) {
                usage();
            } catch (final IllegalArgumentException ex) {
                usage();
            }
        } else if (args[0].equalsIgnoreCase("-F")) {
            size = new File(args[1]).getUsableSpace();
            if (args[0].equals("-f")) {
                size *= .9;
            }
            firstArg = 2;
            emit(MessageFormat.format(
					"Utilizando el tama�o libre en {1} de {0} bytes",
					size, args[1]));
        }

        if (size <= 0) {
            size = DISKETTE_FILE_SIZE;
            emit(MessageFormat.format(
					"Utilizando el tama�o por omisi�n de {0} bytes",
					DISKETTE_FILE_SIZE));
        }

        final int cronHandleTotal;
        int cronHandleFile;

        if (DEBUG) {
            cronHandleTotal = Chrono.getChrono();
            cronHandleFile = Chrono.getChrono();
        }

        for (int ii = firstArg; ii < args.length; ii++) {
            if (DEBUG) {
                Chrono.start(cronHandleFile);
            }

            splitt(args[ii], size);

            if (DEBUG) {
                Chrono.mark(cronHandleFile);
                emit(MessageFormat.format("Time for {0}: {1}", args[ii],
					Chrono.timeDetail(Chrono.elapsed(cronHandleFile))));
            }
        }
		if (DEBUG) {
			Chrono.mark(cronHandleTotal);
			emit(MessageFormat.format("Total time {0}",
					Chrono.timeDetail(Chrono.elapsed(cronHandleTotal))));
		}
    }

    /**
     * Divide un archivo en pedazos mas chicos.
     *
     * @param fileName  El nombre del archivo que hay que dividir
     * @param splitSize El tama�o de los pedazos resultantes
     */
    private static void splitt(final String fileName, final long splitSize) {
        try {
            final int numberOfChunks = FileSplitter.splittFile(fileName, splitSize);
            switch (numberOfChunks) {
            case FileSplitter.DONT_EXISTS:
                emit(MessageFormat.format("Splitter: <{0}> no existe. "
						+ "Verifique el nombre del archivo", fileName));
                break;
            case FileSplitter.IS_DIR:
                emit(MessageFormat.format("Splitter: <{0}> es un directorio: "
						+ "no lo puedo dividir.", fileName));
                break;
            case FileSplitter.SMALL_FILE:
                emit(MessageFormat.format("Splitter: <{0}> es muy chica. "
						+ "No vale la pena el esfuerzo", fileName));
                break;
            default:
                informacionUtilParaElUsuarioInexperto(fileName, numberOfChunks);
                break;
            }
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
        }
    }


    private static void informacionUtilParaElUsuarioInexperto(
            final String fileName, final int numberOfChunks) {
		final int padLen = (int) (Math.log10(numberOfChunks) + 1); 
        emit("REM ** el comando que reconstruye el archivo es:");
        final StringBuilder command = new StringBuilder("copy /b ");
        for (int ii = 0; ii <= numberOfChunks; ii++) {
            command.append(QUOTE).append(fileName).append('.')
					.append(ToolBox.leftPad(String.valueOf(ii), padLen, '0'))
					.append(QUOTE).append(ii == numberOfChunks ? " " : " + ");
        }
        command.append(QUOTE).append(fileName).append(QUOTE);
        emit(command.toString());
    }
}
