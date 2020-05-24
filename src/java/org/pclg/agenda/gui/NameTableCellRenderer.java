package org.pclg.agenda.gui;

import org.pclg.agenda.entities.AgendaRecord;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Font;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

final class NameTableCellRenderer implements TableCellRenderer {
	private Font boldFont;
	private Font plainFont;
	private final JLabel rendererComponent = new JLabel() {
		{
			boldFont = getFont();
			plainFont = boldFont.deriveFont(Font.PLAIN);
			setFont(plainFont);
		}
	};

	private final AgendaTableModel agendaTableModel;
	private final JTable dataTable;

	NameTableCellRenderer(final AgendaTableModel agendaTableModel,
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
		final AgendaRecord record = agendaTableModel.getValueAt(
			table.convertRowIndexToModel(row));
	    if (isSelected) {
	        rendererComponent.setForeground(record.isHighlighted() ?
	        	DataTable.HIGHLIGHT_COLOR : dataTable.getSelectionForeground());
	    } else {
	        rendererComponent.setForeground(record.isHighlighted() ?
	        	DataTable.HIGHLIGHT_COLOR : dataTable.getForeground());
	    }
		final Icon thumbnail = record.getThumbnail();
		rendererComponent.setToolTipText(thumbnail == null ?
//			"Sin thumbnail" :
			"<html><img src=\"" + getClass().getResource("/create.gif") + "\"/> Tooltip </html>" :
//			"<html><img src=\"" + "file:///C:/plucien/Avatar.png" + "\"/> Tooltip </html>" :
			"<html><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==\"/> Tooltip </html>");
		//System.out.println("rendererComponent = " + rendererComponent.getToolTipText());

		rendererComponent.setFont(isEmptyOrBlank(record.getMark()) ? plainFont : boldFont);

		return rendererComponent;
	}
}
