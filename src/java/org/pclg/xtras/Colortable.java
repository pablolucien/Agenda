package org.pclg.xtras;

/*
Google Search: +java.awt.Color +"color name" +String
            Groups 
      Advanced Groups Search    Preferences    Groups Help 
       
       
            Groups search result 2 for +java.awt.Color +"color name" +String  


            Search Result 2
      From: Paul McNamee (paulmac@apl.jhu.edu)
      Subject: Re: teal, beige, tope, chartreuse, purple, violet, 
      lavender,...... 
      Newsgroups: comp.lang.java.programmer
            View: Complete Thread (4 articles) | Original Format
      Date: 1999/05/17 


Mitchell Timin <timin@csrlink.net> writes:
> Java only has 13 predefined colors.  Does anyone know where I can get a few
> dozen more?
> I would like to be able to pick colors by name rather than experimenting
> with RGB levels.  (just like I do for red, blue, yellow, black and pink.)
Feel free to adapt or ignore the attached code.  I created a hashtable
that maps X11 color names to X11 Color objects.  It obviously predates
my knowing about static blocks and anonymous arrays.  Anyway, usage is:

  Color uglyGreen = Colortable.getColor("chartreuse");

- Paul

paul_mcnamee@jhuapl.edu
Johns Hopkins University Applied Physics Laboratory
http://apl.jhu.edu/~paulmac/
*/

// Colortable.java

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to simplify graphical programming by making X11 colors available.
 * <BR>
 * Java only defined a small number of built-in colors, and many programmers are
 * accustomed to descriptive color names (e.g. Crayola, X11...) instead of
 * certain RGB values.
 *
 * Calling <tt><B>getColor</B>("light sea green")</tt> will return a Color
 * object with the right RGB attributes.
 * 
 * @author <A HREF="http://apl.jhu.edu/~paulmac/">Paul McNamee</A>
 * @since 7/11/97 (based on previous (circa 1994) C/X11 code using binary trees)
 */

public class Colortable {
	/** Table of Colors (String -> Color) */
	private static final Map<String, Color> table = new HashMap<>();

	/** Has the table of colors been read in? */
	private static boolean initialized;

	/**
	 * A table of (String -> int[]). The reason this second table is used in
	 * addition to the (String->Color, <tt>table</tt> above) is so Color objects
	 * are only created as needed. On some systems, creating Colors may tie up a
	 * resource (like a color cell in a X11 colormap) and we want to minimize
	 * this. Thus colors are only created as needed.
	 */
	private static final Map<String, int[]> x11cols = new HashMap<>();

	/**
	 * Returns a Color given a X11 color name. If the name can not be found,
	 * then Color.Black is returned.
	 * 
	 * @param str
	 *            An X11 color name.
	 * @return A color matching the input string, or the Color black if the
	 *         desired color is not found, (e.g. due to mispelling).
	 */
	public static Color getColor(final String str) {
		final String colorName = str.toLowerCase();
		Color color = table.get(colorName);
		if (color == null) {
			color = loadColor(colorName);
			if (color == null) {
				// Should just throw an exception.
				System.out.println("Unable to find color:" + colorName);
				return Color.black; // Or should I return null?
			}

			table.put(colorName, color);
		}
		return color;
	}

	/**
	 * Agregado por mi 2002.08.23
	 */
	public static Map<String, int[]> getTable() {
		loadX11Colors(x11cols);
		return (x11cols);
	}

	/** Create and return a new Color object. */
	private static Color loadColor(final String col) {
		if (!initialized) {
			initialized = true;
			loadX11Colors(x11cols);
		}
		final int[] vec = x11cols.get(col);
		return new Color(vec[0], vec[1], vec[2]);
	}

	/** Makes an array of 3 integers. */
	private static int[] mkarr(final int a, final int b, final int c) {
		return new int[] { a, b, c };
	}

