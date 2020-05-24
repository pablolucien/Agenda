// ******************************** package
package org.pclg.migrator.clarion;

// ******************************** imports

import org.pclg.tools.ToolBox;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

/**
 * Represents the header of a Clarion file.
 *
 * The following example is a C structure that defines the file header:
 * struct {
 * unsigned filesig;		// file signature
 * unsigned sfatr;			// file attribute and status
 * unsigned char numbkeys;	// number of keys in file
 * unsigned long numrecs;	// number of records in file
 * unsigned long numdels;	// number of deleted records
 * unsigned numflds;		// number of fields
 * unsigned numpics;		// number of pictures
 * unsigned nummars;		// number of array descriptors
 * unsigned reclen;		// record length (including record header)
 * unsigned long offset;	// start of data area
 * unsigned long logoef;	// logical end of file
 * unsigned long logbof;	// logical beginning of file
 * unsigned long freerec;	// first usable deleted record
 * unsigned char recname[12];// record name without prefix
 * unsigned char memnam[12];// memo name without prefix
 * unsigned char filpre[3];// file name prefix
 * unsigned char recpre[3];// record name prefix
 * unsigned memolen;		// size of memo
 * unsigned memowid;		// column width of memo
 * unsigned long reserved;	// reserved
 * unsigned long chgtime;	// time of last change
 * unsigned long chgdate;	// date of last change
 * unsigned reserved2;		// reserved
 * };
 * <p/>
 * When looking at a dump of a .DAT file, remember that fields marked "unsigned"
 * (otherwise known as "unsigned int") use 2 bytes,
 * "unsigned char" fields use 1 byte, and "unsigned long" fields use 4 bytes.
 * Due to the way the Intel CPUs store their data, bytes of unsigned longs and
 * unsigned ints are  reversed on disk.
 * Almost all of the fields in the header are numbers or strings.
 * One exception is a bit-map.  The file attribute field (sfatr) is a 2-byte field.
 * Currently, only the lower 8 bits are used.  Depending on what is currently
 * happening to the file, the following bits are either turned on or off.
 * Bit 0 is the low order bit and bit 15 is the high order bit.
 * If a bit is turned on, the corresponding condition is true for the file:
 * bit 0 - file is locked
 * bit 1 - file is owned
 * bit 2 - records are encrypted
 * bit 3 - memo file exists
 * bit 4 - file is compressed
 * bit 5 - reclaim deleted records
 * bit 6 - file is read only
 * bit 7 - file may be created.
 *
 * @author El Coyote cojo.
 * @version Unknown.
 */
final class ClarionFileHeader {

    /** file signature. */
    private int filesig;

    /** file attribute and status. */
    private int sfatr;

    /** number of keys in file. */
    private short numbkeys;

    /** number of records in file. */
    private long numrecs;

    /** number of deleted records. */
    private long numdels;

    /** number of fields. */
    private int numflds;

    /** number of pictures. */
    private int numpics;

    /** number of array descriptors. */
    private int nummars;

    /** record length (including record header). */
    private int reclen;

    /** start of data area. */
    private long offset;

    /** logical end of file. */
    private long logoef;

    /** logical beginning of file. */
    private long logbof;

    /** first usable deleted record. */
    private long freerec;

    /** record name without prefixa as a byte[]. */
    private final byte[] recname = new byte[12];

    /** record name without prefixa as a String. */
    private String recnameS;

    /** memo name without prefix as a byte[]. */
    private final byte[] memnam = new byte[12];

    /** memo name without prefix as a String. */
    private String memnamS;

    /** file name prefix as a byte[]. */
    private final byte[] filpre = new byte[3];

    /** file name prefix as a String. */
    private String filpreS;

    /** record name prefix as a byte[]. */
    private final byte[] recpre = new byte[3];

    /** record name prefix as a String.  */
    private String recpreS;

    /** size of memo. */
    private int memolen;

    /** column width of memo. */
    private int memowid;

    /** reserved. */
    private long reserved;

    // Estos dos se pueden agrupar en uno solo ???????????????????????????????????????????????????????????????????

    /** time of last change. */
    private Date chgtime;

    /** date of last change. */
    private Date chgdate;

    /** reserved. */
    private short reserved2;

    /**  Where to write. */
    private final PrintStream miSalida = System.out;

    /** */
    private ClarionRecord record;

    /** Size of buffers. */
    private static final int BUFFER_SIZE = 1024;

    /** Line separator. */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Constructs a ClarionFileHeader given a file.
     * @param fileName the name of the file.
     */
    ClarionFileHeader(final String fileName) {
		final ClarionDataInputStream in;
		try {
			in = new ClarionDataInputStream(new FileInputStream(fileName));
			try {
				readHeader(in);
				if (false) {
					printHeader();
				}
				record = new ClarionRecord(fileName, offset, in, numflds, memnamS, memolen);
				if (false) {
					getRecordDescription();
					printKeysDescription(in);	// Ojo: modifica la posicion del Stream
				}
				in.close();
			} catch (final IOException ex) {
				ToolBox.showInfo(ex);
			} finally {
				in.close();
			}
		} catch (final Exception fnfex) {
			ToolBox.showInfo(fnfex);
		}
    }

