package org.pclg.agenda.gui;

import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaUtil;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.Grupo;
import org.pclg.agenda.entities.Pais;
import org.pclg.agenda.entities.Telefono;
import org.pclg.gui.ColorChangerDocumentListener;
import org.pclg.gui.EditableJList;
import org.pclg.gui.I18NManager;
import org.pclg.gui.ImageButton;
import org.pclg.gui.IntegerTextFieldLimiter;
import org.pclg.gui.ListSelector;
import org.pclg.gui.TextFieldLimiter;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;
import org.pclg.tools.ObservableProperties;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.StringTools;
import org.pclg.xtras.Colortable;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.MessageFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * Panel para editar un AgendaRecord
 *
 * @author El Coyote Cojo
 * @since 2016.04.21
 */
final class RecordEditor extends JPanel {
	/** Command action for btnPrev button. */
	public static final String PREV_COMMAND = "PREV";

	/** Command action for btnNext button. */
	public static final String NEXT_COMMAND = "NEXT";

	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	private static final Color COLOR_MARKED = new Color(-13312);
	private static final Color COLOR_UNMARKED = new Color(-1);
	private static final Color COLOR_POST_IT = new Color(0xFFFD29);
    private static final long serialVersionUID = 7663721325405668100L;
    private JTextArea taAddress;
	private JTextField tfFirstName;
	private JTextField tfLastName;
	private JTextArea taNotes;
	private JTextField tfMark;
	private JTextField tfDay;
	private JTextField tfMonth;
	private JTextField tfYear;
	private JTextField tfAge;
	private JTextField tfDays;
	private ImageButton btnImage;
	private JComboBox<Pais> comboPais;
	private final EditableJList<Telefono> listaTelefonos = new EditableJList<>(Telefono.NULL_VALUE);
	private final ListSelector<Grupo> groupSelector = new ListSelector<>(true);
	private JTextField tfEmail;
	private JCheckBox chkListar;
	private JCheckBox chkDeleted;
	private JComboBox<String> comboSexo;
	private JButton btnPrev;
	private JButton btnNext;
	private final Properties properties;
	private final transient I18NManager i18nManager;
	private final JLabel statsMessageLabel = new JLabel();
	private final MessageFormat statsMessageFormat;
	private transient Object[] stats;
	private int version;
	private boolean imageDirty;
	private String temporaryImagePath;
	private Icon thumbnail;
	final TelefonoCellEditor telephoneEditor;

	RecordEditor(final Properties properties) {
		this.properties = properties;
		i18nManager = I18NManager.getInstance(properties);
		statsMessageFormat = new MessageFormat(PropertiesHelper.
			getStringFromProperties(properties, "DataEntry.lbl.recordStats"));
		if (properties instanceof ObservableProperties) {
			((ObservableProperties) properties).addChangeObserver(
				arg -> {
                    statsMessageFormat.applyPattern(PropertiesHelper.
                        getStringFromProperties(properties,
                            "DataEntry.lbl.recordStats"));
                    SwingUtilities.invokeLater(() -> statsMessageLabel
                        .setText(statsMessageFormat.format(stats)));
                });
		}
		telephoneEditor = new TelefonoCellEditor(properties);
		setupGUI();
	}

	public String getNombre() {
		return tfFirstName.getText();
	}

	public RecordEditor setNombre(final String nombre) {
		tfFirstName.setText(nombre);
		return this;
	}

	public String getApellido() {
		return tfLastName.getText();
	}

	public RecordEditor setApellido(final String apellido) {
		tfLastName.setText(apellido);
		return this;
	}

	public String getNotas() {
		return taNotes.getText();
	}

	public RecordEditor setNotas(final String notas) {
		taNotes.setText(notas);
		taNotes.setCaretPosition(0);
		taNotes.moveCaretPosition(0);
		return this;
	}

	public String getMarca() {
		return tfMark.getText();
	}

	public RecordEditor setMarca(final String marca) {
		tfMark.setText(marca);
		return this;
	}

	public String getAno() {
		return tfYear.getText();
	}

	public RecordEditor setAno(final int ano) {
		tfYear.setText(String.valueOf(ano));
		updateAgeInfo();
		return this;
	}

	public String getMes() {
		return tfMonth.getText();
	}

	public RecordEditor setMes(final int mes) {
		tfMonth.setText(String.valueOf(mes));
		updateAgeInfo();
		return this;
	}

