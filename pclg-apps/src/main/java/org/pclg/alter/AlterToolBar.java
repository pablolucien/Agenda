package org.pclg.alter;

import javax.swing.BorderFactory;
import javax.swing.JToolBar;

/**
 * @since 29/06/2017.
 */
final class AlterToolBar extends JToolBar {
    private static final long serialVersionUID = 593746806226431319L;

    AlterToolBar() {
        super(JToolBar.VERTICAL);
        setBorder(BorderFactory.createCompoundBorder(getBorder(),
            BorderFactory.createEtchedBorder()));
    }
}
