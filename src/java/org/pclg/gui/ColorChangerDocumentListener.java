package org.pclg.gui;

import javax.swing.JColorChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.Color;

/**
 * DocumentListener que cambia los colores de un JTextComponent dependiendo de su contenido. De
 * momento sólo cambia el background si el documento está vacío o no.
* @author El Coyote Cojo
* @since 5/08/16 6:30
*/
public class ColorChangerDocumentListener implements DocumentListener {
	private static final String COLOR_PROMPT = "Entre todos los colores su majestas escoja un color.";
	private final JTextComponent component;
	private Color filledColor;
	private Color emptyColor;

	public ColorChangerDocumentListener(final JTextComponent component, final Color filledColor,
			final Color emptyColor) {
		this.component = component;
		this.filledColor = filledColor;
		this.emptyColor = emptyColor;
		updateColor();
	}

	public ColorChangerDocumentListener(final JTextComponent component) {
		this(component, Color.cyan, Color.lightGray);
	}


	@Override
	public void insertUpdate(final DocumentEvent ev) {
		updateColor();
	}

	@Override
	public void removeUpdate(final DocumentEvent ev) {
		updateColor();
	}

	@Override
	public void changedUpdate(final DocumentEvent ev) {
		updateColor();
	}

	private void updateColor() {
		component.setBackground(component.getDocument().getLength() > 0 ?
			filledColor : emptyColor);
	}

	public Color getFilledColor() {
		return filledColor;
	}

	public void setFilledColor(final Color color) {
		filledColor = color;
	}

	public void chooseFilledColor() {
		final Color color = JColorChooser.showDialog(component, COLOR_PROMPT, filledColor);
		if (color != null) {
			filledColor = color;
		}
	}

	public Color getEmptyColor() {
		return emptyColor;
	}

	public void setEmptyColor(final Color color) {
		emptyColor = color;
	}

	public void chooseEmptyColor() {
		final Color color = JColorChooser.showDialog(component, COLOR_PROMPT, emptyColor);
		if (color != null) {
			emptyColor = color;
		}
	}
}