	/**
	 * Used to load a table of colors. This only happens once. This method
	 * should not be called, except by <tt>loadColor></tt>
	 */
	private static void loadX11Colors(final Map<String, int[]> tbl) {
		tbl.put("snow", mkarr(255, 250, 250));
		tbl.put("ghost white", mkarr(248, 248, 255));
		tbl.put("white smoke", mkarr(245, 245, 245));
		tbl.put("gainsboro", mkarr(220, 220, 220));
		tbl.put("floral white", mkarr(255, 250, 240));
		tbl.put("old lace", mkarr(253, 245, 230));
		tbl.put("linen", mkarr(250, 240, 230));
		tbl.put("antique white", mkarr(250, 235, 215));
		tbl.put("papaya whip", mkarr(255, 239, 213));
		tbl.put("blanched almond", mkarr(255, 235, 205));
		tbl.put("bisque", mkarr(255, 228, 196));
		tbl.put("peach puff", mkarr(255, 218, 185));
		tbl.put("navajo white", mkarr(255, 222, 173));
		tbl.put("moccasin", mkarr(255, 228, 181));
		tbl.put("cornsilk", mkarr(255, 248, 220));
		tbl.put("ivory", mkarr(255, 255, 240));
		tbl.put("lemon chiffon", mkarr(255, 250, 205));
		tbl.put("seashell", mkarr(255, 245, 238));
		tbl.put("honeydew", mkarr(240, 255, 240));
		tbl.put("mint cream", mkarr(245, 255, 250));
		tbl.put("azure", mkarr(240, 255, 255));
		tbl.put("alice blue", mkarr(240, 248, 255));
		tbl.put("lavender", mkarr(230, 230, 250));
		tbl.put("lavender blush", mkarr(255, 240, 245));
		tbl.put("misty rose", mkarr(255, 228, 225));
		tbl.put("white", mkarr(255, 255, 255));
		tbl.put("black", mkarr(0, 0, 0));
		tbl.put("dark slate gray", mkarr(47, 79, 79));
		tbl.put("dark slate grey", mkarr(47, 79, 79));
		tbl.put("dim gray", mkarr(105, 105, 105));
		tbl.put("dim grey", mkarr(105, 105, 105));
		tbl.put("slate gray", mkarr(112, 128, 144));
		tbl.put("slate grey", mkarr(112, 128, 144));
		tbl.put("light slate gray", mkarr(119, 136, 153));
		tbl.put("light slate grey", mkarr(119, 136, 153));
		tbl.put("gray", mkarr(190, 190, 190));
		tbl.put("grey", mkarr(190, 190, 190));
		tbl.put("light grey", mkarr(211, 211, 211));
		tbl.put("light gray", mkarr(211, 211, 211));
		tbl.put("midnight blue", mkarr(25, 25, 112));
		tbl.put("navy", mkarr(0, 0, 128));
		tbl.put("navy blue", mkarr(0, 0, 128));
		tbl.put("cornflower blue", mkarr(100, 149, 237));
		tbl.put("dark slate blue", mkarr(72, 61, 139));
		tbl.put("slate blue", mkarr(106, 90, 205));
		tbl.put("medium slate blue", mkarr(123, 104, 238));
		tbl.put("light slate blue", mkarr(132, 112, 255));
		tbl.put("medium blue", mkarr(0, 0, 205));
		tbl.put("royal blue", mkarr(65, 105, 225));
		tbl.put("blue", mkarr(0, 0, 255));
		tbl.put("dodger blue", mkarr(30, 144, 255));
		tbl.put("deep sky blue", mkarr(0, 191, 255));
		tbl.put("sky blue", mkarr(135, 206, 235));
		tbl.put("light sky blue", mkarr(135, 206, 250));
		tbl.put("steel blue", mkarr(70, 130, 180));
		tbl.put("light steel blue", mkarr(176, 196, 222));
		tbl.put("light blue", mkarr(173, 216, 230));
		tbl.put("powder blue", mkarr(176, 224, 230));
		tbl.put("pale turquoise", mkarr(175, 238, 238));
		tbl.put("dark turquoise", mkarr(0, 206, 209));
		tbl.put("medium turquoise", mkarr(72, 209, 204));
		tbl.put("turquoise", mkarr(64, 224, 208));
		tbl.put("cyan", mkarr(0, 255, 255));
		tbl.put("light cyan", mkarr(224, 255, 255));
		tbl.put("cadet blue", mkarr(95, 158, 160));
		tbl.put("medium aquamarine", mkarr(102, 205, 170));
		tbl.put("aquamarine", mkarr(127, 255, 212));
		tbl.put("dark green", mkarr(0, 100, 0));
		tbl.put("dark olive green", mkarr(85, 107, 47));
		tbl.put("dark sea green", mkarr(143, 188, 143));
		tbl.put("sea green", mkarr(46, 139, 87));
		tbl.put("medium sea green", mkarr(60, 179, 113));
		tbl.put("light sea green", mkarr(32, 178, 170));
		tbl.put("pale green", mkarr(152, 251, 152));
		tbl.put("spring green", mkarr(0, 255, 127));
		tbl.put("lawn green", mkarr(124, 252, 0));
		tbl.put("green", mkarr(0, 255, 0));
		tbl.put("chartreuse", mkarr(127, 255, 0));
		tbl.put("medium spring green", mkarr(0, 250, 154));
		tbl.put("green yellow", mkarr(173, 255, 47));
		tbl.put("lime green", mkarr(50, 205, 50));
		tbl.put("yellow green", mkarr(154, 205, 50));
		tbl.put("forest green", mkarr(34, 139, 34));
		tbl.put("olive drab", mkarr(107, 142, 35));
		tbl.put("dark khaki", mkarr(189, 183, 107));
		tbl.put("khaki", mkarr(240, 230, 140));
		tbl.put("pale goldenrod", mkarr(238, 232, 170));
		tbl.put("light goldenrod yellow", mkarr(250, 250, 210));
		tbl.put("light yellow", mkarr(255, 255, 224));
		tbl.put("yellow", mkarr(255, 255, 0));
		tbl.put("gold", mkarr(255, 215, 0));
		tbl.put("light goldenrod", mkarr(238, 221, 130));
		tbl.put("goldenrod", mkarr(218, 165, 32));
		tbl.put("dark goldenrod", mkarr(184, 134, 11));
		tbl.put("rosy brown", mkarr(188, 143, 143));
		tbl.put("indian red", mkarr(205, 92, 92));
		tbl.put("saddle brown", mkarr(139, 69, 19));
		tbl.put("sienna", mkarr(160, 82, 45));
		tbl.put("peru", mkarr(205, 133, 63));
		tbl.put("burlywood", mkarr(222, 184, 135));
		tbl.put("beige", mkarr(245, 245, 220));
		tbl.put("wheat", mkarr(245, 222, 179));
		tbl.put("sandy brown", mkarr(244, 164, 96));
		tbl.put("tan", mkarr(210, 180, 140));
		tbl.put("chocolate", mkarr(210, 105, 30));
		tbl.put("firebrick", mkarr(178, 34, 34));
		tbl.put("brown", mkarr(165, 42, 42));
		tbl.put("dark salmon", mkarr(233, 150, 122));
		tbl.put("salmon", mkarr(250, 128, 114));
		tbl.put("light salmon", mkarr(255, 160, 122));
		tbl.put("orange", mkarr(255, 165, 0));
		tbl.put("dark orange", mkarr(255, 140, 0));
		tbl.put("coral", mkarr(255, 127, 80));
		tbl.put("light coral", mkarr(240, 128, 128));
		tbl.put("tomato", mkarr(255, 99, 71));
		tbl.put("orange red", mkarr(255, 69, 0));
		tbl.put("red", mkarr(255, 0, 0));
		tbl.put("hot pink", mkarr(255, 105, 180));
		tbl.put("deep pink", mkarr(255, 20, 147));
		tbl.put("pink", mkarr(255, 192, 203));
		tbl.put("light pink", mkarr(255, 182, 193));
		tbl.put("pale violet red", mkarr(219, 112, 147));
		tbl.put("maroon", mkarr(176, 48, 96));
		tbl.put("medium violet red", mkarr(199, 21, 133));
		tbl.put("violet red", mkarr(208, 32, 144));
		tbl.put("magenta", mkarr(255, 0, 255));
		tbl.put("violet", mkarr(238, 130, 238));
		tbl.put("plum", mkarr(221, 160, 221));
		tbl.put("orchid", mkarr(218, 112, 214));
		tbl.put("medium orchid", mkarr(186, 85, 211));
		tbl.put("dark orchid", mkarr(153, 50, 204));
		tbl.put("dark violet", mkarr(148, 0, 211));
		tbl.put("blue violet", mkarr(138, 43, 226));
		tbl.put("purple", mkarr(160, 32, 240));
		tbl.put("medium purple", mkarr(147, 112, 219));
		tbl.put("thistle", mkarr(216, 191, 216));
		tbl.put("snow1", mkarr(255, 250, 250));
		tbl.put("snow2", mkarr(238, 233, 233));
		tbl.put("snow3", mkarr(205, 201, 201));
		tbl.put("snow4", mkarr(139, 137, 137));
		tbl.put("seashell1", mkarr(255, 245, 238));
		tbl.put("seashell2", mkarr(238, 229, 222));
		tbl.put("seashell3", mkarr(205, 197, 191));
		tbl.put("seashell4", mkarr(139, 134, 130));
		tbl.put("antiquewhite1", mkarr(255, 239, 219));
		tbl.put("antiquewhite2", mkarr(238, 223, 204));
		tbl.put("antiquewhite3", mkarr(205, 192, 176));
		tbl.put("antiquewhite4", mkarr(139, 131, 120));
		tbl.put("bisque1", mkarr(255, 228, 196));
		tbl.put("bisque2", mkarr(238, 213, 183));
		tbl.put("bisque3", mkarr(205, 183, 158));
		tbl.put("bisque4", mkarr(139, 125, 107));
		tbl.put("peachpuff1", mkarr(255, 218, 185));
		tbl.put("peachpuff2", mkarr(238, 203, 173));
		tbl.put("peachpuff3", mkarr(205, 175, 149));
		tbl.put("peachpuff4", mkarr(139, 119, 101));
		tbl.put("navajowhite1", mkarr(255, 222, 173));
		tbl.put("navajowhite2", mkarr(238, 207, 161));
		tbl.put("navajowhite3", mkarr(205, 179, 139));
		tbl.put("navajowhite4", mkarr(139, 121, 94));
		tbl.put("lemonchiffon1", mkarr(255, 250, 205));
		tbl.put("lemonchiffon2", mkarr(238, 233, 191));
		tbl.put("lemonchiffon3", mkarr(205, 201, 165));
		tbl.put("lemonchiffon4", mkarr(139, 137, 112));
		tbl.put("cornsilk1", mkarr(255, 248, 220));
		tbl.put("cornsilk2", mkarr(238, 232, 205));
		tbl.put("cornsilk3", mkarr(205, 200, 177));
		tbl.put("cornsilk4", mkarr(139, 136, 120));
		tbl.put("ivory1", mkarr(255, 255, 240));
		tbl.put("ivory2", mkarr(238, 238, 224));
		tbl.put("ivory3", mkarr(205, 205, 193));
		tbl.put("ivory4", mkarr(139, 139, 131));
		tbl.put("honeydew1", mkarr(240, 255, 240));
		tbl.put("honeydew2", mkarr(224, 238, 224));
		tbl.put("honeydew3", mkarr(193, 205, 193));
		tbl.put("honeydew4", mkarr(131, 139, 131));
		tbl.put("lavenderblush1", mkarr(255, 240, 245));
		tbl.put("lavenderblush2", mkarr(238, 224, 229));
		tbl.put("lavenderblush3", mkarr(205, 193, 197));
		tbl.put("lavenderblush4", mkarr(139, 131, 134));
		tbl.put("mistyrose1", mkarr(255, 228, 225));
		tbl.put("mistyrose2", mkarr(238, 213, 210));
		tbl.put("mistyrose3", mkarr(205, 183, 181));
		tbl.put("mistyrose4", mkarr(139, 125, 123));
		tbl.put("azure1", mkarr(240, 255, 255));
		tbl.put("azure2", mkarr(224, 238, 238));
		tbl.put("azure3", mkarr(193, 205, 205));
		tbl.put("azure4", mkarr(131, 139, 139));
		tbl.put("slateblue1", mkarr(131, 111, 255));
		tbl.put("slateblue2", mkarr(122, 103, 238));
		tbl.put("slateblue3", mkarr(105, 89, 205));
		tbl.put("slateblue4", mkarr(71, 60, 139));
		tbl.put("royalblue1", mkarr(72, 118, 255));
		tbl.put("royalblue2", mkarr(67, 110, 238));
		tbl.put("royalblue3", mkarr(58, 95, 205));
		tbl.put("royalblue4", mkarr(39, 64, 139));
		tbl.put("blue1", mkarr(0, 0, 255));
		tbl.put("blue2", mkarr(0, 0, 238));
		tbl.put("blue3", mkarr(0, 0, 205));
		tbl.put("blue4", mkarr(0, 0, 139));
		tbl.put("dodgerblue1", mkarr(30, 144, 255));
		tbl.put("dodgerblue2", mkarr(28, 134, 238));
		tbl.put("dodgerblue3", mkarr(24, 116, 205));
		tbl.put("dodgerblue4", mkarr(16, 78, 139));
		tbl.put("steelblue1", mkarr(99, 184, 255));
		tbl.put("steelblue2", mkarr(92, 172, 238));
		tbl.put("steelblue3", mkarr(79, 148, 205));
		tbl.put("steelblue4", mkarr(54, 100, 139));
		tbl.put("deepskyblue1", mkarr(0, 191, 255));
		tbl.put("deepskyblue2", mkarr(0, 178, 238));
		tbl.put("deepskyblue3", mkarr(0, 154, 205));
		tbl.put("deepskyblue4", mkarr(0, 104, 139));
		tbl.put("skyblue1", mkarr(135, 206, 255));
		tbl.put("skyblue2", mkarr(126, 192, 238));
		tbl.put("skyblue3", mkarr(108, 166, 205));
		tbl.put("skyblue4", mkarr(74, 112, 139));
		tbl.put("lightskyblue1", mkarr(176, 226, 255));
		tbl.put("lightskyblue2", mkarr(164, 211, 238));
		tbl.put("lightskyblue3", mkarr(141, 182, 205));
		tbl.put("lightskyblue4", mkarr(96, 123, 139));
		tbl.put("slategray1", mkarr(198, 226, 255));
		tbl.put("slategray2", mkarr(185, 211, 238));
		tbl.put("slategray3", mkarr(159, 182, 205));
		tbl.put("slategray4", mkarr(108, 123, 139));
		tbl.put("lightsteelblue1", mkarr(202, 225, 255));
		tbl.put("lightsteelblue2", mkarr(188, 210, 238));
		tbl.put("lightsteelblue3", mkarr(162, 181, 205));
		tbl.put("lightsteelblue4", mkarr(110, 123, 139));
		tbl.put("lightblue1", mkarr(191, 239, 255));
		tbl.put("lightblue2", mkarr(178, 223, 238));
		tbl.put("lightblue3", mkarr(154, 192, 205));
		tbl.put("lightblue4", mkarr(104, 131, 139));
		tbl.put("lightcyan1", mkarr(224, 255, 255));
		tbl.put("lightcyan2", mkarr(209, 238, 238));
		tbl.put("lightcyan3", mkarr(180, 205, 205));
		tbl.put("lightcyan4", mkarr(122, 139, 139));
		tbl.put("paleturquoise1", mkarr(187, 255, 255));
		tbl.put("paleturquoise2", mkarr(174, 238, 238));
		tbl.put("paleturquoise3", mkarr(150, 205, 205));
		tbl.put("paleturquoise4", mkarr(102, 139, 139));
		tbl.put("cadetblue1", mkarr(152, 245, 255));
		tbl.put("cadetblue2", mkarr(142, 229, 238));
		tbl.put("cadetblue3", mkarr(122, 197, 205));
		tbl.put("cadetblue4", mkarr(83, 134, 139));
		tbl.put("turquoise1", mkarr(0, 245, 255));
		tbl.put("turquoise2", mkarr(0, 229, 238));
		tbl.put("turquoise3", mkarr(0, 197, 205));
		tbl.put("turquoise4", mkarr(0, 134, 139));
		tbl.put("cyan1", mkarr(0, 255, 255));
		tbl.put("cyan2", mkarr(0, 238, 238));
		tbl.put("cyan3", mkarr(0, 205, 205));
		tbl.put("cyan4", mkarr(0, 139, 139));
		tbl.put("darkslategray1", mkarr(151, 255, 255));
		tbl.put("darkslategray2", mkarr(141, 238, 238));
		tbl.put("darkslategray3", mkarr(121, 205, 205));
		tbl.put("darkslategray4", mkarr(82, 139, 139));
		tbl.put("aquamarine1", mkarr(127, 255, 212));
		tbl.put("aquamarine2", mkarr(118, 238, 198));
		tbl.put("aquamarine3", mkarr(102, 205, 170));
		tbl.put("aquamarine4", mkarr(69, 139, 116));
		tbl.put("darkseagreen1", mkarr(193, 255, 193));
		tbl.put("darkseagreen2", mkarr(180, 238, 180));
		tbl.put("darkseagreen3", mkarr(155, 205, 155));
		tbl.put("darkseagreen4", mkarr(105, 139, 105));
		tbl.put("seagreen1", mkarr(84, 255, 159));
		tbl.put("seagreen2", mkarr(78, 238, 148));
		tbl.put("seagreen3", mkarr(67, 205, 128));
		tbl.put("seagreen4", mkarr(46, 139, 87));
		tbl.put("palegreen1", mkarr(154, 255, 154));
		tbl.put("palegreen2", mkarr(144, 238, 144));
		tbl.put("palegreen3", mkarr(124, 205, 124));
		tbl.put("palegreen4", mkarr(84, 139, 84));
		tbl.put("springgreen1", mkarr(0, 255, 127));
		tbl.put("springgreen2", mkarr(0, 238, 118));
		tbl.put("springgreen3", mkarr(0, 205, 102));
		tbl.put("springgreen4", mkarr(0, 139, 69));
		tbl.put("green1", mkarr(0, 255, 0));
		tbl.put("green2", mkarr(0, 238, 0));
		tbl.put("green3", mkarr(0, 205, 0));
		tbl.put("green4", mkarr(0, 139, 0));
		tbl.put("chartreuse1", mkarr(127, 255, 0));
		tbl.put("chartreuse2", mkarr(118, 238, 0));
		tbl.put("chartreuse3", mkarr(102, 205, 0));
		tbl.put("chartreuse4", mkarr(69, 139, 0));
		tbl.put("olivedrab1", mkarr(192, 255, 62));
		tbl.put("olivedrab2", mkarr(179, 238, 58));
		tbl.put("olivedrab3", mkarr(154, 205, 50));
		tbl.put("olivedrab4", mkarr(105, 139, 34));
		tbl.put("darkolivegreen1", mkarr(202, 255, 112));
		tbl.put("darkolivegreen2", mkarr(188, 238, 104));
		tbl.put("darkolivegreen3", mkarr(162, 205, 90));
		tbl.put("darkolivegreen4", mkarr(110, 139, 61));
		tbl.put("khaki1", mkarr(255, 246, 143));
		tbl.put("khaki2", mkarr(238, 230, 133));
		tbl.put("khaki3", mkarr(205, 198, 115));
		tbl.put("khaki4", mkarr(139, 134, 78));
		tbl.put("lightgoldenrod1", mkarr(255, 236, 139));
		tbl.put("lightgoldenrod2", mkarr(238, 220, 130));
		tbl.put("lightgoldenrod3", mkarr(205, 190, 112));
		tbl.put("lightgoldenrod4", mkarr(139, 129, 76));
		tbl.put("lightyellow1", mkarr(255, 255, 224));
		tbl.put("lightyellow2", mkarr(238, 238, 209));
		tbl.put("lightyellow3", mkarr(205, 205, 180));
		tbl.put("lightyellow4", mkarr(139, 139, 122));
		tbl.put("yellow1", mkarr(255, 255, 0));
		tbl.put("yellow2", mkarr(238, 238, 0));
		tbl.put("yellow3", mkarr(205, 205, 0));
		tbl.put("yellow4", mkarr(139, 139, 0));
		tbl.put("gold1", mkarr(255, 215, 0));
		tbl.put("gold2", mkarr(238, 201, 0));
		tbl.put("gold3", mkarr(205, 173, 0));
		tbl.put("gold4", mkarr(139, 117, 0));
		tbl.put("goldenrod1", mkarr(255, 193, 37));
		tbl.put("goldenrod2", mkarr(238, 180, 34));
		tbl.put("goldenrod3", mkarr(205, 155, 29));
		tbl.put("goldenrod4", mkarr(139, 105, 20));
		tbl.put("darkgoldenrod1", mkarr(255, 185, 15));
		tbl.put("darkgoldenrod2", mkarr(238, 173, 14));
		tbl.put("darkgoldenrod3", mkarr(205, 149, 12));
		tbl.put("darkgoldenrod4", mkarr(139, 101, 8));
		tbl.put("rosybrown1", mkarr(255, 193, 193));
		tbl.put("rosybrown2", mkarr(238, 180, 180));
		tbl.put("rosybrown3", mkarr(205, 155, 155));
		tbl.put("rosybrown4", mkarr(139, 105, 105));
		tbl.put("indianred1", mkarr(255, 106, 106));
		tbl.put("indianred2", mkarr(238, 99, 99));
		tbl.put("indianred3", mkarr(205, 85, 85));
		tbl.put("indianred4", mkarr(139, 58, 58));
		tbl.put("sienna1", mkarr(255, 130, 71));
		tbl.put("sienna2", mkarr(238, 121, 66));
		tbl.put("sienna3", mkarr(205, 104, 57));
		tbl.put("sienna4", mkarr(139, 71, 38));
		tbl.put("burlywood1", mkarr(255, 211, 155));
		tbl.put("burlywood2", mkarr(238, 197, 145));
		tbl.put("burlywood3", mkarr(205, 170, 125));
		tbl.put("burlywood4", mkarr(139, 115, 85));
		tbl.put("wheat1", mkarr(255, 231, 186));
		tbl.put("wheat2", mkarr(238, 216, 174));
		tbl.put("wheat3", mkarr(205, 186, 150));
		tbl.put("wheat4", mkarr(139, 126, 102));
		tbl.put("tan1", mkarr(255, 165, 79));
		tbl.put("tan2", mkarr(238, 154, 73));
		tbl.put("tan3", mkarr(205, 133, 63));
		tbl.put("tan4", mkarr(139, 90, 43));
		tbl.put("chocolate1", mkarr(255, 127, 36));
		tbl.put("chocolate2", mkarr(238, 118, 33));
		tbl.put("chocolate3", mkarr(205, 102, 29));
		tbl.put("chocolate4", mkarr(139, 69, 19));
		tbl.put("firebrick1", mkarr(255, 48, 48));
		tbl.put("firebrick2", mkarr(238, 44, 44));
		tbl.put("firebrick3", mkarr(205, 38, 38));
		tbl.put("firebrick4", mkarr(139, 26, 26));
		tbl.put("brown1", mkarr(255, 64, 64));
		tbl.put("brown2", mkarr(238, 59, 59));
		tbl.put("brown3", mkarr(205, 51, 51));
		tbl.put("brown4", mkarr(139, 35, 35));
		tbl.put("salmon1", mkarr(255, 140, 105));
		tbl.put("salmon2", mkarr(238, 130, 98));
		tbl.put("salmon3", mkarr(205, 112, 84));
		tbl.put("salmon4", mkarr(139, 76, 57));
		tbl.put("lightsalmon1", mkarr(255, 160, 122));
		tbl.put("lightsalmon2", mkarr(238, 149, 114));
		tbl.put("lightsalmon3", mkarr(205, 129, 98));
		tbl.put("lightsalmon4", mkarr(139, 87, 66));
		tbl.put("orange1", mkarr(255, 165, 0));
		tbl.put("orange2", mkarr(238, 154, 0));
		tbl.put("orange3", mkarr(205, 133, 0));
		tbl.put("orange4", mkarr(139, 90, 0));
		tbl.put("darkorange1", mkarr(255, 127, 0));
		tbl.put("darkorange2", mkarr(238, 118, 0));
		tbl.put("darkorange3", mkarr(205, 102, 0));
		tbl.put("darkorange4", mkarr(139, 69, 0));
		tbl.put("coral1", mkarr(255, 114, 86));
		tbl.put("coral2", mkarr(238, 106, 80));
		tbl.put("coral3", mkarr(205, 91, 69));
		tbl.put("coral4", mkarr(139, 62, 47));
		tbl.put("tomato1", mkarr(255, 99, 71));
		tbl.put("tomato2", mkarr(238, 92, 66));
		tbl.put("tomato3", mkarr(205, 79, 57));
		tbl.put("tomato4", mkarr(139, 54, 38));
		tbl.put("orangered1", mkarr(255, 69, 0));
		tbl.put("orangered2", mkarr(238, 64, 0));
		tbl.put("orangered3", mkarr(205, 55, 0));
		tbl.put("orangered4", mkarr(139, 37, 0));
		tbl.put("red1", mkarr(255, 0, 0));
		tbl.put("red2", mkarr(238, 0, 0));
		tbl.put("red3", mkarr(205, 0, 0));
		tbl.put("red4", mkarr(139, 0, 0));
		tbl.put("deeppink1", mkarr(255, 20, 147));
		tbl.put("deeppink2", mkarr(238, 18, 137));
		tbl.put("deeppink3", mkarr(205, 16, 118));
		tbl.put("deeppink4", mkarr(139, 10, 80));
		tbl.put("hotpink1", mkarr(255, 110, 180));
		tbl.put("hotpink2", mkarr(238, 106, 167));
		tbl.put("hotpink3", mkarr(205, 96, 144));
		tbl.put("hotpink4", mkarr(139, 58, 98));
		tbl.put("pink1", mkarr(255, 181, 197));
		tbl.put("pink2", mkarr(238, 169, 184));
		tbl.put("pink3", mkarr(205, 145, 158));
		tbl.put("pink4", mkarr(139, 99, 108));
		tbl.put("lightpink1", mkarr(255, 174, 185));
		tbl.put("lightpink2", mkarr(238, 162, 173));
		tbl.put("lightpink3", mkarr(205, 140, 149));
		tbl.put("lightpink4", mkarr(139, 95, 101));
		tbl.put("palevioletred1", mkarr(255, 130, 171));
		tbl.put("palevioletred2", mkarr(238, 121, 159));
		tbl.put("palevioletred3", mkarr(205, 104, 137));
		tbl.put("palevioletred4", mkarr(139, 71, 93));
		tbl.put("maroon1", mkarr(255, 52, 179));
		tbl.put("maroon2", mkarr(238, 48, 167));
		tbl.put("maroon3", mkarr(205, 41, 144));
		tbl.put("maroon4", mkarr(139, 28, 98));
		tbl.put("violetred1", mkarr(255, 62, 150));
		tbl.put("violetred2", mkarr(238, 58, 140));
		tbl.put("violetred3", mkarr(205, 50, 120));
		tbl.put("violetred4", mkarr(139, 34, 82));
		tbl.put("magenta1", mkarr(255, 0, 255));
		tbl.put("magenta2", mkarr(238, 0, 238));
		tbl.put("magenta3", mkarr(205, 0, 205));
		tbl.put("magenta4", mkarr(139, 0, 139));
		tbl.put("orchid1", mkarr(255, 131, 250));
		tbl.put("orchid2", mkarr(238, 122, 233));
		tbl.put("orchid3", mkarr(205, 105, 201));
		tbl.put("orchid4", mkarr(139, 71, 137));
		tbl.put("plum1", mkarr(255, 187, 255));
		tbl.put("plum2", mkarr(238, 174, 238));
		tbl.put("plum3", mkarr(205, 150, 205));
		tbl.put("plum4", mkarr(139, 102, 139));
		tbl.put("mediumorchid1", mkarr(224, 102, 255));
		tbl.put("mediumorchid2", mkarr(209, 95, 238));
		tbl.put("mediumorchid3", mkarr(180, 82, 205));
		tbl.put("mediumorchid4", mkarr(122, 55, 139));
		tbl.put("darkorchid1", mkarr(191, 62, 255));
		tbl.put("darkorchid2", mkarr(178, 58, 238));
		tbl.put("darkorchid3", mkarr(154, 50, 205));
		tbl.put("darkorchid4", mkarr(104, 34, 139));
		tbl.put("purple1", mkarr(155, 48, 255));
		tbl.put("purple2", mkarr(145, 44, 238));
		tbl.put("purple3", mkarr(125, 38, 205));
		tbl.put("purple4", mkarr(85, 26, 139));
		tbl.put("mediumpurple1", mkarr(171, 130, 255));
		tbl.put("mediumpurple2", mkarr(159, 121, 238));
		tbl.put("mediumpurple3", mkarr(137, 104, 205));
		tbl.put("mediumpurple4", mkarr(93, 71, 139));
		tbl.put("thistle1", mkarr(255, 225, 255));
		tbl.put("thistle2", mkarr(238, 210, 238));
		tbl.put("thistle3", mkarr(205, 181, 205));
		tbl.put("thistle4", mkarr(139, 123, 139));
		tbl.put("gray0", mkarr(0, 0, 0));
		tbl.put("grey1", mkarr(3, 3, 3));
		tbl.put("gray2", mkarr(5, 5, 5));
		tbl.put("grey3", mkarr(8, 8, 8));
		tbl.put("gray4", mkarr(10, 10, 10));
		tbl.put("grey5", mkarr(13, 13, 13));
		tbl.put("gray6", mkarr(15, 15, 15));
		tbl.put("grey7", mkarr(18, 18, 18));
		tbl.put("gray8", mkarr(20, 20, 20));
		tbl.put("grey9", mkarr(23, 23, 23));
		tbl.put("gray10", mkarr(26, 26, 26));
		tbl.put("grey11", mkarr(28, 28, 28));
		tbl.put("gray12", mkarr(31, 31, 31));
		tbl.put("grey13", mkarr(33, 33, 33));
		tbl.put("gray14", mkarr(36, 36, 36));
		tbl.put("grey15", mkarr(38, 38, 38));
		tbl.put("gray16", mkarr(41, 41, 41));
		tbl.put("grey17", mkarr(43, 43, 43));
		tbl.put("gray18", mkarr(46, 46, 46));
		tbl.put("grey19", mkarr(48, 48, 48));
		tbl.put("gray20", mkarr(51, 51, 51));
		tbl.put("grey21", mkarr(54, 54, 54));
		tbl.put("gray22", mkarr(56, 56, 56));
		tbl.put("grey23", mkarr(59, 59, 59));
		tbl.put("gray24", mkarr(61, 61, 61));
		tbl.put("grey25", mkarr(64, 64, 64));
		tbl.put("gray26", mkarr(66, 66, 66));
		tbl.put("grey27", mkarr(69, 69, 69));
		tbl.put("gray28", mkarr(71, 71, 71));
		tbl.put("grey29", mkarr(74, 74, 74));
		tbl.put("gray30", mkarr(77, 77, 77));
		tbl.put("grey31", mkarr(79, 79, 79));
		tbl.put("gray32", mkarr(82, 82, 82));
		tbl.put("grey33", mkarr(84, 84, 84));
		tbl.put("gray34", mkarr(87, 87, 87));
		tbl.put("grey35", mkarr(89, 89, 89));
		tbl.put("gray36", mkarr(92, 92, 92));
		tbl.put("grey37", mkarr(94, 94, 94));
		tbl.put("gray38", mkarr(97, 97, 97));
		tbl.put("grey39", mkarr(99, 99, 99));
		tbl.put("gray40", mkarr(102, 102, 102));
		tbl.put("grey41", mkarr(105, 105, 105));
		tbl.put("gray42", mkarr(107, 107, 107));
		tbl.put("grey43", mkarr(110, 110, 110));
		tbl.put("gray44", mkarr(112, 112, 112));
		tbl.put("grey45", mkarr(115, 115, 115));
		tbl.put("gray46", mkarr(117, 117, 117));
		tbl.put("grey47", mkarr(120, 120, 120));
		tbl.put("gray48", mkarr(122, 122, 122));
		tbl.put("grey49", mkarr(125, 125, 125));
		tbl.put("gray50", mkarr(127, 127, 127));
		tbl.put("grey51", mkarr(130, 130, 130));
		tbl.put("grey52", mkarr(133, 133, 133));
		tbl.put("grey53", mkarr(135, 135, 135));
		tbl.put("grey54", mkarr(138, 138, 138));
		tbl.put("grey55", mkarr(140, 140, 140));
		tbl.put("grey56", mkarr(143, 143, 143));
		tbl.put("grey57", mkarr(145, 145, 145));
		tbl.put("grey58", mkarr(148, 148, 148));
		tbl.put("grey59", mkarr(150, 150, 150));
		tbl.put("grey60", mkarr(153, 153, 153));
		tbl.put("grey61", mkarr(156, 156, 156));
		tbl.put("grey62", mkarr(158, 158, 158));
		tbl.put("grey63", mkarr(161, 161, 161));
		tbl.put("gray64", mkarr(163, 163, 163));
		tbl.put("grey65", mkarr(166, 166, 166));
		tbl.put("gray66", mkarr(168, 168, 168));
		tbl.put("grey67", mkarr(171, 171, 171));
		tbl.put("gray68", mkarr(173, 173, 173));
		tbl.put("grey69", mkarr(176, 176, 176));
		tbl.put("gray70", mkarr(179, 179, 179));
		tbl.put("grey71", mkarr(181, 181, 181));
		tbl.put("gray72", mkarr(184, 184, 184));
		tbl.put("grey73", mkarr(186, 186, 186));
		tbl.put("gray74", mkarr(189, 189, 189));
		tbl.put("grey75", mkarr(191, 191, 191));
		tbl.put("gray76", mkarr(194, 194, 194));
		tbl.put("grey77", mkarr(196, 196, 196));
		tbl.put("gray78", mkarr(199, 199, 199));
		tbl.put("grey79", mkarr(201, 201, 201));
		tbl.put("gray80", mkarr(204, 204, 204));
		tbl.put("grey81", mkarr(207, 207, 207));
		tbl.put("gray82", mkarr(209, 209, 209));
		tbl.put("grey83", mkarr(212, 212, 212));
		tbl.put("gray84", mkarr(214, 214, 214));
		tbl.put("grey85", mkarr(217, 217, 217));
		tbl.put("gray86", mkarr(219, 219, 219));
		tbl.put("grey87", mkarr(222, 222, 222));
		tbl.put("gray88", mkarr(224, 224, 224));
		tbl.put("grey89", mkarr(227, 227, 227));
		tbl.put("gray90", mkarr(229, 229, 229));
		tbl.put("grey91", mkarr(232, 232, 232));
		tbl.put("gray92", mkarr(235, 235, 235));
		tbl.put("grey93", mkarr(237, 237, 237));
		tbl.put("gray94", mkarr(240, 240, 240));
		tbl.put("grey95", mkarr(242, 242, 242));
		tbl.put("gray96", mkarr(245, 245, 245));
		tbl.put("grey97", mkarr(247, 247, 247));
		tbl.put("gray98", mkarr(250, 250, 250));
		tbl.put("grey99", mkarr(252, 252, 252));
		tbl.put("gray100", mkarr(255, 255, 255));
		tbl.put("dark gray", mkarr(169, 169, 169));
		tbl.put("dark blue", mkarr(0, 0, 139));
		tbl.put("dark cyan", mkarr(0, 139, 139));
		tbl.put("dark magenta", mkarr(139, 0, 139));
		tbl.put("dark red", mkarr(139, 0, 0));
		tbl.put("light green", mkarr(144, 238, 144));
		tbl.put("sepia", mkarr(226, 210, 177));
	}
}
