package org.pclg.tools;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Pablo
 * @since 09-abr-2009 12:32:44
 */
public final class ImageTools {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	public static final Icon NULL_ICON = new ImageIcon();

	private ImageTools() {
	}

//	/**
//	 * Tomado de ?????
//	 * <p/>
//	 * <p/>
//	 * loadbitmap() method converted from Windows C code.
//	 * Reads only uncompressed 24- and 8-bit images. Tested with
//	 * images saved using Microsoft Paint in Windows 95. If the image
//	 * is not a 24- or 8-bit image, the program refuses to even try.
//	 * I guess one could include 4-bit images by masking the byte
//	 * by first 1100 and then 0011. I am not really
//	 * interested in such images. If a compressed image is attempted,
//	 * the routine will probably fail by generating an IOException.
//	 * Look for variable ncompression to be different from 0 to indicate
//	 * compression is present.
//	 * <p/>
//	 * Arguments:
//	 * sdir and sfile are the result of the FileDialog()
//	 * getDirectory() and getFile() methods.
//	 * <p/>
//	 * Returns:
//	 * Image Object, be sure to check for (Image)null !!!!
//
//	 * @param fs InputStream providing the image.
//	 * @return Image Object, be sure to check for (Image)null !!!!
//	 */
//	public static Image loadbitmap(final InputStream fs) {
//		final Image image;
//		try {
//			final int bflen = 14; // 14 byte BITMAPFILEHEADER
//			final byte[] bf = new byte[bflen];
//			fs.read(bf, 0, bflen);
//			final int bilen = 40; // 40-byte BITMAPINFOHEADER
//			final byte[] bi = new byte[bilen];
//			fs.read(bi, 0, bilen);
//
//			// Interperet data.
////            int nsize = (((int) bf[5] & 0xff) << 24)
////                    | (((int) bf[4] & 0xff) << 16)
////                    | (((int) bf[3] & 0xff) << 8)
////                    | (int) bf[2] & 0xff;
////          LOGGER.info("File type is :"+(char)bf[0]+(char)bf[1]);
////          LOGGER.info("Size of file is :"+nsize);
//
////            int nbisize = (((int) bi[3] & 0xff) << 24)
////                    | (((int) bi[2] & 0xff) << 16)
////                    | (((int) bi[1] & 0xff) << 8)
////                    | (int) bi[0] & 0xff;
////          LOGGER.info("Size of bitmapinfoheader is :"+nbisize);
//
//			final int nwidth = (((int) bi[7] & 0xff) << 24)
//					| (((int) bi[6] & 0xff) << 16)
//					| (((int) bi[5] & 0xff) << 8)
//					| (int) bi[4] & 0xff;
////          LOGGER.info("Width is :"+nwidth);
//
//			final int nheight = (((int) bi[11] & 0xff) << 24)
//					| (((int) bi[10] & 0xff) << 16)
//					| (((int) bi[9] & 0xff) << 8)
//					| (int) bi[8] & 0xff;
////          LOGGER.info("Height is :"+nheight);
//
//			//int nplanes = (((int) bi[13] & 0xff) << 8) | (int) bi[12] & 0xff;
////          LOGGER.info("Planes is :"+nplanes);
//
//			final int nbitcount = (((int) bi[15] & 0xff) << 8)
//					| (int) bi[14] & 0xff;
////          LOGGER.info("BitCount is :"+nbitcount);
//
//			// Look for non-zero values to indicate compression
////            int ncompression = (((int) bi[19]) << 24)
////                    | (((int) bi[18]) << 16)
////                    | (((int) bi[17]) << 8)
////                    | (int) bi[16];
////          LOGGER.info("Compression is :"+ncompression);
//
//			int nsizeimage = (((int) bi[23] & 0xff) << 24)
//					| (((int) bi[22] & 0xff) << 16)
//					| (((int) bi[21] & 0xff) << 8)
//					| (int) bi[20] & 0xff;
//
//			// Some bitmaps do not have the sizeimage field calculated
//			// Ferret out these cases and fix 'em.
//			if (nsizeimage == 0) {
//				nsizeimage = ((((nwidth * nbitcount) + 31) & ~31) >> 3);
//				nsizeimage *= nheight;
////              LOGGER.info("nsizeimage (backup) is"+nsizeimage);
//			}
////          LOGGER.info("SizeImage is :"+nsizeimage);
//
////            int nxpm = (((int) bi[27] & 0xff) << 24)
////                    | (((int) bi[26] & 0xff) << 16)
////                    | (((int) bi[25] & 0xff) << 8)
////                    | (int) bi[24] & 0xff;
////          LOGGER.info("X-Pixels per meter is :"+nxpm);
//
////            int nypm = (((int) bi[31] & 0xff) << 24)
////                    | (((int) bi[30] & 0xff) << 16)
////                    | (((int) bi[29] & 0xff) << 8)
////                    | (int) bi[28] & 0xff;
////          LOGGER.info("Y-Pixels per meter is :"+nypm);
//
//			final int nclrused = (((int) bi[35] & 0xff) << 24)
//					| (((int) bi[34] & 0xff) << 16)
//					| (((int) bi[33] & 0xff) << 8)
//					| (int) bi[32] & 0xff;
////          LOGGER.info("Colors used are :"+nclrused);
//
////            int nclrimp = (((int) bi[39] & 0xff) << 24)
////                    | (((int) bi[38] & 0xff) << 16)
////                    | (((int) bi[37] & 0xff) << 8)
////                    | (int) bi[36] & 0xff;
////          LOGGER.info("Colors important are :"+nclrimp);
//
//			if (nbitcount == 24) {
//				// No Palatte data for 24-bit format but scan lines are
//				// padded out to even 4-byte boundaries.
//				final int npad = (nsizeimage / nheight) - nwidth * 3;
//				final int[] ndata = new int[nheight * nwidth];
//				final byte[] brgb = new byte[(nwidth + npad) * 3 * nheight];
//				fs.read(brgb, 0, (nwidth + npad) * 3 * nheight);
//				int nindex = 0;
//				for (int j = 0; j < nheight; j++) {
//					for (int i = 0; i < nwidth; i++) {
//						ndata[nwidth * (nheight - j - 1) + i] =
//								(255 & 0xff) << 24
//										| (((int) brgb[nindex + 2] & 0xff)
//										<< 16)
//										| (((int) brgb[nindex + 1] & 0xff) << 8)
//										| (int) brgb[nindex] & 0xff;
//						/*   LOGGER.info("Encoded Color at ("
//														+i+","+j+")is:"+nrgb+" (R,G,B)= ("
//														+((int)(brgb[2]) & 0xff)+","
//														+((int)brgb[1]&0xff)+","
//														+((int)brgb[0]&0xff)+")");
//												*/
//						nindex += 3;
//					}
//					nindex += npad;
//				}
//				image = Toolkit.getDefaultToolkit().createImage(
//						new MemoryImageSource(nwidth, nheight, ndata, 0,
//								nwidth));
//			} else if (nbitcount == 8) {
//				// Have to determine the number of colors, the clrsused
//				// parameter is dominant if it is greater than zero. If
//				// zero, calculate colors based on bitsperpixel.
//				int nNumColors = 0;
//				if (nclrused > 0) {
//					nNumColors = nclrused;
//				} else {
//					nNumColors = (1 & 0xff) << nbitcount;
//				}
////       LOGGER.info("The number of Colors is"+nNumColors);
//
//				// Read the palatte colors.
//				final int[] npalette = new int[nNumColors];
//				final byte[] bpalette = new byte[nNumColors * 4];
//				fs.read(bpalette, 0, nNumColors * 4);
//				int nindex8 = 0;
//				for (int n = 0; n < nNumColors; n++) {
//					npalette[n] = (255 & 0xff) << 24
//							| (((int) bpalette[nindex8 + 2] & 0xff) << 16)
//							| (((int) bpalette[nindex8 + 1] & 0xff) << 8)
//							| (int) bpalette[nindex8] & 0xff;
//					/*
//												LOGGER.info("Palette Color "+n
//										  +" is:"+npalette[n]+" (res,R,G,B)= ("
//										  +((int)(bpalette[nindex8+3]) & 0xff)+","
//										  +((int)(bpalette[nindex8+2]) & 0xff)+","
//										  +((int)bpalette[nindex8+1]&0xff)+","
//										  +((int)bpalette[nindex8]&0xff)+")");
//										*/
//					nindex8 += 4;
//				}
//
//				// Read the image data (actually indices into the palette)
//				// Scan lines are still padded out to even 4-byte
//				// boundaries.
//				final int npad8 = (nsizeimage / nheight) - nwidth;
////       LOGGER.info("nPad is:"+npad8);
//
//				final int[] ndata8 = new int[nwidth * nheight];
//				final byte[] bdata = new byte[(nwidth + npad8) * nheight];
//				fs.read(bdata, 0, (nwidth + npad8) * nheight);
//				nindex8 = 0;
//				for (int j8 = 0; j8 < nheight; j8++) {
//					for (int i8 = 0; i8 < nwidth; i8++) {
//						ndata8[nwidth * (nheight - j8 - 1) + i8] =
//								npalette[((int) bdata[nindex8] & 0xff)];
//						nindex8++;
//					}
//					nindex8 += npad8;
//				}
//
//				image = Toolkit.getDefaultToolkit()
//						.createImage(new MemoryImageSource(nwidth, nheight,
//								ndata8, 0, nwidth));
//			} else {
//				LOGGER.warn("Not a 24-bit or 8-bit Windows Bitmap, aborting...");
//				image = null;
//			}
//
//			fs.close();
//			return image;
//		} catch (final Exception ex) {
//			LOGGER.warn("Caught exception in loadbitmap!");
//			ToolBox.showInfo(ex);
//		}
//		return null;
//	}

//	/** Guarda una imagen en formato jpeg.
//	 * TODO: Probar este m�todo.
//	 */
//	public static boolean saveJPEG(final Image img, final String filename)
//			throws IOException {
//		final OutputStream os = new FileOutputStream(filename);
//		final BufferedImage outImage = new BufferedImage(img.getWidth(null),
//				img.getHeight(null), BufferedImage.TYPE_INT_RGB);
//		final Graphics2D g2d = outImage.createGraphics();
//		g2d.drawImage(img, null, null);
////		final JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
////		encoder.encode(outImage);
//		os.close();
//		return true;
//	}

