package org.pclg.gui;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.EmptyArrays;
import org.pclg.tools.GUITools;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public final class InfoPanel {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private final JLabel label = new JLabel();
	private final JDialog workingDialog;
	
	private InfoPanel() {
		LOGGER.debug(LoggerFactory.ENTER_METHOD);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		workingDialog = new JOptionPane(label,
			JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
            EmptyArrays.EMPTY_OBJECT_ARRAY).createDialog("");
		workingDialog.setModal(false);
		LOGGER.debug(LoggerFactory.EXIT_METHOD);
	}

	public InfoPanel(final String title, final String msg) {
		this();
		setMessage(msg);
		setTitle(title);
	}

	void setTitle(final String title) {
		LOGGER.debug(LoggerFactory.ENTER_METHOD);
		workingDialog.setTitle(title);
		LOGGER.debug(LoggerFactory.EXIT_METHOD);
	}

	public void setMessage(final String msg) {
		LOGGER.debug(LoggerFactory.ENTER_METHOD);
		label.setText(msg);
		workingDialog.pack();
		GUITools.center(workingDialog, null);
		LOGGER.debug(LoggerFactory.EXIT_METHOD);
	}

	public void setVisible(final boolean visible) {
		LOGGER.debug(LoggerFactory.ENTER_METHOD);
		workingDialog.setVisible(visible);
		GUITools.center(workingDialog, null);
		LOGGER.debug(LoggerFactory.EXIT_METHOD);
	}
}
