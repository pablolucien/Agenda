package org.pclg.xtras;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Along the same lines as:
 * 
 * http://forum.java.sun.com/thread.jspa?forumID=32&threadID=300557
 * 
 * I recently wrote some code to modify the java library path at runtime to
 * allow dynamic loading of DLLs or SOs in directories other than those
 * specified in the initial -Djava.library.path="...".
 * 
 * I've seen a few posts regarding this matter so I thought it would be useful
 * to post the code.
 * 
 * The code modifies the static String array which the property
 * java.library.path is initially read into. This string array is then used to
 * check paths when libraries are loaded.
 * 
 * Please note that this code uses some internal knowledge of non-public classes
 * so although it works on the Sun JVMs I have tested it on I can't guarantee
 * that it will work on other JVMs (in such cases it should throw an
 * IOException).
 * 
 * Note also that the changes will not show up if you query 'java.library.path'
 * (but it would be trivial to add code to do a setProperty() when a new path is
 * added).
 * 
 * @author antony_miguel (http://forum.java.sun.com/thread.jspa?threadID=707176)
 * @since 10-Feb-2006
 */
public class LibPathHacker {
	public static void addDir(final String s) throws IOException {
		try {
            final Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
            final String[] paths = (String[]) field.get(null);
            for (final String path : paths) {
                if (s.equals(path)) {
                    return;
                }
            }
			final String[] tmp = new String[paths.length+1];
			System.arraycopy(paths,0,tmp,0,paths.length);
			tmp[paths.length] = s;
			field.set(null,tmp);
	        System.setProperty("java.library.path", 
	                System.getProperty("java.library.path") 
	                	+ File.pathSeparator + s);
		} catch (final IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path");
		} catch (final NoSuchFieldException e) {
			throw new IOException("Failed to get field handle to set library path");
		}
	}
}
