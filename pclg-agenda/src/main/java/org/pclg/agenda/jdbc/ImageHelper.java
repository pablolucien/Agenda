package org.pclg.agenda.jdbc;

import org.apache.logging.log4j.Logger;
import org.pclg.agenda.AgendaDbException;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.log.LoggerFactory;
import org.pclg.media.image.ThumbnailCreator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23/07/17 11:12
 */
public final class ImageHelper implements FieldManagerHelper {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    private static final String INSERT_INTO_IMAGEN_SENTENCE =
        "INSERT INTO IMAGEN (clave, version,  imagePath, thumbnail) VALUES (?, ?, ?, ?)";

    private static final String SELECT_FROM_IMAGEN_SENTENCE =
        "SELECT imagePath, thumbnail FROM IMAGEN WHERE clave = ? AND version = ? ";

    private final PreparedStatement selectStatement;
    private final PreparedStatement insertStatement;
    private final GeneralHelper generalHelper;
    private final String imagesRoot;
    private final ThumbnailCreator thumbnailCreator = new ThumbnailCreator();

    ImageHelper(final Connection dbConnection, final GeneralHelper generalHelper, final String imagesRoot) throws SQLException {
        selectStatement = dbConnection.prepareStatement(SELECT_FROM_IMAGEN_SENTENCE);
        insertStatement = dbConnection.prepareStatement(INSERT_INTO_IMAGEN_SENTENCE);
        this.generalHelper = generalHelper;
        this.imagesRoot = imagesRoot;
    }

    /**
     * Actualiza un contacto con las imagenes que le corresponden por su clave,
     * y version.
     *
     * @param agendaRecord el registro a actualizar.
     */
    @Override
    public void retrieve(final AgendaRecord agendaRecord) throws SQLException {
        try {
            selectStatement.setInt(1, agendaRecord.getKey());
            selectStatement.setInt(2, agendaRecord.getVersionImage());
            final ResultSet rset = selectStatement.executeQuery();
            if (rset.next()) {
                agendaRecord.setImagePath(rset.getString(1));
                final InputStream stream = rset.getBinaryStream(2);
                agendaRecord.setThumbnail(new ImageIcon(ImageIO.read(stream)));
            }
        } catch (final IOException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            throw new AgendaDbException(ex);
        }
    }

    @Override
    public int persist(final AgendaRecord agendaRecord) {
        try {
            int nrUpdates = 0;
            final int clave = agendaRecord.getKey();
            final int newVersionImagen = generalHelper.obtainNextVersion("IMAGEN", clave);
            int index = 0;
            final String imagePath = agendaRecord.getImagePath();
            if (!isEmptyOrBlank(imagePath)) {
                final File originalImageFile = new File(imagesRoot + imagePath);
                if (originalImageFile.exists()) {
                    try (final InputStream stream = thumbnailCreator.getThumbnailAsStream(originalImageFile)) {
                        insertStatement.setInt(++index, clave);
                        insertStatement.setInt(++index, newVersionImagen);
                        insertStatement.setString(++index, imagePath);
                        insertStatement.setBlob(++index, stream);
                        nrUpdates += insertStatement.executeUpdate();
                    }
                }
            }
            return nrUpdates > 0 ? newVersionImagen : agendaRecord.getVersionImage();
        } catch (final SQLException | IOException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            throw new AgendaDbException(ex);
        }
    }
}
