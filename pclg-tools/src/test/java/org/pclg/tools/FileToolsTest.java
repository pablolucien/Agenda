package org.pclg.tools;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.BiConsumer;

import static java.io.File.separator;
import static org.pclg.filesystem.FileCreator.createFile;
import static org.pclg.tools.FileTools.compareContents;
import static org.pclg.tools.FileTools.copyFile;
import static org.pclg.tools.FileTools.readFromFile;
import static org.pclg.tools.FileTools.splitInComponents;
import static org.pclg.tools.FileTools.writeToFile;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

/**
 * @since 31/07/2017.
 */
public class FileToolsTest {
    private static File originalFile;
    private static File goodCopy;
    private static File badCopy;

    @BeforeMethod
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
        assertTrue(compareContents(originalFile, goodCopy), "Should be equal.");
        assertFalse(compareContents(originalFile, badCopy), "Shouldn't be equal.");
    }

    @DataProvider
    public Object[][] names() {
        return new Object[][] {
            {"kkk.data", "kkk", "data"},
            {"kkk", "kkk", ""} ,
            {"kkk.", "kkk.", ""} ,
            {".kkk", ".kkk", ""} ,
        };
    }

    @Test(dataProvider = "names")
    public void splittName(final String name, final String base, final String ext) {
        final FileTools.SplittedName splittedName = FileTools.splittName(name);
        assertEquals(base, splittedName.base, "Should be equal.");
        assertEquals(ext, splittedName.extension, "Should be equal.");
    }

    @Test
    public void sanitizeWindowsFilename() {
        final String invalidWindowsFilename = "*file\"name\"?";
        assertEquals("filename", FileTools.sanitizeWindowsFilename(invalidWindowsFilename, ""), "Should be equal.");
        assertEquals("_file_name__", FileTools.sanitizeWindowsFilename(invalidWindowsFilename, "_"), "Should be equal.");
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

    @Test
    public void testCycleFile_with_extension() throws IOException {
        final File file = File.createTempFile("testCycleFile", ".ext");
        final File dir = file.getParentFile();
        final String name = file.getName();
        final FileTools.SplittedName splittedName = FileTools.splittName(name);
        final File file1 = new File(dir, splittedName.base + "_1." + splittedName.extension);
        final File file2 = new File(dir, splittedName.base + "_2." + splittedName.extension);
        final File file3 = new File(dir, splittedName.base + "_3." + splittedName.extension);
        final File file4 = new File(dir, splittedName.base + "_4." + splittedName.extension);
        checkCycle(file, file1, file2, file3, file4);
    }

    @Test
    public void testCycleFile_without_extension() throws IOException {
        final File file = File.createTempFile("testCycleFile", "");
        final File dir = file.getParentFile();
        final String name = file.getName();
        final FileTools.SplittedName splittedName = FileTools.splittName(name);
        final File file1 = new File(dir, splittedName.base + "_1" + splittedName.extension);
        final File file2 = new File(dir, splittedName.base + "_2" + splittedName.extension);
        final File file3 = new File(dir, splittedName.base + "_3" + splittedName.extension);
        final File file4 = new File(dir, splittedName.base + "_4" + splittedName.extension);
        checkCycle(file, file1, file2, file3, file4);
    }

    @Test
    public void testCycleFile_ending_in_point() throws IOException {
        final File file = File.createTempFile("testCycleFile", ".");
        final File dir = file.getParentFile();
        final String name = file.getName();
        final FileTools.SplittedName splittedName = FileTools.splittName(name);
        // splittedName.base contains the trailing point
        final File file1 = new File(dir, splittedName.base + "_1");
        final File file2 = new File(dir, splittedName.base + "_2");
        final File file3 = new File(dir, splittedName.base + "_3");
        final File file4 = new File(dir, splittedName.base + "_4");
        checkCycle(file, file1, file2, file3, file4);
    }

    private void checkCycle(final File file, final File file1, final File file2, final File file3, final File file4)
            throws IOException {
        assertTrue(file.exists());
        assertFalse(file1.exists());
        assertFalse(file2.exists());
        assertFalse(file3.exists());
        assertFalse(file4.exists());

        // cycle one time
        FileTools.cycleFile(file, 3);
        assertFalse(file.exists());
        assertTrue(file1.exists());
        assertFalse(file2.exists());
        assertFalse(file3.exists());
        assertFalse(file4.exists());

        // nothing should change if file doesn't exist
        FileTools.cycleFile(file, 3);
        assertFalse(file.exists());
        assertTrue(file1.exists());
        assertFalse(file2.exists());
        assertFalse(file3.exists());
        assertFalse(file4.exists());

        // create file and cycle one time
        FileTools.copyFile(file1, file);
        assertTrue(file.exists());
        FileTools.cycleFile(file, 3);
        assertFalse(file.exists());
        assertTrue(file1.exists());
        assertTrue(file2.exists());
        assertFalse(file3.exists());
        assertFalse(file4.exists());

        // create file and cycle one time
        FileTools.copyFile(file1, file);
        assertTrue(file.exists());
        FileTools.cycleFile(file, 3);
        assertFalse(file.exists());
        assertTrue(file1.exists());
        assertTrue(file2.exists());
        assertTrue(file3.exists());
        assertFalse(file4.exists());

        // create file and cycle one time the limit should be respected
        FileTools.copyFile(file1, file);
        assertTrue(file.exists());
        FileTools.cycleFile(file, 3);
        assertFalse(file.exists());
        assertTrue(file1.exists());
        assertTrue(file2.exists());
        assertTrue(file3.exists());
        assertFalse(file4.exists());
    }
}