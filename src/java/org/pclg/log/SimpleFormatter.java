package org.pclg.log;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 24-oct-2007 12:42:13
 * 
 * TODO: Ver el comportamiento concurrente de esto.
 */
final class SimpleFormatter extends Formatter {
    private static final String DATE_FORMAT = "{0,date} {0,time}";
    private static final MessageFormat DATE_FORMATTER = 
        new MessageFormat(DATE_FORMAT);
	private static final String LINE_SEPARATOR = 
	    System.getProperty("line.separator");

    private final Object[] args = new Object[1];
    private final StringBuffer formattedDate = new StringBuffer();
    private final Date date = new Date();

    @Override
	public String format(final LogRecord record) {
        final StringBuilder sb = new StringBuilder();

    	sb.append(record.getLevel().getLocalizedName()).append(": ");
    	date.setTime(record.getMillis());
    	args[0] = date;
    	formattedDate.setLength(0);
    	DATE_FORMATTER.format(args, formattedDate, null);
    	sb.append(formattedDate).append(": ")
    		.append(record.getSourceClassName())
    		.append('.')
    		.append(record.getSourceMethodName())
    		.append("() -> ")
    		.append(record.getMessage())
    		.append(LINE_SEPARATOR);
    	final Throwable thrown = record.getThrown();
        if (thrown != null) {
            sb.append(LoggerFactory.stackTrace2String(thrown));
    	}
        return sb.toString();
	}

}
