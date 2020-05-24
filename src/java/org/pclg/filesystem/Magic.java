package org.pclg.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.Chrono;
import org.pclg.tools.Constants;
import org.pclg.tools.FileComparator;
import org.pclg.tools.FileTools;
import org.pclg.tools.ToolBox;

/**
 * Magic <BR> Emula el comando file(1) de Unix.
 *
 * @author El Coyote Cojo
 * @version 2003.abr.30 22:52:47, CEST
 * @version 2003.jun.20 Modificado para que usar el archivo Magic.magic
 * @version 2003.jun.28 Modificado para que sea compatible con el archivo magic
 *          de file(1). Con suerte, sólo habrá que modificar a org.pclg.filesystem.MagicEntry
 */
public final class Magic {
	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.make();

	/** Las definiciones del archivo magic o Magic.magic. */
	private final MagicEntry[] magicEntries;

	/** Solo reporta los archivos desconocidos. */
	private final boolean onlyUnknown;

	/** La magia de un archivo puede coincidir con varios tipos:
     * esta variable indica que ya ha coincidido con uno
     * (a efectos de reporte).
     */
	private boolean alreadyFound;

	// ******************************** Constructores

	public Magic(final boolean onlyUnknown, final boolean debug) {
		this.onlyUnknown = onlyUnknown;
		magicEntries = readMagicFile(debug);
	}

    /**
     * --author El Coyote Cojo
     * @since 2003.abr.30 22:52:47, CEST
	 * @param dirList Los directorios cuyos archivos vamos a chequear.
	 * @param recurse Indica si se debe actuar recursivamente.
	 * @param onlyUnknown Indica si sólo se muestran archivos desconocidos.
	 * @param debug Da más información para facilitarme la vida.
	 * @throws IOException si algo va mal.
	 */
    public Magic(final String[] dirList, final boolean recurse,
			final boolean onlyUnknown, final boolean debug) throws IOException {
		this(onlyUnknown, debug);
		//final ConfigurableComparator<File> fileComparator = new FileComparator();
		final FileComparator fileComparator = new FileComparator();

        for (final String dirName : dirList) {
            final File dir = new File(dirName);
            if (!dir.exists()) {
                outMessage('<' + dirName + "> no existe.");
                return;
            }

            if (dir.isDirectory()) {
                outMessage("====================");
                outMessage("Procesando directorio " + dir);
                final File[] files = dir.listFiles();
                if (files != null) {
                    Arrays.sort(files, fileComparator);
                    for (final File file : files) {
                        showMagic(file, recurse);
                    }
                }
            } else if (dir.isFile()) {
                outMessage("====================");
                outMessage("Procesando archivo " + dir);
                showMagic(dir, false);
            } else {
                outMessage('<' + dirName + "> no es un archivo o directorio válido");
            }
        }
    }

	/**
     * Busca la magia en un archivo
	 * @param recurse Indica si se debe actuar recursivamente.
     */
    private void showMagic(final File file, final boolean recurse) throws IOException {
        alreadyFound = false;
        if (file.isDirectory()) {
            if (!onlyUnknown) {
                reportResult(file, "Directorio");
            }
            if (recurse) {
                reportResult(file, "-- Procesando su contenido -- ");
                File[] files = file.listFiles();
                if (files != null) {
                    for (final File child : files) {
                        showMagic(child, recurse);
                    }
                }
            }
            return;
        } else if (file.length() == 0) {
            if (!onlyUnknown) {
                reportResult(file, "Archivo vacio");
            }
            return;
        } else if (!file.isFile()) {
            reportResult(file, "No es un archivo");
            return;
        }

        // Gracias a la forma de hacer el sort, los directorios ya fueron procesados, ergo puedo hacer
        // aqui una separación
        if (!onlyUnknown) {
            outMessage("");
        }

		getMagic(file);

		// Si llegamos aqui con alreadyFound == false significa que no lo hemos reconocido
        // ¿Somos lo suficientemente habiles para intuir que tipo de archivo es? ...
        if (!alreadyFound) {
            alreadyFound |= checkTextFile(file);
        }

        if (!alreadyFound) {		// ... pues parece que no.
            reportResult(file, "Desconocido: " + showHeader(file));
        }
    }

	public List<String> getMagic(final File file) throws IOException {
		final List<String> magicMessages = new ArrayList<>(3);
		for (final MagicEntry magicEntry : magicEntries) {
			final boolean thisMatches = magicEntry.checkMagic(file);
			if (thisMatches && !onlyUnknown) {
				final String msg = magicEntry.getMsg();
				magicMessages.add(msg);
				reportResult(file, msg);
			}
			alreadyFound |= thisMatches;
		}
		return magicMessages;
	}


