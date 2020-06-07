package tests;

/**
 * @author SOPRA PROFIT
 * @since 03-oct-2007 16:52:51
 */
public final class TestStringBufferVsArray {
	private static final int LIMIT = 100000;
	private static final int SHORT_LEN = 10;
	private static final int LONG_LEN = 1000;
	private static final int ROUNDS = 20;

	private TestStringBufferVsArray() {
	}

	/**
	 * Devuelve un String con el número de blancos pasado como parámetro
	 *
	 * @param longitud
	 *
	 * @return
	 */
	private static String ajustarEspacios(final int longitud) {
		final StringBuilder datoAjustado = new StringBuilder(longitud);
		for (int i = 0; i < longitud; i++) {
			datoAjustado.append(' ');
		}
		return datoAjustado.toString();
	}

	/**
	 * Devuelve un String con el número de blancos pasado como parámetro
	 *
	 * @param longitud
	 *
	 * @return
	 */
	private static String ajustarEspacios2(final int longitud) {
		final char[] datoAjustado = new char[longitud];
		for (int i = 0; i < longitud; i++) {
			datoAjustado[i] = ' ';
		}
		return new String(datoAjustado);
	}

	/**
	 * Devuelve un String con el número de blancos pasado como parámetro
	 *
	 * @param longitud
	 *
	 * @return
	 */
	private static String ajustarEspacios3(final int longitud) {
		final char[] datoAjustado = new char[longitud];
		for (int i = 0; i < longitud; i++) {
			datoAjustado[i] = ' ';
		}
		return String.valueOf(datoAjustado);
	}

	private static void test() {
		long start;

		// **** SHORT_LEN;
		
		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			ajustarEspacios(SHORT_LEN);
		}
		System.out.println("ajustarEspacios(" + SHORT_LEN + "): "
				+ (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			ajustarEspacios2(SHORT_LEN);
		}
		System.out.println("ajustarEspacios2(" + SHORT_LEN + "): "
				+ (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			ajustarEspacios3(SHORT_LEN);
		}
		System.out.println("ajustarEspacios3(" + SHORT_LEN + "): "
				+ (System.currentTimeMillis() - start));


		// **** LONG_LEN;

		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			ajustarEspacios(LONG_LEN);
		}
		System.out.println("ajustarEspacios(" + LONG_LEN + "): "
				+ (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			ajustarEspacios2(LONG_LEN);
		}
		System.out.println("ajustarEspacios2(" + LONG_LEN + "): "
				+ (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			ajustarEspacios3(LONG_LEN);
		}
		System.out.println("ajustarEspacios3(" + LONG_LEN + "): "
				+ (System.currentTimeMillis() - start));

		System.out.println("*******************************************");
	}

	public static void main(final String[] args) {
		for (int jj = 0; jj < ROUNDS; jj++) {
			test();
		}
	}
}
