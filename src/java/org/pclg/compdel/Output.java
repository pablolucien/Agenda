package org.pclg.compdel;

//import org.pclg.tools.Constants;

import org.pclg.gui.ColorChangerDocumentListener;
import org.pclg.tools.PrintlnTarget;

import javax.swing.JTextArea;
import java.awt.Color;

/**
 * A place to print messages.
 *
 * @author pablo
 * @version $Revision: 1.5 $
 * @since 30-nov-2004
 */
final class Output extends JTextArea implements PrintlnTarget {
	/** Line separator. */
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final long serialVersionUID = -4395834382163483043L;

    Output() {
		final ColorChangerDocumentListener colorChangerDocumentListener =
			new ColorChangerDocumentListener(this);
		colorChangerDocumentListener.setEmptyColor(Color.pink);
		getDocument().addDocumentListener(colorChangerDocumentListener);
	}

	/**
     * Cleans all messages.
     */
    public void clear() {
        setText("");
    }

    /**
     * Prints an empty line.
     */
    @Override
	public void println() {
        append(LINE_SEPARATOR);
    }

    /**
     * Prints a message.
     *
     * @param text the message to be printed.
     */
    @Override
	public void println(final String text) {
        append(text);
        append(LINE_SEPARATOR);
    }
}
