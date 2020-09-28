package org.pclg.agenda.gui;

import org.apache.logging.log4j.Logger;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.entities.TipoTelefono;
import org.pclg.gui.I18NManager;
import org.pclg.gui.TextFieldLimiter;
import org.pclg.log.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Pablo
 * @since 16/03/14 19:50
 */
public class PhoneTypeMgtPanel extends JPanel {
    /** */
	private static final long serialVersionUID = -4313698848409803126L;
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    public static final int TIPO_TELEFONO_NAME_LEN = 20;
    private final JList<TipoTelefono> listaTipoTelefonos = new JList<>();
	private final AgendaDb agendaDb; // FIXME: no se si esto debe estar aqu� o en TipoTelefono o d�nde
	private final JDialog colorChooseDialog;
	private final JColorChooser tcc;

	public PhoneTypeMgtPanel(final Properties properties,
			final AgendaDb agendaDb) {
		super(new BorderLayout());
		this.agendaDb = agendaDb;
		tcc = new JColorChooser();
        tcc.setBorder(BorderFactory.createTitledBorder("Choose Text Color"));
        colorChooseDialog = JColorChooser.createDialog(this, "Escoja el color", true, tcc, null, null);
		populateList();
		add(new JScrollPane(listaTipoTelefonos), BorderLayout.CENTER);
		add(createAddPanel(properties), BorderLayout.SOUTH);
	}

	private void populateList() {
		listaTipoTelefonos.setModel(new PhoneTypeListModel(TipoTelefono.getValues()));
	}

	private JPanel createAddPanel(final Properties properties) {
		final JPanel panel = new JPanel(new FlowLayout());
		panel.setBackground(Color.green);
		panel.setMinimumSize(new Dimension(100, 25));
		final JTextField nameField = new JTextField();
		nameField.setBackground(Color.lightGray);
		nameField.setDocument(new TextFieldLimiter(TIPO_TELEFONO_NAME_LEN));
		nameField.setColumns(TIPO_TELEFONO_NAME_LEN);
		panel.add(nameField);

		final I18NManager i18nManager = I18NManager.getInstance(properties);
		final JButton colorChooser = new JButton();
		panel.add(i18nManager.configureI18NComponent(colorChooser, "AgendaGUI.colorButton"));
		colorChooser.setToolTipText("Elecci�n del color");
		colorChooser.addActionListener(e -> colorChooseDialog.setVisible(true));
		panel.add(colorChooser);
        tcc.getSelectionModel().addChangeListener(
			e -> colorChooser.setBackground(tcc.getColor()));
		final JLabel message = new JLabel();
		message.setForeground(Color.red);
		final JButton button = new JButton();
		panel.add(i18nManager.configureI18NComponent(button, "AgendaGUI.addButton"));
		button.addActionListener(e -> {
			message.setText("");
			final String name = nameField.getText();
			if (name.trim().length() == 0) {
				final String msg= "Tratando de agregar TipoTelefono en blanco. No hago nada";
				message.setText(msg);
				LOGGER.warn(msg);
				return;
			}
			for (TipoTelefono tipoTelefono : TipoTelefono.getValues()) {
				if (name.equalsIgnoreCase(tipoTelefono.getNombre())) {
					final String msg = String.format("Tratando de agregar TipoTelefono repetido : %s. No hago nada.", name);
					message.setText(msg);
					LOGGER.warn(msg);
					return;
				}
			}
			// FIXME: Revisar todo de aqui pa'bajo (y pa'rriba)
			LOGGER.debug("Agregando TipoTelefono : " + name);
			LOGGER.debug("Antes : " + TipoTelefono.getValues());
			try {
				final Color color = tcc.getColor();
				final int rgb = color == null ? 0 : color.getRGB();
				TipoTelefono.add(new TipoTelefono(
					agendaDb.addTipoTelefono(name, rgb), name, rgb));
				populateList();
                listaTipoTelefonos.ensureIndexIsVisible(listaTipoTelefonos.getModel().getSize() - 1);
                nameField.setText("");
				LOGGER.debug("Despues : " + TipoTelefono.getValues());
			} catch (SQLException ex) {
				LOGGER.error(LoggerFactory.ERROR_TAG, ex);
			}
		});
		final JPanel outerPane = new JPanel(new BorderLayout());
		outerPane.add(panel, BorderLayout.CENTER);
		outerPane.add(message, BorderLayout.SOUTH);
		return outerPane;
	}
}
