package org.pclg.gui;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.CompositeKey;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import static org.pclg.tools.GUITools.addMenuItem;
import static org.pclg.tools.StringTools.equalEmptyOrBlank;
import static org.pclg.tools.StringTools.isEmptyOrBlank;

public class ImageButton extends JButton {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final long serialVersionUID = -2181569811436409971L;
	private final Properties properties;
	private final ImageCache imageCache = ImageCache.getInstance();
	/** Tiempo de espera de timer para no pintar la imagen mil veces si se producen muchos eventos. */
	private static final int TIMEOUT = 200; 
	private final ReschedulableTimer timer = new ReschedulableTimer();

	private boolean active = true;
    
	private String imagePath;
	private File imageFile;
	private Image rawImage;
	private Icon thumbnail;
	private File oldImageFile;
	private String oldOriginalImagePath;
	private Icon oldIcon;

	private final JPopupMenu popupMenu = new JPopupMenu("Image Options");
    private final JMenuItem manageImageMenuItem;

	/**
     * Crea un ImageButton configurado seg?n las properties. 
     * @param properties
     * Debe contener los siguientes valores:
     * <code>
     * ImageButton.select         = Clic para elegir Imagen
     * ImageButton.restore        = Bot\u00F3n derecho para restaurar
     * ImageButton.clear          = Bot\u00F3n derecho para limpiar
     * ImageButton.image          = /images/128x128/image-x-pentax-pef.png
     * ImageButton.image.notFound = /images/128x128/edit-bomb.png
     * <code>
     */
    public ImageButton(final Properties properties) {
    	this.properties = properties;
    	setText(PropertiesHelper.getStringFromProperties(properties, "ImageButton.select"));
		ImageTools.getImageIcon(PropertiesHelper
			.getStringFromProperties(properties, "ImageButton.image"))
			.ifPresent(this::setIcon);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);

