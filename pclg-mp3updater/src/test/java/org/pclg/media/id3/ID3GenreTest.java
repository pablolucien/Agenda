package org.pclg.media.id3;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;

/**
 * @author paceLucien
 * @since 07-abr-2011 15:26:43
 */
public class ID3GenreTest {
	@Test
	public void testGetGenre() {
		assertEquals("Blues", ID3Genre.getGenre(0));
		assertEquals("Synthpop", ID3Genre.getGenre(147));
		assertEquals("Pop", ID3Genre.getGenre(13));
		assertEquals(ID3Genre.UNKNOWN_GENRE, ID3Genre.getGenre(-1));
	}

	@Test
	public void testGetGenreId() {
		assertEquals(0, ID3Genre.getGenreId("BlUeS"));
		assertEquals(53, ID3Genre.getGenreId("Pop-Folk"));
		assertEquals(147, ID3Genre.getGenreId("SynthpoP"));
		assertEquals(ID3Genre.UNKNOWN_GENRE_ID, ID3Genre.getGenreId("mixiplix"));
	}

	@Test
	public void testGetGenreList() {
		final String[] genreList1 = ID3Genre.getGenreList();
		assertNotNull(genreList1, "Genre List should not be null");
		assertTrue(genreList1.length > 0, "Genre List should contain something");
		final String[] genreList2 = ID3Genre.getGenreList();
		assertNotNull(genreList2, "Genre List should not be null");
		assertTrue(genreList2.length > 0, "Genre List should contain something");
		assertNotSame(genreList1, genreList2);
	}
}