	/**
     * Trata de ver si el archivo es un archivo de texto
     *
     * @since 2003.06.23
     */
    private boolean checkTextFile(final File targetFile) {
        try (RandomAccessFile fis = new RandomAccessFile(targetFile, "r")) {
            final byte[] magic = new byte[Constants.BUFFER_SIZE];

            fis.seek(0);

            final int count = fis.read(magic);
            final String chunk = new String(magic, 0, count);
            outMessage("Leídos " + String.valueOf(count) + " caracteres.");
            for (int ii = 0; ii < count; ii++) {
                final char character = chunk.charAt(ii);
                if (!Character.isLetterOrDigit(character)
                        && !Character.isWhitespace(character)
                        && !isPunctuationChar(character)) {
                    return false;
                }
            }
			// Llegados aqui, es posible que sea solo texto
			/*if(!onlyUnknown) */ reportResult(targetFile, "Parece ser texto");
			return true;
        } catch (final Exception ex) {
            LOGGER.severe("Error processing <" + targetFile + '>');
            ToolBox.showInfo(ex);
		}

        return false;
    }

    /**
     * Comprueba si un caracter es un signo de puntuación.
     *
     * @param character el caracter a comprobar
     * @return true si es puntuación, false de lo contrario
     */
    private boolean isPunctuationChar(final char character) {
        return "/*,.;:@#|\\¡!¿?<>[]{}'()&%$\"_-=+".indexOf(character) >= 0;
    }

    /**
     * Muestra los primeros bytes del archivo
     *
	 * @param targetFile el archivo cuyos bytes queremos ver.
     *
	 * @return los primeros bytes del archivo.
     *
	 * @since 2003.16.20
     */
    private static String showHeader(final File targetFile) {
		RandomAccessFile fis = null;
		try {
            final int HEADER_LEN = 16;
            final StringBuilder magicString = new StringBuilder(HEADER_LEN);

            fis = new RandomAccessFile(targetFile, "r");
            final byte[] magic = new byte[HEADER_LEN];
            fis.read(magic);
            for (int ii = 0; ii < HEADER_LEN; ii++) {
                magicString.append(ToolBox.leftPad(Integer.toHexString(magic[ii]
                    & 0x000000ff), 2, '0'))
                    .append(ii < HEADER_LEN - 1 ? "-" : "");
            }
			magicString.append(" - ").append(new String(magic));
            for (int ii = 0; ii < magicString.length(); ii++) {
                if (Character.isISOControl(magicString.charAt(ii))) {
                    magicString.setCharAt(ii, '.');
                }
            }

            return magicString.toString();
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
        } finally{
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException e) {
					LOGGER.log(Level.SEVERE, "Error", e);
				}
			}
		}

        return null;
    }

    private void reportResult(final File file, final String msg) throws IOException {
        final String fileName = file.isDirectory() ? file.getCanonicalPath() : file.getName();
        final String outStr = '<' + fileName + '>';
        if (alreadyFound) {
            outMessage(ToolBox.pad("", outStr.length(), '.') + "  >> " + msg + " <<");
        } else {
            outMessage(outStr + "  >> " + msg + " <<");
        }
    }


    // ******************************** Metodos estaticos
    /**
     * Lee el archivo de la magia de gandalf.
	 * @param debug Indica si estamos en modo debug.
 	 * 
     */
    private MagicEntry[] readMagicFile(final boolean debug) {
        final String MAGIC_FILE = "Magic.magic";
        int lineNr = 0;
        boolean ok = false;
		final MagicEntry[] nullMagicEntry = new MagicEntry[0];
		try (final BufferedReader in = new BufferedReader(new InputStreamReader(
			    ClassLoader.getSystemResourceAsStream(MAGIC_FILE)))) {
            final int cron = Chrono.getChrono();
            Chrono.start(cron);
            final List<MagicEntry> entries = new ArrayList<>(Constants.BUFFER_SIZE);
            String line;
            MagicEntry lastEntry = null;
            while ((line = FileTools.readLine(in)) != null) {
                lineNr++;
                if (line.charAt(0) == '>') {
                    lastEntry.addContinuation(line);
				} else if (line.charAt(0) == '&') {
                    lastEntry.addComplementCondition(line);
                } else {
                    lastEntry = new MagicEntry(line);
                    entries.add(lastEntry);
					if (debug && lastEntry != null) {
						outMessage(lastEntry.toString());
					}
                }
            }
			if (debug) {
	            outMessage("Leidas " + entries.size() + " entradas de " + MAGIC_FILE);
			}
            ok = true;

            Chrono.mark(cron);
			if (debug) {
	            outMessage("readMagicFile(): " + Chrono.timeDetail(Chrono.elapsed(cron)));
			}

            return entries.toArray(nullMagicEntry);
        } catch (final IOException ex) {
			LOGGER.log(Level.SEVERE, "Error", ex);
			return nullMagicEntry;
		} finally {
            if (!ok) {
                errMessage("Error en la linea " + lineNr + " de " + MAGIC_FILE);
            }
		}
    }


    /**
     * Outputs a stdout message.
     * @param msg the maessage.
     */
    private static void outMessage(final String msg) {
        System.out.println(msg);
    }

    /**
     * Outputs a stderr message.
     * @param msg the maessage.
     */
    private static void errMessage(final String msg) {
        System.err.println(msg);
    }
}
