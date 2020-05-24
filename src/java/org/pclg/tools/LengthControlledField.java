package org.pclg.tools;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
public class LengthControlledField extends JTextField {
    private static final long serialVersionUID = 8968799929815188266L;
    private int maxLength;

   public LengthControlledField(final int cols) {
      super(cols);
      maxLength = cols;
   }

   public LengthControlledField(final String texto) {
      super(texto);
   }

   public void setLength(final int length) {
      maxLength = length;
   }

   @Override
   protected Document createDefaultModel() {
      return new LengthControlledDocument();
   }

   private class LengthControlledDocument extends PlainDocument {

       private static final long serialVersionUID = -772283555022612455L;

       @Override
	  public void insertString(final int offs, final String str, final AttributeSet a)
         throws BadLocationException {

         if (str == null) {
            return;
         }
         if(maxLength > 0) {
			 if (getLength() + str.length() > maxLength) {
				 return;
			 }
		 }
         super.insertString(offs, str, a);
      }
   }
}
