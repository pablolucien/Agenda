package org.pclg.agenda.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Clase encargada de la criptografía.
 *
 * @author El Coyote
 * @since 12-sep-2007 11:43:27
 */
public final class AgendaCrypto implements Cryptographer {
	/** Cifrador para cifrar. */
	private final Cipher cipherC;

	/** Cifrador para descifrar. */
	private final Cipher cipherD;

	/** El algoritmo a usar. */
    //FIXME: Qué significa cada uno?
//	private static final String TRANSFORMATION = "Blowfish";
//	private final String TRANSFORMATION = "DES";
//	private final String TRANSFORMATION = "DES/CFB/NoPadding";
//	private final String TRANSFORMATION = "DES/CFB8/NoPadding";
//	private final String TRANSFORMATION = "DES/OFB32/PKCS5Padding";
//	private final String TRANSFORMATION = "DES/CBC/PKCS5Padding";
	public enum TRANSFORMATION {
		Blowfish("Blowfish"),
		Blowfish_ECB("Blowfish/ECB/PKCS5Padding", "Blowfish"),
		Blowfish_CFB("Blowfish/CFB/NoPadding", "Blowfish"),
		DES("DES"),
		DES_CFB_NoPadding("DES/CFB/NoPadding"),
		DES_CFB8_NoPadding("DES/CFB/NoPadding"),
		AES("AES"),
		;
		
		private final String name;
		private final String shortName;
		
		TRANSFORMATION(final String name) {
			this.name = name;
			shortName = name;
		}

		TRANSFORMATION(final String name, final String shortName) {
			this.name = name;
			this.shortName = shortName;
		}

		/**
		 * @return the nombre
		 */
		public String getName() {
			return name;
		}
//		
//		/**
//		 * Adapta la password que se le pasa a lo que necesita el algoritmo. 
//		 * For instance: AES needs a key size of 128, 192, or 256 bits. 
//		 * 
//		 * @return the massaged password
//		 */
//		public String massagePassword(final String pwd) {
//			return nombre;
//		}

		public String getShortName() {
			return shortName;
		}
	}

	private final TRANSFORMATION transformation;
	AgendaCrypto(final TRANSFORMATION transformation, final String pwd) throws CryptoException {
		this.transformation = transformation;
		try {
			cipherD = Cipher.getInstance(transformation.getName());
			cipherC = Cipher.getInstance(transformation.getName());
			initCipher(pwd);
		} catch (final NoSuchAlgorithmException | NoSuchPaddingException
			| InvalidKeyException | UnsupportedEncodingException ex) {
			throw new CryptoException(ex);
		}
	}

	private void initCipher(final String pwd) throws InvalidKeyException, UnsupportedEncodingException {
		final SecretKeySpec skeySpec = new SecretKeySpec(pwd.getBytes(StandardCharsets.UTF_8), transformation.getShortName());
		cipherC.init(Cipher.ENCRYPT_MODE, skeySpec);
		cipherD.init(Cipher.DECRYPT_MODE, skeySpec);
	}

	@Override
	public String cifrar(final String datum) throws CryptoException {
		try {
			return new String(Base64.getEncoder().encode(cipherC.doFinal(datum.getBytes())));
		} catch (final IllegalBlockSizeException | BadPaddingException ex) {
			throw new CryptoException(ex);
		}
	}

	@Override
	public String descifrar(final String datum) throws CryptoException {
		try {
			return new String(cipherD.doFinal(Base64.getDecoder().decode(datum)));
		} catch (final IllegalBlockSizeException | BadPaddingException ex) {
			throw new CryptoException(ex);
		}
	}

//	public static void main(final String[] args) throws CryptoException {
//		final AgendaCrypto agendaCrypto = new AgendaCrypto();
//		final String [] data = {
//				"1234567890123456789012345678901234567890123456789012345678901234",
//				"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
//				"123456789012345678901234567890123456789012345678901234567890",
//				"1234567890123456789012345678901234567890123456789012345678901",
//				"12345678901234567890123456789012345678901234567890123456789012",
//				"123456789012345678901234567890123456789012345678901234567890123",
//				"áedñopksku&574~+**óújjj++7844jjBNBJLAcPOLonrÑÑkkdpowiifjnskio^|",
//				"1234567890123456789012345678901234567890123456789012345678901234",
//		};
//		for (final String str : data) {
//			System.out.println("Len = " + agendaCrypto.cifrar(str).length);
//		}
//		/*
//		Tamaño del array cifrado en relación con el tamaño del plaintext
//		40 -> 48
//		50 -> 56
//		60 -> 64
//		61 -> 64
//		62 -> 64
//		63 -> 64
//		64 -> 72
//		*/
//	}
}
