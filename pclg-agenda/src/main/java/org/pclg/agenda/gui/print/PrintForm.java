package org.pclg.agenda.gui.print;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.AgendaUtil;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.Pais;
import org.pclg.gui.FancyButtonPanel;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ImageTools;
import org.pclg.tools.ObservableProperties;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.ToolBox;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Pablo
 * @since 7/08/13 7:49
 */
public class PrintForm extends JPanel implements ActionListener {
	private static final long serialVersionUID = -3896247598841217365L;
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final Font PREVIEW_FONT = new Font("Monospaced", Font.PLAIN, 12);
	private static final Font PRINTER_FONT = new Font("Monospaced", Font.PLAIN, 8);
    private final JTabbedPane tabbedPane;
	private final ListaPaises listaPaises = new ListaPaises();
	private final JTextArea taPrintPreview = new JTextArea();
    private final JScrollPane scrollPanePrintPreview = new JScrollPane(taPrintPreview);
	private final PreviewListener previewActionListener;

	private final class PreviewListener extends MouseAdapter implements ActionListener {
		private static final char SPACE = ' ';
		private static final char DOT = '.';
		private final AgendaDb agendaDb;

		PreviewListener(final AgendaDb agendaDb) {
			this.agendaDb = agendaDb;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			doPreview();
		}

		@Override
		public void mouseClicked(final MouseEvent mouseEvent) {
			if (mouseEvent.getClickCount() == 2) {
				doPreview();
			}
		}

		private void doPreview() {
			final List<Pais> valuesList = listaPaises.getSelectedValuesList();

			taPrintPreview.setText("");
			if (valuesList.isEmpty()) {
				LOGGER.info("Selecci�n VAC�A.");
			} else {
				try {
					final List<AgendaRecord> records = agendaDb
						.selectAllContactsForTelephoneList(valuesList);
					final List<String> lines = new ArrayList<>(
						records.size());
					for (final AgendaRecord record : records) {
						prepareRecord4Print(lines, record, 3);
					}
//						int linesPerPage = 41;
//						int totalPages = (int) Math.ceil(lines.size() / (linesPerPage * 2));
//						List<List<String>> pages = new ArrayList<List<String>>(totalPages);
//
//						for (int pageNr = 0; pageNr < totalPages; pageNr++) {
//							List<String> page = new ArrayList<String>(linesPerPage);
//							pages.add(page);
//							page.add("Listado de tel�fonos. P�gina " + (pageNr + 1) + " de " + totalPages);
//							page.add("----------- \n");
//							for (int lineNr = 0; lineNr < linesPerPage; lineNr++) {
//								String fullLine = lines.get(pageNr * linesPerPage * 2 + lineNr);
//								final int contiguouslineNr = pageNr * linesPerPage * 2 + lineNr + linesPerPage;
//								if (contiguouslineNr <= lines.size()) {
//									fullLine += " ::: " + lines.get(contiguouslineNr);
//								}
//								page.add(fullLine + '\n');
//							}
//						}
//
//						for (List<String> page : pages) {
//							for (String string : page) {
//								taPrintPreview.append(string);
//							}
//						}

					for (final String line : lines) {
						taPrintPreview.append(line);
						taPrintPreview.append("\n");
					}
				} catch (final SQLException e1) {
					LOGGER.log(Level.ERROR, "Error al imprimir", e1);
				}
			}
			// TODO: Posicionarme en el principio
//			try {
//				taPrintPreview.scrollRectToVisible(taPrintPreview.modelToView(0));
//			} catch (BadLocationException e) {
//				LOGGER.log(Level.ERROR, "Ignoring exception", e);
//			}
            final JScrollBar verticalScrollBar = scrollPanePrintPreview.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMinimum());
        }

