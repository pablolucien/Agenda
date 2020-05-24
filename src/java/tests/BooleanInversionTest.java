package tests;

import java.math.BigDecimal;


public class BooleanInversionTest {
	private static final int LIMIT = 100000000;

	public static void main(final String[] args) {
		boolean b = true;
		long start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			b = !b; // slow
		}
		System.out.println("boolean  invert: \t\t "
			+ (System.currentTimeMillis() - start));


		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			b ^= true; // fast
		}
		System.out.println("binary   invert: \t\t "
			+ (System.currentTimeMillis() - start));


		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			b = b ? false : true;
		}
		System.out.println("ternary  invert: \t\t "
			+ (System.currentTimeMillis() - start));

		System.out.println(BooleanInversionTest.class);

		final BigDecimal decimal1 = new BigDecimal(0.1);
		final BigDecimal decimal2 = new BigDecimal("0.1");
		System.out.println("0.1 == " + decimal2 + " != " + decimal1);
	}
}

