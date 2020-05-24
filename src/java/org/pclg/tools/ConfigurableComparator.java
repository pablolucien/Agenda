package org.pclg.tools;

import java.util.Comparator;

/**
 * Esta interfaz define comparadores cuyo criterio de sort
 * se puede configurar dinámicamente.
 *
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 17-oct-2007 20:58:47
 */
public interface ConfigurableComparator<T> extends Comparator<T> {
	class SortCritery {
	    SortCritery ASCENDING = new SortCritery(), DESCENDING = new SortCritery();
	}

	void setSortCritery(int critery);
}
