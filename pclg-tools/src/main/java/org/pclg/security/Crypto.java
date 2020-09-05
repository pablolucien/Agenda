package org.pclg.security;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.Chrono;
import org.pclg.tools.FileTools;
import org.pclg.tools.ToolBox;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;

/**
 * Crypto<BR>
 *
 * @author El Coyote Cojo
 * @version 1.0  - Refaactorizaci�n de lo que hab�a en Cr en el package default.
 * @since 15-sep-2010 17:29:05
 */
public class Crypto {
    /** magic: 'CRYPT' cifrado segun Julio Cesar. */
    private static final byte[] MAGIC = {
        (byte) 'D', (byte) 'S', (byte) 'Z', (byte) 'Q', (byte) 'U'
    };

    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    /**  Size of the buffers. */
    private static final int BUFFER_SIZE = 1024 * 50;

    /**  The buffer. */
    private final byte[] buffer = new byte[BUFFER_SIZE];

    /** El hash del password. */
    private final byte[] passwordHash;

    /** Operacion a efectuar. */
    private final boolean decrypt;

    /** Este se encarga del trabajo. */
    private final Cipher cipher;

	/**
	 * Creates an Cr.
	 * @param password the key used to crypt or decrypt.
	 * @param decrypt wheather we are going to crypt or decrypt.
	 * @throws GeneralSecurityException if an error occurs.
	 */
    public Crypto(final String password, final boolean decrypt) throws GeneralSecurityException {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        this.decrypt = decrypt;
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        // use "UTF-8" since it is implemented on all platforms and is able
        // to cope with all characters
        final byte[] passwordBytes = ToolBox.pad(password, 8, ' ').getBytes(StandardCharsets.UTF_8);
        passwordHash = messageDigest.digest(passwordBytes);

        final String algorithm = "Blowfish";
//			final String algorithm = "PBEWithMD5AndDES";
//			final String algorithm = "DES";
//			final String algorithm = "DESede";

//        	check4Algorithm(algorithm);

//        	logMsg("Creating Crypto instance with algorithm " + algorithm);
        cipher = Cipher.getInstance(algorithm);
        cipher.init(decrypt ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE, new SecretKeySpec(passwordBytes, algorithm));
    }


    private boolean check4Algorithm(final String algorithm) {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        for (final Provider provider : Security.getProviders()) {
            LOGGER.warn(provider + " -> " + provider.getInfo());
            for (final Object key : provider.keySet()) {
                LOGGER.warn("\t -> " + key);
            }

        }
        // Install SunJCE provider
//        final Provider sunJce = new SunJCE();
//        Security.addProvider(sunJce);
        return false;
    }


    /**
     * Procesa un archivo.
     *
     * @param target     el archivo a procesar.
     * @param cronHandle usado para medir los tiempos.
     * @param msg        indica si estamos cifrando o descifrando.
     * @throws java.io.IOException si hay errores de I/O.
     */
    public void processFile(final File target, final int cronHandle,
                            final String msg) throws IOException {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        if (checkFile(target) && checkForSpace(target, target.getParentFile())) {
            if (cronHandle != -1) {
                Chrono.start(cronHandle);
            }
            cryptFile(target);
            if (cronHandle != -1) {
                Chrono.mark(cronHandle);
                logMsg(msg + ' ' + target.getName() + ": " + Chrono.timeDetail(Chrono.elapsed(cronHandle)));
            } else {
                logMsg(msg + ' ' + target.getName() + ": ");
            }
        }
    }


    /**
     * Verifica que podemos procesar el arechivo.
     *
     * @param target el archivo a verificar.
     * @return true si es posible procesarlo; false de lo contrario.
     */
    private static boolean checkFile(final File target) {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        boolean fileOk = true;
        if (!target.exists()) {
            logError(emphasize(target.getName()) + " no existe");
            fileOk = false;
        } else if (!target.canRead()) {
            logError("No puedo leer " + emphasize(target.getName()));
            fileOk = false;
        } else if (!target.isFile()) {
            logError("No puedo cifrar " + emphasize(target.getName()));
            if (target.isDirectory()) {
                logError(": Es un directorio");
            } else {
                logError(": No se que es");
            }
            fileOk = false;
        }

        return fileOk;
    }

