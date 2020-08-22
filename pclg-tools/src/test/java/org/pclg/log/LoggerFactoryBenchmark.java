package org.pclg.log;

import javaspecialists.ReflectionHelper;
import org.pclg.tools.Chrono;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactoryBenchmark {
    /** El logger de esta clase. */
    private static final Logger LOGGER = LoggerFactory.makeSimpleLogger();
	private static final int COUNT = 1000000;

	private LoggerFactoryBenchmark() {
	}

	public static void main(final String[] args) throws NoSuchFieldException, IllegalAccessException {
		final Field loggerField = LoggerFactory.class.getDeclaredField("LOGGER");
		LOGGER.log(Level.INFO, "LoggerFactory.LOGGER = " + loggerField);
		ReflectionHelper.setStaticFinalField(loggerField, null);
		LOGGER.log(Level.INFO, "LoggerFactory.LOGGER = " + loggerField);

		final int chronHandle = Chrono.getChrono();
		for (int jj = 0; jj < 10; jj++) {
			Chrono.start(chronHandle);
			for (int ii = 0; ii < COUNT; ii++) {
				LoggerFactory.make();
			}
			Chrono.mark(chronHandle);
			LOGGER.log(Level.INFO, "make(): " + Chrono.timeDetail(
					Chrono.elapsed(chronHandle)));

			Chrono.start(chronHandle);
			for (int ii = 0; ii < COUNT; ii++) {
				//noinspection deprecation (Only test)
				LoggerFactory.make2();
			}
			Chrono.mark(chronHandle);
			LOGGER.log(Level.INFO, "make2(): " + Chrono.timeDetail(
					Chrono.elapsed(chronHandle)));
		}
/*
08-may-2009 13:22:58 org.pclg.log.LoggerFactory makeSimpleLogger
INFO: Creando logger para: org.pclg.log.TestLoggerFactory
INFO: 08-may-2009 13:22:58: org.pclg.log.TestLoggerFactory.main() -> LoggerFactory.LOGGER = private static final java.util.logging.Logger org.pclg.log.LoggerFactory.LOGGER
INFO: 08-may-2009 13:22:58: org.pclg.log.TestLoggerFactory.main() -> LoggerFactory.LOGGER = private static java.util.logging.Logger org.pclg.log.LoggerFactory.LOGGER
INFO: 08-may-2009 13:23:00: org.pclg.log.TestLoggerFactory.main() -> make(): 1 seconds, 953 milliseconds.
INFO: 08-may-2009 13:23:12: org.pclg.log.TestLoggerFactory.main() -> make2(): 12 seconds, 110 milliseconds.
INFO: 08-may-2009 13:23:14: org.pclg.log.TestLoggerFactory.main() -> make(): 1 seconds, 953 milliseconds.
INFO: 08-may-2009 13:23:26: org.pclg.log.TestLoggerFactory.main() -> make2(): 12 seconds, 94 milliseconds.
INFO: 08-may-2009 13:23:28: org.pclg.log.TestLoggerFactory.main() -> make(): 1 seconds, 937 milliseconds.
INFO: 08-may-2009 13:23:40: org.pclg.log.TestLoggerFactory.main() -> make2(): 12 seconds, 282 milliseconds.
INFO: 08-may-2009 13:23:42: org.pclg.log.TestLoggerFactory.main() -> make(): 1 seconds, 984 milliseconds.
INFO: 08-may-2009 13:23:55: org.pclg.log.TestLoggerFactory.main() -> make2(): 12 seconds, 407 milliseconds.
INFO: 08-may-2009 13:23:57: org.pclg.log.TestLoggerFactory.main() -> make(): 1 seconds, 922 milliseconds.
INFO: 08-may-2009 13:24:09: org.pclg.log.TestLoggerFactory.main() -> make2(): 12 seconds, 250 milliseconds.
INFO: 08-may-2009 13:24:11: org.pclg.log.TestLoggerFactory.main() -> make(): 1 seconds, 937 milliseconds.
INFO: 08-may-2009 13:24:23: org.pclg.log.TestLoggerFactory.main() -> make2(): 12 seconds, 313 milliseconds.
INFO: 08-may-2009 13:24:25: org.pclg.log.TestLoggerFactory.main() -> make(): 1 seconds, 922 milliseconds.
INFO: 08-may-2009 13:24:37: org.pclg.log.TestLoggerFactory.main() -> make2(): 12 seconds, 297 milliseconds.
INFO: 08-may-2009 13:24:39: org.pclg.log.TestLoggerFactory.main() -> make(): 1 seconds, 922 milliseconds.
INFO: 08-may-2009 13:24:51: org.pclg.log.TestLoggerFactory.main() -> make2(): 12 seconds, 94 milliseconds.
INFO: 08-may-2009 13:24:53: org.pclg.log.TestLoggerFactory.main() -> make(): 1 seconds, 922 milliseconds.
INFO: 08-may-2009 13:25:05: org.pclg.log.TestLoggerFactory.main() -> make2(): 12 seconds, 78 milliseconds.
INFO: 08-may-2009 13:25:07: org.pclg.log.TestLoggerFactory.main() -> make(): 1 seconds, 922 milliseconds.
INFO: 08-may-2009 13:25:20: org.pclg.log.TestLoggerFactory.main() -> make2(): 12 seconds, 172 milliseconds.
*/
	}
}
