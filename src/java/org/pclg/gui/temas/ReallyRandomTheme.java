package org.pclg.gui.temas;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * Un tema con colores 'realmente al azar'.
 */
public final class ReallyRandomTheme extends DefaultMetalTheme {
    // primary colors
    /** Foreground texto en labels.*/
    private ColorUIResource primary1;
    private ColorUIResource primary2;			// Foreground slider y scrollbar
    private ColorUIResource primary3;			// Texto seleccionado

    // secondary colors
    private ColorUIResource secondary1;			// Bordes de componentes
    private ColorUIResource secondary2;			// Bordes de componentes
    private ColorUIResource secondary3;			// Background paneles

    // other colors
    private ColorUIResource controlTextColor;	// Control Text Color
    private ColorUIResource desktopColor;		// Desktop Color
    private ColorUIResource whiteColor;			// White Color

    // methods
    @Override
	public String getName() {
        return "Random";
    }

    @Override
	protected ColorUIResource getPrimary1() {
        return new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    @Override
	protected ColorUIResource getPrimary2() {
        return new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    @Override
	protected ColorUIResource getPrimary3() {
        return new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    @Override
	protected ColorUIResource getSecondary1() {
        return new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    @Override
	protected ColorUIResource getSecondary2() {
        return new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    @Override
	protected ColorUIResource getSecondary3() {
        return new ColorUIResource((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    //public ColorUIResource getControlTextColor() { return controlTextColor; }

    //public ColorUIResource getDesktopColor() { return desktopColor; }

    //public ColorUIResource getWhite() { return whiteColor; }	//?????????
}
