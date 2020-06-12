package org.pclg.gui;

import org.testng.annotations.Test;

import javax.swing.text.Document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class JLabeledFieldTest {

	@Test
	public void testSetNumeric() {
		final JLabeledField field = new JLabeledField("Test label");
		field.setNumeric();
		field.setText("1234567890");
		assertEquals("1234567890", field.getText());
		field.setText("ABN");
		assertEquals("1234567890", field.getText());
		field.setText("971364582");
		assertEquals("971364582", field.getText());
		field.setText("9.71364582");
		assertEquals("9.71364582", field.getText());
		field.setText("97136458.2");
		assertEquals("97136458.2", field.getText());
		field.setText("971364582.");
		assertEquals("971364582.", field.getText());
		field.setText("97136.4582.");
		assertEquals("971364582.", field.getText());
	}

	@Test
	public void testSetInteger() {
		final JLabeledField field = new JLabeledField("Test label");
		field.setInteger();
		field.setText("1234567890");
		assertEquals("1234567890", field.getText());
		field.setText("ABN");
		assertEquals("1234567890", field.getText());
		field.setText("971364582");
		assertEquals("971364582", field.getText());
	}

	@Test
	public void testSetLowerCase() {
		final JLabeledField field = new JLabeledField("Test label");
		field.setLowerCase();
		field.setText("TEST");
		assertEquals("test", field.getText());
	}

	@Test
	public void testSetUpperCase() {
		final JLabeledField field = new JLabeledField("Test label");
		field.setUpperCase();
		field.setText("test");
		assertEquals("TEST", field.getText());
	}

	@Test
	public void testSetPlainCase() {
		final JLabeledField field = new JLabeledField("Test label");
		final String test = "Test";
		field.setUpperCase();
		field.setPlainCase();
		field.setText(test);
		assertEquals(test, field.getText());
		field.setLowerCase();
		field.setPlainCase();
		field.setText(test);
		assertEquals(test, field.getText());
	}

	@Test
	public void testIsValid() {
        final JLabeledField field = new JLabeledField("testTextValueChanged");
        final String stringValue = "abc";
        final String numericValue = "123.7";
        final String integerValue = "-123";
        final String naturalValue = "123";

        assertTrue(field.isValid(stringValue));

        field.setNumeric();
        assertFalse(field.isValid(stringValue));
        assertTrue(field.isValid(numericValue));

        field.setInteger();
        assertFalse(field.isValid(stringValue));
        assertFalse(field.isValid(numericValue));
        assertTrue(field.isValid(integerValue));

        field.setNatural();
        assertFalse(field.isValid(stringValue));
        assertFalse(field.isValid(numericValue));
        assertFalse(field.isValid(integerValue));
        assertTrue(field.isValid(naturalValue));

        field.setAny();
        assertTrue(field.isValid(stringValue));
	}

	@Test
	public void testGetDocument() {
		final JLabeledField field = new JLabeledField("test");
		final Document doc = field.getDocument();
		assertTrue (doc instanceof JLabeledField.JLabeledFieldDocument);
	}
}