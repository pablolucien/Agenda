package org.pclg.tools;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.pclg.Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;
import static org.testng.Assert.assertEquals;

public class ArrayToolsTest {
    private Object[] nullObjectArray;
    private char[] nullCharArray;

    @Test
    public void testPrintNullCharArray() {
        final String expected = "char[] = null" + System.lineSeparator();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(out);
        ArrayTools.printArray(output, nullCharArray);
        assertEquals(expected, new String(out.toByteArray()));
    }

    @Test
    public void testPrintNullCharArrayWithBounds() {
        final String expected = "char[] = null" + System.lineSeparator();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(out);
        ArrayTools.printArray(output, nullCharArray, THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING, 0);
        assertEquals(expected, new String(out.toByteArray()));
    }

    @Test
    public void testPrintCharArray() {
        final String expected1 = "char[] = [1, 2, 3, 4, 5, 6, 7]" + System.lineSeparator();
        final String expected2 = "char[] = []" + System.lineSeparator();
        final char[] args = {'1', '2', '3', '4', '5', '6', '7'};
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(out);
        ArrayTools.printArray(output, args);
        assertEquals(expected1, new String(out.toByteArray()));
        out.reset();
        ArrayTools.printArray(output, new char[0]);
        assertEquals(expected2, new String(out.toByteArray()));
    }

    @Test
    public void testPrintCharArrayWithBounds() {
        final String expected1 = "char[] = [..., 3, 4, 5, ...]" + System.lineSeparator();
        final String expected2 = "char[] = [1, 2, 3, 4, 5, ...]" + System.lineSeparator();
        final String expected3 = "char[] = [..., 3, 4, 5, 6, 7]" + System.lineSeparator();
        final char[] args = {'1', '2', '3', '4', '5', '6', '7'};
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(out);
        ArrayTools.printArray(output, args, 2, 3);
        assertEquals(expected1, new String(out.toByteArray()));
        out.reset();
        ArrayTools.printArray(output, args, 0, 5);
        assertEquals(expected2, new String(out.toByteArray()));
        out.reset();
        ArrayTools.printArray(output, args, 2, 5);
        assertEquals(expected3, new String(out.toByteArray()));
    }

    @Test
    public void testPrintNullObjectArray() {
        final String expected = "[] = null" + System.lineSeparator();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(out);
        ArrayTools.printArray(output, nullObjectArray);
        assertEquals(expected, new String(out.toByteArray()));
    }

    @Test
    public void testPrintNullObjectArrayWithBounds() {
        final String expected = "[] = null" + System.lineSeparator();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(out);
        ArrayTools.printArray(output, nullObjectArray, THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING, 0);
        assertEquals(expected, new String(out.toByteArray()));
    }

    @Test
    public void testPrintObjectArray() {
        final String expected = "java.lang.String[] = [1, 2, 3, 4, 5, 6, 7]" + System.lineSeparator();
        final String[] args = {"1", "2", "3", "4", "5", "6", "7"};
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(out);
        ArrayTools.printArray(output, args);
        assertEquals(expected, new String(out.toByteArray()));
    }

    @Test
    public void testPrintObjectArrayWithBounds() {
        final String expected1 = "java.lang.String[] = [..., 3, 4, 5, ...]" + System.lineSeparator();
        final String expected2 = "java.lang.String[] = [1, 2, 3, 4, 5, ...]" + System.lineSeparator();
        final String expected3 = "java.lang.String[] = [..., 3, 4, 5, 6, 7]" + System.lineSeparator();
        final String[] args = {"1", "2", "3", "4", "5", "6", "7"};
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream output = new PrintStream(out);
        ArrayTools.printArray(output, args, 2, 3);
        assertEquals(expected1, new String(out.toByteArray()));
        out.reset();
        ArrayTools.printArray(output, args, 0, 5);
        assertEquals(expected2, new String(out.toByteArray()));
        out.reset();
        ArrayTools.printArray(output, args, 2, 5);
        assertEquals(expected3, new String(out.toByteArray()));
    }
}