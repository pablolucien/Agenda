/*
 * Creado el 18-mar-2008
 */
package org.pclg.dictionary;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Un autor en busca de personajes.
 */
final class TableSelector extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1098226478940517700L;
    private final JRadioButton standardCh = new JRadioButton("Standard", true);
	private final JRadioButton dudosasCh = new JRadioButton("Dudosas");
	private final JRadioButton nuevasCh = new JRadioButton("Nuevas");

	/**
	 * 
	 */
	public TableSelector() {
		add(new JLabel(new ImageIcon(DictionaryPropertites.getProperty("animated.icon.path"))));
		final ButtonGroup group = new ButtonGroup();
		add(standardCh);
		group.add(standardCh);
		standardCh.addActionListener(this);
		add(dudosasCh);
		group.add(dudosasCh);
		dudosasCh.addActionListener(this);
		add(nuevasCh);
		group.add(nuevasCh);
		nuevasCh.addActionListener(this);
	}

	String getSelected() {
		return standardCh.isSelected() ? "entries" : dudosasCh.isSelected() ? "dudosas" : "nuevas";
	}

	/* (sin Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		firePropertyChange("tabla", null, null);
	}
}
