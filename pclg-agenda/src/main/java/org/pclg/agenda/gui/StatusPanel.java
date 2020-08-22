package org.pclg.agenda.gui;

import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaDb;
import org.pclg.gui.I18NManager;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ObservableProperties;
import org.pclg.tools.PropertiesHelper;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

final class StatusPanel extends JPanel {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = 8984335398984738396L;
    private final AgendaDb agendaDb;
    private final JLabel statusLine = new JLabel();
    private String linePattern;
    private String datePattern;

    StatusPanel(final AgendaDb agendaDb, final Properties properties) {
        this.agendaDb = agendaDb;
        linePattern = PropertiesHelper.getStringFromProperties(properties, "StatusPane.linePattern");
        datePattern = PropertiesHelper.getStringFromProperties(properties, "Agenda.datePattern");
        setLocale(new Locale(getStringFromProperties(properties, "Application.ForceLanguage")));
        if (properties instanceof ObservableProperties) {
            ((ObservableProperties) properties).addChangeObserver(observableProperties -> {
                linePattern = PropertiesHelper.getStringFromProperties(observableProperties, "StatusPane.linePattern");
                datePattern = PropertiesHelper.getStringFromProperties(observableProperties, "Agenda.datePattern");
                setLocale(new Locale(getStringFromProperties(properties, "Application.ForceLanguage")));
            });
        }
        setLayout(new BorderLayout());
        final I18NManager i18nManager = I18NManager.getInstance(properties);
        add(i18nManager.configureI18NComponent(new JLabel(), "StatusPane.title"), BorderLayout.WEST);
        add(statusLine, BorderLayout.CENTER);
    }

    public void update() {
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, getLocale());
            final Date lastUpdated = agendaDb.lastUpdated().orElse(null);
            final String lastUpdatedStr = lastUpdated == null ? "N.P.I." :
                dateFormat.format(lastUpdated);
            statusLine.setText(MessageFormat.format(linePattern,
                dateFormat.format(new Date()), lastUpdatedStr));
        } catch (final SQLException ex) {
            LOGGER.error("Updating status line", ex);
        }
    }
}
