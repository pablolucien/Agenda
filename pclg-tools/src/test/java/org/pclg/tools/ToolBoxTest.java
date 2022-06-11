package org.pclg.tools;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

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
		assertEquals(expectedStr, ToolBox.leftPad(testStr, testStr.length() + 5, 'X'));
	}

	@Test
	public void testPad() {
		final String testStr = "la cantante calva";
		final String expectedStr = "la cantante calvaXXXXX";
		assertEquals(expectedStr, ToolBox.pad(testStr, testStr.length() + 5, 'X'));
	}

	@Test
	public void testPadAndClip() {
		final String testStr = "la cantante calva";
		final String expectedStr1 = "la cantante calvaXXXXX";
		final String expectedStr2 = "la cantante";
		assertEquals(expectedStr1, ToolBox.padAndClip(testStr, testStr.length() + 5, 'X'));
		assertEquals(expectedStr2, ToolBox.padAndClip(testStr, expectedStr2.length(), 'X'));
	}

//	@Test
//	public void testGetString() throws Exception {
//		final String testStr = "copy and paste this text";
//		assertEquals(testStr, ToolBox.getString(testStr));
//	}

	@DataProvider
	public static Object[][] integerDataProvider() {
		return new Object[][]{
			{"123",   true,  "is an integer"},
			{"12.3",  false, "is an integer"},
			{"123.",  false, "is an integer"},
			{".123",  false, "is an integer"},
			{".",     false, "is not an integer"},
			{"1 ",    false, "is not an integer"},
			{"1.2.3", false, "is not an integer"},
			{"1..23", false, "is not an integer"},
			{"1.abc", false, "is not an integer"},
			{"abc",   false, "no is an integer"},
			{"a1c",   false, "no is an integer"},
		};
	}

	@Test(dataProvider = "integerDataProvider")
	public void testIsInteger(final String testValue, final boolean expectedResult, final String reason) {
		assertEquals(ToolBox.isInteger(testValue), expectedResult, '[' + testValue + "] " + reason);
	}

	@DataProvider
	public static Object[][] numberDataProvider() {
		return new Object[][]{
			{"123",   true,  "is a number"},
			{"12.3",  true,  "is a number"},
			{"123.",  true,  "is a number"},
			{".123",  true,  "is a number"},
			{".",     false, "is not a number"},
			{"1 ",    false, "is not a number"},
			{"1.2.3", false, "is not a number"},
			{"1..23", false, "is not a number"},
			{"1.abc", false, "is not a number"},
			{"abc",   false, "no is a number"},
			{"a1c",   false, "no is a number"},
		};
	}

	@Test(dataProvider = "numberDataProvider")
	public void testIsNumber(final String testValue, final boolean expectedResult, final String reason) {
		assertEquals(ToolBox.isNumber(testValue), expectedResult, '[' + testValue + "] " + reason);
	}

//	@Test
//	public void testGetExecutionPath() {
//		assertEquals("", ToolBox.getExecutionPath(this));
//	}
}
