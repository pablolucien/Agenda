package org.pclg.security;

import org.pclg.log.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 21-ene-2008 16:14:08
 */
public final class Steganograph {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.make();
	private static final int BITS_IN_INT = 32;
	private static final String MSG_LONGITUD_EXCESIVA = "La longitud del "
			+ "mensaje ({0}) es demasiado grande. Sólo se procesarán {1} bytes.";
	private static final String MSG_IMAGE_DIMENSION = "width = {0}, "
			+ "height = {1}. grabbed = {2}";
	private static final String MSG_MESSAGE_MAX_LENGTH = "Longitud máxima = {0}. Longitud del mensaje = {1}";
	private static final String MSG_MESSAGE_LENGTH = "Message length = {0}";
	private static final int MASK_BIT_0_SET = 0x00000001;
	private static final int MASK_BIT_0_CLEAR = 0xFFFFFFFE;

	private enum Nibble {LOW, HIGH}

	private Steganograph() {
	}

	public static void process(final String[] args) throws IOException,
            InterruptedException {
        LOGGER.setLevel(Level.ALL);
		DataOutputStream dataOut = null;
		try {
			final String action = args[0].toUpperCase();
			if (action.charAt(1) == 'P') {
                final Image encodedImage = encodeMessage(args[1], args[2]);
                writeImage(encodedImage, args[3]);

final int width = encodedImage.getWidth(null);
final int height = encodedImage.getHeight(null);
final int[] pixels = new int[width * height];
final PixelGrabber pixelGrabber = new PixelGrabber(encodedImage, 0, 0,
        width, height, pixels, 0, width);
final boolean grabbed = pixelGrabber.grabPixels();
LOGGER.info("grabbed " + grabbed);

final Image encodedImage1 = readImage(args[3]);
final int width1 = encodedImage.getWidth(null);
final int height1 = encodedImage.getHeight(null);
final int[] pixels1 = new int[width1 * height1];
final PixelGrabber pixelGrabber1 = new PixelGrabber(encodedImage1, 0, 0,
        width1, height1, pixels1, 0, width1);
final boolean grabbed1 = pixelGrabber1.grabPixels();
LOGGER.info("grabbed1 " + grabbed1);

writeImage(encodedImage1, args[3] + "x.gif");
LOGGER.info("Son iguales " + Arrays.equals(pixels, pixels1));
//write2File("D:/plucien/pixels1.txt", pixels1);
//write2File("D:/plucien/pixels.txt", pixels);
			} else {
				final byte[] msg = decodeMessage(args[1]);
				dataOut = new DataOutputStream(new FileOutputStream(args[2]));
				dataOut.write(msg);
			}
		} finally {
			if (dataOut != null) {
				dataOut.close();
			}
		}
	}

	private static Image readImage(final String srcImage) throws IOException {
		return ImageIO.read(new File(srcImage));
	}

	private static void writeImage(final Image image, final String fileName)
            throws IOException {
		final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		final Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.drawImage(image, 0, 0, null);
		graphics2D.dispose();
		ImageIO.write(bufferedImage, "gif", new File(fileName));
	}

