package org.pclg.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.Toolkit;

/**
 * @author El Coyote Cojo
 * @since 25-sep-2007 18:37:03
 */
public class TextFieldLimiter extends PlainDocument {
	private static final long serialVersionUID = -1153957794374340028L;
	private final int maxChar;
	private static final Toolkit DEFAULT_TOOLKIT = Toolkit.getDefaultToolkit();

	public TextFieldLimiter(final int len) {
		if (len <= 0) {
			throw new IllegalArgumentException("La longitud debe ser positiva.");
		}
		maxChar = len;
	}

	@Override
	public void insertString(final int offs, final String str,
			final AttributeSet attributeSet) throws BadLocationException {
		if (str != null && getLength() + str.length() > maxChar) {
			DEFAULT_TOOLKIT.beep();
		} else {
			super.insertString(offs, str, attributeSet);
		}
	}
}
