package org.pclg.alter;

import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

final class Grid extends JPanel {
    private static final long serialVersionUID = 8291280067315280410L;
    private static final String[] COLUMN_NAMES = {"Nr.", "Nombre Inicial", "Nombre Final", "OK?"};

    static class RowInfo implements Serializable {
        private static final long serialVersionUID = 4388181274565344360L;
        private final String initialName;
        private final String finalName;
        private final Boolean result;

        RowInfo(String initialName, String finalName, Boolean result) {
            this.initialName = initialName;
            this.finalName = finalName;
            this.result = result;
        }
    }

    private final List<RowInfo> rows = new ArrayList<>();

    private final AbstractTableModel dataModel = new AbstractTableModel() {
        private static final long serialVersionUID = 4948339036292406913L;

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public String getColumnName(final int col) {
            return COLUMN_NAMES[col];
        }

        @Override
        public Object getValueAt(final int row, final int col) {
            if (row >= rows.size()) {
                return null;
            }

            switch (col) {
                case 0:
                    return Integer.valueOf(row + 1);
                case 1:
                    return rows.get(row).initialName;
                case 2:
                    return rows.get(row).finalName;
                case 3:
                    return rows.get(row).result;
                default:
                    throw new InternalError("Invalid value for column: " + col);
            }
        }

        @Override
        public Class<?> getColumnClass(final int col) {
            final Object obj = getValueAt(0, col);
            return obj == null ? Object.class : obj.getClass();
        }
    };

    private final JTable table = new JTable(dataModel);

    Grid(final Properties properties) {
        setLayout(new BorderLayout());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        final int fontSize = 9;   // ????
        TableColumn column;
        final int[] columnWidths = {5, 30, 30, 10};
        final TableColumnModel columnModel = table.getColumnModel();
        for (int ii = 0; ii < COLUMN_NAMES.length; ii++) {
            column = columnModel.getColumn(ii);
            column.setPreferredWidth(Math.max(columnWidths[ii],
                COLUMN_NAMES[ii].length()) * fontSize);
        }
        columnModel.getColumn(3).setCellRenderer(new TableCellRenderer() {
            private final JLabel rendererComponent = new JLabel();

            @Override
            public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
                final ImageIcon okIcon = ImageTools.getImageIcon("/resources/images/16x16/icons8-checkmark-16.png").orElse(null);
                final ImageIcon koIcon = ImageTools.getImageIcon("/resources/images/16x16/icons8-delete-16.png").orElse(null);
                rendererComponent.setIcon(((Boolean) value).booleanValue() ? okIcon : koIcon);
                return rendererComponent;
            }
        });

        table.setGridColor(Color.red);
        final JScrollPane scrollPane = new JScrollPane(table);
        GUITools.setUpColumns(table, properties, GUITools.ColumnControl.getInstance(table));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void clear() {
        rows.clear();
        dataModel.fireTableDataChanged();
    }

    void addRow(final RowInfo row) {
        rows.add(row);
    }

    public void refresh() {
        dataModel.fireTableStructureChanged();
    }

    public void saveProperties(final Properties properties) {
        GUITools.saveTableProperties(table, properties);
    }
}
