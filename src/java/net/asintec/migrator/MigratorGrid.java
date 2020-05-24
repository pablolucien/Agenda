// ******************************** package
package net.asintec.migrator;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
	MigratorGrid
	Presenta una tabla donde se configura una migracion de datos
	@version 1.00
	@author El Coyote Cojo

	2005.11.26: preprocessorsComboBox deja de ser un atributo y pasa a ser variable del método que lo usa.
					cambiados todos los "i" por "ii"
*/
public class MigratorGrid extends JPanel implements MigrationInfo {
    private static final long serialVersionUID = 7801150170238429532L;
    /** Usado para seleccionar los campos origen */
	/*private*/final JComboBox<String> comboBox = new JComboBox<>();	// ??? es un flechazo: es accesible desde DatabaseSelector ?????????????????????

	/** Los nombres de las columnas */
	private static final String[] columnNames = {"Campo destino", "Campo origen", "Valor Constante", "Preprocesador", "Parámetros"};

	/** Los anchos de las columnas */
	private static final int[] columnWidths = {40, 40, 40, 60, 50};

	/** El vector de datos */
	private final List<Object []> dataVector = new ArrayList<>();

	/** La tabla con los datos */
	private final JTable table;

    /** */
//	private JTableHeader tableHeader;	// El header de la tabla donde se muestra el pop-up menu

	/** */
	private final Migrator gridParent;

	/**
		Este señor se encarga del manejo de los datos
	*/
	private final AbstractTableModel dataModel = new AbstractTableModel() {
        private static final long serialVersionUID = 7154121183046972739L;

        @Override
		public int getColumnCount() {
			return(columnNames.length);
		}

		@Override
		public int getRowCount() {
			return(dataVector.size());
		}

		@Override
		public String getColumnName(final int col) {
			return(columnNames[col]);
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			if(row >= dataVector.size()) {
				return (null);
			}
			final Object dato = dataVector.get(row)[col];

			// Valor constante
			if(col == 2) {
				final Object valueAt = getValueAt(row, 1);
				if (!MigratorConstants.CONSTANTE.equals(valueAt)
					&& !MigratorConstants.AUTO.equals(valueAt)) {
					return (null);
				}
			}

			// Parametros de los preprocesadores
			if(col == 4) {
				if ("".equals(getValueAt(row, 3)) || getValueAt(row, 3) == null)
				{
					return (null);
				}
			}

			return(dato);
		}

		@Override
		public Class getColumnClass(final int col) {
//			if(col == 3) return Boolean.class;
			final Object o = getValueAt(0, col);
			if(o == null) {
				return Object.class;
			}
			return o.getClass();
		}

		@Override
		public boolean isCellEditable(final int row, final int col) {
			if(col == 2) {
				final Object valueAt = getValueAt(row, 1);
				return MigratorConstants.CONSTANTE.equals(valueAt)
					|| MigratorConstants.AUTO.equals(valueAt);
			}

			if(col == 4) {		// Parámetros de Preprocesador
				final String preprocesorName = (String) getValueAt(row, 3);
				if(preprocesorName != null && !preprocesorName.equals("")) {
					return (true);
				}
				else {
					return (false);
				}
			}

			if(col > 0) {
				return true;
			}
			return false;
		}

		@Override
		public void setValueAt(final Object o, final int row, final int col) {
			if(getColumnClass(col) == Boolean.class) {
				if (o instanceof Boolean) {
					dataVector.get(row)[col] = o;
				}
				else if(o instanceof String) {
					dataVector.get(row)[col]
							= Boolean.valueOf((String) o);
				}
			}
			else {
				dataVector.get(row)[col] = o;
			}
		}
	};

