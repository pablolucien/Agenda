package org.pclg.agenda.crypto;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

/**
 * Clase encargada de la criptografía, pero que no hace nada.
 *
 * @author El Coyote
 * @since 28-jul-2012 00:45:00
 */
public final class DummyCrypto implements Cryptographer {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	public DummyCrypto(final String pwd) {
		final String pwd1 = pwd;
	}

	public String cifrarXOR(final String datum) {
		if (datum == null) {
			return null;
		}
		final byte[] bytes = datum.getBytes();
		for (int ii = 0; ii < bytes.length; ii++) { 
			bytes[ii] ^= 8;
		}
		return new String(bytes);
	}


	@Override
	public String cifrar(final String datum) {
		return datum;
	}

	@Override
	public String descifrar(final String datum) {
		return cifrar(datum);
	}
	
	String cifrar_kk(final String datum) {
		if (datum == null) {
			return null;
		}
		final byte[] bytes = datum.getBytes();
		for (int ii = 0; ii < bytes.length; ii++) {
			if (bytes[ii] == '(' || bytes[ii] == ')' || bytes[ii] == '\'' || bytes[ii] == ',') {
				continue;
			}
			bytes[ii] ^= 8;
		}
		return new String(bytes);
	}

	public static void main(final String[] args) {
		final String sql = "('058','Venezuela'),('033','France'),('034','Espa±a'),('044','England'),('046','Sverige'),('001','USA'),('049','Deutschland'),('055','Brasil'),('054','Argentina'),('052','Mexico'),('043','Ísterreich')";
		final DummyCrypto crypto = new DummyCrypto("");
		LOGGER.debug(crypto.cifrar_kk(sql));
	}
}
