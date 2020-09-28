package org.pclg.filesystem.synchonizer;

import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.Dir;
import org.pclg.tools.FileTools;
import org.pclg.tools.Pair;
import org.pclg.tools.VariableSubstitutionHelper;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.pclg.tools.FileTools.NULL_FILE_ARRAY;

/**
 * @author El Coyote Cojo
 * @since 8/09/18 20:11
 */
final class TargetDef implements Iterator<File[]> {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final String RECURSE_TAG = "recurse";
    private static final String UNIDIRECTIONAL_TAG = "unidirectional";
    private static final String A_TAG = "A";
    private static final String B_TAG = "B";
    private static final String EXCLUDE_PATTERN__TAG = "Exclude";
    private static final String INCLUDE_PATTERN_TAG = "Include";
    private static final String TARGETS_TAG = "targets";
    private static final String IMPORT_TAG = "import";
    private static final String FILES_TAG = "files";
    private static final String DIRS_TAG = "dirs";
    private final String[] includedFileNames;

    private final String excludePattern;
    private final String includePattern;
    private final boolean recurse;
    private boolean unidirectional;
    private Pair<File, File> filePair;
    private final File[] files;
    private int filesPointer;
    private Iterator<File[]> iterator;

    TargetDef(final File ... files) {
        this(true, false, null, null, null, files);
    }

    TargetDef(final boolean recurse, boolean unidirectional, final String excludePattern,
              final String includePattern, final String[] includedFileNames, final File... files) {
        this.recurse = recurse;
        this.unidirectional = unidirectional;
        this.excludePattern = excludePattern;
        this.includePattern = includePattern;
        this.includedFileNames = includedFileNames;
        this.files = files;
        if (allAreDirectories(files)) {
            // Postpone listing the directories until needed
            return;
        }
        if (allAreFilesOrDoNotExist(files)) {
            iterator = Collections.singletonList(files).iterator();
        } else if (allExist(files)) {
            throw new IllegalArgumentException("Should be both the same type (file or dir): " + Arrays.asList(files));  // FIXME: o usar toolbox
        } else {
            iterator = Collections.emptyIterator();
        }
    }

    private static boolean allAreDirectories(final File[] files) {
        return Arrays.stream(files).allMatch(File::isDirectory);
    }

    private static boolean allAreFilesOrDoNotExist(final File[] files) {
        return Arrays.stream(files).allMatch(f -> f.isFile() || !f.exists());
    }

    private static boolean allExist(final File[] files) {
        return Arrays.stream(files).allMatch(File::exists);
    }

    private Iterator<File[]> createDirContentIterator() {
        final File dirA;
        final File dirB;
        if (filePair == null || (dirA = filePair.first()) == null || (dirB = filePair.second()) == null) {
            return FileTools.NULL_FILE_ITERATOR;
        }
        if (!dirA.isDirectory() || !dirB.isDirectory()) {
            throw new IllegalStateException();
        }
//        if (includedFileNames != null && includedFileNames.length != 0) {
//            return new OnlyIncludedFilesIterator(files, includedFileNames);
//        }
        final List<File> filesA;
        final List<File> filesB;
        if (recurse) {
            final FileFilter fileFilter = new TargetDefFileFilter(includePattern,
                excludePattern, includedFileNames);
            final Dir dir = new Dir(fileFilter);
            try {
                filesA = dir.listarArchivos(dirA, true, false, Dir.AcceptableType.FILE);
                filesB = dir.listarArchivos(dirB, true, false, Dir.AcceptableType.FILE);
            } catch (final IOException ex) {
                throw new DirectorySynchronizerException(ex);
            }
        } else {
            final FileFilter onlyFilesFilter = File::isFile;
            filesA = Arrays.asList(dirA.listFiles(onlyFilesFilter));
            filesB = Arrays.asList(dirB.listFiles(onlyFilesFilter));
        }
        final List<File[]> pairs = new ArrayList<>(filesA.size() + filesB.size());
        for (final File file : filesA) {
            final File[] pair = new File[2];
            pair[0] = file;                                 // source
            pair[1] = buildTargetFile(dirB, dirA, file);    // target
            pairs.add(pair);
        }
        if (!unidirectional) {
            for (final File file : filesB) {
                final File[] pair = new File[2];
                pair[0] = file;                                 // source
                pair[1] = buildTargetFile(dirA, dirB, file);    // target
                pairs.add(pair);
            }
        }
        return pairs.iterator();
    }