        addMenuItem(event -> popupMenu.setVisible(false), popupMenu, PropertiesHelper.getStringFromProperties(properties, "CancelButton"));
        addMenuItem(null, popupMenu, "-");
        manageImageMenuItem = addMenuItem(event -> manageImage(),
            popupMenu, PropertiesHelper.getStringFromProperties(properties, "ImageButton.clear"));
        addMenuItem(null, popupMenu, "-");      // TODO: crear m?todo  addSeparator();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent mouseEvent) {
                popupMenu.show(ImageButton.this, mouseEvent.getX(), mouseEvent.getY());
            }
        });

		addActionListener(new ActionListener() {
		    private final FileSystemView FILE_SYSTEM_VIEW = new FileSystemView();
		    private final FileView FILE_VIEW = new FileView();
			private final JFileChooser fileChooser = new JFileChooser(
				System.getProperty("user.dir"), FILE_SYSTEM_VIEW);
		    private final FileFilter FILE_FILTER = new FileNameExtensionFilter(
		        "Imagenes",	"gif", "jpg", "jpeg", "png");

			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
                if (!((ImageButton) actionEvent.getSource()).isActive()) {
                    return;
                }
				final String pathname;
				if (imageFile != null) {
					final File parentFile = imageFile.getParentFile();
					pathname = parentFile != null && parentFile.exists() ?
						parentFile.getAbsolutePath() : null;
				} else {
					pathname = properties.getProperty(
						"DataEntry.lastImagesDirectory");
				}
				if (pathname != null) {
					fileChooser.setCurrentDirectory(new File(pathname));
				}
				fileChooser.setFileView(FILE_VIEW);
				fileChooser.setFileFilter(FILE_FILTER);
				if (fileChooser.showOpenDialog(ImageButton.this)
						== JFileChooser.APPROVE_OPTION) {
					imageFile = fileChooser.getSelectedFile();
					showImage();
					properties.setProperty("DataEntry.lastImagesDirectory",
						imageFile.getParentFile().getAbsolutePath());
				}
			}
		});
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
            	schedulePaintImage();
            }
        });
    }

    public void addPopUpOption(final String label, final ActionListener listener) {
        addMenuItem(listener, popupMenu, label);
    }


    private void manageImage() {
        if (active) {
            if (imageFile != null) {
                oldImageFile = imageFile;
                oldOriginalImagePath = imagePath;
                imageFile = null;
                imagePath = null;
                oldIcon = getIcon();
                ImageTools.getImageIcon(PropertiesHelper
                    .getStringFromProperties(properties, "ImageButton.image"))
                    .ifPresent(this::setIcon);
                setText(PropertiesHelper.getStringFromProperties(properties, "ImageButton.select"));
                manageImageMenuItem.setText(PropertiesHelper.getStringFromProperties(properties, "ImageButton.restore"));
            } else {
                imageFile = oldImageFile;
                imagePath = oldOriginalImagePath;
                oldImageFile = null;
                oldOriginalImagePath = null;
                setIcon(oldIcon);
                setText(null);
                manageImageMenuItem.setText(PropertiesHelper.getStringFromProperties(properties, "ImageButton.clear"));
            }
        }
    }

    private boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
		if (!active) {
			setText(null);
			setToolTipText(null);
			repaint();
		}
    }

	/**
	 * @param rawImage the rawImage to set
	 */
	private void setRawImage(final Image rawImage) {
		this.rawImage = rawImage;
    	schedulePaintImage();
	}

    public String getImagePath() {
		if (imageFile != null && imageFile.isFile()) {
			return imageFile.getAbsolutePath();
		} else {
			return imagePath;
        }
	}

	public void setImagePath(final String originalImagePath) {
		if (!equalEmptyOrBlank(imagePath, originalImagePath)) {
			imagePath = originalImagePath;
			imageFile = isEmptyOrBlank(originalImagePath) ? null : new File(originalImagePath);
		}
		showImage();
	}

	private void showImage() {
		SwingUtilities.invokeLater(() -> loadAndShowImage(imageFile == null ? null
			: imageFile.getAbsolutePath()));
	}

	/**
     * Really puts the image on screen.
	 */
	private void reallyPutTheImageOnScreen() {
		if (rawImage != null) {
			LOGGER.debug("reallyPutTheImageOnScreen(): " + rawImage + " -> " + Thread.currentThread());
			final int buttonWidth = getWidth();
			final int buttonHeight = getHeight();
			final int imgWidth = rawImage.getWidth(null);
			final int imgHeight = rawImage.getHeight(null);
			final ImageIcon imageIcon;
			if (imgWidth <= buttonWidth && imgHeight <= buttonHeight) {
				imageIcon = new ImageIcon(rawImage);
			} else if (imgWidth <= buttonWidth) {
				imageIcon = new ImageIcon(getScaledInstanceFromCache(-1,buttonHeight, rawImage));
			} else if (imgHeight <= buttonHeight) {
				imageIcon = new ImageIcon(getScaledInstanceFromCache(buttonWidth, -1, rawImage));
			} else {
				final int targetWidth;
				final int targetHeight;
				final double ratioX = ((double) imgWidth) / buttonWidth;
				final double ratioY = ((double) imgHeight) / buttonHeight;
				if (ratioX < ratioY) {
					targetWidth = -1;
					targetHeight = buttonHeight;
				} else if (ratioX > ratioY) {
					targetWidth = buttonWidth;
					targetHeight = -1;
				} else {
					targetWidth = buttonWidth;
					targetHeight = buttonHeight;
				}
				imageIcon = new ImageIcon(getScaledInstanceFromCache(targetWidth, targetHeight, rawImage));
			}
			setIcon(imageIcon);
			setText(null);
		}
	}

	private static Image getScaledInstanceFromCache(final int width, final int height,
			final Image rawImage) {
		final CompositeKey<Image, Dimension> key = new CompositeKey<>(rawImage, new Dimension(width, height));
		final ImageCache imageCache = ImageCache.getInstance();
		Image image = imageCache.getImageFromCache(key);
		if (image == null) {
			image = rawImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			imageCache.putImageInCache(key, image);
		}

		return image;
	}

	private void loadAndShowImage(final String filePath) {
		if (filePath != null) {
			setToolTipText(filePath + " - "
				+ PropertiesHelper.getStringFromProperties(properties, "ImageButton.options"));
    		SwingUtilities.invokeLater(() -> loadImageFromPath(filePath));
		} else {
			ImageTools.getImageIcon(PropertiesHelper
                .getStringFromProperties(properties, "ImageButton.image"))
				.ifPresent(this::setVoidIcon);
		}
	}

	/**
	 * Imagen a mostrar si no hay ninguna v?lida.
	 * @param imageIcon la imagen.
	 */
	public void setVoidIcon(final ImageIcon imageIcon) {
		setText(PropertiesHelper.getStringFromProperties(
			properties, "ImageButton.select"));
		setIcon(imageIcon);
		setToolTipText(null);
	}

	private void loadImageFromPath(final String filePath) {
		final Image cachedImage = imageCache.getImageFromCache(filePath);
		if (cachedImage == null) {
			LOGGER.debug("loadImageFromPath(): Reading " + filePath + " -> " + Thread.currentThread());
			final File file = new File(filePath);
			if (file.isFile() && file.canRead()) {
				setIcon(null);
				setText(PropertiesHelper.getStringFromProperties(
					properties, "DataEntry.msg.image.loading"));
				SwingUtilities.invokeLater(() -> {
					try {
						final BufferedImage image = ImageIO.read(file);
						imageCache.putImageInCache(filePath, image);
						setRawImage(image);
					} catch (IOException | OutOfMemoryError ex) {
						LOGGER.error(LoggerFactory.ERROR_TAG, ex);
						//throw new RuntimeException(e1);
					}
				});
			} else {
				if (thumbnail == null) {
					setText(PropertiesHelper.getStringFromProperties(properties,
						"DataEntry.msg.image.not_found"));
					ImageTools.getImageIcon(PropertiesHelper.getStringFromProperties(properties,
						"ImageButton.image.notFound")).ifPresent(this::setIcon);
				} else {
					setText(PropertiesHelper.getStringFromProperties(properties,
						"DataEntry.msg.using.thumbnail"));
					setIcon(thumbnail);
				}
			}
		} else {
			setRawImage(cachedImage);
		}
	}

	private void schedulePaintImage() {
		final TimerTask task = new TimerTask() {
			@Override
			public void run() {
//				if (isShowing()) {
		    		SwingUtilities.invokeLater(ImageButton.this::reallyPutTheImageOnScreen);
//				}
			}
		};
		timer.reschedule(task);
	}

	public void setThumbnail(final Icon thumbnail) {
		this.thumbnail = thumbnail;
	}

    private static class ReschedulableTimer {
		/** Timer para pintar la imagen. */
		private Timer timer = new Timer("", true);

		void reschedule(final TimerTask task) {
	    	timer.cancel();
	    	timer = new Timer("", true);
	    	timer.schedule(task, TIMEOUT);
		}
	}
}