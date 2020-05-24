package org.pclg.gui;

import org.testng.annotations.Test;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import static org.testng.Assert.assertEquals;

public class IntegerTextFieldLimiterTest {

    @Test
    public void emptyTextPlus() throws Exception {
        final JTextField textField = new JTextField();
        final IntegerTextFieldLimiter document = new IntegerTextFieldLimiter(5);
        textField.setDocument(document);
        textField.setText("");
        document.insertString(0, "+", null);
        assertEquals(textField.getText(), "1", "Should be incremented");
    }

    @Test
    public void emptyTextMinus() throws Exception {
        final JTextField textField = new JTextField();
        final IntegerTextFieldLimiter document = new IntegerTextFieldLimiter(5);
        textField.setDocument(document);
        textField.setText("");
        document.insertString(0, "-", null);
        assertEquals(textField.getText(), "0", "Should be 0");
    }

    @Test
    public void insertString() throws Exception {
        final JTextField textField = new JTextField();
        textField.setDocument(new IntegerTextFieldLimiter(5));
        String text = "";
        for (int ii = 0; ii < 5; ii++) {
            text += "1";
            textField.setText(text);
            assertEquals(text, textField.getText());
        }
        textField.setText("1234");
        textField.setText("abcd");
        assertEquals("", textField.getText());

        textField.setText("1234567890");
        assertEquals("", textField.getText());
    }

    @Test
    public void plusAndMinus() throws BadLocationException {
        final JTextField textField = new JTextField();
        final IntegerTextFieldLimiter document = new IntegerTextFieldLimiter(-1);
        textField.setDocument(document);
        textField.setText("1234567890");
        assertEquals("1234567890", textField.getText());

        document.insertString(0, "-", null);
        assertEquals("1234567889", textField.getText());

        document.insertString(0, "+", null);
        assertEquals("1234567890", textField.getText());

        textField.setText("2");
        document.insertString(0, "-", null);
        assertEquals("1", textField.getText());
        document.insertString(0, "-", null);
        assertEquals("0", textField.getText());
        document.insertString(0, "-", null);
        assertEquals("0", textField.getText());
    }
}
