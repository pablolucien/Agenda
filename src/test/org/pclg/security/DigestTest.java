package org.pclg.security;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

public class DigestTest {

	@Test
	public void testVerifyDigest() {
		try {
			final File tmpFile = File.createTempFile("test", ".txt");
			tmpFile.deleteOnExit();
			final File dir = tmpFile.getParentFile();
			try (final FileWriter writer1 = new FileWriter(tmpFile)) {
				writer1.write("Los pollos de mi cazuela no sirven para comer");
			} catch (final IOException ex) {
				fail("Could not test: " + ex);
			}
			createFile(dir, tmpFile, "md5", "2c212d925f6a75c9f4787b55df46bd4a");
			createFile(dir, tmpFile, "sha1", "84ef247299026e713ee26d86f0e03fbe03c64edd");
			createFile(dir, tmpFile, "sha-256", "ec896b0ea6561852bf63bd1b102f3a8c8fb0d17e3920dc55d44bf101cc399d39");
			createFile(dir, tmpFile, "sha-512", "e4a991379ddf12f1ca24b85323c400f7778dd2b69c6e322587c67aa2ed8562ceb54f4bfbdd15999dcd1679173e973e6850dca0ea58e02b671457a16324b9dbad");

			assertTrue("md5Digest", Digest.verifyDigest(tmpFile.getAbsolutePath(), "md5"));
			assertTrue("sha1Digest", Digest.verifyDigest(tmpFile.getAbsolutePath(), "sha1"));
			assertTrue("sha_256Digest", Digest.verifyDigest(tmpFile.getAbsolutePath(), "sha-256"));
			assertTrue("sha_512Digest", Digest.verifyDigest(tmpFile.getAbsolutePath(), "sha-512"));
		} catch (final IOException ex) {
			fail("Could not test: " + ex);
		}
	}

	private void createFile(final File dir, final File tmpFile, final String algorithm, final String digest) {
		final File digestFile = new File(dir, tmpFile.getName() + "." + algorithm);
		digestFile.deleteOnExit();
		try (final FileWriter writer = new FileWriter(digestFile)) {
			writer.write(digest);
		} catch (final IOException ex) {
			fail("Could not test: " + ex);
		}
	}

	@Test
	public void testComputeDigest() {
		try {
			final File tmpFile = File.createTempFile("test", ".txt");
			try (final FileWriter writer = new FileWriter(tmpFile)) {
				tmpFile.deleteOnExit();
				writer.write("Los pollos de mi cazuela no sirven para comer");
			} catch (final IOException ex) {
				fail("Could not test: " + ex);
			}
			final String md5Digest = "2c212d925f6a75c9f4787b55df46bd4a";
			final String sha1Digest = "84ef247299026e713ee26d86f0e03fbe03c64edd";
			final String sha_256Digest = "ec896b0ea6561852bf63bd1b102f3a8c8fb0d17e3920dc55d44bf101cc399d39";
			final String sha_512Digest = "e4a991379ddf12f1ca24b85323c400f7778dd2b69c6e322587c67aa2ed8562ceb54f4bfbdd15999dcd1679173e973e6850dca0ea58e02b671457a16324b9dbad";
			assertEquals(md5Digest, Digest.computeDigest(tmpFile, "md5").get());
			assertEquals(sha1Digest, Digest.computeDigest(tmpFile, "sha1").get());
			assertEquals(sha_256Digest, Digest.computeDigest(tmpFile, "sha-256").get());
			assertEquals(sha_512Digest, Digest.computeDigest(tmpFile, "sha-512").get());
		} catch (final IOException ex) {
			fail("Could not test: " + ex);
		}
	}

	@Test
	public void testGenerateByteArrayDigest() {
//		fail("Not yet implemented");
	}

	@Test
	public void testGetAlgorithms() {
//		fail("Not yet implemented");
	}

}
