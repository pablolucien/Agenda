package org.pclg.agenda.gui;

import org.apache.logging.log4j.Logger;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;
import org.pclg.tools.ObservableProperties;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.io.Serial;
import java.util.Enumeration;
import java.util.Properties;

final class DataTable extends JTable {
	static final Color HIGHLIGHT_COLOR = Color.blue;
	static final Color NORMAL_FOREGROUND = Color.black;
	static final Color SELECTION_FOREGROUND = Color.red;
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    @Serial
    private static final long serialVersionUID = -6155327035653191656L;
    private final AgendaTableModel tableModel;
	private final GUITools.ColumnControl columnControl;
	private final TableColumn columnMarcas;
	/** Indica si la columna 'Marcas' es visible o no. */
    private boolean showingMarcasColumn;
	
	DataTable(final Properties properties) {
        tableModel = new AgendaTableModel(properties);
        setModel(tableModel);
		if (properties instanceof ObservableProperties) {
			((ObservableProperties) properties).addChangeObserver(props -> {
                tableModel.setUpLanguage(props);
              		for (int ii = 0, columnCount = tableModel.getColumnCount(); ii < columnCount; ii++) {
              			final int columnIndex = convertColumnIndexToView(ii);
              			if (columnIndex >= 0) {
                              columnModel.getColumn(columnIndex).setHeaderValue(tableModel.getColumnName(ii));
              			}
              		}
              		tableHeader.repaint();

            });
		}
        final Enumeration<TableColumn> en = columnModel.getColumns();
        while (en.hasMoreElements()) {
            en.nextElement().setCellRenderer(
                new AgendaRecordCellRenderer(tableModel));
        }

        setBackground(Color.lightGray);
		setForeground(NORMAL_FOREGROUND);
		setSelectionForeground(SELECTION_FOREGROUND);
        setTableRowSorter();
        setUpCellRenderers(properties);
		// Hay que instanciarlo antes de llamar a setUpColumns();
		columnControl = GUITools.addColumnControlMenu(this, this, properties);
        columnMarcas =  getColumnModel().getColumn(AgendaTableModel.MARCAS_COLUMN_INDEX);
        GUITools.setUpColumns(this, properties, columnControl);
	}

    private void setTableRowSorter() {
        try {
            final TableRowSorter<AgendaTableModel> rowSorter
                    = new AgendaTableRowSorter(tableModel);
            rowSorter.setComparator(AgendaTableModel.DATE_COLUMN_INDEX,
                (AgendaRecord r1, AgendaRecord r2) -> r1.getMonth() == r2.getMonth() ?
                    r1.getDay() - r2.getDay() : r1.getMonth() - r2.getMonth());
            setRowSorter(rowSorter);
        } catch (final NoClassDefFoundError ex) {
            LOGGER.error("Error intentando agregar el TableRowSorter", ex);
        }
    }

    private void setUpCellRenderers(final Properties properties) {
        columnModel.getColumn(convertColumnIndexToView(
            AgendaTableModel.NAME_COLUMN_INDEX)).setCellRenderer(
                new NameTableCellRenderer(tableModel, this));

        columnModel.getColumn(convertColumnIndexToView(
            AgendaTableModel.SURNAME_COLUMN_INDEX)).setCellRenderer(
                new NameTableCellRenderer(tableModel, this));

        columnModel.getColumn(convertColumnIndexToView(
            AgendaTableModel.NOTAS_COLUMN_INDEX)).setCellRenderer(
                new NotasTableCellRenderer(tableModel, this));

        columnModel.getColumn(convertColumnIndexToView(
            AgendaTableModel.DATE_COLUMN_INDEX)).setCellRenderer(
                new DateTableCellRenderer(tableModel, this, properties));

        columnModel.getColumn(convertColumnIndexToView(
            AgendaTableModel.TELEFONOS_COLUMN_INDEX)).setCellRenderer(
                new PhoneTableCellRenderer(tableModel, this));
    }

    int getGlobalColumnCount() {
    	// OJO, esto puede dar algo distinto a super.getColumnCount();
		// puesto que considera todas las columnas, independientemente de
		// que est�n visibles o no.
		return tableModel.getColumnCount();
    }
	
    void showMarcasColumn() {
        if (!showingMarcasColumn) {
        	columnControl.show(columnMarcas, 0);
            showingMarcasColumn = true;
        }
    }

	void hideMarcasColumn() {
		columnControl.hide(columnMarcas);
        showingMarcasColumn = false;
	}

	public void refreshRecord(final AgendaRecord record) {
		tableModel.refreshRecord(record);
		tableModel.fireTableDataChanged();
	}

    void clear() {
		tableModel.clear();
	}

	public void addRecord(final AgendaRecord record) {
		tableModel.addRecord(record);
	}

	/**
	 * Es necesario para considerar los registros correctos independientemente
	 * de que se haya hecho un sort.
	 * @param rowNr el n�mero de fila en la vista.
	 * @return el registro en el modelo.
	 */
	public AgendaRecord getValueAt(final int rowNr) {
		return tableModel.getValueAt(convertRowIndexToModel(rowNr));
	}

	/**
	 * Es necesario para considerar los registros correctos independientemente
	 * de que se haya hecho un sort.
	 * @param rowNr el n�mero de fila en la vista.
	 */
	public void removeDataAt(final int rowNr) {
		tableModel.removeDataAt(convertRowIndexToModel(rowNr));
	}

	public void fireTableDataChanged() {
		tableModel.fireTableDataChanged();
	}
}
