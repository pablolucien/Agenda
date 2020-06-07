package org.pclg.media.mp3;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.*;
import org.pclg.filesystem.fileattributes.FileAttributes;
import org.pclg.filesystem.fileattributes.FileAttributesFactory;
import org.pclg.log.LoggerFactory;
import org.pclg.media.Album;
import org.pclg.media.id3.ID3Genre;
import org.pclg.media.id3.ID3NoTagException;
import org.pclg.media.id3.ID3Tag;
import org.pclg.media.id3.ID3Util;
import org.pclg.media.mp3.gui.MainFrame;
import org.pclg.tools.Dir;
import org.pclg.tools.FileTools;
import org.pclg.tools.PropertiesHelper;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

public final class MP3Update {
	/** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.make();

	/** Indica si estamos probando o ejecutando en realidad */
	private static final boolean TEST_MODE = false;

    /** Help message to the clueless user. */
    private static final String MSG_USAGE =
        "Usage: java org.pclg.media.mp3.MP3Update [-s] <dir> [def_file]\n"
        + "  if the modifier -s is used, a whole directory tree is scanned\n"
        + "  else if an info_file is specified it is used to\n"
        + "  obtain the info of the songs contained in dir\n"
        + "  else the info of the filesystem is used";

	public static final int DEFAULT_PREFIX_LEN = 3;

	private static final String TAG_MAGIC = "ID3";
	private static final int TAG_HEADER_ANF_FOOTER_SIZE = 10;
    private static final String MP3_SUFFIX = ".mp3";

	public static final String REPLACE_CHARS_TEXT_DELIMITER = ",,";
	public static final String DEFAULT_REPLACEMENT_CHARS = "¿¿|?,,¨|:";

	public static class Options {
        public int year;
        public int prefixLen;
        public boolean useTrackNrFromPrefix;
        public int genre;
        public boolean testMode;
        public boolean preserveAttributes;
        public boolean overwrite;
        public boolean deleteId3V2;
    }

    private MP3Update() {
	}

    public static void processRecursive(final String dirName, final Options options, final String[] replaceChars) throws IOException {
        final File dir = new File(dirName);
        final Options options2Use = checkOptions2Use(dir, options);
        if (dir.isDirectory()) {
			process(dirName, null, options2Use, replaceChars);
            final Dir lister = new Dir(Dir.NULL_EXCLUDE_LIST);
            final List<File> files = lister.listarArchivos(dir, true, false, Dir.AcceptableType.DIRECTORY);
            for (final File file : files) {
				process(file.getAbsolutePath(), null, options2Use, replaceChars);
			}
		}
    }

	/**
     * Procesa todos los archivos de este directorio.
     *  @param path El directorio a procesar.
	 * @param replaceChars lista de los caracteres del nombre del fichero que se
	 */
    public static void process(final String path, final Album album, final Options options, final String[] replaceChars) throws IOException {
        final File dir = new File(path);
        if (dir.isDirectory()) {
            final Options options2Use = checkOptions2Use(dir, options);
            final File[] files = dir.listFiles();
            byte trackNr = 0;
            assert files != null;
            for (final File file : files) {
                final String fileName = file.getName();
                if (fileName.toLowerCase().endsWith(".mp3")) {
                    if (options2Use.useTrackNrFromPrefix) {
                        trackNr = determineTrackNrFromFileName(fileName, options2Use.prefixLen);
                    } else {
                        ++trackNr;
                    }
                    update(file, album, trackNr, options2Use, replaceChars);
                }
            }
        }
    }

    /** If ther is an options file in the dir create an Options object from it, otherwise return the default. */
    private static Options checkOptions2Use(final File dir, final Options defaultOptions) throws IOException {
        final File propertiesFile = new File(dir, "MP3Update_Options.properties");
        if (propertiesFile.exists()) {
            final Properties properties = new Properties();
            PropertiesHelper.loadPropertiesFromFile(properties, propertiesFile.getAbsolutePath());
            final Options options = new Options();
            options.year = PropertiesHelper.getIntFromProperties(properties, "year", defaultOptions.year);
            options.prefixLen = PropertiesHelper.getIntFromProperties(properties, "prefixLen", defaultOptions.prefixLen);
            options.useTrackNrFromPrefix = PropertiesHelper.getBooleanFromProperties(properties, "useTrackNrFromPrefix", defaultOptions.useTrackNrFromPrefix);
            options.genre = PropertiesHelper.getIntFromProperties(properties, "genre", defaultOptions.genre);  // TODO: obtenerlo from name
            options.testMode = PropertiesHelper.getBooleanFromProperties(properties, "testMode", defaultOptions.testMode);
            options.preserveAttributes = PropertiesHelper.getBooleanFromProperties(properties, "preserveAttributes", defaultOptions.preserveAttributes);
            options.overwrite = PropertiesHelper.getBooleanFromProperties(properties, "overwrite", defaultOptions.overwrite);
            options.deleteId3V2 = PropertiesHelper.getBooleanFromProperties(properties, "deleteId3V2", defaultOptions.deleteId3V2);
            return options;
        }
        return defaultOptions;
    }

