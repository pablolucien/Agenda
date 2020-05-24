package org.pclg.agenda.gui;

import org.apache.log4j.Logger;
import org.pclg.agenda.entities.TipoTelefono;
import org.pclg.log.LoggerFactory;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import java.util.Collection;

/**
* @author Pablo
* @since 16/03/14 20:33
*/
class PhoneTypeListModel implements ListModel<TipoTelefono> {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private final TipoTelefono[] tipoTelefonos;

	PhoneTypeListModel(final Collection<TipoTelefono> tipoTelefonos) {
		LOGGER.debug(" PhoneTypeListModel(" + tipoTelefonos + ")");
		this.tipoTelefonos = new TipoTelefono[tipoTelefonos.size()];
		tipoTelefonos.toArray(this.tipoTelefonos);
	}

	@Override
	public int getSize() {
		//LOGGER.debug("tipoTelefonos.length = " + tipoTelefonos.length);
		return tipoTelefonos.length;
	}

	@Override
	public TipoTelefono getElementAt(final int index) {
		//LOGGER.debug(" getElementAt(" + index + ")");
		return tipoTelefonos[index];
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
