package org.pclg.agenda.gui;

import org.pclg.agenda.entities.AgendaRecord;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Font;
import java.io.Serial;

final class PhoneTableCellRenderer implements TableCellRenderer {
		private final JLabel rendererComponent = new JLabel() {
			@Serial
            private static final long serialVersionUID = -4450223585232890122L;

            {
				setFont(getFont().deriveFont(Font.ITALIC));
			}
		};
		private final AgendaTableModel agendaTableModel;
		private final JTable dataTable;

		PhoneTableCellRenderer(final AgendaTableModel agendaTableModel,
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
			rendererComponent.setToolTipText(text);
			final AgendaRecord record = agendaTableModel.getValueAt(
				table.convertRowIndexToModel(row));
			if (isSelected) {
				rendererComponent.setText(text);
				rendererComponent.setForeground(record.isHighlighted() ?
					DataTable.HIGHLIGHT_COLOR : dataTable.getSelectionForeground());
			} else {
				if (record.isHighlighted()) {
					rendererComponent.setText(text);
					rendererComponent.setForeground(DataTable.HIGHLIGHT_COLOR);
				} else {
					// Sólo para probar otra forma de hacerlo
					rendererComponent.setText(
						"<html><font color=#ffffdd>" + text + "</font></html>");
				}
			}
			return rendererComponent;
		}
	}
