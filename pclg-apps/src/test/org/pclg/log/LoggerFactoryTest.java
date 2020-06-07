package org.pclg.log;
/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 22-oct-2007 18:30:37
 */

import org.testng.annotations.Test;

import java.util.logging.Logger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

@SuppressWarnings({"ClassWithoutLogger"})
public class LoggerFactoryTest {

	@Test
	public void testMake() {
		final Logger logger = LoggerFactory.make();
		assertNotNull(logger);
        assertEquals(logger.getName(), getClass().getName());
	}

    @SuppressWarnings({"deprecation"}) // (Only test)
	@Test
	public void testMakeSimpleLogger() {
		final Logger logger = LoggerFactory.makeSimpleLogger();
		assertNotNull(logger);
        assertEquals(logger.getName(), getClass().getName());
	}

	@Test
	public void makeLog4J() {
		final org.apache.log4j.Logger logger = LoggerFactory.makeLog4J();
		assertNotNull(logger);
        assertEquals(logger.getName(), getClass().getName());
	}

	@SuppressWarnings({"deprecation"}) // (Only test)
	@Test
	public void testMake2() {
		final Logger logger = LoggerFactory.make2();
		assertNotNull(logger);
	}

	@SuppressWarnings({"deprecation"}) // (Only test)
	@Test
	public void testConsistencia() {
		assertSame(LoggerFactory.make(), LoggerFactory.make2());
	}
}