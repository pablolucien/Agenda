package tests;
import javax.swing.JFrame;
import java.net.ServerSocket;
import java.net.Socket;

/**
	OneInstance
	prueba de una aplicacion que solo se puede ejecutar una vez en una máquina
*/
public class OneInstance extends JFrame implements Runnable {
	private static final int PORT = 9399;	// ??? sacarlo de un fichero de configuracion
    private static final long serialVersionUID = -2989298606142037816L;

    private OneInstance() {
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		testForAnotherInstance();
		new Thread(this).start();
		// Si llego hasta aqui es que todo va bien. Seguir con el proceso normal
		pack();
		setVisible(true);
	}

	private void testForAnotherInstance() {
		try {
			final Socket s = new Socket("localhost", PORT);
			System.err.println("Hay otro '" + getClass().getName() + "' en ejecución. Voy a despertarlo");
			s.close();
			System.exit(0);
		}
		catch(final Exception ex) {
			System.err.println("No hay otro '" + getClass().getName() + "' en ejecución. Voy a iniciarme yo");
		}
	}

	// implements Runnable
	@Override
	public void run() {
		try {
			final ServerSocket s = new ServerSocket(PORT);
			while(true) {
				s.accept();
				setVisible(true);
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void usage() {
		System.out.println("Usage: java OneInstance " +  "");
		System.exit(0);
	}
	
	public static void main(final String [] args) {
		new OneInstance();
	}
}
