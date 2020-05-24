package org.pclg.media.imagetools;

import javax.swing.JFileChooser;
import java.io.File;

/**
 * Un Chooser para elegir y previsualizar imágenes.
 *
 * @author Unknown.
 * @version Unknown.
 */
public final class ImageChooser extends JFileChooser {
    private static final long serialVersionUID = -8416703163814081121L;

    /**
     * Construye un ImageChooser posicionado en el directorio home del usuario.
     */
    public ImageChooser() {
        //this(System.getProperty("user.dir"));
        this(System.getProperty("user.home"));
    }

    /**
     * Construye un ImageChooser posicionado en el directorio que se le
     * pasa como argumento.
     *
     * @param dir el directorio donde posicionarse.
     */
    public ImageChooser(final String dir) {
        super(new File(dir).getAbsoluteFile());
        final ImageFilter imageFilter = new ImageFilter();
        addChoosableFileFilter(imageFilter);
        setFileFilter(imageFilter);
        setFileView(new ImageFileView());
        setAccessory(new ImagePreview(this));
    }
}
