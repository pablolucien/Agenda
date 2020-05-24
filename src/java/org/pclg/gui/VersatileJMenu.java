package org.pclg.gui;

import javax.swing.JMenu;

/**
 * @author El Coyote Cojo
 * @since 4/04/16 19:43
 */
public class VersatileJMenu extends JMenu {
    private static final long serialVersionUID = 5745221434922537009L;

    @Override
	public void setText(final String text) {
		String label = text;
		// Si el label contiene '&' y no es el ultimo caracter
		// el siguiente caracter es el mnemonico
		final int indice = label.indexOf('&');
		if (indice > -1 && indice < label.length() - 1) {
			final char mnemo = label.charAt(indice + 1);
			label = label.substring(0, indice) + label.substring(indice + 1);
			setMnemonic(mnemo);
		}

//		si el label contiene '^'
//		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));

		super.setText(label);
	}
}
