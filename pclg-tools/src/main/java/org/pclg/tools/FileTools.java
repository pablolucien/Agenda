// ******************************** package
package org.pclg.tools;

// ******************************** imports

import org.apache.logging.log4j.Logger;
import org.pclg.filesystem.fileattributes.FileAttributes;
import org.pclg.filesystem.fileattributes.FileAttributesFactory;
import org.pclg.log.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Provee algunas funciones de uso comun para manejo de archivos
 *
 * @author El Coyote Cojo
 * @version 2002.04.01    (D?a de los inocentes y cumplea?os del Chema y Rita Elisa)
 */
public final class FileTools {
    @SuppressWarnings("HardcodedFileSeparator")
	private static final String WINDOWS_INVALID_FILENAME_CHARACTERS_REGEX =
		"[\\\\/:\\*\\?\"<>\\|]";

	public static final File NULL_FILE = new File("***");
	public static final File[] NULL_FILE_ARRAY = new File[0];
    public static final Iterator<File[]> NULL_FILE_ITERATOR = new Iterator<File[]>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public File[] next() {
            throw new NoSuchElementException("No elements in this Iterator");
        }
    };

	/** El Logger de esta clase. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	private static final int BUFFER_SIZE = 10 << 10; // 10 * 1024
	/** ??? Suponemos archivos de 512 Kb o menos. */
	private static final int READ_BUFFER_LEN = 512 * 1024;
	/** Usado para la comparacion de archivos. Es creado la primera vez que lo usamos. */
	private static byte[] readBuffer1;
	/** Usado para la comparacion de archivos. Es creado la primera vez que lo usamos. */
	private static byte[] readBuffer2;

	/**
	 * El constructor por omision es privado para evitar que a algun capullo
	 * se le ocurra hacer new FileTools().
	 */
	private FileTools() {
	}

	/**
	 * Escribe 'data' en un archivo.
	 *
	 * @param data     Los datos a escribir
	 * @param filename El nombre del archivo donde escribir
	 * @param force    Indica si hay que sobreescribir un archivo existente
	 *
	 * @throws IOException Si hay problemas
	 */
	public static void writeToFile(final byte[] data, final String filename,
		final boolean force) throws IOException {
		writeToFile(data, 0, data.length, filename, force);
	}

	/**
	 * Escribe 'data' en un archivo.
	 *
	 * @param data     Los datos a escribir
	 * @param offset   Desde donde comenzar
	 * @param length   Cu?nto escribir
	 * @param filename El nombre del archivo donde escribir
	 * @param force    Indica si hay que sobreescribir un archivo existente
	 *
	 * @throws IOException Si hay problemas
	 */
	private static void writeToFile(final byte[] data, final int offset,
		final int length, final String filename, final boolean force)
		throws IOException {
		final File outFile;
		if (filename == null) {
			outFile = File.createTempFile("dat", ".tmp", new File("."));
		} else {
			outFile = new File(filename);
		}

		writeToFile(data, offset, length, outFile, force);
	}

	/**
	 * Escribe 'data' en un archivo.
	 *
	 * @param data    Los datos a escribir
	 * @param outFile El archivo donde escribir
	 * @param force   Indica si hay que sobreescribir un archivo existente
	 *
	 * @throws IOException Si hay problemas
	 * @since 2004.08.19
	 */
	public static void writeToFile(final byte[] data, final File outFile,
		final boolean force) throws IOException {
		writeToFile(data, 0, data.length, outFile, force);
	}

	/**
	 * Escribe 'data' en un archivo.
	 *
	 * @param data    Los datos a escribir
	 * @param offset  Desde donde comenzar
	 * @param length  Cu?nto escribir
	 * @param outFile El archivo donde escribir
	 * @param force   Indica si hay que sobreescribir un archivo existente
	 *
	 * @throws IOException Si hay problemas
	 * @since 2004.08.19
	 */
	public static void writeToFile(final byte[] data, final int offset,
		final int length, final File outFile, final boolean force)
		throws IOException {
		if (!force && outFile.exists()) {
			throw new IOException(outFile + " YA existe");
		}

		try (final FileOutputStream fos = new FileOutputStream(outFile)) {
			if (data != null) {
				fos.write(data, offset, length);
			}
		}
	}

	/**
	 * Lee el contenido de un archivo.
	 *
	 * @param filename El nombre del archivo desde donde leer
	 *
	 * @return Adivina qu?
	 *
	 * @throws IOException Si hay problemas
	 */
	public static byte[] readFromFile(final String filename)
		throws IOException {
		final File inFile = new File(filename);
		return readFromFile(inFile);
	}

	/**
	 * Lee el contenido de un archivo.
	 *
	 * @param inFile El archivo desde donde leer
	 *
	 * @return Adivina qu?
	 *
	 * @throws IOException Si hay problemas
	 * @since 2004.08.19
	 */
	public static byte[] readFromFile(final File inFile) throws IOException {
		final int bytesToRead = (int) inFile.length();
		final byte[] data = new byte[bytesToRead];
		try (final FileInputStream fis = new FileInputStream(inFile)) {
			if (fis.read(data) != bytesToRead) {
				throw new IOException("Leida una cantidad incorrecta de bytes");
			}
		}
		return data;
	}

	public static void writeIntArrayToFile(final int[] pixels,
		final String filename)
		throws IOException {
		try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(filename))) {
			for (final int pixel : pixels) {
				outputStream.write(pixel);
			}
			outputStream.close();
		}
	}

	public static int[] readIntArrayFromFile(final String filename)
		throws IOException {
		final File file = new File(filename);
		try (final DataInputStream inputStream = new DataInputStream(new FileInputStream(file))) {
			final int[] data = new int[(int) file.length() / 4];
			for (int ii = 0; ii < data.length; ii++) {
				data[ii] = inputStream.readInt();
			}
			return data;
		}
	}

	/**
	 * Escribe un objeto Serializable en un archivo.
	 *
	 * @param data     Los datos a escribir
	 * @param filename El archivo donde escribirlo
	 *
	 * @throws IOException Si hay problemas
	 */
	public static void writeObjectToFile(final Serializable data,
		final String filename) throws IOException {
		try (final FileOutputStream fos = new FileOutputStream(filename);
			 final ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(data);
			oos.flush();
			oos.close();
			fos.close();
		}
	}

	/**
	 * Lee un objeto Serializable de un archivo.
	 *
	 * @param filename El archivo de donde leerlo
	 *
	 * @return El objeto leido
	 *
	 * @throws IOException Si hay problemas
	 */
	public static Object readObjectFromFile(final String filename)
		throws IOException {
		Object ret;
		try (final FileInputStream fis = new FileInputStream(filename);
			 final ObjectInputStream ois = new ObjectInputStream(fis)) {
			ret = null;
			try {
				ret = ois.readObject();
			} catch (final ClassNotFoundException ex) {
				ToolBox.showInfo(ex);
			}
		}
		return ret;
	}

	/**
	 * lee una l?nea del archivo de entrada, descartando los comentarios
	 * si hay comentarios o lineas en blanco sigue leyendo hasta que se acabe
	 * lo que se daba.
	 *
	 * @param in de d?nde leer.
	 *
	 * @return la l?nea le?da.
	 *
	 * @throws IOException Si hay problemas
	 * @since 2003.06.21 00:25 cumplea?os de Lizana
	 */
	public static String readLine(final BufferedReader in) throws IOException {
		String line;
		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("#") || line.length() == 0) {
				continue;
			}
			return line;
		}
		return null;
	}

	/**
	 * Returns an array with all files in a directory and all of its
	 * subdirectories.
	 *
	 * @param path the directory whose content we want.
	 *
	 * @return an array with all files in a directory and all of its
	 * subdirectories.
	 *
	 * @since 2005.12.26
	 */
	public static File[] listFiles(final File path) {
		final File[] files = path.listFiles();
		final List<File> content = new ArrayList<File>(files.length);
		for (final File file : files) {
			if (file.isFile()) {
				content.add(file);
			} else if (file.isDirectory()) {
				final File[] children = listFiles(file);
				content.addAll(Arrays.asList(children));
			}
		}
		return content.toArray(NULL_FILE_ARRAY);
	}

	/**
	 * Overwrites a file with random bytes.
	 * ?Sirve esto para algo, escribe en la misma posicion f?sica en el disco?
	 *
	 * @param file the file to overwrite.
	 *
	 * @throws IOException if something goes wrong.
	 */
	public static void overwriteFile(final File file) throws IOException {
		if (!file.exists()) {
			throw new IllegalStateException(file + " doesn't exist");
		}
		final FileAttributes fileAttributes = FileAttributesFactory.newInstance();
		try (final RandomAccessFile fos = new RandomAccessFile(file, "rw")) {
			// Resguardar los atributos del archivo
			fileAttributes.grabAttributes(file);

			long bytesToWrite = file.length();
			final byte[] buffer = new byte[BUFFER_SIZE];
			final Random random = new Random(System.currentTimeMillis());
			random.nextBytes(buffer);

			while (bytesToWrite > 0) {
				final int count = (int) Math.min(BUFFER_SIZE, bytesToWrite);
				fos.write(buffer, 0, count);
				// OJO: modificar si lo saco a un m?todo utilidad, para que conserve todos los
				// atributos del archivo original.
				bytesToWrite -= count;
			}
		} catch (final IOException ex) {
			LOGGER.error("Error accessing: " + file, ex);
			throw ex;
		} finally {
			fileAttributes.applyAttributes(file);
		}
	}

	/**
	 * TODO: Comparar este m?todo y el siguiente. ?Son necesarios ambos?
	 * Obtenemos un archivo que no exista basado en otro archivo.
	 *
	 * @param inFile el archivo existente.
	 * @param suffix el sufijo a ponerle al nuevo archivo.
	 *
	 * @return el nuevo archivo
	 *
	 * @throws IOException si hay algun error de I/O.
	 */
	public static File getNewFile(final File inFile, final String suffix)
		throws IOException {
		String newFilename = inFile.getName() + suffix;
		File newFile = new File(inFile.getParentFile(), newFilename);
		int sequence = 0;
		while (!newFile.createNewFile()) {
			final String fileName = inFile.getName();
			newFilename = new StringBuilder(
				fileName.length() + suffix.length() + 3)
				.append(fileName).append(suffix).append(sequence).toString();
			newFile = new File(inFile.getParentFile(), newFilename);
			sequence++;
		}
		return newFile;
	}

	/**
	 * Obtains a non-existent file in dir, appending a number to the end of the
	 * file name if necessary.
	 *
	 * @param dir  the directory
	 * @param name the name of the file
	 *
	 * @return the file
	 */
	public static File nextAvailableFile(final File dir, final String name) {
		File file = new File(dir, name);
		if (file.exists()) {
			final String fileNameStart, fileNameEnd;
			int counter = -1;
			final int indexOfDot = name.lastIndexOf('.');
			if (indexOfDot == -1) {
				fileNameStart = name;
				fileNameEnd = "";
			} else {
				fileNameStart = name.substring(0, indexOfDot);
				fileNameEnd = name.substring(indexOfDot);
			}
			while (file.exists()) {
				file = new File(dir, new StringBuilder(name.length() + 3)
					.append(fileNameStart).append(counter++)
					.append(fileNameEnd).toString());
			}
		}
		return file;
	}

	/**
	 * Verifica que haya espacio suficiente en el disco.
	 * No se si funciona en filesystems que manejan diferente los sparse files
	 *
	 * @param size      El espacio requerido
	 * @param targetDir El directorio donde se requiere el espacio
	 *
	 * @return true si se dispone de espacio, false de lo contrario
	 *
	 * @since 2001.01.22
	 */
	public static boolean checkForSpace(final long size, final File targetDir) throws IOException {
		final File tmpFile = File.createTempFile("tmp", ".tmp", targetDir);
		try (final RandomAccessFile raf = new RandomAccessFile(tmpFile, "rw")) {
			tmpFile.deleteOnExit();
			raf.seek(size);
			raf.writeByte(0);
			raf.close();
			return true;
		} catch (final IOException ex) {
			LOGGER.error(LoggerFactory.ERROR_TAG, ex);
		} finally {
			if (!tmpFile.delete()) {
				LOGGER.info("No pude borrar el archivo temporal \""
					+ tmpFile.getName() + '"');
			}
		}
		return false;
	}

	/**
	 * Loads properties from a file.
	 *
	 * @param fileName the name of the file from where to load the properties.
	 *
	 * @return the properties. Never returns <code>null</code>.
	 */
	public static Properties loadProperties(final String fileName) {
		final Properties properties = new Properties();
		try {
			final InputStream inputStream = new FileInputStream(fileName);
			properties.load(inputStream);
			inputStream.close();
		} catch (final IOException ex) {
			ToolBox.showInfo(ex);
		}
		return properties;
	}

	/**
	 * Returns a List<File> with the components of this file starting with a root.
	 *
	 * @param targetFile the file whose tree components
	 *
	 * @return a List<File> with the components of this file starting with a root.
	 */
	public static List<File> splitInComponents(final File targetFile) {
		final List<File> components = new ArrayList<>();
		if (targetFile != null) {
			components.add(targetFile);
			final File parentFile = targetFile.getParentFile();
			if (parentFile != null) {
				splitInComponents(parentFile, components);
			}
			Collections.reverse(components);
		}
		return components;
	}

	/**
	 * @param target
	 * @param components
	 */
	private static void splitInComponents(final File target,
		final List<File> components) {
		components.add(target);
		final File parentFile = target.getParentFile();
		if (parentFile != null) {
			splitInComponents(parentFile, components);
		}
	}

	/**
	 * Deletes recursively all empty subdirs (those which does not contain
	 * files) of this directory and optionally this one.
	 *
	 * @param root        the base dir for the cleaning (this will be deleted only
	 *                    if the param <code>deleteRoot</code> is true).
	 * @param deleteFiles if <code>true</code> also delete the plain files.
	 * @param deleteRoot  if <code>true</code> the root dir will be deleted.
	 *
	 * @return the collection of directories deleted.
	 *
	 * @throws NullPointerException if inputDir is <code>null</code> or is not
	 *                              a directory.
	 */
	public static Collection<File> delTree(final File root, final boolean deleteFiles, final boolean deleteRoot) {
		final Collection<File> deletedDirs = new ArrayList<>();
		File[] files = root.listFiles();
		if (files != null) {
			deleteFiles(files, deleteFiles, deletedDirs);
			files = root.listFiles();
			if (deleteRoot && files != null && files.length == 0 && root.delete()) {
				deletedDirs.add(root);
			}
		}
		return deletedDirs;
	}

	/**
	 * Deletes recursively all empty subdirs (those which do not contain
	 * files), and this one.
	 * If the dirs can't be deleted NO message is issued.
	 *
	 * @param root        the root dir for the cleaning.
	 * @param deletedDirs where to place the list of deleted directories.
	 * @param deleteFiles if <code>true</code> also delete the plain files.
	 *
	 * @throws NullPointerException if root or deletedDirs are <code>null</code>
	 *                              or root is not a directory.
	 */
	private static void delTree(final File root,
		final Collection<File> deletedDirs, final boolean deleteFiles) {
		File[] content = root.listFiles();
		if (content != null) {    // This guard is necesary in case the directory
			// is not readable (vg. D:\System Volume Information)
			if (content.length == 0) {
				if (root.delete()) {
					deletedDirs.add(root);
				}
			} else {
				deleteFiles(content, deleteFiles, deletedDirs);
				content = root.listFiles();
				if (content != null && content.length == 0 && root.delete()) {
					deletedDirs.add(root);
				}
			}
		}
	}

	private static void deleteFiles(File[] files2Delete, boolean deleteDirContent, Collection<File> deletedDirs) {
		for (final File file : files2Delete) {
			if (file.isDirectory()) {
				delTree(file, deletedDirs, deleteDirContent);
			} else if (deleteDirContent && file.isFile()) {
				file.delete();
			}
		}
	}

	/**
	 * Escribe el contenido de un archivo en un PrintStream.
	 *
	 * @param filename    nombre del archivo.
	 * @param printStream el sitio donde escribirlo.
	 */
	public static void catFile(final String filename, final PrintStream printStream) {
		try {
			final InputStream stream = FileTools.class
				.getResourceAsStream(filename);
			if (stream == null) {
				printStream.println("No encuentro " + filename);
			} else {
				catStream(stream, printStream);
			}
		} catch (final IOException ex) {
			ToolBox.showInfo(ex);
		}
	}

	/**
	 * Escribe el contenido de un stream en un PrintStream.
	 *
	 * @param stream      el stream.
	 * @param printStream el sitio donde escribirlo.
	 */
	public static void catStream(final InputStream stream, final PrintStream printStream)
		throws IOException {
		try (final BufferedReader in = new BufferedReader(
			new InputStreamReader(stream))) {
			String line;
			while ((line = in.readLine()) != null) {
				printStream.println(line);
			}
		}
	}

	/**
	 * http://www.mkyong.com/java/how-to-copy-directory-in-java/
	 *
	 * @param src
	 * @param dest
	 *
	 * @throws IOException
	 */

	public static void copyFolder(final File src, final File dest)
		throws IOException {
		if (src.isDirectory()) {
			if (!dest.exists() && !dest.mkdirs()) {
				throw new IOException("Unable to create directory structure for: " + dest);
			}

			final String[] list = src.list();
			if (list != null) {
				for (final String file : list) {
					copyFolder(new File(src, file), new File(dest, file));
				}
			}
		} else {
			try (final InputStream in = new FileInputStream(src);
				 final OutputStream out = new FileOutputStream(dest)) {
				final byte[] buffer = new byte[1024];
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			}
			LOGGER.info("File copied from " + src + " to " + dest);
		}
	}

	/**
	 * @param src
	 * @param dest
	 *
	 * @throws IOException
	 */

	public static void copyFile(final File src, final File dest)
		throws IOException {
		LOGGER.info("Going to copy from " + src + " to " + dest);
		final File destDir = dest.getParentFile();
		if (!destDir.exists()) {
			if (!destDir.mkdirs()) {
				throw new IOException(
					"Unable to create directory structure for: " + dest);
			}
		}
		final InputStream in = new FileInputStream(src);
		final OutputStream out = new FileOutputStream(dest);
		final byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.close();
		LOGGER.info("File copied from " + src + " to " + dest);
	}

	/**
	 * Obtiene un archivo con el nombre basado en el nombre de archivo
	 * pasado como par?metro y un numero consecutivo.
	 *
	 * @param file el archivo.
	 *
	 * @return el archivo alternativo.
	 */
	public static File getAlternativeFile(final File file) {
		return getAlternativeFile(file, 0);
	}

	/**
	 * Obtiene un archivo con el nombre basado en el nombre de archivo
	 * pasado como par?metro y un numero consecutivo con tantos ceros iniciales
	 * para tener un tama?o de al menos <code>padSize</code>.
	 *
	 * @param file    el archivo.
	 * @param padSize el ancho m?nimo del n?mero
	 *
	 * @return el archivo alternativo.
	 */
	private static File getAlternativeFile(File file, final int padSize) {
		final String destino = file.getParent();
		final String filename = file.getName();
		int ii = 0;
		while (file.exists()) {
			final int lastIndexOfDot = filename.lastIndexOf('.');
			if (lastIndexOfDot >= 0) {
				final String base = filename.substring(0, lastIndexOfDot);
				final String ext = filename.substring(lastIndexOfDot);
				file = new File(destino,
					base + ToolBox.leftPad(String.valueOf(ii++), padSize, '0') + ext);
			} else {
				file = new File(destino,
					filename + ToolBox.leftPad(String.valueOf(ii++), padSize, '0'));
			}
			LOGGER.debug(String.format("probando con '%s'", file));
		}
		return file;
	}

	/**
	 * Compara dos archivos.
	 *
	 * @param file1 primer archivo a comparar.
	 * @param file2 segundo archivo a comparar.
	 *
	 * @return true si son iguales, false en caso contrario.
	 *
	 * @see #READ_BUFFER_LEN
	 */
	public static boolean compareContents(final File file1, final File file2) {
		if (!file1.exists() || !file2.exists()) {
			LOGGER.warn(String.format("%s o %s no existe.", file1, file2));
			return false;
		}

		if (file1.isDirectory() || file2.isDirectory()) {
			return false;
		}

		if (!file1.isFile() || !file2.isFile()) {
			LOGGER.warn(String
				.format("Comparacion de cosas raras en el proximo 'release': [%s] - [%s]",
					file1.getName(), file2.getName()));
			return false;
		}

		if (file1.equals(file2)) {
			LOGGER.warn(
				"Siendo el mismo archivo, me niego a compararlo consigo mismo. ?Faltar?a mas!");
			return true;
		}

		LOGGER.debug(
			"PCLGTools.compareContents(): Comparando. " + file1.getAbsolutePath() + " y " + file2
				.getAbsolutePath());
		if (file1.length() != file2.length()) {
			LOGGER.debug("Tama?os distintos indican archivos distintos.");
			return false;
		}

		// Suponemos archivos de 500 Kb o menos. Si es as?, ya tenemos preparados los buffers para
		// posteriores llamadas
		if (readBuffer1 == null) {
			readBuffer1 = new byte[READ_BUFFER_LEN];
		}
		if (readBuffer2 == null) {
			readBuffer2 = new byte[READ_BUFFER_LEN];
		}

		try {
//			if (file1.length() > readBuffer1.length || file2.length() > readBuffer2.length) {
//				// ??? no deberia ser necesario que sean del mismo tama?o
//				readBuffer1 = new byte[(int) file1.length()];
//				readBuffer2 = new byte[(int) file2.length()];	// file2.length() == file1.length() por la comprobacion anterior
//			}
			return doTheComparation(file1, file2);
		} catch (final OutOfMemoryError ex) {
			readBuffer1 = null;    // Liberamos la memoria
			readBuffer2 = null;    // Liberamos la memoria
			System.err.println(
				"\tERROR\n\t" + file1.getAbsolutePath() + "\no\t" + file2.getAbsolutePath());
			System.err.println(ex);
			// Como el problema fue falta de memoria
			System.err.println("Voy a ntentar con un buffer menor");
			readBuffer1 = new byte[READ_BUFFER_LEN];    // Este tama?o no deber?a dar errores
			readBuffer2 = new byte[READ_BUFFER_LEN];    // Este tama?o no deber?a dar errores
			return doTheComparation(file1, file2);
		}
	}

	/**
	 * Compara dos archivos es
	 * un metodo privado que presume que los tama?os de los archivos son
	 * iguales
	 *
	 * @param arch1 primer archivo a comparar.
	 * @param arch2 segundo archivo a comparar.
	 *
	 * @return true si son iguales, false en caso contrario
	 *
	 * @see #compareContents
	 * @since 2002.03.08  Dia Internacional de la Mujer
	 * Trabajadora (o noche, porque son las 22.52 y es viernes. ?Qu? co?o
	 * hago yo aqu??)
	 */
	private static boolean doTheComparation(final File arch1, final File arch2) {
		try (final BufferedInputStream fis1 = new BufferedInputStream(new FileInputStream(arch1));
			 final BufferedInputStream fis2 = new BufferedInputStream(new FileInputStream(arch2))) {
			// Leemos los toletes completos con la esperanza de que la
			// memoria es barata (10$/Mb)

			int pos = 0;
			while (true) {
				final int leidos1 = fis1.read(readBuffer1);
				final int leidos2 = fis2.read(readBuffer2);

				if (leidos1 != leidos2) {
					LOGGER.warn("No pude leer la misma cantidad de los dos archivos");
					return false;
				}

				if (leidos1 == -1) {    // Llegamos al final de los dos ficheros sin problemas
					break;
				}

				// Comparamos byte a byte (o sea pelo a pelo)
				for (int ii = 0; ii < leidos1; ii++) {
					pos++;
					if (readBuffer1[ii] != readBuffer2[ii]) {
						LOGGER.debug(String.format("Diferencia en el byte %d; no sigo comparando.", pos));
						return false;
					}
				}
			}
			return true; // Si llegue aqui es que son iguales
		} catch (final IOException ex) {
			ToolBox.showInfo(ex);
			return false; // Tronamos: asumimos que son diferentes
		}
	}

	public static String sanitizeWindowsFilename(final String filename, final String replacement) {
		return filename.replaceAll(WINDOWS_INVALID_FILENAME_CHARACTERS_REGEX, replacement);
	}

	/**
	 * Converts the contents of a file
	 *
	 * @param inFile the file to convert.
	 * @param processor a function to be applied to the file; this function returns the byte[] that will be written
	 *                  in the file as result.
	 * @throws IOException if something goes awry.
	 */
    public static void convertFileInPlace(final File inFile, final BiConsumer<File, File> processor) throws IOException {
		LOGGER.debug("Processing: " + inFile.getAbsolutePath());
		final FileAttributes fileAttributes = FileAttributesFactory.newInstance();
		fileAttributes.grabAttributes(inFile);
		final File outFile = File.createTempFile("converter", ".file", inFile.getParentFile());

		try {
			processor.accept(inFile, outFile);
		} catch (final Exception ex) {
			LOGGER.error("No pude convertir. trato de eliminar " + emphasize(outFile.getName()));
			if (outFile.delete()) {
				LOGGER.error("  Hecho");
			} else {
				LOGGER.error("  No pude");
			}
			throw ex;
		}

		if (outFile.exists()) {
			fileAttributes.applyAttributes(outFile);
			// Dar el cambiazo
			if (!inFile.delete()) {
				LOGGER.error("No puedo eliminar " + emphasize(inFile.getName()));
			}
			if (!outFile.renameTo(inFile)) {
				LOGGER.error("No puedo renombrar " + emphasize(outFile.getName()));
			}
		}
		LOGGER.debug("Done!");
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

    public static class SplittedName {
		public String base;
		public String extension;
	}

	public static SplittedName splittName(final File file) {
		return splittName(file.getName());
	}

	/**
	 * Gets the base and extension of a file name.
	 */
	public static SplittedName splittName(final String name) {
		final SplittedName splittedName = new SplittedName();
		final int index = name.lastIndexOf('.');
		if (index > 0 && index < name.length() - 1) {
			splittedName.base = name.substring(0, index);
			splittedName.extension = name.substring(index + 1).toLowerCase();
		} else {
			splittedName.base = name;
			splittedName.extension = "";
		}
		return splittedName;
	}
}