package org.pclg.agenda.gui;

import org.pclg.agenda.AgendaUtil;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.ToolBox;

import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author El Coyote
 * @since 10-sep-2007 17:04:15
 */
final class AgendaTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -8122550240258121062L;
	private static final int KEY_COLUMN_INDEX = 0;
	private static final int VERSION_COLUMN_INDEX = 1;
	static final int DATE_COLUMN_INDEX = 2;
	static final int NAME_COLUMN_INDEX = 3;
	static final int SURNAME_COLUMN_INDEX = 4;
	static final int TELEFONOS_COLUMN_INDEX = 5;
	static final int MARCAS_COLUMN_INDEX = 6;
	static final int NOTAS_COLUMN_INDEX = 7;
	
	private static final String[] FIELD_NAMES =
		{"Clave", "Versión", "Fecha", "Nombre", "Apellido", "Teléfonos", "Marca", "Notas"};
	private static final Class<?>[] FIELD_TYPES =
		{Integer.class, Integer.class, String.class, String.class, String.class, String.class, String.class, String.class};
	private final List<AgendaRecord> data = new ArrayList<>();

	private String[] monthNames;

	AgendaTableModel(final Properties properties) {
		setUpLanguage(properties);
    }

	void setUpLanguage(final Properties properties) {
		FIELD_NAMES[KEY_COLUMN_INDEX] = PropertiesHelper.getStringFromProperties(
			properties, "AgendaTableModel.columnHeader.Clave");
		FIELD_NAMES[VERSION_COLUMN_INDEX] = PropertiesHelper.getStringFromProperties(
			properties, "AgendaTableModel.columnHeader.Version");
		FIELD_NAMES[DATE_COLUMN_INDEX] = PropertiesHelper
			.getStringFromProperties(
				properties, "AgendaTableModel.columnHeader.Fecha");
		FIELD_NAMES[NAME_COLUMN_INDEX] = PropertiesHelper.getStringFromProperties(
			properties, "AgendaTableModel.columnHeader.Nombre");
		FIELD_NAMES[SURNAME_COLUMN_INDEX] = PropertiesHelper.getStringFromProperties(
			properties, "AgendaTableModel.columnHeader.Apellido");
		FIELD_NAMES[TELEFONOS_COLUMN_INDEX] = PropertiesHelper
			.getStringFromProperties(
				properties, "AgendaTableModel.columnHeader.Telefonos");
		FIELD_NAMES[MARCAS_COLUMN_INDEX] = PropertiesHelper
			.getStringFromProperties(
				properties, "AgendaTableModel.columnHeader.Marca");
		FIELD_NAMES[NOTAS_COLUMN_INDEX] = PropertiesHelper
			.getStringFromProperties(
				properties, "AgendaTableModel.columnHeader.Notas");
        monthNames = DateFormatSymbols.getInstance(
                JComponent.getDefaultLocale()).getShortMonths();
	}

	@Override
	public String getColumnName(final int col) {
		return FIELD_NAMES[col];
	}

	@Override
	public Class<?> getColumnClass(final int col) {
		return FIELD_TYPES[col];
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return FIELD_NAMES.length;
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		final AgendaRecord record = data.get(row);
		switch (col) {
		case KEY_COLUMN_INDEX:
			return Integer.valueOf(record.getKey());
		case VERSION_COLUMN_INDEX:
			return Integer.valueOf(record.getVersion());
		case DATE_COLUMN_INDEX:
			return formatDate(record);
		case NAME_COLUMN_INDEX:
			return record.getFirstname();
		case SURNAME_COLUMN_INDEX:
			return record.getLastname();
		case TELEFONOS_COLUMN_INDEX:
			return AgendaUtil.concatenarTelefonos(record.getTelephones(),
                record.getCountry().getFormatoTelefono());
		case MARCAS_COLUMN_INDEX:
			return record.getMark();
		case NOTAS_COLUMN_INDEX:
			return record.getNotes();
		default:
			throw new IllegalStateException("Col: " + col + " no existe.");
		}
	}

	public AgendaRecord getValueAt(final int row) {
		return data.get(row);
	}

	public void addRecord(final AgendaRecord record) {
		data.add(record);
		fireTableDataChanged();
	}

	public void removeDataAt(final int row) {
		data.remove(row);
		fireTableDataChanged();
	}

	void clear() {
		data.clear();
	}

	private String formatDate(final AgendaRecord record) {
		final StringBuilder result = new StringBuilder(16);
        final int dia = record.getDay();
        if (dia != 0) {
			result.append(ToolBox.leftPad("" + dia, 2, '0')).append(' ');
		}

        final int mes = record.getMonth();
        if (mes != 0) {
			result.append(monthNames[mes - 1]).append(' ');
		}

        final int año = record.getYear();
        if (año != 0) {
			result.append(año);
		}
		return result.toString().trim();
	}

    public void refreshRecord(final AgendaRecord record) {
        for (final AgendaRecord agendaRecord : data) {
            if (agendaRecord.getKey() == record.getKey())  {
                data.set(data.indexOf(agendaRecord), record);
                return;
            }
        }
        // Si llegamos aquí es que es un registro nuevo
        data.add(record);
    }
}
