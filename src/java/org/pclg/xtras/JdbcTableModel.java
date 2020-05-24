package org.pclg.xtras;

import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * @author El Coyote Cojo
 * @since 19-sep-2007 19:12:32
 */
class JdbcTableModel extends DefaultTableModel {
	private static final long serialVersionUID = -3920954147384683422L;

	JdbcTableModel(final Object[][] data, final String[] headings) {
		super(data, headings);
	}

	JdbcTableModel(final List<List<String>> data, final List<String> headings) {
		final Object[][] rowData = new Object[data.size()][];
		for (int ii = 0; ii < rowData.length; ii++) {
			rowData[ii] = data.get(ii).toArray();
		}
		final Object[] colNames = headings.toArray();
		setDataVector(rowData, colNames);
	}

	@Override
	public boolean isCellEditable(final int row, final int col) {
		//return getColumnClass(col) != ImageIcon.class;
		return false;
	}

	@Override
	public Class<?> getColumnClass(final int col) {
		final List<?> lst = (List<?>) dataVector.elementAt(0);

		return lst.get(col).getClass();
	}
}
