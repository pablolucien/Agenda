package org.pclg.filesystem;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
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
                .filter(EndOfLineChanger::isPossiblyTextFile)
                .forEach(path -> convertFileEOL(path.toFile(), direction));
        }
    }

    public static void convertFileEOL(final File in, final TypeOfConversion typeOfConversion) {
        try {
            final File out = File.createTempFile("out-", ".tmp", in.getParentFile());
            final boolean somethingDone;
            if (typeOfConversion == TypeOfConversion.CRLF2LF) {
                somethingDone = convertCRLF2LF(in, out);
            } else {
                somethingDone = convertLF2CRLF(in, out);
            }
            if (somethingDone) {
                out.setLastModified(in.lastModified());
                Files.delete(in.toPath());
                Files.move(out.toPath(), in.toPath());
            } else {
                Files.delete(out.toPath());
            }
        } catch (final Exception ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
    }

    private static boolean isPossiblyTextFile(final Path path) {
        final String name = path.toFile().getName();
        return name.endsWith(".java")
            || name.endsWith(".properties")
            || name.endsWith(".txt")
            || name.endsWith(".magic")
            || name.endsWith(".info")
            || name.endsWith(".conf")
            || name.endsWith(".xml")
            || name.endsWith(".fxml")
            || name.endsWith(".json")
            || name.endsWith(".js")
            || name.endsWith(".css")
            || name.endsWith(".html")
            || name.endsWith(".yaml")
            || name.endsWith(".bat")
            ;
    }

    private static boolean convertCRLF2LF(final File in, final File out) throws Exception {
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
                LOGGER.log(Level.OFF, "Converted to LF: " + in);
            }
            return somethingDone;
        }
    }

    private static boolean convertLF2CRLF(final File in, final File out) throws Exception {
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
                LOGGER.log(Level.OFF, "Converted to CRLF: " + in);
            }
            return somethingDone;
        }
    }

    public static void main(final String[] args) throws Exception {
        //convertFiles("E:/home/development/projects/Alles/src", TypeOfConversion.LF2CRLF);
        //convertFiles("/media/pablo/MERCURIO/home/development/projects/Alles/src", TypeOfConversion.CRLF2LF);
    }
}
