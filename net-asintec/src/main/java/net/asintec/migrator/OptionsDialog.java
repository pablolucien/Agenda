// ******************************** package
package net.asintec.migrator;

import org.pclg.gui.JLabeledField;
import org.pclg.tools.GUITools;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
	Selecciona las opciones de configuracion
	@version 1.00
	@author El Coyote Cojo
*/

class OptionsDialog extends JDialog implements ActionListener {
	// Los tipos de comnponents a usar
	private static final int TEXT = 1;
	private static final int CHECK = 2;
	private static final int RADIO = 3;
    private static final long serialVersionUID = -7668783554272008960L;

    /** Los titulos de las opciones */
	private final String[] titulos = {"Limpiar la tabla antes", "Sobreescribir los registros", "Poner en NULL los campos '<VACIO>'", "Editor de textos", "etc"};

	/** Los tipos de las opciones */
	private final int[] tipos = {      CHECK,                    CHECK,                         RADIO,                                TEXT,               RADIO};

	/** Los valores iniciales de las opciones */
	private final String[] initial = {"true",                   "false",                       "false",                              "vi",               "true"};

	private final Box optionsPanel = new Box(BoxLayout.Y_AXIS);

	private final JPanel buttonPanel = new JPanel((new GridLayout(0, 2)));
	private final JButton acceptBt   = GUITools.addButton(this, buttonPanel, "Aceptar");
	private JButton cancelBt   = GUITools.addButton(this, buttonPanel, "Cancelar");
	private boolean dialogAccepted;

	public OptionsDialog(final Frame parent) {
		super(parent, "Opciones...", true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent ev) {
				dialogAccepted = false;
				setVisible(false);
			}
		});

		final JLabel title = new JLabel("Estas (y otras) opciones sería guay tenerlas, pero no estan implementadas");
		createPanel(titulos, tipos, initial);
		getContentPane().add(title, BorderLayout.NORTH);
		getContentPane().add(optionsPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(acceptBt);
		pack();
		setLocationRelativeTo(parent);
	}

	/**
		Crea las opciones
	*/
	void createPanel(final String[] titulos, final int[] tipos,
		final String[] initial) {
		for(int i = 0; i < titulos.length; i++) {
			JComponent c = null;
			switch(tipos[i]) {
			case TEXT:
				c = new JLabeledField(titulos[i], initial[i]);
				break;
			case CHECK:
				c = new JCheckBox(titulos[i], null, MigratorArbeiter.isAffimation(initial[i]));
				break;
			case RADIO:
				c = new JRadioButton(titulos[i], null, MigratorArbeiter.isAffimation(initial[i]));
				break;
			default:
				throw new IllegalArgumentException("Tipo de componente invalido: " + tipos[i]);
			}
			optionsPanel.add(c);
		}
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
