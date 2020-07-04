package org.pclg.media.id3;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

/**
 * Sign	Length(bytes)	Position (bytes)	Description
 * A	3	(0-2) 	Tag identification. Must contain 'TAG' if tag exists and is correct.
 * B	30	(3-32)	Title
 * C	30	(33-62)	Artist
 * D	30	(63-92)	Album
 * E	4	(93-96)	Year
 * F	30	(97-126)	Comment
 * G	1	(127)	Genre
 * <p/>
 * The specification asks for all fields to be padded with null character (ASCII
 * 0). However, not all applications respect this (an example is WinAmp which
 * pads fields with <space>, ASCII 32).
 * <p/>
 * There is a small change proposed in ID3v1.1 structure. The last byte of the
 * Comment field may be used to specify the track number of a song in an album.
 * It should contain a null character (ASCII 0) if the information is unknown.
 *
 * Field 	Length 	Description
 * header 	 3 	"TAG"
 * title 	30 	30 characters of the title
 * artist 	30 	30 characters of the artist name
 * album 	30 	30 characters of the album name
 * year 	 4 	A four-digit year
 * comment 	28 or 30 	The comment.
 * zero-byte 1 	If a track number is stored, this byte contains a binary 0.
 * track 	 1 	The number of the track on the album, or 0. Invalid,
 * 				if previous byte is not a binary 0.
 * genre 	 1 	Index in a list of genres, or 255
 *
 */
@SuppressWarnings({"OverloadedMethodsWithSameNumberOfParameters"})
public final class ID3Tag {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	/* Field 	Length 	Description. */
	/** header 	 3 	"TAG". */
	public static final String ID = "TAG";
	/** header 	 3 	"TAG". */
	static final int ID_LEN = ID.length();
	/** title 	30 	30 characters of the title. */
	static final int MAX_ALBUM_NAME_LEN = 30;
	/** artist 	30 	30 characters of the artist name. */
	static final int MAX_ARTIST_NAME_LEN = 30;
	public static final int MAX_SONG_NAME_LEN = 30;
	/** comment 	28 or 30 	The comment. */
	static final int MAX_COMMENT_LEN = 30;
	static final int YEAR_LEN = 4;
	static final int TAG_LEN = 128;
    private String song;
    private String artist;
    private String album;
    private String year;
    private String comment;
    private int genre;
    private byte trackNr;

	/** Nota: este campo es necesario porque no se puede determinar con
	 * certeza que tengo track nr comparando con 0, porque a saber lo que tiene
	 * comment.
	 */
	private boolean hasTrackNr;
	private static final int UNSIGNED_BYTE_MASK = 0x00FF;

	public ID3Tag() {
		song = "";
		artist = "";
		album = "";
		year = "";
		comment = "";
	}

    public void setSong(final String sSong)
        {
        if (sSong.length() > MAX_SONG_NAME_LEN) {
			LOGGER.warn("ID3Tag.setSong: Argument exceeds limit of 30 bytes: " + sSong);
			song = sSong.substring(0, MAX_SONG_NAME_LEN);
		} else {
			song = sSong;
		}
    }

    void setSong(final byte[] bSong)
        {
        setSong(new String(bSong));
    }

    public void setArtist(final String sArtist)
        {
        if (sArtist.length() > MAX_ARTIST_NAME_LEN) {
			LOGGER.warn("ID3Tag.setArtist: Argument exceeds limit of 30 bytes: " + sArtist);
			artist = sArtist.substring(0, MAX_ARTIST_NAME_LEN);
		} else {
			artist = sArtist;
		}
    }

    void setArtist(final byte[] bArtist) {
        setArtist(new String(bArtist));
    }

    public void setAlbum(final String sAlbum) {
        if (sAlbum.length() > MAX_ALBUM_NAME_LEN) {
			LOGGER.warn("ID3Tag.setAlbum: Argument exceeds limit of 30 bytes: " + sAlbum);
			album = sAlbum.substring(0, MAX_ALBUM_NAME_LEN);
		} else {
			album = sAlbum;
		}
    }

    public void setAlbum(final byte[] bAlbum) {
        setAlbum(new String(bAlbum));
    }

    public void setYear(final String sYear) {
        if (sYear.length() > YEAR_LEN) {
			LOGGER.warn("ID3Tag.setYear: Argument exceeds limit of 4 bytes: " + sYear);
			year = sYear.substring(0, YEAR_LEN);
		} else {
			year = sYear;
		}
    }

    public void setYear(final byte[] bYear) {
        setYear(new String(bYear));
    }

    public void setComment(final String sComment) {
        if (sComment.length() > MAX_COMMENT_LEN) {
			LOGGER.warn("ID3Tag.setComment: Argument exceeds limit of 30 bytes: " + sComment);
			comment = sComment.substring(0, MAX_COMMENT_LEN);
		} else {
			comment = sComment;
		}
    }

    public void setComment(final byte[] bComment) {
        setComment(new String(bComment));
    }

    public void setGenre(final int genre) {
		this.genre = genre >= 0 ? genre : 256 + genre;
    }

    public String getSong() {
        return song;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getYear() {
        return year;
    }

    public String getComment() {
        return comment;
    }

    public int getGenre() {
        return genre;
    }

	int getTrackNr() {
		return trackNr;
	}

	public void setTrackNr(final byte trackNr) {
		this.trackNr = trackNr;
		hasTrackNr = trackNr != 0;
	}

    @Override
	public String toString() {
        return new StringBuilder()
			.append("album: ").append(album.trim())
			.append(", track: ").append(trackNr & UNSIGNED_BYTE_MASK)
			.append(", song: ").append(song.trim())
			.append(", artist: ").append(artist.trim())
			.append(", year: ").append(year.trim())
			.append(", comment: ").append(comment.trim())
			.append(", genre: ").append(ID3Genre.getGenre(genre))
			.append(" (").append(genre).append(')')
			.toString();
    }

	/**
	 * Concatenates all the elements in the tag.
	 * @return the tag built.
	 */
	byte[] getTag() {
        final byte[] tag = new byte[TAG_LEN];
		int destPos = 0;

		byte[] bytes = ID.getBytes();
		System.arraycopy(bytes, 0, tag, destPos, bytes.length);
		destPos += ID_LEN;

		bytes = song.trim().getBytes();
		System.arraycopy(bytes, 0, tag, destPos, bytes.length);
		destPos += MAX_SONG_NAME_LEN;

		bytes = artist.trim().getBytes();
		System.arraycopy(bytes, 0, tag, destPos, bytes.length);
		destPos += MAX_ARTIST_NAME_LEN;

		bytes = album.trim().getBytes();
		System.arraycopy(bytes, 0, tag, destPos, bytes.length);
		destPos += MAX_ALBUM_NAME_LEN;

		bytes = year.trim().getBytes();
		System.arraycopy(bytes, 0, tag, destPos, bytes.length);
		destPos += YEAR_LEN;

		bytes = comment.trim().getBytes();
		System.arraycopy(bytes, 0, tag, destPos, bytes.length);

		if (hasTrackNr) {
			tag[TAG_LEN - 2] = trackNr;
		}
		tag[TAG_LEN - 1] = (byte) genre;
        return tag;
    }
}
