package org.pclg.threads;

import org.pclg.log.LoggerFactory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 08-ene-2008 15:13:07
 */
final class ThreadPool {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.make();

	private static final int NR_THREADS = 10;

	private final List<Thread> threads = new ArrayList<>(NR_THREADS);

	private final BitSet inUse = new BitSet(NR_THREADS);

	public ThreadPool() {
		//for
	}

	public Thread getThread() {
LOGGER.info("nextClearBit = " +  inUse.nextClearBit(0));
inUse.flip(0, NR_THREADS);
LOGGER.info("nextClearBit = " +  inUse.nextClearBit(0));
		final int nrThreads = threads.size();
		for (int ii = 0; ii < nrThreads; ii++) {
			if (!inUse.get(ii)) {
				LOGGER.info("Devolviendo Thread nro: " + ii);
				inUse.set(ii);
				return threads.get(ii);
			}
		}
		LOGGER.info("Creando Thread nro: " + nrThreads);
		final Thread thread = new ReusableThread();
		threads.add(thread);
		inUse.set(nrThreads);
		return thread;
	}
}
