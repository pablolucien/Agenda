import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;

/**
 * @since 20/04/2020.
 */
public class PrintLastModified {
    public static void main(String[] args) {
        if (args.length == 1) {
            final Set<String> availableZoneIds = ZoneOffset.getAvailableZoneIds();
            System.out.println("availableZoneIds = " + availableZoneIds);
        }

        final File file = new File(args[0]);
        final long lastModified = file.lastModified();
        final Date date = new Date(lastModified);
        final LocalDateTime localDateTimeUTC = LocalDateTime.ofEpochSecond(lastModified / 1000, 0, ZoneOffset.UTC);
        System.out.printf("%s - %d - %s - %s", file, lastModified, date, localDateTimeUTC.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if (args.length > 1) {
            final LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(lastModified / 1000, 0, ZoneOffset.of(args[1]));
            System.out.printf(" - %s", localDateTime);
        }
        System.out.printf("%n");
    }
}