    private static byte determineTrackNrFromFileName(final String fileName, final int prefixLen) {
		// Suponemos que el número de pista lo determinan los caracteres
		// numéricos que haya hasta prefixLen.
		final String prefix = fileName.substring(0, prefixLen);
		final Pattern pattern = Pattern.compile("(\\d+).*");
		final Matcher matcher = pattern.matcher(prefix);
		byte trackNr = 0;
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info(String.format("filename = %s, prefix = %s", fileName, prefix));
		}
		if (matcher.matches()) {
			final String match = matcher.group(1);
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info(String.format("filename = %s, trackNr = %s", fileName, match));
			}
			trackNr = Byte.parseByte(match);
		}
		return trackNr;
	}

	/** Actualiza un archivo.
     *
     * @param file el archivo a actualizar.
     * @param trackNr la pista que ocupa esta canción en el álbum.
	 * @param replaceChars lista de los caracteres del nombre del fichero que se
	 * reemplazarán en el tag. Por ejemplo ¿¿ por ?
     */
	private static void update(final File file, final Album album,
	        final byte trackNr, final Options options, final String[] replaceChars) {
		try {
            final MP3UpdateWorker worker = new MP3UpdateFilePathWorker(false);
			worker.setPrefixLen(options.prefixLen);
            final ID3Tag tag;
			if (ID3Util.hasID3(file)) {
                if (options.overwrite) {
                    tag = ID3Util.readID3(file);
                    LOGGER.info("ID3Tag found for " + file + ". Modifying it");
                } else {
                    LOGGER.info("ID3Tag found for " + file + ". Doing nothing");
                    return;
                }
            } else {
                tag = new ID3Tag();
                LOGGER.info("No ID3Tag found for " + file + ". Creating it");
            }
            worker.updateTag(tag, file, replaceChars);

			if (options.genre != ID3Genre.UNKNOWN_GENRE_ID) {
				tag.setGenre(options.genre);
			}

			if (options.year > 0) {
				tag.setYear(String.valueOf(options.year));
			}

			if (album != null) {
				final byte genreFromAlbum = album.getGenre();
				if (genreFromAlbum != ID3Genre.UNKNOWN_GENRE_ID) {
					tag.setGenre(genreFromAlbum);
				}

				final String albumYear = album.getYear();
				if (albumYear != null) {
					tag.setYear(albumYear);
				}

				final Album.Song song = album.getSong(trackNr);
				tag.setArtist(song.getArtist());
				tag.setSong(song.getName());

				final String title = album.getTitle();
				if (title != null) {
					tag.setAlbum(title);
				}

				final String comment = album.getComment();
				if (comment != null) {
					tag.setComment(comment);
				}
			}

			// Debido a que trackNr se almacena en los últimos bytes del
			// comentario, hay que hacer este set siempre después de
			// setComment(). ¡Cosas de la vida!
			tag.setTrackNr(trackNr);

			if (options.testMode) {
                LOGGER.info("En modo de prueba. No se actualiza nada");
            } else {
                FileAttributes fileAttributes = null;
                if (options.preserveAttributes) {
                    fileAttributes = FileAttributesFactory.newInstance();
                    fileAttributes.grabAttributes(file);
                }
                ID3Util.writeID3(file, tag);
				// Actualizar la tag ID3V2 si existe.
				// Usamos la library jid3lib. TODO: usarla para todo.
				// y otro TODO: ver por qué crea la copia .original y por qué no es exacta.
                if (options.deleteId3V2) {
                    deleteID3V2Tag(file);
                } else {
                    updateID3V2Tag(file, tag);
                }
                if (options.preserveAttributes) {
                    fileAttributes.applyAttributes(file);
                }
            }
		} catch (ID3NoTagException | IOException | TagException ex) {
			LOGGER.log(Level.SEVERE, "Error: ", ex);
		}
	}

	/**
	 * Actualiza la tag ID3V2, si existe, con los datos de la tag ID3V1.
	 *
	 * @param sourceFile el archivo a actualizar.
	 * @param id3v1Tag la tag con la información.
	 */
	private static void updateID3V2Tag(final File sourceFile, final ID3Tag id3v1Tag)
			throws IOException, TagException {
		final MP3File mp3file = new MP3File(sourceFile);
		final AbstractID3v2 id3v2Tag = mp3file.getID3v2Tag();
		if (id3v2Tag != null) {
			id3v2Tag.setFrame(new ID3v2_4Frame(
				new FrameBodyTALB((byte) 0, id3v1Tag.getAlbum())));
			id3v2Tag.setFrame(new ID3v2_4Frame(
				new FrameBodyTIT2((byte) 0, id3v1Tag.getSong())));
			id3v2Tag.setFrame(new ID3v2_4Frame(
				new FrameBodyTPE1((byte) 0, id3v1Tag.getArtist())));
			mp3file.save();
            LOGGER.info("ID3Tag V2 found. Updated");
		}
	}

	/**
	 * Elimina la tag ID3V2, si existe.
	 *
	 * @param sourceFile el archivo a actualizar.
	 */
	private static void deleteID3V2Tag(final File sourceFile)
			throws IOException {
		final byte[] data = FileTools.readFromFile(sourceFile);
		if (TAG_MAGIC.equals(new String(data, 0, 3))) {
			final int tagSize = getTagSize(data);
			LOGGER.info(sourceFile + " >>> " + tagSize);
			final FileAttributes fileAttributes = FileAttributesFactory.newInstance();
			fileAttributes.grabAttributes(sourceFile);
			FileTools.writeToFile(data, tagSize, data.length - tagSize, sourceFile, true);
			fileAttributes.applyAttributes(sourceFile);
		} else {
			LOGGER.info(sourceFile + " doesn't have ID3V2 Tag  ");
		}
	}

	/**
	   The ID3v2 tag size is the sum of the byte length of the extended
	   header, the padding and the frames after unsynchronisation. If a
	   footer is present this equals to ('total size' - 20) bytes, otherwise
	   ('total size' - 10) bytes.
	*/
	private static int getTagSize(final byte[] data) {
		int size = TAG_HEADER_ANF_FOOTER_SIZE;
//		 A considerar en data[5]:
//	     The second bit (bit 6) indicates whether or not the header is
//	     followed by an extended header. The extended header is described in
//	     section 3.2. A set bit indicates the presence of an extended
//	     header.
		final int bit6set = 64;
		if ((data[5] & bit6set) != 0) {
			LOGGER.info("Has extended header");
			// No afecta al tamaño del tag
		}
//
//	     Bit 4 indicates that a footer (section 3.4) is present at the very
//	     end of the tag. A set bit indicates the presence of a footer.
		final int bit4set = 16;
		if ((data[5] & bit4set) != 0) {
			LOGGER.info("Has footer");
			size += TAG_HEADER_ANF_FOOTER_SIZE;
		}

		size += (data[6] << 21) + (data[7] << 14) + (data[8] << 7) + data[9];
		return size;
	}

	/**
	 * Renames the files in the path with the names in the album. The order
     * of the files as retourned by File.listFiles() must correspond with the
     * names of the songs in the album.
	 *
	 * @param dirName name of the dir where there are the files to be renamed.
	 * @param album the object with the info of the album.
     * @param addTrackNr if true, prepend track number (01- ... nn-) to the
     * file name.
	 */
	public static void renameFiles(final String dirName, final Album album,
            final boolean addTrackNr) {
		final File dir = new File(dirName);
		final File[] files = dir.listFiles(f -> f.getName().endsWith(".mp3"));
        final int songsCount = album.getSongsCount();
        assert files != null;
        if (songsCount < files.length) {
            LOGGER.warning("Cantidad de canciones insuficiente en el album");
        } else {  
            int track = 1;
            final NumberFormat format = new DecimalFormat("00-");
            for (final File file : files) {
                final String prefix = addTrackNr ? format.format(track) : "";
				final Album.Song song = album.getSong(track++);
				final String songArtist = song.getArtist();
				final String songName = song.getName();
				final String fileName = FileTools.sanitizeWindowsFilename(
                    isEmptyOrBlank(songArtist) ? songName : songArtist + " - " + songName, "");
				final String newFileName = prefix + fileName + MP3_SUFFIX;
                final File newFile = new File(dir, newFileName);
                final boolean result = file.renameTo(newFile);
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("file = " + file.getName() + ", newFile = " + newFile.getName() + (result ? " OK" : " Failed"));
                }
            }
        }
	}

    private static void usage() {
        LOGGER.severe(MSG_USAGE);
	}

    public static void main(final String[] args) throws IOException {
		LoggerFactory.configure();
        final Options options = new Options();
        options.year = 0;
        options.prefixLen = DEFAULT_PREFIX_LEN;
        options.useTrackNrFromPrefix = false;
        options.genre = ID3Genre.UNKNOWN_GENRE_ID;
        options.testMode = TEST_MODE;
        options.preserveAttributes = true;
        options.overwrite =  true;
        options.deleteId3V2 = false;
		final String[] replaceChars = DEFAULT_REPLACEMENT_CHARS.split(REPLACE_CHARS_TEXT_DELIMITER);

		switch (args.length) {
        case 0:
			new MainFrame().show();
            break;
        case 1:
			process(args[0], null, options, replaceChars);
            break;
        case 2:
            if (args[0].equals("-s")) {
                processRecursive(args[1], options, replaceChars);
            } else {
                final Album album = Album.createAlbumFromFile(args[1]);
                renameFiles(args[0], album, true);
                process(args[0], album, options, replaceChars);
            }
            break;
        default:
            usage();
        }
    }
}
