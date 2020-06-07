package org.pclg.gui;

import org.pclg.tools.StringTools;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.Toolkit;

/**
 * Limita el tamaño del documento y solo acepta números enteros no negativos.
 * Con las teclas '+' y '-' se incrementa y decrementa el valor del campo. 
 *
 * @author El Coyote Cojo
 * @since 25-sep-2007 18:37:03
 */
public final class IntegerTextFieldLimiter extends PlainDocument {
    private static final long serialVersionUID = 6802389614431291583L;
    private final int maxLen;
	private static final Toolkit DEFAULT_TOOLKIT = Toolkit.getDefaultToolkit();

	public IntegerTextFieldLimiter(final int len) {
		maxLen = len;
	}

	@Override
	public void insertString(final int offset,  final String str,
			final AttributeSet attributeSet) throws BadLocationException {
		if (str != null) {
			final int length = getLength();
			final String text = getText(0, length);
			if (str.charAt(0) == '+') {
				final String newValue = String.valueOf(Long.parseLong(StringTools.isEmptyOrBlank(text) ? "0" : text) + 1);
				if (maxLen < 0 || newValue.length() <= maxLen) {
					replace(0, length, newValue, attributeSet);
				}
			} else if (str.charAt(0) == '-') {
				if ("0".equals(text)) {
					DEFAULT_TOOLKIT.beep();
					return;
				}
				final String newValue = String.valueOf(Long.parseLong(StringTools.isEmptyOrBlank(text) ? "1" : text) - 1);
				if (maxLen < 0 || newValue.length() <= maxLen) {
					replace(0, length, newValue, attributeSet);
				}
			} else if (maxLen > 0 && getLength() + str.length() > maxLen
					|| !str.matches("\\d+")
					|| offset == 0 && str.charAt(0) == '0' && getLength() > 1) {
				DEFAULT_TOOLKIT.beep();
			} else {
				super.insertString(offset, str, attributeSet);
			}
		}
	}
}
