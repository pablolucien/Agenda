package tests;

import org.pclg.log.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: pacelucien Date: 06-may-2009 Time: 17:42:05
 * To change this template use File | Settings | File Templates.
 */
public class TestPixelGrabber {
    /** El logger. */
    private static final Logger LOGGER = LoggerFactory.make();

    public static void main(final String[] args) throws InterruptedException, IOException {
        System.out.println(Arrays.toString(ImageIO.getReaderFormatNames()));
        System.out.println(Arrays.toString(ImageIO.getReaderFileSuffixes()));
        String imageFile = "D:/plucien/Zero_out.gif";
        final int[] pixels = getPixels(imageFile);

        imageFile = "D:/plucien/Zero_out.gifx.gif";
        final int[] pixels1 = getPixels(imageFile);

        LOGGER.info("Son iguales " + Arrays.equals(pixels, pixels1));

//write2File("D:/plucien/pixels1.txt", pixels1);
//write2File("D:/plucien/pixels.txt", pixels);
    }

    private static int[] getPixels(final String imageFile) throws IOException, InterruptedException {
        final Image encodedImage = readImage(imageFile);
        final int width = encodedImage.getWidth(null);
        final int height = encodedImage.getHeight(null);
        final int[] pixels = new int[width * height];
        final PixelGrabber pixelGrabber = new PixelGrabber(encodedImage, 0, 0,
                width, height, pixels, 0, width);
        final boolean grabbed = pixelGrabber.grabPixels();
        LOGGER.info("grabbed " + grabbed);
        return pixels;
    }

    private static Image readImage(final String srcImage) throws IOException {
        return ImageIO.read(new File(srcImage));
    }
}
