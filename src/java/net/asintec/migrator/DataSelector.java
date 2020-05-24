// ******************************** package
package net.asintec.migrator;

import org.pclg.gui.JLabeledField;
import org.pclg.tools.GUITools;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
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
class DataSelector extends JDialog implements ActionListener {
    private static final long serialVersionUID = -2212515776176547591L;
    /** Los paneles a mostrar */
	private final String[] tabs = {"Oracle", "ODBC", "MySQL", "Texto", "Otro"};

	/** Los titulos de los paneles a mostrar */
	private final String[] titles = {"Informacion de conexion", null, null, "Archivos de texto", "Otro"};

	/** Los campos de cada panel (un set por cada panel, puede ser null) */
	private final String[][] fields = {
			{"Usuario", "Password", GUITools.SEPARATOR, "Direccion IP", "Puerto", "Database"},
			{"Usuario", "Password", "DSN", "DBQ"},
			null,
			{"Archivo de entrada", GUITools.SEPARATOR, "Directorio"},
			null,
		};

	JLabeledField userField = new JLabeledField("Usuario", "configuracion");
	JLabeledField passField = new JLabeledField("Password", "configuracion");
	JLabeledField databaseField = new JLabeledField("Database", "orcl");
	private final JPanel buttonPanel = new JPanel((new GridLayout(0, 2)));
	private final JButton acceptBt   = GUITools.addButton(this, buttonPanel, "Aceptar");
	private JButton cancelBt   = GUITools.addButton(this, buttonPanel, "Cancelar");
	private boolean dialogAccepted;
	private final JTabbedPane tabbedPane;

	public DataSelector(final Frame parent) {
		super(parent, "Seleccionar Base de datos", true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent ev) {
				dialogAccepted = false;
				setVisible(false);
			}
		});

		tabbedPane = GUITools.createTabbedPanel(tabs, titles, fields);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(acceptBt);
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
