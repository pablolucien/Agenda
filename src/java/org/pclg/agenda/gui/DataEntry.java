package org.pclg.agenda.gui;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.AgendaUtil;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.AgendaRecordImpl;
import org.pclg.agenda.entities.Pais;
import org.pclg.gui.FancyButtonPanel;
import org.pclg.gui.I18NManager;
import org.pclg.gui.ImageButton;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.DirTree;
import org.pclg.tools.GUITools;
import org.pclg.tools.ObservableProperties;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.StringTools;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;
import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * @author El Coyote
 * @since 13-sep-2007 11:30:17
 */
@SuppressWarnings("serial")
public final class DataEntry extends JPanel implements ActionListener {
	/** serialVersionUID. */
	private static final long serialVersionUID = 6196637337312154662L;
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	private SimpleDateFormat dateFormatStd;

	/** Para saber si estamos creando un nuevo registro. Se pone a false en setData(). */
	private boolean isNewRecord = true;

	/** Para guardar los datos iniciales y asi saber si hay que actualizar o no. */
	private final AgendaRecord originalRecord;

	/** Para guardar los datos editados en el caso de que me mueva con las flechas. */
	private AgendaRecord editedRecord;

	/** La clave del registro que estamos mostrando si este existe. */
	private int recordId;

    // Botones globales del formulario
    private AbstractButton btnAceptar;
    private AbstractButton btnCancelar;
    private AbstractButton btnVCard;

	private String clave;
	private final Properties properties;
	private final AgendaDb agendaDb;
	private final JTabbedPane tabbedPane;
    private final AgendaGUI agendaGUI;
	private Timestamp fechaCreacion;
	private final I18NManager i18nManager;
	private final RecordEditor recordEditor;
	private DirTree dirTree;

