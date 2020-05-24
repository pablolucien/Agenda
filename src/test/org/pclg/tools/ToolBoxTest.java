package org.pclg.tools;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Pablo
 * @since 15-may-2009 11:02:02
 */
@SuppressWarnings({"ClassWithoutLogger"})
public class ToolBoxTest {
	@Test
	public void testLeftPad() throws Exception {
		final String testStr = "la cantante calva";
		final String expectedStr = "XXXXXla cantante calva";
		assertEquals(expectedStr,
			ToolBox.leftPad(testStr, testStr.length() + 5,
				'X'));
	}

	@Test
	public void testPad() throws Exception {
		final String testStr = "la cantante calva";
		final String expectedStr = "la cantante calvaXXXXX";
		assertEquals(expectedStr,
			ToolBox.pad(testStr, testStr.length() + 5,
				'X'));
	}

	@Test
	public void testPadAndClip() throws Exception {
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
		assertTrue("123 es entero", ToolBox.isInteger("123"));
		assertFalse("12.3 no es entero", ToolBox.isInteger("12.3"));
		assertFalse("1.abc no es entero", ToolBox.isInteger("abc"));
		assertFalse("abc no es entero", ToolBox.isInteger("abc"));
	}


	@Test
	public void testIsNumber() {
		assertTrue("123 es número", ToolBox.isNumber("123"));
		assertTrue("12.3 es número", ToolBox.isNumber("12.3"));
		assertFalse("1.abc no es número", ToolBox.isNumber("abc"));
		assertFalse("abc no es número", ToolBox.isNumber("abc"));
	}
	
//	@Test
//	public void testGetExecutionPath() {
//		assertEquals("", ToolBox.getExecutionPath(this));
//	}
}
