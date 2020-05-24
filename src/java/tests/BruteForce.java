package tests;

import org.pclg.tools.Chrono;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * tests.BruteForce
 *
 * @author El Coyote Cojo
 * @version 2001.jun.18 11:02:07, CEST
 */
public final class BruteForce {
	// Variables de clase
	//public static char [] src = { 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final char [] src = {'i', 'e', 's', 'n', '4', '3'};
/*
	public static char [] src = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
										  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
										  'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
										  'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
										  'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
										  'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
										  'y', 'z'
										 };
*/

	// Variables de instancia

	// Constructores

	/**
	 * Constructor por omision
	 */
	private BruteForce() {
	}

	// Metodos estaticos

	/**
	 * Ayuda al usuario
	 */
	public static void usage() {
		System.out.println("Usage: java tests.BruteForce " + "");
		System.exit(0);
	}

	/**
	 * Ejecuta la aplicación
	 */
	public static void main(
			final String [] args) /*throws IOException, SQLException*/ {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Finalizando");
			}
		});


		String connectString;
		final String DSN = "d:/sistema.mdb";
		//String DSN = "kkk";
		Connection srcConnection = null;
		//Driver theDriver = new sun.jdbc.odbc.JdbcOdbcDriver();
		final int cronHandle = Chrono.getChrono();
		Chrono.start(cronHandle);

		// Cargar el driver JDBC-ODBC
		// ???? no hace falta.   DriverManager.registerDriver(theDriver);

		//DriverManager.setLogWriter(new PrintWriter(new FileOutputStream("tests.BruteForce.log")));


		char [] dst;
		int [] curr;
		String pwd;

/*	
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) System.out.println(" Hay un SecurityManager");
System.exit(0);
	System.setSecurityManager(new SecurityManager() {
			public void checkExit(int status) {
				throw new SecurityException();
			}
		}
	);
*/
		for (int len = 6; len <= src.length; len++) {
			final int total = (int) Math.pow(src.length, len);
			System.out.println("Longitud: " + len + " de: " + src.length + " ("
					+ total + " posibilidades)");

			dst = new char[len];
			curr = new int [len];
			for (int i = 0; i < curr.length; i++) {
				curr[i] = -1;
			}

			for (int j = 0; j < total; j++) {
				for (int i = 0; i < curr.length; i++) {
					curr[i]++;
					curr[i] %= src.length;
					if (curr[i] != 0) {
						break;
					}
				}

				for (int i = 0; i < curr.length; i++) {
					dst[i] = src[curr[i]];
				}
				pwd = new String(dst);
				if (j % 5000 == 0) {
					System.out.println(pwd + " " + j);
				}

//if(j < 5000) continue;				
				try {
					//System.out.println("" + j + " " + pwd);
					connectString =
							"jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ="
									+ DSN + ";PWD=" + pwd;

					srcConnection = DriverManager.getConnection(connectString);
					Chrono.mark(cronHandle);
					System.out.println("Listo: " + Chrono.timeDetail(
							Chrono.elapsed(cronHandle)));
					System.out.println(
							"Encontrado <" + pwd + "> en el intento " + j);
					srcConnection.close();
					System.exit(0);
				}
				catch (final SQLException ex) {
					if (ex.getErrorCode() == -1905) {  // ??? Codigo de Access
						//System.out.println("Contraseña erronea");
					} else {
						System.out.println(
								"ex.getErrorCode(): " + ex.getErrorCode());
						ex.printStackTrace();
					}
				}
			}
			System.out.println();
		}
		System.out.println("Adios pampa mia");
	}
}
