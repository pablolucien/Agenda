package org.pclg.gui;

import javax.swing.JScrollPane;
import java.awt.Component;

/**
 * @since 08/08/2018.
 */
public class ManagedScrollPane extends JScrollPane {
    private final ScrollBarManager scrollBarManager = new ScrollBarManager();

    public ManagedScrollPane(final Component target) {
        super(target);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        scrollBarManager.setUpScrollBar(this);
    }

    public ManagedScrollPane(final Component target, final ScrollBarManager.Position position) {
        this(target);
        setScrollBarPosition(position);
    }

    public final void setScrollBarPosition(final ScrollBarManager.Position position) {
        scrollBarManager.setScrollBarPosition(position);
    }

    public final String getScrollBarPosition() {
        return scrollBarManager.getScrollBarPosition();
    }
}
