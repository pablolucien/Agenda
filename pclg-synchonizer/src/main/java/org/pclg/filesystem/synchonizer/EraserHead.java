package org.pclg.filesystem.synchonizer;

import org.apache.logging.log4j.Level;
import org.pclg.log.LoggerFactory;
import org.pclg.log.TextAreaAppender;
import org.pclg.log.TextAreaLogger;
import org.pclg.tools.StringTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.pclg.filesystem.synchonizer.DirectorySynchronizer.ADD_DIR_PAD;
import static org.pclg.filesystem.synchonizer.DirectorySynchronizer.ADD_FILE_PAD;

/**
 * Class to delete things that DirectorySynchronizer has copied by error. Deletes in various targets.
 *
 * @since 20/12/2018.
 */
final class EraserHead {
    private static final TextAreaLogger LOGGER = new TextAreaLogger(LoggerFactory.makeLog4J());

	private EraserHead() {
	}

	static void processStream(final Stream<String> lines, final String[] baseDirs, final boolean onlyTest) {
        final Map<String, List<String>> listMap = lines.collect(Collectors.groupingBy(line -> {
            if (line.contains(ADD_FILE_PAD)) {
                return ADD_FILE_PAD;
            } else if (line.contains(ADD_DIR_PAD)) {
                return ADD_DIR_PAD;
            } else {
                return "Noise";
            }
        }));

        listMap.get(ADD_FILE_PAD).stream().map(line -> line.replace(ADD_FILE_PAD, ""))
                .forEach(line -> deleteFileAndSiblings(line, baseDirs, onlyTest));

        // At this moment it should be possible to delete the directories
        listMap.get(ADD_DIR_PAD).stream().map(line -> line.replace(ADD_DIR_PAD, ""))
                .forEach(line -> deleteFileAndSiblings(line, baseDirs, onlyTest));
    }

    private static void deleteFileAndSiblings(final String filename, final String[] baseDirs,
		    final boolean onlyTest) {
        Arrays.stream(baseDirs).filter(dirName -> !StringTools.isEmptyOrBlank(dirName) && filename.startsWith(dirName)).findFirst().ifPresent(dirName -> {
			final String nameTail = filename.substring(dirName.length());
			LOGGER.debug("\nnameTail = " + nameTail);
			deleteWholePack(nameTail, baseDirs, onlyTest);
		});
    }

    private static void deleteWholePack(final String nameTail, final String[] baseDirs,
			final boolean onlyTest) {
        Arrays.stream(baseDirs).forEach(baseDir -> deleteFile(baseDir + nameTail, onlyTest));
    }

    private static void deleteFile(final String filename, final boolean onlyTest) {
        final File file = new File(filename);
        if (file.exists()) {
			if (onlyTest) {
				LOGGER.log(Level.OFF, "Should have deleted = " + filename);
				return;
			}
            LOGGER.log(Level.OFF, "Deleting = " + filename);
            try {
                Files.delete(file.toPath());
            } catch (final IOException ex) {
                LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                throw new DirectorySynchronizerException(ex);
            }
        } else {
            LOGGER.debug(filename + " doesn't exist!");
        }
    }

	public static void addAppender(final TextAreaAppender appender) {
		LOGGER.addAppender(appender);
	}

    public static void main(final String[] args) {
        try {
            final String[] baseDirs = {"C:\\devops\\workspace", "E:\\workspace_EBU", "F:\\workspace_EBU"};
            final Stream<String> lines = Files.lines(new File(args[0]).toPath());
            processStream(lines, baseDirs, false);
        } catch (final Throwable thr) {
            LOGGER.error(LoggerFactory.ERROR_TAG, thr);
        }
    }
}
