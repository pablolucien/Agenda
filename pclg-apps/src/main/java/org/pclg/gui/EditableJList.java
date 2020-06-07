package org.pclg.gui;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class EditableJList<E> extends JTable {
	private static final long serialVersionUID = 1L;
	/** Para poder agregar un elemento 'nulo' a la lista. */
	private final E nullValue;

	private class  EditableJListTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		final List<E> items = new ArrayList<>();

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public int getRowCount() {
			return items.size();
		}

		@Override
		public E getValueAt(final int rowIndex, final int columnIndex) {
			if (columnIndex != 0) {
				throw new IllegalArgumentException("Column " + columnIndex);
			}
			if (rowIndex < 0 || rowIndex >= items.size()) {
				throw new IllegalArgumentException("Row " + rowIndex);
			}
			return items.get(rowIndex);
		}

		@Override
		public boolean isCellEditable(final int rowIndex,
			final int columnIndex) {
			if (columnIndex != 0) {
				throw new IllegalArgumentException("Column " + columnIndex);
			}
			return true;
		}

		@Override
		public void setValueAt(
			final Object aValue, final int rowIndex, final int columnIndex) {
			if (columnIndex != 0) {
				throw new IllegalArgumentException("Column " + columnIndex);
			}
			if (aValue == null || aValue.equals(nullValue)) {
                if (rowIndex != items.size() - 1) {
                    items.remove(rowIndex);
                    fireTableDataChanged();
                }
			} else {
				items.set(rowIndex, (E) aValue);
				if (rowIndex == items.size() - 1) {
					items.add(nullValue);
					fireTableDataChanged();
					final int newRow = items.size() - 1;
					final Rectangle cellRect = getCellRect(newRow, 0, true);
					scrollRectToVisible(cellRect);
					selectionModel.setSelectionInterval(newRow, newRow);
				}
			}
		}

		public void addItem(final E anObject) {
			items.add(anObject);
		}

		public void clear() {
			items.clear();
		}

		public void addAll(List<E> values) {
			items.addAll(values);
		}
	}

	private final EditableJListTableModel listaModel = new EditableJListTableModel();
	
	public EditableJList(final E nullValue) {
		this.nullValue = nullValue;
		setModel(listaModel);
		setTableHeader(null);
	}

	@Override
	public E getValueAt(final int row, final int column) {
     return listaModel.getValueAt(convertRowIndexToModel(row),
		 convertColumnIndexToModel(column));
 }

	@Override
	public int getRowCount() {
		return listaModel.getRowCount();
	}
	
	public void addItem(final E anObject) {
		listaModel.addItem(anObject);
	}

	public void refresh() {
		listaModel.fireTableDataChanged();
	}

	public void clear() {
		listaModel.clear();
	}
	public void addAll(List<E> values) {
		listaModel.addAll(values);
	}
}
