package org.pclg.dictionary;

import org.pclg.tools.StringTools;

import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.List;


final class DictionaryListModel extends AbstractListModel
{
    private static final long serialVersionUID = -8784515952403091468L;
    private final List delegate = new ArrayList();

	/* (sin Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public final int getSize() {
		return delegate.size();
	}

	/* (sin Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public final Object getElementAt(final int index) {
		return delegate.get(index);
	}

	/**
	 * @param index
	 * @param element
	 */
	public final void add(final int index, final Object element) {
		delegate.add(index, element);
		fireIntervalAdded(this, index, index);
	}

	/**
	 * @param index
	 * @param element
	 */
	public final void addBatch(final int index, final Object element) {
		delegate.add(index, element);
	}

	/**
	 *
	 */
	public final void refresh() {
		fireIntervalAdded(this, 0, getSize());
	}

	/**
		Encuentra el indice del primer elemento que comienza (como String) con
		el parámetros.
	*/
	public final int findFirst(final String element) {
		final String text = element.toLowerCase();
		for (int ii = 0, size = delegate.size(); ii < size; ii++) {
			final String str = delegate.get(ii).toString();
			if (startsWith(str.toLowerCase(), text)) {
				return ii;
			}
		}
		return -1;
	}

	/**
	 * @param string
	 * @param prefix
	 * @return
	 */
	private boolean startsWith(final String string, final String prefix) {
		final String string1 = StringTools.eliminateTildes(string).toLowerCase();
		final String prefix1 = StringTools.eliminateTildes(prefix).toLowerCase();
		return string1.startsWith(prefix1);
	}

	public void clear() {
		delegate.clear();
	}
}
