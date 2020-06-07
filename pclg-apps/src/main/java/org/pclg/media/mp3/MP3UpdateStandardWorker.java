// ******************************** package
package org.pclg.media.mp3;

// ******************************** imports

import org.pclg.media.id3.ID3Tag;

import java.io.File;

/**
 * Actualiza el autor, album y titulo de una cancion.
 *
 * @author El Coyote Cojo.
 * @version unknown.
  * @since 2004.01.13
 */
public final class MP3UpdateStandardWorker extends MP3UpdateWorker {
    // ******************************** Variables de clase

    // ******************************** Variables de instancia
    private int prefixLen;
    /**
     * Indica si respetamos el titulo que está en el tag o el nombre del
     * archivo.
     */
    private static final boolean useInternalTitle = true;

    // ******************************** Metodos de instancia
    @Override
	public int getPrefixLen() {
        return prefixLen;
    }

    @Override
	public void setPrefixLen(final int prefixLen) {
        this.prefixLen = prefixLen;
    }

    /**
     * Actualiza un ID3Tag segun sus criterios, tomando como base un File.
     *
     * @param tag el tag a actualizar
     * @param file el File de donde tomar la informacion.
	 * @param replacementChars lista de los caracteres del nombre del fichero que se
	 * reemplazarán en el tag. Por ejemplo ¿¿ por ?
     * @since 2004.01.13
     */
    @Override
	public void updateTag(final ID3Tag tag, final File file, final String[] replacementChars) {
        message("Filename " + file.getName());
        message("Tag inicial");
        showTag(tag);

        message("");
        if (!useInternalTitle) {
            String song = file.getName().substring(prefixLen, file.getName().indexOf(".mp3"));
			song = replaceChars(replacementChars, song);
			if (song.length() > ID3Tag.MAX_SONG_NAME_LEN) {
                song = song.substring(0, ID3Tag.MAX_SONG_NAME_LEN);
            }
            tag.setSong(song);
        }
    }
}
