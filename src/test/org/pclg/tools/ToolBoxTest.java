package org.pclg.tools;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Pablo
 * @since 15-may-2009 11:02:02
 */
@SuppressWarnings({"ClassWithoutLogger"})
public class ToolBoxTest {
	@Test
	public void testLeftPad() {
		final String testStr = "la cantante calva";
		final String expectedStr = "XXXXXla cantante calva";
		assertEquals(expectedStr,
			ToolBox.leftPad(testStr, testStr.length() + 5,
				'X'));
	}

	@Test
	public void testPad() {
		final String testStr = "la cantante calva";
		final String expectedStr = "la cantante calvaXXXXX";
		assertEquals(expectedStr,
			ToolBox.pad(testStr, testStr.length() + 5,
				'X'));
	}

	@Test
	public void testPadAndClip() {
		final String testStr = "la cantante calva";
		final String expectedStr1 = "la cantante calvaXXXXX";
		final String expectedStr2 = "la cantante";
		assertEquals(expectedStr1,
			ToolBox.padAndClip(testStr, testStr.length()
				+ 5, 'X'));
		assertEquals(expectedStr2, ToolBox.padAndClip(testStr,
			expectedStr2.length(), 'X'));
	}

//	@Test
//	public void testGetString() throws Exception {
//		final String testStr = "copy and paste this text";
//		assertEquals(testStr, ToolBox.getString(testStr));
//	}

	@Test
	public void testIsInteger() {
		assertTrue(ToolBox.isInteger("123"), "123 es entero");
		assertFalse(ToolBox.isInteger("12.3"), "12.3 no es entero");
		assertFalse(ToolBox.isInteger("abc"), "1.abc no es entero");
		assertFalse(ToolBox.isInteger("abc"), "abc no es entero");
	}


	@Test
	public void testIsNumber() {
		assertTrue(ToolBox.isNumber("123"), "123 es número");
		assertTrue(ToolBox.isNumber("12.3"), "12.3 es número");
		assertFalse(ToolBox.isNumber("abc"), "1.abc no es número");
		assertFalse(ToolBox.isNumber("abc"), "abc no es número");
	}
	
//	@Test
//	public void testGetExecutionPath() {
//		assertEquals("", ToolBox.getExecutionPath(this));
//	}
}
