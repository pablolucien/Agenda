package org.pclg.agenda.gui;

import org.apache.log4j.Logger;
import org.pclg.agenda.entities.Pais;
import org.pclg.log.LoggerFactory;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import java.util.Collection;

/**
* @author Pablo
* @since 16/03/14 20:33
*/
public class ListPaisesListModel implements ListModel<Pais> {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private final Pais[] paises;

	public ListPaisesListModel(final Collection<Pais> paises) {
		LOGGER.debug(" ListPaisesListModel(" + paises + ")");
		this.paises = new Pais[paises.size()];
		paises.toArray(this.paises);
	}

	@Override
	public int getSize() {
		//LOGGER.debug("paises.length = " + paises.length);
		return paises.length;
	}

	@Override
	public Pais getElementAt(final int index) {
		//LOGGER.debug(" getElementAt(" + index + ")");
		return paises[index];
	}

	@Override
	public void addListDataListener(final ListDataListener l) {
		LOGGER.debug("");
	}

	@Override
	public void removeListDataListener(final ListDataListener l) {
		LOGGER.debug("");
	}
}
