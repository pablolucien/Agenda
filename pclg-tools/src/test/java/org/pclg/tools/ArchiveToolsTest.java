package org.pclg.tools;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.testng.Assert.assertEquals;

public class ArchiveToolsTest {
    private static final byte[] MANIFEST = "This is a nice MANIFEST".getBytes();

    @Test(enabled = false)
    public void testCreateZipWithManifest() throws IOException {
        final File targetZipFile = new File("/tmp/ArchiveToolsTestWithManifest.zip");
        Files.deleteIfExists(targetZipFile.toPath());
        ArchiveTools.createZip(new File("/home/pablo/databases/portfolio"), targetZipFile, MANIFEST);
        final byte[] manifestRead = ArchiveTools.readManifest(targetZipFile);
        assertEquals(manifestRead, MANIFEST);
    }

    @Test(enabled = false)
    public void testCreateZip() throws IOException {
        final File targetZipFile = new File("/tmp/ArchiveToolsTest.zip");
        Files.deleteIfExists(targetZipFile.toPath());
        ArchiveTools.createZip(new File("/home/pablo/databases/portfolio"), targetZipFile);
    }

    @Test(enabled = false)
    public void testExtractZip() throws IOException {
        final File sourceZipFile = new File("/home/pablo/Finance/Inversiones/portfolio.zip");
        final File targetDir = new File("/tmp/ArchiveToolsTestExtractedDir");
        if (targetDir.exists()) {
            FileTools.delTree(targetDir, true, true);
        }
        ArchiveTools.extractZip(sourceZipFile, targetDir);
    }
}