	/**
	 * Guarda una imagen en formato bmp de 24 bits sin comprimir de Windows.
	 * ver el archivo bmp.doc (bmp.txt) que esta dentro del zip de xv (dino.zip)
	 * 2000.10.18, 10:40 a 2000.10.18 13:40  // Todo el tiempo perdido en el
	 * flip de la imagen
	 * // ??? Ademas hay que hacer el pad a multiplos de 32 pixeles
	 */
	public static boolean saveBitmap(final Image img, final String filename)
			throws IOException {
		final OutputStream os = new FileOutputStream(filename);
		final boolean result = saveBitmap(img, os);
		os.close();
		return result;
	}

	/**
	 * Guarda una imagen en formato bmp de 24 bits sin comprimir de Windows.
	 * ver el archivo bmp.doc (bmp.txt) que esta dentro del zip de xv (dino.zip)
	 * 2000.10.18, 10:40 a 2000.10.18 13:40  // Todo el tiempo perdido en el
	 * flip de la imagen
	 * // ??? Ademas hay que hacer el pad a multiplos de 32 pixeles
	 */
	public static boolean saveBitmap(final Image img, final OutputStream os)
			throws IOException {
		final int width = img.getWidth(null);
		final int height = img.getHeight(null);

		// ??? Explicar los numeros magicos
		// ??? Ademas hay que hacer el pad a multiplos de 32 bits
		//int padSize = (32 - width % 32) % 32;

		// cantidad de bytes
//        int bytesNr = (int) Math.ceil(width / 8.0);
		int padSize = (int) Math.ceil(width / 32.0) - width / 32;
		padSize = 0;
		while ((width + padSize) * 24 / 32 * 32 != (width + padSize) * 24) {
			padSize++;
		}
		//padSize = (int)Math.ceil(padSize / 8.0);
		if ((width + padSize) == 36) {
			padSize += 4;
		}
		LOGGER.info("padSize = " + padSize);
		LOGGER.info("width + padSize = " + (width + padSize));

//padSize = 0;

		final int[] bitmapInts = new int[(width + padSize) * height];
		final byte[] bitmapBytes = new byte[bitmapInts.length
				* 3];	// usamos 3 bytes de cada int
		final int fileSize = bitmapBytes.length + 54;
		final byte[] bitmapFileHeader = new byte[14];
		// The BITMAPFILEHEADER structure contains information about the type, size, and layout of a device-independent bitmap (DIB) file.

		// Member		Description
		// bfType		Specifies the type of file. This member must be BM.
		bitmapFileHeader[0] = 'B';
		bitmapFileHeader[1] = 'M';
		// bfSize		Specifies the size of the file, in bytes.
		// (little endian).
		bitmapFileHeader[2] = (byte) (fileSize);
		bitmapFileHeader[3] = (byte) (fileSize >> 8);
		bitmapFileHeader[4] = (byte) (fileSize >> 16);
		bitmapFileHeader[5] = (byte) (fileSize >> 24);
		// bfReserved1	Reserved; must be set to zero.
		bitmapFileHeader[6] = 0;
		bitmapFileHeader[7] = 0;
		// bfReserved2	Reserved; must be set to zero.
		bitmapFileHeader[8] = 0;
		bitmapFileHeader[9] = 0;
		// bfOffBits	Specifies the byte offset from the BITMAPFILEHEADER structure to the actual bitmap data in the file.
		// en nuestro caso 54 (little endian).
		bitmapFileHeader[10] = 54;
		bitmapFileHeader[11] = 0;
		bitmapFileHeader[12] = 0;
		bitmapFileHeader[13] = 0;

		final byte[] bitmapInfoHeader = new byte[40];

		//???? hacer un calloc que me lo devuelva con ceros
		for (int i = 0; i < bitmapInfoHeader.length; i++) {
			bitmapInfoHeader[i] = (byte) 0;
		}

		//	size of bitmapInfoHeader
		// (little endian).
		bitmapInfoHeader[0] = 40;
		bitmapInfoHeader[1] = 0;
		bitmapInfoHeader[2] = 0;
		bitmapInfoHeader[3] = 0;
		//	width
		bitmapInfoHeader[4] = (byte) (width);
		bitmapInfoHeader[5] = (byte) (width >> 8);
		bitmapInfoHeader[6] = (byte) (width >> 16);
		bitmapInfoHeader[7] = (byte) (width >> 24);
		//	height
		bitmapInfoHeader[8] = (byte) (height);
		bitmapInfoHeader[9] = (byte) (height >> 8);
		bitmapInfoHeader[10] = (byte) (height >> 16);
		bitmapInfoHeader[11] = (byte) (height >> 24);
		// planes
		bitmapInfoHeader[12] = 1;
		bitmapInfoHeader[13] = 0;
		// bit count
		bitmapInfoHeader[14] = 24;
		bitmapInfoHeader[15] = 0;

		// compresion
		bitmapInfoHeader[16] = 0;
		bitmapInfoHeader[17] = 0;
		bitmapInfoHeader[18] = 0;
		bitmapInfoHeader[19] = 0;

		// size image
		bitmapInfoHeader[20] = 0;
		bitmapInfoHeader[21] = 0;
		bitmapInfoHeader[22] = 0;
		bitmapInfoHeader[23] = 0;

		// biXPelsPerMeter
		bitmapInfoHeader[24] = (byte) 0xc0;
		bitmapInfoHeader[25] = 0x1e;
		bitmapInfoHeader[26] = 0;
		bitmapInfoHeader[27] = 0;

		// biYPelsPerMeter
		bitmapInfoHeader[28] = (byte) 0xc0;
		bitmapInfoHeader[29] = 0x1e;
		bitmapInfoHeader[30] = 0;
		bitmapInfoHeader[31] = 0;

		// biClrUsed
		bitmapInfoHeader[32] = 0;
		bitmapInfoHeader[33] = 0;
		bitmapInfoHeader[34] = 0;
		bitmapInfoHeader[35] = 0;

		// biClrImportant
		bitmapInfoHeader[36] = 0;
		bitmapInfoHeader[37] = 0;
		bitmapInfoHeader[38] = 0;
		bitmapInfoHeader[39] = 0;

		//???? hacer un calloc que me lo devuelva con ceros
		for (int i = 0; i < bitmapInts.length; i++) {
			bitmapInts[i] = 0;
		}

		final PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height,
				bitmapInts,
				0, width + padSize);
		try {
			pg.grabPixels();
		} catch (final InterruptedException ex) {
			LOGGER.warn("interrupted waiting for pixels!");
			Thread.currentThread().interrupt();
			return false;
		}

