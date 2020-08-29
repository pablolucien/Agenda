package org.pclg.xtras;

import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * I've seen a lot of forum posts about how to modify the classpath at runtime
 * and a lot of answers saying it can't be done. I needed to add JDBC driver
 * JARs at runtime so I figured out the following method.
 *
 * The system classloader (ClassLoader.getSystemClassLoader()) is a subclass of
 * URLClassLoader. It can therefore be casted into a URLClassLoader and used as
 * one.
 *
 * URLClassLoader has a protected method addURL(URL url), which you can use to
 * add files, jars, web addresses - any valid URL in fact.
 *
 * Since the method is protected you need to use reflection to invoke it.
 *
 * Here's some code for a class which adds a File or URL to the classpath:
 *
 * @author antony_miguel
 *         (http://forum.java.sun.com/thread.jspa?forumID=32&threadID=300557)
 * @since 17-Sep-2002
 */
public class ClassPathHacker {
    // No puedo usar LoggerFactory.makeLog4J() porque el jar de log4j tal vez
    // no est� todav�a en el classpath.
    private static final Logger LOGGER = LoggerFactory.make();
    private static final Class<?>[] parameters = { URL.class };
    private static final FileFilter ARCHIVE_FILE_FILTER = file -> {
        final String name = file.getName().toLowerCase();
        return name.endsWith(".jar") || name.endsWith(".zip");
    };

    private ClassPathHacker() {
	}

	/**
	 * Adds files to the classpath.
	 * @param filenames the names of the files to add separated by '|'.
	 * @throws IOException if something wrong happens.
	 */
    public static void addFiles(final String filenames) throws IOException {
	   if (filenames != null && filenames.length() > 0) {
		   addFiles(filenames.split("\\|"));
	   }
    }

	/**
	 * Adds files to the classpath.
	 * @param filenames the names of the files to add in a String[].
	 * @throws IOException if something wrong happens.
	 */
    public static void addFiles(final String[] filenames) throws IOException {
		for (final String filename : filenames) {
			addFile(new File(filename));
		}
    }

	/**
	 * Adds a file to the classpath.
	 * @param filename the name of the file to add.
	 * @throws IOException if something wrong happens.
	 */
    public static void addFile(final String filename) throws IOException {
        addFile(new File(filename));
    }

    private static void addFile(final File file) throws IOException {
		LOGGER.info("ClassPathHacker: Adding " + file);
		if (!file.exists()) {
			throw new FileNotFoundException(file.toString());
		}
        addURL(file.toURI().toURL());
        if (file.isDirectory()) {
            for (final File child : file.listFiles(ARCHIVE_FILE_FILTER)) {
                addURL(child.toURI().toURL());
            }
        }
    }

    private static void addURL(final URL url) throws IOException {
		final URL[] urls = getClassPath();

		if (Arrays.asList(urls).contains(url)) {
			LOGGER.info(
				url + " is already in the classpath; will not be added.");
			return;
		}

        final Class<?> sysclass = URLClassLoader.class;

        try {
			final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
			if (systemClassLoader instanceof URLClassLoader) {
				final Method method = sysclass.getDeclaredMethod("addURL", parameters);
				method.setAccessible(true);
				final URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
				method.invoke(sysloader, url);
			}
        } catch (final Throwable t) {
            LOGGER.log(Level.SEVERE, "ClassPathHacker: Error adding: " + url, t);
            throw new IOException(
                "Error, could not add URL to system classloader", t);
        }
    }

	public static URL[] getClassPath() {
		final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		if (systemClassLoader instanceof URLClassLoader) {
			return ((URLClassLoader) systemClassLoader).getURLs();
		}
		return new URL[0];
	}
}
