package net.asintec;

// ******************************** imports
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
	net.asintec.MrSpam <BR>
	Envia emails en forma masiva. Al ejecutarlo se debe indicar la ruta de un directorio,
	el programa revisa periodicamente este directorio y si existen ficheros llamados
	'direcciones.txt' y 'contenido.txt', envia el contenido de este ultimo (y valga la redundancia)
	a todas las direcciones que haya en el primero, que se suponen una por linea. Al finalizar, mueve
	los ficheros a un subdirectorio llamado 'backup' con el nombre cambiado para que refleja la
	fecha en que se hizo la operacion.
	@author El Coyote Cojo
	@version 2002.abr.30 16:15:31, CEST
*/
public class MrSpam {
	// ******************************** Variables de clase
	/** El tiempo de espera para revisar a ver si hay trabajo por hacer */
	private static final int TIME_TO_SLEEP = 5 * 60 * 1000;

	// ******************************** Variables de instancia
	/** directorio de trabajo */
	private final String baseDir;

	/** Una direccion de correo a donde enviar mensajes administrativos */
	private final String adminAddress = "pablolucien@asintec.net";

	// ******************************** Constructores

	/**
		Construye un net.asintec.MrSpam
		@param baseDir El directorio de trabajo
		author El Coyote Cojo
		version 2002.abr.30 16:15:31, CEST
	*/
	private MrSpam(final String baseDir) {
		this.baseDir = baseDir;
		// Enviamos un mensaje informando del inicio del trabajo
		try {
			sendMessage(adminAddress, "Iniciando la ejecucion de net.asintec.MrSpam".getBytes());
		}
		catch(final IOException ex) {
			ex.printStackTrace();
		}
		// Entramos en el ciclo de trabajo
		doTheWork();
	}

	// ******************************** Metodos de instancia

	/**
		Envia los correos
		author El Coyote Cojo
		version 2002.abr.30 16:15:31, CEST
	*/
	private void doTheWork() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("_yyyy_MM_dd_HH_mm");
		while(true) {
			//System.err.println("Revisando en [" + baseDir + "] " + new Date());
			try {
				final File inFile = new File(baseDir, "direcciones.txt");
				final File msgFile = new File(baseDir, "contenido.txt");
				if(inFile.exists() && msgFile.exists()) {
					// Leemos el mensaje en un arreglo de bytes
					final byte[] msgData = new byte[(int) msgFile.length()];
					final FileInputStream fis = new FileInputStream(msgFile);
					fis.read(msgData);
					fis.close();

					// Enviamos el mensaje a mi para probar
					sendMessage(adminAddress, msgData);

					// Enviamos el mensaje a sus destinatarios
					String address;
					final BufferedReader in = new BufferedReader(new FileReader(inFile));
					int count = 0;
					while((address = in.readLine()) != null) {
						sendMessage(address, msgData);
						count++;
					}
					in.close();

					// Respaldamos los archivos
					final Date now = new Date();
					final String dateInfo = dateFormat.format(now);
					final File backInFile = new File(baseDir + File.separator + "backup",  "direcciones" + dateInfo + ".txt");
					final File backMsgFile = new File(baseDir + File.separator + "backup", "contenido" + dateInfo + ".txt");
					inFile.renameTo(backInFile);
					msgFile.renameTo(backMsgFile);

					// Informamos de los mensajes enviados para propositos de contabilidad
					System.err.println("Enviados [" + count + "] mensajes en" + now);
				}
			} catch(final IOException ex) {
				ex.printStackTrace();
			}
			//System.err.println("A dormir un rato (con perdon del ministro)");
			try { Thread.sleep(TIME_TO_SLEEP); }
			catch(final InterruptedException ex) {Thread.currentThread().interrupt();}
		}
	}


	/**
		Envia un email con el contenido 'msgData' a la direccion 'address'
		@param msgData El mensaje
		@param address La direccion de envio
		author El Coyote Cojo
		version 2002.may.08 13:30:00, CEST
	*/
	private void sendMessage(final String address, final byte[] msgData) throws IOException {
		System.err.println("Enviando a " + address);
		final Process proc = Runtime.getRuntime().exec("mail " + address);
		final OutputStream os = proc.getOutputStream();
		os.write(msgData);
		os.close();
	}


	// ******************************** Metodos estaticos

	/**
		Ayuda al usuario
		author El Coyote Cojo
		version 2002.abr.30 16:15:31, CEST
	*/
	private static void usage(final String[] args) {
		System.err.println("Usage: java net.asintec.MrSpam " +  "<directorio base>");
		System.exit(1);
	}

	/**
		Ejecuta la aplicación
		author El Coyote Cojo
		version 2002.abr.30 16:15:31, CEST
	*/
	public static void main(final String [] args) {
		if(args.length != 1) {
			usage(args);
		}
		new MrSpam(args[0]);
	}
}
