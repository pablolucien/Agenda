package org.pclg.filesystem;

import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.APRIL;
import static java.util.Calendar.JULY;
import static org.pclg.filesystem.Touch.INCREMENT_TIME;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.FileAssert.assertLength;

/**
 * @since 27/07/2017.
 */
public class TouchTest {
    private final Touch touch = new Touch();

    @Test
    public void shouldSetDateAndTime() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile = new File(dir, "testFile");
        testFile.deleteOnExit();
        assertFalse(testFile.exists(), "It shouldn't exist yet");
        final Calendar calendar = Calendar.getInstance();
        final String testFilePath = testFile.getAbsolutePath();
        touch.executeTouch(new String[]{testFilePath});
        assertTrue(testFile.exists(), "Now it should exist");
        assertLength(testFile, 0, "It should be empty.");
        assertEquals(testFile.lastModified() / 10000, calendar.getTime().getTime() / 10000, "Should have same date (to second)");
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.MONTH, APRIL);
        calendar.set(Calendar.YEAR, 1980);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 34);
        calendar.set(Calendar.SECOND, 56);
        calendar.set(Calendar.MILLISECOND, 0);
        touch.executeTouch(new String[]{"-d", "22/04/1980", "-t", "12:34:56", testFilePath});
        assertEquals(testFile.lastModified(), calendar.getTime().getTime(),
            "Should have same date (up to milliseconds): " + new Date(testFile.lastModified()) + " <> " + calendar.getTime());
    }

    @Test
    public void shouldSetDateAlone() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile = File.createTempFile("testFileShouldSetDateAlone__", "", dir);
        testFile.deleteOnExit();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(testFile.lastModified());
        assertEquals(testFile.lastModified(), calendar.getTime().getTime(),
            "Should have same date: " + new Date(testFile.lastModified()) + " <> " + calendar.getTime());
        final String testFilePath = testFile.getAbsolutePath();
        touch.executeTouch(new String[]{"-d", "14/07/2020", testFilePath});
        calendar.set(Calendar.YEAR, 2020);
        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.DATE, 14);
        assertEquals(testFile.lastModified() / 1000, calendar.getTime().getTime() / 1000,
            "Should have same date (up to milliseconds): " + new Date(testFile.lastModified()) + " <> " + calendar.getTime());
    }

    @Test
    public void shouldSetTimeAlone() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile = File.createTempFile("testFileShouldSetTimeAlone__", "", dir);
        testFile.deleteOnExit();
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1995);
        calendar.set(Calendar.MONTH, JULY);
        calendar.set(Calendar.DATE, 14);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertTrue(testFile.setLastModified(calendar.getTimeInMillis()));

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 34);
        calendar.set(Calendar.SECOND, 56);
        calendar.set(Calendar.MILLISECOND, 0);
        final String testFilePath = testFile.getAbsolutePath();
        touch.executeTouch(new String[]{"-t", "12:34:56", testFilePath});
        assertEquals(testFile.lastModified() / 1000, calendar.getTime().getTime() / 1000,
            "Should have same date (up to milliseconds): " + new Date(testFile.lastModified()) + " <> " + calendar.getTime());
    }

    @Test
    public void testTouchWithoutIncrement() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile1 = new File(dir, "testFile1");
        final File testFile2 = new File(dir, "testFile2");
        final File testFile3 = new File(dir, "testFile3");
        testFile1.deleteOnExit();
        testFile2.deleteOnExit();
        testFile3.deleteOnExit();
        final String testFile1Path = testFile1.getAbsolutePath();
        final String testFile2Path = testFile2.getAbsolutePath();
        final String testFile3Path = testFile3.getAbsolutePath();
        touch.executeTouch(new String[]{testFile1Path, testFile2Path, testFile3Path});
        assertEquals(testFile1.lastModified(), testFile2.lastModified(), "Should have same date");
        assertEquals(testFile1.lastModified(), testFile3.lastModified(), "Should have same date");
    }

    @Test
    public void testTouchWithIncrement() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile1 = new File(dir, "testFile1");
        final File testFile2 = new File(dir, "testFile2");
        final File testFile3 = new File(dir, "testFile3");
        testFile1.deleteOnExit();
        testFile2.deleteOnExit();
        testFile3.deleteOnExit();
        final String testFile1Path = testFile1.getAbsolutePath();
        final String testFile2Path = testFile2.getAbsolutePath();
        final String testFile3Path = testFile3.getAbsolutePath();
        touch.executeTouch(new String[]{"-i", testFile1Path, testFile2Path, testFile3Path});
        assertEquals(testFile1.lastModified() + INCREMENT_TIME, testFile2.lastModified(), "Dates should differ by INCREMENT_TIME");
        assertEquals(testFile2.lastModified() + INCREMENT_TIME, testFile3.lastModified(), "Dates should differ by INCREMENT_TIME");
    }

    @Test
    public void shouldCreateFile() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile = new File(dir, "this file does not exist!");
        testFile.deleteOnExit();
        final String testFilePath = testFile.getAbsolutePath();
        assertFalse(testFile.exists(), "It shouldn't exist");
        touch.executeTouch(new String[]{testFilePath});
        assertTrue(testFile.exists(), "Now it should exist");
    }

    @Test
    public void testGetParameters() throws Exception {
        String[] args = {"-d", "14/07/2020", "-t", "12:34:00", "aaa", "-i", "bbb"};
        final Touch.Parameters parameters = Touch.getParameters(args);
        assertTrue(parameters.incrementTime);
        assertNotNull(parameters.targets);
        assertEquals(parameters.targets.size(), 2);
    }
}