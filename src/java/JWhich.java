
import java.net.URL;

/**
 * Prints the absolute pathname of the class file containing the specified class
 * name, as prescribed by the current classpath.
 * @author Unknown
 * @version Unknown
 */
public final class JWhich {
    /** a dot. */
    private static final char DOT = '.';

    /** a slash. */
    private static final char SLASH = '/';

    /** The usage message. */
    private static final String USAGE_MSG = "Usage: java JWhich <classname, ...>";

    /**
     * Avoid instantiation.
     */
    private JWhich() {
    }

    /**
     * Prints the absolute pathname of the class file containing the specified
     * class name, as prescribed by the current classpath.
     *
     * @param className Name of the class.
     */
    private static void which(String className) {

        if (className.charAt(0) != SLASH) {
            className = SLASH + className;
        }
        className = className.replace(DOT, SLASH);
        className += ".class";

        final URL classUrl = JWhich.class.getResource(className);

        if (classUrl != null) {
            System.out.println("\nClass '" + className + "' found in \n'"
                    + classUrl.getFile() + '\'');
        } else {
            System.out.println("\nClass '" + className + "' not found in \n'"
                    + System.getProperty("java.class.path") + '\'');
        }
    }

    /**
     * Entry point.
     * @param args the names of the classes to find.
     */
    public static void main(final String[] args) {
        if (args.length > 0) {
            for (int ii = 0; ii < args.length; ii++) {
                which(args[ii]);
            }
        } else {
            System.err.println(USAGE_MSG);
        }
    }
}
