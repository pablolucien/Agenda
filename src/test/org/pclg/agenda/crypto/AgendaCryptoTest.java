package org.pclg.agenda.crypto;
/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 22-oct-2007 18:32:45
 */


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@SuppressWarnings({"ClassWithoutLogger"})
public class AgendaCryptoTest {
	private AgendaCrypto agendaCrypto;
	private static final String PLAINTEXT = "Erase un hombre a una nariz pegado";
	private static final String PWD = "password";

	@Before
	public void setUp() throws Exception {
		agendaCrypto = new AgendaCrypto(AgendaCrypto.TRANSFORMATION.Blowfish, PWD);
	}

	@Test
	public void testCifrar() throws Exception {
		final String cipherText = agendaCrypto.cifrar(PLAINTEXT);
//		System.out.println(cipherText);
		final String obscureText = new String(cipherText);
		assertFalse(obscureText.equals(PLAINTEXT));
	}

	@Test
	public void testDescifrar() throws Exception {
		final String cipherText = agendaCrypto.cifrar(PLAINTEXT);
//		System.out.println(cipherText);
		assertEquals(PLAINTEXT, agendaCrypto.descifrar(cipherText));
	}
}