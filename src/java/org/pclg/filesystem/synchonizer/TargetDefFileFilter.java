package org.pclg.filesystem.synchonizer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author El Coyote Cojo
 * @since 1/11/18 8:18
 */
final class TargetDefFileFilter implements FileFilter {
	private static final String DIRECTORY_SUFFIX = "/";
	private static final String SPLIT_REGEX = ":";
	private final List<String> includedFileNames;
	private List<String> excludedDirNames;
	private List<Pattern> excludedFileNamesPatterns;
	private List<Pattern> includedFileNamesPatterns;

	TargetDefFileFilter(final String includePattern, final String excludePattern,
			final String[] includedFileNames) {
		this.includedFileNames = includedFileNames == null ? Collections.emptyList() : Arrays.asList(includedFileNames);
		createFilterCriteria(includePattern, excludePattern);
	}

	@Override
	public boolean accept(final File file) {
		final String fileName = file.getName();
		if (file.isDirectory()) {
			return !excludedDirNames.contains(fileName);
		}

		for (final Pattern fileNamesPattern : excludedFileNamesPatterns) {
			if (fileNamesPattern.matcher(fileName).matches()) {
				return false;
			}
		}

		if (!includedFileNames.isEmpty()) {
			return includedFileNames.contains(fileName);
		}

		if (!includedFileNamesPatterns.isEmpty()) {
			for (final Pattern fileNamesPattern : includedFileNamesPatterns) {
				if (fileNamesPattern.matcher(fileName).matches()) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	private void createFilterCriteria(final String includePattern, final String excludePattern) {
		if (excludePattern == null) {
			excludedDirNames = Collections.emptyList();
			excludedFileNamesPatterns = Collections.emptyList();
		} else {
			final String[] excluded = excludePattern.split(SPLIT_REGEX);
			excludedDirNames = new ArrayList<>(excluded.length);
			excludedFileNamesPatterns = new ArrayList<>(excluded.length);
			for (final String item : excluded) {
				if (item.endsWith(DIRECTORY_SUFFIX)) {
					excludedDirNames.add(item.substring(0, item.lastIndexOf(DIRECTORY_SUFFIX)));
				} else {
					excludedFileNamesPatterns.add(Pattern.compile(normalizeRegez(item)));
				}
			}
		}

		if (includePattern == null) {
			includedFileNamesPatterns = Collections.emptyList();
		} else {
			final String[] included = includePattern.split(SPLIT_REGEX);
			includedFileNamesPatterns = new ArrayList<>(included.length);
			for (final String item : included) {
				includedFileNamesPatterns.add(Pattern.compile(normalizeRegez(item)));
			}
		}
	}

	private String normalizeRegez(final String regex) {
		// El órden de estos replace es importante.
		return regex
			.replaceAll("\\.", "\\\\.")
			.replaceAll("\\*", ".+");
	}
}
