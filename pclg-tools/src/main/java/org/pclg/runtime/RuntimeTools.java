package org.pclg.runtime;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
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
    public static File getExecutionPath(final Class<?> targetClass) throws IOException {
        final String simpleClassName = targetClass.getSimpleName();
        final String fullClassName = targetClass.getName();

        final URL appURL = targetClass.getResource(simpleClassName + ".class");
        if (appURL == null) {
            LOGGER.error("Couldn't find the execution point");
            return null;
        }
        final String protocol = appURL.getProtocol();
        final String path;
        final String baseDir;
        if (protocol.equals("jar")) {
            baseDir = ((JarURLConnection) appURL.openConnection()).getJarFile().getName();
        } else {
            path = appURL.getPath();
            baseDir = path.substring(0, path.indexOf(fullClassName.replace('.', '/')));
        }
        return new File(baseDir);
    }
}
