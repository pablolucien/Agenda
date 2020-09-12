package org.pclg.log;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

import javax.swing.JTextArea;
import java.awt.Font;
import java.io.Writer;

/**
 * @since 08/08/2018.
 */
public class TextAreaAppender extends WriterAppender {
    public TextAreaAppender(final JTextArea textArea) {
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        setName(toString());
        setLayout(new PatternLayout("%level - %m%n"));
        setWriter(new Writer() {
            @Override
            public void write(final char[] cbuf, final int off, final int len) {
                final String str = new String(cbuf, off, len);
//                System.out.println("str = *********************************** " + str);
                textArea.append(str);
//                textArea.update(textArea.getGraphics());
//                textArea.repaint();
            }

            @Override
            public void flush() {
                // Do nothing, but must be implemented.
            }

            @Override
            public void close() {
                TextAreaAppender.this.close();
            }
        });
    }
}
