// ******************************** package
package tests;

// ******************************** imports
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
	zip <BR>
	@author El Coyote Cojo
	@version 2002.sep.10 17:35:28, CEST
*/
public class zip {
	// ******************************** Variables de clase

	// ******************************** Variables de instancia

	// ******************************** Constructores

	/**
		Constructor por omision
		@author El Coyote Cojo
		@version 2002.sep.10 17:35:28, CEST
	*/
	private zip() throws Exception {
		final byte[] buf = new byte[1024];
		int len;

		//new Exception("Stack trace").printStackTrace();

		final ZipFile file = new ZipFile("noemi.zip");
		System.out.println(file.getName());
		final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream("noemi2.zip"));
		final String inFilename = "zip.java";
		for(int i = 0; i < 7; i++) {
			final FileInputStream in1 = new FileInputStream(inFilename);
			zos.putNextEntry(new ZipEntry("pepito/luisito/dir" + i + "/" + inFilename + "_" + i));
			while((len = in1.read(buf)) > 0) {
				zos.write(buf, 0, len);
			}
			zos.closeEntry();
			in1.close();
		}
		zos.close();

/*
		ZipFile file = new ZipFile("/zips/eclipse-SDK-2.0-win32.zip");
		Enumeration contenido = file.entries();
		while(contenido.hasMoreElements()) {
			System.out.println(((ZipEntry) contenido.nextElement()).getName());
		}
*/
	}

	// ******************************** Metodos de instancia


	// ******************************** Metodos estaticos

	/**
		Ejecuta la aplicación
		@author El Coyote Cojo
		@version 2002.sep.10 17:35:28, CEST
	*/
	public static void main(final String[] args) throws Exception {
		new zip();
	}
}
