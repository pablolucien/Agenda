package org.pclg.tools;


import org.junit.Test;

import static org.junit.Assert.*;

public class StringToolsTest {
	private static final String LONG_TEXT = "There were three of us--Mary, Eliza, and myself. I was approaching fifteen, Mary was about a year younger, and Eliza between twelve and thirteen years of age. Mamma treated us all as children, and was blind to the fact that I was no longer what I had been.";
	private static final String WRAPPED_TEXT = "There were three \n"
		+ "of us--Mary, \n"
		+ "Eliza, and myself. \n"
		+ "I was approaching \n"
		+ "fifteen, Mary was \n"
		+ "about a year \n"
		+ "younger, and Eliza \n"
		+ "between twelve and \n"
		+ "thirteen years of \n"
		+ "age. Mamma treated \n"
		+ "us all as \n"
		+ "children, and was \n"
		+ "blind to the fact \n"
		+ "that I was no \n"
		+ "longer what I had \n"
		+ "been.\n";
	private static final String INDENTED_WRAPPED_TEXT = "There were three \n"
		+ "\tof us--Mary, \n"
		+ "\tEliza, and myself. \n"
		+ "\tI was approaching \n"
		+ "\tfifteen, Mary was \n"
		+ "\tabout a year \n"
		+ "\tyounger, and Eliza \n"
		+ "\tbetween twelve and \n"
		+ "\tthirteen years of \n"
		+ "\tage. Mamma treated \n"
		+ "\tus all as \n"
		+ "\tchildren, and was \n"
		+ "\tblind to the fact \n"
		+ "\tthat I was no \n"
		+ "\tlonger what I had \n"
		+ "\tbeen.\n";
	public static final int WRAP_LEN = 20;

	@Test
	public void testValidEmail() throws Exception {
		assertTrue(StringTools.validEmail("a@b.c"));
		assertTrue(StringTools.validEmail("pepito.lito@babew.com"));
		assertFalse(StringTools.validEmail("pepito.lito@bab@w.com"));
		assertFalse(StringTools.validEmail("pep@ito"));
		assertFalse(StringTools.validEmail("pep.ito"));
		assertFalse(StringTools.validEmail("kk"));
	}

	@Test
	public void testStartsWithIgnoreCase() throws Exception {
		final String line = "La cantante calva se está peinando";
		final String begin = "La cantante calva";
		assertTrue(StringTools.startsWithIgnoreCase(line, begin.toLowerCase()));
		assertTrue(StringTools.startsWithIgnoreCase(line, begin.toUpperCase()));
		assertFalse(StringTools.startsWithIgnoreCase(line, "pepito" + begin));
	}

	@Test
	public void testWrapLine() {
		final String wrappedLine = StringTools.wrapLine(LONG_TEXT, WRAP_LEN, "");
		assertEquals(WRAPPED_TEXT, wrappedLine);
	}

	@Test
	public void testWrapLineIndent() {
		final String wrappedLine = StringTools.wrapLine(LONG_TEXT, WRAP_LEN, "\t");
		assertEquals(INDENTED_WRAPPED_TEXT, wrappedLine);
	}
}