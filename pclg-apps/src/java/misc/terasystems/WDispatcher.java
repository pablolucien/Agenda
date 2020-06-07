package misc.terasystems;
//-Recuperacion automatica cuando Hector muere y renece (es posible?)
//-Document contains no data
//-Revisar lo de reabrir el socket (linea 167)

/**
	WDispatcher.java
*/
//package tera.dispatcher;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
	Clase central de la aplicacion WDispatcher.
	Recibe los requerimientos de tipo GET o POST y genera las pagina html
	como respuesta.
	Necesita estar conectado a un servidor de datos a traves de un socket
	Para su funcionamiento debe estar intalado un Web server que soporte el concepto de 'servlet',
	o en su defecto debe estar corriendo el programa servletrunner provisto en la distribucion del JSDK
	<pre>
	Necesita que esten definidos los siguientes parametros.
		auditStr:	El String que debe enviarse a server de datos al conectarse
		userTimeout:	El tiempo en minutos que un usuario puede estar inacactivo antes de ser desconectado automaticamente
		dataTimeout:	El tiempo en milisegundos que espero por respuestas de Hector
		debugLevel:	Nivel de verbosidad del programa: 0 o 1
		servletHost:	el web server donde vive el servlet ...
		servletPort:	... y el puerto que utiliza
		httpHost:	el web server que sirve las paginas ...
		httpPort:	... y el puerto que utiliza
	    httpDir:	... y el directorio base
		dataHost:	El host que contiene el servidor de datos
		dataPort:	... y el puerto que utiliza
		templatesDir:	El directorio de los templates
		welcomePage:	La pagina de acceso al sistema
	</pre>
*/
@SuppressWarnings("deprecation")
public class WDispatcher extends HttpServlet {
    private static final long serialVersionUID = 6888457522087419944L;
    private static int debugLevel;              // Cuan locuaces vamos a ser en los mensajes
    private static String servletHost;    // El host que tiene el servlet ...
	private static String servletPort;    // ... y el puerto donde nos vamos a conectar
    private static String httpBase;    // El Web server y el directorio
	private static String dataHost;    // El host que nos da los datos ...
	private static int dataPort;       // ... y el puerto donde nos vamos a conectar
    private static String templatesDir;  // Donde estan los templates
    private static String auditStr;     // String de validacion a enviar a Hector al conectarme
	private Socket socket;
	private String welcomeURL;
	private BufferedReader serverInputStream;
	private PrintWriter serverOutStream;
    private PrintWriter toClient;     // Donde escribimos nuestras paginas html
	private boolean connected;
	private Hashtable users;        // Mantenemos los usuarios que tienen sesion abierta
	private User currentUser;       //      El usuario con el que estamos trabajando
	private User unknownUser;       // Usuario desconocido
	private HttpSession session;    // Control de sesion
    private String sessionId = "login";
    private String currentYear;
    private String currentMonth;
    private String currentDay;
    private final String currentTime = "00:00";
	private int currZoomLevel;
	private Watchdog wdg;
	private boolean withTrackerApplet = true;	// Indica si vamos a usar Tracker o GIF


    void debug(final String s) {
		if(debugLevel > 0)
			System.out.println(s);
	}

