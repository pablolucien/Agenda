package org.pclg.root;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
//import java.util.Hashtable;

final class FixedGridLayout implements LayoutManager {
	private final int rows;
	private static final int vGap = 1;
	private static final int hGap = 1;
	private final Dimension size;
	//private Hashtable compTable; //constraints (Rectangles)

	public FixedGridLayout(final int rows, final Dimension size) {
		this.rows = rows;
		this.size = size;
	}

	public String toString() {
		return"FixedGridLayout de " + rows + " filas de " + size.width + " x " + size.height;
	}

	@Override
	public void addLayoutComponent(final String name, final Component comp) {
	}

	@Override
	public void removeLayoutComponent(final Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(final Container container) {
		//Always add the container's insets!
		final Insets insets = container.getInsets();
		final int count = container.getComponentCount();
		final Dimension dim = new Dimension(0, 0);
		dim.width = (size.width + hGap) * (int)Math.ceil((double)count / rows) + insets.left + insets.right;
		dim.height = (size.height + vGap) * rows + insets.top + insets.bottom;
		dim.height += 15; // ??? Por el posible scrollbar horizontal
		return dim;
	}

	@Override
	public Dimension minimumLayoutSize(final Container container) {
		return preferredLayoutSize(container);
	}

	@Override
	public void layoutContainer(final Container container) {
		final Insets insets = container.getInsets();

		for (int ii = 0, count = container.getComponentCount(); ii < count; ii++) {
			final Component component = container.getComponent(ii);
			final int xPos = insets.left + ii / rows * (size.width + hGap);
			final int yPos = insets.top + ii % rows * (size.height + vGap);
			component.setBounds(xPos, yPos, size.width, size.height);
		}
	}
}
