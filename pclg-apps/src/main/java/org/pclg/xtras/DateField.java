// Este es un paquete ad hoc para poner cosas que he obtenido de otras fuentes
package org.pclg.xtras;

// Tomado de http://forum.java.sun.com/thread.jsp?forum=57&thread=119575

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/** @author Tomado de http://forum.java.sun.com/thread.jsp?forum=57&thread=119575 */
public class DateField extends JTextField {
    private static final long serialVersionUID = -4623724444226679787L;
    private final KeyLsnr myKeyLsnr;

	public DateField() {
		setText(" / / ");
		myKeyLsnr = new KeyLsnr();
		addKeyListener(myKeyLsnr);
	}

	// Disable cut operations
	@Override
	public void cut() {
	}

	// Disable paste operations
	@Override
	public void paste() {
	}

	@Override
	protected Document createDefaultModel() {
		return new DateDocument();
	}

	class DateDocument extends PlainDocument {
        private static final long serialVersionUID = 313378498433232318L;

        @Override
		public void insertString(final int offset, final String str,
				final AttributeSet a)
				throws BadLocationException {

			final char[] insChars = str.toCharArray();
			boolean valid = true;
			if (insChars.length + offset < 11) {
				for (int i = 0; i < insChars.length; i++) {
					if (!(Character.isDigit(insChars[i]) || insChars[i] == ' '))
					{
						if (insChars[i] != '/' || ((offset + i != 2) && (
								offset + i != 5))) {
							valid = false;
							break;
						}
					}
					if (insChars[i] == '/' && getText(offset, 1).equals("/")) {
						valid = false;
						break;
					}
				}
			} else {
				valid = false;
			}

			if (valid) {
				super.insertString(offset, str, a);
				if (getLength() >= 11) {
					super.remove(offset + str.length(), getLength() - 10);
				}
			}
			remove();
		}

		public void remove() throws BadLocationException {
			super.remove(2, 1);
			super.insertString(2, "/", null);
			super.remove(5, 1);
			super.insertString(5, "/", null);
		}

	}//eo DateDoc

	class KeyLsnr extends KeyAdapter {
		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				final int pos = getCaretPosition();
				if (pos > 0) {
					final char[] text = getText().toCharArray();
					text[pos - 1] = ' ';
					text[2] = '/';
					text[5] = '/';
					switch (pos) {
					case 1:
						text[0] = text[1];
						text[1] = ' ';
						break;
					case 4:
						text[3] = text[4];
						text[4] = ' ';
						break;
					default:
						break;
					}
					setText(new String(text));
					setCaretPosition(pos - 1);
					e.consume();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				final int pos = getCaretPosition();
				if (pos < 10) {
					final char[] text = getText().toCharArray();
					text[pos] = ' ';
					text[2] = '/';
					text[5] = '/';
					switch (pos) {
					case 0:
						text[0] = text[1];
					case 1:
						text[1] = ' ';
						break;
					case 2:
						break;
					case 3:
						text[3] = text[4];
					case 4:
						text[4] = ' ';
						break;
					case 5:
						break;
					case 6:
						text[6] = text[7];
					case 7:
						text[7] = text[8];
					case 8:
						text[8] = text[9];
					case 9:
						text[9] = ' ';
					default:
						break;
					}
					setText(new String(text));
					setCaretPosition(pos);
					e.consume();
				}
			} else if (e.getKeyChar() == '/') {
				final int pos = getCaretPosition();

				if (pos < 3) {
					setCaretPosition(3);
				} else if (pos < 6) {
					setCaretPosition(6);
				}
			}
		}
	}
}//eo DateField
