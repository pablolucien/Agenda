/*
 * Creado el 04-mar-2008
 */
package org.pclg.dictionary;

/**
 * @author Un autor en busca de personajes.
 */
class DictionaryDBException extends Exception {

    private static final long serialVersionUID = 3932097145315901063L;

    /**
	 * 
	 */
	public DictionaryDBException() {
	}

	/**
	 * @param message
	 */
	public DictionaryDBException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DictionaryDBException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public DictionaryDBException(final Throwable cause) {
		super(cause);
	}

}
