// ******************************** package
package net.asintec.migrator;


// ******************************** imports
import org.pclg.tools.BoundsInfo;
import org.pclg.tools.GUITools;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.ToolBox;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;
/**
	TextDatabaseEditor
	@author El Coyote Cojo
	@version 2001.nov.28 18:30:14, CET
*/
public class TextDatabaseEditor extends JFrame implements ActionListener, CaretListener {
    private static final long serialVersionUID = 20640638649728495L;
    // ******************************** Variables de clase

	// ******************************** Variables de instancia
	// -------------
	private final JTextField headerField = new JTextField();
	private final JTextArea textArea = new JTextArea();
	private final JScrollPane scrollPane = new JScrollPane(textArea);
	private final JList fieldsList = new JList();
	private final JScrollPane fieldsListScrollPane = new JScrollPane(fieldsList);

	// ------------- Los botones de control de la aplicación
	private final JPanel buttonPanel  = new JPanel((new GridLayout(0, 2)));
	private final JButton loadBt      = GUITools.addButton(this, buttonPanel, "Leer datos");
	private final JButton markBt      = GUITools.addButton(this, buttonPanel, "Marcar");
	private final Font textFont		= new java.awt.Font("Courier New", Font.BOLD, 12);

	/**
		Propiedades persistentes
		--author El Coyote Cojo
		--version 2001.nov.28 18:30:14, CET
	*/
	private final Properties applicationProps = new Properties();
	private Rectangle bounds;

	// ******************************** Constructores

	/**
		Constructor por omision
		--author El Coyote Cojo
		--version 2001.nov.28 18:30:14, CET
	*/
	private TextDatabaseEditor() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);  // La accion por omision es HIDE_ON_CLOSE
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent ev) {
				saveProperties();
				GUITools.exitApplication(ev.getWindow(), false);
			}
		});
		// Cargamos las propiedades persistentes
		loadProperties();
		// Antes de salir de la aplicacion salvamos las propiedades persistentes
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				saveProperties();
			}
		});

		getContentPane().add(headerField, BorderLayout.NORTH);
		getContentPane().add(fieldsListScrollPane, BorderLayout.EAST);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		headerField.setFont(textFont);

		textArea.setFont(textFont);
		final Caret caret = new DefaultCaret() {
            private static final long serialVersionUID = -6481607567058492425L;

            @Override
					public void paint(final Graphics g) {
						if (!isVisible()) {
							return;
						}
						try {
							final JTextComponent c = getComponent();
							final int dot = getDot();
							final Rectangle r = c.modelToView(dot);
							//g.setColor(c.getCaretColor());
							g.setColor(Color.blue);
							//g.drawLine(r.x, r.y + r.height - 1, r.x + 3, r.y + r.height - 1);
							g.drawLine(r.x, 0, r.x, r.y + r.height - 1);
						}
						catch (final BadLocationException e) {
							System.err.println(e);
						}
					}

					// specify the size of the caret for redrawing
					// and do repaint() -- this is called when the
					// caret moves
					protected synchronized void damage(final Rectangle r) {
						if (r == null) {
							return;
						}
						x = r.x;
						y = r.y + r.height - 1;
						width = 4;
						height = 10;
						repaint();
					}
		};

		textArea.setCaret(caret);