    private void readHeader(final ClarionDataInputStream inputStream) throws IOException {
        filesig = inputStream.readLEUnsignedShort();
        sfatr = inputStream.readLEUnsignedShort();
        numbkeys = (short) inputStream.readUnsignedByte();
        numrecs = inputStream.readLEUnsignedInt();
        numdels = inputStream.readLEUnsignedInt();
        numflds = inputStream.readLEUnsignedShort();
        numpics = inputStream.readLEUnsignedShort();
        nummars = inputStream.readLEUnsignedShort();
        reclen = inputStream.readLEUnsignedShort();
        offset = inputStream.readLEUnsignedInt();
        logoef = inputStream.readLEUnsignedInt();
        logbof = inputStream.readLEUnsignedInt();
        freerec = inputStream.readLEUnsignedInt();
/*
		inputStream.skipBytes(0);
*/
		if (inputStream.read(recname) != recname.length) {
			throw new IOException("Unexpected end of Stream");
		}
        recnameS = ToolBox.normalizeName(new String(recname).trim());

		if (inputStream.read(memnam) != memnam.length) {
			throw new IOException("Unexpected end of Stream");
		}
        memnamS = ToolBox.normalizeName(new String(memnam).trim());

		if (inputStream.read(filpre) != filpre.length) {
			throw new IOException("Unexpected end of Stream");
		}
        filpreS = ToolBox.normalizeName(new String(filpre).trim());

		if (inputStream.read(recpre) != recpre.length) {
			throw new IOException("Unexpected end of Stream");
		}
        recpreS = ToolBox.normalizeName(new String(recpre).trim());

        memolen = inputStream.readLEUnsignedShort();
        memowid = inputStream.readLEUnsignedShort();
        reserved = inputStream.readLEUnsignedInt();
        chgtime = inputStream.readClarionTime();
        chgdate = inputStream.readClarionDate();
        reserved2 = (short) inputStream.readLEUnsignedShort();
    }

    private void printHeader() {
        final StringBuilder buffer = new StringBuilder(BUFFER_SIZE);
        buffer.append(filesig).append(" (").append(Integer.toHexString(filesig))
                .append(")		/* file signature */").append(LINE_SEPARATOR)
                .append(sfatr).append("		/* file attribute and status */").append(LINE_SEPARATOR);
        miSalida.println(buffer.toString());
        printFileAttributes();
        buffer.setLength(0);
        buffer.append(numbkeys).append("		/* number of keys in file */").append(LINE_SEPARATOR)
                .append(getNumrecs()).append("		/* number of records in file */").append(LINE_SEPARATOR)
                .append(numdels).append("		/* number of deleted records */").append(LINE_SEPARATOR)
                .append(numflds).append("		/* number of fields */").append(LINE_SEPARATOR)
                .append(numpics).append("		/* number of pictures */").append(LINE_SEPARATOR)
                .append(nummars).append("		/* number of array descriptors */").append(LINE_SEPARATOR)
                .append(reclen).append("		/* record length (including record header) */").append(LINE_SEPARATOR)
                .append(offset).append("		/* start of data area */").append(LINE_SEPARATOR)
                .append(logoef).append("		/* logical end of file */").append(LINE_SEPARATOR)
                .append(logbof).append("		/* logical beginning of file */").append(LINE_SEPARATOR)
                .append(freerec).append("		/* first usable deleted record */").append(LINE_SEPARATOR)
                .append('[').append(recnameS).append("]	/* record name without prefix */").append(LINE_SEPARATOR)
                .append('[').append(memnamS).append("]	/* memo name without prefix */").append(LINE_SEPARATOR)
                .append('[').append(filpreS).append("]		/* file name prefix */").append(LINE_SEPARATOR)
                .append('[').append(recpreS).append("]		/* record name prefix */").append(LINE_SEPARATOR)
                .append(memolen).append("		/* size of memo */").append(LINE_SEPARATOR)
                .append(memowid).append("		/* column width of memo */").append(LINE_SEPARATOR)
                .append(reserved).append("		/* reserved */").append(LINE_SEPARATOR)
                .append(chgtime).append("	/* time of last change */").append(LINE_SEPARATOR)
                .append(chgdate).append("	/* date of last change */").append(LINE_SEPARATOR)
                .append(reserved2).append("		/* reserved 2 */").append(LINE_SEPARATOR);
        miSalida.println(buffer.toString());
    }

    private void printFileAttributes() {
        final String[] attributes = {
            "file is locked",
            "file is owned",
            "records are encrypted",
            "memo file exists",
            "file is compressed",
            "reclaim deleted records",
            "file is read only",
            "file may be created"
        };

//			sfatr = 8;
        String attr = Integer.toBinaryString(sfatr);		// ??? Usar un bitmap booleano
        attr = ToolBox.leftPad(attr, 8, '0');
        attr = new StringBuffer(attr).reverse().toString();
        for (int ii = 0; ii < attr.length(); ii++) {
            if (attr.charAt(ii) == '1') {
                miSalida.println('\t' + attributes[ii]);
            }
        }
        //miSalida.println(sfatr);
        //miSalida.println(attr);
    }

    public ClarionRecord getRecord() {
        return record;
    }

    private void getRecordDescription() {
        //miSalida.println("getRecordDescription()");
        record.print();
    }

    public String[] getFieldNames() {
        return record.getFieldNames();
    }

    private void printKeysDescription(final ClarionDataInputStream inputStream) throws IOException {	// Ojo: modifica la posicion del Stream
        for (int ii = 0; ii < numbkeys; ii++) {
            final ClarionKey key = new ClarionKey(inputStream);
				key.print();
        }
    }

    long getNumrecs() {
        return numrecs;
    }
}
