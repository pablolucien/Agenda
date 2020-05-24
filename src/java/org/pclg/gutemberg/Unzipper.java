package org.pclg.gutemberg;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/**
 * Summary description for Unzipper.
 */
final class Unzipper {
	public Unzipper(final File dir) throws IOException {
		final File[] subdirs = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(final File file) {
				return file.isDirectory();
			}
		});

		for (int ii = 0; ii < subdirs.length; ii++) {
			process(subdirs[ii]);
		}
	}

	private static void process(final File dir) throws IOException {
		final File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(final File file) {
				return file.isFile() && file.getName().toLowerCase().endsWith(".zip");
			}
		});

		for (int ii = 0; ii < files.length; ii++) {
			unzip(files[ii], dir);
		}
	}

	private static void unzip(final File targetZipFile, final File targetDir)
            throws IOException {
		final String fileName = targetZipFile.getName();
		String targetName = fileName.substring(0, fileName.lastIndexOf(".zip"));
		final ZipFile zipFile = new ZipFile(targetZipFile);
        if (zipFile.size() != 1) {
            System.err.println(
                    "Tiene más de un archivo (o ninguno). Procéselo a mano");
            return;
        }

		final Enumeration entries = zipFile.entries();
		final ZipEntry entry = (ZipEntry)entries.nextElement();
		final String internalName = entry.getName();
		final int posOfDot = internalName.lastIndexOf('.');
		if (posOfDot > -1 && posOfDot < internalName.length()) {
			targetName += internalName.substring(posOfDot);
		}
		System.out.println('<' + targetDir.getName() + ">  -  <"
                + targetZipFile.getName() + "> - <" + internalName + "> <"
                + targetName + ">");

		final File targetFile = new File(targetDir, targetName);
		if (targetFile.exists()) {
			System.err.println("Ya existe: eliminela e intentelo de nuevo");
			return;
		}

        extractFile(zipFile, entry, targetFile);
        zipFile.close();
        if (!targetZipFile.delete()) {
            System.err.println("No pude eliminar: " + targetZipFile);
        //    System.exit(0);
        }
    }

    private static void extractFile(final ZipFile zipFile, final ZipEntry entry,
               final File targetFile) throws IOException {
        final byte[] buffer = new byte[1024 * 100];
        int count;
        final InputStream zis = zipFile.getInputStream(entry);
        final FileOutputStream fos = new FileOutputStream(targetFile);
        while ((count = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, count);
        }
        fos.close();
        zis.close();
    }
}
