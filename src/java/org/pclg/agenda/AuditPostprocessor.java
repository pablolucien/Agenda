package org.pclg.agenda;


import org.apache.log4j.Logger;
import org.jpatterns.gof.CommandPattern;
import org.pclg.dbutil.VersatileProcessor;
import org.pclg.log.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author El Coyote Cojo.
 * @since 02/12/2016.
 */
@CommandPattern.ConcreteCommand
public class AuditPostprocessor implements VersatileProcessor {
    /** El logger. */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final Connection connection;

    public AuditPostprocessor(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void go() {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE CONTROL SET FechaActualizacion = ?")){
            statement.setTimestamp(1, new Timestamp(new Date().getTime()));
            statement.execute();
        } catch (final SQLException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
    }
}
