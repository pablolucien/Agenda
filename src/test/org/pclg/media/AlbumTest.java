package org.pclg.media;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.media.id3.ID3Genre;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author paceLucien
 * @since 09-mar-2011 15:47:03
 */
public class AlbumTest extends TestCase {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    private static final Pattern PATTERN_AGDREF = Pattern.compile("\\d{9,10}");

	// TODO: Este método es solo para que el build no me falle
	// con 'java.lang.Exception: No runnable methods'
	public void testDummy() {
		final Album album = Album.createAlbumFromFileSystem(
			new File("D:/plucien/Musik/Estopa/Allenrock"),
			(byte) ID3Genre.getGenreId("pop"), null, 3, 4);
		LOGGER.info(album.toString());
        LOGGER.info("PATTERN_AGDREF = " + PATTERN_AGDREF);
        assertFalse(PATTERN_AGDREF.matcher("abcdefghi").matches());
        assertFalse(PATTERN_AGDREF.matcher("1").matches());
        assertFalse(PATTERN_AGDREF.matcher("12345678").matches());
        assertTrue(PATTERN_AGDREF.matcher("123456789").matches());
        assertTrue(PATTERN_AGDREF.matcher("1234567890").matches());
        assertFalse(PATTERN_AGDREF.matcher("12345678901").matches());
	}
}
