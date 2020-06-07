package org.pclg.agenda.gui;

import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.entities.Pais;
import org.pclg.gui.I18NManager;
import org.pclg.gui.TextFieldLimiter;
import org.pclg.log.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Properties;

import static org.pclg.tools.StringTools.equalEmptyOrBlank;
import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * @author Pablo
 * @since 16/03/14 19:50
 */
public final class CountryMgtPanel extends JPanel {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = -1296845584355689930L;
    private final JList<Pais> listaPaises = new JList<>();
	private final CountryPanel countryPanel;
	private final AgendaDb agendaDb; // FIXME: no se si esto debe estar aquí o en Pais o dónde

	public CountryMgtPanel(final Properties properties, final AgendaDb agendaDb) {
		super(new BorderLayout());
		this.agendaDb = agendaDb;
		populateList();
		countryPanel = new CountryPanel(properties);
		listaPaises.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent event) {
				if (event.getClickCount() == 2) {
					countryPanel.setPais(listaPaises.getSelectedValue());
				}
			}
		});
		add(new JScrollPane(listaPaises), BorderLayout.CENTER);
		add(countryPanel, BorderLayout.SOUTH);
	}

	private void populateList() {
		listaPaises.setModel(new ListPaisesListModel(Pais.getPaises()));
	}

	public void accept() {
	}

	public void cancel() {
		LOGGER.debug("cancel()");
	}

	private class CountryPanel extends JPanel {
		private static final int STANDARD_FIELD_HEIGHT = 25;
		private static final long serialVersionUID = -8820380264911047178L;
		private final JTextField codeField = new JTextField();
		private final JTextField nameField = new JTextField();
		private final JTextField phoneMaskField = new JTextField();
		private final JLabel message = new JLabel();

		private CountryPanel(final Properties properties) {
			super(new BorderLayout());
			final JPanel panel = new JPanel(new FlowLayout());
			panel.setBackground(Color.green);
			panel.setMinimumSize(new Dimension(100, STANDARD_FIELD_HEIGHT));
			codeField.setBackground(Color.red);
			codeField.setDocument(new TextFieldLimiter(3));
			codeField.setColumns(3);
			codeField.setBounds(0, 0, 10, STANDARD_FIELD_HEIGHT);
			panel.add(codeField);

			nameField.setBackground(Color.lightGray);
			nameField.setDocument(new TextFieldLimiter(40));
			nameField.setColumns(40);
			nameField.setBounds(10, 0, 40, STANDARD_FIELD_HEIGHT);
			panel.add(nameField);

//			phoneMaskField.setBackground(Color.lightGray);
			phoneMaskField.setDocument(new TextFieldLimiter(20));
			phoneMaskField.setColumns(20);
			phoneMaskField.setBounds(10, 0, 20, STANDARD_FIELD_HEIGHT);
			panel.add(phoneMaskField);

			message.setForeground(Color.red);
			final I18NManager i18nManager = I18NManager.getInstance(properties);
			final JButton button = new JButton();
			panel.add(i18nManager.configureI18NComponent(button, "AgendaGUI.addButton"),
				BorderLayout.EAST);
			button.addActionListener(e -> {
				message.setText("");
				final String code = codeField.getText();
				if (isEmptyOrBlank(code)) {
					final String msg= "Tratando de agregar Pais con código vacío. No hago nada";
					message.setText(msg);
					LOGGER.warn(msg);
						return;
				}
				final String name = nameField.getText();
				if (isEmptyOrBlank(name)) {
					final String msg = "Tratando de agregar Pais con nombre vacío. No hago nada";
					message.setText(msg);
					LOGGER.warn(msg);
						return;
				}
				final String phoneMask = phoneMaskField.getText();
				boolean countryExists = false;
				for (final Pais pais : Pais.getPaises()) {
					if (code.equalsIgnoreCase(pais.getCountryCode())) {
						countryExists = true;
						if (name.equalsIgnoreCase(pais.getCountryName())
								&& equalEmptyOrBlank(phoneMask, pais.getFormatoTelefono())) {
							final String msg = String.format("Tratando de agregar Pais repetido : %s. No hago nada.", name);
							message.setText(msg);
							LOGGER.warn(msg);
							return;
						}
						break;
					}
				}
				try {
					if (countryExists) {
						LOGGER.debug("Modificando País : " + name);
						LOGGER.debug("Antes : " + Pais.getPaises());
						agendaDb.updateCountry(code, name, phoneMask);
					} else {
						LOGGER.debug("Agregando País : " + name);
						LOGGER.debug("Antes : " + Pais.getPaises());
						agendaDb.addCountry(code, name, phoneMask);
					}
					new Pais(code, name, phoneMask);
					populateList();
					codeField.setText("");
					nameField.setText("");
					phoneMaskField.setText("");
					LOGGER.debug("Despues : " + Pais.getPaises());
				} catch (SQLException ex) {
					LOGGER.error(LoggerFactory.ERROR_TAG, ex);
				}
			});
			add(panel, BorderLayout.CENTER);
			add(message, BorderLayout.SOUTH);
		}

		public void setPais(final Pais pais) {
			codeField.setText(pais.getCountryCode());
			nameField.setText(pais.getCountryName());
			phoneMaskField.setText(pais.getFormatoTelefono());
			message.setText("");
		}
	}
}
