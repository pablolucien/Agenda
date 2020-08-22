package org.pclg.tools;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * A chronometer.
 * @author pablo
 * @version 2.0
 * @since 07-feb-2005
 * 2005.09.09  convertida en 'utility class'.
 */
public final class Chrono {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

    /** Size of the buffers. */
    private static final int BUFFER_SIZE = 1024;

    /** Quantity of initial slots. */
    private static final int DEFAULT_SIZE = 10;

    /** The properties of the class; essentialy the texts. */
    private static final Properties PROPERTIES = loadProperties();

    static class TimeDetailMessages {
        enum Format {LONG, SHORT}
        /**
         * El String que representa 'dias'
         */
        private static final String LONG_DAYS = PROPERTIES.getProperty("long.dy", "d");
        private static final String SHORT_DAYS = PROPERTIES.getProperty("short.dy", "d");

        /**
         * El String que representa 'horas'
         */
        private static final String LONG_HOURS = PROPERTIES.getProperty("long.hr", "h");
        private static final String SHORT_HOURS = PROPERTIES.getProperty("short.hr", "h");

        /**
         * El String que representa 'minutos'
         */
        private static final String LONG_MINUTES = PROPERTIES.getProperty("long.min", "m");
        private static final String SHORT_MINUTES = PROPERTIES.getProperty("short.min", "m");

        /**
         * El String que representa 'segundos'
         */
        private static final String LONG_SECONDS = PROPERTIES.getProperty("long.sec", "s");
        private static final String SHORT_SECONDS = PROPERTIES.getProperty("short.sec", "s");

        /**
         * El String que representa 'milisegundos'
         */
        private static final String LONG_MILLIS = PROPERTIES.getProperty("long.ms", "ms");
        private static final String SHORT_MILLIS = PROPERTIES.getProperty("short.ms", "ms");

        static String getDAYS(final Format format) {
            return format == Format.LONG ? LONG_DAYS : SHORT_DAYS;
        }

        static String getHOURS(final Format format) {
            return format == Format.LONG ? LONG_HOURS : SHORT_HOURS;
        }

        static String getMINUTES(final Format format) {
            return format == Format.LONG ? LONG_MINUTES : SHORT_MINUTES;
        }

        static String getSECONDS(final Format format) {
            return format == Format.LONG ? LONG_SECONDS : SHORT_SECONDS;
        }

        static String getMILLIS(final Format format) {
            return format == Format.LONG ? LONG_MILLIS : SHORT_MILLIS;
        }
    }

    /** The place to record the start time of a chrono. */
    private static long[] startTime = new long[DEFAULT_SIZE];

    /** The place to record the end time of a chrono. */
    private static long[] endTime = new long[DEFAULT_SIZE];

    /** The last slot asigned. */
    private static int lastUsed = -1;
    private static final char SPACE = ' ';
    private static final char DOT = '.';
    private static final String COMMA_SPACE = ", ";

    /**
     * Avoids instantiation.
     */
    private Chrono() {
    }

    /**
     * Assigns a chrono handle, creating space if needed. This handle is
     * guaranteed to be >= 0. This chrono is started at this moment.
     * @return the handle to a chrono.
     */
    public static synchronized int getChrono() {
        lastUsed++;
        if (lastUsed >= startTime.length) {
            final int newSize = startTime.length << 1;
            startTime = Arrays.copyOf(startTime, newSize);
            endTime = Arrays.copyOf(endTime, newSize);
        }
        start(lastUsed);
        return lastUsed;
    }


    /**
     * Starts a time counter.
     * @param cronNr the handle to a chrono.
     */
    public static void start(final int cronNr) {
        startTime[cronNr] = System.currentTimeMillis();
    }

    /**
     * Stops a time counter.
     * @param cronNr the handle to a chrono.
     */
    public static void mark(final int cronNr) {
        endTime[cronNr] = System.currentTimeMillis();
    }

    /**
     * Returns the time in milliseconds that chrono has measured.
     * @param cronNr the handle to a chrono.
     * @return the time in milliseconds that chrono has measured.
     */
    public static long elapsed(final int cronNr) {
        return endTime[cronNr] - startTime[cronNr];
    }

    /**
     * Splits into hours, minutes,etc a time given in milliseconds.
     * @param milliseconds a duration in milliseconds.
     *
     * @return a String with the time in a human readable form.
     */
    public static String timeDetail(final long milliseconds) {
        return timeDetail(milliseconds, TimeDetailMessages.Format.LONG);
    }

    /**
     * Splits into hours, minutes,etc a time given in milliseconds.
     * @param millis a duration in milliseconds.
     * @param format the format to use.
     *
     * @return a String with the time in a human readable form.
     */
    public static String timeDetail(final long millis, final TimeDetailMessages.Format format) {
		if (millis == 0) {
			return "0 " + TimeDetailMessages.getMILLIS(format);
		}
        long milliseconds = millis;
        long secs = milliseconds / 1000L;
        milliseconds %= 1000L;
        long mins = secs / 60L;
        secs %= 60L;
        long hours = mins / 60L;
        mins %= 60L;
        final long days = hours / 24L;
        hours %= 24L;
        final StringBuilder buffer = new StringBuilder(BUFFER_SIZE);
		append2Buffer(buffer, days, TimeDetailMessages.getDAYS(format));
		append2Buffer(buffer, hours, TimeDetailMessages.getHOURS(format));
        append2Buffer(buffer, mins, TimeDetailMessages.getMINUTES(format));
        append2Buffer(buffer, secs, TimeDetailMessages.getSECONDS(format));
        append2Buffer(buffer, milliseconds, TimeDetailMessages.getMILLIS(format));
        buffer.append(DOT);
        return  buffer.toString();
    }

    private static void append2Buffer(final StringBuilder buffer,
        final long value, final String magnitude) {
        if(value != 0) {
            if (buffer.length() > 0) {
                buffer.append(COMMA_SPACE);
            }
            buffer.append(value).append(SPACE).append(magnitude);
        }
    }


    /**
     * Loads the properties of the application.
     *
     * @return the properties of the application. Never returns <code>null</code>.
     */
    private static Properties loadProperties() {
        final Properties properties = new Properties();
        try {
            final InputStream inputStream = Chrono.class.getClassLoader()
                .getResourceAsStream("Chrono.properties");
            if (inputStream != null) {
                properties.load(inputStream);
                inputStream.close();
            }
        } catch (final IOException ex) {
			LOGGER.error("", ex);
        }
        return properties;
    }
}
