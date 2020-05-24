package org.pclg.media.id3;

import org.junit.Test;

import static org.junit.Assert.*;

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
		assertNotNull("Genre List should not be null", genreList1);
		assertTrue("Genre List should contain something", genreList1.length > 0);
		final String[] genreList2 = ID3Genre.getGenreList();
		assertNotNull("Genre List should not be null", genreList2);
		assertTrue("Genre List should contain something", genreList2.length > 0);
		assertNotSame(genreList1, genreList2);
	}
}
