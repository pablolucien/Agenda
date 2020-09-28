package org.pclg.agenda.jdbc;

import org.apache.logging.log4j.Logger;
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
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23/07/17 10:11
 */
final class GroupHelper implements FieldManagerHelper {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final PreparedStatement selectFromGrupoStatement;
    private final PreparedStatement insertIntoGrupoStatement;
    private final PreparedStatement selectFromContactoGrupoStatement;
    private final PreparedStatement insertIntoContactoGrupoStatement;
    private final GeneralHelper generalHelper;

    private static final String INSERT_INTO_CONTACTO_GRUPO_SENTENCE =
        "INSERT INTO ContactoGrupo  (Clave, Version,  Secuencia, ClaveGrupo) "
            + "VALUES (?, ?, ?, ?)";

    private static final String SELECT_FROM_CONTACTO_GRUPO_SENTENCE =
        "SELECT ClaveGrupo FROM ContactoGrupo WHERE Clave = ? "
            + "AND Version = ? ORDER BY Secuencia";

    private static final String SELECT_FROM_GRUPO =
        "SELECT Clave, NOMBRE FROM GRUPO ORDER BY NOMBRE ";

    private static final String INSERT_INTO_GRUPO =
        "INSERT INTO GRUPO (Clave, Nombre) VALUES (?, ?)";

    GroupHelper(final Connection dbConnection, final GeneralHelper generalHelper) throws SQLException {
        selectFromGrupoStatement = dbConnection.prepareStatement(SELECT_FROM_GRUPO);
        insertIntoGrupoStatement = dbConnection.prepareStatement(INSERT_INTO_GRUPO);
        selectFromContactoGrupoStatement = dbConnection.prepareStatement(SELECT_FROM_CONTACTO_GRUPO_SENTENCE);
        insertIntoContactoGrupoStatement = dbConnection.prepareStatement(INSERT_INTO_CONTACTO_GRUPO_SENTENCE);
        this.generalHelper = generalHelper;
    }

    @Override
    public int persist(final AgendaRecord record) throws SQLException {
        try {
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
                insertIntoContactoGrupoStatement.setInt(1, clave);
                insertIntoContactoGrupoStatement.setInt(2, newVersionContactoGrupo);
                insertIntoContactoGrupoStatement.setInt(3, ++secuencia);
                insertIntoContactoGrupoStatement.setInt(4, grupo.getClave());
                nrUpdates += insertIntoContactoGrupoStatement.executeUpdate();
            }
            return nrUpdates > 0 ? newVersionContactoGrupo : record.getVersionGroup();
        } catch (final NullPointerException ex) {
            LOGGER.error("Error salvando los grupos de " + record.getFirstname() + ' ' + record.getLastname(), ex);
            throw ex;
        }
    }

    int addGroup(final String groupName) throws SQLException {
        final int clave = generalHelper.obtainNextKey("GRUPO");
        insertIntoGrupoStatement.setInt(1, clave);
        insertIntoGrupoStatement.setString(2, groupName);
        insertIntoGrupoStatement.execute();
        return clave;
    }

    /**
     * Actualiza un contacto con los tel�fonos que le corresponden por su clave,
     * y version.
     *
     * @param record el registro a actualizar.
     */
    @Override
    public void retrieve(final AgendaRecord record) throws SQLException {
        final List<Grupo> groups = new ArrayList<>();
        selectFromContactoGrupoStatement.setInt(1, record.getKey());
        selectFromContactoGrupoStatement.setInt(2, record.getVersionGroup());
        final ResultSet rset = selectFromContactoGrupoStatement.executeQuery();
        while (rset.next()) {
            groups.add(Grupo.getGrupo(rset.getInt(1)));
        }
        record.setGroups(groups);
    }

    List<Grupo> getGroups() throws SQLException {
        final List<Grupo> grupos = new ArrayList<>(GeneralHelper.LIST_INITIAL_CAPACITY);
        try (final ResultSet rset = selectFromGrupoStatement.executeQuery()) {
            while (rset.next()) {
                final Grupo grupo = new Grupo(rset.getInt(1), rset.getString(2));
                grupos.add(grupo);
            }
        }
        return grupos;
    }
}
