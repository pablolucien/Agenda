package org.pclg.filesystem.synchonizer;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.filesystem.synchonizer.persistence.DataAccess;
import org.pclg.log.LoggerFactory;
import org.pclg.runtime.RuntimeControl;
import org.pclg.tools.ArrayTools;
import org.pclg.tools.FileTools;
import org.pclg.tools.PropertiesHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @since 27/07/2018.
 */
public class DirectorySynchronizer {
    static final String BASENAME = DirectorySynchronizer.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    static final String ADD_FILE_PAD = "++++++ ";
    private static final String MODIF_FILE_PAD = "****** ";
    private static final String ADD_DIR_PAD = "////// ";
    private static final String SPACES_PAD = "       ";
    private static final String UPDATING_DIRS_TAG = "------ Updating {Dirs} ";
    private static final String UPDATING_FILES_TAG = "------ Updating {Files} ";
    private static final String NOT_UPDATING_FILE = SPACES_PAD + "Can't update: ";
    private static final String DIRECTORIO_NO_TRABAJABLE_MSG = "Este directorio no es trabajable: [%s]";
    static final String DOESN_T_EXIST_CREATING_IT = " doesn't exist; creating it";
    private static final String THREE_STRINGS_FORMAT = "%s%s - %s";
    private final Counters counters;
    private File duplicatedFilesLogFile;
    private final DataAccess dataAccess;

    /** Some little modification time to suppose that the files may have been modified; 2 sec means we suppose they are not. */
    static final int INSIGNIFICANT_DETAIL = 2_000;

    private final Properties properties;

    public DirectorySynchronizer() {
        properties = new Properties();
        PropertiesHelper.loadProperties(properties, BASENAME);
        counters = new Counters(PropertiesHelper.getStringFromProperties(properties, "DirectorySynchronizer.status.msg"));
        dataAccess = new DataAccess(properties);
    }

