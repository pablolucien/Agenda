package org.pclg.filesystem.fileattributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @since 29/12/2017.
 */
public class Java7FileAttributes implements FileAttributes {
    private static final long serialVersionUID = -8592617160400379613L;
    private Map<String, Object> attributes;

    @Override
    public void grabAttributes(final File file) {
        try {
            attributes = Files.readAttributes(file.toPath(), "*");
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    // attributes = {
    // lastAccessTime=2017-12-29T10:27:19.0545Z,
    // lastModifiedTime=2017-12-29T10:27:19.0545Z,
    // size=0,
    // creationTime=2017-12-29T10:27:19.0545Z,
    // isSymbolicLink=false,
    // isRegularFile=true,
    // fileKey=null,
    // isOther=false,
    // isDirectory=false
    // }

    @Override
    public void applyAttributes(final File file) {
        final Path path = file.toPath();
        for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
            try {
                final String key = entry.getKey();
                if (key.equals("size") || key.equals("isSymbolicLink") || key.equals("isRegularFile") || key.equals("fileKey") || key.equals("isOther") || key.equals("isDirectory")) {
                    continue;
                }
                Files.setAttribute(path, key, entry.getValue());
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
