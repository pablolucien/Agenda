package org.pclg.filesystem;

import org.pclg.log.LoggerFactory;
import org.pclg.tools.Chrono;
import org.pclg.tools.Constants;
import org.pclg.tools.FileComparator;
import org.pclg.tools.FileTools;
import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Magic <BR> Emula el comando file(1) de Unix.
 *
 * @author El Coyote Cojo
 * @version 2003.abr.30 22:52:47, CEST
 * @version 2003.jun.20 Modificado para que usar el archivo Magic.magic
 * @version 2003.jun.28 Modificado para que sea compatible con el archivo magic
 * de file(1). Con suerte, s�lo habr� que modificar a org.pclg.filesystem.MagicEntry
 */
public final class Magic {
	/** Logger. */
    private static final Logger LOGGER = LoggerFactory.make();

	/** Las definiciones del archivo magic o Magic.magic. */
    private final MagicEntry[] magicEntries;

	/** Solo reporta los archivos desconocidos. */
    private final boolean onlyUnknown;

    // ******************************** Constructores

    public Magic(final boolean onlyUnknown, final boolean debug) {
        this.onlyUnknown = onlyUnknown;
        magicEntries = readMagicFile(debug);
    }

    /**
     * --author El Coyote Cojo
     *
     * @param dirList     Los directorios cuyos archivos vamos a chequear.
     * @param recurse     Indica si se debe actuar recursivamente.
     * @param onlyUnknown Indica si s�lo se muestran archivos desconocidos.
     * @param debug       Da m�s informaci�n para facilitarme la vida.
     * @throws IOException si algo va mal.
     * @since 2003.abr.30 22:52:47, CEST
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
                outMessage("**>>====================<<**");
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
                outMessage('<' + dirName + "> no es un archivo o directorio v�lido");
            }
        }
    }

    /**
     * Busca la magia en un archivo
     *
     * @param recurse Indica si se debe actuar recursivamente.
     */
    private void showMagic(final File file, final boolean recurse) throws IOException {
        if (file.isDirectory()) {
            if (!onlyUnknown) {
                reportResult(file, "Directorio", false);
            }
            if (recurse) {
                reportResult(file, "-- Procesando su contenido -- ", false);
                final File[] files = file.listFiles();
                if (files != null) {
                    for (final File child : files) {
                        showMagic(child, recurse);
                    }
                }
            }
            return;
        } else if (file.length() == 0) {
            if (!onlyUnknown) {
                reportResult(file, "Archivo vacio", false);
            }
            return;
        } else if (!file.isFile()) {
            reportResult(file, "No es un archivo", false);
            return;
        }

        // Gracias a la forma de hacer el sort, los directorios ya fueron procesados, ergo puedo hacer
        // aqui una separaci�n
        if (!onlyUnknown) {
            outMessage("");
        }

        final List<String> magicMessages = getMagics4File(file);
        reportFoundMagic(file, magicMessages);

        // Si llegamos aqui con alreadyFound == false significa que no lo hemos reconocido
        // �Somos lo suficientemente habiles para intuir que tipo de archivo es? ...
        boolean alreadyFound = magicMessages.size() > 0;
        if (!alreadyFound) {
            alreadyFound = checkTextFile(file);
        }

        if (!alreadyFound) {        // ... pues parece que no.
            reportResult(file, "Desconocido: " + showHeader(file), alreadyFound);
        }
    }

    public List<String> getMagics4File(final File file) {
        final List<String> magicMessages = new ArrayList<>(3);
        for (final MagicEntry magicEntry : magicEntries) {
            if (magicEntry.checkMagic(file)) {
                magicMessages.add(magicEntry.getMsg());
            }
        }
        return magicMessages;
    }

    private void reportFoundMagic(final File file, final List<String> magicMessages) throws IOException {
        boolean alreadyFound = false;
        for (final String magicMessage : magicMessages) {
            if (!onlyUnknown || !isValidExtension(file)) {
                reportResult(file, magicMessage, alreadyFound);
            }
            alreadyFound = true;
        }
    }

    /**
     * Trata de ver si el archivo es un archivo de texto
     *
     * @since 2003.06.23
     */
    private boolean checkTextFile(final File targetFile) {
        try (final RandomAccessFile fis = new RandomAccessFile(targetFile, "r")) {
            final byte[] magic = new byte[Constants.BUFFER_SIZE];

            fis.seek(0);

            final int count = fis.read(magic);
            final String chunk = new String(magic, 0, count);
            outMessage("Le�dos " + count + " caracteres.");
            for (int ii = 0; ii < count; ii++) {
                final char character = chunk.charAt(ii);
                if (!Character.isLetterOrDigit(character)
                        && !Character.isWhitespace(character)
                        && !isPunctuationChar(character)) {
                    return false;
                }
            }
            // Llegados aqui, es posible que sea solo texto
            reportResult(targetFile, "Parece ser texto", false);
            return true;
        } catch (final Exception ex) {
            LOGGER.severe("Error processing <" + targetFile + '>');
            ToolBox.showInfo(ex);
        }

        return false;
    }

    /**
     * Comprueba si un caracter es un signo de puntuaci�n.
     *
     * @param character el caracter a comprobar
     * @return true si es puntuaci�n, false de lo contrario
     */
    private boolean isPunctuationChar(final char character) {
        return "/*,.;:@#|\\�!�?<>[]{}'()&%$\"_-=+".indexOf(character) >= 0;
    }

    /**
     * Muestra los primeros bytes del archivo
     *
     * @param targetFile el archivo cuyos bytes queremos ver.
     * @return los primeros bytes del archivo.
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
        } finally {
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

    private void reportResult(final File file, final String msg, final boolean alreadyFound) throws IOException {
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
     *
     * @param debug Indica si estamos en modo debug.
     */
    private MagicEntry[] readMagicFile(final boolean debug) {
        final String MAGIC_FILE = "Magic.magic";
        int lineNr = 0;
        boolean ok = false;
        final MagicEntry[] nullMagicEntry = new MagicEntry[0];
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(MAGIC_FILE))))) {
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
     *
     * @param msg the maessage.
     */
    private static void outMessage(final String msg) {
        System.out.println(msg);
    }

    /**
     * Outputs a stderr message.
     *
     * @param msg the maessage.
     */
    private static void errMessage(final String msg) {
        System.err.println(msg);
    }

    private final Map<String, List<String>> allowedExtensions = new HashMap<String, List<String>>() {{
        put("Imagen PNG", Collections.singletonList("png"));
        put("Imagen JPEG", Arrays.asList("jpg", "jpeg"));
        put("Audio MP4", Arrays.asList("mp4", "m4a"));
        put("Imagen GIF version 1987", Collections.singletonList("gif"));
        put("Imagen GIF version 1989", Collections.singletonList("gif"));
        put("opus (OggS) en mi opinion", Collections.singletonList("opus"));
        put("colornote", Collections.singletonList("doc"));
    }};

    public boolean isValidExtension(final File file) {
        final List<String> magics = getMagics4File(file);
        final FileTools.SplittedName splittedName = FileTools.splittName(file);
        return magics.stream().anyMatch(magic -> {
            final List<String> extensions = allowedExtensions.get(magic);
            return extensions == null || extensions.isEmpty() || extensions.contains(splittedName.extension.toLowerCase());
        });
    }
}