	/**
		Obtiene los parametros de configuracion e inicia la comunicacion con el servidor de datos
		@param config Un objeto donde se almacena la informacion de configuracion del servlet
		@exception javax.servlet.ServletException Cuando no logra inicializarse
	*/
	public void init(final ServletConfig config) throws ServletException {
		final String httpHost;   // El host que nos da las paginas ...
		final String httpPort;   // ... y el puerto donde nos vamos a conectar
		final String httpDir;    // ... y el directorio de las paginas

		// Extractos de la documentacion
		//    The init method should save the ServletConfig object so that it
		//    can be returned by the getServletConfig method. If a fatal
		//    initialization error occurs, the init method should throw an
		//    appropriate "UnavailableException" exception.
		//    It should never call the method System.exit.
		super.init(config);

		try {
			// Obtenemos los parametros del servlet
			auditStr = getInitParameter("auditStr");
			servletHost = getInitParameter("servletHost");
			servletPort = getInitParameter("servletPort");
			httpHost = getInitParameter("httpHost");
			httpPort = getInitParameter("httpPort");
	    	httpDir  = getInitParameter("httpDir");
			httpBase = "http://" + httpHost + ":" + httpPort + "/" + httpDir + "/";
			dataHost = getInitParameter("dataHost");
			dataPort = Integer.parseInt(getInitParameter("dataPort"));
			templatesDir = getInitParameter("templatesDir");
			welcomeURL = httpBase + getInitParameter("welcomePage");
			debugLevel = Integer.parseInt(getInitParameter("debugLevel"));
			withTrackerApplet = Boolean.valueOf(getInitParameter("withTrackerApplet")).booleanValue();

			// Iniciamos la comunicacion con el server de datos
			connect(dataHost, dataPort);
			users = new Hashtable();
			unknownUser = new User("UNKNOWN", "UNKNOWN");   // Usuario desconocido

			wdg = new Watchdog(this, Integer.parseInt(getInitParameter("userTimeout")), users);
			wdg.start();

			System.err.println("Conectado");
		} catch(final UnknownHostException e) {
			throw new UnavailableException(this, "Don't know about host: " + dataHost);
		} catch(final IOException e) {
			throw new UnavailableException(this,"Couldn't get I/O for the connection to: " + dataHost);
		} catch(final Exception e) {
			throw new UnavailableException(this,"Excepcion: " + e);
		}
	}


	/**
		Limpieza final del ambiente
		Es llamado por el Web server cuando el servlet es descargado
	*/
	public void destroy() {
	// Cerramos la comunicacion con el server de datos
		System.err.println("Cerrando la comunicacion con el host:" + dataHost + " puerto:" + dataPort);
		try {
			sendMsg("Adios");
	    	serverOutStream.close();
	    	serverInputStream.close();
			socket.close();
		} catch(final Exception e) {
			System.err.println("Exception:  " + e);
		}
		connected = false;
	}


	/**
		Ofrece informacion sobre este 'servlet'
		@return La informacion sobre el servlet
	*/
	public String getServletInfo() {
    String infoString = "Servlet " + getClass().getName();
        infoString += "\nDesarrollada para Tera Systems por Pablo Lucien";
        infoString += "\nCopyright Tera Systems Inc., 1998";
		return(infoString);
	}


	/**
		Establece la conexion con el server de datos
		@param host El nombre o la direccion IP del server de datos
		@param port El puerto donde va a comunicarse
		@exception javax.servlet.UnavailableException Cuando no logra conectarse con el host de datos
		@exception IOException Cuando hay errores de IO
		@exception UnknownHostException Cuando no consigue el host de datos
	*/
	void connect(final String host, final int port) throws UnknownHostException, IOException, UnavailableException {
		connected = false;
		System.err.println("Conectandome con el host:" + dataHost + " puerto:" + dataPort);
		socket = new Socket(dataHost, dataPort);
		socket.setSoTimeout(Integer.parseInt(getInitParameter("dataTimeout")));	//???
		socket.setTcpNoDelay(true);		// ??? 
		serverOutStream = new PrintWriter(socket.getOutputStream());
		serverInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		sendMsg("AUDIT " + auditStr);
		final String tmpstr;
		if(!((tmpstr = rcvMsg()).equals("Bienvenido"))) {
			throw new UnavailableException(this, "Host: " + dataHost + " me respondio: '" + tmpstr + "'" +
						 "\n\tMe conecte con un extranho");
		}
		connected = true;
	}


    /**
		Realiza el envio de mensajes al server de datos
	*/
	void sendMsg(final String msg) {
		debug("Enviando:  " + msg);
		serverOutStream.println(msg);
		serverOutStream.flush();
		// ??? Estos metodos no lanzan excepciones. Hay que usar ...
		if(serverOutStream.checkError()) {
	    	debug("Error en sendMsg(). reabrir el socket.");
// destroy(); ?????? Causa el loop del que habla Hector, con mensajes generados por Watchdog
		}
	}


