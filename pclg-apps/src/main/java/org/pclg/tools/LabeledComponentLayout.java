// ******************************** package
package org.pclg.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
	Un Layout Manager para desplegar componentes etiquetados, de modo que la etiqueta
	quede alineada a la derecha y el componente a la izquierda
*/
class LabeledComponentLayout implements LayoutManager {
	/** donde se alinean las etiquetas */
	private int pivot;
	private int vGap = 1;
	private int hGap = 1;
	/** El tamaño a asignar al contenedor */
	private Dimension size;
	//private Hashtable componentTable = new Hashtable(); //constraints (Rectangles)

	public LabeledComponentLayout() {
	}

	public String toString() {
		return("LabeledComponentLayout");
	}

	public void addLayoutComponent(final String name, final Component comp) {
	}

	public void removeLayoutComponent(final Component comp) {
	}

	public Dimension preferredLayoutSize(final Container container) {
		//Always add the container's insets!
		final Insets insets = container.getInsets();
		final Component[] components = container.getComponents();
		final Dimension dim = new Dimension(0, 0);
		pivot = 0;
		for(int i = 0; i < components.length; i++) {
			if(dim.width < components[i].getPreferredSize().width) {
				dim.width = components[i].getPreferredSize().width;
			}
			dim.height += components[i].getPreferredSize().height;
			if(components[i] instanceof LabeledComponent) {
				final int x = ((LabeledComponent) components[i]).getLabelWidth();
				if(x > pivot) {
					pivot = x;
				}
			}
		}
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;
		dim.height += 15; // ??? Por el posible scrollbar horizontal

		return(dim);
	}

	public Dimension minimumLayoutSize(final Container container) {
		return(preferredLayoutSize(container));
	}

	public void layoutContainer(final Container container) {
		final Insets insets = container.getInsets();
		final int count = container.getComponentCount();
		int x = insets.left;
		int y = insets.top;
		for (int i = 0; i < count; i++) {
			final Component c = container.getComponent(i);
			if(c instanceof LabeledComponent) {
				x = pivot - ((LabeledComponent) c).getLabelWidth();
			}
			//c.setBounds(x, y, c.getPreferredSize().width, c.getPreferredSize().height);
			c.setBounds(x, y, ((LabeledComponent) c).getLabelWidth() * 2, c.getPreferredSize().height);
			y += c.getPreferredSize().height;
		}
	}
}
