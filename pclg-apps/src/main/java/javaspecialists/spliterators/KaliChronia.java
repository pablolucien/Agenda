package javaspecialists.spliterators;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
public class KaliChronia {
    public static void main(String... args) {
        // Esto necesita JDK 9
        Stream<LocalDate> newYearDays = StreamSupport.stream(
            new YearSpliterator(
                LocalDate.of(2018, Month.JANUARY, 1)), false);

        newYearDays
            .filter(day -> day.getDayOfWeek() == DayOfWeek.MONDAY)
//            .takeWhile(day -> day.getYear() >= 1900) // Java 9
            .forEach(System.out::println);
    }
}