    void synchronize(final File targetsFile) {
        try {
            duplicatedFilesLogFile = File.createTempFile("DirSync", ".log");
            final List<TargetDef> targets = TargetDef.readTargetsFile(targetsFile, new PropertiesMap(properties));
            for (final TargetDef targetDef : targets) {
                TargetDef.FilePair filePair;
                while ((filePair = targetDef.getFilePair()) != null) {
                    final File fileA = filePair.getFileA();
                    final File fileB = filePair.getFileB();
                    if (fileA.isDirectory() && fileB.isDirectory()) {
                        LOGGER.log(Level.OFF, String.format(THREE_STRINGS_FORMAT, UPDATING_DIRS_TAG,
                                Arrays.toString(targetDef.getDirectories()), targetDef.isUnidirectional() ? "Unidirectional" : ""));
                        synchronizeDirectories(targetDef);
                    } else if (fileA.isFile() && fileB.isFile()) {
                        LOGGER.log(Level.OFF, String.format(THREE_STRINGS_FORMAT, UPDATING_FILES_TAG, fileA, fileB));
                        if (!updateFiles(fileA, fileB, false)) {
                            updateFiles(fileB, fileA, false);
                        }
                    } else if (fileA.isFile() && !fileB.exists()) {
                        LOGGER.log(Level.OFF, String.format(THREE_STRINGS_FORMAT, UPDATING_FILES_TAG, fileA, fileB));
                        createTargetFile(fileA, fileB);
                    } else if (fileB.isFile() && !fileA.exists()) {
                        LOGGER.log(Level.OFF, String.format(THREE_STRINGS_FORMAT, UPDATING_FILES_TAG, fileB, fileA));
                        createTargetFile(fileB, fileA);
                    } else {
                        LOGGER.log(Level.OFF, String.format(THREE_STRINGS_FORMAT, NOT_UPDATING_FILE, fileA, fileB));
                    }
                }
            }
            LOGGER.log(Level.OFF, counters);
            if (counters.filesWithSameContent() == 0) {
                Files.delete(duplicatedFilesLogFile.toPath());
            } else {
                LOGGER.log(Level.OFF, "tempLogFile = " + duplicatedFilesLogFile.getAbsolutePath());
            }
        } catch (final Exception ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
    }

    void synchronizeDirectories(final TargetDef targetDef) {
        targetDef.resetPairsCounter(); // chapucilla temporal (que como todo lo "temporal", durará para siempre :(
        TargetDef.FilePair filePair;
        while ((filePair = targetDef.getFilePair()) != null) {    // FIXME: Repensar esto: no hay que darle más vueltas :) (al getFilePair())
            final File dir1 = filePair.getFileA();
            final File dir2 = filePair.getFileB();
            if (dir1.equals(dir2)) {
                LOGGER.warn("It's not useful to synchronize with oneself: " + dir1);
                continue;
            }
            if (isNotWorkable(dir1)) {
                LOGGER.warn(String.format(DIRECTORIO_NO_TRABAJABLE_MSG, dir1));
                continue;
            }
            if (isNotWorkable(dir2)) {
                LOGGER.warn(String.format(DIRECTORIO_NO_TRABAJABLE_MSG, dir2));
                continue;
            }
            try {
                targetDef.resetIterator(); // chapucilla temporal
                final boolean unidirectional = targetDef.isUnidirectional();
                while (targetDef.hasNext()) {
                    final File[] files = targetDef.next();
                    final File srcFile = files[0];
                    final File targetFile = files[1];
                    if (targetFile.exists()) {
                        updateFiles(srcFile, targetFile, unidirectional);
                    } else {
                        createTargetFile(srcFile, targetFile);
                    }
                }
//                LOGGER.log(Level.OFF, String.format(PropertiesHelper
//                        .getStringFromProperties(properties, "DirectorySynchronizer.status.msg"),
//                    dirsCreated, filesCreated, filesUpdated));
            } catch (final Exception ex) {
                throw new DirectorySynchronizerException(ex);
            }
        }
    }

    private void createTargetFile(final File srcFile, final File targetFile) {
        final Path sourcePath = srcFile.toPath();
        final Path targetPath = targetFile.toPath();
        try {
            final File parentFile = targetFile.getParentFile();
            if (!parentFile.exists()) {
                Files.createDirectories(parentFile.toPath());
                LOGGER.log(Level.OFF, ADD_DIR_PAD + parentFile + DOESN_T_EXIST_CREATING_IT);
                counters.incrementDirsCreated();
            }
            Files.copy(sourcePath, targetPath);
            LOGGER.log(Level.OFF, ADD_FILE_PAD + targetFile + DOESN_T_EXIST_CREATING_IT);
            counters.incrementFilesCreated();
        } catch (final IOException ex) {
            LOGGER.warn(SPACES_PAD + "Could not create " + targetFile + ": " + ex);
        }
    }

    private boolean updateFiles(final File srcFile, final File targetFile, final boolean unidirectional) {
        final long targetFileLastModified = targetFile.lastModified();
        final long srcFileLastModified = srcFile.lastModified();
        if (srcFileLastModified - targetFileLastModified > INSIGNIFICANT_DETAIL) {
            try {
                // Do the copy only if the files are really different.
                if (!FileTools.compareContents(srcFile, targetFile)) {
                    final Path sourcePath = srcFile.toPath();
                    final Path targetPath = targetFile.toPath();
                    Files.copy(sourcePath, targetPath, REPLACE_EXISTING, COPY_ATTRIBUTES);
                    counters.incrementFilesUpdated();
                    LOGGER.log(Level.OFF, String.format("%s%s", MODIF_FILE_PAD, targetFile));
                } else {
                    counters.incrementFilesWithSameContent();
                    logDuplicatedFiles(srcFile, targetFile);
                    // Set the same time, so the next time the comparison must not be done.
                    final boolean timesModified;
                    if (unidirectional) {
                        // Don't want to touch the source
                        timesModified = targetFile.setLastModified(srcFileLastModified);
                    } else {
                        // Set the older, so not to lose history
                        // Yes, both files, because the value can have a slight difference
                        timesModified = targetFile.setLastModified(targetFileLastModified) & srcFile.setLastModified(targetFileLastModified);
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(String.format("%s%s. targetFileLastModified = %d, srcFileLastModified = %d, Times %s modified",
                                "NOT UPDATED SINCE NOT MODIFIED ---> ", targetFile, targetFileLastModified, srcFileLastModified, timesModified ? "" : "Not"));
                    }
                }
                return true;
            } catch (final AccessDeniedException ade) {
                LOGGER.error("ERROR. I've got this: " + ade + ". Check the rights of the file.");
            } catch (final IOException ex) {
                LOGGER.error("ERROR. Nevertheless going ahead.", ex);
            }
        }
        return false;
    }

    private void logDuplicatedFiles(final File srcFile, final File tgtFile) {
        saveInfoForUlteriorChecks(srcFile, tgtFile);
        final long tgtFileLastModified = tgtFile.lastModified();
        final long srcFileLastModified = srcFile.lastModified();
        final Date tgtDate = new Date(tgtFileLastModified);
        final Date srcDate = new Date(srcFileLastModified);
        final LocalDateTime tgtLocalDateTimeUTC = LocalDateTime.ofEpochSecond(tgtFileLastModified / 1000, 0, ZoneOffset.UTC);
        final LocalDateTime srcLocalDateTimeUTC = LocalDateTime.ofEpochSecond(srcFileLastModified / 1000, 0, ZoneOffset.UTC);
        try (final RandomAccessFile randomAccessFile = new RandomAccessFile(duplicatedFilesLogFile, "rw")) {
            randomAccessFile.seek(duplicatedFilesLogFile.length());
            randomAccessFile.writeBytes(String.format("Source: %s, lastModified: %d, date: %s date UTC: %s%n", srcFile.getAbsolutePath(), srcFileLastModified, srcDate, srcLocalDateTimeUTC));
            randomAccessFile.writeBytes(String.format("Target: %s, lastModified: %d, date: %s date UTC: %s%n", tgtFile.getAbsolutePath(), tgtFileLastModified, tgtDate, tgtLocalDateTimeUTC));
            randomAccessFile.writeBytes(String.format("%n"));
        } catch (final IOException ex) {
            LOGGER.error("ERROR logging in " + duplicatedFilesLogFile, ex);
        }
    }

    private void saveInfoForUlteriorChecks(final File srcFile, final File tgtFile) {
//        dataAccess.saveInformation(srcFile);
 //       dataAccess.saveInformation(tgtFile);
    }

    private static boolean isNotWorkable(final File dir) {
        return !(dir.isDirectory() && dir.canExecute() && dir.canRead() && dir.canWrite());
    }

    static void addAppender(final Appender textAreaAppender) {
        LOGGER.addAppender(textAreaAppender);
    }

    public void resetCounters() {
        counters.clear();
    }

    public static void main(final String[] args) {
        try {
            switch (args.length) {
                case 0:
                    new DirectorySynchronizerGUI();
                    break;
                case 1:
                    LOGGER.log(Level.OFF, "Synchronizing " + args[0]);
                    new DirectorySynchronizer().synchronize(new File(args[0]));
                    break;
                case 2:
                    new DirectorySynchronizer().synchronizeDirectories(
                            new TargetDef(new File(args[0]), new File(args[1])));
                    break;
                default:
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ArrayTools.printArray(new PrintStream(out), args);
                    throw new IllegalArgumentException(new String(out.toByteArray()));
            }
        } catch (final Throwable thr) {
            LOGGER.error(LoggerFactory.ERROR_TAG, thr);
        }
    }
}
