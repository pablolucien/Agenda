package org.pclg.filesystem;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


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
            {"/images/unknown-man.png", "Imagen PNG"},
            {"/sounds/success_sound.wav", "Archivo RIFF"},
            {"/sounds/success_sound.wav", "Audio WAVE"},
            {"/TestBook.xls", "Archivo de Microsoft Office"},
            {"/TestBook.xlsx", "Archivo Comprimido (PKZIP)"},
        };
    }

    @Test(dataProvider = "dataProvider")
    public void testGetMagic(String sourceFile, String magicIndicator) throws URISyntaxException, IOException {
        final URI path = getClass().getResource(sourceFile).toURI();
        final File file = new File(path);
        assertTrue(file.exists());
        final List<String> magics = magic.getMagic(file);
        assertTrue(magics.contains(magicIndicator));
    }
}