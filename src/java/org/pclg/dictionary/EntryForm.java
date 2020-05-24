/*
 * Creado el 04-mar-2008
 */
package org.pclg.dictionary;

import org.pclg.tools.GUITools;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Un autor en busca de personajes.
 */
final class EntryForm extends JDialog implements ActionListener {
    private static final long serialVersionUID = 5406381837843994601L;
    private final JTextField word = new JTextField(50);
	private final JTextArea definition = new JTextArea(5, 50);
	private final JButton okBt	= new JButton("Fale");
	private final JButton cancelBt = new JButton("Pírate");
	private boolean dialogAccepted;

	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public EntryForm(final Frame owner) throws HeadlessException {
		super(owner, "Introduzca palabra y definición", true);
		final Container contentPane = getContentPane();
		contentPane.add(word, BorderLayout.NORTH);
		contentPane.add(definition, BorderLayout.CENTER);
		final JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		buttonPanel.add(okBt);
		okBt.addActionListener(this);
		buttonPanel.add(cancelBt);
		cancelBt.addActionListener(this);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		pack();
		GUITools.center(this, owner);
	}

	/**
	 *
	 * @return
	 */
	public DictionaryEntry getWord() {
		return new DictionaryEntry(word.getText().trim(), definition.getText().trim());
	}


	/* (sin Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent event) {
		final Object source = event.getSource();
		if (source == okBt) {
			dialogAccepted = true;
			setVisible(false);
		} else if (source == cancelBt) {
			dialogAccepted = false;
			setVisible(false);
		}
	}
	/**
	 * @return Devuelve dialogAccepted.
	 */
	public boolean isDialogAccepted() {
		return dialogAccepted;
	}
}
