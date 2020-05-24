package net.asintec.migrator;

import org.pclg.tools.GUITools;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
	18/12/2001 version original
	@version 1.00
	@author El Coyote Cojo
*/
class ResultSetBrowser extends JDialog implements ActionListener {
    private static final long serialVersionUID = 8087954528016445993L;
    //   org.pclg.tools.TableSorter sorter;
//   private boolean rowNumSupported;
	private Connection conn;
	private String query;
	private int[] columnWidths;

	// Popup Menu
	private final JPopupMenu popupMenu      = new JPopupMenu("Popup Menu");
	private final JMenuItem ascendMenuItem  = GUITools.addMenuItem(this, popupMenu, "Ascendente");
	private final JMenuItem descendMenuItem = GUITools.addMenuItem(this, popupMenu, "Descentente");
	private JMenuItem fijarMenuItem   = GUITools.addMenuItem(this, popupMenu, "-Fijar Columna");
	private final JMenuItem ocultarMenuItem = GUITools.addMenuItem(this, popupMenu, "Ocultar Columna");
	private final JMenuItem mostrarMenuItem = GUITools.addMenuItem(this, popupMenu, "Mostrar Columna Oculta ...");
	private JTable table;
	private JScrollPane scrollpane;
	private JTableHeader tableHeader;  // El header de la tabla donde se muestra el menu
	private Point popupMenuPoint;      // El punto donde se muestra el menu


	private ResultSetTableModel dataModel;

	public ResultSetBrowser(final JFrame daddy, final Connection conn, final String tableName) {
		super(daddy, "ResultSetBrowser: " + tableName);

		if(conn == null || tableName == null) {
			JOptionPane.showMessageDialog(daddy, "No hay una tabla seleccionada", "Atención", JOptionPane.ERROR_MESSAGE);
			return;
		}

		this.conn = conn;
		query = "Select * from " + tableName;
		System.out.println(query);

		// Cargar los datos en la tabla
		dataModel = new ResultSetTableModel(conn, query, this);
		columnWidths = dataModel.columnWidths;								// ??????  Flechazo

		addWindowListener(new WindowAdapter() {
				public void windowClosing(final WindowEvent ev) {
					dataModel.stop();
					dispose();
					setVisible(false);
//					System.exit(0);
				}
			}
		);


		table = new JTable(dataModel);    // sustituida por las siguientes 3
/*		
		sorter = new org.pclg.tools.TableSorter(dataModel);
		table = new JTable(sorter);
		sorter.addMouseListenerToHeaderInTable(table);
*/
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		final int fontSize = 10;   // ????
		TableColumn column = null;
		for (int i = 0; i < dataModel.columnCount + 1; i++) {
			column = table.getColumnModel().getColumn(i);
			if(i == 0) {
				column.setPreferredWidth(6 * fontSize);
			}
			else {
				column.setPreferredWidth(Math.max(columnWidths[i - 1], dataModel.columnNames[i - 1].length()) * fontSize);
			}
		}



		scrollpane = new JScrollPane(table);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
		getContentPane().add(new JLabel("ResultSetBrowser de Asintec"), BorderLayout.NORTH);


//		TableColumnModel tcm = table.getColumnModel();

		tableHeader = table.getTableHeader();
		tableHeader.add(popupMenu);
		mostrarMenuItem.setEnabled(false);

		tableHeader.addMouseListener(new MouseAdapter() {
					public void mousePressed(final MouseEvent ev) {
//						if(ev.isPopupTrigger())  ??? no furula
							if(SwingUtilities.isRightMouseButton(ev)) {
								popupMenuPoint = ev.getPoint();
//System.err.println(tableHeader.columnAtPoint(p));
								popupMenu.show(tableHeader, popupMenuPoint.x, popupMenuPoint.y);
							}
						}
					}
		);

		pack();
		setVisible(true);
	}


	private final Map columnMap = new HashMap();   // Conjunto de las columnas ocultas

	public void actionPerformed(final ActionEvent ev) {
		if(ev.getSource() == ocultarMenuItem) {
			final TableColumnModel tcm = table.getColumnModel();
			final TableColumn col = tcm.getColumn(table.columnAtPoint(popupMenuPoint));
			tcm.removeColumn(col);
			columnMap.put(col.getIdentifier(), col);
			mostrarMenuItem.setEnabled(true);

//esta mierda no funciona
//			scrollpane.revalidate();
//			table.revalidate();
		}
		else if(ev.getSource() == mostrarMenuItem) {
			final Object[] possibleValues = columnMap.keySet().toArray();

			// Este chequeo no hace falta si la opcion de menu esta desactivada
			//if(possibleValues.length == 0)
			//   return;

			final Object selectedValue = JOptionPane.showInputDialog(this, "Escoja la columna", "Mostrar Columna Oculta",
															JOptionPane.INFORMATION_MESSAGE, null,
															possibleValues, possibleValues[0]);
			if(selectedValue == null) {
				return;
			}

			final TableColumnModel tcm = table.getColumnModel();
/*
			TableColumn col = tcm.getColumn(table.columnAtPoint(popupMenuPoint));
			int index = tcm.getColumnIndex(col.getIdentifier());  // indice de la columna donde esta el popup
*/
			final int index = table.columnAtPoint(popupMenuPoint);
			final TableColumn newCol = (TableColumn)columnMap.get(selectedValue);
			// Agregarla al final
			tcm.addColumn(newCol);
			// Moverla a la posicion seleccionada
			if(index > -1) {
				tcm.moveColumn(tcm.getColumnIndex(newCol.getIdentifier()),
						index);
			}
			columnMap.remove(selectedValue);
			if(columnMap.size() == 0) {
				mostrarMenuItem.setEnabled(false);
			}
		}
		else if(ev.getSource() == ascendMenuItem) {
//			sorter.sortByColumn(table.columnAtPoint(popupMenuPoint), true);
		}
		else if(ev.getSource() == descendMenuItem) {
//			sorter.sortByColumn(table.columnAtPoint(popupMenuPoint), false);
		}
		else {
			System.err.println(table.columnAtPoint(popupMenuPoint));
		}
	}
}
