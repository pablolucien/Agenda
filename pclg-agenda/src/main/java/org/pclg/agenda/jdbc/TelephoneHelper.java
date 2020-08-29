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
 * �Donald E. Knuth
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

    private static final String SELECT_FROM_TIPOTELEFONO =
        "SELECT Clave, NOMBRE, ForeGroundColor FROM TipoTelefono ORDER BY NOMBRE ";

    private static final String INSERT_INTO_TIPOTELEFONO =
        "INSERT INTO TIPOTELEFONO (Clave, Nombre, ForegroundColor) VALUES (?, ?, ?)";

    private final PreparedStatement selectStatement;
    private final PreparedStatement insertStatement;
    private final PreparedStatement selectTipoTelefonoStatement;
    private final PreparedStatement insertTipoTelefonoStatement;
    private final GeneralHelper generalHelper;

    TelephoneHelper(final Connection dbConnection, final GeneralHelper generalHelper) throws SQLException {
        selectStatement = dbConnection.prepareStatement(SELECT_FROM_TELEFONO_SENTENCE);
        insertStatement = dbConnection.prepareStatement(INSERT_INTO_TELEFONO_SENTENCE);
        selectTipoTelefonoStatement = dbConnection.prepareStatement(SELECT_FROM_TIPOTELEFONO);
        insertTipoTelefonoStatement = dbConnection.prepareStatement(INSERT_INTO_TIPOTELEFONO);
        this.generalHelper = generalHelper;
    }

    /**
     * Actualiza un contacto con los tel�fonos que le corresponden por su clave,
     * y version.
     *
     * @param record el registro a actualizar.
     */
    @Override
	public void retrieve(final AgendaRecord record)
        throws SQLException {
            final List<Telefono> telefonoList = new ArrayList<>();
            selectStatement.setInt(1, record.getKey());
            selectStatement.setInt(2, record.getVersionTelephone());
            final ResultSet rset = selectStatement.executeQuery();
            while (rset.next()) {
                telefonoList.add(
                    new Telefono(rset.getString(1), rset.getString(2), rset.getInt(3)));
            }
            record.setTelephones(telefonoList);
    }

    int addTipoTelefono(final String typeName, final int fgColor) throws SQLException {
        final int clave = generalHelper.obtainNextKey("TIPOTELEFONO");
            insertTipoTelefonoStatement.setInt(1, clave);
            insertTipoTelefonoStatement.setString(2, typeName);
            insertTipoTelefonoStatement.setInt(3, fgColor);
            insertTipoTelefonoStatement.execute();
            return clave;
    }

    @Override
	public int persist(final AgendaRecord record) throws SQLException {
            int secuencia = 0;
            int nrUpdates = 0;
            final int clave = record.getKey();
            final int newVersionTelefono = generalHelper.obtainNextVersion("TELEFONO", clave);
            for (final Telefono telefono : record.getTelephones()) {
                final String numero = telefono.getNumero();
                final String countryPrefix = telefono.getCountryPrefix();
                int ii = 0;
                if (!isEmptyOrBlank(numero)) {
                    insertStatement.setInt(++ii, clave);
                    insertStatement.setInt(++ii, newVersionTelefono);
                    insertStatement.setInt(++ii, ++secuencia);
                    insertStatement.setString(++ii, countryPrefix == null ? record.getCountry().getCountryCode() : countryPrefix); // FIXME: chapucilla hasta controlar bien countryPrefix
                    insertStatement.setString(++ii, numero);
                    final int tipo = telefono.getTipo();
                    if (tipo == 0) {
                        insertStatement.setNull(++ii, Types.INTEGER);
                    } else {
                        insertStatement.setInt(++ii, tipo);
                    }
                    nrUpdates += insertStatement.executeUpdate();
                }
            }
            return nrUpdates > 0 ? newVersionTelefono : record.getVersionTelephone();
    }

    List<TipoTelefono> getTelephoneTypes() throws SQLException {
        final List<TipoTelefono> telephoneTypes = new ArrayList<>(GeneralHelper.LIST_INITIAL_CAPACITY);
        try (final ResultSet rset = selectTipoTelefonoStatement.executeQuery()) {
            while (rset.next()) {
                final TipoTelefono tipoTelefono = new TipoTelefono(rset.getInt(1),
                    rset.getString(2), rset.getInt(3));
                telephoneTypes.add(tipoTelefono);
            }
        }
        return telephoneTypes;
    }
}
