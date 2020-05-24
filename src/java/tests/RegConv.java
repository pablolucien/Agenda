package tests;

import java.util.StringTokenizer;

/**
 * Pone en formato legible los datos de los .reg.
 * Luego hay que cambiar "@=hex(2):" por "@=", las \ por \\, las " por \",
 * los %1 por \"%1\" y ponerlo todo entre comillas dobles.
 *  
 * @author paceLucien
 */
final class RegConv
{

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: java tests.RegConv string {...}");
			System.exit(1);
		}
		for (final String arg : args) {
			convertHex2String(arg);
		}
	}

	/**
	 * Convierte la cadena hex a una String, formateandola de manera que la
	 * entienda el editor de registry.
	 *
	 * @param str la cadena hex a convertir.
	 */
	private static void convertHex2String(final String str)
	{
		final StringTokenizer tokenizer = new StringTokenizer(str, ",");
		final StringBuilder buffer = new StringBuilder(str.length());
		buffer.append("@=\"");
		while (tokenizer.hasMoreTokens()) {
			final int intValue = Integer.valueOf(tokenizer.nextToken(), 16)
				.intValue();
			if (intValue == 0) {
				continue;
			}
			buffer.append((char) intValue);
		}
		buffer.append('"');
		final String stringValue = buffer.toString()
			.replaceAll("\\\\", "\\\\\\\\").replaceAll("%1", "\\\\\"%1\\\\\"");
		System.out.println(stringValue);
	}

}
