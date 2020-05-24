package org.pclg.filesystem;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MagicTest {

    private Magic magic;

    @BeforeMethod
    public void setUp() {
        magic = new Magic(false, false);
    }

    @DataProvider
    public static Object[][] dataProvider() {
        return new Object[][] {
            {"/images/unknown-man.jpg", "Imagen PNG"},
            {"/images/unknown-woman.png", "Imagen PNG"},
            {"/sounds/success_sound.wav", "Archivo RIFF"},
            {"/sounds/success_sound.wav", "Audio WAVE"},
            {"/TestBook.xls", "Archivo de Microsoft Office"},
            {"/TestBook.xlsx", "Archivo Comprimido (PKZIP)"},
        };
    }

    @DataProvider
    public static Object[][] dataProviderValidExtension() {
        return new Object[][] {
            {"/images/unknown-man.jpg", false},
            {"/images/unknown-woman.png", true},
        };
    }

    @Test(dataProvider = "dataProvider")
    public void testGetMagic(final String sourceFile, final String magicIndicator) throws URISyntaxException {
        final URI path = getClass().getResource(sourceFile).toURI();
        final File file = new File(path);
        assertTrue(file.exists());
        final List<String> magics = magic.getMagics4File(file);
        assertTrue(magics.contains(magicIndicator));
    }

    @Test(dataProvider = "dataProviderValidExtension")
    public void testGetMagicInvalidExtension(final String sourceFile, final boolean isValid) throws URISyntaxException {
        final URI path = getClass().getResource(sourceFile).toURI();
        final File file = new File(path);
        assertTrue(file.exists());
        assertEquals(magic.isValidExtension(file), isValid);
    }
}