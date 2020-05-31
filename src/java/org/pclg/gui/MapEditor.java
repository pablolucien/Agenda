package org.pclg.gui;

import org.pclg.tools.ImageTools;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static org.pclg.tools.GUITools.setDialogResizable;

/**
 * Allows the edition of the values of a Map<String, String>. 
 * @author El Coyote.
 */
public class MapEditor {
	private static final String ICON_PATH
		= "/images/32x32/abiword.png";
	private final JLabel[] labels;
	private final JComponent[] fields;
	private final Component parent;
	private final JPanel contentPane = new JPanel(new BorderLayout());
	private final Icon icon;
	public static class Property implements Comparable<Property> {
		final String name;
		final Class<?> clazz;

		public Property(final String name, final Class<?> clazz) {
			this.name = name;
			this.clazz = clazz;
		}

		public String getName() {
			return name;
		}

		Class<?> getClazz() {
			return clazz;
		}

		@Override
		public int compareTo(final Property other) {
			return name.compareTo(other.name);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other
				|| other != null && getClass() == other.getClass() && name.equals(((Property) other).name);
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}

	/**
	 * Creates this class.
	 * @param parent the owner of this editor.
	 * @param map the map to edit.
	 */
	public MapEditor(final Component parent, final Map<Property, String> map) {
		this.parent = parent;
		icon = ImageTools.getImageIcon(ICON_PATH).orElse(null);
		final GridBagLayout gridbag = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		final int size = map.size();
		labels = new JLabel[size];
		fields = new JComponent[size];
		final JPanel dataPanel = new JPanel(gridbag);
		int index = 0;
		for (final Entry<Property, String> entry : map.entrySet()) {
			constraints.gridwidth = GridBagConstraints.RELATIVE;
			constraints.weightx = 0.0;
			final Property entryKey = entry.getKey();
			labels[index] = new JLabel(entryKey.getName(), SwingConstants.RIGHT);
			gridbag.setConstraints(labels[index], constraints);
			dataPanel.add(labels[index]);
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			constraints.weightx = 1.0;
			final Class<?> clazz = entryKey.getClazz();
			final String entryValue = entry.getValue();
			if (clazz == Boolean.class) {
				fields[index] = new RadioButtonBooleanChooser(entryValue);
			} else if (clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class) {
				fields[index] = new JTextField(new IntegerTextFieldLimiter(-1), entryValue, 0);
				((JTextField) fields[index]).setHorizontalAlignment(SwingConstants.RIGHT);
			} else if (clazz != null && clazz.isAnnotationPresent(OptionsChooserValues.class)) {
					final OptionsChooserValues annotation = clazz.getAnnotation(OptionsChooserValues.class);
					final JComboBox<String> values = new JComboBox<>(annotation.value());
					values.setSelectedItem(entryValue);
					fields[index] = values;
			} else {
				fields[index] = new JTextField(entryValue);
			}
			gridbag.setConstraints(fields[index], constraints);
			dataPanel.add(fields[index]);
			index++;
		}
		contentPane.add(new JScrollPane(dataPanel), BorderLayout.CENTER);

		// Set some sane size.
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final int height = (int) (screenSize.height * .75);

		final Dimension preferredSize = contentPane.getPreferredSize();
		if (preferredSize.height > height) {
			preferredSize.height = height;
			contentPane.setPreferredSize(preferredSize);
		}
		setDialogResizable(contentPane);
	}

	/**
	 * Shows a Dialog where the map can be edited. 
	 * @param title the title of the dialog.
	 * @return <code>true</code> if the dialog was accepted.
	 */
	public boolean editMap(final String title) {
		return JOptionPane.showConfirmDialog(parent, contentPane, 
			title, JOptionPane.OK_CANCEL_OPTION, 
			JOptionPane.PLAIN_MESSAGE, icon) == JOptionPane.OK_OPTION;
	}

	/**
	 * Returns a map with the edited data.
	 * @return a map with the edited data.
	 */
	public Map<String, String> getMap() {
		final Map<String, String> map = new TreeMap<>();
		for (int ii = 0; ii < labels.length; ii++) {
			if (fields[ii] instanceof BooleanChooser) {
				map.put(labels[ii].getText(),((BooleanChooser) fields[ii]).getValue());
			} else if (fields[ii] instanceof JComboBox) {
				map.put(labels[ii].getText(),
					((JComboBox<?>) fields[ii]).getSelectedItem().toString());
			} else {
				map.put(labels[ii].getText(),((JTextComponent) fields[ii]).getText());
			}
		}
		return map;
	}
}
