package org.pclg.gui;

import org.pclg.tools.LabeledComponent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.regex.Pattern;

/**
 * Que pueda solo aceptar datos numericos, en mayuscuals o minusculas, etc.
 */

public class JLabeledField extends JPanel implements LabeledComponent {
    private static final long serialVersionUID = -653378894054953899L;
    private final JLabel lbl;
	private final JTextField text;
	private boolean numeric;
	private boolean integer;
	private boolean natural;
	private boolean upperCase;
	private boolean lowerCase;
	private int maxLength;
    private final Pattern naturalPattern = Pattern.compile("\\d*");
    private final Pattern integerPattern = Pattern.compile("-?\\d*");
    private final Pattern numericPattern = Pattern.compile("-?\\d*\\.?\\d*");

	@Override
	public int getLabelWidth() {
		return 20;
	}

	@Override
	public JComponent getComponent() {
		return text;
	}

	public JLabeledField(final String label) {
		this(label, "");
	}

	public JLabeledField(final String label, final double number) {
		this(label, String.valueOf(number));
	}

	public JLabeledField(final String label, final boolean b) {
		this(label, String.valueOf(b));
	}


	public JLabeledField(final String label, final String texto) {
		setLayout(new GridLayout());
		lbl = new JLabel(label, SwingConstants.RIGHT);
		text = new JTextField();
		text.setDocument(new JLabeledFieldDocument());
		text.setText(texto);
		add(lbl);
		add(text);
		setBorder(BorderFactory.createEtchedBorder());
	}

	public void addActionListener(final ActionListener l) {
		text.addActionListener(l);
	}

	public void setEditable(final boolean editable) {
		text.setEditable(editable);
	}

	public void setEnabled(final boolean enabled) {
		text.setEnabled(enabled);
	}

	@Override
	public void setForeground(final Color color) {
		if (lbl != null) {
			lbl.setForeground(color);
		}
		if (text != null) {
			text.setForeground(color);
		}
	}

	public String getLabel() {
		return lbl.getText();
	}

	public JLabel getLabelComponent() {
		return lbl;
	}

	public void setLabel(final String label) {
		lbl.setText(label);
	}

	public String getText() {
		return text.getText();
	}

	public void setText(final String value) {
        if (isValid(value)) {
            text.setText(value);
        }
    }

    public void setText(final int s) {
		setText(String.valueOf(s));
	}

	public void setText(final double s) {
		setText(String.valueOf(s));
	}

	public void setText(final boolean s) {
		setText(String.valueOf(s));
	}


    /**
     * Validates if a string is acceptable for this field.
     * @param str the string to validate.
     * @return <code>true</code> if valid,<code>false</code> otherwise.
     */
    boolean isValid(final String str) {
        if (maxLength > 0) {
            if (str.length() > maxLength) {
                return false;
            }
        }

        if (integer && !integerPattern.matcher(str).matches()) {
            return false;
        }

        if (numeric && !numericPattern.matcher(str).matches()) {
            return false;
        }

        if (natural && !naturalPattern.matcher(str).matches()) {
            return false;
        }
        return true;
    }

    public void setAny() {
        numeric = integer = natural = upperCase = lowerCase = false;
   		text.setHorizontalAlignment(JTextField.LEFT);
    }

	public void setNumeric() {
		numeric = true;
        integer = false;
        natural = false;
		text.setHorizontalAlignment(JTextField.RIGHT);
	}

	public void setInteger() {
        numeric = false;
        integer = true;
        natural = false;
		text.setHorizontalAlignment(JTextField.RIGHT);
	}

	public boolean isInteger() {
        return integer || natural;
	}

	public void setNatural() {
        numeric = false;
        integer = false;
		natural = true;
		text.setHorizontalAlignment(JTextField.RIGHT);
	}

	public void setLowerCase() {
		upperCase = false;
		lowerCase = true;
        setText(getText().toLowerCase());
	}

	public void setUpperCase() {
		upperCase = true;
		lowerCase = false;
		setText(getText().toUpperCase());
	}

	public void setPlainCase() {
		upperCase = false;
		lowerCase = false;
	}

	public void setMaxLength(final int len) {
		maxLength = len;
		//text.setLength(len);
	}

	public void selectAll() {
		text.selectAll();
	}

	@Override
	public void requestFocus() {
		text.requestFocus();
	}

	public void setGBLayout(final GridBagLayout gridbag) {
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		setLayout(gridbag);
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.weightx = 0.0;
		gridbag.setConstraints(lbl, c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		gridbag.setConstraints(text, c);
	}

	/*
		implementacion de LabeledComponent
	*/

	/** @return Las dimensiones de la etiqueta del objeto */
	public Dimension getLeftComponentSize() {
		return lbl.getPreferredSize();
	}

	/** @return Las dimensiones de la etiqueta del objeto */
	public void setLeftComponentSize(final Dimension dimension) {
		lbl.setPreferredSize(dimension);
	}

	/** @return Las dimensiones del texto del objeto */
	public Dimension getRightComponentSize() {
		return text.getPreferredSize();
	}

	private static final int GAP = 7;

	/**
	 * @since 2002.05.16
	 */
	@Override
	public Dimension getMinimumSize() {
		final Dimension d1 = text.getMinimumSize();
		final Dimension d2 = lbl.getMinimumSize();
        return new Dimension(d1.width + d2.width + GAP,
            Math.max(d1.height, d2.height) + GAP);
	}

	/**
	 * @since 2002.05.16
	 */
	@Override
	public Dimension getPreferredSize() {
		final Dimension d1 = text.getPreferredSize();
		final Dimension d2 = lbl.getPreferredSize();
        return new Dimension(d1.width + d2.width + GAP,
            Math.max(d1.height, d2.height) + GAP);
	}

	/**
	 * @since 2002.05.16
	 */
	@Override
	public Dimension getMaximumSize() {
		final Dimension d1 = text.getPreferredSize();
		final Dimension d2 = lbl.getPreferredSize();
        return new Dimension(Integer.MAX_VALUE,
            Math.max(d1.height, d2.height));
	}

	@Override
	public synchronized void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
		text.addMouseListener(l);
	}

	public Document getDocument() {
		return text.getDocument();
	}

    public int toInt() {
        if (!isInteger()) {
            throw new IllegalStateException("not integer field");
        }
        final String _text = getText();
        return _text.trim().length() > 0 ? Integer.parseInt(_text) : 0;
    }

    class JLabeledFieldDocument extends PlainDocument {
        private static final long serialVersionUID = -5653036301547021090L;

        @Override
		public void insertString(final int offs, final String str,
				final AttributeSet attributeSet) throws BadLocationException {
			if (str == null) {
				return;
			}
            String insertable = str;
            if (upperCase) {
                insertable = insertable.toUpperCase();
            }
            if (lowerCase) {
                insertable = insertable.toLowerCase();
            }
            if (maxLength > 0 && getLength() + insertable.length() > maxLength) {
            	return;
            }
            if (isValid(insertable)) {
                super.insertString(offs, insertable, attributeSet);
            }
		}
	}
}
