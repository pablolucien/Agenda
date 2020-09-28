package org.pclg.dbutil;

import dbinfo.SQLMessages;
import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;
import org.pclg.tools.StringTools;
import org.pclg.tools.ToolBox;
import org.pclg.xtras.ClassPathHacker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Actualiza una base de datos de acuerdo con un script segun el siguiente
 * formato: <blockquote>
 * <pre>
 * # Script de actualizacion de la base de datos
 * # Las lineas que comienzan con '#' son comentarios
 * # Las lineas que comienzan con '--' son comentarios
 * # Se aceptan comentarios en bloque estilo C con la restriccion de que el par
 * # de caracteres de apertura deben ser los primeros caracteres no blancos de
 * # una linea y los de cierre los ultimos.
 * # Las lineas en blanco no son consideradas
 * # Las lineas que terminan con '|' indican que la siguiente es
 * # continuacion de esta.
 * # El resto de las lineas deben ser instrucciones SQL v�lidas, salvo las
 * # siguentes directivas (una por l�nea)
 * # &lt;CLASSPATH&gt;        La lista, separada por '|' de los archivos o directorios a
 * #                    agregar al classpath the la aplicaci�n.
 * # &lt;DRIVER&gt;           Driver de la base de datos
 * # &lt;URL&gt;              URL de conexion a la base de datos
 * # &lt;USER&gt; user        Establece el nombre de usuario de la base de datos.
 * #                    Si no se proporciona, lo pide en stdin.
 * # &lt;PWD&gt; pwd          Establece la contrasenha de la base de datos.
 * #                    Si no se proporciona, la pide en stdin.
 * # &lt;EXIT&gt;             Finaliza la ejecucion
 * # &lt;SKIP&gt;             No procesa el script hasta que se encuentre un &lt;CONTINUE&gt;
 * # &lt;CONTINUE&gt;         Contin�a el procesamiento del Script
 * # &lt;ZAP_DATA&gt;         Elimina todos los registros de todas las tablas salvo las
 * #                    META_*, REPO_* y SYS_*
 * # &lt;ZAP_DATABASE&gt;     Elimina todos los registros de todas las tablas INCUYENDO
 * #                    las META_*, REPO_* y SYS_*
 * # &lt;DROP_TABLES&gt;      Elimina todas las tablas de datos de usuario
 * # &lt;DROP_ALL_TABLES&gt;  Elimina todas las tablas INCUYENDO las META_*, REPO_*
 * #                    y SYS_*
 * # &lt;VERSION&gt; n.nn     Numero de version a insertar en la base de datos
 * # &lt;COMPACT&gt;          Compacta la base de datos (NO IMPLEMENTADO)
 * # &lt;DO_BACKUP&gt;        Hace el respaldo inicial (Bases de datos Access)
 * # &lt;NO_ASK&gt;           No pide confirmacion en las instrucciones ZAP y DROP
 * #                    (NO IMPLEMENTADO)
 * # &lt;ASK&gt;              Pide nuevamante confirmacion en las instrucciones
 * #                    ZAP y DROP (NO IMPLEMENTADO)
 * #
 * # El Classpath, Driver y URL de la base de datos deben ser las primeras l�neas
 * # salvo las instrucciones &lt;DO_BACKUP&gt; &lt;USER&gt; y &lt;PWD&gt;
 * #
 * #&lt;DO_BACKUP&gt;
 * #
 * &lt;CLASSPATH&gt;F:/lib/derby.jar
 * &lt;DRIVER&gt;org.apache.derby.jdbc.EmbeddedDriver
 * &lt;URL&gt;jdbc:derby:C:/plucien/PERSONNEL/fortunesDB;create=false
 * #&lt;CLASSPATH&gt;
 * #&lt;DRIVER&gt;sun.jdbc.odbc.JdbcOdbcDriver
 * #&lt;URL&gt;jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=C:/tmp/Test.mdb;UID=user;PWD=pwd
 * #
 * &lt;SKIP&gt;
 * DROP TABLE _Usuarios
 * #
 * &lt;CONTINUE&gt;
 * #CREATE TABLE META_VERSION (mayor INTEGER, menor INTEGER, fecha TIMESTAMP,|
 * #	  PRIMARY KEY (mayor, menor))
 * #&lt;VERSION&gt; 3.5
 * #INSERT INTO Usuarios (Codigo, nombre) VALUES (1, 'Generico')
 * CREATE TABLE FORTUNES (|
 * 	Clave INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0 ,INCREMENT BY 1),|
 * 	Fortune LONG VARCHAR NOT NULL|
 * )
 *
 * INSERT INTO FORTUNES (FORTUNE) VALUES ('Soldier of fortune')
 * #
 * </pre>
 * </blockquote>
 *
 * @author El Coyote Cojo
 * @version 2002.03.27 (Cumpla�os de Ricardo)
 */
