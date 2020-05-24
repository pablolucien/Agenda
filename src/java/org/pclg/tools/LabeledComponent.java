package org.pclg.tools;

import javax.swing.JComponent;

/**
	Los objetos que implementan esta interfaz poseen un componente etiquetado
*/
public interface LabeledComponent {
	/** @return La etiqueta del objeto */
	int getLabelWidth();
//	public Dimension getLeftComponentSize();
	/** @return El componente del objeto */
	JComponent getComponent();
//	public Dimension getRightComponentSize();
}
