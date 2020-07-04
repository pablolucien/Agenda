package org.pclg.media.id3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public final class ID3Util {
	private ID3Util() {
	}

	private static InputStream open4Read(final File file) throws IOException,
			SecurityException {
		if (!file.exists()) {
			throw new FileNotFoundException("ID3Util: file \""
				+ file.getAbsolutePath() + "\" not found !");
		}
		if (!file.isFile()) {
			throw new IOException("ID3Util: file \"" + file.getAbsolutePath()
				+ "\" not a file !");
		}
		if (!file.canRead()) {
			throw new SecurityException("ID3Util: file \""
				+ file.getAbsolutePath() + "\" no read permission !");
		}
		return new FileInputStream(file);
	}

	public static boolean hasID3(final String sFilename) throws IOException,
			SecurityException {
		return hasID3(new File(sFilename));
	}

	public static boolean hasID3(final File id3File) throws IOException {
		final long length = id3File.length();
		if (length < ID3Tag.TAG_LEN) {
			return false;
		}
		final InputStream in = open4Read(id3File);
		try {
			in.skip(length - ID3Tag.TAG_LEN);
			final byte[] tag = new byte[ID3Tag.ID_LEN];
			in.read(tag, 0, ID3Tag.ID_LEN);
			return new String(tag).equalsIgnoreCase(ID3Tag.ID);
		} finally {
			in.close();
		}
	}

	public static ID3Tag readID3(final String sFilename) throws IOException,
			SecurityException, ID3NoTagException {
		return readID3(new File(sFilename));
	}

	public static ID3Tag readID3(final File id3File)
			throws IOException, ID3NoTagException {
		final ID3Tag tag = new ID3Tag();
		final InputStream in = open4Read(id3File);
		in.skip(id3File.length() - ID3Tag.TAG_LEN);
		final byte[] id = new byte[ID3Tag.ID_LEN];
		final byte[] song = new byte[ID3Tag.MAX_SONG_NAME_LEN];
		final byte[] artist = new byte[ID3Tag.MAX_ARTIST_NAME_LEN];
		final byte[] album = new byte[ID3Tag.MAX_ALBUM_NAME_LEN];
		final byte[] year = new byte[ID3Tag.YEAR_LEN];
		final byte[] comment = new byte[ID3Tag.MAX_COMMENT_LEN];
		final byte[] genre = new byte[1];
		in.read(id);
		if (new String(id).equalsIgnoreCase(ID3Tag.ID)) {
			in.read(song);
			in.read(artist);
			in.read(album);
			in.read(year);
			in.read(comment);
			in.read(genre);
			tag.setAlbum(album);
			tag.setArtist(artist);
			if (comment[ID3Tag.MAX_COMMENT_LEN - 2] == 0) {
				tag.setComment(new String(comment, 0,
						ID3Tag.MAX_COMMENT_LEN - 2));
				tag.setTrackNr(comment[ID3Tag.MAX_COMMENT_LEN - 1]);
			} else {
				tag.setComment(comment);
			}
			tag.setGenre(genre[0]);
			tag.setSong(song);
			tag.setYear(year);
		} else {
			in.close();
//			throw new ID3NoTagException("ID3Util.readID3: no tag found in file " + id3File);
			System.err.println("ID3Util.readID3: no tag found in file " + id3File);
			return null;
		}
		in.close();
		return tag;
	}

	public static void clearID3(final String filename)
			throws IOException, ID3NoTagException {
		clearID3(new File(filename));
	}

	private static void clearID3(final File file)
			throws IOException {
		if (hasID3(file)) {
			try (final RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
				raf.setLength(file.length() - ID3Tag.TAG_LEN);
			}
		}
	}

	public static void writeID3(final String filename, final ID3Tag tag)
			throws IOException {
		writeID3(new File(filename), tag);
	}

	public static void writeID3(final File file, final ID3Tag tag)
			throws IOException {
		try (final RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			raf.seek(hasID3(file) ? file.length() - ID3Tag.TAG_LEN : file.length());
			raf.write(tag.getTag());
		}
	}
}