	public String getDia() {
		return tfDay.getText();
	}

	public RecordEditor setDia(final int dia) {
		tfDay.setText(String.valueOf(dia));
		updateAgeInfo();
		return this;
	}

	private void updateAgeInfo() {
		final int day = toInt(tfDay.getText());
		final int month = toInt(tfMonth.getText());
		final int year = toInt(tfYear.getText());
		if (day != 0 && month != 0 && year != 0) {
			final AgendaUtil.AgeInfo ageInfo =
                AgendaUtil.computeAgeAndDays(day, month - 1, year);
			if (!AgendaUtil.AgeInfo.NULL_AGE_INFO.equals(ageInfo)) {
				setEdad(String.valueOf(ageInfo.getAge()));
				setDias(String.valueOf(ageInfo.getDays()));
			}
		}
	}

	private static int toInt(final String text) {
		return text.trim().length() == 0 ? 0 :Integer.parseInt(text);
	}

	public String getDireccion() {
		return taAddress.getText();
	}

	public RecordEditor setDireccion(final String direccion) {
		taAddress.setText(direccion);
		taAddress.setCaretPosition(0);
		taAddress.moveCaretPosition(0);
		return this;
	}

	void enableNext(final boolean enabled) {
		btnNext.setEnabled(enabled);
	}

	void enablePrevious(final boolean enabled) {
		btnPrev.setEnabled(enabled);
	}

	RecordEditor setListable(final boolean listar) {
		chkListar.setSelected(listar);
		return this;
	}

    public String getEmails() {
   		return tfEmail.getText();
   	}

	public RecordEditor setEmails(final List<String> emails) {
        tfEmail.setText(emails.stream().collect(Collectors.joining(", ")));
		return this;
	}

	public RecordEditor setDeleted(final boolean deleted) {
		chkDeleted.setSelected(deleted);
		return this;
	}

	boolean isListable() {
		return chkListar.isSelected();
	}

	public boolean isDeleted() {
		return chkDeleted.isSelected();
	}

	RecordEditor setListaTelefonos(final List<Telefono> telefonos) {
		listaTelefonos.clear();
		telefonos.stream().filter(telefono ->
			!isEmptyOrBlank(telefono.getNumero())).forEach(listaTelefonos::addItem);
		listaTelefonos.addItem(Telefono.NULL_VALUE);
		listaTelefonos.refresh();
		return this;
	}

	RecordEditor setGroups(final List<Grupo> grupos) {
		groupSelector.setListaElementosAsignados(grupos);
		return this;
	}

	public RecordEditor setPais(final Pais pais) {
		comboPais.setSelectedItem(pais);
		telephoneEditor.setDefaultCountryPrefix(pais.getCountryCode());
		return this;
	}

	public List<Telefono> getTelephons() {
		final int rowCount = listaTelefonos.getRowCount();
		final List<Telefono> telefonos = new ArrayList<>(rowCount);
		for (int ii = 0; ii < rowCount; ii++) {
			final Telefono telefono = listaTelefonos.getValueAt(ii, 0);
			if (!isEmptyOrBlank(telefono.getNumero())) {
				telefonos.add(telefono);
			}
		}
		return telefonos;
	}

    List<Grupo> getGroups() {
		return groupSelector.getListaElementosAsignados();
	}

	Pais getSelectedCountry() {
		return (Pais) comboPais.getSelectedItem();
	}

	String getSex() {
		return (String) comboSexo.getSelectedItem();
	}

	RecordEditor setSex(final String sex) {
		comboSexo.setSelectedItem(sex);
		return this;
	}

	RecordEditor setImagePath(final String imagePath, final String sexo) {
		if (isEmptyOrBlank(imagePath)) {
			if ("F".equals(sexo)) {
				ImageTools.getImageIcon(PropertiesHelper
					.getStringFromProperties(properties, "unknownWomanImage"))
					.ifPresent(btnImage::setVoidIcon);
			} else if ("M".equals(sexo)) {
				ImageTools.getImageIcon(PropertiesHelper
					.getStringFromProperties(properties, "unknownManImage"))
					.ifPresent(btnImage::setVoidIcon);
			}
		} else {
			btnImage.setImagePath(imagePath);
			btnImage.setThumbnail(thumbnail);
		}
		return this;
	}

	ImageButton getBtnImage() {
		return btnImage;
	}

