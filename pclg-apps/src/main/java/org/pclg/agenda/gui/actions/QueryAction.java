package org.pclg.agenda.gui.actions;

import org.apache.log4j.Logger;
import org.pclg.agenda.gui.AgendaGUI;
import org.pclg.agenda.gui.SimpleQueryExecutor;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;
import org.pclg.tools.StringTools;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.pclg.tools.PropertiesHelper.getIntFromProperties;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;
import static org.pclg.tools.StringTools.isEmptyOrBlank;

public final class QueryAction extends AbstractAction {
    private static final long serialVersionUID = -8894458740981171757L;
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final String CUSTOM_QUERY_KEY_BASE_NAME = "QueryAction.query.";
    private static final String CUSTOM_QUERY_KEY_ORDER_BY_BASE_NAME = "QueryAction.orderBy.";
    private final Component parent;
    private final Properties properties;
    private final transient SimpleQueryExecutor executor;
    private final int numberOfQueries;
    private final JPanel queryPanel = new JPanel(new GridLayout(2, 1));
    private final JComboBox<String> orderValues;
    private final JComboBox<String> queriesValues;


    public QueryAction(final Component parent, final Properties properties,
        final SimpleQueryExecutor executor) {
        this.parent = parent;
        this.properties = properties;
        this.executor = executor;
        numberOfQueries = getIntFromProperties(properties, "QueryAction.maxHistory", 10);
        queriesValues = new JComboBox<>(getItems(CUSTOM_QUERY_KEY_BASE_NAME));
        queriesValues.setEditable(true);
        if (queriesValues.getItemCount() > 0) {
            queriesValues.setSelectedIndex(0);
        }
        orderValues = new JComboBox<>(getItems(CUSTOM_QUERY_KEY_ORDER_BY_BASE_NAME));
        orderValues.insertItemAt("", 0);
        orderValues.setSelectedIndex(0);
        orderValues.setEditable(true);
        queryPanel.add(queriesValues);
        queryPanel.add(orderValues);
    }

    private Vector<String> getItems(final String keyBaseName) {
        final Vector<String> vector = new Vector<>(numberOfQueries);
        for (int ii = 0; ii < numberOfQueries; ii++) {
            final String query = properties.getProperty(keyBaseName + ii);
            if (!isEmptyOrBlank(query) && !vector.contains(query)) {
                vector.add(query);
            }
        }
        return vector;
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        GUITools.setDialogResizable(queryPanel);
        if (JOptionPane.showOptionDialog(parent, queryPanel,
            getStringFromProperties(properties,
                "AgendaGUI.customQueryPrompt"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            ImageTools.getImageIcon(getStringFromProperties(properties,
                    "AgendaGUI.customQueryDialogImage")).orElse(null),
            null, null) == JOptionPane.YES_OPTION) {
            final String selectedQuery = getSelectedOption(queriesValues);
            final String selectedOrderBy = getSelectedOption(orderValues);
            if (!isEmptyOrBlank(selectedQuery)) {
                executor.setQuery(selectedQuery);
                if (!isEmptyOrBlank(selectedOrderBy)) {
                    executor.setOrderBy(selectedOrderBy);
                }
                try {
                    executor.showData(AgendaGUI.ListCriterium.customQuery);
                } catch (final SQLException ex) {
                    LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                    showMessageDialog(parent, StringTools.wrapLine(ex.getMessage()),
                        getStringFromProperties(properties, "Agenda.alert.title"),
                        ERROR_MESSAGE);
                }
            }
        }
    }

    private static String getSelectedOption(final JComboBox<String> comboBox) {
        final String selectedItem = (String) comboBox.getSelectedItem();
        if (!isEmptyOrBlank(selectedItem)) {
            comboBox.removeItem(selectedItem);
            comboBox.insertItemAt(selectedItem, 0);
            comboBox.setSelectedIndex(0);
        }
        return selectedItem;
    }

    public void saveYourProperties(final Properties guiProperties) {
        guiProperties.setProperty("QueryAction.maxHistory", String.valueOf(numberOfQueries));
        storeValues(guiProperties, queriesValues, CUSTOM_QUERY_KEY_BASE_NAME);
        storeValues(guiProperties, orderValues, CUSTOM_QUERY_KEY_ORDER_BY_BASE_NAME);
    }

    private void storeValues(final Properties properties, final JComboBox<String> comboBox, final String keyBaseName) {
        properties.stringPropertyNames().stream()
            .filter(key -> key.startsWith(keyBaseName))
            .forEach(properties::remove);

        int index = 0;
        for (int ii = 0, nn = comboBox.getItemCount(); ii < numberOfQueries && ii < nn; ii++) {
            final String value = comboBox.getItemAt(ii);
            if (!isEmptyOrBlank(value)) {
                properties.setProperty(keyBaseName + index++, value);
            }
        }
    }
}