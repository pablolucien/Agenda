package org.pclg.tools;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
* In a chain of data manipulators some behaviour is common. org.pclg.tools.TableMap
* provides most of this behavour and can be subclassed by filters
* that only need to override a handful of specific methods. org.pclg.tools.TableMap
* implements TableModel by routing all requests to its model, and
* TableModelListener by routing all events to its listeners. Inserting
* a org.pclg.tools.TableMap which has not been subclassed into a chain of table filters
* should have no effect.
*
* @version 1.4 12/17/97
* @author Philip Milne
*/
public class TableMap extends AbstractTableModel
                      implements TableModelListener {
    private static final long serialVersionUID = -8637513675177111757L;
    TableModel model;

    public TableModel getModel() {
        return model;
    }

    void setModel(final TableModel model) {
        this.model = model; 
        model.addTableModelListener(this); 
    }

    // By default, implement TableModel by forwarding all messages 
    // to the model. 

    @Override
	public Object getValueAt(final int aRow, final int aColumn) {
        return model.getValueAt(aRow, aColumn); 
    }
        
    @Override
	public void setValueAt(final Object aValue, final int aRow, final int aColumn) {
        model.setValueAt(aValue, aRow, aColumn); 
    }

    @Override
	public int getRowCount() {
        return (model == null) ? 0 : model.getRowCount(); 
    }

    @Override
	public int getColumnCount() {
        return (model == null) ? 0 : model.getColumnCount(); 
    }
        
    @Override
	public String getColumnName(final int aColumn) {
        return model.getColumnName(aColumn); 
    }

    @Override
	public Class getColumnClass(final int aColumn) {
        return model.getColumnClass(aColumn); 
    }
        
    @Override
	public boolean isCellEditable(final int row, final int column) {
         return model.isCellEditable(row, column); 
    }
//
// Implementation of the TableModelListener interface, 
//
    // By default forward all events to all the listeners. 
    @Override
	public void tableChanged(final TableModelEvent e) {
        fireTableChanged(e);
    }
}
