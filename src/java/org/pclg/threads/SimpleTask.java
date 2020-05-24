package org.pclg.threads;

import org.pclg.log.LoggerFactory;

import java.util.logging.Logger;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 04-ene-2008 12:31:34
 */
final class SimpleTask implements Task {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.make();

	private final String name;

	SimpleTask(final String name) {
		this.name = name;
	}

	@Override
	public void execute() {
		final String msg = "Ejecutando " + name;
		for(int ii = 0; ii < 10; ii++) {
			LOGGER.info(msg);
		}
	}
}
