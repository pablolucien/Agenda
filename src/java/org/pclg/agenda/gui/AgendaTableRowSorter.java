package org.pclg.agenda.gui;

import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;

/**
 * @author Pablo
 * @since 10/08/13 18:58
 */
class AgendaTableRowSorter
		extends TableRowSorter<AgendaTableModel> {
	private final AgendaTableModel tableModel;

	AgendaTableRowSorter(final AgendaTableModel agendaTableModel) {
		super(agendaTableModel);
		tableModel = agendaTableModel;
		setModelWrapper(new AgendaTableRowSorterModelWrapper());
	}

	private class AgendaTableRowSorterModelWrapper
			extends ModelWrapper<AgendaTableModel, Integer> {
		@Override
		public AgendaTableModel getModel() {
			return tableModel;
		}

		@Override
		public int getColumnCount() {
			return (tableModel == null) ? 0 : tableModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return (tableModel == null) ? 0 : tableModel.getRowCount();
		}

		@Override
		public Object getValueAt(final int row, final int column) {
			return column == AgendaTableModel.DATE_COLUMN_INDEX ?
				tableModel.getValueAt(row) :
				tableModel.getValueAt(row, column);
		}

		@Override
		public String getStringValueAt(final int row, final int column) {
			final TableStringConverter converter = getStringConverter();
			if (converter != null) {
				// Use the converter
				final String value = converter.toString(
						tableModel, row, column);
				if (value != null) {
					return value;
				}
				return "";
			}

			// No converter, use getValueAt followed by toString
			final Object o = getValueAt(row, column);
			if (o == null) {
				return "";
			}
			final String string = o.toString();
			if (string == null) {
				return "";
			}
			return string;
		}

		@Override
		public Integer getIdentifier(final int index) {
			return index;
		}
	}
}