public final class DbUpdate {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	/** Indica si se debe informar de lo que se est� haciendo. */
	private static final boolean VERBOSE = true;

	/** Indica que no se puede eliminar porque hay tablas relacionadas. */
//	private static final int ERR_RELATED_TABLES = -1612;
	private static final int ERR_RELATED_TABLES = -1051;

	/** Mensaje. */
	private static final String PROMPT_ZAP_ALL_DATA =
			"Esta operacion BORRAR� todos los datos, incluyendo "
			+ "los META_DATA. �Desea continuar? ";

	/** Mensaje. */
	private static final String PROMPT_ZAP_DATA =
		"Esta operacion BORRAR� todos los datos. �Desea continuar? ";

	/** Mensaje. */
	private static final String PROMPT_ZAP_ALL_TABLES =
		"Esta operacion ELIMINARA todas las tablas, "
		+ "incluyendo las META_DATA. �Desea continuar? ";

	/** Mensaje. */
	private static final String PROMPT_ZAP_TABLES =
		"Esta operacion ELIMINARA todas las tablas. �Desea continuar? ";

	/** Mensaje. */
	private static final String MSG_SKIP =
			"Encontrada una directiva <SKIP>. Suspendido el procesamiento";

	/** Mensaje. */
	private static final String MSG_CONTINUE =
			"Encontrada una directiva <CONTINUE>. Continuando el procesamiento";

	/** Mensaje. */
	private static final String MSG_COMPACT =
			"Encontrada una directiva <COMPACT>.  (NO IMPLEMENTADO)";
	private static final String USER_TAG = "<USER>";
	private static final String PWD_TAG = "<PWD>";
	private static final String CLASSPATH_TAG = "<CLASSPATH>";
	private static final String DRIVER_TAG = "<DRIVER>";
	private static final String URL_TAG = "<URL>";
	private static final String DO_BACKUP_TAG = "<DO_BACKUP>";
	private static final String SKIP_TAG = "<SKIP>";
	private static final String CONTINUE_TAG = "<CONTINUE>";
	private static final String COMPACT_TAG = "<COMPACT>";
	private static final String VERSION_TAG = "<VERSION>";
	private static final String ZAP_DATA_TAG = "<ZAP_DATA>";
	private static final String ZAP_DATABASE_TAG = "<ZAP_DATABASE>";
	private static final String DROP_TABLES_TAG = "<DROP_TABLES>";
	private static final String DROP_ALL_TABLES_TAG = "<DROP_ALL_TABLES>";
	private static final String EXIT_TAG = "<EXIT>";
	private static final String EXIT_MSG
		= "Encontrada una directiva <EXIT>. Fin de la ejecucion";

	/** Representa una linea numerada. */
	private class NumberedLine {
		int lineLumber;
		String line;
	}

