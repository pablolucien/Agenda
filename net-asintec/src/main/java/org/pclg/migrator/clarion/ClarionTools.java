// ******************************** package
package org.pclg.migrator.clarion;

// ******************************** imports

import java.util.Date;

/**
 * ClarionTools.
 *
 * @author El Coyote Cojo
 * @version 2002.feb.06 20:15:07, CEST
 */
public final class ClarionTools {
    /**
     * Avoids instantiation.
     */
    private ClarionTools() {
    }

    /**
     * Converts a Clarion time to a java.util.Date.
     * @param abstime a Clarion time.
     * @return the Clarion time converted to a java.util.Date.
     */
    public static Date clarionLong2Time(long abstime) {
        // IF abstime < 1 OR abstime > 8640000
        // THEN
        // STOP	! abstime is invalid
        //abstime = 5235867; 	// 14:32:38.67

        abstime -= 1L;
        final long hour = abstime / 360000L;
        abstime %= 360000L;
        final long minute = abstime / 6000L;
        abstime %= 6000L;
        final long seconds = abstime / 100L;
//		long hundreds = abstime % 100;
        //noinspection deprecation
        return new Date(0, 0, 0, (int) hour, (int) minute, (int) seconds/*, (int) hundreds*/);
    }

    /**
     * Converts a Clarion date to a java.util.Date.
     * @param absday a Clarion date.
     * @return the Clarion date converted to a java.util.Date.
     */
    @SuppressWarnings("deprecation")
    public static Date clarionLong2Date(long absday) {
        final int[] numberOfDaysInMonth = {
            31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        };

        // IF absday <= 3 OR absday > 109211
        // THEN
        // STOP! absday is invalid
        // absday = 68892; // 11 agosto 1989

        if (absday > 36527) {
            absday -= 3L;
        } else {
            absday -= 4L;
        }

        long year = 1801L + 4L * (absday / 1461L);
        long day;
        long month = 0L;
        absday %= 1461L;
        if (absday == 1460) {
            year += 3L;
            day = 365L;
        } else {
            year += absday / 365L;
            day = absday % 365L;
        }

        if (year < 100) {
            year += 1900L;
        }
//System.err.println(year);

        if (year % 4L == 0 && year != 1900) {
            numberOfDaysInMonth[1] = 29;	// February
        } else {
            numberOfDaysInMonth[1] = 28;	// February
        }

        for (int ii = 0; ii < 12; ii++) {
            day -= (long) numberOfDaysInMonth[ii];
            if (day < 0) {
                day += (long) numberOfDaysInMonth[ii] + 1L;
                break;
            }
            month = (long) (ii + 1);
        }

        return new Date((int) year - 1900, (int) month, (int) day, 0, 0, 0/*, (int) 0*/);
    }
}