	/**
		Construye un MigratorGrid
		@param gridParent El objeto Migrator que hace uso de este
	*/
	public MigratorGrid(final Migrator gridParent) {
		this.gridParent = gridParent;
		setLayout(new BorderLayout());
		table = new JTable(dataModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setUpColumns();
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	/**
		Establece el comportamiento de la tabla, inicializa editores y renderers, etc.
	*/
	void setUpColumns() {
		// Campos de origen
		final TableColumn column1 = table.getColumnModel().getColumn(1);
		column1.setCellEditor(new DefaultCellEditor(comboBox));
		final DefaultTableCellRenderer renderer1 = new DefaultTableCellRenderer();
		renderer1.setToolTipText("Pinche para seleccionar el campo");
		column1.setCellRenderer(renderer1);

		// Preprocesadores
		final java.io.File path = gridParent.getExecutionPath();
//System.out.println(path);
		// FIXME: Mientras veo como resolver esto, me hago un contournement.
//        final Class[] clases = ToolBox.findClasses(net.asintec.migrator.preprocessors
//                .Preprocessor.class, path);
        final Class[] clases = loadPreprocessorFromAlgunSitio();

		/* Aqui se seleccionan los posibles preprocesadores */
		final JComboBox preprocessorsComboBox = new JComboBox();
		preprocessorsComboBox.removeAllItems();
		preprocessorsComboBox.addItem("");
		int maxLen = 0;
		int maxLenItem = 0;
		for(int ii = 0; ii < clases.length; ii++) {
			if (clases[ii] == null) {
				continue;
			}
			final String className = clases[ii].getName();
		// FIXME: Aqui tenemos otro problema despues si hacemos el Class.forName() sin el package :(.
//			preprocessorsComboBox.addItem(className.substring(className
//                    .lastIndexOf('.') + 1));
			preprocessorsComboBox.addItem(className);
			maxLen = Math.max(maxLen, className.length());
			final int len = className.length();
			if (len > maxLen) {
				//maxLen = len;
				maxLenItem = ii;
			}
		}

		// FIXME: Revisar este cambio
        if (clases.length > maxLenItem && clases[maxLenItem] != null) {
            preprocessorsComboBox.setPrototypeDisplayValue(clases[maxLenItem]
                    .getName());
        }

        final TableColumn column3 = table.getColumnModel().getColumn(3);
		column3.setCellEditor(new DefaultCellEditor(preprocessorsComboBox));
		final DefaultTableCellRenderer renderer3 = new DefaultTableCellRenderer();
		renderer3.setToolTipText("Pinche para seleccionar el preprocesador");
		column3.setCellRenderer(renderer3);

		// Una forma ingenua de asignarles tamaños a las columnas
		final int fontSize = 3;	// ????
		TableColumn column = null;
		for (int ii = 0; ii < columnNames.length; ii++) {
			column = table.getColumnModel().getColumn(ii);
			column.setPreferredWidth(Math.max(columnWidths[ii], columnNames[ii].length()) * fontSize);
		}
	}

	private static Class[] loadPreprocessorFromAlgunSitio() {
		final String[] candidates = {
				"org.pclg.agenda.crypto.PreprocessorClipAndCryptString",
				"org.pclg.agenda.crypto.PreprocessorCryptString",
				"org.pclg.migrator.clarion.PreprocessorClarionDate",
				"org.pclg.migrator.preprocessors.PreprocessorAutoIncrement",
				"org.pclg.migrator.preprocessors.PreprocessorYesNo2OneZero",
				"net.asintec.migrator.preprocessors.Preprocessor2Int",
				"net.asintec.migrator.preprocessors.PreprocessorCalculator",
				"net.asintec.migrator.preprocessors.PreprocessorCalculator2",
				"net.asintec.migrator.preprocessors.PreprocessorClipString",
				"net.asintec.migrator.preprocessors.PreprocessorDrake",
				"net.asintec.migrator.preprocessors.PreprocessorDummy",
				"net.asintec.migrator.preprocessors.PreprocessorEncodeString",
				"net.asintec.migrator.preprocessors.PreprocessorHalf2M",
				"net.asintec.migrator.preprocessors.PreprocessorImage",
				"net.asintec.migrator.preprocessors.PreprocessorSubString",
				"net.asintec.migrator.preprocessors.PreprocessorToDate",
				"net.asintec.migrator.preprocessors.PreprocessorToLowerCase",
				"net.asintec.migrator.preprocessors.PreprocessorToUpperCase",
		};

		final Class[] classes = new Class[candidates.length];
		int index = 0;
		for (final String candidate : candidates) {
			try {
				classes[index++] = Class.forName(candidate);
			} catch (final ClassNotFoundException ex) {
				System.err.println(ex);
			}
		}
		return classes;
	}

	/** Limpia la tabla */
	public void clear() {
		dataVector.clear();
		dataModel.fireTableDataChanged();
	}

	/**
		Agrega una fila a la tabla
		@param row La fila que va a ser agregada
	*/
	public void addRow(final Object[] row) {
		dataVector.add(row);
		//dataModel.fireTableRowsInserted(dataList.size() - 1, dataList.size() - 1);
	}

	/**
		Actualiza el valor de una celda
		@param o El valor a introducir
		@param row La fila de la celda
		@param col La columna de la celda
	*/
	public void setValueAt(final Object o, final int row, final int col) {
		dataModel.setValueAt(o, row, col);
	}

	/**
		Obtiene el valor de una celda
		@param row La fila de la celda
		@param col La columna de la celda
		@return El valor contenido en la celda
	*/
	public Object getValueAt(final int row, final int col) {
		return(dataModel.getValueAt(row, col));
	}

	/**
		Indica la cantidad de filas de la tabla
		@return La cantidad de filas de la tabla
	*/
	@Override
	public int getRowCount() {
		return(dataVector.size());
	}

	/**
		Indica la cantidad de columnas de la tabla
		@return La cantidad de columnas de la tabla
	*/
	public int getColumnCount() {
		return(columnNames.length);
	}

	/**
		Refresca la tabla
	*/
	public void refresh() {
		//dataModel.fireTableRowsInserted(0, dataList.size() - 1);
		dataModel.fireTableStructureChanged();
		setUpColumns();
	}

	//------------ Implementacion de MigrationInfo ------------------

	// public int getRowCount(); está definido más arriba

	/**
		Devuelve el ii-esimo campo destino
		@param index El campo que queremos
		@return El ii-esimo campo destino
		@since 2002.05.31
	*/
	@Override
	public String getTargetField(final int index) {
		return((String) getValueAt(index, 0));
	}

	/**
		Devuelve el ii-esimo campo origen
		@param index El campo que queremos
		@return El ii-esimo campo origen
		@since 2002.05.31
	*/
	@Override
	public String getSourceField(final int index) {
		return((String) getValueAt(index, 1));
	}

	/**
		Devuelve el ii-esimo valor constante
		@param index El campo que queremos
		@return El ii-esimo valor constante
		@since 2002.05.31
	*/
	@Override
	public String getConstantOrAutoIncrementValue(final int index) {
		return((String) getValueAt(index, 2));
	}

	/**
		Devuelve el ii-esimo preprocesador
		@param index El campo que queremos
		@return El ii-esimo preprocesador
		@since 2002.05.31
	*/
	@Override
	public String getPreprcessorName(final int index) {
		return((String) getValueAt(index, 3));
	}

	/**
		Devuelve Los parámetros del ii-esimo preprocesador
		@param index El campo que queremos
		@return Los parámetros del ii-esimo preprocesador
		@since 2002.05.31
	*/
	@Override
	public String getPreprcessorParams(final int index) {
		return (String) getValueAt(index, 4);
	}
}
