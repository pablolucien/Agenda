// ******************************** package
package org.pclg.tools;

import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Hashtable;

/*
	Un Layout Manager para desplegar componentes etiquetados, de modo que la etiqueta
	quede alineada a la derecha y el componente a la izquierda
*/
class LabeledComponentLayout1 implements LayoutManager {
	/** donde se alinean las etiquetas */
	private int pivot;
	private final int vGap = 1;
	private int hGap = 1;
	/** El tamaño a asignar al contenedor */
	private Dimension size;
	private Hashtable compTable = new Hashtable();

	public LabeledComponentLayout1() {
	}

	public String toString() {
		return("LabeledComponentLayout1");
	}

	public void addLayoutComponent(final String name, final Component comp) {
	}

	public void removeLayoutComponent(final Component comp) {
	}

	public Dimension preferredLayoutSize(final Container container) {
System.out.println("preferredLayoutSize()");
		//Always add the container's insets!
		final Insets insets = container.getInsets();
		final Component[] components = container.getComponents();
		final Dimension dim = new Dimension(0, 0);
		pivot = 0;
		for(int i = 0; i < components.length; i++) {
if(components[i] instanceof LabeledComponent) ((Container)components[i]).setLayout(new FlowLayout());	// ????
			if(dim.width < components[i].getPreferredSize().width)
				dim.width = components[i].getPreferredSize().width;
			dim.height += components[i].getPreferredSize().height;
			if(components[i] instanceof LabeledComponent) {
				final int x = ((LabeledComponent) components[i]).getLabelWidth();
				//int x = ((LabeledComponent) components[i]).getLeftComponentSize().width;
				if(x > pivot)
					pivot = x;
			}
		}
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom + vGap * components.length;
		//dim.height += 15; // ??? Por el posible scrollbar horizontal

		if(container instanceof JComponent) {
			final javax.swing.border.Border border = ((JComponent)container).getBorder();
			if(border != null) {
				final Insets insets1 = border.getBorderInsets(container);
				dim.width += insets1.left + insets1.right;
				dim.height += insets1.top + insets1.bottom;
			}
		}

		return(dim);
	}

	public Dimension minimumLayoutSize(final Container container) {
		return(preferredLayoutSize(container));
	}

	public void layoutContainer(final Container container) {
System.out.println("layoutContainer()");
		final int count = container.getComponentCount();
		final Insets insets = container.getInsets();
		int x = insets.left;
		int y = insets.top;
		int borderInsetsX = 0;
		int borderInsetsY = 0;

		if(container instanceof JComponent) {
			final javax.swing.border.Border border = ((JComponent)container).getBorder();
			if(border != null) {
				final Insets insets1 = border.getBorderInsets(container);
				borderInsetsX = insets1.left;
				borderInsetsY = insets1.top;
				//y += borderInsetsY;
			}
		}
		
		for (int i = 0; i < count; i++) {
			final Component c = container.getComponent(i);
			if(c instanceof LabeledComponent) {
//System.out.print("getLabelWidth() = " + ((LabeledComponent) c).getLabelWidth());
//System.out.print("   c.getPreferredSize().width = " +  c.getPreferredSize().width);
				x = pivot - ((LabeledComponent) c).getLabelWidth() + borderInsetsX;
				//x = pivot - ((LabeledComponent) c).getLeftComponentSize().width + borderInsetsX;
			}
			//c.setBounds(x, y, c.getPreferredSize().width, c.getPreferredSize().height);
			c.setBounds(x, y, ((LabeledComponent) c).getLabelWidth() * 2, c.getPreferredSize().height); 	// Averiguar el tamaño del de la derecha
//System.out.println("  x = " +  x + " y = " + y);
//System.out.println(c.getBounds());
			y += c.getPreferredSize().height;
			y += vGap;
		}
	}
}
