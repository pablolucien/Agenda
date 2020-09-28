package org.pclg.compdel;

import org.apache.logging.log4j.Logger;
import org.pclg.gui.FancyButtonPanel;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;
import org.pclg.xtras.Colortable;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Pablo
 * @since 24/01/16 11:13
 */
final class ControlPanel {
    enum ACTION {
        NOOP, CLEAR, REFRESH, STOP, SAVE, EXIT, CLEAN, START, PAUSE, RESUME
	}

	enum Status {
		STARTED, PAUSED, STOPPED
	}

	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final String START = "Start";
	private static final String PAUSE = "Pause";
	private static final String RESUME = "Resume";
	private static final String EXIT = "Exit";
	private final ActionManager actionManager = new ActionManager();
	private final JButton clearBt = GUITools.addButton(actionManager, null, "Clear", "Limpia la salida");
	private final JButton refreshBt = GUITools.addButton(actionManager, null, "Refresh", "Borra los digest de la memoria");
	private final JButton controlBt = GUITools.addButton(actionManager, null, START);
	private final JButton stopBt = GUITools.addButton(actionManager, null, "Stop");
	private final JButton saveBt = GUITools.addButton(actionManager, null, "Save result");

	/** Elimina de la bd lo que no est� en el disco */
	private final JButton cleanDigestDBBt = GUITools.addButton(actionManager,
		null, "Clean Digest DB", "Borra de la DB lo que no est� en disco");

	private final JButton exitBt = GUITools.addButton(actionManager, null, EXIT,
		"Das Ende der Anwendung");

    private final FancyButtonPanel buttonPanel = new FancyButtonPanel(clearBt, refreshBt, controlBt,
    			stopBt, saveBt, cleanDigestDBBt, exitBt);
	private final CompDelFrame.CallbackListener callbackListener;

	ControlPanel(final CompDelFrame.CallbackListener callbackListener) {
		this.callbackListener = callbackListener;
	}

    public JPanel getButtonPanel() {
        return buttonPanel;
    }

    private class ActionManager implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final ACTION action;
			if (event.getSource() == controlBt) {
				if (controlBt.getText().equals(START)) {
					setStatus(Status.STARTED);
					action =ACTION.START;
				} else if (controlBt.getText().equals(PAUSE)) {
					setStatus(Status.PAUSED);
					action =ACTION.PAUSE;
				} else if (controlBt.getText().equals(RESUME)) {
					setStatus(Status.STARTED);
					action =ACTION.RESUME;
				} else {
					 action = ACTION.NOOP;
				}
			} else if (event.getSource() == clearBt) {
				action =ACTION.CLEAR;
			} else if (event.getSource() == refreshBt) {
				action =ACTION.REFRESH;
			} else if (event.getSource() == stopBt) {
				setStatus(Status.STOPPED);
				action =ACTION.STOP;
			} else if (event.getSource() == exitBt) {
				action =ACTION.EXIT;
			} else if (event.getSource() == saveBt) {
				action =ACTION.SAVE;
			} else if (event.getSource() == cleanDigestDBBt) {
				action =ACTION.CLEAN;
			} else {
				action = ACTION.NOOP;
			}
			
			LOGGER.debug("To execute: " +  action);

			callbackListener.doAction(action);
		}
	}

	public void setStatus(final Status status) {
		switch (status) {
		case STARTED:
			controlBt.setText(PAUSE);
			controlBt.setForeground(Colortable.getColor("green yellow"));
			controlBt.setToolTipText("Detener momentaneamente el proceso");
//			controlBt.setToolTipText(PropertiesHelper
//				.getStringFromProperties(i18nProperties, "Pause.ToolTipText"));
			break;
		case PAUSED:
			controlBt.setText(RESUME);
			controlBt.setForeground(Colortable.getColor("aquamarine"));
			controlBt.setToolTipText("Continuar el proceso");
			break;
		case STOPPED:
			controlBt.setText(START);
			controlBt.setToolTipText(START);
			controlBt.setForeground(Colortable.getColor("dark olive green"));
			break;
		default:
			throw new IllegalStateException("Este estado es imposible: " + status);
		}
	}
}
