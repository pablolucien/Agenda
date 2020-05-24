package org.pclg.gui.temas;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * Un tema con colores 'monocromaticos'.
 */
public final class MonoTheme extends DefaultMetalTheme {
    // primary colors
    /** Foreground texto en labels.*/
    private final ColorUIResource primary1 = new ColorUIResource(0, 0, 0);					// Foreground texto en labels
    private final ColorUIResource primary2 = new ColorUIResource(128, 128, 128);			// Foreground slider y scrollbar
    private final ColorUIResource primary3 = new ColorUIResource(204, 204, 204);			// Texto seleccionado

    // secondary colors
    private final ColorUIResource secondary1 = new ColorUIResource(70, 70, 70);				// Bordes de componentes
    private final ColorUIResource secondary2 = new ColorUIResource(128, 128, 128);			// Bordes de componentes
    //private final ColorUIResource secondary3 = new ColorUIResource(255, 255, 255);		// Background paneles
    private final ColorUIResource secondary3 = new ColorUIResource(204, 204, 204);			// Background paneles

    // other colors
    private final ColorUIResource controlTextColor = new ColorUIResource(100, 100, 100);	// Control Text Color
    private final ColorUIResource desktopColor = new ColorUIResource(153, 153, 0);		    // Desktop Color
    private final ColorUIResource whiteColor = new ColorUIResource(10, 10, 10);		        // White Color

    // methods
    @Override
	public String getName() {
        return "Mono";
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

    //public ColorUIResource getControlTextColor() { return controlTextColor; }

    //public ColorUIResource getDesktopColor() { return desktopColor; }

    //public ColorUIResource getWhite() { return whiteColor; }	//?????????
}
