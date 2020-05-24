package tests;

/**
 * @author Pablo
 * @since 13-ago-2008 17:29:47
 */
public class FlawedComparator {
	public static void main(final String[] args) {
		compare(-1, 1);
		compare(1, -1);
		compare(0, 0);
	}

	@SuppressWarnings("NumberEquality")  // La idea de esta clase es precisamente mostrar que está mal ;-)
	private static void compare(final Integer i, final Integer i1) {
		if (i < i1) {
			System.out.println(i + " < " + i1);
		} else if (i > i1) {
			System.out.println(i + " > " + i1);
		} else if (i == i1) {
			System.out.println(i + " = " + i1);
		} else {
			System.err.println(" ¡Oh no, violado el principo de tricotomía! ");
		}

		System.out.println(i < i1 ? -1 : (i == i1 ? 0 : 1));
	}
}
