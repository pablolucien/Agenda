package org.pclg.media.imagetools;

import org.pclg.tools.ToolBox;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

final class ImagePreview extends JComponent
    implements PropertyChangeListener {
    private static final long serialVersionUID = -7750840750514605627L;
    private static final int MY_WIDTH = 100;
    private static final int MY_HEIGHT = 50;
    private ImageIcon thumbnail;
    private File file;

    public ImagePreview(final JFileChooser fc) {
        setPreferredSize(new Dimension(MY_WIDTH, MY_HEIGHT));
        fc.addPropertyChangeListener(this);
        add(new JButton("Nueva"));
    }

    private void loadImage() {
        if (file != null && file.isFile()) {
            ImageIcon tmpIcon = null;

            try {
                final byte[] data = new byte[(int) file.length()];
                final FileInputStream fis = new FileInputStream(file);
                fis.read(data);
                fis.close();
                if (data[0] == 0x42 && data[1] == 0x4D) {
                    tmpIcon = new ImageIcon(ImageIO.read(file));
                } else {
                    tmpIcon = new ImageIcon(file.getPath());
                }
            } catch (final FileNotFoundException ex) {
                ToolBox.showInfo(ex);
            } catch (final IOException ex) {
                ToolBox.showInfo(ex);
            }

            if (tmpIcon != null && tmpIcon.getIconWidth() > 90) {
                thumbnail = new ImageIcon(tmpIcon.getImage().
                    getScaledInstance(90, -1,
                        Image.SCALE_DEFAULT));
            } else {
                thumbnail = tmpIcon;
            }
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final String prop = event.getPropertyName();
        if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            file = (File) event.getNewValue();
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    @Override
    public void paintComponent(final Graphics graphics) {
        if (thumbnail == null) {
            loadImage();
        }
        if (thumbnail != null) {
            int xPos = getWidth() / 2 - thumbnail.getIconWidth() / 2;
            int yPos = getHeight() / 2 - thumbnail.getIconHeight() / 2;

            if (yPos < 0) {
                yPos = 0;
            }

            if (xPos < 5) {
                xPos = 5;
            }
            thumbnail.paintIcon(this, graphics, xPos, yPos);
        }
//super.paintComponent(graphics);
    }
}
