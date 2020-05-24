package org.pclg.media.image;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static junit.framework.TestCase.*;

/**
 * @since 16/03/2018.
 */
public class ThumbnailCreatorTest {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final String RESOURCES_DIR = "/tmp/resources/";
	private static final String THUMBNAIL_PREFIX = "thumbnail_";

	@BeforeClass
	public static void init() throws IOException, URISyntaxException {
		final File resourcesDir = new File(RESOURCES_DIR);
		if (resourcesDir.exists() || resourcesDir.mkdirs()) {
			final File source = new File(Runtime.getRuntime().getClass()
				.getResource("/images/unknown-man.png").toURI());
			final File target = new File(resourcesDir, source.getName());
			if (!target.exists()) {
				Files.copy(source.toPath(), target.toPath());
			}
		}
	}

	/**
	 * Not really a test, but a means to know the available formats.
	 */
//	@Test
	public void printFormats() {
		for (final String name : ImageIO.getWriterFormatNames()) {
			LOGGER.error(name);
		}
	}

	@Test
	public void createThumbnail_success() {
		final File dir = new File(RESOURCES_DIR);
		final File[] files = dir.listFiles();
		assertNotNull("Should have something to test.", files);
		assertTrue("Should have something to test.", files.length > 0);
		for (final File file : files) {
			final String path = file.getAbsolutePath();
			LOGGER.debug("Creating thumbnail for " + path);
			testCreation(path);
		}
	}

	private void testCreation(final String pathname) {
		final File originalImageFile = new File(pathname);
		final String fileName = originalImageFile.getName();
		final File thumbnail = new ThumbnailCreator().getThumbnailAsFile(originalImageFile);
		if (thumbnail != null) {    // Can be null if ImageIO can't obtain an image
			final FileTools.SplittedName splittedName = FileTools.splittName(fileName);
			assertTrue("Thumbnail should exist.", thumbnail.exists());
			assertEquals(
				THUMBNAIL_PREFIX + splittedName.base + '.' + "jpg",
				thumbnail.getName());
			assertTrue("Size should be greater than 0.", thumbnail.length() > 0);
			assertTrue("Size should be lesser.", thumbnail.length() < originalImageFile.length());
		}
	}

	@Test(expected = ThumbnailCreator.ThumbnailCreatorException.class)
	public void createThumbnail_fail1() {
		final String pathname = RESOURCES_DIR + "NöXel.png";
		final File originalImageFile = new File(pathname);
		new ThumbnailCreator().getThumbnailAsFile(originalImageFile);
	}

	@AfterClass
	public static void clean() {
		final File dir = new File(RESOURCES_DIR);
		final File[] files = dir.listFiles();
		if (files != null) {
			for (final File file : files) {
				try {
					Files.delete(file.toPath());
				} catch (final IOException ex) {
					LOGGER.error(LoggerFactory.ERROR_TAG, ex);
				}
			}
		}
		try {
			Files.deleteIfExists(dir.toPath());
		} catch (final IOException ex) {
			LOGGER.error(LoggerFactory.ERROR_TAG, ex);
		}
	}
}
