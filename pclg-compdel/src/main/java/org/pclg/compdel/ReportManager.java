package org.pclg.compdel;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.Constants;
import org.pclg.tools.FileTools;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pablo
 * @since 16/01/16 13:42
 */
class ReportManager {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	/** Line separator. */
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * Utilizado para cargar y guardar los ficheros.
	 */
	private static JFileChooser fileChooser;


	/**
	 * Saves the results of  the comparation and possible elimination.
	 *
	 * @param texts2Save conjunto de textos a escribir.
	 */
	static void saveResults(final Window parent, final String initDir, final String... texts2Save) {
		if (fileChooser == null) {
			fileChooser = new JFileChooser(new File(initDir).getAbsolutePath());
		}

		final SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd_hh_mm_ss");
		fileChooser.setSelectedFile(new File(initDir, "CompDelResult_" + format.format(new Date()) + ".txt"));
		if (fileChooser.showDialog(parent, "Guardar como ...") == JFileChooser.APPROVE_OPTION) {
			final File file = fileChooser.getSelectedFile();
			boolean going2Write = true;
			if (file.exists()) {
				going2Write = false;
				final int res = JOptionPane
					.showConfirmDialog(parent, file.getName()
							+ " ya existe "
							+ LINE_SEPARATOR + " �Reemplazar el fichero?",
						file.getName() + " ya existe",
						JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.YES_OPTION) {
					going2Write = true;
				}
			}

			if(going2Write) {
				try {
					final StringBuilder builder =
						new StringBuilder(Constants.BUFFER_SIZE);
					for (final String text : texts2Save) {
						builder.append(text)
							.append("\n\r==========================\n\r");
					}
					FileTools.writeToFile(builder.toString().getBytes(),
						file.getAbsolutePath(), going2Write);
				} catch (final IOException ex) {
					LOGGER.error("", ex);
				}
			}
		}
	}
}