	public DataEntry(final Properties properties, final AgendaDb agendaDb,
					 final JTabbedPane tabbedPane, final AgendaGUI agendaGUI,
					 final AgendaRecord originalRecord) {
		this.properties = properties;
		this.agendaDb = agendaDb;
		this.tabbedPane = tabbedPane;
		this.agendaGUI = agendaGUI;
		i18nManager = I18NManager.getInstance(properties);
        setLayout(new BorderLayout());
        setUpDateFormat(properties);
		final JPanel buttonPanel = createButtonPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		recordEditor = new RecordEditor(properties);
		final ImageButton btnImage = recordEditor.getBtnImage();
		btnImage.addPopUpOption(PropertiesHelper.getStringFromProperties(properties, "DataEntry.MoveImage"), event -> {
            final String imagePath = recordEditor.getImagePath();

            if (imagePath != null) {
				final File imageFile = new File(imagePath);
				final Path source = imageFile.toPath();
				getDirTree();
				dirTree.setFile(imageFile.getParentFile());
				dirTree.setVisible(true);
				if (dirTree.accepted()) {
					dirTree.getFile().ifPresent(ff -> {
						final Path newDir = ff.toPath();
						LOGGER.debug("Moving " + source + " to " + newDir);
						try {
							final Path target = newDir.resolve(source.getFileName());
							Files.move(source, target, REPLACE_EXISTING);
							LOGGER.debug("Done");
							final String strPath = target.toString();
							agendaDb.updateImagePath(recordId, source.toString(), strPath);
							btnImage.setImagePath(strPath);
							if (originalRecord != null) {
								originalRecord.setImagePath(strPath);
							}
						} catch (final IOException | SQLException ex) {
							LOGGER.error(LoggerFactory.ERROR_TAG, ex);
						}
					});
				}
            }
        });
		add(recordEditor, BorderLayout.CENTER);

		if (originalRecord == null) {
			this.originalRecord = getData();
		} else {
            originalRecord.populate();
			this.originalRecord = originalRecord;
			try {
				setData(originalRecord);
			} catch (final SQLException ex) {
				LOGGER.error(LoggerFactory.ERROR_TAG, ex);
			}
		}

		recordEditor.addActionListener(actionEvent -> {
			final String actionCommand = actionEvent.getActionCommand();
			final AgendaRecord currentData = getData();
			final int version = currentData.getVersion();
			switch (actionCommand) {
			case RecordEditor.PREV_COMMAND:
				if (version == 1) {
					return;
				}
				// Comparando las versiones para saber si estamos en la última sin
				// necesidad de ir a la base de datos. Sólo consideramos los cambios
				// en el caso de la última versión.
				if (version == this.originalRecord.getVersion()
						&& !currentData.equals(this.originalRecord)) {
					editedRecord = currentData;
				}
				try {
                    final AgendaRecord record = agendaDb.getContacto(intValue(clave), version - 1);
                    setData(record);
				} catch (final SQLException ex) {
					LOGGER.log(Level.ERROR, LoggerFactory.ERROR_TAG, ex);
					showErrorMessage(ex.getLocalizedMessage());
					return;
				}
				break;
			case RecordEditor.NEXT_COMMAND:
				try {
					final AgendaRecord record = agendaDb.getContacto(intValue(clave), version + 1);
					setData(record);
				} catch (final SQLException ex) {
					LOGGER.log(Level.ERROR, LoggerFactory.ERROR_TAG, ex);
					showErrorMessage(ex.getLocalizedMessage());
				}
				break;
			default:
				throw new IllegalStateException(
					"Comando " + actionCommand + " desconocido");
			}
		});

		btnAceptar.addActionListener(actionEvent -> {
			if (saveData()) {
				tabbedPane.setSelectedIndex(0);
			}
		});

        btnVCard.addActionListener(actionEvent -> {
			getDirTree();
			final String dir = properties.getProperty("AgendaGUI.lastVCardsDirectory");
			if (dir != null) {
				dirTree.setFile(new File(dir));
			}
			dirTree.setVisible(true);

			if (dirTree.accepted()) {
				dirTree.getFile().ifPresent(ff -> {
					properties.setProperty("AgendaGUI.lastVCardsDirectory", ff.getAbsolutePath());
					try {
						AgendaUtil.saveVCard(getData(), ff);
					} catch (final IOException ex) {
						LOGGER.error(LoggerFactory.ERROR_TAG, ex);
						JOptionPane.showMessageDialog(DataEntry.this, ex.getMessage(),
								LoggerFactory.ERROR_TAG, JOptionPane.ERROR_MESSAGE );
					}
				});
			}
		});

		btnCancelar.addActionListener(this);

        // FIXME: falta hacer que funcione cuando lo que tiene el foco es el tab
        final InputMap keyMap =
            getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        final String closeActionId = "close.action";
        keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
            InputEvent.CTRL_DOWN_MASK), closeActionId);
        final ActionMap actionMap = getActionMap();
        actionMap.put(closeActionId, new AbstractAction() {
            @Override
			public void actionPerformed(final ActionEvent event) {
                DataEntry.this.actionPerformed(event);
            }
        });
	}

    private void getDirTree() {
        if (dirTree == null) {
            final JDialog dialog = new JDialog(agendaGUI, Dialog.ModalityType.APPLICATION_MODAL);
            dirTree = new DirTree(dialog);
            GUITools.center(dialog, agendaGUI);
        }
    }

	private void setUpDateFormat(final Properties properties) {
        dateFormatStd = new SimpleDateFormat(PropertiesHelper
            .getStringFromProperties(properties, "Agenda.datePattern"), getLocale());
        if (properties instanceof ObservableProperties) {
            ((ObservableProperties) properties).addChangeObserver(observable -> {
				setLocale(new Locale(getStringFromProperties(properties, "Application.ForceLanguage")));
                dateFormatStd = new SimpleDateFormat(getStringFromProperties(properties, "Agenda.datePattern"), getLocale());
				// FIXME: updateRecordStats(getData());
            });
        }
    }

    private boolean validate(final AgendaRecord record) {
		final StringBuilder msg = new StringBuilder();
		if (isNewRecord && record.isDeleted()) {
			msg.append(PropertiesHelper.getStringFromProperties(properties,
				"delete.new.record.msg")).append('\n');
		}

        if (!agendaDb.isLastVersion(record)) {
            msg.append(MessageFormat.format(PropertiesHelper.getStringFromProperties(properties,
                "save.old.record.version.msg"), Integer.valueOf(recordId))).append('\n');
        }

		if (isEmptyOrBlank(record.getFirstname())
			    && isEmptyOrBlank(record.getLastname())) {
			msg.append(PropertiesHelper.getStringFromProperties(properties,
				"DataEntry.empty.name.msg")).append('\n');
		}

		final List<String> emails = record.getEmails();
		emails.stream().filter(email -> !StringTools.validEmail(email))
			.forEach(email -> msg.append(MessageFormat.format(PropertiesHelper
				.getStringFromProperties(properties, "DataEntry.invalid.email.msg"), email)).append('\n'));

		final int dia = record.getDay();
		final int mes = record.getMonth();
		final int ano = record.getYear();
		if (dia != 0 && mes != 0 && ano != 0) {
			try {
				new Calendar.Builder().setLenient(false).setDate(ano, mes - 1, dia).build();
			} catch (final IllegalArgumentException ex) {
				msg.append(PropertiesHelper.getStringFromProperties(properties,
					"DataEntry.invalid.birth.date")).append('\n');
			}
		}

		if (msg.length() > 0) {
			JOptionPane.showMessageDialog(this, msg.toString(),
				PropertiesHelper.getStringFromProperties(properties, "warning.title"),
				JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	boolean saveData() {
		final AgendaRecord newRecord = getData();
		if (!validate(newRecord)) {
			return false;
		}
		if (isNewRecord) {
			try {
				if (newRecord.isImageDirty()) {
					saveImage(newRecord.getTemporaryImage(), newRecord.getImagePath());
				}
				agendaDb.insertRecord(newRecord);
				setData(newRecord);
			} catch (final SQLException ex) {
				LOGGER.log(Level.ERROR, "Error insertando", ex);
				showErrorMessage(ex.getLocalizedMessage());
			}
		} else if (!newRecord.equals(originalRecord)) {
			try {
				if (newRecord.isDeleted()) {
					if (JOptionPane.showConfirmDialog(this,
						PropertiesHelper.getStringFromProperties(properties,
							"deleting.msg"),
						PropertiesHelper.getStringFromProperties(properties,
							"warning.title"),
						JOptionPane.YES_NO_OPTION) == 0) {
						agendaDb.markAsDeleted(newRecord.getKey());
						tabbedPane.remove(this);
						tabbedPane.setSelectedIndex(0);
					}
					return true;
				} else {
					if (newRecord.equalsGroups(originalRecord)) {
						newRecord.setVersionGroup(originalRecord.getVersionGroup());
					} else {
						newRecord.setVersionGroup(agendaDb
                            .saveGrupos(newRecord));
					}
					if (newRecord.equalsTelephones(originalRecord)) {
						newRecord.setVersionTelephone(originalRecord.getVersionTelephone());
					} else {
						newRecord.setVersionTelephone(agendaDb
                            .saveTelephones(newRecord));
					}
					if (newRecord.equalsAddresses(originalRecord)) {
						newRecord.setVersionAddress(originalRecord.getVersionAddress());
					} else {
						newRecord.setVersionAddress(agendaDb
                            .saveDirecciones(newRecord));
					}
					if (newRecord.equalsEmails(originalRecord)) {
						newRecord.setVersionEmail(originalRecord.getVersionEmail());
					} else {
						newRecord.setVersionEmail(agendaDb
							.saveEmails(newRecord));
					}
					if (newRecord.equalsImages(originalRecord)) {
						newRecord.setVersionImage(originalRecord.getVersionImage());
					} else {
						newRecord.setVersionImage(agendaDb
                            .saveImages(newRecord));
					}
					if (newRecord.equalsNotes(originalRecord)) {
						newRecord.setVersionNotes(originalRecord.getVersionNotes());
					} else {
						newRecord.setVersionNotes(agendaDb.saveNotes(newRecord));
					}
					agendaDb.updateRecord(newRecord);
				}
				setData(newRecord);
			} catch (final Exception ex) {
				LOGGER.log(Level.ERROR, "Error actualizando", ex);
				showErrorMessage(ex.getLocalizedMessage());
				return false;
			}
		}
		editedRecord = null;
		agendaGUI.refreshRecord(newRecord);
		return true;
	}

	private void saveImage(final String sourceImagePath, final String targetFilePath) {
        if (sourceImagePath == null || targetFilePath == null) {
            LOGGER.warn(String.format("Invalid data: sourceImagePath = [%s], targetFilePath = [%s]", sourceImagePath, targetFilePath));
            return;
        }
		final JFileChooser fileChooser = new JFileChooser();
		{
			// FIXME: Esto está repe en ImageButton
			final FileFilter FILE_FILTER = new FileNameExtensionFilter(
				"Imagenes",	"gif", "jpg", "jpeg", "png");
			fileChooser.setFileFilter(FILE_FILTER);
			fileChooser.setMultiSelectionEnabled(false);
		}

		final String dir = properties.getProperty("DataEntry.lastImagesDirectory");
		File targetFile = new File(targetFilePath);
		if (dir != null) {
			fileChooser.setCurrentDirectory(new File(dir));
			fileChooser.setSelectedFile(targetFile);
		}
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			properties.setProperty("DataEntry.lastImagesDirectory",
				fileChooser.getCurrentDirectory().getAbsolutePath());
			targetFile = fileChooser.getSelectedFile();
			LOGGER.debug("targetFile = " + targetFile);
			if (!new File(sourceImagePath).renameTo(targetFile)) {
				showErrorMessage(String.format(properties.getProperty("DataEntry.cantMoveImageMsg"),
					sourceImagePath, targetFile.getAbsolutePath()));
			}
		}
	}

	private void showErrorMessage(final String message) {
		JOptionPane.showMessageDialog(this, message, LoggerFactory.ERROR_TAG,
			JOptionPane.ERROR_MESSAGE);
	}

	/** Action a ejecutar por el botón 'Cancelar' o el aspa del TabPanel. */
	@Override
	public void actionPerformed(final ActionEvent e) {
		if (willClose()) {
			tabbedPane.remove(this);
		}
	}

	boolean willClose() {
		final AgendaRecord newData = getData();
		return newData.equals(originalRecord) && editedRecord == null
			|| newData.getVersion() != originalRecord.getVersion()
            || JOptionPane.showConfirmDialog(this,
			PropertiesHelper.getStringFromProperties(properties, "abort.msg"),
			PropertiesHelper
				.getStringFromProperties(properties, "warning.title"),
			JOptionPane.YES_NO_OPTION) == 0;
	}

	private JPanel createButtonPanel() {
		btnAceptar = i18nManager.configureI18NButton(new JButton(),
			"DataEntry.AcceptButton", "DataEntry.AcceptImage");
		btnCancelar = i18nManager.configureI18NButton(new JButton(),
			"CancelButton", "CancelImage");
		btnVCard = i18nManager.configureI18NButton(new JButton(),
			"AgendaGUI.SaveVcard", "AgendaGUI.VCardImage");

		return new FancyButtonPanel(btnAceptar, btnVCard, btnCancelar);
	}

	AgendaRecord getData() {
        final String sexItem = recordEditor.getSex();
        final Pais paisItem = recordEditor.getSelectedCountry();
		final AgendaRecord record = new AgendaRecordImpl()
            .setKey(intValue(clave))
            .setVersion(recordEditor.getVersion())
            .setDeleted(recordEditor.isDeleted())
            .setFirstname(recordEditor.getNombre())
            .setLastname(recordEditor.getApellido())
            .setAddress(recordEditor.getDireccion())
            .setSex(sexItem == null ? "" : sexItem)
            .setCountry(paisItem == null ? Pais.getUnknownCountry() : paisItem)
            .setDay(intValue(recordEditor.getDia()))
            .setMonth(intValue(recordEditor.getMes()))
            .setYear(intValue(recordEditor.getAno()))
            .setMark(recordEditor.getMarca())
			.setCreationTimestamp(fechaCreacion)
            .setListTelephones(recordEditor.isListable())
            .setNotes(recordEditor.getNotas())
			.setImageDirty(recordEditor.isImageDirty())
			.setImagePath(recordEditor.getImagePath())
			.setTemporaryImage(recordEditor.getTemporaryImagePath());

        final String tfEmailText = recordEditor.getEmails();
        if (tfEmailText.trim().length() > 0) {
			final List<String> emails = new ArrayList<>();
			Collections.addAll(emails, tfEmailText.split("[,; ]+"));
			record.setEmails(emails);
        }

        record.setGroups(recordEditor.getGroups());
        record.setTelephones(recordEditor.getTelephons());
		return record;
	}

	private String parseDate(final Timestamp timestamp) {
		if (timestamp == null) {
			return "";
		}
		return dateFormatStd.format(timestamp);
	}

	private static int intValue(final String strVal) {
		return strVal == null || strVal.trim().length() == 0 ? 0
			: Integer.parseInt(strVal);
	}

	private void setData(AgendaRecord record) throws SQLException {
		// si he editado y me he movido con las flechas.
		if (agendaDb.isLastVersion(record) && editedRecord != null) {
			record = editedRecord;
			editedRecord = null;
		}
		clave = String.valueOf(recordId = record.getKey());
		fillData(record);
		isNewRecord = false;
		enableNavigationButtons(record);
		final int indexOfTabComponent = tabbedPane.indexOfComponent(this);
		if (indexOfTabComponent >= 0) {
			tabbedPane.setTitleAt(indexOfTabComponent, record.getFirstname() + " " + record.getLastname());
		}
	}

	public void fillData(final AgendaRecord record) throws SQLException {
		final String sexo = record.getSex();
		recordEditor.setVersion(record.getVersion())
			.setDeleted(record.isDeleted())
			.setNombre(record.getFirstname())
			.setApellido(record.getLastname())
			.setDireccion(record.getAddress())
			.setSex(String.valueOf(sexo))
			.setPais(Pais.getInstance(record.getCountry().getCountryCode()))
			.setListaTelefonos(record.getTelephones())
			.setDia(record.getDay())
			.setMes(record.getMonth())
			.setAno(record.getYear())
			.setMarca(record.getMark())
			.setListable(record.isListTelephones())
			.setNotas(record.getNotes())
			.setGroups(record.getGroups())
			.setThumbnail(record.getThumbnail())	// Esto tiene que ir antes de setImagePath() para que esté cargada la thumbnail
			.setImagePath(record.getImagePath(), sexo)
			.setTemporaryImagePath(record.getTemporaryImage())
			.setEmails(record.getEmails())
			.setImageDirty(record.isImageDirty());
		fechaCreacion = record.getCreationTimestamp();
		updateRecordStats(record);
	}

	private void updateRecordStats(final AgendaRecord record) {
		final String strFechaCreacion = parseDate(record.getCreationTimestamp());
		final String strFechaActualizacion = parseDate(record.getUpdateTimestamp());
		final int recordCount = agendaDb.getRecordCount();
		final Object[] stats = {
			record.getKey() + " / " + recordCount,
			String.valueOf(record.getVersion()),
			isEmptyOrBlank(strFechaCreacion) ? "?" : strFechaCreacion,
			isEmptyOrBlank(strFechaActualizacion) ? "?" : strFechaActualizacion,
		};
		recordEditor.setStats(stats);
	}

	private void enableNavigationButtons(final AgendaRecord record)
			throws SQLException {
		if (record == null) {
			recordEditor.enableContactChange(true);
			recordEditor.enablePrevious(false);
			recordEditor.enableNext(false);
		} else {
			if (agendaDb.isLastVersion(record)) {
				recordEditor.enableContactChange(true);
				recordEditor.enableNext(false);
			} else {
				recordEditor.enableContactChange(false);
				recordEditor.enableNext(true);
			}
			recordEditor.enablePrevious(record.getVersion() > 1);
		}
	}

	public int getRecordId() {
		return recordId;
	}
}
