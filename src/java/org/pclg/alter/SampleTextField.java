package org.pclg.alter;

import org.pclg.gui.JLabeledField;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.PlainDocument;
import java.awt.Color;
import java.util.concurrent.locks.StampedLock;
import java.util.regex.Pattern;

import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;

/**
 * @author El Coyote Cojo.
 * @since 07/07/2017.
 */
class SampleTextField extends JTextField {
    private static final long serialVersionUID = -1352173734819123062L;
    private final StampedLock mutex = new StampedLock();
	private final JLabeledField startField;
	private final JLabeledField lenField;

	SampleTextField(final JLabeledField startField, final JLabeledField lenField) {
		this.startField = startField;
		this.lenField = lenField;

		/* Highlights in different colors if the selection is made right-to-left or left-to-right. */
		class ReversibleHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
			private final Color reverseColor;
			private boolean reversed;

			private ReversibleHighlightPainter(final Color directColor, final Color reverseColor) {
				super(directColor);
				this.reverseColor = reverseColor;
			}

			@Override
			public Color getColor() {
				return reversed ? reverseColor : super.getColor();
			}

			private void setReversed(final boolean reversed) {
				this.reversed = reversed;
			}
		}

		final ReversibleHighlightPainter reversibleHighlightPainter =
			new ReversibleHighlightPainter(GREEN, ORANGE);

		addCaretListener(event -> {
			if (!mutex.isWriteLocked()) {
				final long lock = mutex.writeLock();
				try {
					final int length = getText().length();
					int startPos = event.getMark();
					int endPos = event.getDot();
					final int len = Math.abs(endPos - startPos);
					if (startPos > endPos) {
						startPos = endPos - length - 1 + len;
						reversibleHighlightPainter.setReversed(true);
					} else {
						reversibleHighlightPainter.setReversed(false);
					}
					startField.setText(String.valueOf(startPos));
					lenField.setText(String.valueOf(len));
					repaint();
				} finally {
					mutex.unlock(lock);
				}
			}
		});

		setCaret(new DefaultCaret() {
			/** serialVersionUID. */
			private static final long serialVersionUID = -7587361935544370088L;
			private static final int FOCUSED_BLINK_RATE = 100;
			private static final int UNFOCUSED_BLINK_RATE = 10_000;

			{
				setBlinkRate(FOCUSED_BLINK_RATE);
			}

			@Override
			protected Highlighter.HighlightPainter getSelectionPainter() {
				return reversibleHighlightPainter;
			}

			@Override
			public void setSelectionVisible(final boolean hasFocus) {
				// Keep the selection even if unfocused...
				super.setSelectionVisible(true);
				// ... but give feedback to the user.
				setBlinkRate(hasFocus ? FOCUSED_BLINK_RATE : UNFOCUSED_BLINK_RATE);
			}
		});

		final DocumentListener updateSampleListener = new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent e) {
				updateSampleSelection();
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				updateSampleSelection();
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				updateSampleSelection();
			}
		};
		startField.getDocument().addDocumentListener(updateSampleListener);
		lenField.getDocument().addDocumentListener(updateSampleListener);
//		setEditable(false);
		setDocument(new PlainDocument() {
            private static final long serialVersionUID = 5310238831720009172L;

            @Override
			public void insertString(final int offs, final String str, final AttributeSet a)
					throws BadLocationException {
				//	Do nothing
			}

			@Override
			public void remove(final int offs, final int len) throws BadLocationException {
				//	Do nothing
			}

			/**
			 * Only replaces full document.
			 */
			@Override
			public void replace(final int offset, final int length, final String text, final AttributeSet attrs)
					throws BadLocationException {
				writeLock();
				try {
					final int docLength = getLength();
					if (text != null && /*text.length() > 0 && */offset == 0 && length == docLength) {
						super.remove(0, docLength);
						super.insertString(offset, text, attrs);
					}
				} finally {
					writeUnlock();
				}
			}
		});
	}

	/**
	 * Actualiza la parte seleccionada del campo sample según los valores de start y len.
	 */
	private void updateSampleSelection() {
		if (!mutex.isWriteLocked()) {
            final String startText = startField.getText();
            final String lenText = lenField.getText();
            if (isNatural(startText) && isNatural(lenText)) {
                final int begin = Integer.parseInt(startText);
                final int end = begin + Integer.parseInt(lenText);
                select(begin, end);
            }
		}
	}

	/** Para verificar que un String representa un número natural. */
	private static final Pattern NATURAL_PATTERN = Pattern.compile("\\d+");

	private boolean isNatural(final String text) {
		return NATURAL_PATTERN.matcher(text).matches();
	}

	@Override
	public void setText(final String t) {
		final long lock = mutex.writeLock();
		try {
			super.setText(t);
		} finally {
			mutex.unlockWrite(lock);
		}
		updateSampleSelection();
	}
}
