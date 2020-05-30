package org.pclg.filesystem;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @since 25/05/2020.
 */
public class EndOfLineChanger {
    public enum TypeOfConversion {CRLF2LF, LF2CRLF}
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    private EndOfLineChanger() {
    }

    public static void convertFiles(final String baseDirectory, final TypeOfConversion direction) throws Exception {
        try (final Stream<Path> paths = Files.walk(Paths.get(baseDirectory))) {
            paths.filter(Files::isRegularFile)
                .filter(path -> {
                    final String name = path.toFile().getName();
                    return name.endsWith(".txt") || name.endsWith(".properties") || name.endsWith(".java")
                        || name.endsWith(".magic") || name.endsWith(".info") || name.endsWith(".conf")
                        || name.endsWith(".xml") || name.endsWith(".json") || name.endsWith(".js")
                        || name.endsWith(".css") || name.endsWith(".html");
                })
                .forEach(path -> {
                    final File in = path.toFile();
                    final File out = new File(in.getParent(), "out-" + in.getName());
                    try {
                        if (direction == TypeOfConversion.CRLF2LF) {
                            convertCRLF2LF(in, out);
                        } else {
                            convertLF2CRLF(in, out);
                        }
                        Files.delete(in.toPath());
                        Files.move(out.toPath(), in.toPath());
                    } catch (final Exception ex) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                    }
                });
        }
    }

    private static void convertCRLF2LF(final File in, final File out) throws Exception {
        try (final FileOutputStream fos = new FileOutputStream(out);
             final FileInputStream fin = new FileInputStream(in)) {
            int car1;
            int car2;
            boolean somethingDone = false;
            while ((car1 = fin.read()) != -1) {
                if (car1 == '\r') {
                    if ((car2 = fin.read()) == '\n') {
                        fos.write('\n');
                        somethingDone = true;
                    } else {
                        fos.write(car1);
                        if (car2 == -1) {
                            break;
                        }
                    }
                    continue;
                }
                fos.write(car1);
            }
            if (somethingDone) {
                LOGGER.log(Level.OFF, "Converted to CRLF: " + in);
            }
        }
    }

    private static void convertLF2CRLF(final File in, final File out) throws Exception {
        try (final FileOutputStream fos = new FileOutputStream(out);
             final FileInputStream fin = new FileInputStream(in)) {
            int car;
            boolean firstCar = true;
            boolean lastWasCR = false;
            boolean somethingDone = false;
            while ((car = fin.read()) != -1) {
                if (firstCar) {
                    lastWasCR = car == '\r';
                    firstCar = false;
                }
                if (car == '\n' && !lastWasCR) {
                    fos.write('\r');
                    somethingDone = true;
                }
                fos.write(car);
                lastWasCR = car == '\r';
            }
            if (somethingDone) {
                LOGGER.log(Level.OFF, "Converted to LF: " + in);
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        convertFiles("/home/pablo/development/projects/Alles/src/test_resources/scratch", TypeOfConversion.LF2CRLF);
        convertFiles("/media/pablo/MERCURIO/home/development/projects/Alles/src", TypeOfConversion.CRLF2LF);
    }
}
