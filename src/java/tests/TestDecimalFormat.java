package tests;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 25-jul-2007 10:37:12
 */
public class TestDecimalFormat {
	public static void main(final String[] args) {
		System.out.println(formatImporte(2598766.4, true, -10, 20, 5, 9, ',', '.'));
		ejemploDeLaDocumentacion();
	}

		/**
		 * Formateo de campos con importes
		 *
		 * 	@param dImporte --> Campo a formatear
		 *  @param blMostPtoMiles  --> Mostrar el separador de miles
		 *  @param intMaxEnteros --> numero maximo de cifras enteras
		 *  @param intMaxDecimal --> numero maximo de cifras decimales
		 * 	@param intMinDecimal --> numero minimo de cifras decimales
		 * 	@param intMinEnteros --> numero minimo de cifra enteras
		 * 	@param chSepDecimal --> caracter para marcar las cifras decimales
		 * 	@param chSepMil --> caracter para marcar las posiciones de los miles
		 *  @return String campo formateado
		 */
	private static String formatImporte(final double dImporte,
		final boolean blMostPtoMiles,
		final int intMaxEnteros, final int intMinEnteros,
		final int intMaxDecimal,
		final int intMinDecimal, final char chSepDecimal, final char chSepMil)
										{
		final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(chSepDecimal);
		dfs.setGroupingSeparator(chSepMil);

		final DecimalFormat df = new DecimalFormat();
		df.setDecimalFormatSymbols(dfs);
		//se formatea el importe
		df.setMaximumIntegerDigits(intMaxEnteros);
		df.setMinimumIntegerDigits(intMinEnteros);
		df.setMaximumFractionDigits(intMaxDecimal);
		df.setMinimumFractionDigits(intMinDecimal);

		df.setGroupingUsed(blMostPtoMiles);
		return df.format(dImporte);
	}

	private static void ejemploDeLaDocumentacion() {
// Print out a number using the localized number, integer, currency,
		// and percent format for each locale
		final Locale[] locales = NumberFormat.getAvailableLocales();
		final double myNumber = -1234.56;
		NumberFormat form;
		for (int j=0; j<4; ++j) {
			System.out.println("FORMAT");
			for (int i = 0; i < locales.length; ++i) {
				if (locales[i].getCountry().length() == 0) {
				   continue; // Skip language-only locales
				}
				System.out.print(locales[i].getDisplayName());
				switch (j) {
				case 0:
					form = NumberFormat.getInstance(locales[i]); break;
				case 1:
					form = NumberFormat.getIntegerInstance(locales[i]); break;
				case 2:
					form = NumberFormat.getCurrencyInstance(locales[i]); break;
				default:
					form = NumberFormat.getPercentInstance(locales[i]); break;
				}
				if (form instanceof DecimalFormat) {
					System.out.print(": " + ((DecimalFormat) form).toPattern());
				}
				System.out.print(" -> " + form.format(myNumber));
				try {
					System.out.println(" -> " + form.parse(form.format(myNumber)));
				} catch (final ParseException e) {}
			}
		}

	}
}