package org.pclg.agenda.gui;

import org.pclg.agenda.AgendaUtil;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.tools.PropertiesHelper;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Font;
import java.text.MessageFormat;
import java.util.Properties;

final class DateTableCellRenderer implements TableCellRenderer {
	private final AgendaTableModel agendaTableModel;
	private final JTable dataTable;
	private final String lblEdad;
	private final String lblDias;
	private final JLabel rendererComponent = new JLabel() {
        {
			setFont(getFont().deriveFont(Font.PLAIN));
		}
	};

	DateTableCellRenderer(final AgendaTableModel agendaTableModel,
			final JTable dataTable, final Properties properties) {
		this.agendaTableModel = agendaTableModel;
		this.dataTable = dataTable;
		lblEdad = PropertiesHelper.getStringFromProperties(properties,
			"DataEntry.lbl.edad");
		lblDias = PropertiesHelper.getStringFromProperties(properties,
			"DataEntry.lbl.dias");
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
	    final AgendaUtil.AgeInfo ageInfo =
	        AgendaUtil.computeAgeAndDays(record.getDay(), record.getMonth() - 1, record.getYear());
	    if (AgendaUtil.AgeInfo.NULL_AGE_INFO.equals(ageInfo)) {
	        rendererComponent.setToolTipText(text);
	    } else {
	        rendererComponent.setToolTipText(
				MessageFormat.format("{0}: {1} - {2}: {3}", lblEdad,
					ageInfo.getAge(), lblDias, ageInfo.getDays()));
	    }
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
