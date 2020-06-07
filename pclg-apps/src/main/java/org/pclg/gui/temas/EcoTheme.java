package org.pclg.gui.temas;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * Un tema con colores 'ecologicos'.
 */
public final class EcoTheme extends DefaultMetalTheme {
    // primary colors
    /** Foreground texto en labels.*/
    private final ColorUIResource primary1 = new ColorUIResource(88, 88, 51);			    // Foreground texto en labels
    private final ColorUIResource primary2 = new ColorUIResource(61, 96, 98);				// Foreground slider y scrollbar
    private final ColorUIResource primary3 = new ColorUIResource(200, 200, 82);				// Texto seleccionado

    // secondary colors
    private final ColorUIResource secondary1 = new ColorUIResource(170, 170, 170);			// Bordes de componentes
    private final ColorUIResource secondary2 = new ColorUIResource(180, 180, 180);			// Bordes de componentes
    private final ColorUIResource secondary3 = new ColorUIResource(200, 224, 224);			// Background paneles

    // other colors
    private final ColorUIResource controlTextColor = new ColorUIResource(100, 100, 100);	// Control Text Color
    private final ColorUIResource desktopColor = new ColorUIResource(153, 153, 0);		    // Desktop Color
    private final ColorUIResource whiteColor = new ColorUIResource(200, 200, 200);	        // White Color

    // methods
    @Override
	public String getName() {
        return "Eco";
    }

    @Override
	protected ColorUIResource getPrimary1() {
        return primary1;
    }

    @Override
	protected ColorUIResource getPrimary2() {
        return primary2;
    }

    @Override
	protected ColorUIResource getPrimary3() {
        return primary3;
    }

    @Override
	protected ColorUIResource getSecondary1() {
        return secondary1;
    }

    @Override
	protected ColorUIResource getSecondary2() {
        return secondary2;
    }

    @Override
	protected ColorUIResource getSecondary3() {
        return secondary3;
    }

    @Override
	public ColorUIResource getControlTextColor() {
        return controlTextColor;
    }

    @Override
	public ColorUIResource getDesktopColor() {
        return desktopColor;
    }

    @Override
	public ColorUIResource getWhite() {
        return whiteColor;
    }	//?????????
}
