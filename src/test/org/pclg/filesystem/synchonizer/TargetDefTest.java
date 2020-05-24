package org.pclg.filesystem.synchonizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.pclg.filesystem.DirectoryCleaner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class TargetDefTest {
	private static final String PREFIX = "TargetDefTest";
	private static final List<File> createdDirs = new ArrayList<>(10);
	private static final String REGEX_BACKSLASH = "\\\\";
	private static final String _TEST_DIR_A = "/tmp/DirectorySynchronizer_Test_dirA";
	private static final String _TEST_DIR_B = "/tmp/DirectorySynchronizer_Test_dirB";
	private static final String _TEST_DIR_C = "/tmp/DirectorySynchronizer_Test_dirC";
	private String testDirA = _TEST_DIR_A;
	private String testDirB = _TEST_DIR_B;
	private String testDirC = _TEST_DIR_C;
	private File aDir;
	private File bDir;
	private File file1 ;
	private File file2 ;
	private final File nonexistentFile = new File("does not exist");

	@BeforeMethod
	public void initFileTree() throws IOException {
		aDir = createTempDir();
		bDir = createTempDir();
		final File child1 = new File(aDir, "1/a/b/c/d/e");
		final File child2 = new File(aDir, "2");
		final File dirCvs = new File(aDir, "2/CVS");
		assertTrue(child1.mkdirs());
		assertTrue(child2.mkdirs());
		assertTrue(dirCvs.mkdirs());
		final Path path1 = Files.createTempFile(child1.toPath(), "FILE_1_", "kk");
		file1 = path1.toFile();
		assertTrue(file1.exists());
		final Path path2 = Files.createTempFile(child2.toPath(), "FILE_2_", "kk");
		file2 = path2.toFile();
		assertTrue(file2.exists());
		final Path path3 = Files.createTempFile(aDir.toPath(), "FILE_3_", "kk");
		final File file3 = path3.toFile();
		assertTrue(file3.exists());
		final Path path4 = Files.createTempFile(bDir.toPath(), "FILE_4_", "kk");
		final File file4 = path4.toFile();
		assertTrue(file4.exists());
		final Path pathCvs = Files.createTempFile(dirCvs.toPath(), "FILE_Cvs_", "kk");
		final File fileCvs = pathCvs.toFile();
		assertTrue(fileCvs.exists());
	}

	private static File createTempDir() throws IOException {
		final File dir = Files.createTempDirectory(PREFIX).toFile();
		createdDirs.add(dir);
		return dir;
	}

	@Test
	public void readTargetsFile() throws IOException, URISyntaxException {
		final File targetsFile = prepareTargetDefFile("/Test_DS_Targets.json");
		final List<TargetDef> targets = TargetDef.readTargetsFile(targetsFile, Collections.emptyMap());
        assertEquals(targets.size(), 5);
		TargetDef targetDef = targets.get(0);
		checkTarget1(targetDef);
		final TargetDef.FilePair filePair;

		targetDef = targets.get(1);
		assertTrue(targetDef.isRecurse());
		filePair = targetDef.getFilePair();
        assertEquals(filePair.getFileA(), new File("C:/home/databases/MainView_ACME.qdr"));
        assertEquals(filePair.getFileB(), new File("L:/_DB/MainView_ACME.qdr"));
        assertEquals(targetDef.getExcludePattern(), "Program files/");
        assertEquals(targetDef.getIncludePattern(), "include this");

		targetDef = targets.get(2);
		assertTrue(targetDef.isRecurse());

		targetDef = targets.get(3);
		assertTrue(targetDef.isRecurse());
		final String excludePattern = targetDef.getExcludePattern();
		assertNotNull(excludePattern);
		assertEquals("Exclude this:Exclude that", excludePattern);
		final String includePattern = targetDef.getIncludePattern();
		assertNotNull(includePattern);
		assertEquals("include this:include that", includePattern);

		targetDef = targets.get(4);
		checkTarget2(targetDef);
	}

	@Test
	public void readMultipleDirectories() throws IOException, URISyntaxException {
		final File targetsFile = prepareTargetDefFile("/Test_DS_Targets.json");
		final List<TargetDef> targets = TargetDef.readTargetsFile(targetsFile, Collections.emptyMap());
        assertEquals(targets.size(), 5);
		final TargetDef targetDef = targets.get(0);
        TargetDef.FilePair  filePair = targetDef.getFilePair();
        assertNotNull(filePair);
        assertEquals(new File(testDirA), filePair.getFileA());
        assertEquals(new File(testDirB), filePair.getFileB());

        filePair = targetDef.getFilePair();
        assertNotNull(filePair);
        assertEquals(new File(testDirB), filePair.getFileA());
        assertEquals(new File(testDirC), filePair.getFileB());

        filePair = targetDef.getFilePair();
        assertNotNull(filePair);
        assertEquals(new File(testDirC), filePair.getFileA());
        assertEquals(new File(testDirA), filePair.getFileB());

        filePair = targetDef.getFilePair();
        assertNull(filePair);
	}

	@Test
	public void readMultipleDirectoriesUnidirectional() throws IOException, URISyntaxException {
		final File targetsFile = prepareTargetDefFile("/Test_DS_Targets_unidirectional.json");
		final List<TargetDef> targets = TargetDef.readTargetsFile(targetsFile, Collections.emptyMap());
        assertEquals(targets.size(), 2);
		assertFalse(targets.get(0).isUnidirectional());
		final TargetDef targetDef = targets.get(1);
		assertTrue(targetDef.isUnidirectional());
        TargetDef.FilePair  filePair = targetDef.getFilePair();
        assertNotNull(filePair);
        assertEquals(new File(testDirA), filePair.getFileA());
        assertEquals(new File(testDirB), filePair.getFileB());

        filePair = targetDef.getFilePair();
        assertNotNull(filePair);
        assertEquals(new File(testDirB), filePair.getFileA());
        assertEquals(new File(testDirC), filePair.getFileB());

        filePair = targetDef.getFilePair();
        assertNull(filePair);
	}

	private File prepareTargetDefFile(final String name) throws URISyntaxException, IOException {
		resetTestDirsNames();
		final File inFile = new File(getClass().getResource(name).toURI());
		final File outFile = createTempJsonFile();
		final String test_dirAName = createTestDirAndGetName("Test_dirA");
		final String test_dirBName = createTestDirAndGetName("Test_dirB");
		final String test_dirCName = createTestDirAndGetName("Test_dirC");
		try (PrintWriter writer = new PrintWriter(outFile)) {
			Files.lines(inFile.toPath()).forEach(line -> writer.println(line.replace(testDirA, test_dirAName)
					.replace(testDirB, test_dirBName)
					.replace(testDirC, test_dirCName)));
		}
		setTestDirsNames(test_dirAName, test_dirBName, test_dirCName);
		return outFile;
	}

	private File adjustIncludedJsons(final File inFile) throws IOException, URISyntaxException {
		resetTestDirsNames();
		final File outFile = createTempJsonFile();
		final File includedFile1 = createTempJsonFile();
		final File includedFile2 = createTempJsonFile();
		final File includedSourceFile1 = new File(getClass().getResource("/Test_DS_Targets_Imported1.json").toURI());
		final File includedSourceFile2 = new File(getClass().getResource("/Test_DS_Targets_Imported2.json").toURI());
		final String test_dirAName = createTestDirAndGetName("Test_dirA");
		final String test_dirBName = createTestDirAndGetName("Test_dirB");

		try (PrintWriter writer = new PrintWriter(includedFile1)) {
			Files.lines(includedSourceFile1.toPath()).forEach(line -> writer.println(line.replace(testDirA, test_dirAName).replace(testDirB, test_dirBName)));
		}
		try (PrintWriter writer = new PrintWriter(includedFile2)) {
			Files.lines(includedSourceFile2.toPath()).forEach(line -> writer.println(line.replace(testDirA, test_dirAName).replace(testDirB, test_dirBName)));
		}
		try (PrintWriter writer = new PrintWriter(outFile)) {
			Files.lines(inFile.toPath()).forEach(line -> writer.println(line.replace("Test_DS_Targets_Imported1.json", includedFile1.getName())
					.replace("Test_DS_Targets_Imported2.json", includedFile2.getName())));
		}
		setTestDirsNames(test_dirAName, test_dirBName, null);
		return outFile;
	}

	private File adjustRecursiveJsons() throws IOException, URISyntaxException {
		final File inFile1 = new File(getClass().getResource("/Test_DS_Targets_Recursive1.json").toURI());
		final File outFile1 = createTempJsonFile();
		final File inFile2 = new File(getClass().getResource("/Test_DS_Targets_Recursive2.json").toURI());
		final File outFile2 = createTempJsonFile();
		final File inFile3 = new File(getClass().getResource("/Test_DS_Targets_Recursive3.json").toURI());
		final File outFile3 = createTempJsonFile();

		try (PrintWriter writer = new PrintWriter(outFile1)) {
			Files.lines(inFile1.toPath()).forEach(line -> writer.println(line
					.replace("Test_DS_Targets_Recursive2.json", outFile2.getAbsolutePath().replaceAll(REGEX_BACKSLASH, "/"))
					.replace("Test_DS_Targets_Recursive3.json", outFile3.getAbsolutePath().replaceAll(REGEX_BACKSLASH, "/"))));
		}

		try (PrintWriter writer = new PrintWriter(outFile2)) {
			Files.lines(inFile2.toPath()).forEach(line -> writer.println(line
					.replace("Test_DS_Targets_Recursive1.json", outFile1.getAbsolutePath().replaceAll(REGEX_BACKSLASH, "/"))
					.replace("Test_DS_Targets_Recursive3.json", outFile3.getAbsolutePath().replaceAll(REGEX_BACKSLASH, "/"))));
		}

		resetTestDirsNames();
		final String test_dirAName = createTestDirAndGetName("Test_dirA");
		final String test_dirBName = createTestDirAndGetName("Test_dirB");
		final String test_dirCName = createTestDirAndGetName("Test_dirC");
		try (PrintWriter writer = new PrintWriter(outFile3)) {
			Files.lines(inFile3.toPath()).forEach(line -> writer.println(line.replace(testDirA, test_dirAName)
					.replace(testDirB, test_dirBName)
					.replace(testDirC, test_dirCName)));
		}

		return outFile1;
	}

	private static File createTempJsonFile() throws IOException {
		final File outFile = File.createTempFile("Test_DS_Targets", ".json");
		outFile.deleteOnExit();
		return outFile;
	}

	private void resetTestDirsNames() {
		testDirA = _TEST_DIR_A;
		testDirB = _TEST_DIR_B;
		testDirC = _TEST_DIR_C;
	}

	private void setTestDirsNames(final String s1, final String s2, final String s3) {
		testDirA = s1;
		testDirB = s2;
		testDirC = s3;
	}

	private static String createTestDirAndGetName(final String testDirBaseName) throws IOException {
		final Path testDir = Files.createTempDirectory(testDirBaseName);
		testDir.toFile().deleteOnExit();
		return testDir.toString().replaceAll(REGEX_BACKSLASH, "/");
	}

	private static void checkTarget2(final TargetDef targetDef) {
		final String[] files = targetDef.getFiles();
		assertNotNull(files);
		assertEquals(3, files.length);
		assertEquals("Directorysynchronizer.jar", files[0]);
		assertEquals("Agenda.jar", files[1]);
		assertEquals("Alter.jar", files[2]);
	}

	private void checkTarget1(final TargetDef targetDef) {
		assertFalse(targetDef.isRecurse());
		final TargetDef.FilePair filePair = targetDef.getFilePair();
        assertNotNull(filePair);
		assertEquals(new File(testDirA), filePair.getFileA());
		assertEquals(new File(testDirB), filePair.getFileB());
		assertEquals("target/", targetDef.getExcludePattern());
		assertEquals("includeThis", targetDef.getIncludePattern());
	}

	@Test
	public void testGetFilePair2() {
		final TargetDef targetDef = new TargetDef(false, false, null, null, null, file1, file2);
		final TargetDef.FilePair filePair = targetDef.getFilePair();
		assertNotNull(filePair);
	}

	@Test
	public void testHasNextFiles() {
		final TargetDef targetDef = new TargetDef(false, false, null, null, null, file1, nonexistentFile);
		assertTrue(targetDef.hasNext());
		targetDef.next();
		assertFalse(targetDef.hasNext());
	}

	@Test
	public void testNextFiles() {
		final TargetDef targetDef = new TargetDef(false, false, null, null, null, file1, nonexistentFile);
		final File[] files = targetDef.next();
		assertNotNull(files);
		assertEquals(files[0], file1);
		assertEquals(files[1], nonexistentFile);
	}

	@Test
	public void testHasNextDirsNotRecurse() throws Exception {
		final String aDirCanonicalPath = aDir.getCanonicalPath();
		final String bDirCanonicalPath = bDir.getCanonicalPath();
		final TargetDef targetDef = new TargetDef(false, false, null, null, null, aDir, bDir);
		targetDef.resetPairsCounter();
		assertNotNull(targetDef.getFilePair());
		assertTrue(targetDef.hasNext());
		File[] files = targetDef.next();
		String canonicalPathSource = files[0].getCanonicalPath();
		String canonicalPathTarget = files[1].getCanonicalPath();
		assertTrue(canonicalPathSource.startsWith(aDirCanonicalPath));
		assertTrue(canonicalPathTarget.startsWith(bDirCanonicalPath));
		assertEquals(canonicalPathSource, replaceParent(canonicalPathTarget, bDirCanonicalPath, aDirCanonicalPath));
		assertTrue(targetDef.hasNext());
		files = targetDef.next();
		canonicalPathSource = files[0].getCanonicalPath();
		canonicalPathTarget = files[1].getCanonicalPath();
		assertTrue(canonicalPathSource.startsWith(bDirCanonicalPath));
		assertTrue(canonicalPathTarget.startsWith(aDirCanonicalPath));
		assertEquals(canonicalPathSource, replaceParent(canonicalPathTarget, aDirCanonicalPath, bDirCanonicalPath));
		assertFalse(targetDef.hasNext());
	}

	@Test
	public void testHasNextDirsRecurseWithoutFilter() throws Exception {
		final String aDirCanonicalPath = aDir.getCanonicalPath();
		final String bDirCanonicalPath = bDir.getCanonicalPath();
		final TargetDef targetDef = new TargetDef(true, false, null, null, null, aDir, bDir);
		targetDef.resetPairsCounter();
		assertNotNull(targetDef.getFilePair());
		assertTrue(targetDef.hasNext());

		File[] files = targetDef.next();
		String canonicalPathSource = files[0].getCanonicalPath();
		String canonicalPathTarget = files[1].getCanonicalPath();
		assertTrue(canonicalPathSource.startsWith(aDirCanonicalPath));
		assertTrue(canonicalPathTarget.startsWith(bDirCanonicalPath));
		assertEquals(canonicalPathSource, replaceParent(canonicalPathTarget, bDirCanonicalPath, aDirCanonicalPath));

		assertTrue(targetDef.hasNext());

		files = targetDef.next();
		canonicalPathSource = files[0].getCanonicalPath();
		canonicalPathTarget = files[1].getCanonicalPath();
		assertTrue(canonicalPathSource.startsWith(aDirCanonicalPath));
		assertTrue(canonicalPathTarget.startsWith(bDirCanonicalPath));
		assertEquals(canonicalPathSource, replaceParent(canonicalPathTarget, bDirCanonicalPath, aDirCanonicalPath));

		files = targetDef.next();
		canonicalPathSource = files[0].getCanonicalPath();
		canonicalPathTarget = files[1].getCanonicalPath();
		assertTrue(canonicalPathSource.startsWith(aDirCanonicalPath));
		assertTrue(canonicalPathTarget.startsWith(bDirCanonicalPath));
		assertEquals(canonicalPathSource, replaceParent(canonicalPathTarget, bDirCanonicalPath, aDirCanonicalPath));

		files = targetDef.next();
		canonicalPathSource = files[0].getCanonicalPath();
		canonicalPathTarget = files[1].getCanonicalPath();
		assertTrue(canonicalPathSource.startsWith(aDirCanonicalPath));
		assertTrue(canonicalPathTarget.startsWith(bDirCanonicalPath));
		assertEquals(canonicalPathSource, replaceParent(canonicalPathTarget, bDirCanonicalPath, aDirCanonicalPath));

		files = targetDef.next();
		canonicalPathSource = files[0].getCanonicalPath();
		canonicalPathTarget = files[1].getCanonicalPath();
		assertTrue(canonicalPathSource.startsWith(bDirCanonicalPath));
		assertTrue(canonicalPathTarget.startsWith(aDirCanonicalPath));
		assertEquals(canonicalPathSource, replaceParent(canonicalPathTarget, aDirCanonicalPath, bDirCanonicalPath));

		assertFalse(targetDef.hasNext());
	}

	private static String replaceParent(final String canonicalPath, final String oldCanonicalParent, final String newCanonicalParent) {
		return newCanonicalParent + canonicalPath.substring(oldCanonicalParent.length());
	}

	@Test
	public void testHasNextDirsRecurseWithFilter() throws Exception {
		final String aDirCanonicalPath = aDir.getCanonicalPath();
		final String bDirCanonicalPath = bDir.getCanonicalPath();
		final TargetDef targetDef = new TargetDef(true, false, "CVS/", null, null, aDir, bDir);
		targetDef.resetPairsCounter();
		assertNotNull(targetDef.getFilePair());
		assertTrue(targetDef.hasNext());
		File[] files = targetDef.next();
		assertTrue(files[0].getCanonicalPath().startsWith(aDirCanonicalPath));
		assertTrue(targetDef.hasNext());
		files = targetDef.next();
		assertTrue(files[0].getCanonicalPath().startsWith(aDirCanonicalPath));
		files = targetDef.next();
		assertTrue(files[0].getCanonicalPath().startsWith(aDirCanonicalPath));
		files = targetDef.next();
		assertTrue(files[0].getCanonicalPath().startsWith(bDirCanonicalPath));
		assertFalse(targetDef.hasNext());
	}

	@Test
	public void testNext() {
		final TargetDef targetDef = new TargetDef(false, false, null, null, null, aDir, bDir);
        if (targetDef.hasNext()) {
            final File[] files1 = targetDef.next();
            assertTrue(files1[0].exists());
            assertFalse(files1[1].exists());
            if (targetDef.hasNext()) {
                final File[] files2 = targetDef.next();
                assertTrue(files2[0].exists());
                assertFalse(files2[1].exists());
            }
        }
	}

	@Test
	public void testSynchronize() {
		final TargetDef targetDef = new TargetDef(false, false, null, null, null, aDir, bDir);
		targetDef.resetPairsCounter();
		assertNotNull(targetDef.getFilePair());
		final File[] files1 = targetDef.next();
		final File[] files2 = targetDef.next();
		assertTrue(files1[0].exists());
		assertFalse(files1[1].exists());
		assertTrue(files2[0].exists());
		assertFalse(files2[1].exists());
		final DirectorySynchronizer synchronizer = new DirectorySynchronizer();
		synchronizer.synchronizeDirectories(new TargetDef(false, false, null, null, null, aDir, bDir));	// New because the cursor of the other is exhausted
		assertTrue(files1[1].exists());
		assertTrue(files2[1].exists());
	}

	@Test
	public void testInclude() throws URISyntaxException, IOException {
		File targetsFile = prepareTargetDefFile("/Test_DS_Targets_Inclusive1.json");
		targetsFile = adjustIncludedJsons(targetsFile);
		final List<TargetDef> targets = TargetDef.readTargetsFile(targetsFile, Collections.emptyMap());
        assertEquals(targets.size(), 2);
		TargetDef targetDef = targets.get(0);
		checkTarget2(targetDef);
		targetDef = targets.get(1);
		checkTarget1(targetDef);
	}

	@Test
	public void testRecurse() throws URISyntaxException, IOException {
		final File targetsFile = adjustRecursiveJsons();
		final List<TargetDef> targets = TargetDef.readTargetsFile(targetsFile, Collections.emptyMap());
        assertEquals(targets.size(), 4);
	}

	@Test(expectedExceptions = FileNotFoundException.class)
	public void testBadInclude() throws URISyntaxException, IOException {
		TargetDef.readTargetsFile(new File(getClass().getResource("/Test_DS_Targets_BadInclusive.json").toURI()), Collections.emptyMap());
	}

	@Test
	public void testGetDirectories() {
		final TargetDef targetDef = new TargetDef(false, false, null, null, null, aDir, bDir);
		final File[] directories = targetDef.getDirectories();
		assertEquals(directories, new File[] {aDir, bDir});
	}

	@AfterMethod
	public void cleanTree() {
		DirectoryCleaner.delTreesWithFiles(createdDirs);
	}
}
