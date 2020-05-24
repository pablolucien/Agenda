package org.pclg.filesystem;

import org.junit.Before;
import org.junit.Test;
import org.pclg.tools.FileTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;


public class FileCreatorTest {
    private File dir;

    @Before
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