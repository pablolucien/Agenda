package org.pclg.tools;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class BrowserAbstractTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -3848850385896392332L;
    public final List dataList = new ArrayList();
	public int columnCount;
	public String [] columnNames;
	@Override
	public int getColumnCount() { return(columnCount + 1); }

	@Override
	public int getRowCount() { return(dataList.size());}

	@Override
	public String getColumnName(final int col) {
		if(col == 0) {
			return ("Nr.");
		}
		return(columnNames[col - 1]);
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		((Object [])(dataList.get(rowIndex)))[columnIndex - 1] = aValue;
	}

	public Object getValueAt(final int row, final int col) {
		if(col == 0) {
			return (new Integer(row + 1));
		}
		if(row >= dataList.size()) {
			return (null);
		}

		return(((Object [])(dataList.get(row)))[col - 1]);
	}

	public Class getColumnClass(final int col) {
		final Object o = getValueAt(0, col);
		if(o == null) {
			return Object.class;
		}
		return o.getClass();
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnIndex > 0;
	}
	
	
}
