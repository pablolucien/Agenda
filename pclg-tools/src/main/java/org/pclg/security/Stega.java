package org.pclg.security;
/*
 * Created on 22-feb-2006
 *
 */

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author plucien
 *
 */
public class Stega {

    private static final String MSG_USAGE = "Usage: java org.pclg.security.Stega <arch_origen> <arch_destino>";
    private static final String INPUT_IMAGE_NAME = "c:/tmp/declarationdeguerre.gif";
    private static final String OUTPUT_IMAGE_NAME = "c:/tmp/declarationdeguerre2.gif";
    private static final String FILE_NAME = "c:/tmp/examen_final_gestion_proyectos.txt";

    /**
     *
     * @param args
     * @throws IOException
     */
	private Stega(final String[] args) throws Exception {
		final BufferedImage inputImage = readImage(INPUT_IMAGE_NAME);
    	final byte[] fileContent = readFile(FILE_NAME);
		final BufferedImage outputImage = procesarArchivoEnImagen(fileContent, inputImage);
    	escribirImagen(outputImage);
    }


    /**
	 * @param outputImage
	 */
	private void escribirImagen(final Image outputImage) {
		//ImageIO.write(outputImage, "gif", new File(OUTPUT_IMAGE_NAME));
	}


	/**
	 * @param fileContent
	 * @param inputImage
	 * @return
	 */
	private BufferedImage procesarArchivoEnImagen(final byte[] fileContent, final BufferedImage inputImage) throws InterruptedException {
		final int[] pixels = getPixels(inputImage);
		final int width = inputImage.getWidth();
		final int height = inputImage.getHeight();
		final MemoryImageSource source = new MemoryImageSource(width, height, pixels, 0, width);
		final Image outputImage = Toolkit.getDefaultToolkit().createImage(source);
		putInImage(fileContent, pixels);
		return null;
		
//		inputImage.setRGB( 0, 0, width, height, pixels, 0, width);
	}

	private void putInImage(final byte[] sourceContent, final int[] pixels) {
		final int byteCount = Math.min(sourceContent.length * 8, pixels.length);
		for (int ii = 0; ii < byteCount; ii++) {

		}
	}

	void handlesinglepixel(final int x, final int y, final int pixel) {
			final int alpha = (pixel >> 24) & 0xff;
			final int red   = (pixel >> 16) & 0xff;
			final int green = (pixel >>  8) & 0xff;
			final int blue  = (pixel      ) & 0xff;
			// Deal with the pixel as necessary...
	 }

	 public void handlepixels(final Image img, final int x, final int y, final int w, final int h) {
			final int[] pixels = new int[w * h];
			final PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
			try {
				pg.grabPixels();
			} catch (final InterruptedException e) {
				System.err.println("interrupted waiting for pixels!");
				Thread.currentThread().interrupt();
				return;
			}
			if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
				System.err.println("image fetch aborted or errored");
				return;
			}
			for (int j = 0; j < h; j++) {
				for (int i = 0; i < w; i++) {
					handlesinglepixel(x+i, y+j, pixels[j * w + i]);
				}
			}
	 }


	/**
	 * @param inputImage
	 * @return
	 */
	private int[] getPixels(final BufferedImage inputImage) throws InterruptedException {
		final int width = inputImage.getWidth();
		final int height = inputImage.getHeight();
		final int[] pixels = new int[width * height];
		final PixelGrabber pg = new PixelGrabber(inputImage, 0, 0, width, height, pixels, 0, width);
		pg.grabPixels();
		return pixels;
	}


	/**
     * @param filename
     * @return
     * @throws IOException
	 */
	private byte[] readFile(final String fileName) throws IOException {
		final File source = new File(fileName);
		final byte[] sourceContent = new byte[(int) source.length()];
		final FileInputStream fis = new FileInputStream(source);
		if (fis.read(sourceContent) != sourceContent.length) {
			throw new IOException("Unexpected end of Stream");
		}
		fis.close();
		return sourceContent;
	}




	/**
	 * @param imageName
	 * @return
	 */
	private BufferedImage readImage(final String imageName) throws IOException {
		return ImageIO.read(new File(imageName));
	}

    /**
     *
     * @param args
     * @throws IOException
     */
    public static final void main(final String[] args) throws Exception {
        if (args.length != 2) {
            usage();
            System.exit(1);
        }
        new Stega(args);
    }

    /**
     *
     */
    private static final void usage() {
        System.err.println(MSG_USAGE);
    }
}
