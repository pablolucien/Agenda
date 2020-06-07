package org.pclg.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import org.pclg.tools.FileTools;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class CryptoTest {
    private static final String PASSWORD = "password1234";
    private File testFile;
    private File testFileZeuge;
    private File tmpDir;

    @BeforeMethod
    public void setUp() throws IOException {
        tmpDir = Files.createTempDirectory(getClass().getName()).toFile();
        testFile = new File(tmpDir, "CryptoTest_TestFile.txt");
        testFileZeuge = new File(tmpDir,"CryptoTest_TestFileZeuge.txt");
        FileTools.writeToFile("La donna è mobile".getBytes(), testFile, true);
        FileTools.copyFile(testFile, testFileZeuge);
    }

    @AfterMethod
    public void cleanUp() throws IOException {
        Files.delete(testFile.toPath());
        Files.delete(testFileZeuge.toPath());
        Files.delete(tmpDir.toPath());
    }

    @Test
    public void testCreateCrypto() {
        try {
            new Crypto(PASSWORD, false);
        } catch (final GeneralSecurityException ex) {
            fail("Cration failed : " + ex.getMessage());
        }
    }

    @Test
    public void testCryptOK() throws IOException, GeneralSecurityException {
        assertTrue(FileTools.compareContents(testFile, testFileZeuge));
        new Crypto(PASSWORD, false).processFile(testFile, -1, "Cifrando");
        assertTrue(Crypto.isEncrypted(testFile));
        assertFalse(FileTools.compareContents(testFile, testFileZeuge));
        new Crypto(PASSWORD, true).processFile(testFile, -1, "Descifrando");
        assertFalse(Crypto.isEncrypted(testFile));
        assertTrue(FileTools.compareContents(testFile, testFileZeuge));
    }

    @Test(expectedExceptions = SecurityException.class, expectedExceptionsMessageRegExp = ">>>> La contraseña no es correcta <<<<")
    public void testCryptKO() throws GeneralSecurityException, IOException {
        assertTrue(FileTools.compareContents(testFile, testFileZeuge));
        new Crypto(PASSWORD, false).processFile(testFile, -1, "Cifrando");
        assertTrue(Crypto.isEncrypted(testFile));
        assertFalse(FileTools.compareContents(testFile, testFileZeuge));
        new Crypto("WrongPassword", true).processFile(testFile, -1, "Descifrando");
        assertTrue(Crypto.isEncrypted(testFile));
        assertFalse(FileTools.compareContents(testFile, testFileZeuge));
    }

    @Test
    public void testIsEncrypted() throws IOException, GeneralSecurityException {
        final Crypto krypto = new Crypto(PASSWORD, false);
        assertFalse(Crypto.isEncrypted(testFile));
        krypto.processFile(testFile, -1, "Cifrando");
        assertTrue(Crypto.isEncrypted(testFile));
    }
}
