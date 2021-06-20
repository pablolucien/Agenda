package org.pclg.root;// ******************************** package

// ******************************** imports

import org.pclg.tools.FontDialog;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;
import org.pclg.tools.ToolBox;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public final class ImgCreator extends JFrame implements ActionListener {
    private static final long serialVersionUID = -5089856397957595885L;
//	private JPanel northPanel = new JPanel();

	private final JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
	private final JButton fontBt = GUITools.addButton(this, buttonPanel, "Fuente", Color.blue, "Seleccionar el tipo de letra");
	private final JButton backColorBt = GUITools.addButton(this, buttonPanel, "Color de Fondo", Color.blue, "Seleccionar el color de fondo");
	private final JButton foreColorBt = GUITools.addButton(this, buttonPanel, "Color de Letra", Color.blue, "Seleccionar el color del texto");
	private final JButton showBt = GUITools.addButton(this, buttonPanel, "Mostrar");
	private final JButton saveBt = GUITools.addButton(this, buttonPanel, "Guardar");
	private final JButton exitBt = GUITools.addButton(this, buttonPanel, "Salir", Color.blue, "Salir de la aplicacion");
	private final JTextField texto = new JTextField("Mensaje");
	private final JLabel imagen = new JLabel();
	private Font imageFont = getFont();
	private final FontDialog fontDialog = new FontDialog(this);
	private final JFileChooser fileChooser = new JFileChooser(".");
//	private JColorChooser colorChooser = new JColorChooser();
	private Color imageForeColor = Color.black;
	private Color imageBackColor = Color.white;
	// ??? el ancho debe ser multiplo de 32 por ahora
	private int imageWidth = 64;
	private int imageHeight = 54;

	private BufferedImage bufferImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
	private static final int APP_WIDTH = 500;
	private static final int APP_HEIGHT = 300;
	private static final String COLOR_PROMPT_1 = "Elija un colorete ...";
	private static final String COLOR_PROMPT_2 = "Escoja un colorin ...";
	private static final String SAVE_PROMPT = "Guardar como ...";

	public ImgCreator() {
		super("ImgCreator Sahara");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent ev) {
				GUITools.exitApplication((Window) ev.getSource(), false);
			}
		});

//		northPanel.add(texto);
		getContentPane().add(texto, BorderLayout.NORTH);
		getContentPane().add(imagen, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setSize(APP_WIDTH, APP_HEIGHT);
	}

	public void setImageSize(final int width, final int height) {
		imageWidth = width;
		imageHeight = height;
	}


	@Override
	public void actionPerformed(final ActionEvent ev) {
		if (ev.getSource() == fontBt) {
			fontDialog.setModal(true);
			fontDialog.setVisible(true);
			imageFont = fontDialog.getSelectedFont();
			showImage();
		} else if (ev.getSource() == saveBt) {
/*
            fileChooser.addChoosableFileFilter(new ImageFilter());
            fileChooser.setFileView(new ImageFileView());
            fileChooser.setAccessory(new ImagePreview(fileChooser));
*/
//			Locale l = new Locale("es", "ES");	// ???? Sirve para algo
//			Locale l = getLocale();	// ???? Sirve para algo
//System.out.println(l.getDisplayLanguage());
//            fileChooser.setLocale(l);
			final int returnVal = fileChooser.showDialog(this, SAVE_PROMPT);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					final File file = fileChooser.getSelectedFile();
					ImageTools.saveBitmap(bufferImage, file.getAbsolutePath());
//                  ToolBox.saveJPEG(bufferImage, "kkk.jpg");
				} catch (final IOException ex) {
					ToolBox.showInfo(ex);
				}
			}
		} else if (ev.getSource() == foreColorBt) {
			final Color color = JColorChooser.showDialog(this, COLOR_PROMPT_1, Color.white);
			if (color != null) {
				imageForeColor = color;
			}
			showImage();
		} else if (ev.getSource() == backColorBt) {
			final Color color = JColorChooser.showDialog(this, COLOR_PROMPT_2, Color.white);
			if (color != null) {
				imageBackColor = color;
			}
			showImage();
		} else if (ev.getSource() == showBt) {
			showImage();
		} else if (ev.getSource() == exitBt) {
			GUITools.exitApplication(this, false);
		}
	}

	@Override
	public void paint(final Graphics gg) {
		super.paint(gg);
		if (bufferImage != null) {
			final Graphics graphics = imagen.getGraphics();
			graphics.drawImage(bufferImage,
					(imagen.getWidth() - bufferImage.getWidth()) / 2,
					(imagen.getHeight() - bufferImage.getHeight()) / 2, this);
		}
	}


	public Image createTextImage(final String text) {
		bufferImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		if (imageFont == null) {
			imageFont = getFont();
		}
		final Dimension dim = new Dimension(bufferImage.getWidth(), bufferImage.getHeight());
		// Obtenemos fm de una etiqueta en vez de del Toolkit para evitar la "deprecation"
		//FontMetrics fm = java.awt.Toolkit.getDefaultToolkit().getFontMetrics(imageFont);
		//JLabel l = new JLabel("Sample Text");
//FontMetrics fm = l.getFontMetrics(imageFont);
		final FontMetrics fm = getFontMetrics(imageFont);
		final Graphics graphics = bufferImage.getGraphics();
		final int textWidth;
		final int textHeight;
//Revisar este codigo  ???????????????????????????
/*
		textHeight = fm.stringHeight(text);
*/
		final Rectangle2D rect = fm.getStringBounds(text, graphics);
//		textWidth = (int) rect.getWidth();
		// Tomado de la documentacion de FontMetrics
		// The advance textWidth is the sum of the advance widths of each of the characters in the
		// character array. The advance of a String is the distance along the baseline of the String.
		// This distance is the textWidth that should be used for centering or right-aligning the String.
		textWidth = fm.stringWidth(text);

		textHeight = (int) rect.getHeight();
//System.out.println("Dimensiones de la imagen: " + fm.getDescent() + " " + fm.getAscent());
//System.out.println("Dimensiones de la imagen: " + rect);
//System.out.println("Dimensiones de la imagen: " + dim.textWidth + " x " + dim.textHeight);
//System.out.println("Dimensiones del texto: " + textWidth + " x " + textHeight);
		graphics.setColor(Color.yellow);
		graphics.setFont(imageFont);
		graphics.setColor(imageBackColor);
		graphics.fillRect(0, 0, dim.width, dim.height);
		graphics.setColor(imageForeColor);
		graphics.drawString(text, (dim.width - textWidth) / 2, (dim.height + textHeight) / 2);
		return bufferImage;
	}

	public void setImageBackground(final Color color) {
		imageBackColor = color;
	}

	public void setImageForeground(final Color color) {
		imageForeColor = color;
	}

	private void showImage() {
		createTextImage(texto.getText());
		repaint();
	}


	public static void main(final String[] args) {
		new ImgCreator().setVisible(true);
	}
}
