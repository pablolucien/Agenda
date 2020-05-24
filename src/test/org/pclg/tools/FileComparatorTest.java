package org.pclg.tools;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileComparatorTest extends TestCase {

	public void testCompareAlphabetic() throws IOException {
		final FileComparator comparator = new FileComparator();
		final File dir0 = Files.createTempDirectory("ttmmpp").toFile();
		dir0.deleteOnExit();
		final File file0 = createFile(dir0, "abc.txt");
		final File file1 = file0;
		final File file2 = createFile(dir0, "def.txt");
		final File file3 = createFile(dir0, "def 1.txt");
		final File file4 = createFile(dir0, "xyz.txt");
		final File file5 = createFile(dir0, "xyz.sql");

		comparator.setSortCritery(FileComparator.SortCriterium.ALPHABETIC);
		assertTrue(comparator.compare(file1, file0) == 0);
		assertTrue(comparator.compare(file1, file2) < 0);
		assertTrue(comparator.compare(file2, file1) > 0);
		assertTrue(comparator.compare(file2, file3) > 0);
		assertTrue(comparator.compare(file4, file5) > 0);
		assertTrue(comparator.compare(dir0, file5) < 0);

		comparator.setSortCritery(FileComparator.SortCriterium.REVERSE_ALPHABETIC);
		assertTrue(comparator.compare(file1, file0) == 0);
		assertTrue(comparator.compare(file1, file2) > 0);
		assertTrue(comparator.compare(file2, file1) < 0);
		assertTrue(comparator.compare(file2, file3) < 0);
		assertTrue(comparator.compare(file4, file5) < 0);
        assertTrue(comparator.compare(dir0, file5) < 0);

		comparator.setSortCritery(FileComparator.SortCriterium.ALPHABETIC_EXTENSIONS_APART);
		assertTrue(comparator.compare(file1, file0) == 0);
		assertTrue(comparator.compare(file1, file2) < 0);
		assertTrue(comparator.compare(file2, file1) > 0);
		assertTrue(comparator.compare(file2, file3) < 0);
		assertTrue(comparator.compare(file4, file5) > 0);
        assertTrue(comparator.compare(dir0, file5) < 0);

		comparator.setSortCritery(FileComparator.SortCriterium.REVERSE_ALPHABETIC_EXTENSIONS_APART);
		assertTrue(comparator.compare(file1, file0) == 0);
		assertTrue(comparator.compare(file1, file2) > 0);
		assertTrue(comparator.compare(file2, file1) < 0);
		assertTrue(comparator.compare(file2, file3) > 0);
		assertTrue(comparator.compare(file4, file5) < 0);
        assertTrue(comparator.compare(dir0, file5) < 0);
	}

	private static File createFile(final File dir, final String name) throws IOException {
		final File file = new File(dir, name);
        file.createNewFile();
		file.deleteOnExit();
		return file;
	}
}
