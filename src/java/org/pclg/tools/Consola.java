package org.pclg.tools;

import org.pclg.gui.ColorChangerDocumentListener;
import org.pclg.gui.JLabeledField;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Consola extends PrintStream {
	private final JDialog frame;
    private final ConsoleOutputStream outputStream;

    public static Consola getConsola(final Window parent, final String title) {
        return new Consola(parent, title, new ConsoleOutputStream());
    }

    private Consola(final Window parent, final String title,
                   final ConsoleOutputStream os) {
		super(os);
		outputStream = os;
		frame = new JDialog(parent, title + " - Consola de java");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        final JPanel buttonPanel = new JPanel();
        final JButton clearBt = new JButton("Limpiar");
        buttonPanel.add(clearBt);
        final JLabeledField guardar = new JLabeledField("Guardar", "noname.txt");
        buttonPanel.add(guardar);
		frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		clearBt.addActionListener(event -> {
			if(event.getSource() == clearBt) {
				outputStream.clear();
			}
		});
		
		final JScrollPane scrollpane = new JScrollPane(os.text);
		frame.getContentPane().add("Center", scrollpane);
		frame.pack();
	}

	public void setVisible(final boolean visibility) {
		frame.setVisible(visibility);
	}

	public Rectangle getBounds() {
		return frame.getBounds();
	}

	public void setBounds(final Rectangle r) {
		frame.setBounds(r);
		frame.validate();
	}

	public void addWindowListener(final WindowListener l) {
		frame.addWindowListener(l);
	}

	public void updateLAF() {
		SwingUtilities.updateComponentTreeUI(frame);
	}

	public String getText() {
		return outputStream.getText();
	}

    public Window getWindow() {
        return frame;
    }

    private static class ConsoleOutputStream extends OutputStream {
        final JTextArea text = new JTextArea(20, 40);

        /** usar un StringBuffer para ir guardando y solo hacer el append cuando me digan refresh() */
        private final StringBuffer textBuffer = new StringBuffer();

        /**

        */
        private ConsoleOutputStream() {
            text.getDocument().addDocumentListener(
                new ColorChangerDocumentListener(text, Color.orange, Color.lightGray));
    //		text.setFont(new Font("Courier", Font.PLAIN, 14));
    //		text.setFont(new Font("Dialog", Font.ITALIC, 14));
        }

        /**
            Implementacion del metodo abstracto de OutputStream
        */
        @Override
        public void write(final int b) throws IOException {
            /*
            textBuffer.append((char) b);
            refresh();		// ????  Hacerlo sólo cuando lo pidan
            */
            text.append(String.valueOf((char) b));
        }

        public JTextArea getTextArea() {
            return text;
        }

        public String getText() {
            return text.getText();
        }

        public void refresh() {
            text.setText(textBuffer.toString());
        }

        public void clear() {
            text.setText("");
        }
    }
}
