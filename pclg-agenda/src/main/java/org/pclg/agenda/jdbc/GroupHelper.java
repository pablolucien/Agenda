package org.pclg.agenda.jdbc;

import org.apache.log4j.Logger;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.Grupo;
import org.pclg.log.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23/07/17 10:11
 */
final class GroupHelper implements FieldManagerHelper {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final Connection dbConnection;
    private final GeneralHelper generalHelper;

    private static final String INSERT_INTO_CONTACTO_GRUPO_SENTENCE =
        "INSERT INTO ContactoGrupo  (Clave, Version,  Secuencia, ClaveGrupo) "
            + "VALUES (?, ?, ?, ?)";

    private static final String SELECT_FROM_CONTACTO_GRUPO_SENTENCE =
        "SELECT ClaveGrupo FROM ContactoGrupo WHERE Clave = ? "
            + "AND Version = ? ORDER BY Secuencia";

    private static final String SELECT_ALL_GRUPOS =
        "SELECT Clave, NOMBRE FROM GRUPO ORDER BY NOMBRE ";

    private static final String INSERT_INTO_GRUPO =
        "INSERT INTO GRUPO (Clave, Nombre) VALUES (?, ?)";

    GroupHelper(final Connection dbConnection, final GeneralHelper generalHelper) {
        this.dbConnection = dbConnection;
        this.generalHelper = generalHelper;
    }

    @Override
	public int persist(final AgendaRecord record) throws SQLException {
        try (final PreparedStatement stmt = dbConnection.prepareStatement(
            INSERT_INTO_CONTACTO_GRUPO_SENTENCE)) {
            int secuencia = 0;
            int nrUpdates = 0;
            final int clave = record.getKey();
            final int newVersionContactoGrupo =
                generalHelper.obtainNextVersion("ContactoGrupo", clave);
            final List<Grupo> grupos = record.getGroups();
            final int len = grupos.size();
            for (int ii = 0; ii < len; ii++) {
                Grupo grupo = grupos.get(ii);
                if (grupo == null) {
                    continue;
                }
                if (grupo.getClave() == Grupo.INVALID_KEY) {
                    grupos.remove(ii);
                    final String nombre = grupo.getNombre();
                    final Grupo grupoTmp = Grupo.valueOf(nombre);
                    if (grupoTmp == null) {
                        Grupo.add(grupo = new Grupo(addGroup(nombre), nombre));
                    } else {
                        grupo = grupoTmp;
                    }
                    grupos.add(ii, grupo);
                }
                stmt.setInt(1, clave);
                stmt.setInt(2, newVersionContactoGrupo);
                stmt.setInt(3, ++secuencia);
                stmt.setInt(4, grupo.getClave());
                nrUpdates += stmt.executeUpdate();
            }
            return nrUpdates > 0 ? newVersionContactoGrupo : record.getVersionGroup();
        } catch (final NullPointerException ex) {
            LOGGER.error("Error salvando los grupos de " + record.getFirstname() + ' ' + record.getLastname(), ex);
            throw ex;
        }
    }

    int addGroup(final String groupName) throws SQLException {
        final int clave = generalHelper.obtainNextKey("GRUPO");
        try (final PreparedStatement pstmt = dbConnection.prepareStatement(
            INSERT_INTO_GRUPO)) {
            pstmt.setInt(1, clave);
            pstmt.setString(2, groupName);
            pstmt.execute();
            dbConnection.commit();
            return clave;
        }
    }

    /** Actualiza un contacto con los teléfonos que le corresponden por su clave,
   	 *  y version.
   	 * @param record el registro a actualizar.
   	 */
    @Override
	public void retrieve(final AgendaRecord record) throws SQLException {
        try (final PreparedStatement statement = dbConnection.prepareStatement(
                SELECT_FROM_CONTACTO_GRUPO_SENTENCE)) {
            final List<Grupo> groups = new ArrayList<>();
            statement.setInt(1, record.getKey());
            statement.setInt(2, record.getVersionGroup());
            final ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                groups.add(Grupo.getGrupo(rset.getInt(1)));
            }
            record.setGroups(groups);
        }
    }

    List<Grupo> getGroups() throws SQLException {
        final List<Grupo> grupos = new ArrayList<>(GeneralHelper.LIST_INITIAL_CAPACITY);
        try (final PreparedStatement pstmt = dbConnection.prepareStatement(
                SELECT_ALL_GRUPOS);
                final ResultSet rset = pstmt.executeQuery()) {
            while (rset.next()) {
                final Grupo grupo = new Grupo(rset.getInt(1), rset.getString(2));
                grupos.add(grupo);
            }
        }
        return grupos;
    }
}
