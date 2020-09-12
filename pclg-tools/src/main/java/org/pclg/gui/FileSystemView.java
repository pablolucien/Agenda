package org.pclg.gui;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 28-sep-2007 13:19:22
 */
final class FileSystemView extends javax.swing.filechooser.FileSystemView {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final String NEW_FOLDER_NAME = "neues Heft";
	private static final File[] ROOTS = File.listRoots();
	private static final File FLOPPY_A = new File("A:\\");
	private static final File COMPUTER_NODE = new File("::{20D04FE0-3AEA-1069-A2D8-08002B30309D}");
	private static final File NETWORK = new File("::{208D2C60-3AEA-1069-A2D7-08002B30309D}");

	public FileSystemView() {
		LOGGER.debug("File system roots: " + Arrays.toString(ROOTS));
	}

//	public String getSystemDisplayName(File f) {
//		return super.getSystemDisplayName(f) + " - " + f.getName();
//	}

	@Override
	public File createNewFolder(final File containingDir) throws IOException {
		final File folder = new File(containingDir, NEW_FOLDER_NAME);
		return folder.mkdir() ? folder : null;
	}


	@Override
	@SuppressWarnings({"MethodWithMultipleReturnPoints"})
	public boolean isDrive(final File dir) {
		for (final File root : ROOTS) {
			if (root.equals(dir)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFloppyDrive(final File dir) {
		return FLOPPY_A.equals(dir);
	}

	@Override
	public boolean isComputerNode(final File dir) {
		return COMPUTER_NODE.equals(dir) || NETWORK.equals(dir);
	}
}

