package tests;

import org.pclg.log.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


class ImageIOTest {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.make();
	private static final int CAPACITY = 1024;


	private ImageIOTest() {
		final Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("jpeg");
		final StringBuilder builder = new StringBuilder(CAPACITY);
		while (iterator.hasNext()) {
			final ImageReader imageReader = iterator.next();
			builder.setLength(0);
			LOGGER.log(Level.INFO, builder
					.append("imageReader = ").append(imageReader).toString());
		}
	}

	public static void main(final String[] args) {
		new ImageIOTest();
	}
}
