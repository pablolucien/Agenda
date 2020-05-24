// ******************************** package
package org.pclg.migrator.clarion;

// ******************************** imports

import org.pclg.tools.ArrayTools;
import org.pclg.tools.ToolBox;

import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.sql.Types;

/**
 * ClarionRecord.
 *
 * @author El Coyote Cojo
 * @version 2002.ene.16 20:15:07, CEST
 */
public final class ClarionRecord {
    /**  Where to write. */
     private final PrintStream miSalida = System.out;

    /**
     * el numero de campos de este registro.
     */
    private int numflds;

    /**
     * el tamaño en bytes de este registro.
     */
    private int reclen;

    /**
     * El nombre del campo memo si lo hay.
     */
    private String memoName;

    /**
     * el tamaño en bytes del campo memo si lo hay.
     */
    private int memoLen;

    /**
     * la descripcion de los campos.
     */
    private ClarionField[] fields;

    /**
     * start of data area.
     */
    private final long offset;

    /**
     * El repositorio de los datos.
     */
    private final RandomAccessFile raf;

    /**
     *
     * @param dataFileName
     * @param offset
     * @param inputStream
     * @param numflds
     * @param memoName
     * @param memoLen
     * @throws IOException
     */
    public ClarionRecord(final String dataFileName, final long offset,
            final ClarionDataInputStream inputStream, final int numflds,
            final String memoName, final int memoLen) throws IOException {
		raf = new RandomAccessFile(dataFileName, "r");
        this.offset = offset;
        this.numflds = numflds;
        reclen = 0;
        if (memoLen > 0) {
            this.memoLen = memoLen;
            this.memoName = memoName;
        }

        fields = new ClarionField[numflds];
        for (int ii = 0; ii < fields.length; ii++) {
            fields[ii] = new ClarionField(inputStream);
            // si el tipo de dato es GROUP no sumamos la longitud, porque se suma la de los componentes individuales
            if (fields[ii].fldtype != ClarionTypes.GROUP) {
                reclen += fields[ii].length;
            }
        }

        // Si hay memo, agregamos un campo más				??? Esto se puede optimizar
        if (memoLen > 0) {
            this.numflds++;
            final ClarionField[] memo = new ClarionField[1];
			// Aquí suponemos que el archivo de datos siempre termina con
			// .DAT y el de memo con .MEM; supongo que es una suposición
			// razonable.
			final String memoFileName = dataFileName
				.substring(0, dataFileName.toUpperCase().lastIndexOf(".DAT"))
				+ ".MEM";
			memo[0] = new ClarionMemoField(memoName, memoLen, memoFileName);
            fields = (ClarionField[]) ArrayTools.concat(fields, memo);
        }

        //miSalida.println("ClarionRecord()" + reclen);
    }

	@Override
	protected void finalize() throws Throwable {
		raf.close();
		super.finalize();
	}

	/**
     * Devuelve un arreglo con los nombres de los campos de la tabla que está abierta.
     *
     * @return Un arreglo con los nombres de los campos de la tabla que está abierta.
     */
    public String[] getFieldNames() {
        final String[] names = new String[fields.length];
        for (int ii = 0; ii < fields.length; ii++) {
            names[ii] = fields[ii].fldnameS;
        }
        return names;
    }

    /**
     * Devuelve un arreglo con los tamaños de los campos de la tabla que está abierta.
     *
     * @return Un arreglo con los tamaños de los campos de la tabla que está abierta.
     */
    public int[] getFieldSizes() {
        final int[] fieldSizes = new int[fields.length];
        for (int ii = 0; ii < fields.length; ii++) {
            fieldSizes[ii] = fields[ii].length;
        }
        return fieldSizes;
    }

    public void print() {
        miSalida.println("Campos: ---------------------");
		for (final ClarionField field : fields) {
			field.print();
		}
/*		
		if(memoLen > 0) {
			miSalida.println("[" + memoName + "]	Nombre del campo");
			miSalida.println("MEMO 			Tipo del campo");
			miSalida.println("" + memoLen + " 		Tama\u00F1o del campo");
		}
*/
        //Imprimir algunos registros
        print(1);
        //print(7);
        //print(100);
    }

