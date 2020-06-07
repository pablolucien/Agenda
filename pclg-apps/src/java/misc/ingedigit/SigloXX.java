/** SigloXX
	Convertidor de programas cobol para el anyo 2000
	980904 - 981009
*/
package misc.ingedigit;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

/** Convertidor de programas cobol para el anyo 2000. Modifica los campos que detecta como fechas
	para que acepten cuatro digitos en el anyo
	
	@author El Zorro
	@version 1.0 (980904 - 980910) - La proxima version sera a finales de 9.998 A.D.
*/
public class SigloXX extends FrameWithEvents implements ActionListener {
    private static final long serialVersionUID = -4296096394119922044L;
    private final TextField sourceName;
	private final TextField statusLine;
	private final Button saveBt;
	private final Button nextBt;
	private final Button convBt;
	private final Button batBt;
	private final Button scanBt;
	private final Button quitBt;
	private final List fileList;
	private final TextArea notepad;
	private final Filtro filtro = new Filtro("cbl cpy cpb fd");	// filtro para la lista de directorio
	private final String comment = "      * " + getClass().getName() + " " + (new Date()).toString();
	private final String newLine = System.getProperty("line.separator");
	private boolean FDFile;
	private int cambios;	// Cantidad de Cambios efectuados

	/**
		@param files Un arreglo de nombres de archivos a convertir.
	*/
	private SigloXX(final String[] files) {
		super("SigloXX: La solucion!");
        addWindowListener(this);    // Me interesan los eventos
		sourceName = new TextField();
		sourceName.setEditable(false);
		statusLine = new TextField();
		statusLine.setEditable(false);
		statusLine.setForeground(Color.red);
		final Panel northPanel = new Panel(new GridLayout(1, 2));
		northPanel.add(sourceName);
		northPanel.add(statusLine);
		final Panel buttonPanel = new Panel(new GridLayout(1, 5));	// ???? Por que funciona si tengo 6 botones?
		nextBt = addButton(buttonPanel, "Siguiente");
		convBt = addButton(buttonPanel, "Convertir");
		batBt = addButton(buttonPanel, "Automatico");
		scanBt = addButton(buttonPanel, "Revisar");
		saveBt = addButton(buttonPanel, "Guardar");
		saveBt.setForeground(Color.red);
		quitBt = addButton(buttonPanel, "Salir");
		fileList = new List(20, false);
		fileList.addActionListener(this);
		boolean batchMode = false;
		for(int i = 0; i < files.length; i++) {
			if(files[i].equals("-batch")) {
				batchMode = true;
			}
			else {
				fileList.add(files[i]);
			}
		}
		notepad = new TextArea(org.pclg.Elephants.text, 40, 82);
		notepad.setFont(new Font("Monospaced", Font.PLAIN, 12));
		add("North", northPanel);
		add("Center", notepad);
		add("South", buttonPanel);
		add("East", fileList);
		if(batchMode) {
			scan();
			batch();
			exit();
		}
	}


	/**
		Crea un boton y lo agrega a un container
		@param parent El container
		@param caption El texto del boton
		@return Una referencia al boton recien creado
	*/
	Button addButton(final Container parent, final String caption) {
		final Button b;
		b = new Button(caption);
		b.addActionListener(this);
		b.setForeground(Color.blue);
		parent.add(b);
		return(b);
	}

	private void editSource(final String file) {
		if(file == null) {
			return;
		}
		try {
			final File f = new File(file);
			final byte[] b = new byte[(int) f.length()];
			final DataInputStream in = new DataInputStream(new FileInputStream(f));
			in.read(b, 0, b.length);
			notepad.setText(new String(b));
			sourceName.setText(file);
			message("");
			if(file.toLowerCase().endsWith(".fd")) {
				FDFile = true;
			}
			else {
				FDFile = false;
			}
		}
		catch(final IOException ex) {
			message(ex.toString());
			ex.printStackTrace();
		}
	}


