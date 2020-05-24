package org.pclg.alter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author El Coyote Cojo
 * @since 6/01/18 12:45
 */
final class ListenerHelper {
	private final ActionListener listener;

	ListenerHelper(final ActionListener listener) {
		this.listener = listener;
	}

	void notifyEvent(final ActionEvent event, final AlterCommand command) {
		event.setSource(command);
		listener.actionPerformed(event);
	}
}
