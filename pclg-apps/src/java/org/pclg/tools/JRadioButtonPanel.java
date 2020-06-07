package org.pclg.tools;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridLayout;

public final class JRadioButtonPanel extends JPanel {
    private static final int VERTICAL = 0;
    private static final int HORIZONTAL = 1;
    private static final long serialVersionUID = -3923983590804279569L;
    private final ButtonGroup cbg = new ButtonGroup();

    public JRadioButtonPanel() {
        setBorder(BorderFactory.createEtchedBorder());
    }

    public JRadioButton createButton(final String caption) {
        return createButton(caption, false);
    }

    private JRadioButton createButton(final String caption, final boolean selected) {
        final JRadioButton cb = new JRadioButton(caption, selected);
        cbg.add(cb);   // Agrupacion logica
        add(cb);       // Agrupacion fisica
        return cb;
    }

    public void setStyle(final int style) {
        switch (style) {
		case VERTICAL:
            setLayout(new GridLayout(0, 1));
			break;
		case HORIZONTAL:
            setLayout(new GridLayout(1, 0));
			break;
		default:
			throw new IllegalArgumentException("style: " + style);
        }
    }
}
