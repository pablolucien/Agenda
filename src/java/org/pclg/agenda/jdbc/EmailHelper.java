package org.pclg.agenda.jdbc;

import org.pclg.agenda.entities.AgendaRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23/07/17 11:54
 */
final class EmailHelper implements FieldManagerHelper {
    private static final String INSERT_INTO_EMAIL_SENTENCE =
        "INSERT INTO EMAIL (Clave, Version,  Secuencia, Email) "
            + "VALUES (?, ?, ?, ?)";

    private static final String SELECT_FROM_EMAIL_SENTENCE =
        "SELECT Email FROM EMAIL WHERE Clave = ? AND Version = ? "
            + GeneralHelper.ORDER_BY_SECUENCIA_CLAUSE;

    private final Connection dbConnection;
    private final GeneralHelper generalHelper;

    EmailHelper(final Connection dbConnection, final GeneralHelper generalHelper) {
        this.dbConnection = dbConnection;
        this.generalHelper = generalHelper;
    }

    /**
     * Actualiza un contacto con los emails que le corresponden por su clave,
     * y version.
     *
     * @param record el registro a actualizar.
     */
    @Override
	public void retrieve(final AgendaRecord record)
        throws SQLException {
        try (final PreparedStatement stmt = dbConnection.prepareStatement(
            SELECT_FROM_EMAIL_SENTENCE)) {
            final List<String> emailsList = new ArrayList<>();
            stmt.setInt(1, record.getKey());
            stmt.setInt(2, record.getVersionEmail());
            final ResultSet rset = stmt.executeQuery();
            while (rset.next()) {
                emailsList.add(rset.getString(1));
            }
            record.setEmails(emailsList);
        }
    }

    @Override
	public int persist(final AgendaRecord record) throws SQLException {
        try (final PreparedStatement stmt = dbConnection.prepareStatement(
            INSERT_INTO_EMAIL_SENTENCE)) {
            int secuencia = 0;
            int nrUpdates = 0;
            final int clave = record.getKey();
            final int newVersionEmail = generalHelper.obtainNextVersion("EMAIL", clave);
            for (final String email : record.getEmails()) {
                if (!isEmptyOrBlank(email)) {
                    stmt.setInt(1, clave);
                    stmt.setInt(2, newVersionEmail);
                    stmt.setInt(3, ++secuencia);
                    stmt.setString(4, email);
                    nrUpdates += stmt.executeUpdate();
                }
            }
            return nrUpdates > 0 ? newVersionEmail : record.getVersionEmail();
        }
    }
}