	/**
	 * lee una l�nea del archivo de entrada, descartando los comentarios.
	 * @param in de donde leer.
	 * @param numberedLine d�nde poner lo le�do.
	 * @throws IOException si hay problemas I/O.
	 */
	private static void readLine(final BufferedReader in,
			final NumberedLine numberedLine) throws IOException {
		boolean insideBlockComment = false;
		String line;
		numberedLine.line = "";
		while ((line = in.readLine()) != null) {
			numberedLine.lineLumber++;
			line = line.trim();
			if (StringTools.isCommentOrBlank(line)) {
				continue;
			}
			if (line.startsWith("/*")) {
				insideBlockComment = true;
				continue;
			}
			if (line.endsWith("*/")) {
				insideBlockComment = false;
				continue;
			}
			if (insideBlockComment) {
				continue;
			}

			if (line.endsWith("|")) {
				numberedLine.line += line.substring(0, line.length() - 1);
				continue;
			}
			numberedLine.line += line;
			return;
		}
	}

	/**
	 * Muestra una l�nea del archivo indicando el numero de esta.
	 * @param numberedLine la linea.
	 */
	private void displayLine(final NumberedLine numberedLine) {
		if (VERBOSE) {
			outMessage(ToolBox.leftPad(String.valueOf(numberedLine.lineLumber),
                    4, ' ') + ": " + numberedLine.line);
		}
	}

	/**
	 * Outputs a stdout message.
	 * @param text the maessage.
	 */
	private static void outMessage(final String text) {
		System.out.println(text);
	}

	/**
	 * Outputs a stderr message.
	 * @param text the maessage.
	 */
	private static void errMessage(final String text) {
		System.err.println(text);
	}

	/**
	 * Hace una copia de un archivo.
	 *
	 * @param fileName El nombre del archivo al respaldar.
	 * @throws IOException si hay errores de I/O.
	 */
	private static void backUpFile(final String fileName) throws IOException {
		final File inFile = new File(fileName);
		if (!inFile.isFile()) {
			throw new IOException('\"' + inFile.getName() + '\"' + " es un directorio u otra cosa");
		}
		String newFileName = fileName;
		String ext = "";
		if (fileName.indexOf('.') > -1) {
			newFileName = fileName.substring(0, fileName.lastIndexOf('.'));
			ext = fileName.substring(fileName.lastIndexOf('.'));
		}
		final Date now = new Date();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("_yyyy_MM_dd__HH_mm_ss");
		newFileName += dateFormat.format(now) + ext;
		final File outFile = new File(newFileName);
		final FileInputStream fis = new FileInputStream(inFile);
		final FileOutputStream fos = new FileOutputStream(outFile);
		final byte[] buf = new byte[1024 * 50];
		int count;
		if (VERBOSE) {
			System.out.print("Copiando " + inFile.getName() + " a "
					+ outFile.getName() + " ... ");
		}
		while ((count = fis.read(buf)) != -1) {
			fos.write(buf, 0, count);
		}
		fos.close();
		fis.close();
		if (VERBOSE) {
			outMessage(" listo el pollo");
		}
	}

	/**
	 * Elimina los datos de todas las tablas de la base de datos.
	 *
     * @param stmt the statement to interact with the database.
     * @param zapAll Si es true elimina tambien los datos de las metatablas, si
	 *               es false solo elimina los datos de usuario.
     * @param tables the List of the tables to process.
     * @throws SQLException si hay problemas de base de datos.
     * @throws java.io.IOException if something goes wrong with the filesystem.
	 */
	private static void zapData(final Statement stmt, final boolean zapAll,
		final List<String> tables) throws SQLException, IOException {
		// Pedimos una confirmacion al operador
		final String answer;

		if (zapAll) {
			answer = ToolBox.getString(PROMPT_ZAP_ALL_DATA);
		} else {
			answer = ToolBox.getString(PROMPT_ZAP_DATA);
		}

		if (!answer.toLowerCase().startsWith("s")) {
			return;
		}

		boolean again = false;	// Indica que debo volver a intentarlo
		do {
			for (final String tabla : tables) {
				if ((tabla.startsWith("META_")
					|| tabla.startsWith("REPO_")
					|| tabla.startsWith("SYS_")) && !zapAll) {
					continue;
				}
				final String command = "DELETE FROM \"" + tabla + '\"';
				outMessage(command);
				try {
					stmt.executeUpdate(command);
				} catch (final SQLException ex) {
					if (ex.getErrorCode() == ERR_RELATED_TABLES) {
						again = true;
						errMessage(ex.getMessage());
					} else {
						throw ex;
					}
				}
			}
		} while (again);
	}

