package tests;

import org.pclg.log.LoggerFactory;

import java.io.IOException;
import java.util.logging.Logger;

//import org.pclg.security.Steganograph;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23-ene-2008 18:04:22
 */
final class Stega {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.make();

	private static void usage() {
		LOGGER.info("Uso:\n"
                + "\tjava tests.Stega -p[ut] <msg file> <src image> <dest image>\n"
                + "\tjava tests.Stega -g[et] <src image> <msg file> ");
	}

	public static void main(final String[] args) throws IOException, InterruptedException {
		final String action;
		if (args.length == 0
				|| !(action = args[0].toUpperCase()).equals("-P")
				&& !action.equals("-PUT")
				&& !action.equals("-G")
				&& !action.equals("-GET")
				|| action.charAt(1) == 'P' && args.length != 4
				|| action.charAt(1) == 'G' && args.length != 3) {
			usage();
		} else {
			//Steganograph.process(args);
		}
	}
}
