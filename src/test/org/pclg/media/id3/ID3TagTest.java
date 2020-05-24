package org.pclg.media.id3;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 22-abr-2011 5:33:17
 */
public class ID3TagTest {
	private ID3Tag id3Tag;
	private static final String ALBUM = "El Album Blanco";
	private static final String ARTIST = "The Beatles";
	private static final String SONG = "Revolution nr. 9";
	private static final int GENRE = ID3Genre.getGenreId("Synthpop");
	private static final String YEAR = "1969";
	private static final String COMMENT = "El album mol�n";
	private static final byte TRACK  = 6;

	@BeforeMethod
	public void setUp() {
		id3Tag = new ID3Tag();
		id3Tag.setAlbum(ALBUM);
		id3Tag.setArtist(ARTIST);
		id3Tag.setYear(YEAR);
		id3Tag.setComment(COMMENT);
		id3Tag.setSong(SONG);
		id3Tag.setGenre((byte) GENRE);
		id3Tag.setTrackNr(TRACK);
	}

	@Test
	public void testAlbum() {
		assertEquals(SONG, id3Tag.getSong());
		assertEquals(ARTIST, id3Tag.getArtist());
		assertEquals(ALBUM, id3Tag.getAlbum());
		assertEquals(YEAR, id3Tag.getYear());
		assertEquals(COMMENT, id3Tag.getComment());
		assertEquals(TRACK, id3Tag.getTrackNr());
		assertEquals(GENRE, id3Tag.getGenre());
	}

	@Test
	public void testTag() {
		final byte[] tag = id3Tag.getTag();
		int offset = 0;

		assertEquals(ID3Tag.ID, new String(tag, offset, ID3Tag.ID_LEN));
		offset += ID3Tag.ID_LEN;

		assertEquals(SONG,
			new String(tag, offset, ID3Tag.MAX_SONG_NAME_LEN).trim());
		offset += ID3Tag.MAX_SONG_NAME_LEN;

		assertEquals(ARTIST,
			new String(tag, offset, ID3Tag.MAX_ARTIST_NAME_LEN).trim());
		offset += ID3Tag.MAX_ARTIST_NAME_LEN;

		assertEquals(ALBUM,
			new String(tag, offset, ID3Tag.MAX_ALBUM_NAME_LEN).trim());
		offset += ID3Tag.MAX_ALBUM_NAME_LEN;

		assertEquals(YEAR,
			new String(tag, offset, ID3Tag.YEAR_LEN).trim());
		offset += ID3Tag.YEAR_LEN;

		assertEquals(COMMENT,
			new String(tag, offset, ID3Tag.MAX_COMMENT_LEN).trim());
		offset += ID3Tag.MAX_COMMENT_LEN - 1;

		assertEquals(TRACK, tag[offset++]);
		assertEquals(GENRE,
			tag[offset] >= 0 ? tag[offset] : 256 + tag[offset]);
	}
}
