package org.pclg.root;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.media.imagetools.ImageChooser;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.pclg.tools.ImageTools.NULL_ICON;

public class ThumbsViewer extends JFrame implements ActionListener {
    private static final long serialVersionUID = -4789287494200259649L;
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final String SELECCIONAR_IMAGEN = "Seleccionar Imagen";
    private static final String CARGAR_IMAGENES = "Cargar Imagenes";
    private static final String SELECCIONAR_DIRECTORIO = "Seleccionar directorio";
    private final JPanel buttonPanel = new JPanel();
    private final JPanel upperPanel = new JPanel();
    private final JPanel thumbnailsPanel = new JPanel();
    private final JButton selectBt = GUITools.addButton(this, buttonPanel, SELECCIONAR_IMAGEN);
    private final JButton loadBt = GUITools.addButton(this, buttonPanel, CARGAR_IMAGENES);
    private final Dimension labelSize = new Dimension(100, 100);
    private final ImageChooser imageChooser = new ImageChooser(".");
    private final ImgCreator imageCreator = new ImgCreator();
    private transient Icon tmpIcon = NULL_ICON;
    private Cursor oldCursor;

    class JExtendedButton extends JButton {

        private static final long serialVersionUID = 5504199167812365030L;

        JExtendedButton() {
            setPreferredSize(labelSize);
            setMinimumSize(labelSize);
            setHorizontalAlignment(JLabel.CENTER);
            setHorizontalTextPosition(JLabel.CENTER);
            setVerticalTextPosition(JLabel.BOTTOM);
            setBackground(Color.pink);
        }

/* ?????? Este metodo es el que caga la vaina
		public void setIcon(Icon defaultIcon) {
			if(defaultIcon instanceof ImageIcon) {
				super.setIcon(new ImageIcon(
					((ImageIcon)defaultIcon).getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT)));
			}
			else
				super.setIcon(defaultIcon);
		}
//*/
    }

    private ThumbsViewer() {
        super("Thumbs Viewer");
        setTitle(getClass().getName());
        ImageTools.getImageIcon("/toolbarButtonGraphics/general/Find16.gif")
            .ifPresent(imageIcon -> setIconImage(imageIcon.getImage()));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent ev) {
                dispose();
                setVisible(false);
                System.exit(0);
            }
        });

//		imageCreator.setImageSize(100, 100);
        imageCreator.setImageSize(64, 64);
//		imageCreator.setImageSize(32, 32);

        try {
            // Armar la GUI
            upperPanel.setLayout(new FixedGridLayout(2, labelSize));
            upperPanel.setBorder(BorderFactory.createEtchedBorder());

            thumbnailsPanel.setLayout(new FixedGridLayout(6, labelSize));
            thumbnailsPanel.setBorder(BorderFactory.createEtchedBorder());

            final JLabel imagen = new JLabel();
            imagen.setHorizontalAlignment(JLabel.CENTER);
            imagen.setHorizontalTextPosition(JLabel.CENTER);
            imagen.setVerticalTextPosition(JLabel.BOTTOM);
            final JTextField info = new JTextField(40);
            buttonPanel.add(info);
            getContentPane().add("North", new JScrollPane(upperPanel));
            getContentPane().add("South", buttonPanel);
            getContentPane().add("East", new JScrollPane(new JList<>()));
            getContentPane().add("Center", new JScrollPane(thumbnailsPanel));
            setSize(800, 600);
            setVisible(true);
            loadFamilies();
        } catch (final Exception ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
    }


    private void loadFamilies() {
        imageCreator.setFont(new Font("Arial", Font.BOLD, 12));
        imageCreator.setImageSize(132, 132);
        upperPanel.removeAll();
        for (int i = 0; i < 15; i++) {
            final JExtendedButton button = new JExtendedButton();
            final String lbl = "Etiqueta " + i;
            final ImageIcon img;
            img = new ImageIcon(imageCreator.createTextImage(lbl));
            button.setText(lbl);
            button.setIcon(img);
            upperPanel.add(button);
            button.addActionListener(this);
        }
        synchronized (getTreeLock()) {
            validateTree();
        }
    }

    private void loadThumbs() {
        final int oldMode = imageChooser.getFileSelectionMode();
        imageChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        final int returnVal = imageChooser.showDialog(this, SELECCIONAR_DIRECTORIO);
        imageChooser.setFileSelectionMode(oldMode);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        oldCursor = getCursor();
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        final File dir = imageChooser.getSelectedFile();
        final File[] files = dir.listFiles(/*(dir1, name) ->
			name.toLowerCase().endsWith(".jpg") ||
			name.toLowerCase().endsWith(".gif") ||
			name.toLowerCase().endsWith(".png") ||
			name.toLowerCase().endsWith(".bmp")*/
        );
        thumbnailsPanel.removeAll();
        if (files != null) {
            for (final File file : files) {
                final JExtendedButton button = new JExtendedButton();
                button.addActionListener(this);
                final String fileName = file.getName();
                button.setText(fileName);
                if (file.isDirectory()) {
                    ImageTools.getImageIcon("/toolbarButtonGraphics/general/Open24.gif").ifPresent(button::setIcon);
                } else if (fileName.endsWith(".bmp")) {
                    try {
                        button.setIcon(new ImageIcon(ImageIO.read(file)));
                    } catch (final Exception ex) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                    }
                } else {
                    button.setIcon(new ImageIcon(file.getAbsolutePath()));
                }
                button.setToolTipText(fileName);
                thumbnailsPanel.add(button);
            }
        }
        synchronized (getTreeLock()) {
            validateTree();
        }
        setCursor(oldCursor);
    }


    @Override
    public void actionPerformed(final ActionEvent ev) {
        if (ev.getSource() instanceof JExtendedButton) {
            if (tmpIcon == NULL_ICON) {
                tmpIcon = ((AbstractButton) ev.getSource()).getIcon();
                oldCursor = getCursor();
                setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            } else {
                ((AbstractButton) ev.getSource()).setIcon(tmpIcon);
                tmpIcon = NULL_ICON;
                setCursor(oldCursor);
            }
            return;
        }

        if (ev.getSource() == selectBt) {
            final int returnVal = imageChooser.showDialog(this, "Seleccionar imagen");
            try {
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    final File file = imageChooser.getSelectedFile();
                    final int length = (int) file.length();
                    final byte[] data = new byte[length];
                    try (final FileInputStream fis = new FileInputStream(file)) {
                        final int bytesRead = fis.read(data);
                        if (bytesRead != length) {
                            throw new IOException("Could only read " + bytesRead + " of " + length);
                        }
                    }
                    loadFamilies();
                }
            } catch (final IOException ex) {
                LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            }
        } else if (ev.getSource() == loadBt) {
            loadThumbs();
        }
    }


    public static void main(final String[] args) {
        new ThumbsViewer();
    }
}
