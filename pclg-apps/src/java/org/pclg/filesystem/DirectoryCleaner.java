package org.pclg.filesystem;

import org.pclg.tools.Dir;
import org.pclg.tools.PrintlnTarget;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * A class used primarily to delete a set of directories. (An OO wrapper to a
 * procedural solution).
 * @author pablo
 * @version $Revision: 1.5 $
 * @since 29-nov-2004
 */
public final class DirectoryCleaner {
	/**
	 * Avoids instantiation.
	 */
	private DirectoryCleaner() {
	}

	/**
	 * Borra los directorios vacíos.
	 *
     * @param dirs Un ArrayList compuesto con objetos File que representan
     *             directorios.
	 * @param output the area where to print messages.
	 * @return the list of directories really deleted.
	 */
	public static List<File> cleanDirs(final Collection<File> dirs, final PrintlnTarget output) {
		inform(output, "\tLimpiando directorios " + new Date());
		boolean borradoAlguno;
		int nrDels = 0;
		final int initialDirsSize = dirs.size();
		final List<File> deletedDirs = new ArrayList<>(initialDirsSize);

		do {
			borradoAlguno = false;
            for (final Iterator<File> iterator = dirs.iterator(); iterator.hasNext();) {
				final File dir = iterator.next();
				if (dir == null) {
					continue;
				}

				if (!dir.isDirectory()) {
					inform(output, dir.getAbsolutePath() + " NO es un directorio!!");
					continue;
				}

				// 2007.04.30 Si no hay permisos de lectura (vg. D:\System Volume Information
				// list() es null
				final String[] list = dir.list();
                if (list == null || list.length != 0) {		// que pasa con . y .. en Unix???
					// NO esta vacio!!, ni siquiera lo intento
					continue;
				}

				if (dir.delete()) {
					deletedDirs.add(dir);
					inform(output, "\tBorrado " + dir.getAbsolutePath());
					borradoAlguno = true;
					nrDels++;
                    iterator.remove();	// No considerarlo en subsiguientes veces
				}
			}
		} while (borradoAlguno);

		inform(output, "\tFin de la limpieza " + new Date());
		inform(output, "\tBorrados " + nrDels + " directorios de " + initialDirsSize);
		return deletedDirs;
	}

	/**
	 * Writes a message to a CompDel.Output object.
     * @param output  where to print messages.
     * @param msg the message to write.
	 */
	private static void inform(final PrintlnTarget output, final String msg) {
		if (output != null) {
			output.println(msg);
		}
	}

	/**
	 * Deletes recursively all empty subdirs (those which does not contain files).
	 *
	 * @param root  the base dir for the cleaning (this will be deleted only if the
	 *              param <code>deleteRoot</code> is true).
	 * @param deleteRoot if <code>true</code> the root dir will be deleted.
	 * @return the number of directories deleted.
	 * @throws NullPointerException if inputDir is <code>null</code> or is not a directory.
	 */
	static int cleanDirs(final File root, final boolean deleteRoot) {
		int count = 0;
		for (final File file : root.listFiles()) {
			if (file.isDirectory()) {
				count += delTree(file);
			}
		}
		if (deleteRoot && root.listFiles().length == 0 && root.delete()) {
			count++;
		}
		return count;
	}

	/**
	 * Deletes recursively all empty subdirs (those which do not contain files), and this one.
	 * If the dirs can't be deleted NO message is issued.
	 *
	 * @param root the root dir for the cleaning.
	 * @return the number of directories deleted.
	 * @throws NullPointerException if root is <code>null</code> or is not a directory.
	 */
	private static int delTree(final File root) {
		int count = 0;
		final File[] content = root.listFiles();
		if (content.length == 0) {
			if (root.delete()) {
				count++;
			}
		} else {
			for (final File file : content) {
				if (file.isDirectory()) {
					count += delTree(file);
				}
			}
			if (root.listFiles().length == 0 && root.delete()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Deletes the directory trees passed with their files.
	 * @param dirs the directories to be deleted.
	 */
	public static void delTreesWithFiles(final List<File> dirs) {
		final Dir dirLister = new Dir(Dir.NULL_EXCLUDE_LIST);
		dirs.stream().filter(File::exists)
			.forEach(dir -> {
				try {
					dirLister.listarArchivos(dir, true, false).stream()
						.filter(File::isFile).forEach(file -> {
						try {
							Files.delete(file.toPath());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				cleanDirs(dir, true);
			});

	}
}
