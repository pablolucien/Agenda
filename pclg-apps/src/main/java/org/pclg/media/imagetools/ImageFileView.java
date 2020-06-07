package org.pclg.media.imagetools;

import org.pclg.tools.FileTools;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;
import java.io.File;

/**
 * A FileView to view Images.
 *
 * @author Unknown.
 * @version Unknown
 */
final class ImageFileView extends FileView {
    /** An icon. */
    private final ImageIcon jpgIcon = new ImageIcon("images/jpgIcon.gif");

    /** An icon. */
    private final ImageIcon gifIcon = new ImageIcon("images/gifIcon.gif");

    /** An icon. */
    private final ImageIcon tiffIcon = new ImageIcon("images/tiffIcon.gif");

    @Override
    public String getName(final File file) {
        return null; // let the L&F FileView figure this out
    }

    @Override
    public String getDescription(final File file) {
        return null; // let the L&F FileView figure this out
    }

    @Override
    public Boolean isTraversable(final File file) {
        return null; // let the L&F FileView figure this out
    }

    @Override
    public String getTypeDescription(final File file) {
		final String extension = FileTools.splittName(file).extension.toLowerCase();
        String type = null;

        if (extension != null) {
            // ¿Se puede hacer un chain of responsibility para determinar la desc. en vez de esto?
            switch (extension) {
                case Utils.JPEG:
                case Utils.JPG:
                    type = "JPEG Image";
                    break;
                case Utils.GIF:
                    type = "GIF Image";
                    break;
                case Utils.BMP:
                    type = "Windows bitmap";
                    break;
                case Utils.TIFF:
                case Utils.TIF:
                    type = "TIFF Image";
                    break;
            }
        }
        return type;
    }

    @Override
    public Icon getIcon(final File file) {
        final String extension = FileTools.splittName(file).extension.toLowerCase();
        Icon icon = null;

        if (extension != null) {
            switch (extension) {
                case Utils.JPEG:
                case Utils.JPG:
                    icon = jpgIcon;
                    break;
                case Utils.GIF:
                    icon = gifIcon;
                    break;
                case Utils.TIFF:
                case Utils.TIF:
                    icon = tiffIcon;
                    break;
            }
        }
        return icon;
    }
}