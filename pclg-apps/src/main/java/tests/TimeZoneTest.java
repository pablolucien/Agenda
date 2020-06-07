package tests;

import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

class TimeZoneTest {
	public static void main(final String[] args) {
		TimeZone timeZone = TimeZone.getTimeZone("España");
		System.out.println(timeZone.getDisplayName());
		timeZone = TimeZone.getDefault();
		System.out.println(timeZone.getDisplayName());
		timeZone = TimeZone.getTimeZone("America/Caracas");
		System.out.println(timeZone.getDisplayName());
/*
		String[] ids = TimeZone.getAvailableIDs();
		for (String id : ids) {
			System.out.println(id);
		}
*/

		Currency currency = Currency.getInstance("VEB");
		System.out.println(currency.toString());
		currency = Currency.getInstance(Locale.getDefault());
		System.out.println(currency.toString());

		final Map<String, String> env = System.getenv();
		System.out.println("env = " + env);
	}
}
