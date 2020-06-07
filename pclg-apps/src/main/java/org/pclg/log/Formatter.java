package org.pclg.log;

import java.util.logging.LogRecord;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 24-oct-2007 12:42:13
 */
@SuppressWarnings({"ClassWithoutLogger"})
final class Formatter extends java.util.logging.Formatter {

	@Override
	public String format(final LogRecord record) {
		return record.getMessage() + '\n';
	}
}