	// Recibe mensajes del server
	String rcvMsg() {
		String msg;
		try {
			msg = serverInputStream.readLine();
		}
		catch(final SocketException ex) {
			if(!ex.toString().equals("java.net.SocketException: Connection reset by peer")) {
				ex.printStackTrace();
			}
			msg = "EOT " + ex.toString();
			connected = false;
		}
		catch(final InterruptedIOException ex) {
			// Timeout ????
			msg = "EOT Timeout en rcvMsg()";
			connected = false;
		}
		catch(final IOException ex) {
			ex.printStackTrace();
			msg = "EOT " + ex.toString();
		}
		if(msg == null) {
	    	connected = false;
	    	debug("Recibi:  " + msg + " -- asumo que estoy desconectado");
			msg = "EOT Disconnected from Data Server";
		}
		else
	    	debug("Recibi:  " + msg);
		return(msg);
	}


    // Mensajes de error
	void error(final String msg) {
		startPage("Error");
		toClient.println("<H1>Error</H1>");
		toClient.println("<br><h2> " + "Problem: <b>" + msg + "</b></h2>");
		endPage();
	}


    // Mensajes de confirmacion
	void confirmacion() {
		startPage("Confirmation");
		toClient.println("<H1>Confirmation</H1>");
		toClient.println("<br>Your request is being processed");
		toClient.println("<br>Thank you!");
		endPage();
	}


	/**
	Scheduler de la aplicacion (Metodo GET): recibe los
	requerimientos y los despacha
	Aqui manejamos zoom y movimiento en los mapas
	@exception javax.servlet.ServletException cuando no logra
	*/
	public void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
	// Si no tenemos conexion con Tera no hay nada que hacer
		if(!connected) {
			try {
				connect(dataHost, dataPort);
			}
			catch(final Exception ex) {
				debug("No pude conectarme");
				res.sendRedirect(welcomeURL);
				return;
			}
		}
		
		// ponemos el  "content type" header de la respuesta
		// (hay que hacerlo antes de acceder al PrintWriter)
		res.setContentType("text/html");

		// obtenemos el 'PrintWriter' de res para escribirle al cliente.
		// (deberia estar asosiado a la sesion)
		toClient = res.getWriter();
	
		// Lo primero es averiguar los datos de sesion que fue creada en Post
		session = req.getSession(false);
		if(session == null) {
			res.sendRedirect(welcomeURL);
			return;
		}
		sessionId = session.getId();
		if(currentUser == null) {
			res.sendRedirect(welcomeURL);
			return;
		}
		currentUser.userid = (String) session.getValue("user");
		currentUser = (User) users.get(currentUser.userid);
		if(currentUser == null) {
			res.sendRedirect(welcomeURL);
			return;
		}
		currentUser.lastAccess = new Date();

		final String zoom;
		if((zoom = req.getParameter("zoom")) != null) {
			zoomMap((String)session.getValue("camionActual"), Integer.parseInt(zoom));
			return;
		}

		final String moveTo;
		if((moveTo = req.getParameter("moveTo")) != null) {
			moveMap(moveTo, (String)session.getValue("camionActual"), currZoomLevel);
			return;
		}

