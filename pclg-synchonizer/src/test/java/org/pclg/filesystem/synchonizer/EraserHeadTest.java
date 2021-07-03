package org.pclg.filesystem.synchonizer;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.pclg.filesystem.synchonizer.DirectorySynchronizer.ADD_DIR_PAD;
import static org.pclg.filesystem.synchonizer.DirectorySynchronizer.ADD_FILE_PAD;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class EraserHeadTest {
    final String testDirName = "/tmp/EraserHeadTest";
    final File testDir = new File(testDirName);
    final File testDirFirstParent = new File(testDirName + "/tmp");
    final File testDirLastLeaf = new File(testDirName + "/tmp/la/donna/e/mobile");

    @Test
    public void testProcessStream_files_and_dirs() throws IOException {
        testDirLastLeaf.mkdirs();
        final File testFile = new File(testDirLastLeaf, "kk");
        testFile.createNewFile();
        assertTrue(testDirLastLeaf.exists());
        assertTrue(testFile.exists());
        final Stream<String> lines = Arrays.stream(
            new String[] {
                ADD_DIR_PAD + testDirName + "/tmp",
                ADD_DIR_PAD + testDirName + "/tmp/la",
                ADD_DIR_PAD + testDirName + "/tmp/la/donna",
                ADD_DIR_PAD + testDirName + "/tmp/la/donna/e",
                ADD_DIR_PAD + testDirName + "/tmp/la/donna/e/mobile",
                ADD_FILE_PAD + testDirName + "/tmp/la/donna/e/mobile/kk"
            });

        final String[] baseDirs = {testDirName};
        final boolean onlyTest = false;
        EraserHead.processStream(lines, baseDirs, onlyTest);
        assertFalse(testFile.exists());
        assertFalse(testDirFirstParent.exists());
        assertTrue(testDir.exists());
    }

    @Test
    public void testProcessStream_only_dirs() throws IOException {
        testDirLastLeaf.mkdirs();
        assertTrue(testDirLastLeaf.exists());
        final Stream<String> lines = Arrays.stream(
            new String[] {
                ADD_DIR_PAD + testDirName + "/tmp",
                ADD_DIR_PAD + testDirName + "/tmp/la",
                ADD_DIR_PAD + testDirName + "/tmp/la/donna",
                ADD_DIR_PAD + testDirName + "/tmp/la/donna/e",
                ADD_DIR_PAD + testDirName + "/tmp/la/donna/e/mobile",
            });

        final String[] baseDirs = {testDirName};
        final boolean onlyTest = false;
        EraserHead.processStream(lines, baseDirs, onlyTest);
        assertFalse(testDirFirstParent.exists());
        assertTrue(testDir.exists());
    }

    @Test
    public void testProcessStream_only_files() throws IOException {
        testDirLastLeaf.mkdirs();
        final File testFile = new File(testDirLastLeaf, "kk");
        testFile.createNewFile();
        assertTrue(testDirLastLeaf.exists());
        assertTrue(testFile.exists());
        final Stream<String> lines = Arrays.stream(
            new String[] {
                ADD_FILE_PAD + testDirName + "/tmp/la/donna/e/mobile/kk"
            });

        final String[] baseDirs = {testDirName};
        final boolean onlyTest = false;
        EraserHead.processStream(lines, baseDirs, onlyTest);
        assertFalse(testFile.exists());
        assertTrue(testDirLastLeaf.exists());
        assertTrue(testDir.exists());
    }
}