	/**
	 * Elimina las tablas de la base de datos.
	 *
     * @param stmt the statement to interact with the database.
     * @param dropAll Si es true elimina tambien las metatablas, si es false
	 *                solo elimina las tablas de usuario.
     * @param tables the List of the tables to process.
     * @throws SQLException si hay problemas de base de datos.
     * @throws java.io.IOException if something goes wrong with the filesystem.
	 */
	private static void dropTables(final Statement stmt, final boolean dropAll,
			final List<String> tables)
			throws SQLException, IOException {
		// Pedimos una confirmacion al operador
		final String answer;

		if (dropAll) {
			answer = ToolBox.getString(PROMPT_ZAP_ALL_TABLES);
		} else {
			answer = ToolBox.getString(PROMPT_ZAP_TABLES);
		}

		if (!answer.toLowerCase().startsWith("s")) {
			return;
		}

		boolean again;	// Indica que debo volver a intentarlo
		do {
			again = false;
			for (int ii = 0, tablesSize = tables.size(); ii < tablesSize; ii++) {
				final String tabla = tables.get(ii);
				if (tabla == null || ((tabla.startsWith("META_")
						|| tabla.startsWith("REPO_")
						|| tabla.startsWith("SYS_")) && !dropAll)) {
					continue;
				}
				final String command = "DROP TABLE \"" + tabla + '\"';
				outMessage(command);
				try {
					stmt.executeUpdate(command);
					tables.set(ii, null);
				} catch (final SQLException ex) {
					if (ex.getErrorCode() == ERR_RELATED_TABLES) {
						again = true;					// Volvemos a intentarlo
						errMessage(ex.getMessage());
					} else {
						throw ex;
					}
				}
			}
		} while (again);
	}

	/**
	 * Updates the datdabase.
	 * @param args  arguments to the app.
	 */
	private void update(final String[] args) {
		String databaseName = null;	// S�lo para BDs Access
		try {
			// Abrimos el archivo de Script
			final BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream(args[0])));

			// El Classpath, Driver y URL de la base de datos deben ser las primeras l�neas
			// que no sean comentario salvo las instrucciones <DO_BACKUP> <USER> o <PWD>
			boolean doBackup = false;
			String user = "";
			String pwd = "";
			final NumberedLine numberedLine = new NumberedLine();
			numberedLine.lineLumber = 0;
			int neededParametersFound = 0;
			String classpath = null;
			String driver = null;
			String url = null;
			while(neededParametersFound < 3) {
				readLine(in, numberedLine);
				final String line = numberedLine.line;
				if (line.equals(DO_BACKUP_TAG)) {
					errMessage("Hacemos copia de seguridad");
					doBackup = true;
				} else if (line.startsWith(USER_TAG)) {
					user = getUser(line);
				} else if (line.startsWith(PWD_TAG)) {
					pwd = getPassword(line);
				} else if (line.startsWith(CLASSPATH_TAG)) {
					classpath = line.substring(CLASSPATH_TAG.length());
					neededParametersFound++;
					outMessage("classpath: " + classpath);
				} else if (line.startsWith(DRIVER_TAG)) {
					driver = line.substring(DRIVER_TAG.length());
					neededParametersFound++;
					outMessage("driver: " + driver);
				} else if (line.startsWith(URL_TAG)) {
					url = line.substring(URL_TAG.length());
					neededParametersFound++;
					outMessage("url: " + url);
				}
			}

			// La conexion con la bd
			final Connection connection;

			if (doBackup) {
				final int indexOfDBQ;
				databaseName = url.substring(
					(indexOfDBQ = url.indexOf("DBQ=")) + "DBQ=".length(),
					url.indexOf(";", indexOfDBQ));
				outMessage("databaseName: " + databaseName);
				final File file = new File(databaseName);
				if (file.exists()) {
					backUpFile(databaseName);		// hacemos una copia de la db
				}
			}

