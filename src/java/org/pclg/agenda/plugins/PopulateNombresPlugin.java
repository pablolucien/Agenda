package org.pclg.agenda.plugins;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class PopulateNombresPlugin implements Plugin {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    @Override
    public void execute(final Properties properties, final Connection conn, final String... args) throws SQLException {
        final String sqlSelect = "SELECT C.Nombre, C.Apellido FROM root.contacto C";

        try (final Statement stmt = conn.createStatement(
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE);
             final ResultSet rset = stmt.executeQuery(sqlSelect)) {
            while (rset.next()) {
                int ii = 0;
                final String nombre = rset.getString(++ii);
                final String apellido = rset.getString(++ii);
                doStuff(conn, nombre, apellido);
            }
        }
    }

    /**
     * Inserta en la tabla nombres y actualiza la tabla contactos.
     */
    private void doStuff(final Connection conn, final String nombre, final String apellido) {
        LOGGER.debug("conn = [" + conn + "], nombre = [" + nombre + "], apellido = [" + apellido + "]");
    }
}
