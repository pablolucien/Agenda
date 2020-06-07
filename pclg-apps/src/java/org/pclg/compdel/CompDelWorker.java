/*
 * CompDelWorker.java
 * Created on 01-dic-2004
 */
package org.pclg.compdel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.apache.log4j.Logger;
import org.pclg.filesystem.DirectoryCleaner;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.Chrono;
import org.pclg.tools.Constants;
import org.pclg.tools.DigestTools;
import org.pclg.tools.Dir;
import org.pclg.tools.FileTools;
import org.pclg.tools.ToolBox;


import static org.pclg.tools.FileTools.NULL_FILE_ARRAY;
import static org.pclg.tools.StringTools.intern;

/**
 * Does the work of comparing and optionally deleting.
 * @author El Coyote Cojo.
 * @version 1.0
 * @since 01-dic-2004
 */
final class CompDelWorker extends Thread {
    /** Para el utilísimo "logueado". */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    /** Mensaje. */
    private static final String MSG_TERMINATE = "Me pidieron que cometiera seppuku";

    private static final long TIME_2_SLEEP = 1000L;

    /** Should terminte. */
    private volatile boolean mustDie;

    /** Are we working or not. */
    private volatile boolean dozing;

    /** the master object. */
    private final CompDel compDel;

    /** where to print standard messages. */
    private final Output out;

    /** where to print error messages. */
    private final Output err;
	private final ControlPanel controlPanel;

    private final UndoFileManager undoFileManager = new UndoFileManager();

	/**
	 * directorios a excluir del proceso.
	 * C:\RECYCLER|D:\RECYCLER|E:\RECYCLER|C:\windows|C:\Program Files|C:\images|D:\images|E:\images|C:\home\databases|C:\sistema|C:\musique|C:\jdk|C:\home\peliculas|C:\Cdu|
	 */
	private String[] excludeList;

	// En un arreglo de File ponemos todos los archivos del arbol
	private List<File> archivo1;
	private List<File> archivo2;

	private boolean dir1IsDirty = true;
	private boolean dir2IsDirty = true;

	// 2002.02.26 Un Hack para ahorrar tiempo: no pedir el dir ni el digest si ya está hecho
	private File oldDirF1;
	private File oldDirF2;

	// directorios para la comparacion.
	private File dirF1;
	private File dirF2;

	// los digests de los susodichos
	private String[] digest1;
	private String[] digest2;

	/**
     * Creates a Worker.
     * @param compDel the master object.
     * @param out where to print standard messages.
     * @param err where to print error messages.
     */
    CompDelWorker(final CompDel compDel, final Output out, final Output err, final ControlPanel controlPanel) {
        this.compDel = compDel;
        this.out = out;
        this.err = err;
		this.controlPanel = controlPanel;
	}

    /**
     * Indica que debemos abortar la operación.
     *
     * @since 2006.07.29
     */
    void terminate() {
        mustDie = true;
        LOGGER.info(MSG_TERMINATE);
    }

    /**
     * De momento está como una chapucilla. (¿Hasta cuándo seguirá así? nice
     * question!).
     *
     * @since 2003.11.11 Cumpleaños de Mercily
     */
    void suspendActivities() {
        dozing = true;
    }

    /**
     * De momento está como una chapucilla. (¿Hasta cuándo seguirá así? nice
     * question!).
     *
     * @since 2003.11.11 Cumpleaños de Mercily
     */
    void resumeActivities() {
        dozing = false;
    }


    @Override
	public void run() {
        compDel.frame.optionPanel.setUserMsg(formatExcludeList());
        if (compDel.frame.optionPanel.acceptOptions()) {
            try {
                doTheWork();
            } catch (final FileNotFoundException ex) {
                ToolBox.showInfo(ex);
            }
        }

        controlPanel.setStatus(ControlPanel.Status.STOPPED);
    }

