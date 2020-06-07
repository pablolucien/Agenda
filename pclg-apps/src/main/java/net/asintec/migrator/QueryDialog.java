package net.asintec.migrator;

import org.pclg.tools.GUITools;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
	Selecciona un driver jdbc y una conexion
	@version 1.00
	@author El Coyote Cojo
*/
class QueryDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -6019969472824149143L;
    private boolean accepted;
	private final JTextArea text = new JTextArea(20, 40);
	public String connectString;
	private final JPanel buttonPanel = new JPanel((new GridLayout(0, 2)));
	private final JButton loadBt        = GUITools.addButton(this, buttonPanel, "Cargar");
	private final JButton saveBt        = GUITools.addButton(this, buttonPanel, "Guardar");
	private final JButton acceptBt      = GUITools.addButton(this, buttonPanel, "Aceptar");
	private final JButton cancelBt      = GUITools.addButton(this, buttonPanel, "Cancelar");
	private final JFileChooser fileChooser = new JFileChooser(new File(".").getAbsolutePath());
	private final FileFilter fileFilter = new FileFilter() {
							public boolean accept(final File f) {
								if(f.isDirectory()) {
									return (true);
								}
								if(f.getName().toLowerCase().endsWith(".sql")) {
									return (true);
								}
								return(false);
							}

							public String getDescription() {
								return("Querys almacenadas (*.sql)");
							}
						};

	


	public QueryDialog(final Frame parent) {
		super(parent, "Query ad hoc", true);
		addWindowListener(new WindowAdapter() {
					public void windowClosing(final WindowEvent ev) {
						dispose();
						setVisible(false);
					}
				}
		);
		getContentPane().add(text, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		pack();
		GUITools.center(this, parent);
	}

	@SuppressWarnings({"deprecation"})
	public void show() {
		super.setVisible(true);
		accepted = false;
	}
	
	public void setText(final String txt) {
		text.setText(txt);
	}

	public String getText() {
		return(text.getText());
	}

	/** Indica si el dialogo fue aceptado o cancelado */
	public boolean isAccepted() {
		return(accepted);
	}

	// implements ActionListener
	public void actionPerformed(final ActionEvent event) {
		if(event.getSource() == cancelBt) {
			accepted = false;
			setVisible(false);
		}
		else if(event.getSource() == acceptBt) {
			accepted = true;
			setVisible(false);
		}
		else if(event.getSource() == loadBt) {
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setFileFilter(fileFilter);
			if(fileChooser.showDialog(this, "Abrir") == JFileChooser.APPROVE_OPTION) {
			}
		}
		else if(event.getSource() == saveBt) {
			fileChooser.setFileFilter(fileFilter);
			if(fileChooser.showDialog(this, "Guardar como ...") == JFileChooser.APPROVE_OPTION) {
			}
		}
	}
}
