package org.pclg.agenda.jdbc;

import org.pclg.agenda.entities.Pais;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23/07/17 10:48
 */
final class CountryHelper {
    private static final String SELECT_FROM_PAIS =
        "SELECT CODIGO, NOMBRE, FORMATO_TELEFONO FROM PAIS";

    private static final String INSERT_INTO_PAIS =
        "INSERT INTO PAIS (Codigo, Nombre, Formato_Telefono) VALUES (?, ?, ?)";

    private static final String UPDATE_PAIS =
        "UPDATE PAIS SET Nombre = ?, Formato_Telefono = ? WHERE Codigo = ?";

    private final Connection dbConnection;

    CountryHelper(final Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    void loadCountries() throws SQLException {
        try (final PreparedStatement stmt = dbConnection.prepareStatement(SELECT_FROM_PAIS);
             final ResultSet rset = stmt.executeQuery()) {
            while (rset.next()) {
                new Pais(rset.getString(1), rset.getString(2), rset.getString(3));
            }
        }

    }

    void addCountry(final String code, final String name, final String phoneMask) throws SQLException {
        try (final PreparedStatement pstmt = dbConnection.prepareStatement(INSERT_INTO_PAIS)) {
            persist(pstmt, code, name, phoneMask);
        }

    }

    void updateCountry(final String code, final String name, final String phoneMask) throws SQLException {
        try (final PreparedStatement pstmt = dbConnection.prepareStatement(UPDATE_PAIS)) {
            persist(pstmt, name, phoneMask, code);
        }
    }

    private void persist(final PreparedStatement pstmt, final String code, final String name,
                         final String phoneMask) throws SQLException {
        int ii = 0;
        pstmt.setString(++ii, code);
        pstmt.setString(++ii, name);
        pstmt.setString(++ii, phoneMask);
        pstmt.execute();
        dbConnection.commit();
    }
}
