package org.pclg.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * A directory queue to store the browsing history between them.
 * @author El Coyote Cojo
 * @since 2015.06.09
 */
public final class DirQueue {
	private final List<File> queue;
	private int cwdPointer = -1;

	/**
	 * Crea una cola de acuerdo a los datos de properties posicionada en el directorio correcto.
	 */
	public DirQueue(final Properties properties) {
		final String canonicalName = getClass().getCanonicalName();
		final int queueSize = Integer.parseInt(
			properties.getProperty(canonicalName + ".queueSize", "0"));
        queue = new ArrayList<>(queueSize);
		for (int ii = 0; ii < queueSize; ii++) {
			final String dir = properties
				.getProperty(canonicalName + ".dir" + ii);
			if (!isEmptyOrBlank(dir)) {
				queue.add(new File(dir));
			}
		}
		cwdPointer = Math.min(
            Integer.parseInt(properties.getProperty(canonicalName + ".cwdPointer", "-1")),
            queue.size() - 1);
	}

	/**
	 * @return the size of the queue.
	 */
	public int size() {
		return queue.size();
	}

    /**
   	 * @return the current dir if any.
   	 */
   	public File cwd() {
   		return cwdPointer >= 0 ? queue.get(cwdPointer) : null;
   	}


	/**
	 * @return It moves one position forward if there is one and returns the directory that is in that position.
	 */
	public File fwd() {
		if (cwdPointer < queue.size() - 1) {
			cwdPointer++;
		}
		return queue.get(cwdPointer);
	}

	/**
	 * It moves one position forward in the queue.
	 * If there is a directory in that position and it is different from the one passed as a parameter,
	 * replaces it and removes everything from there on.
	 * If there is nothing adds it.
	 * If it is the same, it does not make changes.
	 *
	 * @return  the directory that is in that position.
	 */
	public File fwd(final File newDir) {
		cwdPointer++;
		final int size = queue.size();
		final File absoluteDir = newDir.getAbsoluteFile();
		if (size > cwdPointer) {
			final File currentDir = queue.get(cwdPointer);
			if (currentDir.equals(absoluteDir)) {
				return currentDir;
			} else {
				queue.set(cwdPointer, absoluteDir);
				for (int ii = size - 1; ii > cwdPointer; ii--) {
					queue.remove(ii);
				}
			}
		} else {
			queue.add(cwdPointer, absoluteDir);
		}
		return queue.get(cwdPointer);
	}

	/**
	 * @return Se desplaza una posici�n hacia atr�s si la hay y devuelve el 
	 * directiorio que est� en esa posicion.
	 */
	public File back() {
		if (cwdPointer > 0) {
			cwdPointer--;
		}
		return queue.get(cwdPointer);
	}

	/**
	 * @return true if it is possible to navigate back.
	 */
	public boolean canGoBack() {
		return cwdPointer > 0;
	}

	/**
	 * @return true if it is possible to navigate forward.
	 */
	public boolean canGoForth() {
		return cwdPointer < queue.size() - 1;
	}

	/**
	 * @return el directorio adonde iria si se llama a back().
	 */
	public File peekPreviousDir() {
		return cwdPointer > 0 ? queue.get(cwdPointer - 1) : null;
	}

	/**
	 * @return el directorio adonde iria si se llama a fwd().
	 */
	public File peekNextDir() {
		return cwdPointer < queue.size() - 1 ? queue.get(cwdPointer + 1) : null;
	}

	@Override
	public String toString() {
		return "DirQueue [queue=" + queue + ", cwdPointer=" + cwdPointer + ']';
	}

	public void saveProperties(final Properties customProps) {
		if (customProps != null) {
			final String canonicalName = getClass().getCanonicalName();
			final int size = queue.size();
			customProps.setProperty(canonicalName + ".queueSize", String.valueOf(size));
			customProps.setProperty(canonicalName + ".cwdPointer", String.valueOf(cwdPointer));
			for (int ii = 0; ii < size; ii++) {
				customProps.setProperty(canonicalName + ".dir" + ii, resolvePath(queue.get(ii)));
            }
		}
	}

	private static String resolvePath(final File file) {
		try {
			return file.getCanonicalPath();
		} catch (final Exception ex) {
			return file.getAbsolutePath();
		}
	}
}