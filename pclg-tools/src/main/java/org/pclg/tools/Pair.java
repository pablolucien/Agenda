package org.pclg.tools;

/**
* @author El Coyote Cojo
* @since 2/07/20 17:40
*/
public class Pair<T, S> {
    private final T first;
    private final S second;

    public Pair(final T first, final S second) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return first;
    }

    public S second() {
        return second;
    }

    @Override
    public String toString() {
        return "{" + first + ", " + second + '}';
    }
}
