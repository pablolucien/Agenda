package tests;

import org.pclg.log.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 18-oct-2007 11:26:48
 */
public final class ArraysTest {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.make();

	private ArraysTest() {
	}

	public static void main(final String[] args) {
		final String[] array = {"yo", "tu", "el" };
		final List<String> lista = Arrays.asList(array);
		LOGGER.log(Level.INFO, lista.toString());
		lista.set(1, "nosotros");
		LOGGER.log(Level.INFO, lista.get(1));
		try {
			lista.add("vosotros");
		} catch (final UnsupportedOperationException e) {
			LOGGER.log(Level.INFO, "Operation not supported in " + lista.getClass());
		}
	}
}
