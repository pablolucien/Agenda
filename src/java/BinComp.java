// ******************************** package

// ******************************** imports
import org.pclg.tools.Chrono;
import org.pclg.tools.FileTools;
import org.pclg.tools.ToolBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
	BinComp <BR>
	Reemplazo del estúpido 'comp' de DOS
	@author El Coyote Cojo
	@version 2003.ene.11 20:01:30, CET
*/
public class BinComp {
	/**
		Constructor por omision
		--@author El Coyote Cojo
		-version 2003.ene.11 20:01:30, CET
	*/
	private BinComp(final File f1, final File f2) {
		report("<<" + f1 + ">> vs. <<" + f2 + ">>");
		try {
			if(compare(f1, f2)) {
				report("\tson iguales");
			}
			else {
				report("\tson diferentes");
				report("\tConvirtiendo el segundo");
				convert(new File("2"), new File("3"), 1);
			}
		}
		catch(final IOException ex) {
			ToolBox.showInfo(ex);
		}
	}

	// ******************************** Metodos de instancia
	/**
		Compara dos archivos y genera un archivo con las diferencias
	*/
	boolean compare(final File f1, final File f2) throws IOException {
		boolean areEqual = true;
		final int cronHandle = Chrono.getChrono();
		Chrono.start(cronHandle);
		final byte[] b1 = FileTools.readFromFile(f1.getName());
		Chrono.mark(cronHandle);
		report("Lectura del primer  archivo " + Chrono.timeDetail(
							Chrono.elapsed(cronHandle)));

		Chrono.start(cronHandle);
		final byte[] b2 = FileTools.readFromFile(f2.getName());
		Chrono.mark(cronHandle);
		report("Lectura del segundo archivo " + Chrono.timeDetail(
							Chrono.elapsed(cronHandle)));

		final File diffFile = new File("diferencias.txt");
		final DataOutputStream dos = new DataOutputStream(new FileOutputStream(diffFile));
		try {
			int length = b1.length;
			if(b1.length != b2.length) {
				length = Math.min(b1.length, b2.length);
				areEqual = false;
				report("\tTienen tamaños distintos. Comparando los primeros " + length + " bytes");
				dos.writeInt(-1);		// En la esperanza de que de una excepcion si se trata de convertir archivos uasndo estas diferencias
			}

			int nrDiff = 0;
			Chrono.start(cronHandle);
			//for(int ii = 0; ii < length; ii++) {
			try {
				int ii = -1;
				while(true) {
				ii++;
				if(b1[ii] != b2[ii]) {
					areEqual = false;
					nrDiff++;
					report("\t" + ii + ": " + b1[ii] + " != " + b2[ii] + " -> " + (char)b1[ii] + " != " + (char)b2[ii]);
					dos.writeInt(ii);			// Posicion de la diferencia
					dos.writeByte(b1[ii]);	// Primer valor
					dos.writeByte(b2[ii]);	// Segundo valor
				}
				}
			}
			catch(final ArrayIndexOutOfBoundsException ex) {
			}
			//}
			Chrono.mark(cronHandle);
			report("Comparacion " + Chrono.timeDetail(Chrono.elapsed(cronHandle))
					+ " diferencias: " + nrDiff);
		}
		finally {
			dos.close();
			if(areEqual) {
				diffFile.delete();
			}
		}

		return(areEqual);
	}

	/**
		Conviere un archivo a la primera o segunda version segun el archivo con las diferencias
	*/
	void convert(final File f1, final File f2, final int version) throws IOException {
		final int cron = Chrono.getChrono();

		Chrono.start(cron);
		final byte[] b1 = FileTools.readFromFile(f1.getName());
		Chrono.mark(cron);
		report("Lectura del archivo: " + Chrono.timeDetail(Chrono.elapsed(cron)));

		final File diffFile = new File("diferencias.txt");
		final DataInputStream dis = new DataInputStream(new FileInputStream(diffFile));
		try {
			Chrono.start(cron);
			final int length = (int) diffFile.length() / 6;		// sizeOf(int) + sizeOf(byte) * 2
			for(int ii = 0; ii < length; ii++) {
				final int pos = dis.readInt();				// Posicion de la diferencia
				final byte val1 = dis.readByte();				// Primer valor
				final byte val2 = dis.readByte();				// Segundo valor
				if(version == 1) {
					b1[pos] = val1;
				}
				else {
					b1[pos] = val2;
				}
			}
			Chrono.mark(cron);
			report("Conversion: " + Chrono.timeDetail(Chrono.elapsed(cron)));

			Chrono.start(cron);
			FileTools.writeToFile(b1, f2.getName(), false);
			Chrono.mark(cron);
			report("Escritura del archivo: " + Chrono.timeDetail(Chrono.elapsed(cron)));
		}
		finally {
			dis.close();
		}
	}

	private void report(final String s) {
		System.out.println(s);
	}


	// ******************************** Metodos estaticos

	/**
		Ayuda al usuario
		--@author El Coyote Cojo
		-version 2003.ene.11 20:01:30, CET
	*/
	public static void usage(final String[] args) {
		System.err.println("Usage: java BinComp " +  "");
		System.exit(1);
	}

	/**
		Ejecuta la aplicación
		--@author El Coyote Cojo
		-version 2003.ene.11 20:01:30, CET
	*/
	public static void main(final String[] args) {
		new BinComp(new File("1"), new File("2"));
	}
}
