package org.pclg.tools;

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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Asistente para generar reportes ??/??/???? version original 10/08/2000 Carga
 * de los datos en un thread aparte
 */
public class Browser extends JFrame implements ActionListener {
    private static final long serialVersionUID = -8539783489191246999L;
    private TableSorter sorter;

	private ResultSet rset;

	private ResultSetMetaData tableInfo;

	private String query;

	private int[] columnWidths;

	// Popup Menu
	private final JPopupMenu popupMenu = new JPopupMenu("Popup Menu");

	private final JMenuItem ascendMenuItem = addMenuItem(popupMenu, "Ascendente");

	private final JMenuItem descendMenuItem = addMenuItem(popupMenu, "Descentente");

	private JMenuItem separator = addMenuItem(popupMenu, "-");

	private JMenuItem fijarMenuItem = addMenuItem(popupMenu, "Fijar Columna");

	private final JMenuItem ocultarMenuItem = addMenuItem(popupMenu,
			"Ocultar Columna");

	private final JMenuItem mostrarMenuItem = addMenuItem(popupMenu,
			"Mostrar Columna Oculta ...");

	private JTable table;

	private JScrollPane scrollpane;

	private JTableHeader tableHeader; // El header de la tabla donde se muestra el
								// menu

	private Point popupMenuPoint; // El punto donde se muestra el menu

	private final BrowserAbstractTableModel dataModel = new BrowserAbstractTableModel();

	public Browser(final Connection conn, final String tableName) {
		super("Browser: " + tableName);
		loadData(conn, tableName);
		setUp();
	}

	public Browser(final String[] headers, final List<String[]> data) {
		super("Browser: ");
		dataModel.columnCount = headers.length;
		dataModel.columnNames = headers;
		columnWidths = new int[dataModel.columnCount];
		for (final String[] datum : data) {
			dataModel.dataList.add(datum);
		}
		setUp();
	}