			// Establecemos la conexion con la base de datos
			ClassPathHacker.addFiles(classpath.split("\\|"));
			Class.forName(driver);
			connection = DriverManager.getConnection(url);

			final DatabaseMetaData meta = connection.getMetaData();

			if (VERBOSE) {
				informAboutDatabase(databaseName, meta);
			}
			final List<String> tables = new ArrayList<>(100);
			getTables(meta, tables);

			executeScript(in, connection, tables, numberedLine);

			// Cerramos el archivo de Script
			in.close();

			// Cerramos la conexion con la base de datos
			connection.close();
		} catch (final ClassNotFoundException | IOException ex) {
			ToolBox.showInfo(ex);
		} catch (final SQLException ex) {
			final String msg = SQLMessages.getMessage(ex, databaseName);
			if (msg != null) {
				errMessage(msg);
			} else {
				ToolBox.showInfo(ex);
			}
		}
	}

	/**
	 * Obtiene el segundo token de una linea. Si no existe lo pide a stdin.
	 * @param line la l�nea de donde obtener el token.
	 * @param prompt el prompt para pedir input.
     * @return el token le�do.
     * @throws IOException si ha problemas deI/O.
	 */
	private static String getToken(final String line, final String prompt) throws
			IOException {
		final StringTokenizer st = new StringTokenizer(line);
		st.nextToken();
		if (st.hasMoreTokens()) {
			return st.nextToken();
		}
		return ToolBox.getString(prompt);
	}

	/**
	 * * Obtains magically the password.
	 * @param line  la l�nea de d�nde leer.
     * @return the password.
     * @throws IOException si ha problemas deI/O.
	 */
	private static String getPassword(final String line) throws IOException {
		return getToken(line, "Password:");
	}

	/**
	 * Obtains magically the username.
	 * @param line la l�nea de d�nde obtener the username.
     * @return the username.
     * @throws IOException si ha problemas deI/O.
	 */
	private static String getUser(final String line) throws IOException {
		return getToken(line, "User Name:");
	}

	/**
	 * Ejecuta el script.
	 * @param in el stream de donde leer el script.
	 * @param connection la conexion con la bd
	 * @param tables los nombres de las tablas a actualizar.
     * @param numberedLine la l�nea donde se leen los comandos del script.
     * @throws SQLException si hay problemas de base de datos.
	 * @throws IOException si hay problemas de I/O.
	 */
	private void executeScript(final BufferedReader in,
			final Connection connection, final List<String> tables,
			final NumberedLine numberedLine) throws SQLException, IOException {
		final Statement stmt = connection.createStatement();
		boolean working = true;
		readLine(in, numberedLine);
		while (numberedLine.line != null && !numberedLine.line.trim().equals("")) {
			if (numberedLine.line.equalsIgnoreCase(SKIP_TAG)) {
				if (working) {
					outMessage(MSG_SKIP);
				}
				working = false;
				readLine(in, numberedLine);
				continue;
			} else if (numberedLine.line.equalsIgnoreCase(CONTINUE_TAG)) {
				if (!working) {
					outMessage(MSG_CONTINUE);
				}
				working = true;
				readLine(in, numberedLine);
				continue;
			}

			if (!working) {
				readLine(in, numberedLine);
				continue;
			}

			displayLine(numberedLine);
			if (numberedLine.line.toUpperCase().startsWith(COMPACT_TAG)) {
				outMessage(MSG_COMPACT);
				readLine(in, numberedLine);
				continue;
			}

			if (numberedLine.line.toUpperCase().startsWith(VERSION_TAG)) {
				updateVersion(stmt, numberedLine.line);
				readLine(in, numberedLine);
				continue;
			}

			if (numberedLine.line.equalsIgnoreCase(ZAP_DATA_TAG)) {
				zapData(stmt, false, tables);
				readLine(in, numberedLine);
				continue;
			}

			if (numberedLine.line.equalsIgnoreCase(ZAP_DATABASE_TAG)) {
				zapData(stmt, true, tables);
				readLine(in, numberedLine);
				continue;
			}

			if (numberedLine.line.equalsIgnoreCase(DROP_TABLES_TAG)) {
				dropTables(stmt, false, tables);
				readLine(in, numberedLine);
				continue;
			}

			if (numberedLine.line.equalsIgnoreCase(DROP_ALL_TABLES_TAG)) {
				dropTables(stmt, true, tables);
				readLine(in, numberedLine);
				continue;
			}

			if (numberedLine.line.equalsIgnoreCase(EXIT_TAG)) {
				outMessage(EXIT_MSG);
				break;
			}

			final int count = stmt.executeUpdate(numberedLine.line);
			numberedLine.line = " -- Afectadas " + count + " filas";
			displayLine(numberedLine);
			readLine(in, numberedLine);
		}
	}

	/**
	 * Updates the version of the db.
	 * @param stmt the statement to interact with the database.
     * @param linea the input line of the script.
	 * @throws SQLException si hay problemas de base de datos.
	 */
	private static void updateVersion(final Statement stmt, final String linea)
			throws SQLException {
		final String mayor = linea.substring(linea.indexOf(' ') + 1,
			linea.indexOf('.'));
		final String menor = linea.substring(linea.indexOf('.') + 1);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("#MM/dd/yyyy#");
		final StringBuilder buffer = new StringBuilder(1024);
		buffer.append("UPDATE META_VERSION SET mayor = ").append(mayor)
				.append(", menor = ").append(menor).append(", fecha = ")
				.append(dateFormat.format(new Date()));
		final String command = buffer.toString();
		outMessage(command);
		stmt.executeUpdate(command);
	}

	/**
	 * Obtiene los nombres de las tablas de la base de datos.
	 * @param meta el objeto usado para obtener la informacion.
	 * @param tables the List to put the names of the tables into.
     * @throws SQLException si hay problemas.
	 */
	private static void getTables(final DatabaseMetaData meta,
			final List<String> tables) throws SQLException {
		final String[] tableTypes = {"TABLE"};
		final ResultSet dbMetaData;
		dbMetaData = meta.getTables(null, null, "%", tableTypes);
		final int interestingCol = 3;
		while (dbMetaData.next()) {
			tables.add(dbMetaData.getString(interestingCol));
		}
	}

	/**
	 * Provee informacion sobre la base de datos.
	 * @param databaseName el nombre de la base de datos.
	 * @param meta el objeto usado para obtener la informacion.
	 * @throws SQLException si hay problemas.
	 */
	private static void informAboutDatabase(final String databaseName,
			final DatabaseMetaData meta) throws SQLException {
		outMessage("Usando la base de datos: " + databaseName);
		outMessage("=========== Informacion de la base de datos ============");
		outMessage("DatabaseProductName: " + meta.getDatabaseProductName());
		outMessage("DatabaseProductVersion: " + meta.getDatabaseProductVersion());
		outMessage("UserName: " + meta.getUserName());
	}


	/**
	 * Verifica que se hayan pasado parametros correctos, y si no, termina la
	 * ejecucion.
	 * @param args los parametros de la aplicacion.
	 */
	private static void checkArgs(final String[] args) {
		if (args[0].equals("-help")) {
			try {
				FileTools.catStream(ClassLoader.getSystemClassLoader().getResourceAsStream("DbUpdate.help"), System.err);
			} catch (final IOException ex) {
				LOGGER.error(LoggerFactory.ERROR_TAG, ex);
			}
			System.exit(1);
		} else if (args.length != 1) {
			errMessage("Uso: java org.pclg.dbutil.DbUpdate -help | <script>");
			System.exit(1);
		}
	}

	/**
	 * Entry point.
	 * @param args arguments to the app.
	 */
	public static void main(final String[] args) {
		checkArgs(args);
		new DbUpdate().update(args);
	}

}
