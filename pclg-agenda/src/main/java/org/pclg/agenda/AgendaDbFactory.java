package org.pclg.agenda;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jpatterns.gof.AbstractFactoryPattern;
import org.pclg.log.LoggerFactory;

import java.util.Properties;

/**
 * @author El Coyote Cojo
 * @since 14-sep-2007 12:49:18
 */
@AbstractFactoryPattern.ConcreteFactory
public final class AgendaDbFactory {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	private AgendaDbFactory() {
	}

	public static AgendaDb getAgendaDb(final Properties appProperties) {
		AgendaDb agendaDb = null;
		try {
			agendaDb = (AgendaDb) Class.forName(appProperties
					.getProperty("AgendaDb.className")).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
			LOGGER.log(Level.ERROR, LoggerFactory.ERROR_TAG, ex);
		}
		return agendaDb;
	}
}
