// 2003.11.29 puesto en un paquete para evitar que sean compilados por ant
// cada vez.
package org.pclg.misc;

import javax.swing.table.AbstractTableModel;
import java.util.List;


/**
 *
 */
final class VectorArrayTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 8495357157634034967L;
    //    private Vector dataList;
    private List dataList;
    private final int columnCount;
    private final String[] columnNames;

    /**
     *
     * @param list
     * @param names
     */
//    public VectorArrayTableModel(final Vector list, final String[] names) {
    public VectorArrayTableModel(final List list, final String[] names) {
        dataList = list;
        columnNames = names;
        columnCount = columnNames.length;
    }

    /**
     *
     * @param list
     */
    public void setData(final List list) {
        dataList = list;
        fireTableStructureChanged();
    }

    /**
     *
     * @return
     */
    @Override
	public int getColumnCount() {
        return columnCount;
    }

    /**
     *
     * @return
     */
    @Override
	public int getRowCount() {
        return dataList.size();
    }

    /**
     *
     * @param col
     * @return
     */
    @Override
	public String getColumnName(final int col) {
        return columnNames[col];
    }

    /**
     *
     * @param row
     * @param col
     * @return
     */
    @Override
	public Object getValueAt(final int row, final int col) {
        Object value = null;
        if (row < dataList.size()) {
//            value = ((Object[]) dataList.elementAt(row))[col];
            value = ((Object[]) dataList.get(row))[col];
        }
        return value;


    }

    /**
     *
     * @param aValue
     * @param row
     * @param col
     */
    @Override
	public void setValueAt(final Object aValue, final int row, final int col) {
//        ((Object[]) dataList.elementAt(row))[col] = aValue;
        ((Object[]) dataList.get(row))[col] = aValue;
    }

    /**
     *
     * @param col
     * @return
     */
    @Override
	public Class getColumnClass(final int col) {
        Class objectClass = Object.class;
        final Object obj = getValueAt(0, col);
        if (obj != null) {
            objectClass = obj.getClass();
        }
        return objectClass;
    }

    /**
     *
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    @Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return columnIndex == 0;
    }
}
