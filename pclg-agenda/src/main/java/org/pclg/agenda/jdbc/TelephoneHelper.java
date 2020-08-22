package org.pclg.agenda.jdbc;

import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.Telefono;
import org.pclg.agenda.entities.TipoTelefono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23/07/17 10:36
 */
public final class TelephoneHelper implements FieldManagerHelper {
    private static final String INSERT_INTO_TELEFONO_SENTENCE =
        "INSERT INTO TELEFONO (Clave, Version,  Secuencia, countryPrefix, Numero, Tipo) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_FROM_TELEFONO_SENTENCE =
        "SELECT countryPrefix, Numero, Tipo FROM TELEFONO WHERE Clave = ? AND Version = ? "
            + GeneralHelper.ORDER_BY_SECUENCIA_CLAUSE;

    private static final String SELECT_ALL_TIPOS_TELEFONO =
        "SELECT Clave, NOMBRE, ForeGroundColor FROM TipoTelefono ORDER BY NOMBRE ";

    private static final String INSERT_INTO_TIPOTELEFONO =
        "INSERT INTO TIPOTELEFONO (Clave, Nombre, ForegroundColor) VALUES (?, ?, ?)";

    private final Connection dbConnection;
    private final GeneralHelper generalHelper;

    TelephoneHelper(final Connection dbConnection, final GeneralHelper generalHelper) {
        this.dbConnection = dbConnection;
        this.generalHelper = generalHelper;
    }

    /**
     * Actualiza un contacto con los teléfonos que le corresponden por su clave,
     * y version.
     *
     * @param record el registro a actualizar.
     */
    @Override
	public void retrieve(final AgendaRecord record)
        throws SQLException {
        try (final PreparedStatement statement = dbConnection.prepareStatement(SELECT_FROM_TELEFONO_SENTENCE)) {
            final List<Telefono> telefonoList = new ArrayList<>();
            statement.setInt(1, record.getKey());
            statement.setInt(2, record.getVersionTelephone());
            final ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                telefonoList.add(
                    new Telefono(rset.getString(1), rset.getString(2), rset.getInt(3)));
            }
            record.setTelephones(telefonoList);
        }
    }

    int addTipoTelefono(final String typeName, final int fgColor) throws SQLException {
        final int clave = generalHelper.obtainNextKey("TIPOTELEFONO");
        try (final PreparedStatement pstmt = dbConnection.prepareStatement(
                INSERT_INTO_TIPOTELEFONO)) {
            pstmt.setInt(1, clave);
            pstmt.setString(2, typeName);
            pstmt.setInt(3, fgColor);
            pstmt.execute();
            dbConnection.commit();
            return clave;
        }
    }

    @Override
	public int persist(final AgendaRecord record) throws SQLException {
        try (final PreparedStatement stmt = dbConnection.prepareStatement(INSERT_INTO_TELEFONO_SENTENCE)) {
            int secuencia = 0;
            int nrUpdates = 0;
            final int clave = record.getKey();
            final int newVersionTelefono = generalHelper.obtainNextVersion("TELEFONO", clave);
            for (final Telefono telefono : record.getTelephones()) {
                final String numero = telefono.getNumero();
                final String countryPrefix = telefono.getCountryPrefix();
                int ii = 0;
                if (!isEmptyOrBlank(numero)) {
                    stmt.setInt(++ii, clave);
                    stmt.setInt(++ii, newVersionTelefono);
                    stmt.setInt(++ii, ++secuencia);
                    stmt.setString(++ii, countryPrefix == null ? record.getCountry().getCountryCode() : countryPrefix); // FIXME: chapucilla hasta controlar bien countryPrefix
                    stmt.setString(++ii, numero);
                    final int tipo = telefono.getTipo();
                    if (tipo == 0) {
                        stmt.setNull(++ii, Types.INTEGER);
                    } else {
                        stmt.setInt(++ii, tipo);
                    }
                    nrUpdates += stmt.executeUpdate();
                }
            }
            return nrUpdates > 0 ? newVersionTelefono : record.getVersionTelephone();
        }
    }

    List<TipoTelefono> getTelephoneTypes() throws SQLException {
        final List<TipoTelefono> telephoneTypes = new ArrayList<>(GeneralHelper.LIST_INITIAL_CAPACITY);
        try (final PreparedStatement pstmt = dbConnection.prepareStatement(
                SELECT_ALL_TIPOS_TELEFONO);
            final ResultSet rset = pstmt.executeQuery()) {
            while (rset.next()) {
                final TipoTelefono tipoTelefono = new TipoTelefono(rset.getInt(1),
                    rset.getString(2), rset.getInt(3));
                telephoneTypes.add(tipoTelefono);
            }
        }
        return telephoneTypes;
    }
}
