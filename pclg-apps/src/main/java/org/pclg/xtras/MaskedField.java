// Este es un paquete ad hoc para poner cosas que he obtenido de otras fuentes
package org.pclg.xtras;

// tomado de http://forum.java.sun.com/thread.jsp?forum=57&thread=119575

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tomado de http://forum.java.sun.com/thread.jsp?forum=57&thread=119575
 */
public class MaskedField extends JTextField {

    private static final long serialVersionUID = 8305215483354532112L;
    private final StringBuffer text;

	private MaskedField(final String format, final char delimeter) {
		this(format, new char[]{delimeter});
	}

	private MaskedField(final String format, final char[] delimeters) {
		super(format.length() + 1);
		final List<Integer> delims = new ArrayList<>();
		setFont(new Font("Monospaced", Font.BOLD, 14));
		final int length = format.length();
		text = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			char currentChar = ' ';
			for (final char delimeter : delimeters) {
				if (format.charAt(i) == delimeter) {
					delims.add(Integer.valueOf(i));
					currentChar = format.charAt(i);
				}
			}
			text.append(currentChar);
		}
		setCaretPosition(0);
		addKeyListener(new MaskedFieldListener(delims));
		setText(text.toString());
	}

	private class MaskedFieldListener extends KeyAdapter {
		private final List<Integer> delims;

		private MaskedFieldListener(final List<Integer> delims) {
			this.delims = delims;
		}

		@Override
		public void keyPressed(final KeyEvent key) {
			int caretPos = getCaretPosition();
			boolean isDeleteValid = true;
			boolean isBackValid = true;
			for (final Integer delim : delims) {
				final int x = delim.intValue();
				if (caretPos == x) {
					isDeleteValid = false;
					break;
				}
				if (caretPos == x + 1) {
					isBackValid = false;
					break;
				}
			}
			switch (key.getKeyCode()) {
			case KeyEvent.VK_BACK_SPACE:
				if (caretPos > 0 && isBackValid)                // ?????????? El 0 lo agregué yo
				{
					text.insert(caretPos, ' ');
					text.deleteCharAt(getCaretPosition() - 1);
					caretPos -= 1;
				}
				if (!isBackValid) {
					caretPos -= 1;
				}
				break;
			case KeyEvent.VK_DELETE:
				if (caretPos <= text.length() - 1 && isDeleteValid) {
					text.insert(caretPos, ' ');
					text.deleteCharAt(getCaretPosition() + 1);
					caretPos += 1;
				}
				if (!isDeleteValid) {
					caretPos += 1;
				}
				break;
			default:
				if (Character.isDigit(key.getKeyChar())) {
					if (caretPos <= text.length() - 1 && isDeleteValid) {
						text.setCharAt(caretPos, key.getKeyChar());
					}
					if (caretPos <= text.length() - 2 && !isDeleteValid) {
						text.setCharAt(caretPos + 1, key.getKeyChar());
						caretPos += 1;
					}
					if (caretPos <= text.length() - 1) {
						caretPos += 1;
					}
				}
				break;
			}
			key.consume();
			setText(text.toString());
			setCaretPosition(caretPos);
		}

		@Override
		public void keyTyped(final KeyEvent key) {
			key.consume();
		}
	}

	public static void main(final String[] args) {
		final JFrame frame = new JFrame("A Few Masked Fields");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JPanel panel = new JPanel();
		panel.add(new MaskedField("xx/xx/xxxx", '/'));
		panel.add(new MaskedField("xxx.xxx.xxx.xxx", '.'));
		panel.add(new MaskedField("$xxx.xx", new char[]{'$', '.'}));
		panel.add(new MaskedField("(xxx)xxx-xxxx", new char[]{'(', ')', '-'}));
		panel.add(new org.pclg.xtras.DateField());
		frame.getContentPane().add(panel);
		frame.setSize(250, 100);
		frame.setVisible(true);
	}
}