		// Hay que hacer magia, porque la imagen almacena los pixels de
		// arriba abajo y de izq a derecha, mientras que el bmp lo hace
		// de abajo arriba y de izq a derecha.
		// Ademas no se muy bien si es RGB o BGR o su puta madre!!!

		// Hay que hacer un swap de las lineas de abajo arriba y viceversa
		final int[] tmpInts = new int[width + padSize];
		for (int i = 0; i < height / 2; i++) {
			System.arraycopy(bitmapInts, i * (width + padSize), tmpInts, 0,
					width + padSize);
			System.arraycopy(bitmapInts, (height - i - 1) * (width + padSize),
					bitmapInts, i * (width + padSize), width + padSize);
			System.arraycopy(tmpInts, 0, bitmapInts,
					(height - i - 1) * (width + padSize), width + padSize);
		}

		for (int i = 0; i < bitmapInts.length; i++) {
			// alfa  = (byte) bitmapInts[i] >> 24;
			// R
			bitmapBytes[i * 3] = (byte) (bitmapInts[i]);
			// G
			bitmapBytes[i * 3 + 1] = (byte) (bitmapInts[i] >> 8);
			// B
			bitmapBytes[i * 3 + 2] = (byte) (bitmapInts[i] >> 16);
		}

		os.write(bitmapFileHeader);
		os.write(bitmapInfoHeader);
		os.write(bitmapBytes);
		return true;
	}

	private static final Map<String, ImageIcon> IMAGE_ICON_CACHE = new HashMap<>();

	public static void clearIconCache() {
    	IMAGE_ICON_CACHE.clear();
    }

    /**
     * Obtains an icon given its url as a string. The icon is cached for 
     * subsecuents requests.
     * 
     * @param resource the resource url as a string.
     * @return an Icon or <code>null</code>
     */
    public static Optional<ImageIcon> getImageIcon(final String resource) {
        return Optional.ofNullable(IMAGE_ICON_CACHE.computeIfAbsent(resource, path -> {
			ImageIcon icon = null;
			LOGGER.debug(path + " no est� en cache; trato de crearlo");
			final URL url = ImageTools.class.getResource(path);
			if (url == null) {
				LOGGER.warn("Imposible crear " + path);
			} else {
				icon = new ImageIcon(url);
			}
			return icon;
		}));
    }
}
