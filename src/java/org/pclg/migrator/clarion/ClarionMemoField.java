// ******************************** package
package org.pclg.migrator.clarion;

// ******************************** imports

import org.pclg.tools.ToolBox;

import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

/**
 * ClarionMemoField
 * <p/>
 * MEMO FIELDS
 * Memo fields are unique because they are stored in a separate file from your data.
 * If there is a memo file associated with a data base, the "memnam" field of the data base header has name of
 * the memo field and "memolen" and "memowid" fields have the length and width of the memo field.
 * <p/>
 * If a record in the data base has a memo associated with it, the "rptr" field in the header,
 * occurring prior to each record, will have a value.  With this value, calculate the offset needed to go into the memo
 * file to read the memo.
 * Given "rptr", the following formula yields the offset:
 * <p/>
 * offset - ((rptr - 1) * 256) + 6
 * <p/>
 * The memo file consists of a 6 byte header, followed by 256 byte memo blocks.  The header appears below:
 * <p/>
 * struct {
 * unsigned memsig = 0x334D;/* memo file signature
 * unsigned long firstdel;/* first deleted memo block
 * };
 * <p/>
 * Memo blocks appear below:
 * <p/>
 * struct {
 * unsigned long nxtblk;/* next block for this memo
 * char       memo[252];/* memo text
 * };
 * <p/>
 * Read the memo file in 256 byte chunks, take the value of "rptr" and calculate the offset into the memo file.
 * Go to the offset and read 256 bytes.
 * The first four bytes will either point to the next 256 byte chunk or be zero, indicating that there are no more blocks
 * for the memo.  The remaining 252 bytes are plain text.
 *
 * @author El Coyote Cojo
 * @version 2002.ene.23 21:23:07, CEST
 */
public final class ClarionMemoField extends ClarionField {
    /**  Where to write. */
     final PrintStream miSalida = System.out;
    private final String memoFileName;

    /**
     * El repositorio de los datos.
     */
    private final RandomAccessFile raf;
    private static final String NOT_MEMO_FIELD = "Este campo no es de tipo MEMO por algun extraño motivo";

    /**
     * Construye un campo de tipo MEMO.
     */
    public ClarionMemoField(final String memoName, final int memoLen,
            final String memoFileName) throws IOException {
        super(memoName, memoLen);
        this.memoFileName = memoFileName;
        raf = new RandomAccessFile(memoFileName, "r");
    }


    /**
     * Imprime el contenido de este campo, suponiendo que el byte[] es el registro.
     */
    @Override
	public void printData(final byte[] recHeader, final byte[] rec) {
        System.err.println(fldnameS + "\t[" + getData(recHeader, rec) + ']');
    }

    /**
     * Devuelve el contenido de este campo, suponiendo que el byte[] es el registro.
     */
    @Override
	public String getData(final byte[] recHeader, final byte[] rec) {
        if (fldtype != ClarionTypes.MEMO) {
            throw new InvalidFieldTypeException(NOT_MEMO_FIELD);
        }

        try {
            final byte[] recMemoBytes = new byte[length];

            /*
                Each record of data is preceded by a header.
                This header is defined as follows:
                struct {
                    unsigned char rhd;	// record header type and status
                    unsigned long rptr;	// pointer for next deleted record or memo if active
                };
            */
			long pos = (ToolBox.unsignedByte(recHeader[4]) << 24)
				+ (ToolBox.unsignedByte(recHeader[3]) << 16)
				+ (ToolBox.unsignedByte(recHeader[2]) << 8)
				+ (ToolBox.unsignedByte(recHeader[1]));

            if (pos == 0) {
                // No tiene memo
                return "";
            }

            pos = (pos - 1L) * 256L + 6L;

            raf.seek(pos);

            final byte[] recMemoHeader = new byte[4];		// unsigned long nxtblk;/* next block for this memo
            // ??????? Arregrar esto para que lea los bloques siguientes
            raf.read(recMemoHeader);
            raf.read(recMemoBytes);

            // convertir los NULL en ' ' o terminar el String ????????
            for (int ii = 0; ii < recMemoBytes.length; ii++) {
                if (recMemoBytes[ii] == 0) {
                    recMemoBytes[ii] = ' ';
                }
            }
            return new String(recMemoBytes);
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
			return ex.getMessage();
        }
    }
}
