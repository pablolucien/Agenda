package org.pclg.filesystem.fileattributes;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.pclg.Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;

/**
 * Copia los atributos de unos Files en un directorio a los Files de otro
 * directorio que tengan los mismos nombres.
 *
 * @author El Coyote Cojo
 */
public class CopyAttributes {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	private CopyAttributes() {
	}

	public static void main(final String[] args) throws IOException {
		if (args.length != 2) {
			usage();
			System.exit(THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING);
		}
		if (args[0].equals("-undo")) {
			restoreAttributes(new File(args[1]));
		} else {
			copyAttributes(new File(args[0]), new File(args[1]));
		}
	}

	public static void usage() {
		final String className = CopyAttributes.class.getSimpleName();
		LOGGER.warn(String.format("Uso: %s <targetDir> <sourceDir> | %s -undo <undoFile>.", className, className));
	}

	/**
	 * Copia los atributos de los archivos que est�n en sourceDir a los archivos
	 * de targetDir que tengan los mismos nombres.
	 *
	 * @param targetDir El directorio or�gen.
	 * @param sourceDir El directorio destino.
	 *
	 * @return the undo file.
	 */
	static File copyAttributes(final File targetDir, final File sourceDir) throws IOException {
		LOGGER.info(targetDir);
		if (!targetDir.isDirectory()) {
			throw new IllegalArgumentException(String.format("targetDir <%s> no es un directorio.", targetDir));
		}
		if (!sourceDir.isDirectory()) {
			throw new IllegalArgumentException(String.format("sourceDir <%s> no es un directorio.", sourceDir));
		}
		final File[] files = targetDir.listFiles();
		if (files != null) {
			final Map<File, FileAttributes> modifications = new HashMap<>(files.length);
			for (final File targetFile : files) {
				final File sourceFile = new File(sourceDir, targetFile.getName());
				if (!sourceFile.exists()) {
					LOGGER.warn(String.format("%s no existe: no hago n� de n�.", sourceFile));
					continue;
				}
				final FileAttributes tgtAttributes = FileAttributesFactory.newInstance();
				tgtAttributes.grabAttributes(targetFile);
				modifications.put(targetFile, tgtAttributes);

				final FileAttributes srcAttributes = FileAttributesFactory.newInstance();
				srcAttributes.grabAttributes(sourceFile);
				srcAttributes.applyAttributes(targetFile);
			}
			final File tempFile = File.createTempFile(CopyAttributes.class.getSimpleName(), ".ser");
			LOGGER.warn(String.format("Almacenando la informaci�n de undo en %s.", tempFile));
			try (final ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(tempFile))) {
				stream.writeObject(modifications);
			} catch (final IOException ex) {
				LOGGER.error(ex);	// No hago nada ?!?!?!?!?!?!
			}
			return tempFile;
		}
		return null;
	}

	private static void restoreAttributes(final File serialFile) {
		LOGGER.info(serialFile);
		try (final ObjectInputStream stream = new ObjectInputStream(new FileInputStream(serialFile))) {
			@SuppressWarnings("unchecked")
			final Map<File, FileAttributes> mods = (Map<File, FileAttributes>) stream.readObject();
			for (final Entry<File, FileAttributes> entry : mods.entrySet()) {
				LOGGER.warn(entry.getKey());
				entry.getValue().applyAttributes(entry.getKey());
			}
		} catch (IOException | ClassNotFoundException ex) {
			LOGGER.error(ex);
		}
	}
}