	/**
		Muestra un mensaje de status
		@param msg El mensaje que se quiere mostrar
	*/
	void message(final String msg) {
		statusLine.setText(msg);
	}



	/**
		Reemplaza el substring oldS por newS en s
		Asume que oldS existe una y solo una vez en s
		@param s El string a modificar
		@param oldS El substring a reemplazar
		@param newS El substring de reemplazo
		@return El string modificado
	*/
	private String replace(final String s, final String oldS, final String newS) {
		final int pos = s.indexOf(oldS);
		final String t = s.substring(0, pos) + newS + s.substring(pos + oldS.length());
		cambios++;
		return(t);
	}


	/**
		Realiza la conversion del fuente de cobol para que aguante un par de siglos mas

		Analisis del Gazza:
		PALABRAS CLAVES:
       		FECHA (Si tiene PIC de 6, pasarla a PIC de 8) (*)
       		FCHA (Si tiene PIC de 6, pasarla a PIC de 8) (*)
			agregar FCH 01 Dic '98
       		FEC (Si tiene PIC de 6, pasarla a PIC de 8) (*)
       		DESDE (Si tiene PIC de 6, pasarla a PIC de 8)
       		HASTA (Si tiene PIC de 6, pasarla a PIC de 8)
       		ANO (Si tiene PIC de 2, pasarlo a PIC de 4)
       		ANIO (Si tiene PIC de 2, pasarlo a PIC de 4)
       		A-COMPROB
		NOTA: 
			ANO puede venir PIC 99, PIC 9(9), PIC XX o PIC X(2)
			FECHA puede venir PIC 999999, PIC 9(6), PIC XXXXXX o PIC X(6)
        	Cuidaddo con los COMPUTATIONAL (**)
        	Cuidado con los OCCURS (**)
        	(*) Cuidaddo con FECHA de 4 (MES-ANO)
        	Cuidado con los REDEFINES y el campo original no tiene nombre de FECHA
        	(**) Hay que modificar RECORD CONTAINS

// Modificaciones del 01/12/1998
// no modificar cuando el anio ya viene de 4 digitos (9999)
// orden en que se chequea la fecha de 4 digitos (pongo warning despues de convertirla yo mismo)

// revisar tambien FCH

// ver los pic 9(02) 9(06) ...

	*/


	// Palabra clave (Anos)
	private final String [] posiblesAnos = {"ANO", "ANIO", " A-COMPROB"};
	// PIC problema, solucion
	private final String [][] posiblesPICsDeAnos = {
		{"PIC 9(2)", "PIC 9(4)"},
		{"PIC 9(02)", "PIC 9(4)"},
		{"PIC 9(002)", "PIC 9(4)"},
		{"PIC 9(0002)", "PIC 9(4)"},
		{"PIC 9(00002)", "PIC 9(4)"},
		{"PIC 99.", "PIC 9(4)"},
		{"PIC 99 ", "PIC 9(4)"},
		{"PIC  99.", "PIC 9(4)"},
		{"PIC  99 ", "PIC 9(4)"},
		{"PIC   99.", "PIC 9(4)"},
		{"PIC   99 ", "PIC 9(4)"},
		{"PIC X(2)", "PIC X(4)"},
		{"PIC XX ", "PIC X(4)"},
		{"PIC XX.", "PIC X(4)"}
	};

	// Palabra clave (Fechas)
	private final String [] posiblesFechas = {"HOY", "FEC", "FCH", "DESDE", "HASTA"};
	// PIC problema, solucion
	private final String [][] posiblesPICsDeFechas = {
		{"PIC 9(6)", "PIC 9(8)"},
		{"PIC 9(06)", "PIC 9(8)"},
		{"PIC 9(006)", "PIC 9(8)"},
		{"PIC 9(0006)", "PIC 9(8)"},
		{"PIC 9(00006)", "PIC 9(8)"},
		{"PIC 999999.", "PIC 9(8)"},
		{"PIC 999999 ", "PIC 9(8)"},
		{"PIC  999999.", "PIC 9(8)"},
		{"PIC  999999 ", "PIC 9(8)"},
		{"PIC X(6)", "PIC X(8)"},
		{"PIC XXXXXX.", "PIC X(8)"},
		{"PIC XXXXXX ", "PIC X(8)"}
	};


