package org.pclg.filesystem;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.pclg.filesystem.Touch.INCREMENT_TIME;

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
        assertFalse("It shouldn't exist yet", testFile.exists());
        final Calendar calendar = Calendar.getInstance();
        final String testFilePath = testFile.getAbsolutePath();
        Touch.main(new String[] {testFilePath});
        assertTrue("Now it should exist", testFile.exists());
        assertEquals("Should have same date (to second)", testFile.lastModified() / 10000, calendar.getTime().getTime() / 10000);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.YEAR, 1980);
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 34);
        calendar.set(Calendar.SECOND, 56);
        calendar.set(Calendar.MILLISECOND, 0);
        Touch.main(new String[] {"-d", "01/01/1980", "-t", "13:34:56", testFilePath});
        assertEquals("Should have same date (to millisecond): " + new Date(testFile.lastModified()) + " <> " + calendar.getTime(),
            testFile.lastModified(), calendar.getTime().getTime());
    }

    @Test
    public void testTouchWithoutIncrement() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile1 = new File(dir, "testFile1");
        final File testFile2 = new File(dir, "testFile2");
        testFile1.deleteOnExit();
        testFile2.deleteOnExit();
        final Calendar calendar = Calendar.getInstance();
        final String testFile1Path = testFile1.getAbsolutePath();
        final String testFile2Path = testFile2.getAbsolutePath();
        Touch.main(new String[] {testFile1Path, testFile2Path});
        assertEquals("Should have same date", testFile1.lastModified(), testFile2.lastModified());
    }

    @Test
    public void testTouchWithIncrement() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile1 = new File(dir, "testFile1");
        final File testFile2 = new File(dir, "testFile2");
        testFile1.deleteOnExit();
        testFile2.deleteOnExit();
        final Calendar calendar = Calendar.getInstance();
        final String testFile1Path = testFile1.getAbsolutePath();
        final String testFile2Path = testFile2.getAbsolutePath();
        Touch.main(new String[] {"-i", testFile1Path, testFile2Path});
        assertEquals("Dates should differ by INCREMENT_TIME", testFile1.lastModified() + INCREMENT_TIME, testFile2.lastModified());
    }

    //@Test
    public void touchFile() throws Exception {
        final File dir = Files.createTempDirectory(null).toFile();
        dir.deleteOnExit();
        final File testFile = new File("D:\\Profiles\\PACELU~1\\AppData\\Local\\Temp\\___", "Pan sin horno hecho en sarte?n ¡Solo 2 ingredientes!");
        testFile.deleteOnExit();
        final String testFilePath = testFile.getAbsolutePath();
        Touch.main(new String[] {testFilePath});
        assertTrue("Now it should exist", testFile.exists());
    }
}