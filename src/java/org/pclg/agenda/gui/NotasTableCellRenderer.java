package org.pclg.agenda.gui;

import org.pclg.agenda.entities.AgendaRecord;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Font;

final class NotasTableCellRenderer implements TableCellRenderer {
	private final JLabel rendererComponent = new JLabel() {
		private static final long serialVersionUID = -5139116431154485053L;

        {
			setFont(getFont().deriveFont(Font.ITALIC));
		}
	};
	
	private final AgendaTableModel agendaTableModel;
	private final JTable dataTable;
	
	NotasTableCellRenderer(final AgendaTableModel agendaTableModel,
			final JTable dataTable) {
		this.agendaTableModel = agendaTableModel;
		this.dataTable = dataTable;
	}

	@Override
	public Component getTableCellRendererComponent(
		final JTable table, final Object value,
		final boolean isSelected, final boolean hasFocus,
		final int row, final int column) {
		final String text = value == null ? "" : String.valueOf(value);
		rendererComponent.setText(text);
		rendererComponent.setToolTipText(text);
		final AgendaRecord record = agendaTableModel.getValueAt(
			table.convertRowIndexToModel(row));
	    if (isSelected) {
	        rendererComponent.setForeground(record.isHighlighted() ?
	        	DataTable.HIGHLIGHT_COLOR : dataTable.getSelectionForeground());
	    } else {
	        rendererComponent.setForeground(record.isHighlighted() ?
	        	DataTable.HIGHLIGHT_COLOR : dataTable.getForeground());
	    }
		return rendererComponent;
	}
}
