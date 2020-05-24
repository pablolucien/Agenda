// ******************************** package
package org.pclg.tools;

// ******************************** imports

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Un PrintStream que descarta lo que le metan.
 * @author El Coyote Cojo.
 * @version 1.0
 */
final class NullPrintStream extends PrintStream {
    /**
     * Create a new print stream. This stream will not flush automatically.
     * @param out The output stream to which values and objects will be printed
     */
    public NullPrintStream(final OutputStream out) {
        super(out);
    }

    /**
     * Create a new print stream.
     * @param out  The output stream to which values and objects will be printed
     * @param autoFlush A boolean; if true, the output buffer will be flushed
     * whenever a byte array is written, one of the println methods is invoked,
     * or a newline character or byte ('\n') is written
     */
    public NullPrintStream(final OutputStream out, final boolean autoFlush) {
        super(out/*, autoFlush*/);
    }

    /**
     * Create a new print stream.
     * @param out  The output stream to which values and objects will be printed
     * @param autoFlush A boolean; if true, the output buffer will be flushed
     * whenever a byte array is written, one of the println methods is invoked,
     * or a newline character or byte ('\n') is written
     * @param encoding The name of a supported  character encoding
     */
    public NullPrintStream(final OutputStream out, final boolean autoFlush,
            final String encoding) {
        super(out/*, autoFlush, encoding*/);
    }

    /**
     * No hace nada: descarta los datos.
     * @param text  The <code>String</code> to be printed.
     */
    @Override
	public void println(final String text) {
    }
}

