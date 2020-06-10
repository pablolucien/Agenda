package tests;

/**
 * @since 11-sep-2007 17:22:39
 */
public class TestAndAndAndAnd {
	public static void main(final String[] args) {
		System.out.println("con & y &");
		if (m1() & m2() & m3());
		System.out.println();
		System.out.println("con && y &&");
		if (m1() && m2() && m3());
		System.out.println();
		System.out.println("con & y &&");
		if (m1() & m2() && m3());
		System.out.println();
		System.out.println("con && y &");
		if (m1() && m2() & m3());
		System.out.println();
	}

	private static boolean m3() {
		System.out.println("En m3()");
		return false;
	}

	private static boolean m2() {
		System.out.println("En m2()");
		return false;
	}

	private static boolean m1() {
		System.out.println("En m1()");
		return false;
	}
}
