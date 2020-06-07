package org.pclg.gui;

import org.pclg.tools.GUITools;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;

/**
 * @author El Coyote Cojo
 * @since 16/09/18 10:18
 */
public final class FancyButtonPanel extends JPanel {
    private static final Color LAST_BUTTON_COLOR = new Color(224, 57, 23);
    private final Component[] components;
    private final Component glue = Box.createGlue();

    public enum Orientation {
        HORIZONTAL, VERTICAL;

    }

    public FancyButtonPanel(final Component ... components) {
        this(Orientation.HORIZONTAL, components);
    }

    public FancyButtonPanel(final Orientation orientation, final Component ... components) {
        this.components = components.clone();
        setLayout(new BoxLayout(this, orientation == Orientation.VERTICAL ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS));
        GUITools.setEqualPreferredDimensions(components);
        for (int ii = 0, len = components.length - 1; ii < len; ii++) {
            add(components[ii]);
        }
        add(glue);
        final Component lastComponent = components[components.length - 1];
        lastComponent.setForeground(LAST_BUTTON_COLOR);
        add(lastComponent);
        Arrays.stream(components).forEach(component -> component.addPropertyChangeListener("text", evt -> {
            ((Component) evt.getSource()).setPreferredSize(null);
            remove(glue);
            GUITools.setEqualPreferredDimensions(components);
            add(glue, components.length - 1);
        }));
    }

    public void setComponentsAlignment(final int alignment) {
        Arrays.stream(components).forEach(component -> {
            if (component instanceof AbstractButton) {
                ((AbstractButton) component).setHorizontalAlignment(alignment);
            }
        });
    }
}
