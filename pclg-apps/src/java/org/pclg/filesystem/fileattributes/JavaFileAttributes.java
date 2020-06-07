package org.pclg.filesystem.fileattributes;

import java.io.File;
import java.io.Serializable;

/**
 * An implementation of FileAttributes that uses the standard java library.
 * @author El Coyote Cojo
 * @version 1.0
 * @since 25-nov-2004
 */
public final class JavaFileAttributes implements FileAttributes, Serializable {
    private static final long serialVersionUID = -6232146201624232135L;
    /** Last modification time. */
    private long lastModified;

    /**
     * Obtiene los atributos del archivo file.
     *
     * @param file el archivo cuyos atributos se quiere.
     */
    @Override
	public void grabAttributes(final File file) {
       lastModified = file.lastModified();
        //if(inFile.isHidden()) ...
        //if(inFile.????()) outFile.setReadOnly();
    }

    /**
     * Aplica al archivo file los atributos presumiblemente obtenidos
     * previamente de el u otro archivo.
     *
     * @param file el archivo al que se quiere aplicar los atributos .
     */
    @Override
	public void applyAttributes(final File file) {
         file.setLastModified(lastModified);
    }
}
