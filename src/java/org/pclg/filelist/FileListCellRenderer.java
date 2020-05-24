// ******************************** package
package org.pclg.filelist;

// ******************************** imports

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Color;
import java.awt.Component;
import java.io.File;

/**
 * Un "renderer" para una lista de archivos.
 * @author El Coyote Cojo.
 * @version 1.0.
 */
final class FileListCellRenderer extends DefaultListCellRenderer {
    /** El color en que se muestran los archivos normales. */
    private static final Color FILE_COLOR = Color.darkGray;

    /** El color en que se muestran los directorios. */
    private static final Color DIR_COLOR = Color.blue;

    /** El color en que se muestran los archivos desconocidos. */
    private static final Color OTHER_COLOR = Color.red;

    /** El color en que se muestran los archivos eliminados. */
    private static final Color DEAD_COLOR = Color.pink;

    /** El color en que se muestran el directorio padre. */
    private static final Color PARENT_COLOR = Color.magenta;

    /** El nombre que se muestra del directorio padre. */
    private static final String PARENT_DIR = "..";
    private static final long serialVersionUID = 6817406722059861526L;

    //private static final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

    @Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
            final boolean isSelected, final boolean cellHasFocus) {
        if (value instanceof File) {
            final File file = (File) value;
            setText(file.getName());

            if (file.isFile()) {
                setForeground(FILE_COLOR);
            } else if (file.isDirectory()) {
                if (file.getName().equals(PARENT_DIR)) {
                    setForeground(PARENT_COLOR);
                } else {
                    setForeground(DIR_COLOR);
                }
                //setCursor(handCursor);
            } else if (!file.exists()) {
                setForeground(DEAD_COLOR);
            } else {
                setForeground(OTHER_COLOR);
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
            } else {
                setBackground(list.getBackground());
            }

            setEnabled(list.isEnabled());
            setFont(list.getFont());
        }

        return this;
    }
}
