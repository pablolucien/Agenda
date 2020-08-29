package org.pclg.agenda.jdbc;

import org.pclg.agenda.entities.AgendaRecord;

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
 * @since 23/07/17 11:59
 */
public final class NoteHelper implements FieldManagerHelper {

    private static final String INSERT_INTO_NOTA_SENTENCE =
        "INSERT INTO NOTA (Clave, Version,  Secuencia, Nota) VALUES (?, ?, ?, ?)";

    private static final String SELECT_FROM_NOTA_SENTENCE =
        "SELECT Nota FROM NOTA WHERE Clave = ? AND Version = ? "
            + GeneralHelper.ORDER_BY_SECUENCIA_CLAUSE;

    private final GeneralHelper generalHelper;
    private final PreparedStatement selectStatement;
    private final PreparedStatement insertStatement;
    // Extract the PreparedStatements in order to optimize their use, but I lose the thread safety. Can ThreadLocal help me?
    //private static final ThreadLocal<PreparedStatement> preparedStatementThreadLocal = new InheritableThreadLocal<>();

    NoteHelper(final Connection dbConnection, final GeneralHelper generalHelper) throws SQLException {
        this.generalHelper = generalHelper;
        selectStatement = dbConnection.prepareStatement(SELECT_FROM_NOTA_SENTENCE);
        insertStatement = dbConnection.prepareStatement(INSERT_INTO_NOTA_SENTENCE);
    }

    /**
     * Actualiza un contacto con las notas que le corresponden por su clave,
     * y version.
     *
     * @param record el registro a actualizar.
     */
    @Override
    public void retrieve(final AgendaRecord record) throws SQLException {
        selectStatement.setInt(1, record.getKey());
        selectStatement.setInt(2, record.getVersionNotes());
        final ResultSet rset = selectStatement.executeQuery();
        final StringBuilder builder = new StringBuilder(AgendaRecord.NOTES_FIELD_LEN);
        while (rset.next()) {
            builder.append(rset.getString(1));
        }
        if (builder.length() > 0) {
            record.setNotes(builder.toString());
        }
    }

    @Override
    public int persist(final AgendaRecord record) throws SQLException {
        int nrUpdates = 0;
        final int clave = record.getKey();
        final int newVersionNota = generalHelper.obtainNextVersion("NOTA", clave);
        final String nota = record.getNotes();
        if (!isEmptyOrBlank(nota)) {
            final int length = nota.length();
            final int nrChunks = (int) Math.ceil((double) length
                / AgendaRecord.NOTES_FIELD_LEN);
            for (int ii = 1; ii <= nrChunks; ii++) {
                insertStatement.setInt(1, clave);
                insertStatement.setInt(2, newVersionNota);
                insertStatement.setInt(3, ii);
                insertStatement.setString(4,
                    nota.substring((ii - 1) * AgendaRecord.NOTES_FIELD_LEN,
                        Math.min(ii * AgendaRecord.NOTES_FIELD_LEN, length)));
                nrUpdates += insertStatement.executeUpdate();
            }
        }
        return nrUpdates > 0 ? newVersionNota : record.getVersionNotes();
    }
}
