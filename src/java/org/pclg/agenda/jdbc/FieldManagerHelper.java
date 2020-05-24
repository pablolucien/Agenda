package org.pclg.agenda.jdbc;

import org.pclg.agenda.entities.AgendaRecord;

import java.sql.SQLException;

/**
 * @author El Coyote Cojo
 * @since 5/10/18 17:58
 */
public interface FieldManagerHelper {
    /**
     * Obtiene y actualiza un contacto con el/los valor/es que le corresponden
     * de acuerdo con su clave y version.
     *
     * @param record el registro a actualizar.
     *
     * @throws SQLException si problemas haber.
     */
    void retrieve(final AgendaRecord record) throws SQLException;

    /**
     * Graba el los valores del campo gestionado de este regiatro.
     * @param record el registro a grabar.
     * @return la nueva version de este campo.
     *
     * @throws SQLException si problemas haber.
     */
    int persist(final AgendaRecord record) throws SQLException;
}
