/*
 * Creado el 28-feb-2008
 */
package org.pclg.dictionary;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.Stack;

import java.util.LinkedList;

/**
 * @author Un autor en busca de personajes.
 */
final class DictionaryStack implements Stack<String>, Cloneable {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private LinkedList<String> delegate = new LinkedList<>();
	private final String nombre;

	DictionaryStack(final String nombre) {
		this.nombre = nombre;
	}

	/* (sin Javadoc)
	 * @see org.pclg.tools.Stack#bury()
	 */
	@Override
	public String unbury() {
		final String item = delegate.removeLast();
		if (Dictionary.DEBUG) {
		    LOGGER.debug(nombre + ":unbury(): " + this);
		}
		return item;
	}

	/* (sin Javadoc)
	 * @see org.pclg.tools.Stack#bury(java.lang.Object)
	 */
	@Override
	public String bury(final String item) {
		if (delegate.isEmpty() || !item.equals(delegate.getLast())) {
			delegate.addLast(item);
		}
		if (Dictionary.DEBUG) {
			LOGGER.debug(nombre + ":bury(): " + this);
		}
		return item;
	}


	/* (sin Javadoc)
	 * @see org.pclg.dictionary.Stack#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	/* (sin Javadoc)
	 * @see org.pclg.dictionary.Stack#peek()
	 */
	@Override
	public String peek() {
		return delegate.getLast();
	}

	/* (sin Javadoc)
	 * @see org.pclg.dictionary.Stack#search(java.lang.Object)
	 */
	@Override
	public int search(final String item) {
		return delegate.lastIndexOf(item);
	}


	/* (sin Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		final DictionaryStack theClone = (DictionaryStack) super.clone();
		theClone.delegate = (LinkedList<String>) delegate.clone();
		return theClone;
	}
}