		private void prepareRecord4Print(final List<String> lines,
			final AgendaRecord record, final int nrTelefsPerLine) {
			final String nombre = record.getFirstname() == null ? "" : record.getFirstname();
			final String apellido = record.getLastname() == null ? ""
				: record.getLastname();
			final int size = record.getTelephones().size();
			final int nrChunks = (int) Math.ceil((double) size / nrTelefsPerLine);
            final String mask = record.getCountry().getFormatoTelefono();
			final String telefonos = AgendaUtil.concatenarTelefonos(
				record.getTelephones().subList(0, Math.min(nrTelefsPerLine, size)),
                mask);
			lines.add(ToolBox.pad(nombre + SPACE + apellido + SPACE, 42, DOT)
				+ SPACE + telefonos);
			for (int ii = 1; ii < nrChunks; ii++) {
                lines.add(ToolBox.pad(" ", 42, SPACE) + SPACE
					+ AgendaUtil.concatenarTelefonos(
						record.getTelephones().subList(ii * nrTelefsPerLine,
                            Math.min((ii + 1) * nrTelefsPerLine, size)), mask));
			}
		}

	}

	public PrintForm(final Properties properties, final AgendaDb agendaDb,
		    final JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
		if (properties instanceof ObservableProperties) {
			((ObservableProperties) properties).addChangeObserver(this::setUpButtonsLabels);
		}
		setLayout(new BorderLayout());
		previewActionListener = new PreviewListener(agendaDb);
        listaPaises.addMouseListener(previewActionListener);
		add(listaPaises, BorderLayout.EAST);

		taPrintPreview.setFont(PREVIEW_FONT);
		add(scrollPanePrintPreview, BorderLayout.CENTER);

		add(createButtonPanel(properties), BorderLayout.SOUTH);
	}

	// Esto es una chapucilla.
	
	private JButton btnCancel;
	private JButton btnPreview;
	private JButton btnClearPreview;
	private JButton btnPrint;
	
	private JPanel createButtonPanel(final Properties properties) {
		btnCancel = new JButton(
			PropertiesHelper.getStringFromProperties(properties,
				"CancelButton"),
			ImageTools.getImageIcon(PropertiesHelper
				.getStringFromProperties(properties,
					"CancelImage")).orElse(null));
		btnPreview = new JButton(PropertiesHelper
			.getStringFromProperties(properties, "PrintForm.previewButton"),
			ImageTools.getImageIcon(PropertiesHelper
				.getStringFromProperties(properties, "PrintForm.previewImage")).orElse(
				null));
		btnClearPreview = new JButton(PropertiesHelper
			.getStringFromProperties(properties, "PrintForm.newButton"),
			ImageTools.getImageIcon(PropertiesHelper
				.getStringFromProperties(properties, "PrintForm.newImage")).orElse(
				null));
		btnPrint = new JButton(PropertiesHelper
			.getStringFromProperties(properties, "PrintForm.printButton"),
			ImageTools.getImageIcon(PropertiesHelper
				.getStringFromProperties(properties, "PrintForm.printImage")).orElse(
				null));
		btnCancel.addActionListener(this);

		btnPreview.addActionListener(previewActionListener);

		btnClearPreview.addActionListener(e -> taPrintPreview.setText(""));

		btnPrint.addActionListener(e -> {
            try {
                final PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
                //attributeSet.add(new Copies(3));
                attributeSet.add(Sides.DUPLEX);
                taPrintPreview.setFont(PRINTER_FONT);
                taPrintPreview.print(
                    new MessageFormat(PropertiesHelper
                        .getStringFromProperties(properties,
                            "listado.telefonico.header")),
                    new MessageFormat(PropertiesHelper
                        .getStringFromProperties(properties,
                            "listado.telefonico.footer")), true, null,
                    attributeSet, true);
                taPrintPreview.setFont(PREVIEW_FONT);
            } catch (final PrinterException e1) {
                JOptionPane.showMessageDialog(PrintForm.this,
                    PropertiesHelper.getStringFromProperties(properties,
                        "print.error.msg") + e1.getMessage(),
                    PropertiesHelper.getStringFromProperties(properties,
                        "error.title"),
                        JOptionPane.ERROR_MESSAGE);
                LOGGER.log(Level.ERROR, LoggerFactory.ERROR_TAG, e1);
            }
        });

		return new FancyButtonPanel(btnPreview, btnPrint, btnClearPreview, btnCancel);
	}

	/** Action a ejecutar por el bot�n 'Cancelar' o el aspa del TabPanel. */
	@Override
	public void actionPerformed(final ActionEvent e) {
		tabbedPane.remove(this);      // Hay m�s referencias ? Ver si se ejecuta finalize()
		tabbedPane.setSelectedIndex(0);
	}

	private void setUpButtonsLabels(final Properties properties) {
		btnCancel.setText(
			PropertiesHelper.getStringFromProperties(properties, "CancelButton"));
		btnCancel.setIcon(ImageTools.getImageIcon(
			PropertiesHelper.getStringFromProperties(properties, "CancelImage")).orElse(
			null));
		btnPreview.setText(PropertiesHelper
			.getStringFromProperties(properties, "PrintForm.previewButton"));
		btnPreview.setIcon(ImageTools.getImageIcon(PropertiesHelper
			.getStringFromProperties(properties, "PrintForm.previewImage")).orElse(
			null));
		btnClearPreview.setText(PropertiesHelper
			.getStringFromProperties(properties, "PrintForm.newButton"));
		btnClearPreview.setIcon(ImageTools.getImageIcon(PropertiesHelper
			.getStringFromProperties(properties, "PrintForm.newImage")).orElse(
			null));
		btnPrint.setText(PropertiesHelper
			.getStringFromProperties(properties, "PrintForm.printButton"));
		btnPrint.setIcon(ImageTools.getImageIcon(PropertiesHelper
			.getStringFromProperties(properties, "PrintForm.printImage")).orElse(
			null));
	}
}
