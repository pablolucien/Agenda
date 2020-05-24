// ******************************** package
package org.pclg.media.mp3;

// ******************************** imports

import org.pclg.media.id3.ID3Genre;
import org.pclg.media.id3.ID3Tag;

import java.io.File;

/**
 * Actualiza el autor, album y titulo de una cancion.
 *
 * @author El Coyote Cojo.
 * @version unknown.
 * @since 2004.01.13
 */
abstract class MP3UpdateWorker {
    public abstract int getPrefixLen();
    public abstract void setPrefixLen(int prefixLen);
    /**
     * Actualiza un ID3Tag segun sus criterios, tomando como base un File.
     *
     *
     * @param tag el tag a actualizar
     * @param file el File de donde tomar la informacion.
	 * @param replacementChars lista de los caracteres del nombre del fichero que se
	 * reemplazarán en el tag. Por ejemplo ¿¿ por ?
     * @since 2004.01.13
     */
    public abstract void updateTag(final ID3Tag tag, final File file,
		final String[] replacementChars);

    // ******************************** Metodos de clase
    /**
     * Muestra el contenido del tag.
     *
     * @param tag el tag a mostrar.
     * @since 2004.01.14
     */
    static void showTag(final ID3Tag tag) {
        message("Artist   " + tag.getArtist().trim());
        message("Album    " + tag.getAlbum().trim());
        message("Song     " + tag.getSong().trim());
        final int genre = tag.getGenre();
        message("Genre    " + genre + " - " + ID3Genre.getGenre(genre));
        message("Year     " + tag.getYear());
        message("Comment  " + tag.getComment().trim());
    }

	String replaceChars(final String[] replaceChars,
		String song) {
		for (final String repPair : replaceChars) {
			final String[] pair = repPair.split("\\|");
			if (pair.length == 2) {
				song = song.replaceAll(pair[0], pair[1]);
			}
		}
		return song;
	}

    /**
     * Outputs a message.
     *
     * @param msg the message.
     */
    static void message(final String msg) {
        System.out.println(msg);
    }
}