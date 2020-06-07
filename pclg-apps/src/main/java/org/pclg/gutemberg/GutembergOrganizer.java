package org.pclg.gutemberg;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Summary description for GutembergOrganizer
 */
public class GutembergOrganizer {
	public static void main(final String[] args) throws IOException {
		final File dir = new File("C:/home/Literatur");
		//File[] files = dir.listFiles(new FileFilter() {
		//    public boolean accept(File file) {
		//        return file.isFile();
		//    }
		//});

		//for (int ii = 0; ii < files.length; ii++) {
		//    process(files[ii], dir);
		//}
		new Unzipper(dir);

		espera();
	}

	public static void process(final File file, final File dir) throws IOException {
		final String name = file.getName();
		String base = null;
		String ext = null;
		int posOfDot = -1;
		if ((posOfDot = name.lastIndexOf('.')) > 0) {
			base = name.substring(0, posOfDot);
			ext = name.substring(posOfDot + 1);
			String title = null;
			String author = null;
			int posOfBy = -1;
			if ((posOfBy = base.indexOf(" by ")) > 0) {
				title = base.substring(0, posOfBy);
				author = base.substring(posOfBy + 4);
//				System.out.println('<' + title + ">  -  <" + ext + "> - <" + author + '>');
				final File targetDir = new File(dir, author);
				if ((targetDir.exists() && targetDir.isDirectory()) || targetDir.mkdir()) {
					final File targetFile = new File(targetDir, title + "." + ext);
					if (file.renameTo(targetFile)) {
						if (ext.equalsIgnoreCase("zip")) {
							unzip(targetFile, targetDir, title);
						}
					}
				}
			}
		}
	}

	private static void unzip(final File targetFile, final File targetDir,
		final String fileName)
            throws IOException {
		final ZipFile zipFile = new ZipFile(targetFile);
		final Enumeration entries = zipFile.entries();
		final ZipEntry entry = (ZipEntry) entries.nextElement();
		System.out.println('<' + targetDir.getName() + ">  -  <"
                + targetFile.getName()
			+ "> - <" + entry.getName() + "> <" + fileName + ">");
	}

	private static void espera() {
		final Console in = System.console();
		if (in != null) {
			in.readLine("Pulse Enter");
		}
	}
}
