package tests;

import org.pclg.log.LoggerFactory;
import org.pclg.tools.ToolBox;

import java.util.logging.Logger;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23-ene-2008 17:01:00
 */
final class BitOperationsTest {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.make();
	private enum Nibble {LOW, HIGH}

	private BitOperationsTest() {
//		final int pos = 7;
//		final int pos2 = 3;
//		for (int value = 8; value < 50; value++) {
//			test(value, pos, true);
//			test(value, pos2, false);
//		}
		final int value = 0;
		final byte data =  (byte) 151;
		System.out.println("data = " + data);
		final int highNibble = encodeNibble(value, data, Nibble.HIGH);
		final int lowNibble = encodeNibble(value, data, Nibble.LOW);
		System.out.println(ToolBox.leftPad(Integer.toBinaryString(data), 8, '0').substring(24) + " - "
				+ ToolBox.leftPad(Integer.toBinaryString(highNibble), 32, '0') + " - "
				+ ToolBox.leftPad(Integer.toBinaryString(lowNibble), 32, '0') + " - "
				+ ToolBox.leftPad(Integer.toBinaryString(decodeByte(highNibble, lowNibble)), 8, '0').substring(24));
	}

	private static int encodeNibble(final int pixel, final byte b, final Nibble nibble) {
		final int basePos = nibble == Nibble.LOW ? 0 : 4;
		int retValue = pixel;
		for (int ii = 0; ii < 4; ii++) {
			retValue = setBit(retValue, ii * 8, testBit(b, basePos + ii));
		}
		return retValue;
	}

	private static byte decodeByte(final int pixelHigh, final int pixelLow) {
		byte retVal = 0;
		for (int ii = 0; ii < 4; ii++) {
			retVal |= testBit(pixelHigh, ii * 8) ? 1 << (ii + 4) : 0;
			retVal |= testBit(pixelLow, ii * 8) ? 1 << ii : 0;
		}
		return retVal;
	}


	private void test(final int value, final int pos, final boolean on) {
		System.out.println(ToolBox.leftPad(String.valueOf(value), 2, ' ') + " - "
				+ ToolBox.leftPad(Integer.toBinaryString(value), 10, '0') + "\n   - "
				+ ToolBox.leftPad(Integer.toBinaryString(setBit(value, pos, on)), 10, '0') + '\n');
	}

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

	public static void main(final String[] args) {
		new BitOperationsTest();
	}
}
