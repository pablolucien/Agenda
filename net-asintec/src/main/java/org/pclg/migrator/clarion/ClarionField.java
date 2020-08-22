// ******************************** package
package org.pclg.migrator.clarion;

// ******************************** imports

import org.pclg.tools.ToolBox;

import java.io.IOException;
import java.io.PrintStream;

/**
 * ClarionField.
 *
 * @author El Coyote Cojo
 * @version 2002.ene.16 20:15:07, CEST
 */
class ClarionField {
    /**
     * Where to write.
     */
    private final PrintStream miSalida = System.out;

    /**
     * La longitud del prefijo de los campos, que es una cosa asi como 'REC:'.
     */
    private static final int PREFIX_LEN = 4;
    /*
    Field descriptors follow the file header.  There are as many field descriptor
    entries as there are fields in the file. Each field descriptor consists
    of 8 fields.  They are defined as follows:

    struct {
            unsigned char fldtype;		// type of field
            unsigned fldname[16];		// name of field
            unsigned foffset;			// offset into record
            unsigned length;			// length of field
            unsigned char decsig;		// significance for decimals
            unsigned char decdec;		// number of decimal places
            unsigned arrnum;			// array number
            unsigned picnum;			// picture number
    };

    The "fldtype" field defines the field type.  The following types are currently used.

        1 - LONG
        2 - REAL
        3 - STRING
        4 - STRING WITH PICTURE TOKEN
        5 - BYTE
        6 - SHORT
        7 - GROUP
        8 - DECIMAL
    */

    /**
     * type of field.
     */
    final byte fldtype;

    /**
     * name of field as a byte[].
     */
    private final byte[] fldname = new byte[16];

    /**
     * name of field as a String.
     */
    final String fldnameS;

    /**
     * offset into record.
     */
    private int foffset;

    /**
     * length of field.
     */
    final int length;

    /**
     * significance for decimals.
     */
    private byte decsig;

    /**
     * number of decimal places.
     */
    private byte decdec;

    /**
     * array number.
     */
    private int arrnum;

    /**
     * picture number.
     */
    private int picnum;

    /**
     * Construye un campo de tipo MEMO.
     *
     * @param memoName the name of the field.
     * @param memoLen  the length of the field.
     */
	ClarionField(final String memoName, final int memoLen) {
        fldtype = ClarionTypes.MEMO;
        fldnameS = memoName.trim();
        length = memoLen;
    }

    /**
     * Construye un campo standard.
     *
     * @param inputStream the source of the data.
     * @throws IOException if an I/O error occurs.
     */
    public ClarionField(final ClarionDataInputStream inputStream) throws IOException {
        fldtype = inputStream.readByte();
        if (inputStream.read(fldname) != fldname.length) {
			throw new IOException("Unexpected end of Stream");
		}
        fldnameS = new String(fldname, PREFIX_LEN, fldname.length - PREFIX_LEN).trim();		// Eliminamos el prefijo
        foffset = inputStream.readLEUnsignedShort();
        length = inputStream.readLEUnsignedShort();
        decsig = inputStream.readByte();
        decdec = inputStream.readByte();
        arrnum = inputStream.readLEUnsignedShort();
        picnum = inputStream.readLEUnsignedShort();
    }


    /**
     * Prints the content of this field.
     */
    public final void print() {
        miSalida.println('[' + fldnameS + "]		/* name of field */");
        miSalida.println(ClarionTypes.fieldTypeName(fldtype) + "		/* type of field */");
        miSalida.println(String.valueOf(foffset) + "		/* offset into record */");
        miSalida.println(String.valueOf(length) + "		/* length of field */");
        miSalida.println(String.valueOf(decsig) + "		// significance for decimals ");
        miSalida.println(String.valueOf(decdec) + "		// number of decimal places ");
        miSalida.println(String.valueOf(arrnum) + "		// array number ");
        miSalida.println(String.valueOf(picnum) + "		// picture number ");
        miSalida.println("-------------------------------------------------------");
    }

    /**
     * Imprime el contenido de este campo, suponiendo que el byte[] es el registro.
     */
    public void printData(final byte[] recHeader, final byte[] rec) {
        System.err.println(fldnameS + "\t[" + getData(recHeader, rec) + ']');
    }

    /**
     * Devuelve el contenido de este campo, suponiendo que el byte[] es el registro.
     */
    public String getData(final byte[] recHeader, final byte[] rec) {
        final long value;
        final String data;

        switch (fldtype) {
        case ClarionTypes.STRING:
            data = new String(rec, foffset, length);
            break;
        case ClarionTypes.BYTE:
            value = ToolBox.unsignedByte(rec[foffset]);
            data = String.valueOf(value);
            break;
        case ClarionTypes.SHORT:
            value = (ToolBox.unsignedByte(rec[foffset + 1]) << 8L)
                    + (ToolBox.unsignedByte(rec[foffset]));
            data = String.valueOf(value);
            break;
        case ClarionTypes.LONG:
            value = (ToolBox.unsignedByte(rec[foffset + 3]) << 24L)
                    + (ToolBox.unsignedByte(rec[foffset + 2]) << 16L)
                    + (ToolBox.unsignedByte(rec[foffset + 1]) << 8L)
                    + (ToolBox.unsignedByte(rec[foffset]));
            data = String.valueOf(value);
            break;
        case ClarionTypes.GROUP:
            // ¿No debemos devolver nada o todos los componentes del grupo????
            data = "GROUP";
            break;
        case ClarionTypes.REAL:
        case ClarionTypes.STRING_WITH_PICTURE_TOKEN:
        case ClarionTypes.DECIMAL:
        default:
            data = "AUN NO MANEJO ESTE TIPO DE DATO: "
                    + ClarionTypes.fieldTypeName(fldtype);
        }
        return data;
    }
}
