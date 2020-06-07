// ******************************** package

// ******************************** imports

import org.pclg.security.Digest;
import org.pclg.tools.Chrono;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * Diff. <BR>
 *
 * @author El Coyote Cojo
 * @version 2003.abr.25 12:02:27, CEST
 */
public final class Diff {
	// ******************************** Variables de clase
	private static final String DIFF_FILE_SUFIX = ".diff.txt";
	private static final String HEADER_FILE_SUFIX = ".diff_header.txt";
	private static final int SHA1_DIGEST_SIZE = 20;
	private static final int BUFFER_SIZE = 1024 * 1024;

	// ******************************** Variables de instancia
	private int cronHandle = -1;

	// ******************************** Constructores

	/**
	 * --author El Coyote Cojo
	 * @since 2003.abr.25 12:02:27, CEST
	 * @since 2003.may.10
	 */
	private Diff(final boolean combine, final String newDirName,
			final String oldDirName,
			final String diffDirName) {
		final File newDir = new File(newDirName);
		final File oldDir = new File(oldDirName);
		final File diffDir = new File(diffDirName);

		// Sanity check.
		checkDirs(combine, newDir, oldDir, diffDir);

		initCron();

		if (combine) {
			doCombination(oldDir, diffDir);
		} else {
			doCreation(newDir, oldDir, diffDir);
		}
	}

	// ******************************** Metodos de instancia

	/**
	 * Chequea los directorios.
	 *
	 * @since 2003.05.10
	 */
	private static void checkDirs(final boolean combine, final File newDir,
			final File oldDir,
			final File diffDir) {
		// Sanity check. Se podría hacer con assert
		if (!combine && !newDir.isDirectory())
		{	// Cuando estoy combinando, no necesito newDir (ni siquiera existe)
			throw new RuntimeException(newDir + " no es un directorio");
		}

		if (!oldDir.isDirectory()) {
			throw new RuntimeException(oldDir + " no es un directorio");
		}

		if (!diffDir.isDirectory()) {
			throw new RuntimeException(diffDir + " no es un directorio");
		}
	}

	/**
	 * Inicializa la medicion de tiempos, si está disponible.
	 *
	 * @since 2003.05.10
	 */
	private void initCron() {
		try {
			cronHandle = Chrono.getChrono();
		}
		catch (final NoClassDefFoundError ex) {
			System.err.println("No encuentro <org.pclg.tools.Chrono> "
					+ "en el classpath: No mido los tiempos");
		}
	}

	/**
	 * Comienza la medicion de tiempos, si está disponible.
	 *
	 * @since 2003.05.10
	 */
	private void startTiming() {
		if (cronHandle != -1) {
			Chrono.start(cronHandle);
		}
	}

	/**
	 * Informa sobre la medicion de tiempos, si está disponible.
	 *
	 * @since 2003.05.10
	 */
	private void printTiming() {
		if (cronHandle != -1) {
			Chrono.mark(cronHandle);
			System.out.println(Chrono.timeDetail(Chrono.elapsed(cronHandle)));
		}
	}

	/**
	 * Prepara la combinacion.
	 *
	 * @since 2003.05.10
	 */
	private void doCombination(final File oldDir, final File diffDir) {
		final File[] diffFiles = diffDir.listFiles();

		for (int i = 0; i < diffFiles.length; i++) {
			if (!diffFiles[i].isFile()) {
				System.out.println(diffFiles[i] + " no es un archivo");
				continue;
			}
			String name = diffFiles[i].getName();
			if (!name.endsWith(HEADER_FILE_SUFIX)) {
				continue;
			}

			name = name
					.substring(0, name.length() - HEADER_FILE_SUFIX.length());

			final File oldFile = new File(oldDir, name);
			if (!oldFile.exists()) {
				System.out.println(oldFile + " no existe");
				continue;
			}

			System.out.println(name);
			startTiming();
			combineDiffFiles(oldFile, new File(diffDir, name + DIFF_FILE_SUFIX),
					new File(diffDir, name + HEADER_FILE_SUFIX));
			printTiming();
		}
	}