    /**
     * Imprimir un registro.
     * @param recno el nro del registro deseado.
     */
	void print(final int recno) {
        final byte[] recHeader = readHeader(recno);
        final byte[] recBytes = readData(recno);
        System.err.println("________________________________________________");
		for (final ClarionField field : fields) {
			field.printData(recHeader, recBytes);
		}
    }

    /**
     * Devuelve un registro.
     * @param recno el nro del registro deseado.
     * @param selectedFields los nombres de los campos que queremos.
     * @return los campos que queremos en un Object[].
     */
    public Object[] get(final int recno, final String[] selectedFields) {
        final Object[] data = new Object[selectedFields.length];
        final byte[] recHeader = readHeader(recno);
        final byte[] recBytes = readData(recno);
        for (int jj = 0; jj < selectedFields.length; jj++) {
            // Buscamos la posicion del campo real de la 'base de datos'
            boolean found = false;
			for (final ClarionField field : fields) {
				if (field.fldnameS.equals(selectedFields[jj])) {
					data[jj] = field.getData(recHeader, recBytes);
					found = true;
					break;
				}
			}
            // si no es un campo real, debe ser una constante
            if (!found) {
                // Segun como funciona el Arbeiter, la constante viene entre comillas simples, que hay que eliminar
                data[jj] = selectedFields[jj].substring(1, selectedFields[jj].length() - 1);
            }
        }
        return data;
    }

    /**
     * Leer el header de un registro del archivo.
     * @param recno el nro del registro deseado.
     */
    private byte[] readHeader(final int recno) {
        try {
            final byte[] recHead = new byte[5];		// Leer el header
            raf.seek(offset + (recno - 1) * (reclen + 5));
            raf.read(recHead);
            return recHead;
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
        }
        return null;
    }

    /**
     * Leer los datos de un registro del archivo.
     * @param recno el nro del registro deseado.
     */
    private byte[] readData(final int recno) {
        try {
            final byte[] recBytes = new byte[reclen];
            raf.seek(offset + (recno - 1) * (reclen + 5) + 5);
            raf.read(recBytes);
            return recBytes;
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
        }
        return null;
    }

    /**
     * Devuelve un arreglo con los tipos de los campos de una tabla.
     * @return Un arreglo con los tipos de los campos de una tabla.
     */
    public int[] getFieldTypes() {
        final int[] fieldTypes = new int[fields.length];
        for (int ii = 0; ii < fields.length; ii++) {
            switch (fields[ii].fldtype) {
            case ClarionTypes.BYTE:
                fieldTypes[ii] = Types.TINYINT;
                break;
            case ClarionTypes.LONG:
                fieldTypes[ii] = Types.INTEGER;	// BIGINT???
                break;
            case ClarionTypes.MEMO:
                fieldTypes[ii] = Types.VARCHAR;
                break;
            default:
                fieldTypes[ii] = Types.VARCHAR;
            }
        }
        return fieldTypes;
    }

    /**
     * Devuenve un arreglo con los nombres de los tipos de los campos de una tabla.
     * @return Un arreglo con los nombres de los tipos de los campos de una tabla
     */
    public String[] getFieldTypeNames() {
        final String[] fieldTypeNames = new String[fields.length];
        for (int ii = 0; ii < fields.length; ii++) {
            switch (fields[ii].fldtype) {
            case ClarionTypes.BYTE:
                fieldTypeNames[ii] = "TINYINT";
                break;
            case ClarionTypes.LONG:
                fieldTypeNames[ii] = "INTEGER";	// BIGINT???
                break;
			case ClarionTypes.MEMO:
			   fieldTypeNames[ii] = "VARCHAR";
			   break;
            default:
                fieldTypeNames[ii] = "VARCHAR";
            }
        }
        return fieldTypeNames;
    }
}
