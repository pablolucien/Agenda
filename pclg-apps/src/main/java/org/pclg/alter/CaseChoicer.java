package org.pclg.alter;

import org.pclg.tools.StringTools;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import java.util.function.Function;

/**
 * Choices the case of the files and provides the transformation method.
 * @author El Coyote Cojo.
 * @version 1.0
 * @since 30-nov-2004
 */
final class CaseChoicer extends JComboBox<CaseChoicer.Option> {
    private static final long serialVersionUID = 8245090303492014503L;

    public enum Option {
        LOWER_EXT("Lower ext.", filename -> {
            final int indexOfDot = filename.lastIndexOf('.');
            return indexOfDot == -1 ? filename : filename.substring(0, indexOfDot) + filename.substring(indexOfDot).toLowerCase();
        }),
        LOWER_BASE("Lower base", filename -> {
            final int indexOfDot = filename.lastIndexOf('.');
            return indexOfDot == -1 ? filename : filename.substring(0, indexOfDot).toLowerCase() + filename.substring(indexOfDot);
        }),
        LOWER_ALL("Lower all", String::toLowerCase),
        CAPITALIZED("Caps", StringTools::capitalize),
        UPPER_FIRST("Upper 1st.", filename -> Character.toUpperCase(filename.charAt(0)) + filename.substring(1)),
        UPPER_FIRST_THEN_LOWER("Upper 1st. then lower", filename -> Character.toUpperCase(filename.charAt(0)) + filename.substring(1).toLowerCase());

        private final String title;
        private final Function<String, String> function;

        Option(final String title, final Function<String, String> function) {
            this.title = title;
            this.function = function;
        }

        @Override
        public String toString() {
            return title;
        }

        public String modify(final String name) {
            return function.apply(name);
        }
    }
    /**
     * Creates a <code>CaseChoicer</code> .
     */
    CaseChoicer() {
        setModel(new DefaultComboBoxModel<>(Option.values()));
    }
}