	private static Image encodeMessage(final String msgFileName,
            final String srcImage) throws IOException {
		Image imageOut = null;
		DataInputStream textIn = null;
		try {
			final Image imageIn = readImage(srcImage);
			final int width = imageIn.getWidth(null);
			final int height = imageIn.getHeight(null);
			final int[] pixels = new int[width * height];
			final PixelGrabber pixelGrabber = new PixelGrabber(imageIn, 0, 0,
                    width, height, pixels, 0, width);
			final boolean grabbed = pixelGrabber.grabPixels();
			final int maxMsgLen = (width * height) / 8 - BITS_IN_INT;
			LOGGER.info(MessageFormat.format(MSG_IMAGE_DIMENSION, width, height,
                    grabbed));

			final File msgFile = new File(msgFileName);
			textIn = new DataInputStream(new FileInputStream(msgFile));
			int msgLength = (int) msgFile.length();
			LOGGER.info(MessageFormat.format(MSG_MESSAGE_MAX_LENGTH, maxMsgLen,
                    msgLength));
			if (msgLength > maxMsgLen) {
				LOGGER.severe(MessageFormat.format(MSG_LONGITUD_EXCESIVA,
                        msgLength, maxMsgLen));
				msgLength = maxMsgLen;
			}

			final byte[] text = new byte[msgLength];
			final int count = textIn.read(text);
			if (count != text.length) {
				LOGGER.severe(MessageFormat.format("Leídos sólo {0} bytes de "  
                        + "{1} esperados.", count, text.length));
			}
			encodeLength(pixels, text.length);
			encodeName(pixels, msgFile.getName());
			encodeMessage(text, pixels);

			final MemoryImageSource memoryImageSource = new MemoryImageSource(width, height, pixels, 0, width);
			imageOut = Toolkit.getDefaultToolkit().createImage(memoryImageSource);
		} catch (final InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Error", e);
			Thread.currentThread().interrupt();
		} finally {
		    if (textIn != null) {
				textIn.close();
			}
		}
try {
	kk_decodeMessage(imageOut);
} catch (final InterruptedException e) {
	LOGGER.log(Level.SEVERE, "Error", e);
	Thread.currentThread().interrupt();
}
		return imageOut;
	}

