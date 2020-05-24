package org.pclg.filesystem.fileattributes;

import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Map;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * @since 29/12/2017.
 */
public class CopyAttributesTest {
    @Test
    public void main() throws Exception {
        final FileSystem fileSystem = FileSystems.getDefault();
        final String baseDir = "/tmp";
        final Path sourceDir = fileSystem.getPath(baseDir, "source");
        final Path targetDir = fileSystem.getPath(baseDir, "target");
        Files.createDirectories(sourceDir).toFile().deleteOnExit();
        Files.createDirectories(targetDir).toFile().deleteOnExit();
        final int count = 10;
        final Path[] sourceFiles = new Path[count];
        final Path[] targetFiles = new Path[count];

        for (int ii = 0; ii < count; ii++) {
            sourceFiles[ii] = fileSystem.getPath(sourceDir.toString(), "File" + ii);
            sourceFiles[ii].toFile().deleteOnExit();
            targetFiles[ii] = fileSystem.getPath(targetDir.toString(), "File" + ii);
            targetFiles[ii].toFile().deleteOnExit();
            if (!Files.exists(sourceFiles[ii])) {
                Files.createFile(sourceFiles[ii]);
            }
            Files.copy(sourceFiles[ii], targetFiles[ii], REPLACE_EXISTING, COPY_ATTRIBUTES);
            final Map<String, Object> sourceAttributes = Files.readAttributes(sourceFiles[ii], "lastModifiedTime");
            Map<String, Object> targetAttributes = Files.readAttributes(targetFiles[ii], "lastModifiedTime");
            assertEquals(sourceAttributes, targetAttributes);

            Files.setLastModifiedTime(targetFiles[ii], FileTime.fromMillis(1000L * ii));
            targetAttributes = Files.readAttributes(targetFiles[ii], "lastModifiedTime");
            assertNotEquals(targetAttributes, sourceAttributes);
        }

        final File undoFile = CopyAttributes.copyAttributes(targetDir.toFile(), sourceDir.toFile());
		undoFile.deleteOnExit();

        for (int ii = 0; ii < count; ii++) {
            final Map<String, Object> sourceAttributes = Files.readAttributes(sourceFiles[ii], "lastModifiedTime");
            final Map<String, Object> targetAttributes = Files.readAttributes(targetFiles[ii], "lastModifiedTime");
            assertEquals(sourceAttributes, targetAttributes, "File" + ii);
        }

        CopyAttributes.main(new String[] {"-undo", undoFile.getAbsolutePath()});

        for (int ii = 0; ii < count; ii++) {
            final Map<String, Object> sourceAttributes = Files.readAttributes(sourceFiles[ii], "lastModifiedTime");
            final Map<String, Object> targetAttributes = Files.readAttributes(targetFiles[ii], "lastModifiedTime");
//            assertFalse(sourceAttributes.equals(targetAttributes));
        }
    }
}