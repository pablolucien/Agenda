package org.pclg.filesystem.synchonizer;

import org.pclg.tools.FileTools;
import org.pclg.tools.StringTools;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class OnlyIncludedFilesIteratorTest {
    private final File[] dirs = {new File("dir1"), new File("dir2"), new File("dir3"), new File("dir4")};
    private final String[] fileNames = {"file1", "file2", "file3"};
    private final int nrPairs = dirs.length * fileNames.length;
    private OnlyIncludedFilesIterator iterator;

    @BeforeMethod
    public void setUp() {
        iterator = new OnlyIncludedFilesIterator(dirs, fileNames);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructor_should_fail_1() {
        new OnlyIncludedFilesIterator(dirs, StringTools.EMPTY_STRING_ARRAY);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructor_should_fail_2() {
        new OnlyIncludedFilesIterator(FileTools.NULL_FILE_ARRAY, fileNames);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructor_should_fail_3() {
        new OnlyIncludedFilesIterator(new File[1], fileNames);
    }
    
    @Test
    public void testHasNext() {
        for (int ii = 0; ii < nrPairs; ii++) {
            assertTrue(iterator.hasNext());
            iterator.next();
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testNext() {
        File[] files = iterator.next();
        assertEquals(files[0], new File(dirs[0], fileNames[0]));
        assertEquals(files[1], new File(dirs[1], fileNames[0]));
        for (int ii = 1; ii < nrPairs - 1; ii++) {
            iterator.next();
        }
        files = iterator.next();
        assertEquals(files[0], new File(dirs[dirs.length - 1], fileNames[fileNames.length - 1]));
        assertEquals(files[1], new File(dirs[0], fileNames[fileNames.length - 1]));
        assertFalse(iterator.hasNext());
    }
}