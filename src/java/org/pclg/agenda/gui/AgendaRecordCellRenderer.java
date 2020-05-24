package org.pclg.agenda.gui;

import org.pclg.agenda.entities.AgendaRecord;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

public class AgendaRecordCellRenderer extends DefaultTableCellRenderer {

	/** */
	private static final long serialVersionUID = 5915144269638510318L;
	
	private final AgendaTableModel agendaTableModel;

	public AgendaRecordCellRenderer(final AgendaTableModel agendaTableModel) {
		this.agendaTableModel = agendaTableModel;
	}

	@Override
	public Component getTableCellRendererComponent(
		final JTable table, final Object value,
			final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        final Component rendererComponent =
            super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
		final AgendaRecord record = agendaTableModel.getValueAt(
			table.convertRowIndexToModel(row));
		if (isSelected) {
			rendererComponent.setForeground(
				record.isHighlighted() ?
					DataTable.HIGHLIGHT_COLOR : DataTable.SELECTION_FOREGROUND);
		} else {
            rendererComponent.setForeground(
                record.isHighlighted() ?
                    DataTable.HIGHLIGHT_COLOR : DataTable.NORMAL_FOREGROUND);
        }
		return rendererComponent;
	}

}
