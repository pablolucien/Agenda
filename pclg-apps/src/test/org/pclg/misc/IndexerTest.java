package org.pclg.misc;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import org.testng.annotations.Test;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


/**
 * @since 17/10/2018.
 */
public class IndexerTest {

    @Test
    public void createIndex() throws URISyntaxException, IOException {
		final String testFileName = "/El_pez_mas_viejo_del_rio.txt";
		final Class<? extends IndexerTest> myClass = getClass();
		final String source = new File(getClass().getResource(testFileName).toURI()).getAbsolutePath();
        final Map<String, Integer> frequencies = Indexer.createIndex(source);
        assertNotNull(frequencies);
        assertEquals(frequencies.size(), 46);
        assertEquals(frequencies.get("sombrío"), Integer.valueOf(3));
        assertEquals(frequencies.get("sonreía"), Integer.valueOf(2));
        assertEquals(frequencies.get("divierte"), Integer.valueOf(1));
        assertEquals(frequencies.get("el"), Integer.valueOf(7));
        assertEquals(frequencies.get("es"), Integer.valueOf(1));
        assertNull(frequencies.get("savonarola"));
		assertTrue(new File(myClass.getResource(testFileName + Indexer.INDEX_FILENAME_SUFFIX).toURI())
                        .delete(),"Debería haberlo borrado");
    }
}