		// Si no es ninguno de los anteriores, se jodio la pajarira
		res.sendRedirect(welcomeURL);
		session.invalidate();    // ??? ver como funciona esto
		session = null;    // ???
	}


	/**
		Scheduler de la aplicacion (Metodo POST): recibe los requerimientos y los despacha
		@exception javax.servlet.ServletException cuando no logra
	*/
	public void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
	String answer;   // Las respuestas de Hector
		String userid;

		// Si no tenemos conexion con Tera no hay nada que hacer
		if(!connected) {
			try {
				connect(dataHost, dataPort);
			}
			catch(final Exception ex) {
				debug("No pude conectarme");
				res.sendRedirect(welcomeURL);
				return;
			}
		}
		
		// Averiguamos la fecha actual (aunque de aqui para abajo falle, puede
		// servir para otros usuarios)
		final Date date = new Date();
		currentYear = "" + (1900 + date.getYear());
		currentMonth = "" + (date.getMonth() + 1);
		currentDay = "" + date.getDate();

		// Lo primero es averiguar los datos de sesion
		session = req.getSession(true);
		if(session == null) {
			throw new ServletException("Error tratando de crear una sesion");
		}
		sessionId = session.getId();

		// De aqui en adelante podemos usar session.putValue(), session.getValue(),
		// etc., para mantener el estado de la sesion

		// ponemos el  "content type" header de la respuesta
		// (hay que hacerlo antes de acceder al PrintWriter)
		res.setContentType("text/html");

		// obtenemos el 'PrintWriter' de res para escribirle al cliente.
		// (deberia estar asosiado a la sesion)
		toClient = res.getWriter();

		// Mantenemos el control de quien nos esta llamando.
		// deberia ser un hash oculto
		if(req.getParameter("session").equals("login")) {
	    	userid = "%" + req.getParameter("username") + "%";
			final String password = req.getParameter("password");

	    	// Si tenemos user y password se los pasamos a Hector
	    	if(userid != null) {    //???
				sendMsg("USER " + userid + " " + password);
				session.putValue("user", userid);
				answer = rcvMsg();
				if(answer == null) {
		    		error("Problemas de comunicacion");
					return;
				}
				if(!answer.equals("OK")) {
		    		session.invalidate();    // ??? ver como funciona esto
		    		session = null;    // ???
					currentUser = unknownUser;
		    		error(answer.substring(answer.indexOf(' ')));
					return;
				}
				currentUser = new User(session.getId(), userid);
				users.put(currentUser.userid, currentUser);
				final String linea;
				linea = rcvMsg();
				final String [] todosLosCamiones = new String[2];	// El segundo elemento, en blanco, impide que se
				todosLosCamiones[1] = "";					// llame a showMap con la lista entera
				todosLosCamiones[0] = new String(linea);
				session.putValue("listaDeCamiones", todosLosCamiones);
				selectionForm(linea);
			}
		}
		userid = (String) session.getValue("user");
		/*  ???*/
		if(userid == null) { // Tiene que hacer login para poder entrar
			session.invalidate();
			res.sendRedirect(welcomeURL);
			return;
		}
		/**/
		currentUser = (User) users.get(userid);
		if(currentUser == null) { // Tiene que hacer login para poder entrar
			session.invalidate();
			res.sendRedirect(welcomeURL);
			return;
		}
		currentUser.lastAccess = new Date();

		final Enumeration values = req.getParameterNames();
		while(values.hasMoreElements()) {
			final String name = (String)values.nextElement();
			final String [] value = req.getParameterValues(name);

			// Guardamos los camiones que le interesan al tipo
			String [] camiones = req.getParameterValues("ide");
			// Si el usuario marca 'All' van todos
			if(camiones != null) {
				for(int i = 0; i < camiones.length; i++) {
					if(camiones[i].equalsIgnoreCase("all")) {
						camiones = (String[])session.getValue("listaDeCamiones");
						break;
					}
				}
			}

			// Chequeamos las opciones seleccionadas por el usuario
	    	// Reportes
			if(name.equals("option") && value[0].equals("1")) {
				if(camiones != null && camiones.length > 0)
					report("REPO1", camiones, "");
				return;
			}
			if(name.equals("option") && value[0].equals("2")) {
				if(camiones != null && camiones.length > 0) {
					// Necesitamos las fechas y horas
					final String fechas = getDates(req);
					if(fechas != null)
						report("REPO2", camiones, fechas);
					else
			error("Invalid date. <br>Please check your input.");
				}
				return;
			}

	    	// Mapa
			if(name.equals("option") && value[0].equals("3")) {
				if(camiones != null) {
					if(camiones.length != 1)
			error("You should select one (and only one) vehicle for your request to be processed.");
					else {
						session.putValue("camionActual", camiones[0]);
						showMap(camiones[0], 0);
					}
				}
				return;
			}

			// Logoff
			if(name.equals("logoff")) {
				logoff(currentUser.userid);
				session.invalidate();
				session = null;
				res.sendRedirect(welcomeURL);
				currentUser = unknownUser;
				return;
			}

			// Fax
			if(name.equals("fax")) {
				currentUser.fax_nr = req.getParameter("fax_nr");
				sendMsg("FAX " + currentUser.userid + " " + req.getParameter("fax_nr") + " " + req.getParameter("report_id"));
				if((answer = rcvMsg()).equals("OK"))
					confirmacion();
				else
		    		error(answer);
				return;
			}

			// E-mail
			if(name.equals("email")) {
				currentUser.e_mail = req.getParameter("e_mail");
				sendMsg("EMAIL " + currentUser.userid + " " + req.getParameter("e_mail") + " " + req.getParameter("report_id"));
				if((answer = rcvMsg()).equals("OK"))
					confirmacion();
				else
		    		error(answer);
				return;
			}

			// Snail mail
			if(name.equals("smail")) {
				sendMsg("MAIL " + currentUser.userid + " " + req.getParameter("report_id"));
				if((answer = rcvMsg()).equals("OK"))
					confirmacion();
				else
		    		error(answer);
				return;
			}
		}

		// Close the writer; the response is done.
		toClient.close();
    } // doPost()


	// Retorna un objeto de clase Date dado un String con la fecha y otro con la hora
	Date string2Date(final String f, final String h) {
//              try {
			final Date d = new Date(Integer.parseInt(f.substring(0, 4)) - 1900, Integer.parseInt(f.substring(5, 7)), Integer.parseInt(f.substring(8, 10)), Integer.parseInt(h.substring(0, 2)), Integer.parseInt(h.substring(3, 5)));
//debug(d.toString());
			return(d);
//              }
/* ???
	catch(NumberFormatException ex) {
			ex.printStackTrace();
		}
		catch(StringIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
	return(null);
*/
	}


	String date2String(final Date d) {
		return("" +
		  	(1900 + d.getYear()) + "/" +
		     pad(d.getMonth()) + "/" +
		     pad(d.getDate())  + " " +
		     pad(d.getHours()) + ":" +
		     pad(d.getMinutes()));
	}


    String pad(final int i) {
		return(pad("" + i));
	}


	String pad(String s) {
		s = s.trim();
		while(s.length() < 2)
			s = "0" + s;
		return(s);
	}


	// Obtiene las fechas y hora para los reportes por rango y las valida; las devuelve como un String
	String getDates(final HttpServletRequest req) {
		final String a1 = req.getParameter("ano1");
		final String m1 = pad(req.getParameter("mes1"));
		final String d1 = pad(req.getParameter("dia1"));
		final String a2 = req.getParameter("ano2");
		final String m2 = pad(req.getParameter("mes2"));
		final String d2 = pad(req.getParameter("dia2"));

		try {
			if(Integer.parseInt(m1) > 12 || Integer.parseInt(d1) > 31)
				return(null);

			final String f1 = a1 + "/" + m1 + "/" + d1;
			final String h1 = pad(req.getParameter("hora1")) + ":" + pad(req.getParameter("min1"));

			if(Integer.parseInt(m2) > 12 || Integer.parseInt(d2) > 31)
				return(null);

			final String f2 = a2 + "/" + m2 + "/" + d2;
			final String h2 = pad(req.getParameter("hora2")) + ":" + pad(req.getParameter("min2"));

			final Date d3 = string2Date(f1, h1);
			final Date d4 = string2Date(f2, h2);

			if(d3 == null || d4 == null || d3.after(d4))
				return(null);

			return("%" + date2String(d3) + "% %" + date2String(d4) + "%");
		}
		catch(final NumberFormatException ex) {
			return(null);
		}
	}


    // Forma base para seleccionar las acciones a ralizar
	void selectionForm(final String linea) {
		startPage("Your vehicles");
		toClient.println("<H1>These are your vehicles</H1>");
		toClient.println("<hr>");
		final StringTokenizer st = new StringTokenizer(linea);

		while(st.hasMoreElements()) {
	    	final String item = st.nextToken();
	    	toClient.println("<BR><input type=checkbox name=ide value=" + item + ">" + item);
		}

		toClient.println("<BR><input type=checkbox name=ide value=all>All of them");
		toClient.println("<HR>");
		toClient.println("<BR><H2>Your options:</H2>");
		toClient.println("<BR><input type=radio name=option value=3>Map (only one vehicle)");
		toClient.println("<BR><input type=radio name=option value=1>Current location");
		toClient.println("<BR><input type=radio name=option value=2>Locations between 2 dates: ");
		toClient.println("<ul>");
		toClient.println("<li>");

		toClient.println("First date (YYYY/MM/DD) <input type=text name=ano1 size=4 maxlength=4 value=" + currentYear + ">");
		toClient.println("/");
		toClient.println("<input type=text name=mes1 size=2 maxlength=2 value=" + currentMonth + ">");
		toClient.println("/");
		toClient.println("<input type=text name=dia1 size=2 maxlength=2 value=" + currentDay + ">");
		toClient.println(" time (HH:MM) <input type=text name=hora1 size=2 maxlength=2 value=" + currentTime + ">");
		toClient.println(":");
		toClient.println("<input type=text name=min1 size=2 maxlength=2 value=" + currentTime + ">");
		toClient.println("</li>");
		toClient.println("<li>");

		toClient.println("Last  date (YYYY/MM/DD) <input type=text name=ano2 size=4 maxlength=4 value=" + currentYear + ">");
		toClient.println("/");
		toClient.println("<input type=text name=mes2 size=2 maxlength=2 value=" + currentMonth + ">");
		toClient.println("/");
		toClient.println("<input type=text name=dia2 size=2 maxlength=2 value=" + currentDay + ">");
		toClient.println(" time (HH:MM) <input type=text name=hora2 size=2 maxlength=2 value=" + currentTime + ">");
		toClient.println(":");
		toClient.println("<input type=text name=min2 size=2 maxlength=2 value=" + currentTime + ">");
   /*
		toClient.println("Final date (YYYY/MM/DD) <input type=text name=fecha2 size=10 maxlength=10 value=" + currentDate + ">");
		toClient.println(" time (HH:MM) <input type=text name=hora2 size=5 maxlength=5 value=" + currentTime + ">");
   */
		toClient.println("</li>");
		toClient.println("</ul>");
		toClient.println("<BR><BR><input type=submit><input type=reset>");
		endPage();
	}


	// Reporte de posiciones
	void report(final String tipo, final String[] target, final String fechas) {
		String msg = tipo + " " + currentUser.userid;
		if(tipo.equals("REPO2"))
			msg += " " + fechas;
		msg += " % ";
		for(int i = 0; i < target.length; i++) {
			msg += target[i] + " ";
		}       
		msg += "%";
		sendMsg(msg);
		final String reportName = rcvMsg();   // ??? ponerlo como campo oculto en la pagina
		startPage("Here are vehicle(s)");
		toClient.println("<H1>Here are your vehicle(s)</H1>");
		toClient.println("<input type=hidden name=report_id value=\"" + reportName + "\">");
		String line;
		while((line = rcvMsg()) != null && !line.startsWith("EOT")) {
			toClient.println("<br> " + line);
		}       
		deliveryInfo();
		endPage();
	}


    /**
		lee el template de la pagina del mapa y lo muestra con los agregados del programa
		@return true si logra realizar la operacion. false de lo contrario
	*/
	boolean readTemplate(final String mapName, final int zoom) {
		BufferedReader reader = null;
		try {
	    	reader = new BufferedReader(new FileReader(templatesDir + "mapa.html"));
			String line;
	    	while((line = reader.readLine()) != null) {
				// 'tags' del template
				if(line.equals("!!pclg-MapName")) {     // Nombre del archivo a mostrar
					if(withTrackerApplet) {
						// Haciendolo con un applet
		    			toClient.println("<applet code=Tracker.class codebase=http://vps1.ingedigit.com/pablo/ width=410 height=275> <param name=imagen value=" + mapName + "> You need a java enabled browser </applet>");
					}
					else {
		    			toClient.print("<img src=");
		    			toClient.print(httpBase);
						toClient.print(mapName);
		    			toClient.println(">");
					}
				}
				else if(line.equals("!!pclg-Bolitas")) {   // Donde van las bolas
					for(int i = -4; i < 5; i++) {
						if(i == zoom) {
			    			toClient.print("<img src=\"");
			    			toClient.print(httpBase);
							toClient.print("images/magenta.gif\"");
						}
						else {
							toClient.print("<A HREF=\"trucks?zoom=" + i + "\">");
			    			toClient.print("<img src=\"");
			    			toClient.print(httpBase);
							toClient.print("images/blue.gif\"");
						}
						toClient.println(" width=16 height=16 align=\"absmiddle\" BORDER=0></A>");
					}
				}
				else if(line.equals("!!pclg-URL")) {   // ??? Host y directorio base
		     		;
				}
				else    // Lo que diga el template
					toClient.println(line);
			}
		}
		catch(final IOException ex) {
			ex.printStackTrace();
			return(false);
		}
		finally {
			try {
				if(reader != null)
					reader.close();
			}
			catch(final IOException ex) {
				ex.printStackTrace();
			}
		}
/*
*/
		return(true);
	}


	/**
		Parsea el string que manda Hector, devuelve la direccion de la imagen
		y actualiza las coordenadas
	*/
	String parseMap(final String s) {
		if(s.startsWith("NAK"))
			return(s);
		currentUser.coordX = s.substring(s.indexOf(' '), s.lastIndexOf(' '));
		currentUser.coordY = s.substring(s.lastIndexOf(' '));
		return(s.substring(0, s.indexOf(' ')));
	}


	/**
		Mueve un mapa segun los puntos cardinales
	*/
	void moveMap(final String centro, final String camion, final int zoom) {
		final String mapName;
		sendMsg(centro + " " + currentUser.userid + " % " + camion + " % " + zoom + " " + currentUser.coordX + " " + currentUser.coordY);
		mapName = parseMap(rcvMsg());
		if(mapName.startsWith("NAK")) {
			error(mapName);
			return;
		}
		startPage("Your map");
		toClient.println("<H1>Here is the map you requested</H1>");
		toClient.println("<input type=hidden name=report_id value=" + mapName + ">");
		toClient.println("<center>");
		toClient.println("<table border=1>");
		toClient.println("<tr>");
		toClient.println("<td>");
		readTemplate(mapName, zoom);
		toClient.println("</td>");
		toClient.println("</tr>");
		toClient.println("</table>");
		String address;
		while(!(address = rcvMsg()).startsWith("EOT")) {
			toClient.println("<br> " + address);
		}       
		toClient.println("</center>");
		deliveryInfo();
		endPage();
	}


	/**
		"Zoomea" un mapa
	*/
	void zoomMap(final String camion, final int zoom) {
		currZoomLevel = zoom;
		final String mapName;
		sendMsg("ZOOM " + currentUser.userid + " % " + camion + " % " + zoom + " " + currentUser.coordX + " " + currentUser.coordY);
		mapName = parseMap(rcvMsg());
		if(mapName.startsWith("NAK")) {
			error(mapName);
			return;
		}
		startPage("Your map");
		toClient.println("<H1>Here is the map you requested</H1>");
		toClient.println("<input type=hidden name=report_id value=" + mapName + ">");
		toClient.println("<center>");
		toClient.println("<table border=1>");
		toClient.println("<tr>");
		toClient.println("<td>");
		readTemplate(mapName, zoom);
		toClient.println("</td>");
		toClient.println("</tr>");
		toClient.println("</table>");
		String address;
		while(!(address = rcvMsg()).startsWith("EOT")) {
			toClient.println("<br> " + address);
		}       
		toClient.println("</center>");
		deliveryInfo();
		endPage();
	}


	/**
		Muestra un mapa
	*/
	void showMap(final String camion, final int zoom) {
		currZoomLevel = zoom;
		final String mapName;
		sendMsg("MAPA1 " + currentUser.userid + " % " + camion + " % "/* + zoom*/);
		mapName = parseMap(rcvMsg());
		if(mapName.startsWith("NAK")) {
			error(mapName);
			return;
		}
		startPage("Your map");
		toClient.println("<H1>Here is the map you requested</H1>");
		toClient.println("<input type=hidden name=report_id value=" + mapName + ">");
		toClient.println("<center>");
		toClient.println("<table border=1>");
		toClient.println("<tr>");
		toClient.println("<td>");
		readTemplate(mapName, zoom);
		toClient.println("</td>");
		toClient.println("</tr>");
		toClient.println("</table>");
		String address;
		address = rcvMsg();
		while(address != null && !address.startsWith("EOT")) {
			toClient.println("<br> " + address);
			address = rcvMsg();
		}       
		toClient.println("</center>");
		deliveryInfo();
		endPage();
	}


    /**
		Encabezado de las paginas
	*/
	void startPage(final String title) {
		toClient.println("<html>");
		toClient.println("<head>");
		toClient.println("<title>" + title + "</title>");
		toClient.println("</head>");
		toClient.println("<body background=" + httpBase + "images/fund.gif>");
		toClient.println("<form action=http://" + servletHost + ":" + servletPort + "/servlet/trucks method=POST>");
		toClient.println("<input type=hidden name=session value=" + sessionId + ">");
		toClient.println("<input type=hidden name=userid value=" + currentUser.userid + ">");
	}
	
	// Footer de las paginas
	void endPage() {
		toClient.println("<br><center>");
		toClient.println("<input type=submit size=10 name=logoff value=\"Logoff\">");
		toClient.println("</center>");
		toClient.println("</form>");
		try {
	    	final BufferedReader reader = new BufferedReader(new FileReader(templatesDir + "footer.html"));
			String line;
	    	while((line = reader.readLine()) != null)
				toClient.println(line);
	    	reader.close();
		}
		catch(final IOException ex) {
			ex.printStackTrace();
		}
	}

	// Saca pa' fuera un usuario
	void logoff(final String user) {
		debug("Desconectando a " + user);
		sendMsg("LOGOFF " + user);
		rcvMsg();       // "OK"
		users.remove(user);     // Invalidar la sesion ???
	}

	// Informacion de a donde se envia el reporte
	void deliveryInfo() {
		toClient.println("<HR>");
		toClient.println("<table>");
		toClient.println("<tr>");
		toClient.println("<td>");
		toClient.println("<input type=submit size=10 name=fax value=\"Send to fax   \">");
		toClient.println("</td>");
		toClient.println("<td>");
		toClient.println("<input type=text name=fax_nr value=\"" + currentUser.fax_nr + "\">");
		toClient.println("</td>");
		toClient.println("<tr>");
		toClient.println("<td>");
		toClient.println("<input type=submit size=10 name=email value=\"Send to e_mail\">");
		toClient.println("</td>");
		toClient.println("<td>");
		toClient.println("<input type=text name=e_mail value=\"" + currentUser.e_mail + "\">");
		toClient.println("</td>");
		toClient.println("<tr>");
		toClient.println("<td>");
		toClient.println("<input type=submit size=10 name=smail value=\"Send to mail  \">");
		toClient.println("</td>");
		toClient.println("</tr>");
		toClient.println("</table>");
	}
}


