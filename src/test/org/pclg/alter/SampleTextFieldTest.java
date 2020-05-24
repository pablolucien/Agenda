package org.pclg.alter;

import junit.framework.TestCase;
import org.pclg.gui.JLabeledField;

/**
 * @since 14/07/2017.
 */
public class SampleTextFieldTest extends TestCase {
    //@Test
    public void testSampleTextField() throws Exception {
        final JLabeledField startField = new JLabeledField("start");
        final JLabeledField lenField = new JLabeledField("len");
        final SampleTextField sampleTextField = new SampleTextField(startField, lenField);
        assertEquals("Should be empty", "", startField.getText());
        assertEquals("Should be empty", "", lenField.getText());
        sampleTextField.setText("La donna è mobile");
        assertEquals("Should be empty", "", startField.getText());
        assertEquals("Should be empty", "", lenField.getText());
        sampleTextField.setCaretPosition(2);
        sampleTextField.moveCaretPosition(5);
        assertEquals("Should be 2", "2", startField.getText());
        assertEquals("Should be 3", "3", lenField.getText());
        sampleTextField.setCaretPosition(17);
        sampleTextField.moveCaretPosition(16);
        assertEquals("Should be -1", "-1", startField.getText());
        assertEquals("Should be 1", "1", lenField.getText());
        sampleTextField.setCaretPosition(5);
        sampleTextField.moveCaretPosition(2);
        assertEquals("Should be -13", "-13", startField.getText());
        assertEquals("Should be 3", "3", lenField.getText());
        // TODO: Test colors
    }
}
