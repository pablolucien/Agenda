package org.pclg.media.image;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * "Inspired" on https://www.techcoil.com/blog/how-to-create-a-thumbnail-of-an-image-in-java-without-using-external-libraries/
 *
 * @since 16/03/2018.
 */
public class ThumbnailCreator {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final String IO_EXCEPTION_READING_IMAGE
		= "IO exception occurred while trying to read image.";
	private static final String ERROR_WRITING_TO_FILE = "Error writing image to file";
	private static final String ERROR_WRITING_TO_MEMORY = "Error writing image to memory";
	private static final String ERROR_GETTING_IMAGE = "Could not obtain an image from ";
	private static final String FORMAT_NAME = "JPG";

	/**
     * Creates a thumbnail of an image stored in a file in the filesystem.
     *
     * @param originalImageFile the File that represents the image.
     * @return the file that contails the thumbnail.
     * @throws ThumbnailCreator.ThumbnailCreatorException if something goes wrong.
     */
    public File getThumbnailAsFile(final File originalImageFile) {
        String message = null;
        try {
            message = IO_EXCEPTION_READING_IMAGE;
            final BufferedImage resizedImage = createThumbnailFromFile(originalImageFile);
            if (resizedImage == null) {
				return null;
			}

            message = ERROR_WRITING_TO_FILE;
            final String originalImageFileNameBase = FileTools.splittName(originalImageFile.getName()).base;
            final File thumbnailFile = new File(originalImageFile.getParent(), "thumbnail_" + originalImageFileNameBase + '.' + "jpg");
            ImageIO.write(resizedImage, FORMAT_NAME, thumbnailFile);
            return thumbnailFile;
        } catch (IOException | NullPointerException ex) {
            throw new ThumbnailCreatorException(message);
        }
    }

    public InputStream getThumbnailAsStream(final File originalImageFile) {
        String message = null;
        try {
            message = IO_EXCEPTION_READING_IMAGE;
            final BufferedImage resizedImage = createThumbnailFromFile(originalImageFile);
            if (resizedImage == null) {
				return null;
			}

            message = ERROR_WRITING_TO_MEMORY;
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, FORMAT_NAME, outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException | NullPointerException ex) {
            throw new ThumbnailCreatorException(message);
        }
    }

    private BufferedImage createThumbnailFromFile(final File originalImageFile) throws IOException {
        final BufferedImage originalBufferedImage = ImageIO.read(originalImageFile);
        if (originalBufferedImage == null) {
            LOGGER.warn(ERROR_GETTING_IMAGE + originalImageFile);
            return null;
        }
        final long originalBufferedImageWidth = originalBufferedImage.getWidth();
        final long originalBufferedImageHeight = originalBufferedImage.getHeight();

        final int thumbnailWidth = 150;
        final long max = Math.max(originalBufferedImageWidth, originalBufferedImageHeight);

        final int widthToScale = (int) (thumbnailWidth * originalBufferedImageWidth / max);
        final int heightToScale = (int) (thumbnailWidth * originalBufferedImageHeight / max);
        final BufferedImage resizedImage = new BufferedImage(widthToScale, heightToScale, originalBufferedImage.getType());
        final Graphics2D g = resizedImage.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalBufferedImage, 0, 0, widthToScale, heightToScale, null);
        g.dispose();
        return resizedImage;
    }

    public class ThumbnailCreatorException extends RuntimeException {
        public ThumbnailCreatorException(final String message) {
			super(message);
        }
    }
}
