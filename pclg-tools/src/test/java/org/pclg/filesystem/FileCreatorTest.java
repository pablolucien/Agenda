package org.pclg.filesystem;

import org.pclg.tools.FileTools;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class FileCreatorTest {
    private File dir;

    @BeforeMethod
    public void setUp() throws IOException {
        dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
    }

    @Test
    public void testCreateFile() throws Exception {
        final File file2Create = new File(dir, "file2Create");
        assertFalse(file2Create.exists());
        final int size = 1_000_000;
        FileCreator.createFile(file2Create.getAbsolutePath(), size, false);
        assertTrue(file2Create.exists());
        assertEquals(size, file2Create.length());
		assertTrue(file2Create.delete());
    }

    @Test
    public void testCreateAndOverwriteFile() throws Exception {
        final File file2Create = new File(dir, "file2CreateAndOverwrite");
        assertFalse(file2Create.exists());
        final int size = 1_000_000;
        FileCreator.createFile(file2Create.getAbsolutePath(), size, true);
        assertTrue(file2Create.exists());
        assertEquals(size, file2Create.length());
		assertTrue(file2Create.delete());
    }

    @Test()
    public void divisionWithException() throws IOException {
        final File file2Create = new File(dir, "file2CreateAndOverwrite");
        try {
            FileTools.overwriteFile(file2Create);
            fail("IllegalStateException expected");
        } catch (final IllegalStateException ex) {
            //Expected
        }
    }
}