	void convert() {
		try {
			message("Convirtiendo ...");
			final StringBuilder sb = new StringBuilder();
			final BufferedReader reader = new BufferedReader(new StringReader(notepad.getText()));
			String s;
			int status = 0;
			cambios = 0;
			boolean withRecordContains = false;
			boolean withOccurs = false;
			String recordContainsLine = null;
			int recordPosition = 0;
			int recordIncrease = 0;
			int currLevel = -1;	// FD, 01, 03, etc.
			final int lastLevel = -1;
			while((s = reader.readLine()) != null) {
				// Si es un comentario o una linea en blanco no nos interesa.
				// Tampoco si estamos en la PROCEDURE DIVISION
				if(s.startsWith("       PROCEDURE"))	// No seguimos convirtiendo
				{
					status = 1;
				}
				if(s.length() >= 7 && s.charAt(6) != '*' && status == 0) {
					s = s.toUpperCase();

					// Verificamos en que nivel estamos. Solo es util para los FD
					final String t = s.substring(7).trim();
//System.out.println("t = " + t);
					if(t.startsWith("FD")) {
						currLevel = 0;
					}
					else {
						try {
							currLevel = Integer
									.parseInt(t.substring(0, t.indexOf(' ')));
						}
						catch (final NumberFormatException ex) {
						}
						catch (final StringIndexOutOfBoundsException ex) {
						}
					}
//System.out.println("currLevel = " + currLevel);

					if(s.indexOf("OCCURS ") >= 0) {
						withOccurs = true;
					}
					if(s.indexOf("RECORD CONTAINS ") >= 0) {
						withRecordContains = true;
						recordPosition = sb.length();	// Posicion justo antes de la linea
						recordContainsLine = s;			// Guardamos la original
					}

					// Fechas de 4 digitos (mes-anyo)
					if((s.indexOf("FEC") >= 0 || s.indexOf("FCH") >= 0 || s.indexOf("DESDE") >= 0 || s.indexOf("HASTA") >= 0) &&
						(s.indexOf("PIC 9(4)") >= 0 || s.indexOf("PIC 9999") >= 0 || s.indexOf("PIC X(4)") >= 0 || s.indexOf("PIC XXXX") >= 0)) {
							sb.append(comment).append(newLine).append("WARNING: FECHA DE 4 DIGITOS").append(newLine);
							recordIncrease += 2;
					}

					// Modificacion del ANO
loopAno:			for(int i = 0; i < posiblesAnos.length; i++) {
						for(int j = 0; j < posiblesPICsDeAnos.length; j++) {
							if(s.indexOf(posiblesAnos[i]) >= 0 && s.indexOf(posiblesPICsDeAnos[j][0]) >= 0) {
								sb.append(comment).append(newLine);
								sb.append(s.substring(0, 6)).append("*")
										.append(s.substring(7)).append(newLine);
								s = replace(s, posiblesPICsDeAnos[j][0], posiblesPICsDeAnos[j][1]);
								recordIncrease += 2;
								break loopAno;
							}
						}
					}
					// Modificacion de la FECHA
loopFecha:			for(int i = 0; i < posiblesFechas.length; i++) {
						for(int j = 0; j < posiblesPICsDeFechas.length; j++) {
							if(s.indexOf(posiblesFechas[i]) >= 0 && s.indexOf(posiblesPICsDeFechas[j][0]) >= 0) {
								sb.append(comment).append(newLine);
								sb.append(s.substring(0, 6)).append("*")
										.append(s.substring(7)).append(newLine);
								s = replace(s, posiblesPICsDeFechas[j][0], posiblesPICsDeFechas[j][1]);
								recordIncrease += 2;
								break loopFecha;
							}
						}
					}
				}
				// Agregamos la linea (original o modificada)
				sb.append(s).append(newLine);
			}
			//  Si tiene "RECORD CONTAINS" debemos modificarlo (solo los *.fd)
			if(FDFile && withRecordContains && recordIncrease > 0) {
				// Agregamos el comentario de identificacion
				sb.insert(recordPosition, comment);
				recordPosition += comment.length();
				sb.insert(recordPosition, newLine);
				// Agregamos la original en comentario
				recordPosition += newLine.length() + 6;
				sb.setCharAt(recordPosition, '*');
				// Agregamos la modificada
				final int pos = recordContainsLine.indexOf("CONTAINS") + 9;
				int recordLength = 0;
				try {
					recordLength = Integer.parseInt(recordContainsLine.substring(pos, recordContainsLine.indexOf(" C", pos)).trim()); //?????   arreglar cuando hay varios ' ' entre CONTAINS y el numero
				}
				catch(final Exception ex) { }
				recordPosition += recordContainsLine.length() - 5;	// -5 ?????
				// Si tiene OCCURS estamos en aguas turbias; tambien si recordLength == 0
				if(withOccurs || recordLength == 0) {
					sb.insert(recordPosition, "WARNING--> RECORD CONTAINS " +
							(recordLength + recordIncrease) + " CHARACTERS"
							+ newLine);
				}
				else {
					sb.insert(recordPosition, "           RECORD CONTAINS " +
							(recordLength + recordIncrease) + " CHARACTERS"
							+ newLine);
				}
			}
			notepad.setText(sb.toString());
			reader.close();
			message(String.valueOf(cambios) + " cambios efectuados");
		}
		catch(final IOException ex) {
			message(ex.toString());
			ex.printStackTrace();
		}
	}


