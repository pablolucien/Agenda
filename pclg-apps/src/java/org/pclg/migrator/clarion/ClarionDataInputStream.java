// ******************************** package
package org.pclg.migrator.clarion;

// ******************************** imports

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * ClarionDataInputStream.
 *
 * @author El Coyote Cojo
 * @version 2002.ene.16 20:15:07, CEST
 */
final class ClarionDataInputStream extends DataInputStream {
    /**
     * Constructs a ClarionDataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param is the specified input stream.
     */
    ClarionDataInputStream(final InputStream is) {
        super(is);
    }

    // LE == Little Endian

    /**
     * Reads a Clarion unsigned short.
     *
     * @return a Clarion unsigned short.
     * @throws IOException if an I/O error occurs.
     */
    int readLEUnsignedShort() throws IOException {
        final int b1 = read();
        final int b2 = read();
        return (b2 << 8) + (b1);
    }

    /**
     * Reads a Clarion Unsigned Int.
     *
     * @return a Clarion Unsigned Int.
     * @throws IOException if an I/O error occurs.
     */
    long readLEUnsignedInt() throws IOException {
        final long b1 = read();
        final long b2 = read();
        final long b3 = read();
        final long b4 = read();
        return (b4 << 24L) + (b3 << 16L) + (b2 << 8L) + (b1);
    }

    /**
     * Reads a Clarion time.
     *
     * @return a Clarion time.
     * @throws IOException if an I/O error occurs.
     */
    Date readClarionTime() throws IOException {
        return ClarionTools.clarionLong2Time(readLEUnsignedInt());
    }

    /**
     * Read a date from a Clarion file.
     *
     * @return a clarion date.
     * @throws IOException if an I/O error occurs.
     */
    Date readClarionDate() throws IOException {
        return ClarionTools.clarionLong2Date(readLEUnsignedInt());
    }
}
