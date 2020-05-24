package misc.terasystems;
/*
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
*/
class DataServer {
	private static final int SERVER_PORT = 5555;

	public static void main(final String[] args) {
		ServerSocket serverSocket = null;
		final boolean listening = true;

		System.out.println("Iniciando el servidor. Escuchando en el puerto: " + SERVER_PORT);
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (final IOException e) {
            System.err.println("Could not listen on port: " + SERVER_PORT + ", " + e.getMessage());
            System.exit(1);
        }

        while (listening) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (final IOException e) {
                System.err.println("Accept failed: " + SERVER_PORT + ", " + e.getMessage());
                continue;
            }
//System.err.println("Iniciando nuevo thread");
            new DataServerThread(clientSocket).start();
        }

//        try {
//            serverSocket.close();
//        } catch (final IOException e) {
//            System.err.println("Could not close server socket." + e.getMessage());
//        }
    }
}


/*
  2013 07 19. Esto estaba descomentado. Como no se si lo que realmente valía
  era esto o la clase DataServerThread que está en un .java aparte, la dejo
  aquí en comentario hasta saber qué pasó en esos días de 1998 :)
class DataServerThread extends Thread {
    Socket socket = null;

    DataServerThread(Socket socket) {
        super("KKMultiServerThread");
        this.socket = socket;
    }

	public void run() {
	try {
		DataInputStream is = new DataInputStream(
                                  new BufferedInputStream(socket.getInputStream()));
        PrintStream os = new PrintStream(new BufferedOutputStream(socket.getOutputStream(), 1024), false);
		String inputLine, outputLine;

		String usuario = "x";
		while ((inputLine = is.readLine()) != null) {
			System.out.println("Recibi: " + inputLine);
			if(inputLine.equalsIgnoreCase("AUDIT 0")) {
				os.println("Bienvenido");
			}
			else if(inputLine.equalsIgnoreCase("Adios")) {
				os.println("Chao pescao");
			}
			else if(inputLine.startsWith("USER")) {
				usuario = inputLine.substring(5);
				if(!usuario.startsWith("%pepito") &&
					!usuario.startsWith("%luisito") &&
					!usuario.startsWith("%jaimito")) {
					os.println("NAK User unknown:" + usuario);
				}
				else {
					String linea;
					DataInputStream dis = new DataInputStream(new FileInputStream("trucks.txt"));
					os.println("OK");
					while((linea = dis.readLine()) != null) {
						os.print(" " + linea);
					}
					os.println();
					dis.close();
				}
			}
			else if(inputLine.startsWith("REPO")) {
				os.println("ReportName");
				os.println(inputLine);
				for(int i = 0; i < 20; i++)
					os.println("Linea del reporte numero " + i);
				os.println("EOT");  // fin de tranmision: cambio y fuera
			}
			else if(inputLine.startsWith("MAPA") ||
					inputLine.startsWith("ZOOM") ||
					inputLine.startsWith("N ") ||
					inputLine.startsWith("NW ") ||
					inputLine.startsWith("W ") ||
					inputLine.startsWith("SO ") ||
					inputLine.startsWith("S ") ||
					inputLine.startsWith("SE ") ||
					inputLine.startsWith("E ") ||
					inputLine.startsWith("NE ")) {
				os.println("miami.gif coordX coordY");
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
//				break;
			}
			else {
				os.println("NAK: Protocolo invalido");
//break;
			}
			os.flush();
		}
		os.close();
		is.close();
		socket.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
}
 */
