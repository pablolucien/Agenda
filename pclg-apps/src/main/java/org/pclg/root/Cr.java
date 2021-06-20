package org.pclg.root;

import org.pclg.security.Crypto;
import org.pclg.tools.Chrono;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;


/**
 * Cr <BR>
 * Cifra y descifra archivos.
 * 2002-12-30 Agregada la verificacion de magic y password.
 *
 * @author El Coyote Cojo
 * @version 2002.dec.21
 */
public final class Cr {
	/**
	 * Prevent instantiation.
	 */
	private Cr() {
	}

	/**
     * Obtiene la clave a usar.
     * @param shouldDecrypt indica si se va a cifrar o descifrar.
     * @return la contrase�a a usar.
     * @throws IOException si hay problemas.
     */
    private static String getPassword(final boolean shouldDecrypt)
            throws IOException {
        String pwd;
        if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
            writeMsg("WARNING: Caps lock is on. Are you sure? Write 'YES' to continue.");
			final BufferedReader keyboard =
				new BufferedReader(new InputStreamReader(System.in));
			if (!"YES".equals(keyboard.readLine())) {
				writeMsg("Aborting process");
				System.exit(-1);
			}
        }
        final Console console = System.console();
        if (console != null) {
            char[] pwd1;
            char[] pwd2;
            do {
                System.out.print("Enter password: ");
                pwd1 = console.readPassword();
                if (shouldDecrypt) {
                    pwd2 = pwd1;
                } else {
                    System.out.print("Reenter please: ");
                    pwd2 = console.readPassword();
                }
            } while (!Arrays.equals(pwd1, pwd2) 
					|| pwd1	== null || pwd1.length == 0);
            pwd = new String(pwd1);
        } else {
            final BufferedReader keyboard =
                new BufferedReader(new InputStreamReader(System.in));
            do {
                System.out.print("Enter password (WARNING in cleat text): ");
            } while ((pwd = keyboard.readLine()).length() == 0);
        }
        return pwd;
    }

    /**
     * Ayuda al usuario.
     */
    private static void usage() {
        writeMsg("Usage: java Cr [-d] <file(s)>");
        System.exit(1);
    }

    /**
     * Entry point to the application.
     *
     * @param args arguments to the app.
     * @throws IOException when some I/O error occurs.
     * @throws GeneralSecurityException when some security error occurs.
     */
    public static void main(final String[] args) throws IOException,
            GeneralSecurityException {
        //logMsg("To do:\n1) Eliminar los temporales en caso de falla (�con cuidado!)");
        if (args.length == 0) {
            usage();
        }
        int firstArg = 0;

        boolean decrypt = false;

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("-d")) {
                decrypt = true;
                firstArg++;
            }
        }

        boolean hayAlgo = false;
        final File[] targets = new File[args.length - firstArg];
        for (int ii = 0; ii < targets.length; ii++) {
            targets[ii] = new File(args[ii + firstArg]);
            if (targets[ii].exists()) {
                hayAlgo = true;
            }
        }

        if (hayAlgo) {
            processBatch(targets, decrypt);
        } else {
            logError("\nNo existe ningun archivo para procesar");
        }
    }

    /**
     * Cifra o descifra un lote de archivos.
     * @param targets los archivos que hay que porocesar.
     * @param decrypt indica si fa a cifrar o descifrar.
     * @throws IOException si hay problemas de I/O.
     * @throws GeneralSecurityException Si hay problemas con el (des)cifrado.
     */
    private static void processBatch(final File[] targets, final boolean decrypt)
            throws IOException, GeneralSecurityException {
        final String msg = decrypt ? "Descifrando" : "Cifrando";

        final String password = getPassword(decrypt);
        int cronHandle = -1;
        try {
            System.out.print("Inicializando: ");
            cronHandle = Chrono.getChrono();
            Chrono.start(cronHandle);
        } catch (final NoClassDefFoundError ex) {
            logError("No encuentro org.pclg.tools.Chrono en el classpath: "
                + "No mido los tiempos");
        }

        final Crypto krypto = new Crypto(password, decrypt);
        if (cronHandle != -1) {
            Chrono.mark(cronHandle);
            writeMsg(Chrono.timeDetail(Chrono.elapsed(cronHandle)));
        }

        for (final File target : targets) {
            krypto.processFile(target, cronHandle, msg);
        }
    }

    /**
     * Logs a message to standard output.
     * @param msg the message to log.
     */
    private static void writeMsg(final CharSequence msg) {
        System.out.println(msg);
    }

    /**
     * Logs a message to error output.
     * @param msg the message to log.
     */
    private static void logError(final CharSequence msg) {
        System.err.println(msg);
    }
}