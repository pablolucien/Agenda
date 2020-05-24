package org.pclg.threads;

import org.pclg.log.LoggerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 04-ene-2008 11:28:25
 */
final class ThreadReuseTest {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.make();
	private static final int TIME_TO_SLEEP = 10000;

	private ThreadReuseTest() {
	}

	public static void main(final String[] args) {
new ThreadPool().getThread();
		final ReusableThread t = new ReusableThread();
		t.start();
		t.setTask(new SimpleTask("Tarea uno"));
		try {
			LOGGER.info("ThreadReuseTest durmiendo");
			Thread.sleep(TIME_TO_SLEEP);
		} catch (final InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Error", e);
			Thread.currentThread().interrupt();
		}
		t.setTask(new SimpleTask("Tarea dos"));
		t.die();
	}
}
