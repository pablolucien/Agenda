package org.pclg.filesystem;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Toca un o unos ficheros en el sentido de Unix (pone la fecha y hora actual
 * como fecha y hora de modificación.
 *
 * @since 20080530
 * @author EL Coyote Cojo
 */
public final class Touch {
	/* Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
	static final int INCREMENT_TIME = 10000;

    /* Usage message. */
    private static final String MSG_USAGE =
            "Uso:\n java org.pclg.filesystem.Touch [-d dd/MM/yyyy | dd-MM-yyyy] [-t hh:mm:ss] [-i[nc[rement]]] <filename(s)>\n"
            + "\t(hh en formato de 24 horas)\n"
            + "\tSi no se especifican -d o -t se usa la fecha/hora del sistema\n\n"
            + "\tSi se especifica -i[nc[rement]] se incrementa el tiempo en 10 segundos para cada archivo.\n\n"
            + "\tjava org.pclg.filesystem.Touch [-h|-help] muestra esta ayuda";


	private Touch(final String[] args) throws IOException, ParseException {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("-help")) {
            usage();
        }
        
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
		final SimpleDateFormat globalFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		final List<String> targets = new ArrayList<>();
		Date userDate = null;
		Date userTime = null;
		boolean incrementTime = false;

		for (int argsIndex = 0; argsIndex < args.length; argsIndex++) {
        //------------------------------------
	        if (args[argsIndex].equals("-d")) {
	            if (argsIndex + 1 >= args.length) {
	                usage();
	            }
	            userDate = dateFormat.parse(args[++argsIndex].replaceAll("-", "/"));
	            continue;
	        }

	        if (args[argsIndex].equals("-t")) {
	            if (argsIndex + 1 >= args.length) {
	                usage();
	            }
	            userTime = timeFormat.parse(args[++argsIndex]);
	            continue;
	        }

			if (args[argsIndex].equals("-i") || args[argsIndex].equals("-inc") || args[argsIndex].equals("-increment")) {
				incrementTime = true;
				continue;
			}

			targets.add(args[argsIndex]);
		}
        final long time;
        if (userDate == null && userTime == null) {
        	time = new Date().getTime();
        } else if (userDate != null && userTime != null) {
        	final Calendar dateCalendar = Calendar.getInstance();
        	dateCalendar.setTime(userDate);
        	final Calendar timeCalendar = Calendar.getInstance();
        	timeCalendar.setTime(userTime);
        	dateCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
        	dateCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        	dateCalendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
        	time = dateCalendar.getTimeInMillis();
        } else {
        	time = Long.MIN_VALUE;	// FIXME: ¿Qué caso de uso es este?
        }

        LOGGER.debug(
            String.format("userDate = %s, userTime = %s, time = %s, targets = %s",
                userDate == null ? "null" : dateFormat.format(userDate),
                userTime == null ? "null" : timeFormat.format(userTime),
                time == Long.MIN_VALUE ? "null" : globalFormat.format(new Date(time)),
                targets));

        if (targets.isEmpty()) {
            usage();
        }

		touchFiles(targets, time, userDate, incrementTime);
	}

	private void touchFiles(final List<String> targets, long time, final Date userDate, final boolean incrementTime) throws IOException {
		for (final String fileName : targets) {
			touchFile(new File(fileName), time, userDate);
			if (incrementTime) {
				time += INCREMENT_TIME;
			}
		}
	}

	static void touchFile(final File file, final long time, final Date userDate) throws IOException {
		final boolean success;
		if (file.exists()) {
			if (time == Long.MIN_VALUE) {
				final Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(file.lastModified());
				// FIXME: Si necesario mover esto a donde se define time = Long.MIN_VALUE
				if (userDate != null) {
					calendar.set(userDate.getYear() + 1900, userDate.getMonth(), userDate.getDate());
				} else {

				}
				//calendar./
				success = file.setLastModified(calendar.getTimeInMillis());
			} else {
				success = file.setLastModified(time);
			}
		} else {
			success = file.createNewFile() && file.setLastModified(time);
		}

		if (!success) {
			LOGGER.warn("Could not touch: " + file.getAbsolutePath());
		}
	}

	/**
     * A little help to the friends.
     */
    private static void usage() {
        LOGGER.warn(MSG_USAGE);
        System.exit(1);
    }

	@SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static void main(final String[] args) throws IOException, ParseException {
		new Touch(args);
	}
}
