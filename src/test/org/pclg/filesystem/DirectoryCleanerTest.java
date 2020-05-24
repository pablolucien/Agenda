package org.pclg.filesystem;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class DirectoryCleanerTest {
	private File emptyRootDir;
	private File nonEmptyRootDir;
	private static List<File> createdDirs = new ArrayList<>();

	@Before
	public void createTree() throws IOException {
		emptyRootDir = Files.createTempDirectory("DirectoryCleanerTest").toFile();
		nonEmptyRootDir = Files.createTempDirectory("DirectoryCleanerTest").toFile();
		final File children1 = new File(emptyRootDir, "1/a/b/c/d/e");
		final File children2 = new File(emptyRootDir, "2/a/b/c/d/e");
		final File children3 = new File(nonEmptyRootDir, "3/a/b/c/d/e");
		createdDirs.add(emptyRootDir);
		createdDirs.add(nonEmptyRootDir);
		assertTrue(children1.mkdirs());
		assertTrue(children2.mkdirs());
		assertTrue(children3.mkdirs());
		final Path tempFile = Files.createTempFile(nonEmptyRootDir.toPath(), "kk", "kk");
		final File file = tempFile.toFile();
		assertTrue(file.exists());
	}

	@Test
	public void testCleanDirsDeletingRoot() throws Exception {

		//FIXME: standarizar con FileTools.delTree()

		assertThat("", DirectoryCleaner.cleanDirs(emptyRootDir, true), is(13));
		assertFalse(emptyRootDir.exists());
	}

	@Test
	public void testCleanDirsKeepingRoot() throws Exception {
		assertThat("", DirectoryCleaner.cleanDirs(emptyRootDir, false), is(12));
		assertTrue(emptyRootDir.exists());
	}

	@Test
	public void testCleanNonEmptyDir1() throws Exception {
		assertThat("", DirectoryCleaner.cleanDirs(nonEmptyRootDir, true), is(6));
		assertTrue(nonEmptyRootDir.exists());
	}

	@Test
	public void testCleanNonEmptyDir2() throws Exception {
		assertThat("", DirectoryCleaner.cleanDirs(nonEmptyRootDir, false), is(6));
		assertTrue(nonEmptyRootDir.exists());
	}

	@AfterClass
	public static void cleanTree() {
		// Delete all of createdDirs usando la misma clase que estamos probando: ¡que locura!
        DirectoryCleaner.delTreesWithFiles(createdDirs);
	}
}