package javaspecialists.spliterators;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.StreamSupport;
public class HeinzBirthdayDays {
    public static void main(String... args) {
        // Esto necesita JDK 9
        StreamSupport.stream(
            new YearSpliterator(
                LocalDate.of(2017, Month.DECEMBER, 4)), false)
//            .takeWhile(day -> day.getYear() >= 1971) // Java 9
            .map(day -> day + " -> " + day.getDayOfWeek())
            .forEach(System.out::println);
    }
}