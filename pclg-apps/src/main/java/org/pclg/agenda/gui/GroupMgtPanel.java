package org.pclg.agenda.gui;

import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.entities.Grupo;
import org.pclg.gui.EditableJList;
import org.pclg.gui.I18NManager;
import org.pclg.gui.TextFieldLimiter;
import org.pclg.log.LoggerFactory;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Pablo
 * @since 19/10/13 19:35
 */
public class GroupMgtPanel extends JPanel {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = -2084657209309564903L;
	private final EditableJList<Grupo> listaGrupos = new EditableJList<>(Grupo.NULL_VALUE);
	private final AgendaDb agendaDb; // FIXME: no se si esto debe estar aqu� o en Grupo o d�nde
    private long lastUpdated;

    public GroupMgtPanel(final Properties properties, final AgendaDb agendaDb) {
		super(new BorderLayout());
		this.agendaDb = agendaDb;
		populateList();

		add(new JScrollPane(listaGrupos), BorderLayout.CENTER);
		final JTextField groupEditorComponent = new JTextField();
		groupEditorComponent.setDocument(new TextFieldLimiter(Grupo.NAME_FIELD_LEN));
		final DefaultCellEditor groupEditor =
			new DefaultCellEditor(groupEditorComponent);
		groupEditor.setClickCountToStart(1);
		listaGrupos.setDefaultEditor(String.class, groupEditor);

		add(createAddPanel(properties), BorderLayout.SOUTH);
	}

	private void populateList() {
		listaGrupos.clear();
		listaGrupos.addAll(Grupo.getValues());
		listaGrupos.refresh();
	}

	private JPanel createAddPanel(final Properties properties) {
		final JPanel panel = new JPanel(new BorderLayout());
		final JTextField nameField = new JTextField();
		nameField.setDocument(new TextFieldLimiter(40));
		panel.add(nameField, BorderLayout.CENTER);
		final I18NManager i18nManager = I18NManager.getInstance(properties);
		final JLabel message = new JLabel();
		message.setForeground(Color.red);
		panel.add(message, BorderLayout.SOUTH);
		final JButton button = new JButton();
		panel.add(i18nManager.configureI18NComponent(button, "AgendaGUI.addButton"), 
			BorderLayout.EAST);
		button.addActionListener(e -> {
			message.setText("");
			final String name = nameField.getText();
			for (Grupo group : Grupo.getValues()) {
				if (name.equalsIgnoreCase(group.getNombre())) {
					final String msg = String.format("Tratando de agregar grupo repetido : %s. No hago nada.", name);
					message.setText(msg);
					LOGGER.warn(msg);
					return;
				}
			}
			LOGGER.debug("Agregando grupo : " + name);
			LOGGER.debug("Antes : " + Grupo.getValues());
			try {
				final int clave = agendaDb.addGrupo(name);
				Grupo.add(new Grupo(clave, name));
				nameField.setText("");
				populateList();
				LOGGER.debug("Despues : " + Grupo.getValues());
			} catch (SQLException ex) {
				LOGGER.error(LoggerFactory.ERROR_TAG, ex);
			}
		});
		return panel;
	}

	public void accept() {
		if (LOGGER.isDebugEnabled()) {
            final StringBuilder builder = new StringBuilder(1024);
			for (int ii = 0, rowCount = listaGrupos.getRowCount();
				 	ii < rowCount; ii++) {
				final Object obj = listaGrupos.getValueAt(ii, 0);
                builder.append("accept(): ").append(obj.getClass()).append(" - ").append(obj).append('\n');
            }
            LOGGER.debug(builder);
        }
	}

    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            final long lastUpdatedGrupo = Grupo.getLastUpdated();
            if (lastUpdated < lastUpdatedGrupo) {
                populateList();
                lastUpdated = lastUpdatedGrupo;
            }
        }
        super.setVisible(visible);
    }
}