	String getImagePath() {
		return btnImage.getImagePath();
	}

	String getTemporaryImagePath() {
		return temporaryImagePath;
	}

    RecordEditor setTemporaryImagePath(final String temporaryImagePath) {
   		this.temporaryImagePath = temporaryImagePath;
   		return this;
   	}

    RecordEditor setThumbnail(final Icon thumbnail) {
   		this.thumbnail = thumbnail;
   		return this;
   	}

	private RecordEditor setDias(final String days) {
		tfDays.setText(days);
		return this;
	}

	private RecordEditor setEdad(final String age) {
		tfAge.setText(age);
		return this;
	}

	public int getVersion() {
		return version;
	}

	public RecordEditor setVersion(final int version) {
		this.version = version;
		return this;
	}

	RecordEditor setStats(final Object[] stats) {
		this.stats = stats;
		statsMessageLabel.setText(statsMessageFormat.format(stats));
		return this;
	}

	/* M�todo generado por JGuiD */
	private void setupGUI() {
		final JPanel westPane = new JPanel(new BorderLayout());
		final JPanel westUpperPane = new JPanel(null);
		final Color background = Color.lightGray;
		westPane.add(westUpperPane, BorderLayout.NORTH);
		final int westUpperPaneWidth = 550;
		final int westUpperPaneHeight = 390;
		final int leftMargin = 10;

		JLabel label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setLocation(0, 2);
		label.setSize(westUpperPaneWidth, 25);
		label.setBackground(Color.pink);
		label.setForeground(Color.yellow);
		label.setOpaque(true);
		i18nManager.configureI18NComponent(label, "actualizacion.de.datos");
		westUpperPane.add(label);

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setLocation(leftMargin, 30);
		label.setSize(70, 25);
		i18nManager.configureI18NComponent(label, "DataEntry.lbl.nombre");
		label.setToolTipText(PropertiesHelper
			.getStringFromProperties(properties,
				"DataEntry.tooltip.lbl.nombre"));
		westUpperPane.add(label);

		final FocusListener focusListener = new FocusListener() {
			String previousValue;

			@Override
			public void focusGained(final FocusEvent e) {
				previousValue = ((JTextComponent) e.getSource()).getText();
			}

			@Override
			public void focusLost(final FocusEvent e) {
				if (previousValue.length() == 0) {
					final JTextComponent textComponent = (JTextComponent) e
						.getSource();
					textComponent.setText(
						StringTools.toTitleCase(textComponent.getText()));
				}
			}
		};

		tfFirstName = new JTextField();
		tfFirstName.setLocation(95, 30);
		tfFirstName.setSize(160, 25);
		tfFirstName.setColumns(10);
		tfFirstName.setToolTipText(PropertiesHelper.getStringFromProperties(
			properties, "DataEntry.tooltip.nombre"));
		tfFirstName.setDocument(new TextFieldLimiter(AgendaRecord.NAME_FIELD_LEN));
		tfFirstName.addFocusListener(focusListener);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(final ComponentEvent e) {
				tfFirstName.requestFocus();
				removeComponentListener(this);
			}
		});

		westUpperPane.add(tfFirstName);

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setLocation(290, 30);
		label.setSize(90, 25);
		i18nManager.configureI18NComponent(label, "DataEntry.lbl.apellido");
		westUpperPane.add(label);

		tfLastName = new JTextField();
		tfLastName.setLocation(380, 30);
		tfLastName.setSize(160, 25);
		tfLastName.setColumns(10);
		tfLastName.setToolTipText(PropertiesHelper.getStringFromProperties(
			properties, "DataEntry.tooltip.apellido"));
		westUpperPane.add(tfLastName);
		tfLastName.setDocument(new TextFieldLimiter(AgendaRecord.SURNAME_FIELD_LEN));
		tfLastName.addFocusListener(focusListener);

		comboSexo = new JComboBox<>(new String[]{"F", "M", "E"});
		comboSexo.setLocation(95, 60);
		comboSexo.setSize(50, 25);
		comboSexo.setEditable(false);
		comboSexo.setEnabled(true);
		westUpperPane.add(comboSexo);

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setLocation(leftMargin, 60);
		label.setSize(70, 25);
		i18nManager.configureI18NComponent(label, "DataEntry.lbl.sexo");
		westUpperPane.add(label);

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setLocation(150, 60);
		label.setSize(42, 25);
		i18nManager.configureI18NComponent(label, "DataEntry.lbl.fecha");
		westUpperPane.add(label);

		final DocumentListener dateListener = new DocumentListener() {
			/**
			 * Gives notification that there was an insert into the document.  The
			 * range given by the DocumentEvent bounds the freshly inserted region.
			 *
			 * @param e the document event
			 */
			@Override
			public void insertUpdate(final DocumentEvent e) {
				updateAgeAndDays();
			}

			/**
			 * Gives notification that a portion of the document has been
			 * removed.  The range is given in terms of what the view last
			 * saw (that is, before updating sticky positions).
			 *
			 * @param e the document event
			 */
			@Override
			public void removeUpdate(final DocumentEvent e) {
				updateAgeAndDays();
			}

			/**
			 * Gives notification that an attribute or set of attributes changed.
			 *
			 * @param e the document event
			 */
			@Override
			public void changedUpdate(final DocumentEvent e) {
				updateAgeAndDays();
			}

			private void updateAgeAndDays() {
				final String year = tfYear.getText();
				if (!isEmptyOrBlank(year)) {
					String dia = tfDay.getText();
					String mes = tfMonth.getText();
					if (isEmptyOrBlank(dia)) {
						dia = "0";
					}
					if (isEmptyOrBlank(mes)) {
						mes = "0";
					}
                    try {
                        final AgendaUtil.AgeInfo ageInfo = AgendaUtil.computeAgeAndDays(
                            Integer.valueOf(dia), Integer.valueOf(mes) - 1, Integer.valueOf(year));
                        tfAge.setText(String.valueOf(ageInfo.getAge()));
                        tfDays.setText(String.valueOf(ageInfo.getDays()));
                    } catch (NumberFormatException | DateTimeException ex) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                    }
                }
			}
		};

		tfDay = new JTextField();
		tfDay.setLocation(202, 60);
		tfDay.setSize(25, 25);
		tfDay.setColumns(10);
		tfDay.setHorizontalAlignment(SwingUtilities.RIGHT);
		tfDay.setDocument(new IntegerTextFieldLimiter(2));
		tfDay.getDocument().addDocumentListener(dateListener);
		westUpperPane.add(tfDay);

		tfMonth = new JTextField();
		tfMonth.setLocation(228, 60);
		tfMonth.setSize(25, 25);
		tfMonth.setColumns(10);
		tfMonth.setHorizontalAlignment(SwingUtilities.RIGHT);
		tfMonth.setDocument(new IntegerTextFieldLimiter(2));
		tfMonth.getDocument().addDocumentListener(dateListener);
		westUpperPane.add(tfMonth);

		tfYear = new JTextField();
		tfYear.setLocation(254, 60);
		tfYear.setSize(40, 25);
		tfYear.setColumns(10);
		tfYear.setHorizontalAlignment(SwingUtilities.RIGHT);
		tfYear.setDocument(new IntegerTextFieldLimiter(4));
		tfYear.getDocument().addDocumentListener(dateListener);
		westUpperPane.add(tfYear);

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setLocation(299, 60);
		label.setSize(38, 25);
		i18nManager.configureI18NComponent(label, "DataEntry.lbl.edad");
		westUpperPane.add(label);

		tfAge = new JTextField();
		tfAge.setLocation(340, 60);
		tfAge.setSize(63, 25);
		tfAge.setColumns(10);
		tfAge.setHorizontalAlignment(SwingUtilities.RIGHT);
		tfAge.setEnabled(false);
		westUpperPane.add(tfAge);

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setLocation(434, 60);
		label.setSize(50, 25);
		i18nManager.configureI18NComponent(label, "DataEntry.lbl.dias");
		westUpperPane.add(label);

		tfDays = new JTextField();
		tfDays.setLocation(485, 60);
		tfDays.setSize(55, 25);
		tfDays.setText("999");
		tfDays.setColumns(10);
		tfDays.setHorizontalAlignment(SwingUtilities.RIGHT);
		tfDays.setEnabled(false);
		westUpperPane.add(tfDays);

		final JLabel lblTelefonos = new JLabel();
		lblTelefonos.setLocation(95, 90);
		lblTelefonos.setSize(80, 25);
		i18nManager.configureI18NComponent(lblTelefonos,
			"DataEntry.lbl.telefonos");
		westUpperPane.add(lblTelefonos);

		final JScrollPane scrollPane = new JScrollPane(listaTelefonos);
		scrollPane.setLocation(95, 120);
		scrollPane.setSize(135, 95);
		westUpperPane.add(scrollPane);

		final TableColumn columnTlf = listaTelefonos.getColumnModel().getColumn(0);
		columnTlf.setCellEditor(telephoneEditor);

		final TelephoneCellRenderer telephoneRenderer = new TelephoneCellRenderer();
		columnTlf.setCellRenderer(telephoneRenderer);

		listaTelefonos.clear();
		listaTelefonos.addItem(Telefono.NULL_VALUE);

		final JLabel lbDireccion = new JLabel();
		lbDireccion.setLocation(230, 90);
		lbDireccion.setSize(57, 25);
		i18nManager.configureI18NComponent(lbDireccion,
			"DataEntry.lbl.direccion");
		westUpperPane.add(lbDireccion);

		taAddress = new JTextArea();
		taAddress.setRows(3);
		taAddress.setColumns(10);
		taAddress.setLineWrap(true);
		taAddress.setWrapStyleWord(true);
		final JScrollPane scrollPaneDireccion = new JScrollPane(taAddress);
		scrollPaneDireccion.setLocation(228, 120);
		scrollPaneDireccion.setSize(312, 95);
		westUpperPane.add(scrollPaneDireccion);

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setLocation(310, 90);
		label.setSize(70, 25);
		i18nManager.configureI18NComponent(label, "DataEntry.lbl.pais");
		westUpperPane.add(label);

		comboPais = new JComboBox<>();
		comboPais.setLocation(390, 90);
		comboPais.setSize(150, 25);
		comboPais.setEditable(false);
		comboPais.setEnabled(true);
		comboPais.removeAllItems();
		Pais.getPaises().forEach(comboPais::addItem);
        comboPais.addItemListener(event -> {
			telephoneEditor.setDefaultCountryPrefix(((Pais) event.getItem()).getCountryCode());
			listaTelefonos.repaint();
		});
		comboPais.setSelectedItem(Pais.getDefaultCountry());
		westUpperPane.add(comboPais);

		int yPos = 220;
		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setLocation(leftMargin, yPos);
		label.setSize(60, 25);
		i18nManager.configureI18NComponent(label,
			"DataEntry.lbl.emails");
		westUpperPane.add(label);

		tfEmail = new JTextField();
		tfEmail.setLocation(95, yPos);
		tfEmail.setSize(445, 25);
		tfEmail.setColumns(10);
		westUpperPane.add(tfEmail);

		yPos += 30;
		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setLocation(leftMargin, yPos);
		label.setSize(50, 25);
		i18nManager.configureI18NComponent(label, "DataEntry.lbl.marca");
		westUpperPane.add(label);

		tfMark = new JTextField();
		tfMark.setLocation(leftMargin + 50, yPos);
		tfMark.setSize(52, 25);
		tfMark.setColumns(10);
		tfMark.setHorizontalAlignment(SwingUtilities.RIGHT);
		tfMark.setDocument(new TextFieldLimiter(2));
		tfMark.getDocument().addDocumentListener(
			new ColorChangerDocumentListener(tfMark, COLOR_MARKED, COLOR_UNMARKED));
		westUpperPane.add(tfMark);

		groupSelector.setLocation(130, yPos);
		groupSelector.setSize(410, 135);
		groupSelector.setBackground(background);
		groupSelector.setListaElementos(Grupo.getValues());
		Grupo.addChangeObserver(observable -> {
            try {
                groupSelector.setListaElementos(Grupo.getValues());
            } catch (final Exception e) {
                LOGGER.error("Error", e);
            }
        });
		westUpperPane.add(groupSelector);

		yPos += 30;
		chkListar = new JCheckBox();
		chkListar.setLocation(leftMargin, yPos);
		chkListar.setSize(100, 25);
		i18nManager.configureI18NComponent(chkListar, "DataEntry.lbl.listar");
		chkListar.setSelected(true);
		chkListar.setBorder(null);
		westUpperPane.add(chkListar);

		yPos += 25;
		chkDeleted = new JCheckBox();
		chkDeleted.setLocation(leftMargin, yPos);
		chkDeleted.setSize(100, 25);
		i18nManager.configureI18NComponent(chkDeleted,
			"DataEntry.lbl.borrado");
		chkDeleted.setSelected(false);
		chkDeleted.setBorder(null);
		westUpperPane.add(chkDeleted);

		westUpperPane.setBackground(background);
		chkListar.setBackground(background);
		chkDeleted.setBackground(background);
		comboPais.setBackground(background);
		comboSexo.setBackground(background);

		statsMessageLabel.setSize(westUpperPaneWidth, 25);
		statsMessageLabel.setBackground(Color.pink);
		statsMessageLabel.setOpaque(true);
		statsMessageLabel.setText(statsMessageFormat.format(null));

		westPane.add(statsMessageLabel, BorderLayout.SOUTH);
		westPane.add(createNotesPane(), BorderLayout.CENTER);

		yPos += 50;
		btnPrev = new JButton();
		btnPrev.setLocation(leftMargin, yPos);
		btnPrev.setSize(25, 25);
		btnPrev.setForeground(new Color(-13434625));
		btnPrev.setToolTipText(PropertiesHelper.getStringFromProperties(
			properties, "DataEntry.lbl.previo"));
		btnPrev.setEnabled(false);
		ImageTools.getImageIcon(PropertiesHelper
			.getStringFromProperties(properties, "btnPrevImage"))
			.ifPresent(btnPrev::setIcon);
		btnPrev.setActionCommand(PREV_COMMAND);
		westUpperPane.add(btnPrev);

		btnNext = new JButton();
		btnNext.setLocation(leftMargin + 25, yPos);
		btnNext.setSize(25, 25);
		btnNext.setForeground(new Color(-13434625));
		btnNext.setToolTipText(PropertiesHelper.getStringFromProperties(
			properties, "DataEntry.lbl.siguiente"));
		btnNext.setEnabled(false);
		ImageTools.getImageIcon(PropertiesHelper
			.getStringFromProperties(properties, "btnNextImage"))
			.ifPresent(btnNext::setIcon);
		btnNext.setActionCommand(NEXT_COMMAND);
		westUpperPane.add(btnNext);

		final Dimension dimension = new Dimension(westUpperPaneWidth, westUpperPaneHeight);
		westUpperPane.setPreferredSize(dimension);
		westUpperPane.setMaximumSize(dimension);
		westUpperPane.setMinimumSize(dimension);
		setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();

		gbc.weightx = 0;
		gbc.weighty = 0.5;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(westPane, gbc);

		btnImage = new ImageButton(properties);
		btnImage.setBackground(Colortable.getColor("deepskyblue3"));
        btnImage.addPopUpOption(PropertiesHelper.getStringFromProperties(
        	properties, "DataEntry.copyPath"), this::copyPath2Clipboard);
		i18nManager.configureI18NComponent(btnImage, "ImageButton.select");

		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(btnImage, gbc);
	}

    private void copyPath2Clipboard(final ActionEvent event) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(btnImage.getImagePath()), null);
    }

    private JPanel createNotesPane() {
		final JPanel notesPane = new JPanel(new BorderLayout());
		notesPane.add(i18nManager.configureI18NComponent(new JLabel(),
			"DataEntry.lbl.notas"), BorderLayout.NORTH);
		taNotes = new JTextArea();
		taNotes.getDocument().addDocumentListener(
			new ColorChangerDocumentListener(taNotes, COLOR_POST_IT, COLOR_UNMARKED));
		taNotes.setRows(5);
		taNotes.setColumns(5);
		taNotes.setLineWrap(true);
		taNotes.setWrapStyleWord(true);
		notesPane.add(new JScrollPane(taNotes), BorderLayout.CENTER);
		return notesPane;
	}

	void enableContactChange(final boolean enabled) {
		GUITools.containerMutator(this, component -> {
			if (component instanceof JTextComponent) {
				((JTextComponent) component).setEditable(enabled);
			} else if (!(component instanceof JLabel)
				&& !(component instanceof JScrollBar)) {
				component.setEnabled(enabled);
			}
		});
		btnImage.setActive(enabled);
		btnImage.setEnabled(true);
	}

	public void addActionListener(final ActionListener listener) {
		btnPrev.addActionListener(listener);
		btnNext.addActionListener(listener);
	}

	boolean isImageDirty() {
		return imageDirty;
	}

	void setImageDirty(final boolean imageDirty) {
		this.imageDirty = imageDirty;
	}
}