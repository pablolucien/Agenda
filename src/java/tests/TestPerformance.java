package tests;

public class TestPerformance {
	private static final long COUNT = 100000000L;
	public static void main(final String[] args) {
		System.out.println("The early bird gets the worm, but the second mouse gets the cheese.");
		test1();
		test2();
		test3();
		test4();
	}


	private static void test1() {
		final String candidate = "2";
		boolean result;
		long start = System.currentTimeMillis();
		for (long ii = 0; ii < COUNT; ii++) {
			result = candidate.equals("");
		}
		long end = System.currentTimeMillis();
		System.out.println("Con equals(): " + (end - start));

		start = System.currentTimeMillis();
		for (long ii = 0; ii < COUNT; ii++) {
			result = candidate.length() == 0;
		}
		end = System.currentTimeMillis();
		System.out.println("Con length(): " + (end - start));
		System.out.println();
	}

	private static void test2() {
		final String candidate = "2";
		boolean result;
		long start = System.currentTimeMillis();
		for (long ii = 0; ii < COUNT; ii++) {
			result = candidate.length() == 0;
		}
		long end = System.currentTimeMillis();
		System.out.println("Con length(): " + (end - start));

		start = System.currentTimeMillis();
		for (long ii = 0; ii < COUNT; ii++) {
			result = candidate.equals("");
		}
		end = System.currentTimeMillis();
		System.out.println("Con equals(): " + (end - start));
		System.out.println();
	}
	
	private static void test3() {
		final String candidate = "";
		boolean result;
		long start = System.currentTimeMillis();
		for (long ii = 0; ii < COUNT; ii++) {
			result = candidate.equals("");
		}
		long end = System.currentTimeMillis();
		System.out.println("Con equals(): " + (end - start));

		start = System.currentTimeMillis();
		for (long ii = 0; ii < COUNT; ii++) {
			result = candidate.length() == 0;
		}
		end = System.currentTimeMillis();
		System.out.println("Con length(): " + (end - start));
		System.out.println();
	}

	private static void test4() {
		final String candidate = "";
		boolean result;
		long start = System.currentTimeMillis();
		for (long ii = 0; ii < COUNT; ii++) {
			result = candidate.length() == 0;
		}
		long end = System.currentTimeMillis();
		System.out.println("Con length(): " + (end - start));

		start = System.currentTimeMillis();
		for (long ii = 0; ii < COUNT; ii++) {
			result = candidate.equals("");
		}
		end = System.currentTimeMillis();
		System.out.println("Con equals(): " + (end - start));
		System.out.println();
	}
}