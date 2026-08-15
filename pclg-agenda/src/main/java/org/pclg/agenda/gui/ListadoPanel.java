package org.pclg.agenda.gui;

import org.apache.logging.log4j.Logger;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.AgendaUtil;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ChangeObserver;
import org.pclg.tools.GUITools;
import org.pclg.tools.ObservableProperties;
import org.pclg.tools.StringTools;
import org.pclg.xtras.Colortable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.MonthDay;
import java.util.List;
import java.util.Properties;

import static org.pclg.agenda.gui.AgendaGUI.ListCriterium;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;

/**
 * Premature optimization is the root of all evil.
 * -Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 27/11/16 10:08
 */
public final class ListadoPanel extends JPanel implements SimpleQueryExecutor, ChangeObserver<ObservableProperties> {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    @Serial
    private static final long serialVersionUID = -5079235848385292907L;
    private final AgendaGUI owner;
    private final AgendaDb agendaDb;
    private final Properties properties;
    private final DataTable dataTable;
    /** Cantidad de registros que se están mostrando. */
   	private int recordsSize;
    private final JLabel reportTitle = new JLabel();
    private final JLabel recordsCount = new JLabel();

    /**
     * Último criterio de listado usado.
     */
    private ListCriterium currentListCriterium;
    private String recordFilter;
    private GroupSelectionPanel groupSelectionPanel;
    private String query;
    private String orderByClause;
    private String recordsCountPattern;

    private class TableMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
//			if (mouseEvent.getClickCount() == 1 && dataTable.getColumnClass(dataTable.getSelectedColumn()) == ImageIcon.class) {
//				final ImageIcon icon = (ImageIcon) agendaTableModel.getValueAt(
//						dataTable.getSelectedRow(),
//						dataTable.getSelectedColumn());
//				JOptionPane.showMessageDialog(AgendaGUI.this, icon, "Imagen", JOptionPane.INFORMATION_MESSAGE);
//			}

