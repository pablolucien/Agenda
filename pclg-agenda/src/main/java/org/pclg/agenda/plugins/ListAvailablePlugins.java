package org.pclg.agenda.plugins;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.runtime.RuntimeTools;
import org.pclg.tools.ToolBox;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

public class ListAvailablePlugins implements Plugin {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    /**
     * @param properties properties of the application (unused).
     * @param conn       la conexion a la base de datos (unused).
     * @param args       parameters to te class (unused).
     */
    @Override
    public void execute(final Properties properties, final Connection conn, final String... args) {
        try {
            final File path = RuntimeTools.getExecutionPath(getClass());
            LOGGER.log(Level.OFF, ">> Preprocesadores disponibles en " + path + " <<");
            final Class<?>[] clases = ToolBox.findClasses(Plugin.class, path);
            for (final Class<?> clase : clases) {
                LOGGER.log(Level.OFF, clase);
            /*
            // El problema con este approach es que instancia todas las clases que haya por el camino. FIXME: Buscar otra solución
            try {
//                final Plugin plugin = (Plugin) clase.newInstance();
                //System.out.println(clase);
//                System.out.println(plugin.getDescription());
            } catch (final IllegalAccessException ex) {
                ToolBox.showInfo(ex);
            } catch (final InstantiationException ex) {
                ToolBox.showInfo(ex);
            }
            */
            }
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