    private static void write2File(final String filename, final int[] pixels) throws IOException {
        final DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(filename));
        for (final int pixel : pixels) {
            outputStream.writeInt(pixel);
        }
        outputStream.close();
    }

    private static byte[] kk_decodeMessage(final Image encodedImage)
            throws InterruptedException, IOException {
        //final Image encodedImage = readImage("D:\\plucien\\Zero_out.gif");
		final int width = encodedImage.getWidth(null);
		final int height = encodedImage.getHeight(null);
		final int[] pixels = new int[width * height];
		final PixelGrabber pixelGrabber = new PixelGrabber(encodedImage, 0, 0,
				width, height, pixels, 0, width);
		final boolean grabbed = pixelGrabber.grabPixels();
		LOGGER.info(MessageFormat.format(MSG_IMAGE_DIMENSION, width, height, grabbed));
		final int msgLen = decodeLength(pixels);
		LOGGER.info(MessageFormat.format(MSG_MESSAGE_LENGTH, msgLen));
		final byte[] msg = new byte[msgLen];
		decodeMessage(pixels, msg);
		LOGGER.info(new String(msg));
        return msg;
	}

	private static byte[] decodeMessage(final String srcImage)
			throws InterruptedException, IOException {
		final Image encodedImage = readImage(srcImage);
		final int width = encodedImage.getWidth(null);
		final int height = encodedImage.getHeight(null);
		final int[] pixels = new int[width * height];
		final PixelGrabber pixelGrabber = new PixelGrabber(encodedImage, 0, 0,
				width, height, pixels, 0, width);
		final boolean grabbed = pixelGrabber.grabPixels();
		LOGGER.info(MessageFormat.format(MSG_IMAGE_DIMENSION, width, height, grabbed));
		final int msgLen = decodeLength(pixels);
		LOGGER.info(MessageFormat.format(MSG_MESSAGE_LENGTH, msgLen));
		final byte[] msg = new byte[msgLen];
		decodeMessage(pixels, msg);
		LOGGER.info(new String(msg));
		return msg;
	}

	/**
	 * Codifica un mensaje en el array de pixeles.
	 * @param msg El mensaje a codificar.
	 * @param pixels El array donde se codifica.
	 */
	private static void encodeMessage_new(final byte[] msg, final int[] pixels) {
		int insertPos = BITS_IN_INT;
		for (final byte datum : msg) {
			pixels[insertPos] = encodeNibble(pixels[insertPos++], datum, Nibble.HIGH);
			pixels[insertPos] = encodeNibble(pixels[insertPos++], datum, Nibble.LOW);
		}
	}

	private static void encodeMessage(final byte[] msg, final int[] pixels) {
		for (int ii = 0; ii < msg.length; ii++) {
			for (int jj = 0; jj < 8; jj++) {
//				final byte bit = getBit(msg[ii], jj);
//				pixels[(ii + BITS_IN_INT) * 8 + jj] &= MASK_BIT_0_CLEAR;
//				pixels[(ii + BITS_IN_INT) * 8 + jj] |= bit;
				final boolean on = testBit(msg[ii], jj);
				pixels[(ii + BITS_IN_INT) * 8 + jj] =
						setBit(pixels[(ii + BITS_IN_INT) * 8 + jj], 0, on);
			}
		}
	}

	/**
	 * Decodifica un mensaje de un array de pixeles.
	 * @param pixels El array de donde se decodifica.
	 * @param msg El mensaje decodificado.
	 */
	private static void decodeMessage_new(final int[] pixels, final byte[] msg) {
		int pos = BITS_IN_INT;
		for (int ii = 0; ii < msg.length; ii++) {
			msg[ii] = decodeByte(pixels[pos], pixels[pos + 1]);
			pos += 2;
		}
		LOGGER.info("decodeMessage = " + new String(msg));
	}


	private static void decodeMessage(final int[] pixels, final byte[] msg/*, final int msgLen*/) {
		for (int ii = BITS_IN_INT; ii < msg.length + BITS_IN_INT; ii++) {
			for (int jj = 0; jj < 8; jj++) {
				msg[ii - BITS_IN_INT] |= (byte) ((pixels[ii * 8 + jj] &= MASK_BIT_0_SET) << jj);
			}
		}
	}

	private static void encodeName(final int[] pixels, final String name) {
	}

	private static void encodeLength(final int[] pixels, final int length) {
		LOGGER.info("Codificando la longitud: " + length);
		for (int ii = 0; ii < 4; ii++) {
			for (int jj = 0; jj < 8; jj++) {
				final int bitPos = ii * 8 + jj;
				final byte bit = (byte) (length >> bitPos);
				pixels[bitPos] &= MASK_BIT_0_CLEAR;
				pixels[bitPos] |= bit;
				LOGGER.info(MessageFormat.format("bit {0} = {1}", bitPos, bit & 0x01));
			}
		}
	}

	private static int decodeLength(final int[] pixels) {
		int length = 0;
		for (int ii = 0; ii < 4; ii++) {
			for (int jj = 0; jj < 8; jj++) {
				final int bitPos = ii * 8 + jj;
				length |= (pixels[bitPos] & MASK_BIT_0_SET) << bitPos;
                //LOGGER.info(MessageFormat.format("bit {0} = {1}", bitPos, bit & 0x01));
			}
		}
        LOGGER.info(MessageFormat.format("Decodificando la longitud: {0}", length));
		return length;
	}

	/**
	 * Codifica el nibble en cada 8 bits del int.
	 *
	 * @param pixel el int que representa un pixel RGBA.
	 * @param datum el dato a codificar.
	 * @param nibble indica si es el nibble más o menos significativo.
	 * @return en dato codificado en el int.
	 */
	private static int encodeNibble(final int pixel, final byte datum, final Nibble nibble) {
		final int basePos = nibble == Nibble.LOW ? 0 : 4;
		int retValue = pixel;
		for (int ii = 0; ii < 4; ii++) {
			retValue = setBit(retValue, ii * 8, testBit(datum, basePos + ii));
		}
		return retValue;
	}

	private static byte decodeByte(final int pixelHigh, final int pixelLow) {
		byte retVal = 0;
		for (int ii = 0; ii < 4; ii++) {
			retVal |= testBit(pixelHigh, ii * 8) ? 1 << ii + 4 : 0;
			retVal |= testBit(pixelLow, ii * 8) ? 1 << ii : 0;
		}
		return retVal;
	}
//	private static byte getBit(final byte data, final int pos) {
//		return (byte) (data >> pos & 0x01);
//	}

	private static boolean testBit(final int data, final int pos) {
		return (data >> pos & 0x01) != 0;
	}

	private static int setBit(final int data, final int pos, final boolean on) {
		int retVal = data;
		if (on) {
			retVal |= 0x01 << pos;
		} else {
			retVal &= ~(0x01 << pos);
		}
		return retVal;
	}
}
