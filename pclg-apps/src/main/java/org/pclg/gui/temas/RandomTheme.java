package org.pclg.gui.temas;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * Un tema con colores 'al azar'.
 */
public final class RandomTheme extends DefaultMetalTheme {
    // primary colors
    /** Foreground texto en labels.*/
    private final ColorUIResource primary1 = new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());			// Foreground texto en labels
    private final ColorUIResource primary2 = new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());			// Foreground slider y scrollbar
    private final ColorUIResource primary3 = new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());			// Texto seleccionado

    // secondary colors
    private final ColorUIResource secondary1 = new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());		// Bordes de componentes
    private final ColorUIResource secondary2 = new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());		// Bordes de componentes
    private final ColorUIResource secondary3 = new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());		// Background paneles

    // other colors
    private final ColorUIResource controlTextColor = new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());	// Control Text Color
    private final ColorUIResource desktopColor = new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());		// Desktop Color
    private final ColorUIResource whiteColor = new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());		// White Color

    // methods
    @Override
	public String getName() {
        return "Random";
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