    private static File buildTargetFile(final File targetDir, final File sourceDir, final File sourceFile) {
        try {
            final String canonicalPath = sourceFile.getCanonicalPath();
            final String sourceDirCanonicalPath = sourceDir.getCanonicalPath();
            final String replace = canonicalPath.replace(sourceDirCanonicalPath + (sourceDirCanonicalPath.endsWith(File.separator) ? "" : File.separator), "");
            return new File(targetDir, replace);
        } catch (final IOException ex) {
            throw new DirectorySynchronizerException(ex);
        }
    }

    static List<TargetDef> readTargetsFile(final File targetsFile, final Map<String, String> variablesMapping) throws IOException {
        return readTargetsFile(targetsFile, variablesMapping, new HashSet<>());
    }

    private static List<TargetDef> readTargetsFile(final File targetsFile, final Map<String, String> variablesMapping,
            final Set<File> includedTargetFiles) throws IOException {
        if (includedTargetFiles.contains(targetsFile)) {
            LOGGER.warn("Redundant targets file: " + targetsFile);
            return Collections.emptyList();
        }
        includedTargetFiles.add(targetsFile);
        final List<String> allLines = Files.readAllLines(targetsFile.toPath(), StandardCharsets.ISO_8859_1);
        final String jsonStr = allLines.stream()
            .map(s -> VariableSubstitutionHelper.replace(s, variablesMapping)).collect(Collectors.joining(""));
        final JSONObject jsonObject = new JSONObject(jsonStr);
        final List<TargetDef> targetDefs = new ArrayList<>();
        if (jsonObject.has(IMPORT_TAG)) {
            final JSONArray jsonArray = jsonObject.getJSONArray(IMPORT_TAG);
            for (int ii = 0, length = jsonArray.length(); ii < length; ii++) {
                final String filename = jsonArray.getString(ii);
                File file = new File(filename);
                if (!file.isAbsolute()) {
                    file = new File(targetsFile.getParentFile(), filename);
                }
                if (!file.exists()) {
                    throw new FileNotFoundException(file.getAbsolutePath());
                }
                targetDefs.addAll(readTargetsFile(file, variablesMapping, includedTargetFiles));
            }
        }
        if (jsonObject.has(TARGETS_TAG)) {
            final JSONArray jsonArray = jsonObject.getJSONArray(TARGETS_TAG);
            targetDefs.addAll(getTargetDefsFromArray(jsonArray));
        }
        return targetDefs;
    }

