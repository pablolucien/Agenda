package org.pclg.filesystem.fileattributes;

/**
 * A factorry to obtain the FileAttributes object best suited depending
 * on the file system and operating system.
 * @author El Coyote Cojo
 * @version 1.0
 * @since 25-nov-2004
 */
public final class FileAttributesFactory {
    /**
     * Avoids instantiation.
     */
    private FileAttributesFactory() {
    }

    /**
     * Returns the FileAttributes object best suited depending
     * on the file system and operating system.
     * @return a FileAttributes object.
     */
    public static FileAttributes newInstance() {
        return new Java7FileAttributes();
    }

//    public static void main(String[] args) {
//        System.err.println(System.getProperty("os.name"));
//    }
}
