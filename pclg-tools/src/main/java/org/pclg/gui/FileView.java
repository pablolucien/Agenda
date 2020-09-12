package org.pclg.gui;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.PropertiesHelper;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * ****************************************************
 * Premature optimization is the root of all evil.     *
 * �Donald E. Knuth                                   *
 * *****************************************************
 * A convenience implementation of the FileView interface that
 * manages name, icon, traversable, and file type information.
 * This this implemention will work well with file systems that use
 * "dot" extensions to indicate file type. For example: "picture.gif"
 * as a gif image.
 * If the java.io.File ever contains some of this information, such as
 * file type, icon, and hidden file inforation, this implementation may
 * become obsolete. At minimum, it should be rewritten at that time to
 * use any new type information provided by java.io.File
 * Example:
 * JFileChooser chooser = new JFileChooser();
 * fileView = new ExampleFileView();
 * fileView.putIcon("jpg", new ImageIcon("images/jpgIcon.jpg"));
 * fileView.putIcon("gif", new ImageIcon("images/gifIcon.gif"));
 * chooser.setFileView(fileView);
 *
 * @author Jeff Dinkins
 * @author El Coyote Cojo
 * @since 28-sep-2007 10:45:21
 * @version 1.18 11/30/05
 */
public final class FileView extends javax.swing.filechooser.FileView {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private final File DIR_NO_ACCESIBLE;
	private final Map<String, Icon> icons;
	private final Map<File, String> fileDescriptions;
	private final Map<String, String> typeDescriptions;
	private Properties properties;
	private static final String SPLIT_SEPARATOR = "@#@";
//	private File propertiesFile;
//	private long lastModifiedTime;

	/** Constructs a new AgendaFileView. */
	public FileView() {
		loadAppProperties();
		DIR_NO_ACCESIBLE = new File(PropertiesHelper
			.getStringFromProperties(properties, "no_access_dir"));
		properties.keySet().size();
		final Set<Object> keySet = properties.keySet();
		final int size = keySet.size();
		icons = new HashMap<>(size);
		fileDescriptions = new HashMap<>(size);
		typeDescriptions = new HashMap<>(size);
		for (final Object element : keySet) {
			final String key = (String) element;
			if (key.startsWith("EXT_")) {
				final String[] values = PropertiesHelper
					.getStringFromProperties(properties, key).split(SPLIT_SEPARATOR);
				try {
					putTypeDescription(key, values[0]);
					putIcon(key.substring(4), new ImageIcon(getClass().getResource(values[1])));
				} catch (final Exception ex) {
					LOGGER.log(Level.ERROR, MessageFormat.format(
						PropertiesHelper.getStringFromProperties(properties, 
							"error_obteniendo_recursos"), key), ex);
				}
			}
		}
	}

	private void loadAppProperties() {
		properties = new Properties();
		try {
			final String propsFileName = getClass().getSimpleName() + ".properties";
			final InputStream in = FileView.class.getClassLoader()
					.getResourceAsStream(propsFileName);
			if (in == null) {
				throw new FileNotFoundException(propsFileName);
			}
			properties.load(in);
			in.close();
		} catch (final IOException ex) {
			LOGGER.log(Level.ERROR, LoggerFactory.ERROR_TAG, ex);
		}
	}

	/**
	 * The name of the file.  Do nothing special here. Let
	 * the system file view handle this.
	 *
	 * @see FileView#getName
	 */
	@Override
	public String getName(final File f) {
		return null;
	}

	/** Adds a human readable description of the file.
	 *
	 * @param file the file
	 * @param fileDescription the description
	 */
	public void putDescription(final File file, final String fileDescription) {
		fileDescriptions.put(file, fileDescription);
	}

	/**
	 * A human readable description of the file.
	 *
	 * @see FileView#getDescription
	 */
	@Override
	public String getDescription(final File f) {
		return fileDescriptions.get(f);
	}

	/**
	 * Adds a human readable type description for files. Based on "dot"
	 * extension strings, e.g: ".gif". Case is ignored.
	 *
	 * @param extension the extension.
	 * @param typeDescription the typeDescription.
	 */
	private void putTypeDescription(final String extension, final String typeDescription) {
		typeDescriptions.put(extension, typeDescription);
	}

	/**
	 * Adds a human readable type description for files of the type of
	 * the passed in file. Based on "dot" extension strings, e.g: ".gif".
	 * Case is ignored.
	 *
	 * @param file the file.
	 * @param typeDescription the typeDescription.
	 */
	public void putTypeDescription(final File file, final String typeDescription) {
		putTypeDescription(getExtension(file), typeDescription);
	}

	/**
	 * A human readable description of the type of the file.
	 *
	 * @see FileView#getTypeDescription
	 */
	@Override
	public String getTypeDescription(final File f) {
		return typeDescriptions.get(getExtension(f));
	}

	/**
	 * Convenience method that returns the "dot" extension for the
	 * given file.
	 *
	 * @param file the file whose extension we want.
	 * @return the extension of the file.
	 */
	private static String getExtension(final File file) {
		final String name = file.getName();
		final int extensionIndex = name.lastIndexOf('.');
		return extensionIndex < 0 ? null :
				name.substring(extensionIndex + 1).toLowerCase();
	}

	/**
	 * Adds an icon based on the file type "dot" extension
	 * string, e.g: ".gif". Case is ignored.
	 *
	 * @param extension the extension.
	 * @param icon the icon.
	 */
	private void putIcon(final String extension, final Icon icon) {
		icons.put(extension, icon);
	}

	/**
	 * Icon that reperesents this file. Default implementation returns
	 * null. You might want to override this to return something more
	 * interesting.
	 *
	 * @see FileView#getIcon
	 */
	@Override
	public Icon getIcon(final File file) {
		Icon icon = null;
		final String extension = getExtension(file);
		if (extension != null && file.isFile()) {
//			checkPropertiesFile();
			icon = icons.get(extension);
		}
		return icon;
	}

//	private void checkPropertiesFile() {
//		if (lastModifiedTime != propertiesFile.lastModified()) {
//
//		}
//	}

	/**
	 * Whether the directory is traversable or not. Generic implementation
	 * returns true for all directories and special folders.
	 * You might want to subtype ExampleFileView to do somethimg more interesting,
	 * such as recognize compound documents directories; in such a case you might
	 * return a special icon for the directory that makes it look like a regular
	 * document, and return false for isTraversable to not allow users to
	 * descend into the directory.
	 *
	 * @see FileView#isTraversable
	 */
	@Override
	public Boolean isTraversable(final File file) {
		// null ==> Use default from FileSystemView
		return file.equals(DIR_NO_ACCESIBLE) ? Boolean.FALSE : null;
	}

	/**
	 * Test bench.
	 * @param arg umentos
	 */
	public static void main(final String[] arg) {
		final FileView fileView = new FileView();
		final JFileChooser fileChooser = new JFileChooser("D:/plucien/Reise/agenda",
				new FileSystemView());
		final FileFilter filter = new FileNameExtensionFilter("Imagenes",
				"gif", "jpg", "png", "ico", "bmp", "tiff");
		fileChooser.setFileView(fileView);
		fileChooser.setFileFilter(filter);
		fileChooser.showOpenDialog(null);
	}
}