//		textArea.addCaretListener(this);

		if(bounds == null) {
			pack();
			GUITools.center(this, null);   // Centrar en la pantalla
		}
		else {
			setBounds(
					bounds);			// Ultima posicion guardada de la pantalla
		}

		setVisible(true);
		loadBt.doClick();
	}

	// ******************************** Metodos de instancia

	/** implementacion de CaretListener */
	public void caretUpdate(final CaretEvent e) {
		// Obtener la posicion en el texto
		final int dot = e.getDot();
		final int mark = e.getMark();
		if(dot == mark) {  // no hay selection
			try {
				final Rectangle caretCoords = textArea.modelToView(dot);
				//	Convertir to view coordinates
				System.out.println("caret: text position: " + dot + ", view location = [" + caretCoords.x + ", " + caretCoords.y + "]" + "\n");
			}
			catch(final BadLocationException ble) {
				System.out.println("caret: text position: " + dot + "\n");
			}
		}
		else if(dot < mark) {
			System.out.println("selection from: " + dot + " to " + mark + "\n");
		}
		else {
			System.out.println("selection from: " + mark + " to " + dot + "\n");
		}
	}


	/** implementacion de ActionListener */
	public void actionPerformed(final ActionEvent event) {
		if(event.getSource() == loadBt) {
			try (final BufferedReader bufferedReader = new BufferedReader(new FileReader("CLI_MIGR2.TXT"))) {
				textArea.setText("");
				String line;
				// Leemos algunas lineas del archivo
				line = bufferedReader.readLine();
textArea.append("Lo de arriba debe estar coordinado con lo de abajo.\n");
				if(line != null) {
					headerField.setText(line);
					textArea.append(line + "\n");
					textArea.append(ToolBox.pad("", line.length(), '-') + "\n");
					final StringTokenizer stringTokenizer = new StringTokenizer(line);
					final int fieldCount = stringTokenizer.countTokens();
					final String[] fieldNames = new String[fieldCount];
					for(int i = 0; i < fieldNames.length; i++) {
						fieldNames[i] = stringTokenizer.nextToken();
					}
					fieldsList.setListData(fieldNames);
				}

				for(int i = 0; i < 50; i++) {
					line = bufferedReader.readLine();
					if(line == null) {
						break;
					}
					textArea.append(line + "\n");
				}
	/*
				StringTokenizer stringTokenizer1 = new StringTokenizer(line1);
				StringTokenizer stringTokenizer2 = new StringTokenizer(line2);
				int fieldCount = stringTokenizer1.countTokens();
				fieldNames = new String[fieldCount];
				fieldStarts = new int[fieldCount];
				fieldEnds = new int[fieldCount];
				for(int i = 0; i < fieldNames.length; i++) {
					fieldNames[i] = stringTokenizer1.nextToken();
					String size;
					size = stringTokenizer2.nextToken();
					fieldStarts[i] = Integer.parseInt(size.substring(0, size.indexOf('-')).trim()) - 1;
					fieldEnds[i] = Integer.parseInt(size.substring(size.indexOf('-') + 1).trim());
				}
				debug(fieldStarts);
				debug(fieldEnds);
	//			debugFields();
	*/
			}
			catch(final IOException ex) {
				ToolBox.showInfo(ex);
			}
		}
		else if(event.getSource() == markBt) {
			final int start = textArea.getSelectionStart();
			final int end = textArea.getSelectionEnd();
			System.out.println("start: " + start + ", end: " + end);

			final int dot = textArea.getCaretPosition();
			try {
				final Rectangle caretCoords = textArea.modelToView(dot);
				//	Convertir to view coordinates
				System.out.println("caret: text position: " + dot + ", view location = [" + caretCoords.x + ", " + caretCoords.y + "]" + "\n");
			}
			catch(final BadLocationException ble) {
				System.out.println("caret: text position: " + dot + "\n");
			}
		}
	}

	/**
		Carga todas propiedades de la aplicacion
		--author El Coyote Cojo
		--version 2001.nov.28 18:30:14, CET
	*/
	private void loadProperties() {
		try (final FileInputStream in = new FileInputStream(getClass().getName() + ".properties")) {
			applicationProps.load(in);
			bounds = PropertiesHelper.getBounds(applicationProps, "").getBounds();
		} catch(final IOException ex) {
			ToolBox.showInfo(ex);
		}
	}

	/**
		Guarda todas propiedades en el archivo de configuracion
		--author El Coyote Cojo
		--version 2001.nov.28 18:30:14, CET
	*/
	private void saveProperties() {
		bounds = getBounds();
		PropertiesHelper.saveBounds(new BoundsInfo(bounds), applicationProps, "");

		try (final FileOutputStream out = new FileOutputStream(getClass().getName() + ".properties")) {
			applicationProps.store(out, "Configuracion de " + getClass().getName());
			out.close();
		} catch(final IOException ex) {
			ToolBox.showInfo(ex);
		}
	}

	/**
		Guarda la estructura de un archivo de textos de los nuestros
	*/
	public static /* de momento*/ void saveStruct(final String txtFileName, final String structFileName) {
		final File outFile = new File(structFileName);
			try (final PrintWriter pw = new PrintWriter(new FileWriter(outFile))) {
				pw.println("### Estructrura de " + txtFileName);
				pw.println("###");
				pw.close();
			} catch(final IOException ex) {
				ToolBox.showInfo(ex);
			}
	}


	// ******************************** Metodos estaticos

	/**
		Ayuda al usuario
		--author El Coyote Cojo
		--version 2001.nov.28 18:30:14, CET
	*/
	public static void usage(final String[] args) {
		System.out.println("Usage: java TextDatabaseEditor " +  "");
		System.exit(0);
	}

	/**
		Ejecuta la aplicación
		--author El Coyote Cojo
		--version 2001.nov.28 18:30:14, CET
	*/
	public static void main(final String[] args) {
		new TextDatabaseEditor();
	}
}
