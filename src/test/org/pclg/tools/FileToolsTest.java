package org.pclg.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.BiConsumer;
import org.junit.Before;
import org.junit.Test;


import static java.io.File.separator;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.pclg.filesystem.FileCreator.createFile;
import static org.pclg.tools.FileTools.compareContents;
import static org.pclg.tools.FileTools.copyFile;
import static org.pclg.tools.FileTools.readFromFile;
import static org.pclg.tools.FileTools.splitInComponents;
import static org.pclg.tools.FileTools.writeToFile;

/**
 * @since 31/07/2017.
 */
public class FileToolsTest {
	private static File originalFile;
	private static File goodCopy;
	private static File badCopy;

	@Before
	public void setUp() throws IOException {
		final File dir = Files.createTempDirectory(null).toFile();
		dir.deleteOnExit();
		originalFile = new File(dir, "originalFile");
		originalFile.deleteOnExit();
		createFile(originalFile.getAbsolutePath(), 1_000_000, false);
		goodCopy = new File(dir, "goodCopy");
		goodCopy.deleteOnExit();
		copyFile(originalFile, goodCopy);
		badCopy = new File(dir, "badCopy");
		badCopy.deleteOnExit();
		copyFile(originalFile, badCopy);
		final byte[] bytes = readFromFile(badCopy);
		final int pos = bytes.length / 2;
		bytes[pos] = (byte) ~bytes[pos];
		writeToFile(bytes, badCopy, true);
	}

	@Test
	public void testSplitInComponents() {
		final String home = "home";
		final String users = "users";
		final String coyote = "ElCoyote";
		final String docs = "docs";
		final String pathname = separator + home + separator + users + separator + coyote + separator + docs;
		final List<File> components = splitInComponents(new File(pathname));
		int index = -1;
		assertEquals(components.get(++index), new File(separator));
		assertEquals(components.get(++index), new File(separator + home));
		assertEquals(components.get(++index), new File(separator + home + separator + users));
		assertEquals(components.get(++index), new File(separator + home + separator + users + separator + coyote));
		assertEquals(components.get(++index), new File(separator + home + separator + users + separator + coyote + separator + docs));
	}

	@Test
	public void sonIguales() {
		assertTrue("Should be equal.", compareContents(originalFile, goodCopy));
		assertFalse("Shouldn't be equal.", compareContents(originalFile, badCopy));
	}

	@Test
	public void splittName() {
		FileTools.SplittedName splittedName = FileTools.splittName("kkk.data");
		assertEquals("Should be equal.", "kkk", splittedName.base);
		assertEquals("Should be equal.", "data", splittedName.extension);
		splittedName = FileTools.splittName("kkk");
		assertEquals("Should be equal.", "kkk", splittedName.base);
		assertEquals("Should be equal.", "", splittedName.extension);
		splittedName = FileTools.splittName("kkk.");
		assertEquals("Should be equal.", "kkk.", splittedName.base);
		assertEquals("Should be equal.", "", splittedName.extension);
		splittedName = FileTools.splittName(".kkk");
		assertEquals("Should be equal.", ".kkk", splittedName.base);
		assertEquals("Should be equal.", "", splittedName.extension);
	}

	@Test
	public void sanitizeWindowsFilename() {
		final String invalidWindowsFilename = "*file\"name\"?";
		assertEquals("Should be equal.", "filename", FileTools.sanitizeWindowsFilename(invalidWindowsFilename, ""));
		assertEquals("Should be equal.", "_file_name__", FileTools.sanitizeWindowsFilename(
			invalidWindowsFilename, "_"));
	}

	@Test
	public void convertFileInPlace() throws IOException {
		final File testFile = File.createTempFile("test", ".txt");
		testFile.deleteOnExit();
		final String text = "La donna è mobile";
		final BiConsumer<File, File> toUppercaseConverter = (in, out) -> {
			try {
				writeToFile(new String(readFromFile(in)).toUpperCase().getBytes(), out, true);
			} catch (final IOException ex) {
				throw new RuntimeException(ex);
			}
		};
		FileTools.writeToFile(text.getBytes(), testFile, true);
		FileTools.convertFileInPlace(testFile, toUppercaseConverter);
		assertArrayEquals(text.toUpperCase().getBytes(), FileTools.readFromFile(testFile));
	}
}
