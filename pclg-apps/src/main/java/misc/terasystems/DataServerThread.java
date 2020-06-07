package misc.terasystems;
/*
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

class DataServerThread extends Thread {
    private Socket socket;

    DataServerThread(final Socket socket) {
        super("KKMultiServerThread");
        this.socket = socket;
    }

	public void run() {
	try {
		final BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		final PrintStream os = new PrintStream(
                              new BufferedOutputStream(socket.getOutputStream(), 1024), false);
		String inputLine;
		String outputLine;

		String usuario = "x";
		while ((inputLine = is.readLine()) != null) {
			System.out.println("Recibi: " + inputLine);
			if(inputLine.equalsIgnoreCase("Hola")) {
				os.println("Bienvenido");
			}
			else if(inputLine.equalsIgnoreCase("Adios")) {
				os.println("Chao pescao");
			}
			else if(inputLine.startsWith("USER")) {
				usuario = inputLine.substring(5);
			}
			else if(inputLine.startsWith("PASS")) {
				if(!usuario.startsWith("pepito")) {
					os.println("NAK User unknown:" + usuario);
				}
				else {
					String linea;
					final BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream("trucks.txt")));
					os.println("OK");
					while((linea = dis.readLine()) != null) {
						os.print(" " + linea);
					}
					os.println();
					dis.close();
//					os.println("EOT");  // fin de tranmision: cambio y fuera
				}
			}
			else if(inputLine.startsWith("REPO")) {
				os.println("El reporte se llama pepito");
				os.println(inputLine);
				for(int i = 0; i < 20; i++)
					os.println("Linea del reporte numero " + i);
				os.println("EOT");  // fin de tranmision: cambio y fuera
			}
			else if(inputLine.startsWith("MAPA")) {
				os.println("http://161.196.58.51/pablo/miami.gif");
				for(int i = 0; i < 5; i++)
					os.println("Linea de la direccion numero " + i);
				os.println("EOT");  // fin de tranmision: cambio y fuera
			}
			else if(inputLine.startsWith("FAX")) {
				os.println("OK");
			}
			else if(inputLine.startsWith("MAIL")) {
				os.println("OK");
			}
			else if(inputLine.startsWith("EMAIL")) {
				os.println("OK");
			}
			else if(inputLine.startsWith("LOGOFF")) {
				os.println("OK");
			}
			os.flush();
		}
		os.close();
		is.close();
		socket.close();
	} catch (final IOException e) {
		e.printStackTrace();
	}
	}
}
