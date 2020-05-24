package org.pclg.tools;

import org.apache.log4j.Logger;
import org.pclg.filesystem.DirectoryCleaner;
import org.pclg.log.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class DirTest {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	public static File createDirTreeWithFiles() throws IOException {
		LOGGER.debug("Creando la estructura");
		final Path tmpDir = new File("/tmp").toPath();
		final File dir1 = Files.createTempDirectory(tmpDir, "DST_tmpDir").toFile();
		final Path common1 = Files.createFile(new File(dir1, "Common.txt").toPath());
		Files.write(common1, "I will disappear".getBytes());
		LOGGER.debug("dir1 = " + dir1);
		Files.createDirectories(new File(dir1, "sub11/sub12").toPath());
		final Path child122 = Files.createDirectories(new File(dir1, "sub21/sub22").toPath());
		Files.createFile(new File(child122.toFile(), "Enkel1").toPath());
		Files.createFile(new File(child122.toFile(), "Common").toPath());
		return dir1;
	}

	@Test
	public void testListarArchivos() throws Exception {
		final File baseDir = createDirTreeWithFiles();
		final Dir dir = new Dir(Dir.NULL_EXCLUDE_LIST);
		for (final File file : dir.listarArchivos(baseDir, true, false, Dir.AcceptableType.FILE)) {
			assertNotNull(file);
			assertTrue(file.isFile());
		}
		DirectoryCleaner.delTreesWithFiles(Collections.singletonList(baseDir));
	}

	@Test
	public void testListarDirectorios() throws Exception {
		final File baseDir = createDirTreeWithFiles();
		final Dir dir = new Dir(Dir.NULL_EXCLUDE_LIST);
		for (final File file : dir
			.listarArchivos(baseDir, true, false, Dir.AcceptableType.DIRECTORY)) {
			assertNotNull(file);
			assertTrue(file.isDirectory());
		}
		DirectoryCleaner.delTreesWithFiles(Collections.singletonList(baseDir));
	}

	@Test
	public void testListarAmbosTipos() throws Exception {
		final File baseDir = createDirTreeWithFiles();
		final Dir dir = new Dir(Dir.NULL_EXCLUDE_LIST);
		boolean thereAreFiles = false;
		boolean thereAreDirectories = false;
		for (final File file : dir.listarArchivos(baseDir, true, false, Dir.AcceptableType.BOTH)) {
			assertNotNull(file);
			thereAreFiles |= file.isFile();
			thereAreDirectories |= file.isDirectory();
		}
		assertTrue(thereAreFiles && thereAreDirectories);
		DirectoryCleaner.delTreesWithFiles(Collections.singletonList(baseDir));
	}

	@Test
	public void testListarDefault() throws Exception {
		final File baseDir = createDirTreeWithFiles();
		final Dir dir = new Dir(Dir.NULL_EXCLUDE_LIST);
		boolean thereAreFiles = false;
		boolean thereAreDirectories = false;
		for (final File file : dir.listarArchivos(baseDir, true, false)) {
			assertNotNull(file);
			thereAreFiles |= file.isFile();
			thereAreDirectories |= file.isDirectory();
		}
		assertTrue(thereAreFiles && thereAreDirectories);
		DirectoryCleaner.delTreesWithFiles(Collections.singletonList(baseDir));
	}

//	@Test
	public void measureDirTime() throws Exception {
		final int chrono = Chrono.getChrono();
		final int times = 10;
		long totalElapsed = 0;
		for (int ii = 0; ii < times; ii++) {
			Chrono.start(chrono);
			final Dir dir = new Dir(Dir.NULL_EXCLUDE_LIST);
			dir.listarArchivos(new File("/m2_repository"), true, false);
			Chrono.mark(chrono);
			final long elapsed = Chrono.elapsed(chrono);
			totalElapsed += elapsed;
			LOGGER.warn(String.format("Total time: %s", Chrono.timeDetail(elapsed)));
		}
		LOGGER.warn(String.format("Averageotal time: %s", Chrono.timeDetail(totalElapsed / times)));
	}
}
