package org.pclg.agenda.crypto;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 22-oct-2007 18:32:45
 */
@SuppressWarnings({"ClassWithoutLogger"})
public class AgendaCryptoTest {
	private AgendaCrypto agendaCrypto;
	private static final String PLAINTEXT = "Erase un hombre a una nariz pegado";
	private static final String PWD = "password";

	@BeforeMethod
	public void setUp() throws Exception {
		agendaCrypto = new AgendaCrypto(AgendaCrypto.TRANSFORMATION.Blowfish, PWD);
	}

	@Test
	public void testCifrar() throws Exception {
		final String cipherText = agendaCrypto.cifrar(PLAINTEXT);
//		System.out.println(cipherText);
		final String obscureText = new String(cipherText);
		assertNotEquals(PLAINTEXT, obscureText);
	}

	@Test
	public void testDescifrar() throws Exception {
		final String cipherText = agendaCrypto.cifrar(PLAINTEXT);
//		System.out.println(cipherText);
		assertEquals(PLAINTEXT, agendaCrypto.descifrar(cipherText));
	}
}