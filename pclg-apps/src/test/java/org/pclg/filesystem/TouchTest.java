package org.pclg.filesystem;

import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;

import static org.pclg.filesystem.Touch.INCREMENT_TIME;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.FileAssert.assertLength;
/**
 * @since 27/07/2017.
 */
public class TouchTest {
    @Test
    public void testMain1() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile = new File(dir, "testFile");
        testFile.deleteOnExit();
        assertFalse(testFile.exists(), "It shouldn't exist yet");
        final Calendar calendar = Calendar.getInstance();
        final String testFilePath = testFile.getAbsolutePath();
        Touch.main(new String[] {testFilePath});
        assertTrue(testFile.exists(), "Now it should exist");
        assertLength(testFile, 0, "It should be empty.");
        assertEquals(testFile.lastModified() / 10000, calendar.getTime().getTime() / 10000, "Should have same date (to second)");
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.YEAR, 1980);
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 34);
        calendar.set(Calendar.SECOND, 56);
        calendar.set(Calendar.MILLISECOND, 0);
        Touch.main(new String[] {"-d", "01/01/1980", "-t", "13:34:56", testFilePath});
        assertEquals(testFile.lastModified(), calendar.getTime().getTime(),
                "Should have same date (to millisecond): " + new Date(testFile.lastModified()) + " <> " + calendar.getTime());
    }

    @Test
    public void testTouchWithoutIncrement() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile1 = new File(dir, "testFile1");
        final File testFile2 = new File(dir, "testFile2");
        testFile1.deleteOnExit();
        testFile2.deleteOnExit();
        final String testFile1Path = testFile1.getAbsolutePath();
        final String testFile2Path = testFile2.getAbsolutePath();
        Touch.main(new String[] {testFile1Path, testFile2Path});
        assertEquals(testFile1.lastModified(), testFile2.lastModified(), "Should have same date");
    }

    @Test
    public void testTouchWithIncrement() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile1 = new File(dir, "testFile1");
        final File testFile2 = new File(dir, "testFile2");
        testFile1.deleteOnExit();
        testFile2.deleteOnExit();
        final String testFile1Path = testFile1.getAbsolutePath();
        final String testFile2Path = testFile2.getAbsolutePath();
        Touch.main(new String[] {"-i", testFile1Path, testFile2Path});
        assertEquals(testFile1.lastModified() + INCREMENT_TIME, testFile2.lastModified(), "Dates should differ by INCREMENT_TIME");
    }

    @Test(enabled = false)
    public void touchFile() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile = new File("D:\\Profiles\\PACELU~1\\AppData\\Local\\Temp\\___", "Pan sin horno hecho en sarte?n �Solo 2 ingredientes!");
        testFile.deleteOnExit();
        final String testFilePath = testFile.getAbsolutePath();
        Touch.main(new String[] {testFilePath});
        assertTrue(testFile.exists(), "Now it should exist");
    }
}