    private String formatExcludeList() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Lista de exclusión:");
        for (final String excluded : excludeList) {
            stringBuilder.append("\n ... ").append(excluded);
        }
        return stringBuilder.toString();
    }

    // Estadisticas
    static class Totals {
        long desperdicio;
        long repetidos;
        long totalComparaciones;
    }

    /**
     * Does the work.
     * --param theSlave De momento está como una chapucilla. (¿Hasta cuándo
     *                 seguirá así? nice question!) desde 2003.11.11 Cumpleaños
     *                 de Mercily.
     * @throws java.io.FileNotFoundException if errors.
     */
    private void doTheWork() throws FileNotFoundException {
        int ii;
        int jj;
        final int cronTotal = Chrono.getChrono();
        final int cron = Chrono.getChrono();

        try {
            setUpFileSets();
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
            controlPanel.setStatus(ControlPanel.Status.STOPPED);
            resetFields();
			compDel.alert();
			return;
        }
        if (compDel.frame.optionPanel.undoBox.isSelected()) {
            undoFileManager.initUndoFile();
        }

        sayHello();
        Chrono.start(cron);

        final Totals totals = new Totals();
        if (compDel.frame.optionPanel.fastHeuristicsOnly.isSelected() && compDel.frame.optionPanel.deleteBox.isSelected() && !dirF1.equals(dirF2)) {
            cleanFilesWithoutDigest(archivo1.toArray(NULL_FILE_ARRAY), archivo2.toArray(NULL_FILE_ARRAY), totals);
        }

        Collection<File> dirs = null;
        if (compDel.frame.optionPanel.delDirBox.isSelected() && !mustDie) {
            dirs = new HashSet<>(Constants.BUFFER_SIZE);
            for (final File anArchivo1 : archivo1) {
                if (anArchivo1 != null && anArchivo1.isDirectory()) {
                    dirs.add(anArchivo1);
                }
            }
        }

        if (!compDel.frame.optionPanel.fastHeuristicsOnly.isSelected()) {
            generateDigests();
            printETA();

            File lastParentDir = null;

            for (ii = 0; ii < digest1.length && !mustDie; ii++) {
                while (dozing) {
                    sleepEinBisschen(TIME_2_SLEEP);
                }

                updateTitle(ii);

                if (digest1[ii] == null) {
                    continue;
                }

                for (jj = 0; jj < digest2.length; jj++) {
                    beNice();
                    if (digest2[jj] == null
                        || archivo1.get(ii).equals(archivo2.get(jj))  // !!! Importante: no borrarlo si estoy comparando contra él mismo
                        || digest1[ii] != digest2[jj]) {      // puedo por el intern()
                        continue;
                    }

                    totals.totalComparaciones++;
                    if (FileTools.compareContents(archivo1.get(ii), archivo2.get(jj))) {
                        totals.desperdicio += archivo2.get(jj).length();
                        totals.repetidos++;
                        // Separar la salida si hay cambio de directorio (esto funciona si la vaina está ordenada)
                        if (!archivo1.get(ii).getParentFile().equals(lastParentDir)) {
                            lastParentDir = archivo1.get(ii).getParentFile();
                            out.println("-----  Cambio de directorio ------------------  " + lastParentDir.getAbsolutePath());
                        }

                        tryToDeleteFile(ii, jj);
                        break;
                    }
                }
            }
        }

        if (compDel.frame.optionPanel.delDirBox.isSelected() && !mustDie) {
            if (dirs != null) {     // delDirBox.isSelected() could have changed, so dirs can be null
                compDel.frame.setTitle(getClass().getSimpleName() + ": borrando directorios vacíos");
                undoFileManager.setDeletedDirs(DirectoryCleaner.cleanDirs(dirs, err));
            }
        }

        undoFileManager.closeUndoFile();

        Chrono.mark(cron);
        Chrono.mark(cronTotal);
        sayGoodby(totals.repetidos, totals.desperdicio, totals.totalComparaciones,
                cron, cronTotal);
    }	// doTheWork()


	/**
	 * Estimated Time of Arrival.
	 */
	private void printETA() {
		final int TIEMPO_POR_ARCHIVO = 360;
		final long miliSegundos = (long) (archivo1.size() * TIEMPO_POR_ARCHIVO / 1000);
		err.println("Tiempo estimado: " + Chrono.timeDetail(miliSegundos * 1000L));
		err.println("En realidad el tiempo no es lineal en relacion con la cantidad de archivos.\nDepende del factorial de ese numero");
		err.println("Comenzando:\t " + new Date());
	}

	private void setUpFileSets() throws IOException {
		final String[] excluded = compDel.frame.optionPanel.ignoreExclude.isSelected() ? null : excludeList;
		final boolean recurse = compDel.frame.optionPanel.recurseBox.isSelected();
		if (archivo1 == null || dir1IsDirty) {
			err.println("A pedir el primer directorio:\t" + new Date());
			archivo1 = new Dir(excluded).listarArchivos(dirF1, recurse, false);
			err.println("El primer directorio ya está listo:\t" + new Date());
		} else {
			err.println("El primer directorio ya lo tenía desde antes:\t" + new Date());
		}

		if (dirF1.equals(dirF2)) {
			err.println("Comparando en el mismo directorio");
			archivo2 = archivo1;
		} else {
			if (archivo2 == null || dir2IsDirty) {
				err.println("A pedir el segundo directorio:\t" + new Date());
				archivo2 = new Dir(excluded).listarArchivos(dirF2, recurse, false);
				err.println("El segundo también está listo:\t" + new Date());
			} else {
				err.println("El segundo directorio ya lo tenía desde antes:\t" + new Date());
			}
		}
	}


	/**
	 * Generates Digests.
	 */
	private void generateDigests() {
		//setTitle(getClass().getName() + ": Generando digests");
		err.println("ESTIMAR LA DURACIÓN DE LOS DIGESTS ");
        final int cron = Chrono.getChrono();
		if (dir1IsDirty || digest1 == null) {
			digest1 = DigestTools.generateDigest(archivo1.toArray(NULL_FILE_ARRAY), true, compDel.frame.optionPanel.ignoreBox.isSelected());
			intern(digest1);
			dir1IsDirty = false;
		}

		if (dirF1.equals(dirF2)) {
			err.println("Comparando en el mismo directorio: no pido el segundo digest");
			digest2 = digest1;
		} else {
			if (dir2IsDirty || digest2 == null) {
				digest2 = DigestTools.generateDigest(archivo2.toArray(NULL_FILE_ARRAY), true,
                        compDel.frame.optionPanel.ignoreBox.isSelected());
				intern(digest2);
				dir2IsDirty = false;
			}
		}
        Chrono.mark(cron);
        err.println("DURACIÓN DE LOS DIGESTS: "
                + Chrono.timeDetail(Chrono.elapsed(cron)));
	}



    private void beNice() {
		final long time2Sleep = 100L;
		while (dozing) {
			sleepEinBisschen(time2Sleep);
		}
	}

	private static void sleepEinBisschen(final long time2Sleep) {
		try {
			sleep(time2Sleep);
		} catch (final InterruptedException ex) {
             Thread.currentThread().interrupt();
		}
	}

	/**
	 * Tries to delete a file if it equals another one.
	 *
	 * @param ii the pos of the File in the first File[].
	 * @param jj the pos of the File in the second File[].
	 */
	private void tryToDeleteFile(final int ii, final int jj) {
		// Borrarlo si es necesario y si no, informar
		if (compDel.frame.optionPanel.deleteBox.isSelected() && archivo1.get(ii).delete()) {
			out.println(String.valueOf(ii + 1) + "\tBorrado par Coño -> "
                    + archivo1.get(ii) + " == " + archivo2.get(jj));
			digest1[ii] = null;
            if (compDel.frame.optionPanel.undoBox.isSelected()) {
                undoFileManager.add2UndoFile(archivo1.get(ii), archivo2.get(jj));
            }
		} else {
			if (compDel.frame.optionPanel.deleteBox.isSelected()) {
				// Es que no pude
				out.println((ii + 1) + "\tNO PUDE BORRAR " + archivo1.get(ii) + " == " + archivo2.get(jj));
			} else {
				// Es que no quise
				out.println((ii + 1) + "\tNO BORRADO " + archivo1.get(ii) + " == " + archivo2.get(jj));
			}
		}
		// En cualquier caso, si no quiero considerarlo despues
		if (compDel.frame.optionPanel.simulateDeleteBox.isSelected()) {
			digest1[ii] = null;
		}
	}

    private String progressInfoTitle;

    /**
     *  An activity report message.
     */
    private void sayHello() {
        final long nrOfPlaintFiles1 = computeNrOfPlaintFiles(archivo1.toArray(NULL_FILE_ARRAY));
        final long nrOfPlaintFiles2 = computeNrOfPlaintFiles(archivo2.toArray(NULL_FILE_ARRAY));
        err.println("Comparando "
                + nrOfPlaintFiles1 + " archivos en: " + dirF1.getAbsolutePath()
                + "\n con        "
                + nrOfPlaintFiles2 + " en: " + dirF2.getAbsolutePath());

        progressInfoTitle = "Comparando " + nrOfPlaintFiles1 + " archivos en: " + dirF1.getAbsolutePath()
            + " con " + nrOfPlaintFiles2 + " en: " + dirF2.getAbsolutePath()
            + " | ";
        compDel.frame.setTitle(progressInfoTitle);
    }

    private static long computeNrOfPlaintFiles(final File[] files) {
        return Arrays.stream(files).filter(Objects::nonNull).filter(File::isFile).count();
    }

    /**
     * An activity report message.
     * @param repetidos the number of duplicate files found.
     * @param desperdicio the space occupied by the duplicate files found.
     * @param totalComparaciones how many comparisons where made.
     * @param cron used to show the time elapsed.
     * @param cronTotal used to show the total time elapsed.
     */
    private void sayGoodby(final long repetidos, final long desperdicio,
                           final long totalComparaciones, final int cron, final int cronTotal) {
        err.println("totalComparaciones:\t " + totalComparaciones);
        err.println("Archivos repetidos: " + repetidos);
        err.println("Desperdicio: " + NumberFormat.getInstance().format(desperdicio));
        err.println("Terminando:\t " + new Date() + "\nTiempo real: "
				+ Chrono.timeDetail(Chrono.elapsed(cron)));
        err.println("Terminando:\t " + new Date() 
				+ "\nTiempo real TOTAL : " + Chrono.timeDetail(Chrono.elapsed(cronTotal)));
        err.println("That's all folks");
        compDel.frame.setTitle("Finalizado: " + getClass().getSimpleName());
    }

	void setExcudeList(final String text) {
		excludeList = text.split("\\|");
	}

	public void init(final String firstDir, final String secondDir,
			final String undoFileLocation) {
		dirF1 = new File(firstDir);
		dirF2 = new File(secondDir);
		undoFileManager.setUndoFileLocation(undoFileLocation);
        err.println("Locating undo files in: " + undoFileLocation);
        err.println();

		// ???? buscar los absolutes
		dir1IsDirty = !dirF1.equals(oldDirF1);

		// ???? buscar los absolutes
		dir2IsDirty = !dirF2.equals(oldDirF2);
		oldDirF1 = dirF1;
		oldDirF2 = dirF2;

		err.println("Comparando los archivos de " + dirF1.getAbsolutePath()
				+ "\ncon                   los de                 " + dirF2.getAbsolutePath());
	}

	void resetFields() {
		dir1IsDirty = true;
		dir2IsDirty = true;
		oldDirF1 = null;
		oldDirF2 = null;
	}

    /**
     * Si se llaman igual, tienen el mismo tamaño, etc. hacer la comparacion y el borrado sin el digest.
     * @param files1 first file to compare.
     * @param files2 second file to compare.
     * @param totals to keep track of work done.
     */
    private void cleanFilesWithoutDigest(final File[] files1, final File[] files2, final Totals totals) {
        if (files1 != null && files2 != null) {
            out.println("Heuristics: deleting before digests.");
            int count = 0;
            for (final File file1 : files1) {
                updateTitle(count++);
                for (int ii = 0; ii < files2.length; ii++) {
                    while (dozing) {
                       sleepEinBisschen(TIME_2_SLEEP);
                   }
                    final File file2 = files2[ii];
                    if (file1.equals(file2)) {
                        continue;
                    }
                    final long deletedLength = file1.length();
                    if (file1.getName().equals(file2.getName())
                            && deletedLength == file2.length()) {
                        totals.totalComparaciones++;
                        if (FileTools.compareContents(file1, file2)) {
                            final boolean deleted = file1.delete();
                            out.println(String.format("Deletion of %s (equal to %s): %s", file1, file2, deleted ? "OK" : "KO"));
                            if (deleted) {
                                totals.desperdicio += deletedLength;
                                totals.repetidos++;
                                if (compDel.frame.optionPanel.undoBox.isSelected()) {
                                    undoFileManager.add2UndoFile(file1, files2[ii]);
                               }
                               files2[ii] = FileTools.NULL_FILE;
                            }
                            break;
                        }
                    }
                }
            }
            err.println("In cleanFilesWithoutDigest(): Total comparaciones: " + totals.totalComparaciones);
            err.println("In cleanFilesWithoutDigest(): Archivos repetidos: " + totals.repetidos);
            err.println("In cleanFilesWithoutDigest(): Desperdicio: " + NumberFormat.getInstance().format(totals.desperdicio));
        }
    }

    private void updateTitle(final int count) {
        // El cambio de titulo debe ser costoso. hacerlo solo de vez en cuando
        if (count % 100 == 0) {
            compDel.frame.setTitle(progressInfoTitle + count);
        }
    }
}