package org.pclg.filesystem.synchonizer;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.pclg.filesystem.synchonizer.DirectorySynchronizer.INSIGNIFICANT_DETAIL;
import static org.testng.Assert.assertEquals;

/**
 * The Three Laws of TDD (UncleBob).
 * <p>
 * Over the years I have come to describe Test Driven Development in terms of three simple rules. They are:
 * <p>
 * 1    You are not allowed to write any production code unless it is to make a failing unit test pass.
 * 2    You are not allowed to write any more of a unit test than is sufficient to fail; and compilation failures are failures.
 * 3    You are not allowed to write any more production code than is sufficient to pass the one failing unit test.
 *
 * @since 27/07/2018.
 */
public class DirectorySynchronizerTest {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final String SOBREVIVIR = "I will survive";
    private File dir1;
    private File dir2;

    //    @Test(expected = DirectorySynchronizerException.class)
    public void shouldThrowException() {
//        new DirectorySynchronizer().synchronizeDirectories(new File(""), new File(""));
    }

    private void initSynchronizeTest() throws IOException {
        LOGGER.debug("Creando la estructura");
		final Path tmpDir = Files.createTempDirectory(getClass().getName());
		tmpDir.toFile().deleteOnExit();
		dir1 = Files.createTempDirectory(tmpDir, "DST_tmpDir").toFile();
        final Path common1 = Files.createFile(new File(dir1, "Common.txt").toPath());
        Files.write(common1, "I will disappear".getBytes());
        LOGGER.debug("dir1 = " + dir1);
        Files.createDirectories(new File(dir1, "sub11/sub12").toPath());
        final Path child122 = Files.createDirectories(new File(dir1, "sub21/sub22").toPath());
        Files.createFile(new File(child122.toFile(), "Enkel1").toPath());
        Files.createFile(new File(child122.toFile(), "Common").toPath());
        dir2 = Files.createTempDirectory(tmpDir, "DST_tmpDir").toFile();
        final Path common2 = Files.createFile(new File(dir2, "Common.txt").toPath());
        Files.write(common2, SOBREVIVIR.getBytes());
        final File common2ToFile = common2.toFile();
        common2ToFile.setLastModified(common2ToFile.lastModified() + INSIGNIFICANT_DETAIL); // Need that this file be "newer" than common1.
        LOGGER.debug("dir2 = " + dir2);
        final Path child212 = Files.createDirectories(new File(dir2, "hijo11/hijo12").toPath());
        Files.createFile(new File(child212.toFile(), "nieto1").toPath());
        Files.createDirectories(new File(dir2, "hijo21/hijo22").toPath());
    }

    private void cleanUpSynchronizeTest() throws IOException {
        LOGGER.debug("Deber�a estar limpiando...\n... pero estoy descansando");
        FileTools.delTree(dir1, true, true);
        FileTools.delTree(dir2, true, true);
    }

    @Test
	public void synchronizeDirs() throws IOException {
		initSynchronizeTest();
		final DirectorySynchronizer synchronizer = new DirectorySynchronizer();
		assertEquals(3, dir1.listFiles().length);
        assertEquals(3, dir2.listFiles().length);
//        synchronizer.synchronizeDirectories(dir1, dir2);
//        assertEquals(5, dir1.listFiles().length);
//        assertEquals(5, dir2.listFiles().length);
//        assertNotNull(common1);
//        final List<String> lines = Files.readAllLines(common1);
//        assertThat(lines.size(), is(1));
//        assertThat(lines.get(0), is(SOBREVIVIR));
        cleanUpSynchronizeTest();
    }

   // TODO: finish test
	@Test
	public void synchronizeJsonFile() throws IOException, URISyntaxException {
//		initSynchronizeTest();
		final DirectorySynchronizer synchronizer = new DirectorySynchronizer();
		final File targetsFile = new File(getClass()
            .getResource("/Test_DS_Targets.json").toURI());
		final List<TargetDef> targets = TargetDef.readTargetsFile(targetsFile, Collections.emptyMap());
		synchronizer.synchronize(targetsFile);
	}
}