    /**
     * Checks if there is enough space for the file.
     *
     * @param target the file to check.
     * @param dir    the dir where to check for space.
     * @return true if here is enough space; false otherwise.
     */
    private static boolean checkForSpace(final File target, final File dir) {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        boolean enoughSpace = true;
        try {
            if (!FileTools.checkForSpace(target.length(), dir)) {
                logError("No hay espacio suficiente para procesar \""
                        + target.getName() + '\"');
                enoughSpace = false;
            }
        } catch (final NoClassDefFoundError ex) {
            logError("No encuentro org.pclg.tools.FileTools en el classpath:"
                    + " No verifico el espacio disponible");
        } catch (final IOException ex) {
            logError("Error en FileTools: " + ex.getMessage()
                    + " No verifico el espacio disponible");
        }
        return enoughSpace;
    }

    /**
     * Contrato: inFile.isFile() tiene que ser 'true', verificado por el caller.
     *
     * @param targetFile el archivo a procesar.
     * @throws IOException si hay problemas.
     */
    private void cryptFile(final File targetFile) throws IOException {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        FileTools.convertFileInPlace(targetFile,
                (inFile, outFile) -> {
                    try (final FileInputStream fis = new FileInputStream(inFile);
                         final FileOutputStream fos = new FileOutputStream(outFile)) {
                        if (decrypt) {
                            if (!checkMagicAndPassword(fis)) {
                                throw new SecurityException(">>>> La contrase�a no es correcta <<<<");
                            }
                        } else {
                            fos.write(MAGIC);
                            fos.write(passwordHash);
                        }
                        doCipher(fis, fos);
                    } catch (final IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }


    /**
     * Verifica que el archivo a desencriptar est� realmente cifrado por Cr y
     * la contase�a coincida.
     *
     * @param fis El sitio de donde vamos a leer
     * @return true si todas las condiciones se verifican.
     * @throws IOException si hay problemas.
     * @since 2002-12-26 22:04:00
     */
    private boolean checkMagicAndPassword(final InputStream fis) throws IOException {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        boolean isOK = hasMagic(fis);
        if (!isOK) {
            logError(">>>> No hay magia. Yo no reconozco haber cifrado esto <<<<");
        } else {
            // hasMagic(fis) has the side effect of moving the fis pointer to the begining of the hash
            final byte[] hash = new byte[passwordHash.length];
            final int count = fis.read(hash);
            if (count != passwordHash.length || !Arrays.equals(hash, passwordHash)) {
                logError(">>>> La contrase�a no es correcta <<<<");
                isOK = false;
            }
        }
        return isOK;
    }

    private static boolean hasMagic(final InputStream fis) throws IOException {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        final byte[] magic = new byte[MAGIC.length];
        final int count = fis.read(magic);
        return count == MAGIC.length && Arrays.equals(magic, MAGIC);
    }

    public static boolean isEncrypted(final File file) throws IOException {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        final boolean hasMagic;
        try (final InputStream fis = new FileInputStream(file)) {
            hasMagic = hasMagic(fis);
        }
        LOGGER.debug(LoggerFactory.EXIT_METHOD);
        return hasMagic;
    }

    /**
     * Does the cipher of an InputStream into an OutputStream.
     *
     * @param fis the InputStream.
     * @param fos the OutputStream.
     * @throws IOException if an I/O error occurs.
     */
    private void doCipher(final InputStream fis, final OutputStream fos)
            throws IOException {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        try (final CipherInputStream cis = new CipherInputStream(fis, cipher)) {
            int count;
            while ((count = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
            }
        }
    }

    /**
     * Returns a string  'emphasized'.
     *
     * @param string the string to 'emphasize'.
     * @return the string 'emphasize'.
     */
    private static String emphasize(final String string) {
        return '\"' + string + '\"';
    }

    /**
     * Logs a message to standard output.
     *
     * @param msg the message to log.
     */
    private static void logMsg(final String msg) {
        LOGGER.log(Level.OFF, msg);
    }

    /**
     * Logs a message to error output.
     *
     * @param msg the message to log.
     */
    private static void logError(final String msg) {
        LOGGER.error(msg);
    }
}
