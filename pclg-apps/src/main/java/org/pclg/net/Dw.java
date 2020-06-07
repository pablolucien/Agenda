package org.pclg.net;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ControlableThread;
import org.pclg.tools.StringTools;
import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.pclg.tools.FileTools.getAlternativeFile;
import static org.pclg.tools.FileTools.sanitizeWindowsFilename;

/**
 * @author Pablo
 * @since 10-sep-2006 11:43:01
 */
public final class Dw {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final int TIME_2_SLEEP = 10000;
	private static final String abc = 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//	private static final String[] mes = {"Jan", "Feb", "Mar", "Apr", "May",
//		"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
//	};
	private Properties properties;
	private boolean verbose;
	private boolean debug;

	/** Tratamos de bajarlo 'retries' veces. */
	private int retries;

	/** Tama�o del pool de threads a usar para las descargas. */
	private int poolSize;

	/**
	 * @param args los par�metros a usar:<br>
	 *  if args[0] es - debo leer de stdin el URL.<br>
	 *  if args[0] es -f, sigue un archivo que contiene los URL a bajar.<br>
	 *    formato del archivo de datos:<br>
	 *    base del url desde hasta extension [prefix].<br>
	 *    http://ftp.ddj.com/download/docs/java/threads 1 20 .txt pepito<br>
	 *    donde desde y hasta pueden ser numericos o letras. 
	 *      Si comienzan con 0's se ajusta el nombre<br>
	 *    Un siguiente parametro opcional prefix es como se va a
	 *      prefijar los archivos locales<br>
	 *      (puede ser "&lt;directorio&gt;/" si "&lt;directorio&gt;" existe<br>
	 *  if args[0] es -d, sigue el directorio donde lo voy a guardar.
	 *
	 * @throws java.io.IOException si hay alg�n problema
	 */
	public Dw(final String[] args) throws IOException {
		final String dir = ".";

		if (args.length == 0) {
			LOGGER.error(LoggerFactory.getRandomErrorMessage() + " -> Que quieres bajarte, papito?");
			System.exit(0);
		}

		loadProperties();
		setProxy();

		// interpretar(argumentos);
		if ("-f".equals(args[0])) {
			// sigue el nombre del archivo de datos
			final File configFile = new File(args[1]);
			if (configFile.exists() && configFile.canRead()) {
				downloadFromConfigFile(configFile, dir);
			} else {
                LOGGER.warn(String.format("[%s] No existe o no es legible.\n", configFile));
			}
		} else {
			// El URL viene en la linea de comando
			download(new TargetInfo("", args[0], dir));
		}

		System.exit(0); // Por algun motivo, a veces el programa no terminaba
	}

	private void downloadFromConfigFile(final File configFile,
			final String dir) throws IOException {
		final List<TargetInfo> targets = new ArrayList<>();
		try (final BufferedReader inputReader = new BufferedReader(
				new FileReader(configFile))) {
			String line;
			while ((line = inputReader.readLine()) != null) {
				final String base;
				final String ext;
				final String firstS;
				final String lastS;
				final int first;
				final int last;
				final String prefix;

				if (StringTools.isCommentOrBlank(line)) {
					continue;
				}

				final StringTokenizer st = new StringTokenizer(line);

				if (st.countTokens() == 1) { // Un solo string es el URL
					targets.add(new TargetInfo("", line, dir));
				} else { // Tengo los 4 elementos
					base = st.nextToken();
					firstS = st.nextToken();
					lastS = st.nextToken();
					ext = st.nextToken();
					// El quinto elemento (Milla Jovovich) es el prefijo
					prefix = st.countTokens() > 0 ? st.nextToken() : "";
					try {
						// Asumimos que desde y hasta son numericos
						first = Integer.parseInt(firstS);
						last = Integer.parseInt(lastS) + 1;
						// si es numerico revisamos si comienza con 0, 00,
						// etc y ajustamos el pad convenientemente
						int padSize = 0;
						if (firstS.charAt(0) == '0') {
							padSize = firstS.length();
						}
						for (int ii = first; ii < last; ii++) {
							final String consec =
								ToolBox
									.leftPad(String.valueOf(ii), padSize, '0');

// IDEA-114997 (Bug) (IDEA 13: inspection to replace StringBuilder with String is incorrect)
							@SuppressWarnings("StringBufferReplaceableByString")
							final StringBuilder address =
								new StringBuilder(base.length()
									+ consec.length() + ext.length())
									.append(base).append(consec).append(ext);
							targets.add(new TargetInfo(prefix,
								address.toString(), dir));
						}
					} catch (final NumberFormatException ex) {
						// desde y hasta no son numericos: asumimos que son
						// letras
						for (int i = abc.indexOf(firstS.charAt(0));
							 i <= abc.indexOf(lastS.charAt(0)); i++) {
							final String address = base + abc.charAt(i) + ext;
							targets.add(new TargetInfo(prefix, address, dir));
						}
					}
				}
			}
		} catch (final Exception ex) {
			ToolBox.showInfo(ex);
		}

		final ExecutorService executor = Executors.newFixedThreadPool(poolSize);
		final List<Future<?>> futures = new ArrayList<>(targets.size());
		for (final TargetInfo target : targets) {
			futures.add(executor.submit(() -> download(target)));
		}

		for (final Future<?> future : futures) {
			try {
				future.get();
			} catch (final InterruptedException ex) {
				ToolBox.showInfo(ex);
				Thread.currentThread().interrupt();
			} catch (final ExecutionException ex) {
				ToolBox.showInfo(ex);
			}
		}
		executor.shutdown();
	}

