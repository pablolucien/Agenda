package net.asintec.migrator;

import org.pclg.tools.Chrono;
import org.pclg.tools.ToolBox;

import javax.swing.JDialog;
import javax.swing.table.AbstractTableModel;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author El Coyote Cojo
 * @version 1.00
 */
final class ResultSetTableModel extends AbstractTableModel
		implements Runnable {
    private static final long serialVersionUID = 2049528564984537353L;
    private final List<Object[]> dataVector = new ArrayList<>();
	public int columnCount;
	public String[] columnNames;
	private ResultSet rset;
	private final JDialog parent;
	private ResultSetMetaData tableInfo;
	public int[] columnWidths;
	private boolean shouldStop;
	private int numRows;
	private static final int BUFFER_SIZE = 1024;


	public ResultSetTableModel(final Connection conn, final String query,
			final JDialog parent) {
		this.parent = parent;

		try {
			final Statement miSql = conn.createStatement(
					ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rset = miSql.executeQuery(query);

			tableInfo = rset.getMetaData();
			columnCount = tableInfo.getColumnCount();
			columnNames = new String[columnCount];
			columnWidths = new int[columnCount];

			// Obtener la cantidad de filas
			rset.last();
			numRows = rset.getRow();
			rset.beforeFirst();

			for (int i = 0; i < columnCount; i++) {
				columnNames[i] = tableInfo.getColumnName(i + 1);
				final int type = tableInfo.getColumnType(i + 1);
				if (type == Types.LONGVARCHAR || type == Types.BLOB
						|| type == Types.CLOB) {
					columnWidths[i] = 10;
				} else {
					columnWidths[i] = tableInfo.getColumnDisplaySize(i + 1);
				}
			}

			final Thread worker = new Thread(this);
//			worker.setPriority(Thread.MIN_PRIORITY);
			worker.start();
		}
		catch (final SQLException ex) {
			ToolBox.showInfo(ex, false);
			throw new RuntimeException(ex.toString());
		}
	}

	@Override
	public int getColumnCount() {
		return (columnCount + 1);
	}

	@Override
	public int getRowCount() {
		return (numRows);
	}

	@Override
	public String getColumnName(final int col) {
		if (col == 0) {
			return ("Nr.");
		}
		return (columnNames[col - 1]);
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		if (col == 0) {
			return (new Integer(row + 1));
		}
		if (row >= dataVector.size()) {
			return (null);
		}

		return (((dataVector.get(row)))[col - 1]);
	}

	@Override
	public Class getColumnClass(final int col) {
		final Object o = getValueAt(0, col);
		if (o == null) {
			return Object.class;
		}
		return o.getClass();
	}


	public void stop() {
		shouldStop = true;
	}

	/**
	 * Carga los datos del resultset
	 */
	@Override
	public void run() {
		final String oldTitle = parent.getTitle();
		parent.setTitle(oldTitle + " -- Cargando datos ...");
		final int cronHandle = Chrono.getChrono();
		try {
			int first = 0;
			int last = 0;
			int count = 0;
			while (rset.next()) {
				Thread.yield();
				if (shouldStop) {
					System.err.println("ResultSetBrowser: Me mataron");
					break;
				}
				final Object[] fields = new Object[columnCount];
				for (int i = 0; i < columnCount; i++) {
					switch (tableInfo.getColumnType(i + 1)) {
					case Types.TIME:
//						fields[i] = rset.getTime(i + 1);
//						break;
					case Types.DATE:
//						fields[i] = rset.getDate(i + 1);
//						break;
					case Types.TIMESTAMP:   // ??? Aparentemente los campos fecha vienen con este tipo
						fields[i] = rset.getTimestamp(i + 1);
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
//						fields[i] = rset.getBigDecimal(i + 1);
//						break;
					case Types.FLOAT:
					case Types.DOUBLE:
					case Types.REAL:
						fields[i] = new Double(rset.getDouble(i + 1));
						break;
					case Types.BIT:
						fields[i] = Boolean.valueOf(rset.getBoolean(i + 1));
						break;
					case Types.CHAR:
					case Types.VARCHAR:
					case Types.LONGVARCHAR:
						fields[i] = rset.getString(i + 1);
						break;
					case Types.VARBINARY:
					case Types.LONGVARBINARY:
						//fields[i] = rset.getBytes(i + 1);
						//fields[i] = "OBJETO";
						// Esto es probablemente muy lento, mejor poner la linea anterior
						if (rset.getBytes(i + 1) == null) {
							fields[i] = "**NULL**";
						} else {
							fields[i] = "**OBJETO**";
						}

						break;
					case Types.CLOB:
						//fields[i] = rset.getObject(i + 1);
						String s = "";
						final Reader reader = rset.getCharacterStream(i + 1);
						if (reader == null) {
							fields[i] = "**NULL**";
						} else {
							int total;
							final char[] buff = new char[BUFFER_SIZE];
							while ((total = reader.read(buff)) > 0) {
								s += new String(buff, 0, total);
							}
							fields[i] = s;
						}
						break;
//					case Types.BLOB:
//						fields[i] = "BLOB";
//						break;
					default:
						System.err.println(getClass().getName()
								+ " Tipo de dato desconocido "
								+ columnNames[i]);
						fields[i] = rset.getObject(i + 1);
					}
				}
//System.out.println(fields[0]);
				dataVector.add(fields);
				last++;
				count++;
				if (count % 10 == 0) {
					fireTableRowsInserted(first, last);
					first = last;
				}
			}
			if (first != last) {
				fireTableRowsInserted(first, last);
				first = last;
			}
		}
		catch (final IOException ex) {
			ToolBox.showInfo(ex);
		}
		catch (final SQLException ex) {
			ToolBox.showInfo(ex);
		}
		Chrono.mark(cronHandle);
		System.out.println("ResultSetBrowser Cargado: "
				+ Chrono.timeDetail(Chrono.elapsed(cronHandle)));
		parent.setTitle(oldTitle);
	}
}
