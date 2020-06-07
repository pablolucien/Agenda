package misc.sopra.eci;

import org.pclg.log.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * We should forget about small efficiencies, say about 97% of the time: 
 * Premature optimization is the root of all evil. — Donald Knuth
 * 
 * Creado el 05-Dec-2008
 */

/**
 * Lee lo que genera Sopra de la base de datos de Remedy.
 *
 * @author Un autor en busca de personajes.
 * @since 05-Dec-2008
 */
final class LectorRemedy {
    private static final byte[] CRLF = "\r\n".getBytes();
    private static final String BASE_DIR = 
        "c:/plucien/proyectos/Extracción de datos Remedy/Pruebas/";
	private static final Logger LOGGER = LoggerFactory.make();


    private LectorRemedy() throws IOException {
        final FileInputStream fis = new FileInputStream(BASE_DIR + "FE25HED2");
        final FileOutputStream fos = new FileOutputStream(BASE_DIR 
            + "fe25hed2_proc.txt");
        final byte[] c1 = new byte[15];
        final byte[] c2 = new byte[30];
        final byte[] c3 = new byte[11];
        final byte[] c4 = new byte[30];
        final byte[] c5 = new byte[30];
        final byte[] c6 = new byte[11];
        final byte[] c7 = new byte[11];
        final byte[] c8 = new byte[254];
        final byte[] c9 = new byte[10];
        // Longitud hasta aquí = 402
        final byte[] c10 = new byte[500];
        // Longitud del registro = 902

        final byte[] lastRecord = new byte[c1.length];
        int numRecords = 0;
        int numRecordsSegúnSopra = 0;
        int numLongRecords = 0;
        while (fis.read(c1) == c1.length) {
            numRecordsSegúnSopra++;
            if (!Arrays.equals(c1, lastRecord)) {
                numRecords++;
                System.arraycopy(c1, 0, lastRecord, 0, lastRecord.length);
            }
            fos.write(c1);
            fis.read(c2);
            fos.write(c2);
            fis.read(c3);
            fos.write(c3);
            fis.read(c4);
            fos.write(c4);
            fis.read(c5);
            fos.write(c5);
            fis.read(c6);
            fos.write(c6);
            fis.read(c7);
            fos.write(c7);
            fis.read(c8);
            fos.write(c8);
            fis.read(c9);
            fos.write(c9);
            int len = Integer.parseInt(new String(c9).trim());
            if (len == 0) {
				LOGGER.log(Level.SEVERE, "Registro con longitud 0");
            } else if (len > c10.length) {
				LOGGER.log(Level.SEVERE, "Registro " + new String(c1).trim() 
				        + " con longitud len > c10.length: " +  len);
                len = c10.length;
                numLongRecords++;
            } 
            fis.read(c10, 0, len);
            fos.write(c10, 0, len);
            fis.skip(c10.length - len);
            fos.write(CRLF);
        }

		LOGGER.log(Level.SEVERE, "Total de incidencias: " +  numRecords);
		LOGGER.log(Level.SEVERE, "Total de incidencias según Sopra: " 
		        +  numRecordsSegúnSopra);
		LOGGER.log(Level.SEVERE, "Total de incidencias largas: " 
		        +  numLongRecords);

        fos.close();
        fis.close();
    }

    public static void main(final String[] args) throws IOException {
        new LectorRemedy();
    }
}
