package tests;

/**
 * @author El Coyote Cojo
 * @since 26-jul-2007 12:42:11
 */
public class TestStackTrace {
	private static void probar1() {
		probar2();
	}

	private static void probar2() {
		probar3();
	}

	private static void probar3() {
		probar4();
	}

	private static void probar4() {
		try {
			if (true) {
				throw new RuntimeException("La donna  mobile");
			}
		} catch (final Exception e) {
			final StackTraceElement[] trace = e.getStackTrace();
			for (int ii = 0; ii < trace.length; ii++) {
				final StackTraceElement stackTraceElement = trace[ii];
				System.err.println("Elemento de pila: " + ii);
				System.err.println("Archivo: " + trace[ii].getFileName());
				System.err.println("Clase: " + trace[ii].getClassName());
				System.err.println("Mtodo: " + trace[ii].getMethodName());
				System.err.println("Lnea: " + trace[ii].getLineNumber());
				System.err.println("----------");
			}
		} finally {
		}
	}

	public static void main(final String[] args) {
		probar1();
	}
}
