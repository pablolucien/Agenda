package org.pclg.tools;

import java.awt.Rectangle;

/**
 * @since 22/11/2018.
 */
public final class BoundsInfo {
    public static final Rectangle NULL_RECTANGLE = new Rectangle();
    private boolean maximized;
    private boolean minimized;
    private Rectangle bounds = NULL_RECTANGLE;

    public BoundsInfo() {
    }

    public BoundsInfo(final Rectangle bounds) {
        setBounds(bounds);
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(final boolean maximized) {
        this.maximized = maximized;
    }

    public boolean isMinimized() {
        return minimized;
    }

    public void setMinimized(final boolean minimized) {
        this.minimized = minimized;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(final Rectangle bounds) {
        this.bounds = bounds == null ? NULL_RECTANGLE : bounds;
    }
}
