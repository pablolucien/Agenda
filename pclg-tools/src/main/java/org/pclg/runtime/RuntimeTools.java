package org.pclg.runtime;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * @author El Coyote Cojo
 * @since 14/08/20 17:54
 */
public final class RuntimeTools {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    private RuntimeTools() {
    }

    /**
     * Finds where the application is executing (a directory or a jar):
     */
    public static File getExecutionPath(final Class<?> targetClass) {
        final String myName = targetClass.getSimpleName();
        final String packageName = targetClass.getPackage().getName();

        final URL appURL = targetClass.getResource(myName + ".class");
        if (appURL == null) {
            LOGGER.error("Couldn't find the execution point");
            return null;
        }

        final String path = appURL.getPath();
        int end = path.lastIndexOf('!');        // If it's a jar, it ends with ! and the name of the class
        if (end == -1) {
            end = path.lastIndexOf('/');        // If it's a directory, it ends with / and the name of the class
        }
        final String fullDir = path.substring(path.indexOf('/') + 1, end);
        final String baseDir = fullDir.substring(0, fullDir.indexOf(packageName.replace('.', '/')));
        return new File(baseDir);
    }
}
