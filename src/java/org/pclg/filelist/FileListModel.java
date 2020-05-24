// ******************************** package
package org.pclg.filelist;

// ******************************** imports

import org.pclg.tools.FileComparator;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import java.awt.Window;
import java.io.File;
import java.util.Arrays;

/**
 * Un "model" para una lista de archivos.
 *
 * @author El Coyote Cojo.
 * @version 1.0.
 */
public final class FileListModel extends DefaultListModel<File> {
    private static final long serialVersionUID = -1395662735865295185L;
    /** Este flechazo es para que los mensajes no queden detras de la ventana de la aplicacion. */
    private Window mainWindow;

    /** La cantidad de archivos normales. */
    private int fileNr;

    /** La cantidad de directorios. */
    private int dirNr;

    /** La cantidad de otros tipos de archivos. */
    private int otherNr;

    /** Este comparador es el que utilizaremos para ordenar los archivos. */
//    private final ConfigurableComparator<File> fileComparator = new FileComparator();
    private final FileComparator fileComparator = new FileComparator();

    /**
     *
     * @param parent
     * @param files
     */
    public void setListItems(final File parent, final File[] files) {
        if (parent == null) {
            JOptionPane.showMessageDialog(mainWindow,
                "FileListModel.setListItems(): parent == null", "Alerta",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (files == null) {
            JOptionPane.showMessageDialog(mainWindow,
                "FileListModel.setListItems(): files == null", "Alerta",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        removeAllElements();
        fileNr = otherNr = dirNr = 0;
        addElement(new File(parent, ".."));

        Arrays.sort(files, fileComparator);

        for (final File file : files) {
            addElement(file);
            if (!file.exists()) {
                otherNr++;
            } else if (file.isFile()) {
                fileNr++;
            } else if (file.isDirectory()) {
                dirNr++;
            } else {
                otherNr++;
            }
        }
    }

    /**
     * Establece el criterio de ordenacione de los archivis.
     * @param critery el criterio de ordenacione de los archivis.
     * @since 2004.08.19
     */
    final void setSortCritery(final FileComparator.SortCriterium critery) {
        fileComparator.setSortCritery(critery);
    }

    /**
     * Devuelve la cantidad de directorios que hay en la lista.
     * @return  la cantidad de directorios que hay en la lista.
     */
    public int dirQty() {
        return dirNr;
    }

    /**
     * Devuelve la cantidad de archivos normales que hay en la lista.
     * @return  la cantidad de archivos que hay en la lista.
     */
    public int fileQty() {
        return fileNr;
    }

    /**
     * Devuelve la cantidad de otro tipo de archivos que hay en la lista.
     * @return  la cantidad de otro tipo de archivos que hay en la lista.
     */
    public int otherQty() {
        return otherNr;
    }

    /** Este flechazo es para que los mensajes no queden detras de la ventana de la aplicacion.
     *
     * @param wnd La ventana principal de la aplicacion.
     */
    public void setAppWindow(final Window wnd) {
        mainWindow = wnd;
    }
}
