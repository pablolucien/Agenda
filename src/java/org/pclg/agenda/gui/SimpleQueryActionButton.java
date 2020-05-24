package org.pclg.agenda.gui;

import org.apache.log4j.Logger;
import org.pclg.gui.I18NManager;
import org.pclg.log.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Properties;

import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * Una clase de acción que ejecuta el listado de los contactos con una
 * query simple (sin parámetros ni set-up.
 * @since 2016.03.01
 */
final class SimpleQueryActionButton extends JButton {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = 8064010355934180967L;

    /**
     * Una clase de acción que ejecuta el listado de los contactos con una
     * query simple (sin parámetros ni set-up.
     * @since 2016.03.01
     */
    private final class SimpleQueryAction extends AbstractAction {
        private static final long serialVersionUID = -1641728863085868828L;
        private final AgendaGUI.ListCriterium criterium;
        private final SimpleQueryExecutor executor;

        SimpleQueryAction(final AgendaGUI.ListCriterium criterium, final SimpleQueryExecutor executor) {
            this.criterium = criterium;
            this.executor = executor;
        }

        @Override
        public void actionPerformed(final ActionEvent ev) {
            try {
                executor.showData(criterium);
            } catch (final SQLException ex) {
                LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            }
        }
    }

    /**
     * Creates a button where properties are taken from the
     * <code>Action</code> supplied.
     *
     * @param criterium the <code>Action</code> used to specify the new button
     * @param i18nManager el manager a usar
     * @param properties de dónde sacar los textos.
	 * @since 1.3
     */
    SimpleQueryActionButton(final AgendaGUI.ListCriterium criterium, final SimpleQueryExecutor executor,
		    final I18NManager i18nManager, final Properties properties) {
        final Action action = new SimpleQueryAction(criterium, executor);
        final String name = criterium.name();
        final String keyLabel = "AgendaGUI." + name + "Button";
        final String keyIcon = "AgendaGUI." + name + "Image";
        i18nManager.configureI18NAction(action, keyLabel, keyIcon);
        setAction(action);
        setToolTipText(getStringFromProperties(properties, keyLabel));
    }
}
