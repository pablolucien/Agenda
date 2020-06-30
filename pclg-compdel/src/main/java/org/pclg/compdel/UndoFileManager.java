package org.pclg.compdel;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.runtime.RuntimeControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @since 15/01/2018.
 */
public class UndoFileManager {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private List<File> deletedDirs;
    private List<String> deletedFiles;
    private PrintStream undoFile;
    private String undoFileLocation;

    UndoFileManager() {
        RuntimeControl.registerShutdownHook(this::closeUndoFile);
    }

    /** Initialises the files needed to undo de deletions.
     * @throws java.io.FileNotFoundException if something goes bad.
     */
    void initUndoFile() throws FileNotFoundException {
        final String now = new Date().toString().replace(':', '_').replace(' ', '_');
        if (undoFileLocation == null) {
            throw new NullPointerException("Can't create undo file in null directory");
        }
        final File dir = new File(undoFileLocation);
        if (!dir.exists()) {
            LOGGER.debug("Creating undo directory: " + (dir.mkdirs() ? "OK" : "KO!"));
        }

        final File file = new File(undoFileLocation, "CompDel_Undo_" + now + ".bat");
        undoFile = new PrintStream(file);
        LOGGER.debug("Creating undo file:\t " + file);
        deletedFiles = new ArrayList<>();
    }

    void add2UndoFile(final File deletedFile, final File savedFile) {
        final String savedFilePath = savedFile.getPath();
        final String deletedFileParentPath = deletedFile.getParent();
        final String undoLine = "copy \"" + savedFilePath + "\" \"" + deletedFileParentPath + '"';
        deletedFiles.add(undoLine);
    }


    void closeUndoFile() {
        if (undoFile != null) {
            if (deletedDirs != null) {
                for (int ii = deletedDirs.size() - 1; ii >= 0; ii--) {
                    undoFile.println("md \"" + deletedDirs.get(ii) + "\"");
                }
            }
            if (deletedFiles != null) {
                for (int ii = deletedFiles.size() - 1; ii >= 0; ii--) {
                    undoFile.println(deletedFiles.get(ii));
                }
            }
            undoFile.close();
        }
    }

    void setUndoFileLocation(final String undoFileLocation) {
        this.undoFileLocation = undoFileLocation;
    }

    void setDeletedDirs(final List<File> deletedDirs) {
        this.deletedDirs = deletedDirs;
    }
}
