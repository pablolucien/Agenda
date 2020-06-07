package tests;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.AbstractID3v2FrameBody;
import org.farng.mp3.id3.FrameBodyTALB;
import org.farng.mp3.id3.FrameBodyTIT2;
import org.farng.mp3.id3.FrameBodyTPE1;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_4Frame;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Pablo
 * @since 11/05/13 19:12
 */
public class TestJid3Lib {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.make();

	public static void main(final String[] args) throws IOException, TagException {
		final File sourceFile = new File("c:/tmp/2013-04-15_Especialistas.mp3");
		final MP3File mp3file = new MP3File(sourceFile);
		final String artist = "_SER";
		final String album = "Especialistas Secundarios";
		final String title = "2013-04-15_Especialistas";
		final ID3v1 id3v1Tag = mp3file.getID3v1Tag();
		System.out.println("id3v1Tag = " + id3v1Tag);
		id3v1Tag.setAlbumTitle(album);
		id3v1Tag.setSongTitle(title);
		id3v1Tag.setLeadArtist(artist);
		LOGGER.info("***** id3v1Tag ****** = " + id3v1Tag);

		final AbstractID3v2 id3v2Tag = mp3file.getID3v2Tag();
		LOGGER.info("id3v2Tag = " + id3v2Tag);
		if (id3v2Tag != null) {
			//id3v2Tag.setAlbumTitle(id3v1Tag.getAlbumTitle());
			//id3v2Tag.setSongTitle(id3v1Tag.getSongTitle());
			//id3v2Tag.setLeadArtist(id3v1Tag.getLeadArtist());
			//id3v2Tag.setAuthorComposer(artist);
			AbstractID3v2Frame frame;
			AbstractID3v2FrameBody frameBody;

			frameBody = new FrameBodyTALB((byte) 0, id3v1Tag.getAlbumTitle());
			frame = new ID3v2_4Frame(frameBody);
			id3v2Tag.setFrame(frame);

			frameBody = new FrameBodyTIT2((byte) 0, id3v1Tag.getSongTitle());
			frame = new ID3v2_4Frame(frameBody);
			id3v2Tag.setFrame(frame);

			frameBody = new FrameBodyTPE1((byte) 0, id3v1Tag.getLeadArtist());
			frame = new ID3v2_4Frame(frameBody);
			id3v2Tag.setFrame(frame);

			LOGGER.info("***** id3v2Tag ****** = " + id3v2Tag);
		}
		//LOGGER.info("mp3file.getLyrics3Tag() = " + mp3file.getLyrics3Tag());
		mp3file.save();
	}
}
