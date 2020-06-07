package tests;

class InternTest {
	private static final int LIMIT = 1000000;

	public static void main(final String[] args) {
		final String s1 = "pepito";
		final String s2 = "luisito";
		final String s3 = new String("pepito");
		int dummy = 0;
		long start = System.currentTimeMillis();
		for (int ii = 0; ii < LIMIT; ii++) {
			if (s1.intern() == s2.intern()) {
				dummy++;
			}
		}
		System.out.println("intern() diferentes: " + (System.currentTimeMillis() - start));


		dummy = 0;
		start = System.currentTimeMillis();
		for (int ii = 0; ii < LIMIT; ii++) {
			if (s1.equals(s2)) {
				dummy++;
			}
		}
		System.out.println("equals() diferentes: " + (System.currentTimeMillis() - start));

		dummy = 0;
		start = System.currentTimeMillis();
		for (int ii = 0; ii < LIMIT; ii++) {
			if (s1.intern() == s2.intern()) {
				dummy++;
			}
		}
		System.out.println("intern() iguales: " + (System.currentTimeMillis() - start));


		dummy = 0;
		start = System.currentTimeMillis();
		for (int ii = 0; ii < LIMIT; ii++) {
			if (s1.equals(s3)) {
				dummy++;
			}
		}
		System.out.println("equals() iguales: " + (System.currentTimeMillis() - start));

		dummy = 0;
		start = System.currentTimeMillis();
		for (int ii = 0; ii < LIMIT; ii++) {
			if (s1.intern() == s1.intern()) {
				dummy++;
			}
		}
		System.out.println("intern() identicas: " + (System.currentTimeMillis() - start));


		dummy = 0;
		start = System.currentTimeMillis();
		for (int ii = 0; ii < LIMIT; ii++) {
			if (s1.equals(s1)) {
				dummy++;
			}
		}
		System.out.println("equals() identicas: " + (System.currentTimeMillis() - start));


	}
}