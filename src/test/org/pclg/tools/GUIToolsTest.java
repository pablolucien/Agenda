package org.pclg.tools;

import static org.testng.Assert.assertEquals;

public class GUIToolsTest {

//	//@Test
//	public void testAdd2LAFMenu() {
//        final Map<JRadioButtonMenuItem, String> menuMap =
//            LnFController.add2LAFMenu(null, new JMenu(), new ButtonGroup());
//        final Collection<String> values = menuMap.values();
//        assertTrue(values.contains("com.incors.plaf.kunststoff.KunststoffLookAndFeel|com.incors.plaf.kunststoff.themes.KunststoffDesktopTheme"));
//        assertTrue(values.contains("javax.swing.plaf.metal.MetalLookAndFeel|com.incors.plaf.kunststoff.themes.KunststoffDesktopTheme"));
//    }

    //@Test
	public void testFormatNumberWithMask() {
		assertEquals("628.87.90.11", GUITools.formatNumberWithMask("628879011", "###.##.##.##"));
		assertEquals("913.57.44.94", GUITools.formatNumberWithMask("913574494", "###.##.##.##"));
		assertEquals("4127.32.19.14", GUITools.formatNumberWithMask("4127321914", "###.##.##.##"));
		assertEquals("1.23", GUITools.formatNumberWithMask("123", "###.##.##.##"));
		assertEquals("(613)491-68-04", GUITools.formatNumberWithMask("6134916804", "(###)###-##-##"));
		assertEquals("06.15.32.65.29", GUITools.formatNumberWithMask("0615326529", "##.##.##.##.##"));
		assertEquals("0615326529", GUITools.formatNumberWithMask("0615326529", null));
		assertEquals("0615326529", GUITools.formatNumberWithMask("0615326529", ""));
	}
}