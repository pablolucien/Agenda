package org.pclg.filelist;

import org.pclg.tools.FileComparator;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * SortSelector <BR>
 *
 * @author El Coyote Cojo
 * @version 2005.jun.12
 */
class SortSelector {

	private FileComparator.SortCriterium lastSortOption;

	private final Component parent;

	SortSelector(final Component parent) {
		this.parent = parent;
	}

	FileComparator.SortCriterium getSortOption() {
        final Object selectedValue = JOptionPane.showInputDialog(parent,
            "Escoja el criterio", "Ordenar", JOptionPane.QUESTION_MESSAGE,
            null, FileComparator.SortCriterium.values(), lastSortOption);
        lastSortOption = (FileComparator.SortCriterium) selectedValue;
        return lastSortOption;
	}
}
