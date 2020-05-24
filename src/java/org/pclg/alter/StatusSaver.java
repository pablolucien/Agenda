package org.pclg.alter;

import org.pclg.gui.JLabeledField;

/**
 * Guarda las selecciones del usuario.
 * @author El Coyote Cojo.
 * @version 1.0
 * @since 30-nov-2004
 */
final class StatusSaver {
    /** The fields whose status is to be saved and restored. */
    private final JLabeledField[] fields;

    /** The content of the fields. */
    private final String[] texts;

    /**
     * Creates a new StatusSaver.
     * @param fields The fields whose status is to be saved and restored.
     */
    StatusSaver(final JLabeledField[] fields) {
        this.fields = fields;
        texts = new String[fields.length];
    }

    /** Saves the contents of the fields. */
    void save() {
        for (int ii = 0; ii < fields.length; ii++) {
            texts[ii] = fields[ii].getText();
        }
    }

    /** Resoteres the contents of the fields. */
    void restore() {
        for (int ii = 0; ii < fields.length; ii++) {
            fields[ii].setText(texts[ii]);
        }
    }
}
