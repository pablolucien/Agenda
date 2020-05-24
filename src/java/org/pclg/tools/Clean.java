package org.pclg.tools;

import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 *	version 2003.07.02 Agregado el servicio de logging.
 */
public final class Clean {

	private static final Logger logger = LoggerFactory.make();
	private final AtomicInteger nrFiles = new AtomicInteger();
	private final AtomicInteger nrDirs = new AtomicInteger();
	private final AtomicLong deletedBytes = new AtomicLong();

	/**
     * .
     */
    private Clean(final String[] args) throws IOException {
		boolean overwrite = true;
		final Properties properties = new Properties();
		try {
			final InputStream in = Clean.class.getClassLoader()
				.getResourceAsStream("Clean.properties");
			if (in == null) {
				logger.warning("No existe el fichero de properties");
				//ver qué pasa con el log en este caso
			} else {
				properties.load(in);
				in.close();
				initLog(properties);
				overwrite = Boolean.parseBoolean(properties.getProperty("overwrite"));
			}
		} catch (final IOException ex) {
			logger.severe("No puedo configurar el logger. Se usan los valores por omisión");
		}
		logger.info("Iniciando aplicación");
		logger.info("Level: " + logger.getLevel());
		logger.info("overwrite: " + overwrite);
		// Let's get dangerous!
		final String[] targets = args.length > 0 ? args : getTargets();
		final int poolSize = Integer.parseInt(properties.getProperty("poolSize", "5"));
		if (poolSize <= 0) {
			throw new IllegalArgumentException("Pool size must be > 0");
		}
		final ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
		final List<Future<?>> futures = new ArrayList<Future<?>>();

		for (final String target : targets) {
			futures.add(executorService.submit(new Cleaner(target, overwrite)));
		}

		for (final Future<?> future : futures) {
			try {
				future.get();
			} catch (final Exception ex) {
				logger.log(Level.SEVERE , "¿Y ahora qué hago?", ex);
			}
		}

		logger.info(MessageFormat.format(
			"Finalizando aplicación. Borrados {0} dirs, {1} archivos, {2} bytes",
			nrDirs, nrFiles, deletedBytes));
		System.exit(0);
    }

	private static void initLog(final Properties properties) throws IOException {
		final String filename = properties.getProperty("file");
		//System.err.println(filename);
		final String logLevel = properties.getProperty("level");
		//System.err.println(logLevel);
		logger.setLevel(Level.parse(logLevel));
		//System.out.println("Level: " + logger.getLevel());
		//debugHandlers();
		final FileHandler fileHandler = new FileHandler(filename, true);
		fileHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fileHandler);
		logger.setUseParentHandlers(false);
		//debugHandlers();
	}


	private static String[] getTargets() throws IOException {
        final List<String> targetsList;
        final BufferedReader in = new BufferedReader(new InputStreamReader(
        	Clean.class.getClassLoader().getResourceAsStream(
				"Clean.targets")));
        try {
            targetsList = new ArrayList<String>();
            String line;
            while((line = in.readLine()) != null) {
                if (StringTools.isCommentOrBlank(line)) {
                    continue;
                }
                targetsList.add(line);
            }
        } finally {
        	in.close();
        }
		logger.info("Target list = " + targetsList);

		final String[] targets = new String[targetsList.size()];
		targetsList.toArray(targets);

		return targets;
	}

    /**
     * Ejecuta la aplicacion.
     * @param args argumentos.
     * @throws IOException si algo va mal.
     */
	public static void main(final String[] args) throws IOException {
		new Clean(args);
	}
	
	class Cleaner implements Runnable {
		private final String target;
		private final boolean overwrite;

		Cleaner(final String target, final boolean overwrite) {
			this.target = target;
			this.overwrite = overwrite;
		}

		@Override
		public void run() {
			logger.info("--- **** Cleaner.run(): processing " + target);
			if (target.endsWith("/*.*")) {
				final File dir = new File(target.substring(0,
						target.indexOf("/*.*")));
				if (!dir.exists()) {
					logger.info("No existe " + dir);
					return;
				}
				logger.info("Eliminando el contenido de " + dir);
				final File[] contents = dir.listFiles();
				if (contents == null) {
					logger.log(Level.SEVERE, new StringBuilder()
							.append("listFiles() in  ").append(dir)
							.append(" returned null").toString());
				} else {
					for (final File content : contents) {
						clean(content, overwrite);
					}
				}
			} else {
				final File targetFile = new File(target);
				clean(targetFile, overwrite);
			}
		}
	
		/**
		 * Deletes a file; if it's a directory its content is deleted as well.
		 * @param targetFile the file to delete.
	     * @param overwrite wheather to overwrite the content or not.
		 */
		public void clean(final File targetFile, final boolean overwrite)  {
			logger.log(Level.CONFIG, "Deleteando: " + targetFile);
			if(targetFile.isDirectory()) {
				deepClean(targetFile, overwrite);
			}
			deleteFile(targetFile, overwrite);
		}

	/**
	 * Deletes a directory and its content.
	 * @param dir the directory to delete.
     * @param overwrite indicates if the files should be overwritten.
	 */
	public void deepClean(final File dir, final boolean overwrite)  {
		final File[] files = dir.listFiles();
		for (int ii = 0; files !=null && ii < files.length; ii++) {
			logger.log(Level.FINER, "Borrando: " + files[ii]);
			if (files[ii].isDirectory()) {
				deepClean(files[ii], overwrite);
			}
			deleteFile(files[ii], overwrite);
		}
	}

    /**
     * Deletes a file from the filesystem. Optionally overwites its content
     * before deletion.
     * @param file the file to delete.
     * @param overwrite wheather to overwrite the content or not.
     * --since 2005.08.27
     */
    public void deleteFile(final File file, final boolean overwrite) {
		final boolean deleted;
		if (file.isFile()) {
			final long length = file.length();
			if (overwrite) {
				try {
					FileTools.overwriteFile(file);
				} catch (final Exception ex) {
					logger.log(Level.SEVERE, "Error accessing: " + file, ex);
				}
			}
			if (deleted = file.delete()) {
				nrFiles.getAndIncrement();
				deletedBytes.getAndAdd(length);
			}
		} else if (file.isDirectory()) {
			if (deleted = file.delete()) {
				nrDirs.getAndIncrement();
			}
		} else {
			deleted = false;
		}

		if (!deleted) {
			logger.log(Level.WARNING, "Could not delete: " + file);
		}
	}
 	}
}
