package org.pclg.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DateTools <BR>
 *
 * @author El Coyote Cojo
 * @version 2002.jun.12 17:31:01, CEST
 */
public final class DateTools {
    /**
     * Los posibles formatos de fecha
     * ????????????????????????  Esto podría estar en un archivo de configuracion
     */
    private static final String[] dateFormats = {
        "yyyy-MM-dd", "yyyy-MM-dd hh:mm:ss",
        "yyyy.MM.dd", "yyyy.MM.dd hh:mm:ss",
        "dd/MM/yyyy", "dd/MM/yyyy hh:mm:ss",
        "dd-MM-yyyy", "dd-MM-yyyy hh:mm:ss",
    };

    /**
     * Los parsers segun los posibles formatos de fecha
     */
    private static SimpleDateFormat[] dateParsers;

    /**
     * El constructor por omision es privado para evitar que a algun
     * capullo se le ocurra hacer new DateTools()
     */
    private DateTools() {
    }

    /**
     * Tratar de interpretar si el usuario quiere un rango de fechas
     * posibilidades:	15/01/1998 un día
     *                  02/2000	   un mes
     *                  2002	   un año
     *
     * @param strDate   la fecha a interpretar
     * @return Un arreglo con fecha inicial y fecha final las cuales pueden ser null
     * @throws ParseException si el parámetro no se puede interpretar como una
     * fecha.
     */
    public static Date[] guessDate(String strDate) throws ParseException {
        final Date[] interval = new Date[2];
        if (strDate.length() == 9 || strDate.length() == 6) {
            strDate = "0" + strDate;
        }

        if (strDate.length() == 10) {
            final Date d1 = parseDate(strDate);
            interval[0] = interval[1] = d1;
        } else if (strDate.length() == 7) {
            // Adivinamos el inicio del mes
            strDate = "01/" + strDate;
            final Date d1 = parseDate(strDate);

            // Adivinamos el fin del mes
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(d1);
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

            interval[0] = d1;
            interval[1] = calendar.getTime();
        } else if (strDate.length() == 4) {
            // Adivinamos el inicio del año
            final String strDate1 = "01/01/" + strDate;
            final Date d1 = parseDate(strDate1);

            // Adivinamos el fin del año
            final String strDate2 = "31/12/" + strDate;
            final Date d2 = parseDate(strDate2);

            interval[0] = d1;
            interval[1] = d2;
        }

        return (interval);
    }

    /**
     * Devuelve un objeto Date construido a partir de un String que
     * representa una fecha.
     * --author El Coyote cojo
     *
     * @param strDate La supuesta fecha
     * @return un objeto Date construido a partir de un String que representa
     *         una fecha.
     * @throws ParseException si no se puede interpretar el argumento
     *                        como una fecha
     * @since 2001.12.26
     */
    public static Date parseDate(final String strDate) throws ParseException {
        // Esta excepcion la lanzamos si al final no hemos podido "parsear" la fecha
        ParseException theException = null;

        // Inicializamos los parsers si es necesa ...
        if (dateParsers == null) {
            dateParsers = new SimpleDateFormat[dateFormats.length];
            for (int i = 0; i < dateParsers.length; i++) {
                dateParsers[i] = new SimpleDateFormat(dateFormats[i]);
            }
        }

        // ... e intentamos hacer nuestro negocio
//LOGGER.severe("ToolBox.parseDate() ENTRA : " + strDate);
        for (final SimpleDateFormat dateParser : dateParsers) {
            try {
                final Date date = dateParser.parse(strDate);
//LOGGER.severe("ToolBox.parseDate() SALE : " + date);
                return (date);
            } catch (final ParseException ex) {
                theException = ex;
            }
        }

        // Si llegamos hasta aqui es que la cagamos
        throw(theException);
    }
}
