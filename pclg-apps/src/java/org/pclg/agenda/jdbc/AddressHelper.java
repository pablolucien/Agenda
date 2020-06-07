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
 * @since 23/07/17 10:56
 */
final class AddressHelper implements FieldManagerHelper {
    private static final String INSERT_INTO_DIRECCION_SENTENCE =
        "INSERT INTO DIRECCION (Clave, Version,  Secuencia, Direccion) "
            + "VALUES (?, ?, ?, ?)";

    private static final String SELECT_FROM_DIRECCION_SENTENCE =
        "SELECT Direccion FROM DIRECCION WHERE Clave = ? AND Version = ? "
            + GeneralHelper.ORDER_BY_SECUENCIA_CLAUSE;

    private final Connection dbConnection;
    private final GeneralHelper generalHelper;

    AddressHelper(final Connection dbConnection, final GeneralHelper generalHelper) {
        this.dbConnection = dbConnection;
        this.generalHelper = generalHelper;
    }

    /**
     * Actualiza un contacto con las direcciones que le corresponden por su clave,
     * y version.
     *
     * @param record el registro a actualizar.
     */
    @Override
	public void retrieve(final AgendaRecord record)
        throws SQLException {
        try (final PreparedStatement stmt = dbConnection.prepareStatement(
            SELECT_FROM_DIRECCION_SENTENCE)) {
            stmt.setInt(1, record.getKey());
            stmt.setInt(2, record.getVersionAddress());
            final ResultSet rset = stmt.executeQuery();
            final StringBuilder builder = new StringBuilder(AgendaRecord.ADDRESS_FIELD_LEN);
            while (rset.next()) {
                builder.append(rset.getString(1));
            }
            if (builder.length() > 0) {
                record.setAddress(builder.toString());
            }
        }
    }

    @Override
	public int persist(final AgendaRecord record) throws SQLException {
        try (final PreparedStatement stmt = dbConnection.prepareStatement(
            INSERT_INTO_DIRECCION_SENTENCE)) {
            int nrUpdates = 0;
            final int clave = record.getKey();
            final int newVersionDireccion = generalHelper.obtainNextVersion("DIRECCION", clave);
            final String direccion = record.getAddress();
            if (!isEmptyOrBlank(direccion)) {
                final int length = direccion.length();
                final int nrChunks = (int) Math.ceil((double) length / AgendaRecord.ADDRESS_FIELD_LEN);
                for (int ii = 1; ii <= nrChunks; ii++) {
                    stmt.setInt(1, clave);
                    stmt.setInt(2, newVersionDireccion);
                    stmt.setInt(3, ii);
                    stmt.setString(4, direccion.substring((ii - 1) * AgendaRecord.ADDRESS_FIELD_LEN,
                        Math.min(ii * AgendaRecord.ADDRESS_FIELD_LEN, length)));
                    nrUpdates += stmt.executeUpdate();
                }
            }
            return nrUpdates > 0 ? newVersionDireccion : record.getVersionAddress();
        }
    }
}
