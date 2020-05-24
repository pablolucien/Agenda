package org.pclg.media.id3;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Pablo
 * @since 20-abr-2011 18:17:34
 */
public class ID3Test {
	private File testFileName;

    @Before
	public void setUp() throws Exception {
		testFileName = File.createTempFile("test", ".mp3");
		testFileName.deleteOnExit();
	}

	@Test
	public void writeId3()
			throws IOException, ID3NoTagException {
		System.out.println("Testing = " + testFileName);
		final ID3Tag id3Tag = new ID3Tag();
		id3Tag.setAlbum("El Album");
		id3Tag.setArtist("El Artista");
		id3Tag.setComment("El comentario");
		id3Tag.setGenre((byte) 45);
		id3Tag.setSong("La canción");
		id3Tag.setTrackNr((byte) 200);
		id3Tag.setYear("1234");
		ID3Util.writeID3(testFileName, id3Tag);
	}

	@Test
	public void readId3()
			throws IOException, ID3NoTagException {
		System.out.println("Testing = " + testFileName);
		if (ID3Util.hasID3(testFileName)) {
			final ID3Tag id3Tag = ID3Util.readID3(testFileName);
			System.out.println("id3Tag = " + id3Tag);
		} else {
			System.out.println("No hay tag;");
		}
	}
}
