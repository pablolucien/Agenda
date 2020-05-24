package tests;

/**
 * Clase para probar que la norma de no usar setLength(0) es una tontería.
 */
class TestStringBuffer_setLength {
	private static final int LIMIT = 1000000;
	private static final String MENSAJE = "Clase para probar que la norma de no "
			+ "usar setLength(0) es una tontería.";

	private TestStringBuffer_setLength() {
	}

	public static void main(final String[] args) {
		long start;
		StringBuffer sb = new StringBuffer(10);

		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			sb.append(MENSAJE);
			sb.setLength(0);
/*
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
//*/
		}
		System.out.println("sb.setLength(0): "
			+ (System.currentTimeMillis() - start));

		sb = new StringBuffer(10);

		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			sb.append(MENSAJE);
			sb.delete(0, sb.length());
/*
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
//*/
		}
		System.out.println("sb.delete(0, sb.length()): "
			+ (System.currentTimeMillis() - start));

		sb = new StringBuffer(10);

		start = System.currentTimeMillis();
		for (int jj = 0; jj < LIMIT; jj++) {
			sb.append(MENSAJE);
			sb.delete(0, Integer.MAX_VALUE);
/*
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
			sb.append(MENSAJE);
//*/
		}
		System.out.println("sb.delete(0, Integer.MAX_VALUE): "
			+ (System.currentTimeMillis() - start));
	}
}