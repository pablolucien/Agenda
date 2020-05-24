package org.pclg.xtras;

import org.pclg.tools.ToolBox;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Pablo
 * @since 19-ago-2007 12:09:18
 */
public final class ColorTableTest extends JFrame {

    private static final long serialVersionUID = 2234613409593751301L;

    private class ColorTableCellRenderer extends JLabel implements ListCellRenderer {
		public static final int PAD_LEN = 20;
        private static final long serialVersionUID = 4902074469627028270L;

        ColorTableCellRenderer() {
			setFont(new Font("Monospaced", Font.BOLD, 12));
		}

		/**
		 *
		 * @param list          the list
		 * @param value         value to display
		 * @param index         cell index
		 * @param isSelected    is the cell selected
		 * @param cellHasFocus  the list and the cell have the focus
		 * @return a component to display
		 */
		@Override
		public Component getListCellRendererComponent(final JList list, final Object value,
				final int index, final boolean isSelected, final boolean cellHasFocus) {
			final Map.Entry<String, int[]> entry = (Map.Entry<String, int[]>) value;
			final String colorName = entry.getKey();
			final int[] RGB = entry.getValue();
			setText(ToolBox.pad(colorName, PAD_LEN) + " " + RGB[0] + ":" + RGB[1] + ":" + RGB[2]);
			setBackground(Colortable.getColor(colorName));
			if (isSelected) {
				setForeground(Color.white);
			} else {
				setForeground(Color.black);
			}
			setEnabled(list.isEnabled());
			setOpaque(true);
			return this;
		}

	}

	private ColorTableTest() {
		super(ColorTableTest.class.getSimpleName());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(new JLabel("Lista de colores"), BorderLayout.NORTH);
		final Map colorMap = Colortable.getTable();

		final DefaultListModel listModelWest = new DefaultListModel();
		final JList colorListWest = new JList(listModelWest);
		colorListWest.setVisibleRowCount(25);
		colorListWest.setCellRenderer(new ColorTableCellRenderer());
		final JScrollPane scrollPaneWest = new JScrollPane(colorListWest);
		add(scrollPaneWest, BorderLayout.WEST);
		final Set<Map.Entry<String, int[]>> entriesWest = new TreeSet<>(
			(o1, o2) -> o1.getKey().compareTo(o2.getKey()));
		entriesWest.addAll(colorMap.entrySet());
		for (final Map.Entry entry : entriesWest) {
			listModelWest.addElement(entry);
		}

		final DefaultListModel listModelEast = new DefaultListModel();
		final JList colorListEast = new JList(listModelEast);
		colorListEast.setVisibleRowCount(25);
		colorListEast.setCellRenderer(new ColorTableCellRenderer());
		final JScrollPane scrollPaneEast = new JScrollPane(colorListEast);
		add(scrollPaneEast, BorderLayout.EAST);
		final Set<Map.Entry<String, int[]>> entriesEast = new TreeSet<>(
			(o1, o2) -> {
				final int[] rgb1 = o1.getValue();
				final int[] rgb2 = o2.getValue();
				if (rgb1[0] < rgb2[0]) {
					return -1;
				} else if (rgb1[0] == rgb2[0]) {
					if (rgb1[1] < rgb2[1]) {
						return -1;
					} else if (rgb1[1] == rgb2[1]) {
						if (rgb1[2] < rgb2[2]) {
							return -1;
						} else if (rgb1[2] == rgb2[2]) {
							return 0;
						}
					}
				}
				return 1;
//						return Arrays.toString(o1.getValue()).compareTo(Arrays.toString(o2.getValue()));
			});
		entriesEast.addAll(colorMap.entrySet());
		for (final Map.Entry entry : entriesEast) {
			listModelEast.addElement(entry);
		}

		pack();
		setVisible(true);
	}



	public static void main(final String[] args) {
		new ColorTableTest();
	}
}
