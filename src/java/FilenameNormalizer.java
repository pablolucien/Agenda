import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Normaliza los nombres de archivo descargados de la interneta que puedan
 * tener caracteres codificados, tipo ' ' - '%20'.
 */
public class FilenameNormalizer {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            LOGGER.error("Uso: FilenameNormalizer dir {dir ...} ");
        }
        for (final String arg : args) {
            LOGGER.info("procesando: " + arg);
            final File dir = new File(arg);
            if (!dir.exists()) {
                LOGGER.warn(arg + " no existe");
                continue;
            }
            final File[] content = dir.listFiles();
            if (content != null) {
                for (final File file : content) {
                    String name = file.getName();
                    if (name.contains("%20")) {
                        LOGGER.info("name = " + name);
                        name = name.replaceAll("%20", " ");
                        LOGGER.info("name = " + name);
                        Files.move(file.toPath(), new File(dir, name).toPath());
                    }
                }
            }
        }
    }
}
