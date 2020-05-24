// ******************************** package
package org.pclg.tools;

// ******************************** imports
/**
 Dir.java
 */

// 1. Que es mas costoso en java: ¨ un String[]  o un File[] ?
// 2. recursivo por parametros
// 3. solo directorios o archivos por parametros

import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lista todos los archivos de un arbol de directorios
 */
public class Dir {

	public static final File[] EMPTY_FILE_ARRAY = new File[0];

	public enum AcceptableType {
		FILE, DIRECTORY, BOTH
	}

	/** Logger for this class. */
 	private static final Logger LOGGER = LoggerFactory.make();

	public static final String[] NULL_EXCLUDE_LIST = null;	// FIXME: Haciendo esta chapucilla para no tener que cambiar de momento a todos los clientes que usan este constructor

    /**
     * directorios que excluimos de la busqueda
     */
    private final String[] excludeList;

    /**
     * Un filtro para seleccionar los archivos que van y los que no
     */
    private final FileFilter listFilter;


    /**
     * @param excludeList Lista de los directorios que no se consideran en la busqueda (puede ser 'null')
     */
    public Dir(final String[] excludeList) {
        this(excludeList, null);
    }

    /**
     * @param excludeList Lista de los directorios que no se consideran en la busqueda (puede ser 'null')
     * @param listFilter  Un filtro para determinar los archivos que son reportados (si es 'null' todos lo son)
     */
	private Dir(final String[] excludeList, final FilenameFilter listFilter) {
        this.excludeList = excludeList;
		// FIXME: Haciendo esta chapucilla para no tener que cambiar de momento a todos los clientes que usan este constructor
		if (listFilter == null) {
			this.listFilter = null;
		} else {
			this.listFilter = pathname -> listFilter.accept(pathname.getParentFile(), pathname.getName());
		}
	}

	public Dir(final FileFilter listFilter) {
		excludeList = NULL_EXCLUDE_LIST;
		this.listFilter = listFilter;
	}


	/**
  * Devuelve los archivos contenidos en el directorio
  * y en sus subdirectorios
  * da informacion extra, como el tiempo que tardó
  * OJO: Recursivo; hasta donde el stack aguante
  *
  * @param cwd     es el directorio a listar
  * @param recurse Determina si lo hacemos recursivo o no
  * @return Un File[] con el contenido del directorio
  *         --author     El Zorro
  *         20000907    cambiado de String[] a File[]
  * @throws IOException si el parametro no es un directorio o pasa alguna otra cosa
  */
 public List<File> listarArchivos(final File cwd, final boolean recurse) throws IOException {
     return listarArchivos(cwd, recurse, true);
 }

	/**
	 * Devuelve los archivos contenidos en el directorio
	 * y en sus subdirectorios
	 * OJO: Recursivo; hasta donde el stack aguante
	 *
	 * @param cwd     es el directorio a listar
	 * @param verbose si es 'true' da informacion extra, como el tiempo que tardó
	 * @param recurse Determina si lo hacemos recursivo o no
	 * @return Un File[] con el contenido del directorio
	 *         --author     El Zorro
	 *         20000907    cambiado de String[] a File[]
	 * @throws IOException si el parametro no es un directorio o pasa alguna otra cosa
	 */
	public List<File> listarArchivos(final File cwd, final boolean recurse, final boolean verbose)
		    throws IOException {
		return listarArchivos(cwd, recurse, verbose, AcceptableType.BOTH);
	}

	public List<File> listarArchivos(final File cwd, final boolean recurse, final boolean verbose, final AcceptableType acceptableType)
		    throws IOException {
		// Si está en la lista negra devuelvo nadita
		if (excludeList != null) {
			for (final String anExcludeList : excludeList) {
				if (cwd.getAbsolutePath().equalsIgnoreCase(anExcludeList)) {
					LOGGER.warning("Dir: " + cwd.getAbsolutePath() + " está en la 'excludeList'");
					return Collections.emptyList();
				}
			}
		}

		int cronHandle = -1;
		if (verbose) {
			cronHandle = Chrono.getChrono();
			Chrono.start(cronHandle);
		}


		if (!cwd.isDirectory()) {   // Vamos mal
			throw new IOException(cwd.toString() + " No es un directorio");
		}

		final File[] cwdContents;
		// tomamos el contenido
		if (listFilter == null) {
			cwdContents = cwd.listFiles();
		} else {
			cwdContents = cwd.listFiles(listFilter);
		}

		// 2006.06.27: Descubriendo que E:\System Volume Information devuelve null
		if (cwdContents == null) {
			return Collections.emptyList();
		}

		Arrays.sort(cwdContents);

		final List<File> fileList = new ArrayList<>(cwdContents.length);
		int numSubdirs = 0;
		int numFiles = 0;
		for (final File file : cwdContents) {
			switch (acceptableType) {
			case FILE:
				if (file.isFile()) {
					fileList.add(file.getCanonicalFile());
				}
				break;
			case DIRECTORY:
				if (file.isDirectory()) {
					fileList.add(file.getCanonicalFile());
				}
				break;
			case BOTH:
				fileList.add(file.getCanonicalFile());
			}

			if (!file.isDirectory()) {
				numFiles++;
				continue;
			}
			numSubdirs++;

			if (!recurse) {
				continue;
			}

			fileList.addAll(listarArchivos(file, recurse, verbose, acceptableType));
		}

		if (verbose) {
			Chrono.mark(cronHandle);
			LOGGER.info("listarArchivos(dir = " + cwd
				+ ", verbose = " + verbose + ", recurse = " + recurse + "): "
				+ Chrono.timeDetail(Chrono.elapsed(cronHandle)) + " "
				+ numSubdirs + " directorios, "
				+ numFiles + " archivos");
		}
		return fileList;
	}

    /**
     * @param args: arg[0] es el directorio base. Si no es especificado se asume cwd
     */
    public static void main(final String[] args) {
        try {
            final String dirName = args.length == 0 ? "." : args[0];
            final File dirF = new File(dirName);
			LOGGER.warning("Lista de archivos en: " + dirF.getAbsolutePath());

            final int cron1 = Chrono.getChrono();
            final Dir dir = new Dir(null, null);
            final List<File> archivo = dir.listarArchivos(dirF, true, false);
            Chrono.mark(cron1);
            LOGGER.info(Chrono.timeDetail(Chrono.elapsed(cron1)));

			for (final File anArchivo : archivo) {
				LOGGER.info(anArchivo.getAbsolutePath() + "\t\t"
						+ anArchivo.length() + "\t\t"
						+ new Date(anArchivo.lastModified()));
			}
			LOGGER.info("Fin del listado");
            Chrono.mark(cron1);
			LOGGER.info(Chrono.timeDetail(Chrono.elapsed(cron1)));
        }
        catch (final IOException ex) {
			LOGGER.log(Level.SEVERE,
				"Si pasas el nombre de un directorio es mas de pinga: ", ex);
        }
    }
}