    private static List<TargetDef> getTargetDefsFromArray(final JSONArray jsonArray) {
        final int length = jsonArray.length();
        final List<TargetDef> targets = new ArrayList<>(length);
        final List<Thread> threads = new ArrayList<>(length);
        //FIXME: �Estoy haci�ndo esto s�lo para que el test pase o realmente me interesa el �rden?
        for (int ii = 0; ii < length; ii++) {
            targets.add(null);
        }
        for (int ii = 0; ii < length; ii++) {
            final JSONObject obj = jsonArray.getJSONObject(ii);
            final int index = ii;
            final Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        final TargetDef targetDef = createTargetDefFromJSONObject(obj);
                        synchronized (targets) {
                            targets.set(index, targetDef);
                        }
                    } catch (final IOException ex) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                        throw new DirectorySynchronizerException(ex);
                    }
                }

                private TargetDef createTargetDefFromJSONObject(final JSONObject innerJsonObject) throws IOException {
                    final File[] targetFiles;
                    if (innerJsonObject.has(A_TAG)) {
                        targetFiles = new File[] {new File(innerJsonObject.getString(A_TAG)), new File(innerJsonObject.getString(B_TAG))};
                    } else {
                        final JSONArray array = innerJsonObject.getJSONArray(DIRS_TAG);
                        final int length = array.length();
                        final List<File> dirs = new ArrayList<>(length);
                        for (int ii = 0; ii < length; ii++) {
                            final File possibleDir = new File(array.getString(ii));
                            if (possibleDir.exists()) {
                                dirs.add(possibleDir);
                            }
                        }
                        targetFiles = dirs.toArray(NULL_FILE_ARRAY);
                    }
                    final boolean innerRecurse = !innerJsonObject.has(RECURSE_TAG) || innerJsonObject.getBoolean(RECURSE_TAG);
                    final boolean innerUnidirectional = innerJsonObject.has(UNIDIRECTIONAL_TAG) && innerJsonObject.getBoolean(UNIDIRECTIONAL_TAG);
                    final String innerExcludePattern = innerJsonObject.has(EXCLUDE_PATTERN__TAG) ? innerJsonObject.getString(EXCLUDE_PATTERN__TAG) : null;
                    final String innerIncludePattern = innerJsonObject.has(INCLUDE_PATTERN_TAG) ? innerJsonObject.getString(INCLUDE_PATTERN_TAG) : null;
                    final String[] innerIncludedFileNames = obtainIncludedFiles(innerJsonObject.has(FILES_TAG) ? innerJsonObject.getJSONArray(FILES_TAG) : null);
                    return new TargetDef(innerRecurse, innerUnidirectional, innerExcludePattern, innerIncludePattern, innerIncludedFileNames, targetFiles);
                }

                private String[] obtainIncludedFiles(final JSONArray jsonArray) {
                    final String[] includedFileNames;
                    if (jsonArray != null) {
                        final int length = jsonArray.length();
                        includedFileNames = new String[length];
                        for (int ii = 0; ii < length; ii++) {
                            includedFileNames[ii] = jsonArray.getString(ii);
                        }
                    } else {
                        includedFileNames = null;
                    }
                    return includedFileNames;
                }
            };
            threads.add(thread);
            thread.start();
        }
        for (final Thread thread : threads) {
            try {
                thread.join();
            } catch (final InterruptedException ex) {
                LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                Thread.currentThread().interrupt();
            }
        }
        return targets;
    }

    public String[] getFiles() {
        return includedFileNames;
    }

    void resetPairsCounter() {
        filesPointer = 0;
    }

    Pair<File, File> getFilePair() {
        if (filesPointer < files.length - 1) {
            filePair = new Pair<>(files[filesPointer], files[filesPointer + 1]);
        } else if (filesPointer < files.length && !unidirectional) {
            filePair = new Pair<>(files[filesPointer], files[0]);
        } else {
            filePair = null;
        }
        filesPointer++;

        return filePair;
    }

    String getExcludePattern() {
        return excludePattern;
    }

    String getIncludePattern() {
        return includePattern;
    }

    public boolean isRecurse() {
        return recurse;
    }

    public boolean isUnidirectional() {
        return unidirectional;
    }

    File[] getDirectories() {
        return files.clone();
    }

    @Override
    public boolean hasNext() {
        if (iterator == null) {
            iterator = createDirContentIterator();
        }
        return iterator.hasNext();
    }

    @Override
    public File[] next() {
        if (iterator == null) {
            iterator = createDirContentIterator();
        }
        return iterator.next();
    }

    void resetIterator() {
        iterator = createDirContentIterator();
    }


    @Override
    public String toString() {
        return "TargetDef{" +
            "includedFileNames=" + Arrays.toString(includedFileNames) +
            ", excludePattern='" + excludePattern + '\'' +
            ", includePattern='" + includePattern + '\'' +
            ", recurse=" + recurse +
            ", filePair=" + filePair +
            (files != null ? ", " + Arrays.asList(files) : "") +
            '}';
    }

    private static class OnlyIncludedFilesIterator implements Iterator<File[]> {
        private final File[] dirs;
        private final String[] fileNames;
        private int index;

        public OnlyIncludedFilesIterator(File[] dirs, String[] fileNames) {
            this.dirs = dirs.clone();
            this.fileNames = fileNames.clone();
        }

        @Override
        public boolean hasNext() {
            return index < fileNames.length;
        }

        @Override
        public File[] next() {
            return new File[] {new File(dirs[0], fileNames[index]), new File(dirs[1], fileNames[index++])};
        }
    }
}
