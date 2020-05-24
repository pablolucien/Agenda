package misc.terasystems;
/**
	Trucks.java
*/

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 */

@SuppressWarnings("deprecation")
public class Trucks extends HttpServlet {
    private static final long serialVersionUID = -2290101898306619940L;
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private String dataHost;	// El host que nos da los datos ...
	private int dataPort;		// ... y el puerto donde nos vamos a conectar
	private Socket socket;
	private PrintStream serverOs;
	private DataInputStream serverIs;
	private static PrintWriter toClient;	// Donde escribimos nuestras paginas html
	private boolean connected;
	private static String userid = "Usuario desconocido";
    
	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		String resultsDir = getInitParameter("resultsDir");
		if (resultsDir == null) {
			final Enumeration initParams = getInitParameterNames();
			LOGGER.debug("The init parameters were: ");
			while (initParams.hasMoreElements()) {
				LOGGER.debug(initParams.nextElement());
			}
			LOGGER.debug("Should have seen one parameter name");
			throw new UnavailableException("Not given a directory to write survey results!");
		}
		// Iniciamos la comunicacion con Manolo
		dataHost = getInitParameter("dataHost");
		dataPort = Integer.parseInt(getInitParameter("dataPort"));
		LOGGER.debug("Conectandome con MM en el host:" + dataHost + " puerto:" + dataPort);
		try {
			socket = new Socket(dataHost, dataPort);
			serverOs = new PrintStream(socket.getOutputStream());
			serverIs = new DataInputStream(socket.getInputStream());
			sendMsg("Hola");
			if(!rcvMsg().equals("Bienvenido"))
				LOGGER.debug("Me conecte con un extranho");
		} catch (final UnknownHostException e) {
			LOGGER.debug("Don't know about host: " + dataHost);
			return;
		} catch (final IOException e) {
			LOGGER.debug("Couldn't get I/O for the connection to: " + dataHost);
			return;
		} catch (final Exception e) {
			LOGGER.debug("Excepcion: " + e);
			return;
		}
		connected = true;
		LOGGER.debug("Conectado");
	}

	public void destroy() {
		// Cerramos la comunicacion con Manolo
		LOGGER.debug("Cerrando la comunicacion con el host:" + dataHost + " puerto:" + dataPort);
		try {
			sendMsg("Adios");
			serverOs.close();
			serverIs.close();
			socket.close();
		} catch (final Exception e) {
			LOGGER.debug("Exception:  " + e);
		}
		connected = false;
	}


	// Envio de mensajes al server
	private void sendMsg(final String msg) {
		LOGGER.debug("Enviando:  " + msg);
		serverOs.println(msg);
		serverOs.flush();
	}


	// Recibe mensajes del server
	private String rcvMsg() {
		String msg;
		try {
			msg = serverIs.readLine();
		}
		catch(final IOException ex) {
			ex.printStackTrace();
			msg = ex.toString();
		}
		LOGGER.debug("Recibi:  " + msg);
		return(msg);
	}

	private void error(final String msg) {
		startPage("Error");
		toClient.println("<H1>Error</H1>");
		toClient.println("<br> " + "A problem has occurred: " + msg);
		endPage();
	}

	private void confirmacion() {
		startPage("Confirmation");
		toClient.println("<H1>Confirmation</H1>");
		toClient.println("<br> " + "Your request is been processed");
		toClient.println("<br> " + "Thank you!");
		endPage();
	}

	/**
	*/
	public void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
		// first, set the "content type" header of the response
		res.setContentType("text/html");
		//Get the response's PrintWriter to return text to the client.
		toClient = res.getWriter();

		if(!connected) {
			return;
		}
		
		// Mantenemos el control de quien nos esta llamando.
		// deberia ser un hash oculto
		if(req.getParameterValues("step")[0].equals("1")) {	// paso 1
			userid = req.getParameterValues("username")[0];
			final String password = req.getParameterValues("password")[0];

			// Si tenemos user y password se los pasamos a Manolo
			if(userid != null) {
				sendMsg("USER " + userid);
				sendMsg("PASS " + password);
				final String answer = rcvMsg();
				if(!answer.equals("OK")) {
					error(answer.substring(answer.indexOf(' ')));
					return;
				}
			}
		}

		final Enumeration values = req.getParameterNames();
		while(values.hasMoreElements()) {
			final String name = (String)values.nextElement();
			final String [] value = req.getParameterValues(name);

			// Guardamos los camiones que le interesan al tipo
			final String [] camiones = req.getParameterValues("ide");

/*	revisar este chequeo
			if(camiones.length == 0)
				return;
*/
			// Chequeamos las opciones seleccionadas por el usuario
			if(name.equals("option") && value[0].equals("1")) {
				report("REPO1", camiones);
				return;
			}
			if(name.equals("option") && value[0].equals("2")) {
				report("REPO2", camiones);
				return;
			}
			if(name.equals("option") && value[0].equals("3")) {
				if(camiones.length != 1)
					showError();
				else
					showMap(camiones[0], 0);
				return;
			}

			if(name.equals("fax")) {
				sendMsg("FAX " + userid + " " + req.getParameterValues("fax_nr")[0]);
				if(rcvMsg().equals("OK"))
					confirmacion();
				else
					error("");
				return;
			}
			if(name.equals("email")) {
				sendMsg("EMAIL " + userid + " " + req.getParameterValues("e-mail")[0]);
				if(rcvMsg().equals("OK"))
					confirmacion();
				else
					error("");
				return;
			}
			if(name.equals("smail")) {
				sendMsg("MAIL " + userid);
				if(rcvMsg().equals("OK"))
					confirmacion();
				else
					error("");
				return;
			}
		}

		selectionForm();
		// Close the writer; the response is done.
		toClient.close();
	}


	// Forma inicial para seleccionar las acciones
	private void selectionForm() {
		startPage("Your trucks");
		toClient.println("<H1>These are your trucks</H1>");
		toClient.println("<br>");
		try {
			final String linea;
			linea = rcvMsg();
			final StringTokenizer st = new StringTokenizer(linea);
			while(st.hasMoreElements()) {
				final String item = st.nextToken();
				toClient.println("<BR><input type=checkbox name=ide value=" + item + ">" + item);
			}
			toClient.println("<BR><input type=checkbox name=ide value=all>All of them");
			toClient.println("<BR>");
			toClient.println("<BR> Your options are:");
			toClient.println("<BR><input type=radio name=option value=1>Current location");
			toClient.println("<BR><input type=radio name=option value=2>Last 20 locations");
			toClient.println("<BR><input type=radio name=option value=3>Map");
			toClient.println("<BR>or...:");
			toClient.println("<BR><img src=\"http://161.196.58.51/pablo/Beer.gif\">");
			toClient.println("<BR><BR><input type=submit><input type=reset>");
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			toClient.println(ex);
		}
		endPage();
	}


	/**
	 * Reporte de posiciones
	 */
	private void report(final String tipo, final String[] target) {
		String msg = tipo + " " + userid;
		for(int i = 0; i < target.length; i++) {
			msg = msg + " " + target[i];
		}	
		sendMsg(msg);
		final String reportName = rcvMsg();	// ponerlo como campo oculto en la pagina
		startPage("Your truck is here");
		toClient.println("<H1>Your truck is here</H1>");
		String line;
		while(!(line = rcvMsg()).equals("EOT")) {
			toClient.println("<br> " + line);
		}	
		deliveryInfo();
		endPage();
	}


	/** Mensaje de error para la peticion de mapas */
	private void showError() {
		startPage("Your map");
		final String errMsg = "You should select one (and only one) truck for your request to be processed.";
		toClient.println(errMsg);
		endPage();
	}

	/** Muestra un mapa. */
	private void showMap(final String camion, final int zoom) {
		sendMsg("MAPA1 " + userid + " " + camion + " " + zoom);
		startPage("Your map");
		toClient.println("<H1>Here is the map you requested</H1>");
		toClient.println("<center>");
		toClient.println("<table border=1>");
		toClient.println("<tr>");
		toClient.println("<td>");
		toClient.println("<BR><img src=\"" + rcvMsg() + "\">");
		toClient.println("</td>");
		toClient.println("</tr>");
		toClient.println("</table>");
		String address;
		while(!(address = rcvMsg()).equals("EOT")) {
			toClient.println("<br> " + address);
		}	
		toClient.println("<BR><input type=submit value=\"Zoom in\">");
		toClient.println("<input type=submit value=\"Zoom out\">");
		toClient.println("</center>");
		deliveryInfo();
		endPage();
	}

	/** Encabezado de las paginas. */
	private void startPage(final String title) {
		toClient.println("<html>");
		toClient.println("<head>");
		toClient.println("<title>" + title + "</title>");
		toClient.println("</head>");
		toClient.println("<body background=http://161.196.58.51/pablo/teralogo.gif>");
		toClient.println("<form action=http://161.196.58.51:8080/servlet/trucks method=POST>");
		toClient.println("<input type=hidden name=step value=2>");
		toClient.println("<input type=hidden name=userid value=userid>");
		toClient.println("<input type=hidden name=_fax_nr value=fax_nr>");
		toClient.println("<input type=hidden name=_e-mail value=e-mail>");
	}

	/**
	 * Footer de las paginas
 	 */
	private void endPage() {
		toClient.println("</form>");
		toClient.println("<HR>");
		toClient.println("<BR>Copyright, mail, etc.");
		toClient.println("</html>");
	}

	/**
	 * Informacion de a donde se envia el reporte
	 */
	private void deliveryInfo() {
		toClient.println("<HR>");
		toClient.println("<BR><BR><input type=submit name=fax value=\"Send to fax   \">");
		toClient.println("<input type=text name=fax_nr value=1234>");
		toClient.println("<BR><input type=submit name=email value=\"Send to e-mail\">");
		toClient.println("<input type=text name=e-mail value=abc@xyz.com>");
		toClient.println("<BR><input type=submit name=smail value=\"Send to mail  \">");
	}
}
