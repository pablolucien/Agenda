package org.pclg.media;

import org.pclg.log.LoggerFactory;
import org.pclg.media.id3.ID3Genre;
import org.pclg.tools.StringTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mantiene la informacion de un álbum.
 * 
 * @author El Coyoye
 * @since 07-mar-2011 12:11:37
 */
public final class Album {
	/** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.makeSimpleLogger();

    /** The artist of this album. */
    private String artist;

    /** The title of this album. */
    private String title;

    /** The genre of this album. */
    private byte genre;

    /** The year of this album. */
    private String year;

	/** The comment of this album. TODO: should be for every song */
    private String comment;

	/** The number of songs of a 'standard' album */
	private static final int CAPACITY = 20;

    /** The songs of this album. */
    private final List<Song> songs = new ArrayList<>(CAPACITY);

	private static final String ARTIST_SONG_SEPARATOR_TAG = ":\\|:";
	private static final String GENRE_TAG = "genre:";
	private static final String COMMENT_TAG = "comment:";
	private static final String ARTIST_TAG = "artist:";
	private static final String YEAR_TAG = "year:";
	private static final String TITLE_TAG = "title:";

	public class Song {
		private String artist;
		private String name;

		public String getArtist() {
			return artist;
		}

		public void setArtist(final String artist) {
			this.artist = artist;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

        @Override
        public String toString() {
            return "Song{" +
                "artist='" + artist + '\'' +
                ", name='" + name + '\'' +
                '}';
        }
    }
    /**
     * Contructs an empty Album.
     */
	private Album() {
    }

    /**
     * Contructs an Album with the data provided and an empty songs list.
     *
     * @param artist The artist of this album.
     * @param title The title of this album.
     * @param genre The genre of this album.
     * @param year The songs of this album.
     */
    public Album(final String artist, final String title, final byte genre,
            final String year) {
        this.artist = artist;
        this.title = title;
        this.genre = genre;
        this.year = year;
    }

    /**
     * Returns the artist of this album.
     * @return the artist of this album.
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Sets the artist of this album.
     * @param artist the artist of this album.
     */
    public void setArtist(final String artist) {
        this.artist = artist;
    }

    /**
     * Returns the title of this album.
     * @return the title of this album.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this album.
     * @param title the title of this album.
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Returns the genre of this album.
     * @return the genre of this album.
     */
    public byte getGenre() {
        return genre;
    }

    /**
     * Sets the genre of this album.
     * @param genre the genre of this album.
     */
    public void setGenre(final byte genre) {
        this.genre = genre;
    }

    /**
     * Returns the yesr of this album.
     * @return the year of this album.
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the year of this album.
     * @param year the year of this album.
     */
    public void setYear(final String year) {
        this.year = year;
    }

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

    /**
     * Returns the count of the songs of this album.
     * @return the count of the songs of this album.
     */
    public int getSongsCount() {
        return songs.size();
    }

    /**
     * Adds a song to this album.
     * @param artist  the artist of the song if not present in songStr.
     * @param songStr the song to be added in the format <artist:|:name> or <name>.
     */
	void addSong(final String artist, final String songStr) {
		final String[] strings = songStr.split(ARTIST_SONG_SEPARATOR_TAG, 2);
		final Song song = new Song();
		if (strings.length == 1) {
			song.artist = artist == null ? "" : artist;
			song.name = strings[0];
		} else {
			song.artist = strings[0];
			song.name = strings[1];
		}
        songs.add(song);
    }

    /**
     * Returns the nth song of this album.
     * @param track the track number of the song to be retrieved (starting with 1)
     * @return the nth song of this album.
     */
    public Song getSong(final int track) {
        return songs.get(track - 1);
    }

    /**
     * Returns a string representation of the album.
     * @return a string representation of the album.
     */
    @Override
    public String toString() {
        return new StringBuilder().append("Album{")
			.append(ARTIST_TAG).append(artist).append(", ")
            .append(TITLE_TAG).append(title).append(", ")
            .append(GENRE_TAG).append(genre).append(", ")
            .append(YEAR_TAG).append(year).append(", ")
            .append(COMMENT_TAG).append(comment).append(", ")
            .append(", songs=").append(songs).append('}').toString();
    }

    /**
     * Creates an album from a file.
     * The file should have the following structure:
     * <pre>
     * genre: Genre
     * title: Title
     * year: year
     * artist: Artist name
     *
     * song 1
     * ....
     * song n
     * </pre>
     *
     * @param namesFilename the file with the info of the album.
     * @return the Album that contains the info from the file.
     * 
     * @throws IOException if there are problemes reading the file.
     */
    public static Album createAlbumFromFile(final String namesFilename)
            throws IOException {
        try (final BufferedReader reader = new BufferedReader(new FileReader(namesFilename))) {
            final Album album = new Album();
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringTools.isCommentOrBlank(line)) {
                    continue;
                }

				line = line.trim();

				if (StringTools.startsWithIgnoreCase(line, GENRE_TAG)) {
					album.genre = (byte) ID3Genre.getGenreId(
						line.substring(GENRE_TAG.length()).trim());
                } else {
					if (StringTools.startsWithIgnoreCase(line, TITLE_TAG)) {
						album.title = line.substring(TITLE_TAG.length()).trim();
					} else {
						if (StringTools.startsWithIgnoreCase(line, YEAR_TAG)) {
							album.year = line.substring(YEAR_TAG.length()).trim();
						} else {
							if (StringTools.startsWithIgnoreCase(line, ARTIST_TAG)) {
								album.artist = line.substring(ARTIST_TAG.length()).trim();
							} else {
								if (StringTools.startsWithIgnoreCase(line, COMMENT_TAG)) {
									album.comment = line.substring(COMMENT_TAG.length()).trim();
								} else {
									album.addSong(album.artist, line);  // FIXME: el artista debe haber sido leído antes, ergo definido antes en el archivo .info
								}
							}
						}
					}
				}
            }
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(album.toString());
            }
            return album;
        }
    }

    /**
     * Creates an albul from the filesystem.
     * The directory tree should have the following structure:
     * <pre>
     * Artist name|
     *            |_Title|
     *                   |_01-song 1.mp3 (track number optional)
     *                   |_.....
     *                   |_nn-song n.mp3 (track number optional)
     * </pre>
     *
     * @param dir the directory where the songs are stored (represents "Title"
     *          in the above structure.
     * @param genre the musical genre of this album (may be null).
     * @param year the year of first publication of this album (may be null).
     * @param chars2Trim1 number of chars (track number) to trim at the begining
     *          of the filename.
     * @param chars2Trim2 number of chars (filename extension) to trim at the
     *          end of the filename.
     * @return the Album that contains the info of the directory.
     */
    public static Album createAlbumFromFileSystem(final File dir,
            final byte genre, final String year, final int chars2Trim1,
            final int chars2Trim2) {
        final Album album = new Album();
		album.artist = dir.getParentFile().getName();
		album.title = dir.getName();
		album.genre = genre;
		album.year = year;
        final String[] songs = dir.list();
		if (songs != null) {
			for (final String song : songs) {
				album.addSong(album.artist, song.substring(chars2Trim1, song.length() - chars2Trim2));
			}
		}
		LOGGER.info(album.toString());
        return album;
    }
}
