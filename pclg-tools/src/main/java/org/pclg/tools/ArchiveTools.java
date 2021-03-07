package org.pclg.tools;

import org.pclg.log.EnhancedLogger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Archive tools such as zip, unzip, etc.
 */
public class ArchiveTools {
    private static final EnhancedLogger LOGGER = LoggerFactory.makeEnhancedLogger();

    private ArchiveTools() {
    }

    public static void createZip(final File sourceFile, final File targetZipFile) {
        if (!sourceFile.exists()) {
            throw new RuntimeException("Source file " + sourceFile + " doesn't exist");
        }
        try {
            final Path target = Files.createFile(targetZipFile.toPath());
            try (final ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(target))) {
                final Path sourceDir = sourceFile.toPath();
                try (final Stream<Path> pathStream = Files.walk(sourceDir)) {
                    pathStream
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            final ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                            try {
                                zs.putNextEntry(zipEntry);
                                Files.copy(path, zs);
                                zs.closeEntry();
                            } catch (final IOException ex) {
                                ex.printStackTrace();
                                LOGGER.error(ex);
                            }
                        });
                }
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
            LOGGER.error(ex);
        }
    }

    public static void extractZip(final File backupFile, final File databaseDir) {
        try (final ZipFile zipFile = new ZipFile(backupFile)) {
            final Path databasePath = databaseDir.toPath();
            if (!databaseDir.exists()) {
                Files.createDirectories(databasePath);
            }

            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                final String name = entry.getName();
                final Path resolvedTarget = databasePath.resolve(name);
                final Path resolvedTargetParent = resolvedTarget.getParent();
                Files.createDirectories(resolvedTargetParent);
                final InputStream inputStream = zipFile.getInputStream(entry);
                Files.copy(inputStream, resolvedTarget, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
            LOGGER.error(ex);
        }
    }

    /**
     * Devuelve el contenido de un archivo zip o jar
     * --author El Coyote cojo
     *
     * @param archiveFile El archivo que queremos revisar
     * @return el contenido del archivo
     * @since 2001.11.13
     */
    public static Optional<String[]> listZip(final File archiveFile) {
        final List<String> strings = new ArrayList<>();
        try (final ZipFile zipFile = new ZipFile(archiveFile)) {
            for (final Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
                final ZipEntry entry = entries.nextElement();
                strings.add(entry.getName());
            }
            String[] result = new String[strings.size()];
            result = strings.toArray(result);
            return Optional.of(result);
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
            return Optional.empty();
        }
    }
}
