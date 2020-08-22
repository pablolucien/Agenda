// ******************************** package
package org.pclg.gui;

// ******************************** imports

import org.pclg.tools.LabeledComponent;
import org.pclg.tools.LengthControlledField;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Que pueda solo aceptar datos numericos, en mayuscuals o minusculas, etc.
 *
 * @author El Coyote Cojo
 * @version 2003.ago.20 19:54:15, CEST
 */
public final class DirSelectorField extends JPanel implements LabeledComponent,
		ActionListener {

    private static final long serialVersionUID = -1939920298015175981L;
    private final JButton button;
    private DirTree dirTree;
    private Window dirTreeParent;
    private final LengthControlledField text;

    public DirSelectorField(final String label) {
        this(label, "");
    }

    private DirSelectorField(final String label, final String texto) {
        final GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.0;
        
        button = new JButton(label);
        layout.setConstraints(button, constraints);
        add(button);
        button.addActionListener(this);

        text = new LengthControlledField(texto);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 1.0;
        layout.setConstraints(text, constraints);
        add(text);
        setBorder(BorderFactory.createEtchedBorder());
    }

    public synchronized void addActionListener(final ActionListener listener) {
        text.addActionListener(listener);
    }

    //implementacion de ActionListener
    @Override
	public void actionPerformed(final ActionEvent ev) {
        if (dirTree == null) {
            if (dirTreeParent == null) {
                dirTreeParent = new JFrame();
            }
            final JDialog dialog = new JDialog(dirTreeParent,
				Dialog.ModalityType.APPLICATION_MODAL);
            dirTree = new DirTree(dialog);
        }
		dirTree.setFile(new File(getText()));
        dirTree.setVisible(true);

		if (dirTree.accepted()) {
			dirTree.getFile().ifPresent(ff -> setText(ff.getAbsolutePath()));
		}
    }

    public void setEditable(final boolean editability) {
        text.setEditable(editability);
    }

    @Override
	public void setForeground(final Color color) {
		if (color != null) {
			if (button != null) {
				button.setForeground(color);
			}
			if (text != null) {
				text.setForeground(color);
			}
		}
	}

    public synchronized String getLabel() {
        return button.getText();
    }

    public synchronized Component getButton() {
        return button;
    }

    public synchronized void setLabel(final String caption) {
        button.setText(caption);
    }

    public synchronized String getText() {
        return text.getText();
    }

    public synchronized void setText(final String s) {
        text.setText(s);
    }

    public synchronized void setText(final int s) {
        text.setText(String.valueOf(s));
    }

    public synchronized void setText(final double s) {
        text.setText(String.valueOf(s));
    }

    public synchronized void setText(final boolean s) {
        text.setText(String.valueOf(s));
    }


    @Override
	public void requestFocus() {
        text.requestFocus();
    }

    /*
        implementacion de LabeledComponent
    */
    /**
     * @return La etiqueta del objeto
     */
    @Override
	public int getLabelWidth() {
        return 0;
    }

    public Dimension getLeftComponentSize() {
        return button.getPreferredSize();
        //return(button.getPreferredSize());
    }

    @Override
	public JComponent getComponent() {
        return text;
    }

    public Dimension getRightComponentSize() {
        return text.getPreferredSize();
    }


    private static final class UpperCaseDocument extends PlainDocument {

        private static final long serialVersionUID = 168148797821726838L;

        @Override
		public void insertString(final int offs, final String str, final AttributeSet attributes)
                throws BadLocationException {

            if (str == null) {
                return;
            }
            if (getLength() + str.length() > 10) {
                return;
            }
            final char[] upper = str.toCharArray();
            for (int ii = 0; ii < upper.length; ii++) {
                upper[ii] = Character.toUpperCase(upper[ii]);
            }
            super.insertString(offs, new String(upper), attributes);
        }
    }

    public Window getDirTreeParent() {
        return dirTreeParent;
    }

    public void setDirTreeParent(final Window dirTreeParent) {
        this.dirTreeParent = dirTreeParent;
    }
}
