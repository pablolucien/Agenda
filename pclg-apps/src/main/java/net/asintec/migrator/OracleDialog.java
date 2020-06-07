// ******************************** package
package net.asintec.migrator;

import org.pclg.gui.JLabeledField;
import org.pclg.tools.GUITools;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
	Selecciona un driver jdbc y una conexion
	@version 1.00
	@author El Coyote Cojo
*/
class OracleDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -7235297827438561803L;
    final JLabeledField userField = new JLabeledField("Usuario", "telespace");
	final JLabeledField passField = new JLabeledField("Password", "tXj208pM");
	private final JPanel buttonPanel = new JPanel((new GridLayout(0, 2)));
	private final JButton acceptBt   = GUITools.addButton(this, buttonPanel, "Aceptar");
	private JButton cancelBt   = GUITools.addButton(this, buttonPanel, "Cancelar");
	private boolean dialogAccepted;

	public OracleDialog(final Frame parent) {
		super(parent, true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent ev) {
				dialogAccepted = false;
				setVisible(false);
			}
		});

		getContentPane().setLayout(new GridLayout(0, 1));
		getContentPane().add(new JLabel("Informacion de conexion"));
		getContentPane().add(userField);
		getContentPane().add(passField);
		getContentPane().add(buttonPanel);
		pack();
		setLocationRelativeTo(parent);
	}

	public boolean accepted() {
		return(dialogAccepted);
	}

	// implements ActionListener
	@Override
	public void actionPerformed(final ActionEvent event) {
		setVisible(false);
		if(event.getSource() == acceptBt) {
			dialogAccepted = true;
		}
		else {
			dialogAccepted = false;
		}
	}
}