            if (mouseEvent.getClickCount() == 2) {
                showSelectedRecords();
            }
        }
    }

    private class TableKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            final int keyCode = keyEvent.getKeyCode();
            if (keyCode == KeyEvent.VK_DELETE) {
                final int[] selectedRows = dataTable.getSelectedRows();
                // Hay que hacerlo a la visconversa para mantener la consistencia
                // en los índices.
                for (int ii = selectedRows.length - 1; ii >= 0; ii--) {
                    final AgendaRecord record = dataTable.getValueAt(selectedRows[ii]);
                    try {
                        agendaDb.markAsDeleted(record.getKey());
                        dataTable.removeDataAt(selectedRows[ii]);
                    } catch (final SQLException ex) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                    }
                }
            } else if (keyCode == KeyEvent.VK_ENTER) {
                showSelectedRecords();
            }
        }
    }

    /**
     * @param owner La conexión con el usuario.
     * @param agendaDb La conexión con los datos.
     * @param properties the properties
     */
    public ListadoPanel(final AgendaGUI owner, final AgendaDb agendaDb, final Properties properties) {
        this.owner = owner;
        this.agendaDb = agendaDb;
        this.properties = properties;
        setLayout(new BorderLayout());

        dataTable = new DataTable(properties);
        dataTable.addMouseListener(new TableMouseListener());
        dataTable.addKeyListener(new TableKeyListener());
        dataTable.hideMarcasColumn();

        add(reportTitle, BorderLayout.NORTH);
        add(new JScrollPane(dataTable), BorderLayout.CENTER);
        add(recordsCount, BorderLayout.SOUTH);
        recordsCountPattern = getStringFromProperties(properties, "ListadoPane.recordCount");

        if (properties instanceof ObservableProperties) {
            ((ObservableProperties) properties).addChangeObserver(this);
        }
    }

    @Override
    public void setQuery(final String query) {
        this.query = query;
    }

    @Override
    public void setOrderBy(final String orderByClause) {
        if (!StringTools.isEmptyOrBlank(orderByClause)) {
            this.orderByClause = orderByClause;
        }
    }

    @Override
    public void setRecordFilter(final String recordFilter) {
        this.recordFilter = recordFilter;
    }

    @Override
    public void setGroupSelectionPanel(final GroupSelectionPanel groupSelectionPanel) {
        this.groupSelectionPanel = groupSelectionPanel;
    }

    /**
     * Muestra los datos según el criterio de búsqueda que se le pase.
     *
     * @param listCriterium el criterio de búsqueda.
     * @throws SQLException si ocurre algún error.
     */
    @Override
    public void showData(final ListCriterium listCriterium) throws SQLException {
        dataTable.clear();
        dataTable.fireTableDataChanged();
        dataTable.hideMarcasColumn();
        final List<AgendaRecord> records = switch (listCriterium) {
            case byDate -> agendaDb.selectAllContactsByBirthday();
            case byInterestingDate -> agendaDb.selectContactsByInterestingDates();
            case byName -> agendaDb.selectAllContactsByName();
            case byMark -> {
                dataTable.showMarcasColumn();
                yield agendaDb.selectSpecialContacts();
            }
            case byBirthday -> agendaDb.selectContactsByNearBirthday();
            case deleted -> agendaDb.selectAllDeletedContactsByName();
            case byGroup -> agendaDb.selectContactsByGroups(
                groupSelectionPanel.getSelectedValues(),
                groupSelectionPanel.isSelectionIntersection(),
                groupSelectionPanel.isNegation());
            case recentlyModified -> agendaDb.selectRecentlyModifiedContacts();
            case filtered -> agendaDb.selectFilteredContacts(recordFilter);
            case customQuery -> agendaDb.selectContactsByCustomQuery(query, orderByClause);
        };

        currentListCriterium = listCriterium;
        updateTitle();

        int firstRow2Select = -1;
        int lastRow2Select = -1;
        boolean foundFirst2Select = false;
        MonthDay selectedDate = MonthDay.now();
        for (final AgendaRecord record : records) {
            dataTable.addRecord(record);
            if (listCriterium == ListCriterium.byDate
                || listCriterium == ListCriterium.byBirthday) {
                final MonthDay birthday = MonthDay.of(record.getMonth(), record.getDay());
                if (!foundFirst2Select) {
                    lastRow2Select = ++firstRow2Select;
                    if (!birthday.isBefore(selectedDate)) {
                        foundFirst2Select = true;
                        record.setHighlighted(true);
                        selectedDate = birthday;
                    }
                } else {
                    if (birthday.equals(selectedDate)) {
                        record.setHighlighted(true);
                        lastRow2Select++;
                    }
                }
            } else if (listCriterium == ListCriterium.byInterestingDate) {
                final AgendaUtil.AgeInfo ageInfo = AgendaUtil
                    .computeAgeAndDays(record.getDay(), record.getMonth() - 1, record.getYear());
				final int interestingDatesDivisor = Integer.parseInt(properties
					.getProperty("AgendaDb.interestingDatesDivisor", "1000"));
				final int daysModDivisor = ageInfo.getDays() % interestingDatesDivisor;
                if (daysModDivisor == 0 || daysModDivisor > interestingDatesDivisor - 3) {
                    record.setHighlighted(true);
                }
            }
        }

        dataTable.fireTableDataChanged();

        if ((listCriterium == ListCriterium.byDate
            || listCriterium == ListCriterium.byBirthday)
            && firstRow2Select >= 0 && lastRow2Select >= firstRow2Select) {
            final Rectangle cellRect =
                dataTable.getCellRect(firstRow2Select, 0, true);
            cellRect.height *= 10; // Ver al menos 10 filas
            dataTable.scrollRectToVisible(cellRect);
        }

		recordsSize = records.size();
        recordsCount.setText(MessageFormat.format(recordsCountPattern,
            Integer.valueOf(recordsSize)));
        if (recordsSize == 1) {
            owner.showRecord(dataTable.getValueAt(0));
        }
    }

    /**
     * Actualiza el título de la primera pestaña del panel. Es necesario tenerlo
     * separado de showData(final LIST_CRITERIA listCriterium) para poder
     * actualizarlo al cambiar el idioma :(
     */
    private void updateTitle() {
        final String title;
        switch (currentListCriterium) {
            case byDate:
            case byName:
            case byMark:
            case byBirthday:
            case deleted:
            case byGroup:
                title = getStringFromProperties(properties,
                    "AgendaGUI." + currentListCriterium.name() + "Button");
                reportTitle.setForeground(Colortable.getColor("chocolate"));
                break;
            case byInterestingDate:
                title = getStringFromProperties(properties,
                    "AgendaGUI.byInterestingDateButton") + " - ("
                    + properties.getProperty(
                    "AgendaDb.daysFork4InterestingDates", "10") + ')';
                reportTitle.setForeground(Colortable.getColor("orange"));
                break;
            case recentlyModified:
                title = getStringFromProperties(properties,
                    "AgendaGUI.recentlyModifiedButton") + " - ("
                    + properties.getProperty(
                    "AgendaDb.daysFork4RecentlyModified", "1") + ')';
                reportTitle.setForeground(Colortable.getColor("honeydew1"));
                break;
            case filtered:
                title = getStringFromProperties(properties,
                    "AgendaGUI.filteredButton") + " - " + recordFilter;
                reportTitle.setForeground(Colortable.getColor("royalblue3"));
                break;
            case customQuery:
                title = getStringFromProperties(properties,
                    "AgendaGUI.customQueryButton") + " - " + query;
                reportTitle.setForeground(Colortable.getColor("navy"));
                break;
            default:
                throw new IllegalStateException("Criterium " + currentListCriterium
                    + " unknown");
        }
        reportTitle.setText(title);
    }

    private void showSelectedRecords() {
        final AgendaRecord[] records = getSelectedRecords();
        owner.showSelectedRecords(records);
    }

    public AgendaRecord[] getSelectedRecords() {
        final int[] selectedRows = dataTable.getSelectedRows();
        final AgendaRecord[] records = new AgendaRecord[selectedRows.length];
        for (int ii = 0; ii < selectedRows.length; ii++) {
            records[ii] = dataTable.getValueAt(selectedRows[ii]);
        }
        return records;
    }

    void refreshRecord(final AgendaRecord record) {
        dataTable.refreshRecord(record);
    }

    void saveYourProperties(final Properties guiProperties) {
        GUITools.saveTableProperties(dataTable, guiProperties);
    }

    @Override
    public void objectChanged(final ObservableProperties observableProperties) {
        SwingUtilities.invokeLater(() -> {
            updateTitle();
            recordsCountPattern = getStringFromProperties(properties,
                "ListadoPane.recordCount");
            recordsCount.setText(MessageFormat.format(recordsCountPattern,
                Integer.valueOf(recordsSize)));
        });
    }
}
