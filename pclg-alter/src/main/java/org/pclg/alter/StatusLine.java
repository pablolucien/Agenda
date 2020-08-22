package org.pclg.alter;

import org.pclg.tools.Constants;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

/** A status line.
 * @author El Coyote Cojo.
 * @version 1.0
 * @since 30-nov-2004
 */
final class StatusLine extends JLabel {
    /** A label. */
    private static final String COLON_SPACE = ": ";
    /** A label. */
    private static final String HYPHEN = " - ";
    /** A label. */
    private static final String DIRS_LABEL = "Directorios";
    /** A label. */
    private static final String FILES_LABEL = "Archivos";
    /** A label. */
    private static final String OTHER_LABEL = "Otros";
    /** A label. */
    private static final String SELECTED_LABEL = "Selected";
    private static final long serialVersionUID = -6930619905553786153L;

    /**
     * Creates a <code>StatusLine</code> instance .
     */
	StatusLine() {
        setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEtchedBorder()));
    }

    /**
     * Updates itself.
     * @param dirQty the quantity of directory files.
     * @param fileQty the quantity of plain files.
     * @param otherQty the quantity of other files.
     * @param total the total of selected files.
     */
    public void updateStatus(final int dirQty, final int fileQty, final int otherQty, final int total) {
        setText(new StringBuilder(Constants.BUFFER_SIZE)
                .append(DIRS_LABEL).append(COLON_SPACE).append(dirQty).append(HYPHEN)
                .append(FILES_LABEL).append(COLON_SPACE).append(fileQty).append(HYPHEN)
                .append(OTHER_LABEL).append(COLON_SPACE).append(otherQty).append(HYPHEN)
                .append(SELECTED_LABEL).append(COLON_SPACE).append(total)
                .toString());
    }
}