	/**
	 * Prepara la creacion.
	 *
	 * @since 2003.05.10
	 */
	private void doCreation(final File newDir, final File oldDir,
			final File diffDir) {
		final File[] newFiles = newDir.listFiles();

		for (int i = 0; i < newFiles.length; i++) {
			if (!newFiles[i].isFile()) {
				System.out.println(newFiles[i] + " no es un archivo");
				continue;
			}
			final String name = newFiles[i].getName();
			final File oldFile = new File(oldDir, name);
			if (!oldFile.exists()) {
				System.out.println(oldFile
						+ " no existe: La diferencia es el archivo completo");
				continue;
			}

			System.out.println(name);

			startTiming();
			createDiffFiles(oldFile, newFiles[i],
					new File(diffDir, name + DIFF_FILE_SUFIX),
					new File(diffDir, name + HEADER_FILE_SUFIX));
			printTiming();
		}
	}

	/**
	 * Incorpora las diferencias al archivo viejo.
	 */
	private static void combineDiffFiles(final File oldFile,
			final File diffFile,
			final File diffHeaderFile) {
		try {
			final RandomAccessFile oldOut = new RandomAccessFile(oldFile, "rw");
			final BufferedInputStream diffIn = new BufferedInputStream(
					new FileInputStream(diffFile));
			final DataInputStream diffHeadIn = new DataInputStream(
					new BufferedInputStream(
							new FileInputStream(diffHeaderFile)));

			boolean adding = true;
			while (adding) {
				try {
					final int pos = diffHeadIn.readInt();
					final int len = diffHeadIn.readInt();
					if (len > 0)
					{		// Es importante la comprobacion, porque podriamos extender el archivo aunque no haya datos
						oldOut.seek(pos);
						for (int i = 0; i < len; i++) {
							final int c = diffIn.read();
							if (c == -1) {
								System.out.println("Pos = " + pos + ", len = "
										+ len
										+ " <<< FIN DEL ARCHIVO DE ENTRADA >>> ");
								break;
							}
							oldOut.write(c);
						}
					} else {
						System.out.println("Pos = " + pos + ", len = " + len);
					}
				}
				catch (final EOFException ex) {
					adding = false;
					//ex.printStackTrace();
					System.out.println("Se acabó la entrada");
				}
			}

			oldOut.close();
			diffIn.close();
			diffHeadIn.close();
		}
		catch (final IOException ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * Crea los archivos con las diferencias
	 */
	private static void createDiffFiles(final File oldFile, final File newFile,
			final File diffFile,
			final File diffHeaderFile) {
//Poner 0 en los espacios vacios de kazaa, para que comprima mejor
		try {
			final BufferedInputStream oldIn = new BufferedInputStream(
					new FileInputStream(oldFile));
			final BufferedInputStream newIn = new BufferedInputStream(
					new FileInputStream(newFile));
			final BufferedOutputStream diffOut = new BufferedOutputStream(
					new FileOutputStream(diffFile));
			final DataOutputStream diffHeadOut = new DataOutputStream(
					new BufferedOutputStream(
							new FileOutputStream(diffHeaderFile)));

			// Suponemos que la longitud de new es >= que la de old.
			//  Habría que verificarlo
			// 2003.05.20: de hecho, es un error. hay que truncarlo ???????????
			byte oldByte;
			byte newByte;
			int c;
			int pos = 0;
			int len = 0;
			boolean adding = false;
			boolean lenWritten = false;		// No se si se puede hacer mejor
					// revisando la logica, el problema es que podemos
					// salir del ciclo sin haber escrito 'len'

			// Procesamos la longitud comun
			while ((c = oldIn.read()) != -1) {
				oldByte = (byte) c;
				c = newIn.read();
				newByte = (byte) c;
				if (oldByte != newByte) {
					if (!adding) {
						adding = true;
						diffHeadOut.writeInt(pos);
						lenWritten = false;
						len = 0;
					}
					len++;
					diffOut.write(newByte);
				} else {
					if (adding) {
						adding = false;
						diffHeadOut.writeInt(len);
						lenWritten = true;
						len = 0;
					}
				}
				pos++;
			}

			if (!lenWritten) {
				diffHeadOut.writeInt(len);
				len = 0;
			}

			// Procesamos el resto
			diffHeadOut.writeInt(pos);
			while ((c = newIn.read()) != -1) {
				len++;
				newByte = (byte) c;
				diffOut.write(newByte);
			}
			diffHeadOut.writeInt(len);

			oldIn.close();
			newIn.close();
			diffOut.close();
			diffHeadOut.close();
		}
		catch (final IOException ex) {
			ex.printStackTrace();
		}
	}


	// ******************************** Metodos estaticos
	private static void genDigest(final String dirName,
			final String targetDirName) {
		final File dir = new File(dirName);
		if (!dir.isDirectory()) {
			System.err.println("<" + dir + "> No es un directorio valido");
			return;
		}
		final File targetDir = new File(targetDirName);
		if (!targetDir.isDirectory()) {
			System.err
					.println("<" + targetDir + "> No es un directorio valido");
			return;
		}
		final File[] files = dir.listFiles();
		final byte[] buffer = new byte[BUFFER_SIZE];
		int count;

		for (int i = 0; i < files.length; i++) {
			if (!files[i].isFile()) {
				System.out.println(files[i] + " no es un archivo");
				continue;
			}
			final String name = files[i].getName();
			System.out.println(name);
			try {
				final FileInputStream fis = new FileInputStream(files[i]);
				final FileOutputStream fos = new FileOutputStream(
						new File(targetDir, name + ".digest"));
				while ((count = fis.read(buffer)) > 0) {
					// Limpiar el fin del buffer si la lectura no lo llenó
					if (count < buffer.length) {
						for (int jj = count; jj < buffer.length; jj++) {
							buffer[jj] = 0;
						}
					}
					final byte[] digest = 
                            Digest. generateByteArrayDigest(buffer, "SHA-1");
					fos.write(digest);
				}
				fis.close();
				fos.close();
			}
			catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void compareDigest(final String dirName1,
			final String dirName2) {
		final File dir1 = new File(dirName1);
		final File[] files1 = dir1.listFiles();
		final File dir2 = new File(dirName2);
		//File[] files2 = dir.listFiles2();
		File file2;
		final byte[] buffer1 = new byte[SHA1_DIGEST_SIZE];
		final byte[] buffer2 = new byte[SHA1_DIGEST_SIZE];
		int count;
		int chunkCount;

		for (int i = 0; i < files1.length; i++) {
			if (!files1[i].isFile()) {
				System.out.println(files1[i] + " no es un archivo");
				System.out.println();
				continue;
			}
			final String name = files1[i].getName();
			System.out.println(name);

			file2 = new File(dir2, name);

			if (!file2.exists()) {
				System.out.println("El segundo archivo no existe");
				System.out.println();
				continue;
			}

			if (files1[i].length() != file2.length()) {
				System.out.println("Son de tamaños diferentes");
			}

			boolean equal;
			try {
				final FileInputStream fis1 = new FileInputStream(files1[i]);
				final FileInputStream fis2 = new FileInputStream(file2);
				chunkCount = 0;
				equal = true;
				while ((count = fis1.read(buffer1)) > 0) {
					if (count != SHA1_DIGEST_SIZE) {
						System.out.println("Leidos solo " + count
								+ " bytes en el primer archivo");
						break;
					}
					count = fis2.read(buffer2);
					if (count != SHA1_DIGEST_SIZE) {
						//System.out.println("Leidos solo " + count + " bytes en el segundo archivo");
						//break;
					}

					if (count != SHA1_DIGEST_SIZE || !Arrays
							.equals(buffer1, buffer2)) {
						//System.out.println("Diferencia en el bloque " + chunkCount);
						System.out.print(" " + chunkCount);
						equal = false;
					}

					chunkCount++;
				}
				if (equal) {
					System.out.println("Son iguales");
				} else {
					System.out.println();
				}

				fis1.close();
				fis2.close();
				System.out.println();
			}
			catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Ayuda al usuario
	 * Al ser un método publico, puede ser llamado por cualquier otra clase, en particular un sistema de ayuda
	 * <p/>
	 * --author El Coyote Cojo
	 * @since 2003.abr.25 12:02:27, CEST
	 */
	private static void usage() {
		System.err.println("Usage: java Diff <new_dir> <old_dir> <diff_dir>");
		System.err.println("    java Diff " + "-combine  <old_dir> <diff_dir>");
		System.err
				.println("    java Diff " + "-digest   <dir>     <target dir>");
		System.err.println("    java Diff " + "-dig_comp <dir1>    <dir2>");
		System.exit(1);
	}

	/**
	 * Ejecuta la aplicación.
	 * <p/>
	 * --author El Coyote Cojo
	 * @since 2003.abr.25 12:02:27, CEST
	 */
	public static void main(final String[] args) {
		if (args.length != 3) {
			usage();
		}

		if (args[0].equals("-digest")) {
			genDigest(args[1], args[2]);
			return;
		}

		if (args[0].equals("-dig_comp")) {
			compareDigest(args[1], args[2]);
			return;
		}

		final boolean combine = args[0].equals("-combine");
		new Diff(combine, args[0], args[1], args[2]);
	}
}
