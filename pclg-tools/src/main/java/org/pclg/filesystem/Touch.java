package org.pclg.filesystem;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.getInstance;

/**
 * Toca un o unos ficheros en el sentido de Unix (pone la fecha y hora actual
 * como fecha y hora de modificación).
 *
 * @author EL Coyote Cojo
 * @since 2008.05.30
 */
public final class Touch {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    static final int INCREMENT_TIME = 10000;

    /* Usage message. */
    private static final String MSG_USAGE =
        "Uso:\n java org.pclg.filesystem.Touch [-d dd/MM/yyyy | dd-MM-yyyy] [-t hh:mm:ss] [-i[nc[rement]]] <filename(s)>\n"
            + "\t(hh en formato de 24 horas)\n"
            + "\tSi no se especifican -d o -t se usa la fecha/hora del sistema\n\n"
            + "\tSi se especifica -i[nc[rement]] se incrementa el tiempo en 10 segundos para cada archivo.\n\n"
            + "\tjava org.pclg.filesystem.Touch [-h|-help] muestra esta ayuda";

    static class Parameters {
        final List<String> targets = new ArrayList<>();
        Date userDate = null;
        Date userTime = null;
        long targetTime;
        boolean incrementTime = false;

        boolean onlyUpdatingDate() {
            return userDate != null && userTime == null;
        }

        boolean onlyUpdatingTime() {
            return userDate == null && userTime != null;
        }
    }

    public void executeTouch(final String[] args) throws IOException, ParseException {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("-help")) {
            usage();
        }

        final Parameters parameters = getParameters(args);

        if (parameters.targets.isEmpty()) {
            usage();
        }

        touchFiles(parameters);
    }

    static Parameters getParameters(String[] args) throws ParseException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        Parameters parameters = new Parameters();
        for (int argsIndex = 0; argsIndex < args.length; argsIndex++) {
            switch (args[argsIndex]) {
            case "-d":
                if (argsIndex + 1 >= args.length) {
                    usage();
                }
                parameters.userDate = dateFormat.parse(args[++argsIndex].replaceAll("-", "/"));
                continue;
            case "-t":
                if (argsIndex + 1 >= args.length) {
                    usage();
                }
                parameters.userTime = timeFormat.parse(args[++argsIndex]);
                continue;
            case "-i":
            case "-inc":
            case "-increment":
                parameters.incrementTime = true;
                continue;
            }

            parameters.targets.add(args[argsIndex]);
        }

        final SimpleDateFormat globalFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        LOGGER.debug(
            String.format("userDate = %s, userTime = %s, time = %s, targets = %s",
                parameters.userDate == null ? "null" : dateFormat.format(parameters.userDate),
                parameters.userTime == null ? "null" : timeFormat.format(parameters.userTime),
                globalFormat.format(new Date(parameters.targetTime)),
                parameters.targets));
        return parameters;
    }

    private static void computeTargetTime(Parameters parameters, final Calendar dateCalendar, final Calendar timeCalendar) {
        Date userDate = parameters.userDate;
        Date userTime = parameters.userTime;
        if (userDate == null && userTime == null) {
            parameters.targetTime = new Date().getTime();
        } else if (userDate != null && userTime != null) {
            dateCalendar.setTime(userDate);
            timeCalendar.setTime(userTime);
            dateCalendar.set(HOUR_OF_DAY, timeCalendar.get(HOUR_OF_DAY));
            dateCalendar.set(MINUTE, timeCalendar.get(MINUTE));
            dateCalendar.set(SECOND, timeCalendar.get(SECOND));
            parameters.targetTime = dateCalendar.getTimeInMillis();
        } else if (userDate != null) {
            dateCalendar.setTime(userDate);
            dateCalendar.set(HOUR_OF_DAY, timeCalendar.get(HOUR_OF_DAY));
            dateCalendar.set(MINUTE, timeCalendar.get(MINUTE));
            dateCalendar.set(SECOND, timeCalendar.get(SECOND));
            parameters.targetTime = dateCalendar.getTimeInMillis();
        } else {
            timeCalendar.setTime(userTime);
            dateCalendar.set(HOUR_OF_DAY, timeCalendar.get(HOUR_OF_DAY));
            dateCalendar.set(MINUTE, timeCalendar.get(MINUTE));
            dateCalendar.set(SECOND, timeCalendar.get(SECOND));
            parameters.targetTime = dateCalendar.getTimeInMillis();
        }
    }

    private void touchFiles(final Parameters parameters) throws IOException {
        final List<String> targets = parameters.targets;
        final boolean incrementTime = parameters.incrementTime;
        long time2Increment = 0;
        final Calendar dateCalendar = getInstance();
        final Calendar timeCalendar = getInstance();
        computeTargetTime(parameters, dateCalendar, timeCalendar);
        for (final String fileName : targets) {
            long time = parameters.targetTime + time2Increment;
            final File file = new File(fileName);
            if (parameters.onlyUpdatingTime()) {
                dateCalendar.setTimeInMillis(file.lastModified());
                computeTargetTime(parameters, dateCalendar, timeCalendar);
                time = parameters.targetTime;
            } else if (parameters.onlyUpdatingDate()) {
                timeCalendar.setTimeInMillis(file.lastModified());
                computeTargetTime(parameters, dateCalendar, timeCalendar);
                time = parameters.targetTime;
            }
            touchFile(file, time);
            if (incrementTime) {
                time2Increment += INCREMENT_TIME;
            }
        }
    }

    static void touchFile(final File file, final long time) throws IOException {
        final boolean success;
        if (file.exists()) {
            success = file.setLastModified(time);
        } else {
            success = file.createNewFile() && file.setLastModified(time);
        }

        if (!success) {
            LOGGER.warn("Could not touch: {}", file.getAbsolutePath());
        }
    }

    /**
     * A little help to the friends.
     */
    private static void usage() {
        LOGGER.warn(MSG_USAGE);
        System.exit(1);
    }

    public static void main(final String[] args) throws IOException, ParseException {
        new Touch().executeTouch(args);
    }
}
