/*
 * Creado el 14-mar-2008
 */
package org.pclg.tools;

/**
 * @author Un autor en busca de personajes.
 */
public interface Stack<T> {
	/**
     * Lo mismo que pop, pero usando la terminología de Alan Turing.
     *
	 * @see java.util.Stack#pop()
	 */
	T unbury();

	/* (sin Javadoc)
	 * @see java.util.Stack#peek()
	 */
	T peek();

    /**
        * Lo mismo que push, pero usando la terminología de Alan Turing.
        *
	 * @see java.util.Stack#push(java.lang.Object)
	 */
	T bury(final T item);

	/* (sin Javadoc)
	 * @see java.util.Stack#search(java.lang.Object)
	 */
	int search(final T item);

	/**
	 * @see java.util.Stack#empty()
	 */
	boolean isEmpty();

    /**
     * @return <code>true</code> if this Stack can't accept more items.
     */
	default boolean isFull() {
        return false;
    }
}