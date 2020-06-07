package org.pclg.tools;
/*
 * Creado el 12-feb-2008
 */

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author El Coyote Cojo.
 */
public class NumberParser {
	/** Esta clase existe para usarla en entornos donde no hay autoboxing. */
	private static class NumberParserMap extends HashMap {
        private static final long serialVersionUID = -9067173108135369324L;

        public void put(final String key, final long value) {
			super.put(key, new Long(value));
		}
	}

	private static final Map<String, Long> map = new HashMap<>();

	static {
		map.put("un", 1L);
		map.put("uno", 1L);
		map.put("dos", 2L);
		map.put("tres", 3L);
		map.put("cuatro", 4L);
		map.put("cinco",5L);
		map.put("seis", 6L);
		map.put("siete", 7L);
		map.put("ocho", 8L);
		map.put("nueve", 9L);
		map.put("diez", 10L);
		map.put("once", 11L);
		map.put("doce", 12L);
		map.put("trece", 13L);
		map.put("catorce", 14L);
		map.put("quince", 15L);
		map.put("dieciseis", 16L);
		map.put("diecisiete", 17L);
		map.put("dieciocho", 18L);
		map.put("diecinueve", 19L);
		map.put("veinte", 20L);
		map.put("ventiun", 21L);
		map.put("ventiuno", 21L);
		map.put("ventidos", 22L);
		map.put("ventitres", 23L);
		map.put("venticuatro", 24L);
		map.put("venticinco", 25L);
		map.put("ventiseis", 26L);
		map.put("ventisiete", 27L);
		map.put("ventiocho", 28L);
		map.put("ventinueve", 29L);
		map.put("treinta", 30L);
		map.put("cuarenta", 40L);
		map.put("cincuenta", 50L);
		map.put("sesenta", 60L);
		map.put("setenta", 70L);
		map.put("ochenta", 80L);
		map.put("noventa", 90L);
		map.put("cien", 100L);
		map.put("ciento", 100L);
		map.put("doscientos", 200L);
		map.put("trescientos", 300L);
		map.put("cuatrocientos", 400L);
		map.put("quinientos", 500L);
		map.put("seiscientos", 600L);
		map.put("setecientos", 700L);
		map.put("ochocientos", 800L);
		map.put("novecientos", 900L);
		map.put("mil", 1000L);
		map.put("millon", 1000000L);
		map.put("millón", 1000000L);
		map.put("millones", 1000000L);
		map.put("millardo", 1000000000L);
		map.put("millardos", 1000000000L);
		map.put("billon", 1000000000000L);
		map.put("billón", 1000000000000L);
		map.put("billones", 1000000000000L);
	}

	/**
	 * @param string
	 * @return
	 */
	long parseLong(final String string) {
		long value = 0;
//		final String[] tokens = string.split("\\s+");
		final String[] tokens = string.replace("y", "").split("\\s+");
		final long[] terms = new long[tokens.length];
		for (int ii = 0; ii < tokens.length; ii++) {
			terms[ii] = toNumber(tokens[ii]);
			System.out.println(tokens[ii] + " - " + terms[ii]);
		}
	//	Stack term2 = new Stack();
	//	term2.addAll(new ArrayList(terms));
		value = aggregate(terms, 0);
		return value;
	}

	/**
	 * @param terms
	 * @return
	 */
	private long aggregate(final long[] terms, int startPos) {
		long retVal = 0;
		retVal = terms[startPos];
		if (startPos < terms.length - 1) {
			if (terms[startPos + 1] == 0) {
				startPos += 2;
				retVal += terms[startPos];
			}
			if (terms[startPos + 1] > terms[startPos]) {
				startPos++;
				retVal *= terms[startPos];
				startPos++;
				retVal += aggregate(terms, startPos);
			} else {
				startPos++;
				retVal += aggregate(terms, startPos);
			}
		}
		return retVal;
	}

	/**
	 * @param string
	 * @return
	 */
	private long toNumber(final String string) {
		long value = 0;
		final Long longValue = map.get(string);
		if (longValue != null) {
			value = longValue.longValue();
		}
		return value;
	}

	public static void main(final String[] args) {
		final NumberParser numberParser = new NumberParser();
		final String[] testSet = {
				"tres mil quinientos ventisiete",
				"dos millones cinco mil trescientos ventisiete",
				"cuatro millones treinta y siete mil trescientos ventisiete",
				"cuatro millones treinta y siete mil ochocientos cuarenta y cinco",
		};
		for (String s : testSet) {
			final long number = numberParser.parseLong(s);
			final NumberFormat numberFormat = new DecimalFormat();
			System.out.println(s + " = " + numberFormat.format(number));
		}
	}
}
