package javaspecialists.spliterators;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
/**
 * Takes a start date and iterates backwards one year at a time.
 */
public class YearSpliterator implements Spliterator<LocalDate> {
    private LocalDate date;
    public YearSpliterator(final LocalDate startDate) {
        date = startDate;
    }
    public long estimateSize() {
        return Long.MAX_VALUE;
    }
    public boolean tryAdvance(final Consumer<? super LocalDate> action) {
        Objects.requireNonNull(action);
        action.accept(date);
        date = date.minusYears(1);
//        return true;
        // workaround para J9
       return date.getYear() >= 1900;
    }
    public Spliterator<LocalDate> trySplit() {
        return null;
    }
    public int characteristics() {
        return DISTINCT | IMMUTABLE | NONNULL | ORDERED | SORTED;
    }
    public Comparator<? super LocalDate> getComparator() {
        return Comparator.reverseOrder();
    }
}