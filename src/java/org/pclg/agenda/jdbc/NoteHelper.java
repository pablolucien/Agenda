package org.pclg.agenda.jdbc;

import org.pclg.agenda.entities.AgendaRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23/07/17 11:59
 */
public final class NoteHelper implements FieldManagerHelper {

    private static final String INSERT_INTO_NOTA_SENTENCE =
        "INSERT INTO NOTA (Clave, Version,  Secuencia, Nota) VALUES (?, ?, ?, ?)";

    private static final String SELECT_FROM_NOTA_SENTENCE =
        "SELECT Nota FROM NOTA WHERE Clave = ? AND Version = ? "
            + GeneralHelper.ORDER_BY_SECUENCIA_CLAUSE;
    private final Connection dbConnection;
    private final GeneralHelper generalHelper;

    NoteHelper(final Connection dbConnection, final GeneralHelper generalHelper) {
        this.dbConnection = dbConnection;
        this.generalHelper = generalHelper;
    }

    /**
     * Actualiza un contacto con las notas que le corresponden por su clave,
     * y version.
     *
     * @param record el registro a actualizar.
     */
    @Override
    public void retrieve(final AgendaRecord record) throws SQLException {
        try (final PreparedStatement stmt = dbConnection.prepareStatement(SELECT_FROM_NOTA_SENTENCE)) {
            stmt.setInt(1, record.getKey());
            stmt.setInt(2, record.getVersionNotes());
            final ResultSet rset = stmt.executeQuery();
            final StringBuilder builder = new StringBuilder(AgendaRecord.NOTES_FIELD_LEN);
            while (rset.next()) {
                builder.append(rset.getString(1));
            }
            if (builder.length() > 0) {
                record.setNotes(builder.toString());
            }
        }
    }

    @Override
    public int persist(final AgendaRecord record) throws SQLException {
        try (final PreparedStatement stmt = dbConnection.prepareStatement(INSERT_INTO_NOTA_SENTENCE)) {
            int nrUpdates = 0;
            final int clave = record.getKey();
            final int newVersionNota = generalHelper.obtainNextVersion("NOTA", clave);
            final String nota = record.getNotes();
            if (!isEmptyOrBlank(nota)) {
                final int length = nota.length();
                final int nrChunks = (int) Math.ceil((double) length
                    / AgendaRecord.NOTES_FIELD_LEN);
                for (int ii = 1; ii <= nrChunks; ii++) {
                    stmt.setInt(1, clave);
                    stmt.setInt(2, newVersionNota);
                    stmt.setInt(3, ii);
                    stmt.setString(4,
                        nota.substring((ii - 1) * AgendaRecord.NOTES_FIELD_LEN,
                            Math.min(ii * AgendaRecord.NOTES_FIELD_LEN, length)));
                    nrUpdates += stmt.executeUpdate();
                }
            }
            return nrUpdates > 0 ? newVersionNota : record.getVersionNotes();
        }
    }
}
