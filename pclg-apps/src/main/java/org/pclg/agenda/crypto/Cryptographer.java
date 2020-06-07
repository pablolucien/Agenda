package org.pclg.agenda.crypto;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 26-sep-2007 10:49:53
 */
interface Cryptographer {
	String cifrar(String datum) throws CryptoException;

	String descifrar(String datum) throws CryptoException;
}
