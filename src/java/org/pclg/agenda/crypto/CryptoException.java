package org.pclg.agenda.crypto;

/**
 * @author El Coyote
 * @since 12-sep-2007 11:49:36
 */
public final class CryptoException extends Exception {
	private static final long serialVersionUID = -7418452065874427840L;

	CryptoException(final Throwable throwable) {
		super(throwable);
	}
}
