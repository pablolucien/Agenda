package org.pclg.alter;

import org.pclg.gui.JLabeledField;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @since 09/01/2018.
 */
class AlterUtil {
    private AlterUtil() {
    }

    static String insertCharsBeforeExtension(final String name, final String chars2Insert) {
        final int lastIndexOfDot = name.lastIndexOf('.');
        return lastIndexOfDot < 0 ? name + chars2Insert : name.substring(0, lastIndexOfDot) + chars2Insert + name.substring(lastIndexOfDot);
    }

    /**
     * Determina si un 'File' debe ser considerado apto para ser modificado
     * Si hay ficheros marcados explicitamente, se aceptan esos,
     * si no, el default.
     *
     * @param targetFiles
     * @param allFilesAllowed
     * @param selectedFiles
     * @param prefix
     * @return true if the file is eligible; false otherwise.
     */
    static List<File> getEligibleFiles(final File[] targetFiles, final boolean allFilesAllowed, final List<File> selectedFiles, final String prefix) {
        // If the user want all files then we do no filtering.
        if (allFilesAllowed) {
            return Arrays.asList(targetFiles);
        }

        // If the user has some files selected then we accept their selection.
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            return selectedFiles;
        }

        final List<File> eligibleFiles = new ArrayList<>(targetFiles.length);
        for (final File targetFile : targetFiles) {
            // If the user has some files selected then we accept their selection.
            // Only accept plain files
            if (!targetFile.isFile()) {
                continue;
            }

            final String name = targetFile.getName();

            if (!name.startsWith(prefix)) {
                continue;
            }

            // Don't want to rename my classes :)
            if (name.endsWith(".java")) {
                continue;
            }

            // Al others are accepted.
            eligibleFiles.add(targetFile);

        }
        return eligibleFiles;
    }

    /**
     * Gets an int from an JLabeledField.
     *
     * @param textField the field from where to get the mumber.
     *
     * @return the number in the field or 0 if there is not an int in the field.
     */
    static int getIntFromTextField(final JLabeledField textField) {
        int n0;
        try {
            n0 = Integer.parseInt(textField.getText());
        } catch (final NumberFormatException ex) {
            n0 = 0;
        }
        return n0;
    }
}
