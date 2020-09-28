package misc.sopra;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * OuvrirSuivi: Ouvre le fichier de suivi.<BR>
 *
 * @author El Coyote Cojo
 * @since 10-sep-2010 16:56:14
 * @version 1.0
 */
public class OuvrirSuivi {
	private static final Logger LOGGER = LogManager.getLogger(OuvrirSuivi.class);
    private static final String PATH_SEPARATOR = "/";
	private static final String FILE_NOT_EXISTS_MSG_KEY = "file.not.exists.msg";
    private static final String FILE_PATTERN_KEY = "file.pattern";
    private static final String TARGET_TRIGRAMME_KEY = "targetTrigramme";
    private static final String GENERIC_TRIGRAMME_KEY = "genericTrigramme";
    private static final String WAIT_KEY = "action.wait";
    private static final String TITLE_KEY = "msg.box.title";
    private static final String PREVIOUS_WEEK_KEY = "action.previuosWeek";
    private static final String SPAN_KEY = "spanInterval";
    private static final String COPY_GENERIC_FILE_KEY = "action.copyGenericFile";
    private static final String COPY_AND_OPEN_KEY = "action.copyEtPrevious";
    private static final String NO_RESOURCES_MSG =
        "No puedo obtener recursos para la aplicaci�n: ({0}.properties ({1}))";
	private static final String IO_ERROR_MSG = "La torta!! No puedo copiar. Error: {0}";
    private static final  URL QUESTION_ICON_RESOURCE =
        ClassLoader.getSystemResource("dialog-question.png");
    private static final  Icon QUESTION_ICON = QUESTION_ICON_RESOURCE == null ?
        null : new ImageIcon(QUESTION_ICON_RESOURCE);
	private static final String MONTHLY_SPAN = "MONTH";

    public static void main(final String[] args) {
        try {
			new OuvrirSuivi().openSuiviOrDie(0, args.length > 0 ? 
				args[0] : OuvrirSuivi.class.getSimpleName());
		} catch (final Throwable throwable) {
			LOGGER.error("", throwable);
		}
    }

    /**
     * Ouvre le fichier de suivi ou meurt.
     * @param spanOffset le num�ro de semaines ou mois en avance (> 0) ou en
     * arri�re (< 0) � ouvrir.
     * @param bundleBaseName the ressource bundle to use
     * @return <code>true</code> si le fichier a �t� ouvert, <code>false</code> au contraire.
     */
    private boolean openSuiviOrDie(final int spanOffset, final String bundleBaseName) {
        try {
            final ResourceBundle resourceBundle =
                    ResourceBundle.getBundle(bundleBaseName);
            final Format msgFormatFileNotExists = new MessageFormat(
                    resourceBundle.getString(FILE_NOT_EXISTS_MSG_KEY));
            final Format fileFormat = new MessageFormat(
                    resourceBundle.getString(FILE_PATTERN_KEY));
            final Calendar calendar = Calendar.getInstance();
            final String span = resourceBundle.getString(SPAN_KEY);
            if (MONTHLY_SPAN.equals(span)) {
            	calendar.add(Calendar.MONTH,  spanOffset);
            } else {
            	calendar.add(Calendar.WEEK_OF_YEAR,  spanOffset);
            }
            final String targetTrigramme = resourceBundle.getString(TARGET_TRIGRAMME_KEY);
			final Object[] formatParams = {calendar.getTime(), targetTrigramme};
			final String filePath = fileFormat.format(formatParams);
			final File targetFile = getFile(filePath);
			
            if (targetFile.exists()) {
				Desktop.getDesktop().open(targetFile);
            } else {
				final String genericTrigramme = resourceBundle.getString(GENERIC_TRIGRAMME_KEY);
				formatParams[1] = genericTrigramme;
				final String genericFileName = fileFormat.format(formatParams);
				final File genericFile = getFile(genericFileName);
				final Object[] dialogOptions;
				if (genericFile.exists()) {
					dialogOptions = new Object[] {resourceBundle.getString(WAIT_KEY),
                        resourceBundle.getString(PREVIOUS_WEEK_KEY),
						resourceBundle.getString(COPY_GENERIC_FILE_KEY),
						resourceBundle.getString(COPY_AND_OPEN_KEY)};
				} else {
					dialogOptions = new Object[] {resourceBundle.getString(WAIT_KEY),
                        resourceBundle.getString(PREVIOUS_WEEK_KEY)};
				}
				final int option = JOptionPane.showOptionDialog(null,
	                msgFormatFileNotExists.format(new Object[] {targetFile}),
                    resourceBundle.getString(TITLE_KEY), JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, QUESTION_ICON,
					dialogOptions,
                    null);
                switch (option) {
				case 1:
					return openSuiviOrDie(spanOffset - 1, bundleBaseName);
				case 2: {
					final String genericFileRealName = genericFile.getAbsolutePath();
					final String targetFileName = genericFileRealName
						.replaceAll(genericTrigramme, targetTrigramme);
					copyFile(genericFile, new File(targetFileName));
					return openSuiviOrDie(spanOffset, bundleBaseName);
					}
				case 3:
					final boolean couldOpen = openSuiviOrDie(spanOffset - 1, bundleBaseName);
					//if (couldOpen) {
						final String genericFileRealName = genericFile.getAbsolutePath();
						final String targetFileName = genericFileRealName
							.replaceAll(genericTrigramme, targetTrigramme);
						copyFile(genericFile, new File(targetFileName));
						return openSuiviOrDie(0, bundleBaseName);
//					} else {
//						return couldOpen;
//					}
				default:
					break;
                }
            }
        } catch (final MissingResourceException ex) {
			LOGGER.error(ex);
            JOptionPane.showMessageDialog(null,
            	new MessageFormat(NO_RESOURCES_MSG)
                	.format(new Object[] {bundleBaseName, ex.getMessage()}),
					"", JOptionPane.ERROR_MESSAGE);
        } catch (final IOException ex) {
			LOGGER.error(ex);
            JOptionPane.showMessageDialog(null,
            	new MessageFormat(IO_ERROR_MSG)
                	.format(new Object[] {ex.getMessage()}),
					"", JOptionPane.ERROR_MESSAGE);
        }
		return false;
    }
	
	private static File getFile(final String filePath) {
		final int pos = filePath.lastIndexOf(PATH_SEPARATOR);
		final String dirName;
		final String fileName;
		if (pos >= 0) {
			dirName = filePath.substring(0, pos);
			fileName = filePath.substring(pos + 1);
		} else {
			dirName = ".";
			fileName = filePath;
		}
		final File dir = new File(dirName);
		final File[] files;
		if (dir.exists()) {
			files = dir.listFiles((dir1, name) -> name.matches(fileName));
		} else {
			files = new File[1];
			files[0] = new File(fileName);
		}
		return files.length > 0 ? files[0] : new File(fileName);
	}

	private static void copyFile(final File sourceFile, final File targetFile)
			throws IOException {
        try (final FileChannel inChannel = 
        		new FileInputStream(sourceFile).getChannel(); 
        	 final FileChannel outChannel =
        	 	new FileOutputStream(targetFile).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } 
/* con un c�digo como este se elimina el warning de Eclipse 
   Resource leak: '<unassigned Closeable value>' is never closed, 
   pero �es necesario?
 		try (final FileInputStream inStream = new FileInputStream(sourceFile);
			 final FileOutputStream outStream = new FileOutputStream(targetFile);
			 final FileChannel inChannel = inStream.getChannel();
			 final FileChannel outChannel = outStream.getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } 

 */
	}
}