	private void setUp() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent ev) {
				dispose();
				setVisible(false);
			}
		});

		sorter = new TableSorter(dataModel);
		table = new JTable(sorter);
		sorter.addMouseListenerToHeaderInTable(table);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		final int fontSize = 20; // ????
		TableColumn column = null;
		for (int i = 0; i < dataModel.columnCount + 1; i++) {
			column = table.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(6 * fontSize);
			} else {
				column.setPreferredWidth(Math.max(columnWidths[i - 1],
						dataModel.columnNames[i - 1].length())
						* fontSize);
			}
		}

		scrollpane = new JScrollPane(table);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
		getContentPane().add(new JLabel("Browser"),
				BorderLayout.NORTH);

		tableHeader = table.getTableHeader();
		tableHeader.add(popupMenu);
		mostrarMenuItem.setEnabled(false);

		tableHeader.addMouseListener(new MouseAdapter() {
			public void mousePressed(final MouseEvent ev) {
				if (SwingUtilities.isRightMouseButton(ev)) {
					popupMenuPoint = ev.getPoint();
					popupMenu.show(tableHeader, popupMenuPoint.x,
							popupMenuPoint.y);
				}
			}
		});

		pack();
		setVisible(true);
	}

	private void loadData(final Connection conn, final String tableName) {
		this.query = "Select * from " + tableName;
		System.out.println(query);
		try {
			final Statement miSql = conn.createStatement();
			rset = miSql.executeQuery(query);
			tableInfo = rset.getMetaData();
			dataModel.columnCount = tableInfo.getColumnCount();
			dataModel.columnNames = new String[dataModel.columnCount];
			columnWidths = new int[dataModel.columnCount];

			for (int i = 0; i < dataModel.columnCount; i++) {
				dataModel.columnNames[i] = tableInfo.getColumnName(i + 1);
				if (tableInfo.getColumnType(i + 1) == Types.LONGVARCHAR) {
					columnWidths[i] = 10;
				} else {
					columnWidths[i] = tableInfo.getColumnDisplaySize(i + 1);
				}
			}

			new DataLoader().start();
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Metodo de utilidad para agregrar items a un menu
	 */
	private JMenuItem addMenuItem(final JPopupMenu parent, final String label) {
		if (label.equals("-")) {
			parent.addSeparator();
			return null;
		} else {
			final JMenuItem mi = new JMenuItem(label);
			parent.add(mi);
			mi.addActionListener(this);
			return (mi);
		}
	}

	private final Map columnMap = new HashMap(); // Conjunto de las columnas ocultas

	public void actionPerformed(final ActionEvent ev) {
		if (ev.getSource() == ocultarMenuItem) {
			final TableColumnModel tcm = table.getColumnModel();
			final TableColumn col = tcm.getColumn(table
					.columnAtPoint(popupMenuPoint));
			tcm.removeColumn(col);
			columnMap.put(col.getIdentifier(), col);
			mostrarMenuItem.setEnabled(true);
		} else if (ev.getSource() == mostrarMenuItem) {
			final Object[] possibleValues = columnMap.keySet().toArray();

			final Object selectedValue = JOptionPane.showInputDialog(this,
					"Escoja la columna", "Mostrar Columna Oculta",
					JOptionPane.INFORMATION_MESSAGE, null, possibleValues,
					possibleValues[0]);
			if (selectedValue == null) {
				return;
			}

			final TableColumnModel tcm = table.getColumnModel();
			final int index = table.columnAtPoint(popupMenuPoint);
			final TableColumn newCol = (TableColumn) columnMap
					.get(selectedValue);
			tcm.addColumn(newCol);
			if (index > -1) {
				tcm.moveColumn(tcm.getColumnIndex(newCol.getIdentifier()),
						index);
			}
			columnMap.remove(selectedValue);
			if (columnMap.size() == 0) {
				mostrarMenuItem.setEnabled(false);
			}
		} else if (ev.getSource() == ascendMenuItem) {
			sorter.sortByColumn(table.columnAtPoint(popupMenuPoint), true);
		} else if (ev.getSource() == descendMenuItem) {
			sorter.sortByColumn(table.columnAtPoint(popupMenuPoint), false);
		} else {
			System.err.println(table.columnAtPoint(popupMenuPoint));
		}
	}

	private class DataLoader extends Thread {
		public void run() {
			final String oldTitle = getTitle();
			setTitle(oldTitle + " -- Cargando datos ...");
			final int cron = Chrono.getChrono();
			Chrono.start(cron);
			try {
				int first = 0;
				int last = 0;
				int count = 0;
				while (rset.next()) {
					final Object[] fields = new Object[dataModel.columnCount];
					for (int i = 0; i < dataModel.columnCount; i++) {
						switch (tableInfo.getColumnType(i + 1)) {
						case Types.TIME:
							fields[i] = rset.getTime(i + 1);
							break;
						case Types.DATE:
							fields[i] = rset.getDate(i + 1);
							break;
						case Types.TIMESTAMP: // ??? Aparentemente los campos
												// fecha vienen con este tipo
							fields[i] = rset.getDate(i + 1);
							break;
						case Types.TINYINT:
							fields[i] = new Byte(rset.getByte(i + 1));
							break;
						case Types.INTEGER:
							fields[i] = new Integer(rset.getInt(i + 1));
							break;
						case Types.BIGINT:
							fields[i] = new Long(rset.getLong(i + 1));
							break;
						case Types.DECIMAL:
						case Types.NUMERIC:
							// fields[i] = rset.getBigDecimal(i + 1);
							// break;
						case Types.FLOAT:
						case Types.DOUBLE:
						case Types.REAL:
							fields[i] = Double.valueOf(rset.getDouble(i + 1));
							break;
						case Types.BIT:
							fields[i] = Boolean.valueOf(rset.getBoolean(i + 1));
							break;
						case Types.CHAR:
						case Types.VARCHAR:
						case Types.LONGVARCHAR:
							try {
								fields[i] = rset.getString(i + 1);
							} catch (final Exception ex) {
								ex.printStackTrace();
							}
							break;
						default:
							fields[i] = rset.getString(i + 1);
						}
					}
					dataModel.dataList.add(fields);
					last++;
					count++;
					if (count % 10 == 0) {
						dataModel.fireTableRowsInserted(first, last);
						first = last;
					}
				}
				if (first != last) {
					dataModel.fireTableRowsInserted(first, last);
					first = last;
				}
			} catch (final SQLException ex) {
				ex.printStackTrace();
			}
			Chrono.mark(cron);
			System.out.println("Browser Cargado: "
					+ Chrono.timeDetail(Chrono.elapsed(cron)));
			setTitle(oldTitle);
		}
	}
}
