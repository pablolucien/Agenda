package org.pclg.threads;

import org.pclg.log.LoggerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 04-ene-2008 11:29:18
 */
final class ReusableThread extends Thread {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.make();
	private static final int TIME_TO_SLEEP = 100000;
	private Task task;
	private volatile boolean shouldRun = true;
	private final Object lock = new Object();

	public void setTask(final Task task) {
		this.task = task;
		synchronized (lock) {
			lock.notify();
		}
	}

	public void die() {
		shouldRun = false;
	}

	@Override
	public void run() {
		while(shouldRun) {
			synchronized (lock) {
				while (task == null && shouldRun) {
					try {
						LOGGER.info("ReusableThread esperando");
						lock.wait();
					} catch (final InterruptedException ex) {
						LOGGER.log(Level.SEVERE, LoggerFactory.ERROR_TAG, ex);
						Thread.currentThread().interrupt();
					}
				}
			}
			task.execute();
			task = null;
		}
	}
}
