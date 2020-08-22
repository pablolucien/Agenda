package tests;

final class ASCII {
	public static void main(final String[] args) {
		for (int ii = 128; ii < 256; ii++) {
			System.out.println(ii + " --> " + (char) ii);
		}
	}
}