/**     clase de ayuda para mantener las variables
		de estado de un usuario de una sesion
*/
class User {
    private final String sessionId;   // Identificador de la sesion del tipo (Clave)
	public String userid;
	public Date lastAccess;
	public String fax_nr;
	public String e_mail;
	public String [] camiones;
	public String coordX, coordY;

	public User(final String sessionId, final String id) {
		this.sessionId = sessionId;
		userid = id;
		lastAccess = new Date();
		fax_nr = "(555)987.6514";
		e_mail = id + "@xyz.com";
		coordX = "0.0";
		coordY = "0.0";
	}
}


/**     clase de ayuda para controlar que un usuario esta demasiado tiempo inactivo
*/
class Watchdog extends Thread {
	private final WDispatcher parent;
	private final long timeout;
	private final Hashtable ht;
	private Enumeration users;

	public Watchdog(
		final WDispatcher parent, final long timeout, final Hashtable ht) {
		this.parent = parent;
		this.timeout = timeout;
		this.ht = ht;
	}

	public void run() {
		while(true) {
			try {
				sleep(1000 * 60 * 1); // Un minuto
			}
			catch(final InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			try {
//                              System.out.println("Watchdog: chequeando");
				users = ht.elements();
				while(users.hasMoreElements()) {
					final User u = (User) users.nextElement();
//					parent.debug("\t" + u.userid + ":\t" + u.sessionId + "\t" + u.lastAccess.toString());
					if((new Date()).getTime() - u.lastAccess.getTime() > 1000 * 60 * timeout) {  // 10 minutos
						parent.logoff(u.userid);
					}
				}
			}
			catch(final Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