	/** 
		Crea un archivo llamado `filename` y guarda all `content`
		Si el archivo existe lo renombra a `filename`.old
		@param filename El archivo
		@param content Lo que se va a almacenar
		@param backup Indica si se va a crear un archivo de respaldo
	*/
	void save(final String filename, final String content, final boolean backup) {
		final File f = new File(filename);
		if(backup && f.exists()) {
			if(!f.renameTo(new File(filename + ".old"))) {
				message("Error renombrando a " + filename);
				return;
			}
		}

		try {
			final DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
			dos.writeBytes(content);
			dos.close();
		}
		catch(final IOException ex) {
			message(ex.toString());
			ex.printStackTrace();
		}
	}


	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource() == saveBt) {
			save(sourceName.getText(), notepad.getText(), true);
		}
		if(e.getSource() == nextBt) {
			fileList.select(fileList.getSelectedIndex() + 1);
//			fileList.makeVisible(fileList.getSelectedIndex());
			editSource(fileList.getSelectedItem());
		}
		else if(e.getSource() == convBt) {
			convert();
		}
		else if(e.getSource() == batBt) {
			batch();
		}
		else if(e.getSource() == scanBt) {
			scan();
		}
		else if(e.getSource() == quitBt) {
        	exit();
		}
		else if(e.getSource() == fileList) {
			editSource(fileList.getSelectedItem());
		}
	}


	/**
	 * Procesa todos los archivos seleccionados
	 */
	private void batch() {
			for(int i = 0; i < fileList.getItemCount(); i++) {
				fileList.select(i);
//				fileList.makeVisible(i);
				editSource(fileList.getSelectedItem());
				convert();
//				if(cambios > 0)
					save(sourceName.getText(), notepad.getText(), false);
			}
			message("Listo el pollo");
	}


	/**
	 * Revisa el directorio y crea la lista de archivos
	 */
	private void scan() {
		final File dir = new File(".");
		final String [] dirList = dir.list(filtro);
		fileList.removeAll();
        StringArraySorter.sort(dirList);
		for(int i = 0; i < dirList.length; i++) {
			fileList.add(dirList[i]);
		}
	}

	public static void main(final String [] args) {
		final SigloXX v = new SigloXX(args);
        v.pack();
		v.setVisible(true);
	}
}
