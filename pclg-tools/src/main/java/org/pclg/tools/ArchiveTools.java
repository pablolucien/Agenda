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
    public static final String MANIFEST_NAME = "org.pclg.tools.ArchiveTools.manifest";

    private ArchiveTools() {
    }

    public static void createZip(final File sourceFile, final File targetZipFile) {
        createZip(sourceFile, targetZipFile, null);
    }
    
    public static void createZip(final File sourceFile, final File targetZipFile, final byte[] manifest) {
        if (!sourceFile.exists()) {
            throw new RuntimeException("Source file " + sourceFile + " doesn't exist");
        }
        try {
            final Path target = Files.createFile(targetZipFile.toPath());
            try (final ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(target))) {
                if (manifest != null) {
                    final ZipEntry zipEntry = new ZipEntry(MANIFEST_NAME);
                    zs.putNextEntry(zipEntry);
                    zs.write(manifest);
                    zs.closeEntry();
                }

                final Path sourceDir = sourceFile.toPath();
                try (final Stream<Path> pathStream = Files.walk(sourceDir)) {
                    pathStream
                        .filter(path -> !path.equals(sourceDir))
                        .forEach(path -> {
                            final Path relativize = sourceDir.relativize(path);
                            final String name = relativize.toString().replace('\\', '/');
                            final ZipEntry zipEntry;
                            try {
                                if (Files.isDirectory(path)) {
                                    // FIXME: This dosn't seem to work
                                    zipEntry = new ZipEntry(name + '/');
                                    zs.putNextEntry(zipEntry);
                                } else {
                                    zipEntry = new ZipEntry(name);
                                    zs.putNextEntry(zipEntry);
                                    Files.copy(path, zs);
                                }
                                zs.closeEntry();
                            } catch (final IOException ex) {
                                LOGGER.error(ex);
                            }
                        });
                }
            }
        } catch (final IOException ex) {
            LOGGER.error(ex);
        }
    }

    public static void extractZip(final File sourceFile, final File targetDir) {
        if (!sourceFile.exists()) {
            throw new RuntimeException("Source file " + sourceFile + " doesn't exist");
        }
        try (final ZipFile zipFile = new ZipFile(sourceFile)) {
            final Path databasePath = targetDir.toPath();
            if (!targetDir.exists()) {
                Files.createDirectories(databasePath);
            }

            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                final String entryName = entry.getName();
                if (MANIFEST_NAME.equals(entryName)) {
                    continue;
                }
                final String name = entryName.replace("\\", "/");     // FIXME: Why do I have to do this and is it the best way? Should I do something in createZip()?
                final Path resolvedTarget = databasePath.resolve(name);
                if (entry.isDirectory()) {
                    Files.createDirectories(resolvedTarget);
                } else {
                    final Path resolvedTargetParent = resolvedTarget.getParent();
                    Files.createDirectories(resolvedTargetParent);
                    final InputStream inputStream = zipFile.getInputStream(entry);
                    Files.copy(inputStream, resolvedTarget, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (final IOException ex) {
            LOGGER.error(ex);
        }
    }

    public static byte[] readManifest(final File sourceFile) {
        if (!sourceFile.exists()) {
            throw new RuntimeException("Source file " + sourceFile + " doesn't exist");
        }
        try (final ZipFile zipFile = new ZipFile(sourceFile)) {
            final ZipEntry entry = zipFile.getEntry(MANIFEST_NAME);
            if (entry == null) {
                return ArrayTools.NULL_BYTE_ARRAY;
            }
            final InputStream inputStream = zipFile.getInputStream(entry);
            final int entrySize = (int) entry.getSize();
            final byte[] bytes = new byte[entrySize];
            final int count;
            if ((count = inputStream.read(bytes)) != entrySize) {
                throw new RuntimeException("Error reading manifest: read " + count + " bytes; expected " + entrySize);
            }
            return bytes;
        } catch (final IOException ex) {
            LOGGER.error(ex);
            throw new RuntimeException(ex);
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
