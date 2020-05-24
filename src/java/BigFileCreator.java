import org.pclg.filesystem.FileCreator;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ToolBox;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * BigFileCreator <BR>
 * Creador de archivos
 * Uso: BigFileCreator &lt;n&gt;[k|m|g|t] &lt;filename&gt;
 * donde &lt;n&gt; es el tamaño del archivo
 * (k = kilobytes, m = megabytes, g = gigabytes, t = terabytes)
 * por omision bytes
 * y &lt;filename&gt; el nombre del archivo a crear.
 *
 * @author El Coyote Cojo
 * @version 1.0
 */
@SuppressWarnings({"ClassWithoutPackageStatement"})
public final class BigFileCreator {
    /** El logger. */
    private static final Logger LOGGER = LoggerFactory.make();

    /* Usage message. */
    private static final String MSG_USAGE =
            "Uso: BigFileCreator <n>[b|k|m|g|t] [-o] <filename>\n"
            + "\tdonde <n> es el tamaño del archivo\n"
            + "\t(b: bytes, k: kilobytes, m: megabytes, g: gigabytes, t: terabytes)\n"
            + "\t(por omision bytes)\n"
            + "\ty <filename> el nombre del archivo a crear.\n"
            + "\tSi se usa -o, el archivo creado es llenado con bytes al azar";

    /**
     * Prevents instantiation.
     */
    private BigFileCreator() {
    }

    /**
     * A little help to the friends.
     */
    private static void usage() {
        LOGGER.severe(MSG_USAGE);
        System.exit(1);
    }

    /**
     * Entry point to the application.
     * @param args arguments to the app.
     * @throws IOException when some I/O error occurs.
     */
    public static void main(final String [] args) throws IOException {
        if (args.length != 2 && args.length != 3) {
            usage();
        }
        if (args.length == 3 && !args[1].equals("-o")) {
            usage();
        }

        final long size = ToolBox.parseSize(args[0]);
        final String nome = args[args.length -1];
        LOGGER.info("Creando <" + nome + "> con " + size + " bytes");
        FileCreator.createFile(nome, size, args.length == 3);
    }
}
