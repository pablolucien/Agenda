package org.pclg.tools;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ArchiveToolsTest {
    private static final byte[] MANIFEST = "This is a nice MANIFEST".getBytes();
    private File sourceDir;

    @BeforeClass
    public void setUp() throws URISyntaxException {
        sourceDir = new File(Objects.requireNonNull(getClass().getResource("/sounds")).toURI());
    }

    @Test
    public void testCreateZipWithManifest() throws IOException {
        final File targetZipFile = new File("/tmp/ArchiveToolsTestWithManifest.zip");
        Files.deleteIfExists(targetZipFile.toPath());
        ArchiveTools.createZip(sourceDir, targetZipFile, MANIFEST);
        final byte[] manifestRead = ArchiveTools.readManifest(targetZipFile);
        assertEquals(manifestRead, MANIFEST);
    }

    @Test
    public void testCreateZip() throws IOException {
        final File targetZipFile = new File("/tmp/ArchiveToolsTest.zip");
        Files.deleteIfExists(targetZipFile.toPath());
        ArchiveTools.createZip(sourceDir, targetZipFile);
    }

    @Test(expectedExceptions = {ArchiveToolsException.class})
    public void testCreateZip_targetDirDoesNotExist_should_fail() throws IOException {
        final File targetZipFile = new File("/tmp/kkk/XXX/ArchiveToolsTest.zip");
        Files.deleteIfExists(targetZipFile.toPath());
        ArchiveTools.createZip(sourceDir, targetZipFile);
    }

    @Test
    public void testExtractZip() throws URISyntaxException {
        final File sourceZipFile = new File(Objects.requireNonNull(getClass().getResource("/ArchiveToolsTest.zip")).toURI());
        final File targetDir = new File("/tmp/ArchiveToolsTestExtractedDir");
        final File targetExtractedFile = new File(targetDir, "Chrono.properties");
        if (targetDir.exists()) {
            FileTools.delTree(targetDir, true, true);
        }
        assertFalse(targetDir.exists());
        assertFalse(targetExtractedFile.exists());
        ArchiveTools.extractZip(sourceZipFile, targetDir);
        assertTrue(targetDir.exists());
        assertTrue(targetExtractedFile.exists());
    }
}