	private void loadProperties() throws IOException {
		properties = new Properties();
		try (final InputStream inStream = getClass().getClassLoader()
				.getResourceAsStream("Dw.properties")) {
			properties.load(inStream);
		} catch (final IOException ex) {
			LOGGER.error(LoggerFactory.getRandomErrorMessage(), ex);
		}
		// No funciona en versiones anteriores a la 1.5
		verbose = Boolean.parseBoolean(properties.getProperty("verbose", "true"));
		debug = Boolean.parseBoolean(properties.getProperty("debug", "true"));
		retries = Integer.parseInt(properties.getProperty("retries", "5"));
		if (retries <= 0) {
			throw new IllegalArgumentException("Number of retries must be > 0");
		}
		poolSize = Integer.parseInt(properties.getProperty("poolSize", "5"));
		if (poolSize <= 0) {
			throw new IllegalArgumentException("Pool size must be > 0");
		}
		if (verbose) {
			LOGGER.warn(String.format(
				"debug = %b, retries = %d, poolSize = %d, proxySet = %s%n",
				debug, retries, poolSize, 
				properties.getProperty("http.proxySet", "false")));
		}
	}

	private void setProxy() {
		final String proxySet = properties.getProperty("http.proxySet");
		System.setProperty("http.proxySet", proxySet);
		if (proxySet.equalsIgnoreCase("true")) {
			final String proxyHost = properties.getProperty("http.proxyHost");
			final String proxyPort = properties.getProperty("http.proxyPort");
			if (verbose) {
				LOGGER.warn("Usando proxy: " + proxyHost + ":"
					+ proxyPort);
			}
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort);
			// this didn't work
			// System.setProperty("http.proxyUser",
			// properties.getProperty("http.proxyUser"));
			// System.setProperty("http.proxyPassword",
			// properties.getProperty("http.proxyPassword"));
			Authenticator.setDefault(new AuthenticateProxy(properties
				.getProperty("http.proxyUser"), properties
				.getProperty("http.proxyPassword")));
		}
	}

	private void download(final TargetInfo target) {
		if (verbose) {
			LOGGER.warn(target.address);
		}
		// Si es posible deberiamos continuar el download haciendo skip de lo
		// que tenemos
		for (int ii = 0; ii < retries; ii++) {
			final Dwldr dw;
			try {
				dw = new Dwldr(target.prefix, target.address, target.destino);
			} catch (final MalformedURLException ex) {
				LOGGER.warn(ex.toString());
				break;
			}
			dw.start();
			while (dw.isAlive()) {
				try {
					Thread.sleep(TIME_2_SLEEP);
				} catch (final InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
			if (dw.finalFeliz || dw.fileNotFound) {
				return;
			}
		}
		// Si no pudimos damos un mensaje
		LOGGER.warn("download de: " + target.address + " termino MAL");
	}

	/**
	 * Dwldr: Downloader
	 */
	final class Dwldr extends Thread {
		private static final int BUFFER_SIZE = 1024;

		/** Qu� vamos a bajar y donde lo ponemos. */
		private final String prefix;
		private final String address;
		private final String destino;

		/** Pudimos hacerlo? */
		private boolean finalFeliz;

		/** Indica que el archivo no existe en el host, para evitar reintentos. */
		private boolean fileNotFound;

		private URL unencodedURL;

		Dwldr(final String prefix, final String address, final String destino)
				throws MalformedURLException {
			this.address = address;
			this.destino = destino;
			this.prefix = prefix;
			unencodedURL = new URL(address);
		}

		@Override
		public void run() {
			final String file = unencodedURL.getFile();
			final String filename = sanitizeWindowsFilename(prefix
				+ file.substring(file.lastIndexOf('/') + 1), "_");
			File elArchivo = new File(destino, filename);
			if (elArchivo.exists()) {
				elArchivo = getAlternativeFile(elArchivo);
			}

			/*
			 * WatchDog nos detiene si pasa demasiado tiempo, para evitar el bloqueo
			 * en la lectura.
			 */
			try (final RandomAccessFile raf = new RandomAccessFile(elArchivo, "rw");
				 final InputStream is = new URL(StringTools.urlEncode(address)).openStream()) {
				final byte[] contenido = new byte[BUFFER_SIZE];
				// WatchDog nos detiene si pasa demasiado tiempo,
				// para evitar el bloqueo en la lectura
				final WatchDog wd = new WatchDog(this, WatchDog.TIMEOUT);
				wd.start();
				int count;
				while ((count = is.read(contenido)) > 0) {
					wd.reset(); // vamos bien
					raf.write(contenido, 0, count);
				}
				wd.halt();
				finalFeliz = true;
			} catch (final ClosedByInterruptException ex) {
				LOGGER.warn("Interrupted (by WatchDog?)");
			} catch (final FileNotFoundException ex) {
				fileNotFound = true;
				reportException(ex);
			} catch (final Exception ex) {
				reportException(ex);
			}
		}

		void reportException(final Exception ex) {
			if (debug) {
				ToolBox.showInfo(ex);
			} else {
				LOGGER.warn(ex.toString());
			}
		}
	}

	/**
	 * "Struct" para almacenar la informaci�n de los targets.
	 */
	private static final class TargetInfo {
		// No pongo getters porque no me da la gana!!
		private final String prefix, address, destino;

		private TargetInfo(final String prefix, final String address,
			final String destino) {
			this.prefix = prefix;
			this.address = address;
			this.destino = destino;
		}
	}

	/**
	 * WatchDog detiene al Thread que lo creo si no es reseteado cada cierto
	 * tiempo
	 */
	static final class WatchDog extends Thread implements ControlableThread {
		private static final int TIMEOUT = 1000 * 60 * 3;
		private static final long TIME_TO_SLEEP = 30000;

		private long start;
		private final long timeout;
		final Thread master;
		private boolean shouldRun = true;

		WatchDog(final Thread master, final long timeout) {
			this.master = master;
			this.timeout = timeout;
		}

		@Override
		public void halt() {
			shouldRun = false;
		}

		@Override
		public void run() {
			start = System.currentTimeMillis();
			while (shouldRun) {
				try {
					sleep(TIME_TO_SLEEP);
				} catch (final InterruptedException ex) {
					LOGGER.warn("Interrupted");
					Thread.currentThread().interrupt();
					break;
				}
				if (System.currentTimeMillis() - start > timeout) {
					break;
				}
			}
			// Solo llegamos aqui si se produce el timeout
			LOGGER.warn("WatchDog: Timeout " + ((Dwldr) master).address);
			master.interrupt(); // ??? Era stop. ver si funciona.
		}

		public void reset() {
			start = System.currentTimeMillis();
		}
	}

	private static final class AuthenticateProxy extends Authenticator {
		private final String user;
		private final String pwd;

		AuthenticateProxy(final String user, final String pwd) {
			this.user = user;
			this.pwd = pwd;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, pwd.toCharArray());
		}
	}
}