package org.pclg.agenda.plugins;

import org.apache.logging.log4j.Logger;
import org.pclg.annotations.QuickAndDirty;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

@QuickAndDirty
public class CheckAndCopyPhotosPlugin implements Plugin {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    private static final String SELECT_FROM_IMAGEN_SENTENCE = "SELECT imagePath FROM IMAGEN";

    /**
     * @param properties properties of the application.
     * @param conn       la conexion a la base de datos para leer las im�genes.
     * @param args       contiene la unidad donde leer la imagen, si no existe la
     */
    @Override
    public void execute(final Properties properties, final Connection conn, final String... args)
        throws SQLException {
        final String name = getClass().getName();
        if (args.length != 2) {
            showMessageDialog(null,
                "Usage: " + name + " <sourceImagesRoot> <targetImagesRoot>",
                //getStringFromProperties(properties, "Plugins.CheckAndCopyPhotosPlugin.parameter"),
                getStringFromProperties(properties, "Agenda.alert.title"),
                ERROR_MESSAGE);
            return;
        }
        LOGGER.info(name + ".execute() " + args[0] + ' ' + args[1]);
        final String sourceImagesRoot = args[0].endsWith(File.separator) ? args[0] : args[0] + File.separatorChar;
        final String targetImagesRoot = args[1].endsWith(File.separator) ? args[1] : args[1] + File.separatorChar;
        try (final PreparedStatement stmt = conn.prepareStatement(
            SELECT_FROM_IMAGEN_SENTENCE)) {
            final ResultSet rset = stmt.executeQuery();
            while (rset.next()) {
                checkAndCopy(rset.getString(1), sourceImagesRoot, targetImagesRoot);
            }
        }
    }

    private void checkAndCopy(final String imagePath, final String sourceImagesRoot, final String targetImagesRoot) {
        LOGGER.info(imagePath);
        final File targetFile = new File(targetImagesRoot + imagePath);
        if (!targetFile.exists()) {
            LOGGER.warn(targetFile + " doesn't exists");
            final File sourceFile = new File(sourceImagesRoot + imagePath);
            if (sourceFile.exists()) {
                try {
                    FileTools.copyFile(sourceFile, targetFile);
                } catch (final IOException ex) {
                    LOGGER.error("Error", ex);
                }
            } else {
                LOGGER.warn(sourceFile + " also doesn't exists");
            }
        }
    }
}
