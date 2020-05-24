package org.pclg.media.imagetools;

import org.pclg.tools.FileTools;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A file filter to select Images.
 *
 * @author Unknown.
 * @version Unknown.
 */
final class ImageFilter extends FileFilter {

    /**
     * Accept all directories and all gif, jpg, or tiff files.
     *
     * @param file the file to check.
     * @return true if the file is an image file or a directory, false
     * otherwise.
     */

    @Override
    public boolean accept(final File file) {
        if (file.isDirectory()) {
            return true;
        }

		final String extension = FileTools.splittName(file).extension.toLowerCase();
        return extension != null && (
            extension.equals(Utils.TIFF)
                || extension.equals(Utils.TIF)
                || extension.equals(Utils.GIF)
                || extension.equals(Utils.BMP)
                || extension.equals(Utils.JPEG)
                || extension.equals(Utils.JPG)
                || extension.equals(Utils.PNG)
        );
    }

    /**
     * The description of this filter.
     *
     * @return The description of this filter.
     */
    @Override
    public String getDescription() {
        return "Solamente imagenes";
    }
}
