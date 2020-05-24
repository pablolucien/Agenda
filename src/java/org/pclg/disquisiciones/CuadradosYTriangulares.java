package org.pclg.disquisiciones;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

/**
 * Clase para resolver el enigma de Numberphile de si hay más números a la vez
 * triangulares (x = m * (m + 1) / 2) y cuadrados (x = n * n) además del 36.
 *
 * Si los hay, ¿cuál es el patrón?
 *
 * Puzzle Is 36 the only triangle-square number.mp4
 *
 * Para encontrarlos buscamos los puntos en que ambas funciones tomen
 * valores que estén en el rango de ambas.
 *
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 6/08/17 8:20
 */
public final class CuadradosYTriangulares {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    private CuadradosYTriangulares() {
    }

    static class IntegerPair implements Comparable<IntegerPair> {
        final Integer x;
        final Integer y;

        IntegerPair(final Integer x, final Integer y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "{" + x + ", " + y + '}';
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }

            final IntegerPair that = (IntegerPair) other;

            return !(y != null ? !y.equals(that.y) : that.y != null);

        }

        @Override
        public int hashCode() {
            return y != null ? y.hashCode() : 0;
        }

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         * <p>
         * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
         * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
         * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
         * <tt>y.compareTo(x)</tt> throws an exception.)
         * <p>
         * <p>The implementor must also ensure that the relation is transitive:
         * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
         * <tt>x.compareTo(z)&gt;0</tt>.
         * <p>
         * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
         * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
         * all <tt>z</tt>.
         * <p>
         * <p>It is strongly recommended, but <i>not</i> strictly required that
         * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
         * class that implements the <tt>Comparable</tt> interface and violates
         * this condition should clearly indicate this fact.  The recommended
         * language is "Note: this class has a natural ordering that is
         * inconsistent with equals."
         * <p>
         * <p>In the foregoing description, the notation
         * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
         * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
         * <tt>0</tt>, or <tt>1</tt> according to whether the value of
         * <i>expression</i> is negative, zero or positive.
         *
         * @param other the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it
         *                              from being compared to this object.
         */
        @Override
        public int compareTo(final IntegerPair other) {
            return y.compareTo(other.y);
        }
    }

    public static void main(final String[] args) {
        final int total = 10000;
        final Set<IntegerPair> cuadrados = new TreeSet<>();
        final Set<IntegerPair> triangulares = new TreeSet<>();
        for (int n = 1; n < total; n++) {
            cuadrados.add(cuadrado(n));
            triangulares.add(triangular(n));
        }
        triangulares.retainAll(cuadrados);
        cuadrados.retainAll(triangulares);
        LOGGER.error(cuadrados);
        LOGGER.error(triangulares);
    }

    private static IntegerPair cuadrado(final int n) {
        return new IntegerPair(Integer.valueOf(n), Integer.valueOf(n * n));
    }

    private static IntegerPair triangular(final int n) {
        return new IntegerPair(Integer.valueOf(n), Integer.valueOf(n * (n + 1) / 2));
    }
}
