package org.pclg.filelist;

import org.apache.logging.log4j.Logger;
import org.pclg.filesystem.DirQueue;
import org.pclg.gui.DirTree;
import org.pclg.gui.JLabeledField;
import org.pclg.gui.VersatileComboBox;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileComparator;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.RegexFilter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * FileListPanel <BR>
 * Un panel para mostrar una lista de archivos
 *
 * @author El Coyote Cojo
 * @version 2004.ago.18 14:51:18, CEST
 */
public class FileListPanel extends JPanel {
	/** serialVersionUID. */
	private static final long serialVersionUID = -7846368194888483191L;

	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	/** Este color mola. */
	private static final Color SEPIA = new Color(226, 210, 177);

	/** Directorio de imagenes. */
	private static final String IMAGES_DIR = "/toolbarButtonGraphics/";

	/** Directorio de imagenes generales. */
	private static final String GENERAL_DIR = "/toolbarButtonGraphics/general/";

	/** Directorio de imagenes de navegacion. */
	private static final String NAVIGATION_DIR= "/toolbarButtonGraphics/navigation/";

	private final JButton fwdBt = new JButton(ImageTools.getImageIcon(NAVIGATION_DIR + "Forward16.gif").orElse(null));
	private final JButton backBt = new JButton(ImageTools.getImageIcon(NAVIGATION_DIR + "Back16.gif").orElse(null));
	private final JButton treeBt = new JButton(ImageTools.getImageIcon(GENERAL_DIR + "Open16.gif").orElse(null));
	private final JButton scanBt = new JButton(ImageTools.getImageIcon(GENERAL_DIR + "Refresh16.gif").orElse(null));
	private final JButton sortBt = new JButton(ImageTools.getImageIcon(IMAGES_DIR + "sort.gif").orElse(null));

	private final FileListModel fileListModel = new FileListModel();
	private final JList<File> fileList = new JList<>(fileListModel);
	private final JScrollPane fileListScrollPane = new JScrollPane(fileList);
	private JLabeledField displayFilter;

	/** Extensiones estandard para el filtro de la lista de directorios. */
	private static final String REGEX = ".+\\.jpg|.+\\.jpeg|.+\\.gif|.+\\.bmp";

	/** Filtro para la lista de archivos. */
	private final RegexFilter filter = new RegexFilter(REGEX, "JPG, GIF, & BMP Images");

	private VersatileComboBox versatileComboBox;

	/**
	 * Mantiene la historia de los directorios que hemos visitado. �Hacerlo persistente?
	 */
	private final DirQueue dirList;

	private DirTree dirTree;

	private SortSelector sortSelector;

	/** El directorio de trabajo. */
	private File currentDirectory;

	/**
	 * La ultima modificacion del directorio de trabajo. No poner 0, porque los dir raices
	 * en W98 aparentemente tienen este valor y no lo cambian
	 */
	private long lastModified = -1;

	private final List<FileListObserver> observers = new ArrayList<>();

	/**
	 * Se dispara si es modificado el directorio o su contenido:
	 * (crear o eliminar archivos, cambiarle el nombre, etc.)
	 * ... solo que la puta mierda no funciona en windoze 98 (s� en NT)
	 */
	private final Timer timer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent ev) {
			if (currentDirectory != null && lastModified != currentDirectory.lastModified()) {  // lastModified() parece que funciona s�lo en algunas m�quinas :(
				// Este proceso puede ser largo, por lo tanto es mejor no reentrar
				timer.stop();
				lastModified = currentDirectory.lastModified();
				scan();
				timer.start();
			}
			//parent.updateMemoryStatus();    // esto es un flechazo, pero se podr�a usar un callback...
		}
	});


	private final Window parentWindow;

	/**
	 * Constructor
	 * --author El Coyote Cojo
	 * --version 2004.ago.18 14:51:18, CEST
	 */
	public FileListPanel(final Window parentWindow, final Properties properties) {
		super(new BorderLayout());
		this.parentWindow = parentWindow;
		dirList = new DirQueue(properties);
        setUpDisplayFilter(properties);
		File dir = dirList.cwd();
		if (dir == null) {
			dir = new File(".");
			dirList.fwd(dir);
		}
		currentDirectory = dir;
		goToDir(currentDirectory);
		LOGGER.debug(dirList);
		add(fileListScrollPane, BorderLayout.CENTER);
        add(displayFilter, BorderLayout.SOUTH);
		createFileListPanel();
		configureNavigationButtons();
		fileListModel.setSortCritery(FileComparator.SortCriterium.values()[0]);
		timer.start();
	}

    private void setUpDisplayFilter(final Properties properties) {
	    displayFilter = new JLabeledField(PropertiesHelper.getStringFromProperties(properties, "FileListPanel.filter.prompt"));
	    displayFilter.setEditable(false);
        versatileComboBox = new VersatileComboBox(displayFilter, properties, "FileListPanel.filter", 10);
        displayFilter.addMouseListener(new MouseAdapter() {
            private Cursor oldCursor;

            @Override
            public void mouseClicked(final MouseEvent e) {
                if (versatileComboBox.accept()) {
                    displayFilter.setText(versatileComboBox.getSelectedItem());
                    scan();
                }
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                oldCursor = getCursor();
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                setCursor(oldCursor);
            }
        });
    }

    /**
	 * Adds the specified mouse listener to receive mouse events from
	 * this component.
	 * If listener <code>listener</code> is <code>null</code>,
	 * no exception is thrown and no action is performed.
	 * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
	 * >AWT Threading Issues</a> for details on AWT's threading model.
	 *
	 * @param listener the mouse listener
	 *
	 * @see java.awt.event.MouseEvent
	 * @see java.awt.event.MouseListener
	 * @see #removeMouseListener
	 * @see #getMouseListeners
	 * @since JDK1.1
	 */
	@Override
	public synchronized void addMouseListener(final MouseListener listener) {
		if (listener != null) {
			super.addMouseListener(listener);
			fileList.addMouseListener(listener);
		}
	}

	// ******************************** Metodos de instancia
	private void createFileListPanel() {
        // Esto porque me da la gana
        fileList.setBackground(SEPIA);

        // Muestra los items tal como yo quiero
        fileList.setCellRenderer(new FileListCellRenderer());

        fileList.setVisibleRowCount(25);
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {
                File selected = fileList.getSelectedValue();

                if ((mouseEvent.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                    // Eliminar las selecciones
                    fileList.clearSelection();
                } else if (mouseEvent.getClickCount() == 2) {
                    // con doble click cambiar de directorio si procede
                    try {
                        selected = selected.getCanonicalFile();
                    } catch (final IOException ex) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                    }

                    if (selected.isDirectory()) {
                        goToDir(dirList.fwd(selected));
                        configureNavigationButtons();
                    }
                }
                fireFileListChanged();
            }
        });

        final JPanel northPanel = new JPanel(new GridLayout(1, 0));
        add(northPanel, BorderLayout.NORTH);

        treeBt.addActionListener(event -> {
            if (dirTree == null) {
                dirTree = new DirTree(new JDialog(parentWindow, Dialog.ModalityType.APPLICATION_MODAL));    // Must be Modal for getFile() to work!
            }
            dirTree.setFile(currentDirectory);
            dirTree.setVisible(true);

            if (dirTree.accepted()) {
                dirTree.getFile().ifPresent(file -> {
                    goToDir(dirList.fwd(file));
                    configureNavigationButtons();
                });
            }
            fireFileListChanged();
        });
        treeBt.setToolTipText("Arbol de directorios");
        northPanel.add(treeBt);

        backBt.addActionListener(event -> {
            goToDir(dirList.back());
            configureNavigationButtons();
            fireFileListChanged();
        });
        backBt.setToolTipText("Ir al directorio anterior");
        northPanel.add(backBt);
        backBt.setEnabled(false);

        fwdBt.addActionListener(event -> {
            goToDir(dirList.fwd());
            configureNavigationButtons();
            fireFileListChanged();
        });
        fwdBt.setToolTipText("Ir al siguiente directorio");
        northPanel.add(fwdBt);
        fwdBt.setEnabled(false);

        scanBt.addActionListener(event -> {
            scan();
            fireFileListChanged();
        });
        scanBt.setToolTipText("Actualizar");
        northPanel.add(scanBt);

        sortBt.addActionListener(event -> {
            if (sortSelector == null) {
                sortSelector = new SortSelector(this);
            }
            final FileComparator.SortCriterium sortOption = sortSelector.getSortOption();

            if (sortOption != null) {
                // en realidad FileListModel deber�a tener un m�todo sort();
                fileListModel.setSortCritery(sortOption);
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                fileListModel.setListItems(currentDirectory,
                    currentDirectory.listFiles(filter));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            fireFileListChanged();
        });
        sortBt.setToolTipText("Ordenar");
        northPanel.add(sortBt);
    }

	/**
	 * Limpia lo que pueda estar seleccionado
	 */
	public void clear() {
		fileList.clearSelection();
	}

	/**
	 * Va al inicio de la lista
	 */
	private void goTop() {
		fileListScrollPane.getVerticalScrollBar().setValue(0);
	}

	/**
	 * Devuelve lo que pueda estar seleccionado
	 */
	public Object getSelectedValue() {
		return fileList.getSelectedValue();
	}

	/**
	 * Devuelve lo que pueda estar seleccionado
	 */
	public List<File> getSelectedValues() {
		return fileList.getSelectedValuesList();
	}

	public int[] getSelectedIndices() {
		return fileList.getSelectedIndices();
	}


	public void setSelectedIndices(final int[] indices) {
		fileList.setSelectedIndices(indices);
	}


	public void clearSelection() {
		fileList.clearSelection();
	}

	/**
	 * Como su nombre lo indica
	 */
    private void goToDir(File dir) {
        LOGGER.debug(dirList);
        if (dir != null) {
            while (!dir.exists() || !dir.isDirectory()) {
                dir = dir.getParentFile();
                if (dir == null) {
                    return;
                }
            }
            currentDirectory = dir;
            // hace que el timer llame a scan();
            lastModified = -1;
            scan();
        }
    }

	public void scan() {
		try {
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			filter.setRegex(displayFilter.getText());
			fileListModel.setListItems(currentDirectory, currentDirectory.listFiles(filter));
			goTop();
		} finally {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}


	public void setFilter(final String lastFilter) {
		if (lastFilter == null) {
			displayFilter.setText(REGEX);
		} else {
			displayFilter.setText(lastFilter);
		}
	}

	public String getFilter() {
		return displayFilter.getText();
	}

	/**
	 * ??????? FLECHAZO NUMBER THREE.
	 */
	public FileListModel getFileListModel() {
		return fileListModel;
	}

	public File getCurrentDirectory() {
		return currentDirectory;
	}

	/**
	 * @param currentDirectory the currentDirectory to set
	 */
	private void setCurrentDirectory(final File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}

	private void configureNavigationButtons() {
		backBt.setEnabled(dirList.canGoBack());
		final File previousDir = dirList.peekPreviousDir();
		backBt.setToolTipText(previousDir == null ? "Dead end" : String.valueOf(previousDir));
		fwdBt.setEnabled(dirList.canGoForth());
		final File nextDir = dirList.peekNextDir();
		fwdBt.setToolTipText(nextDir == null ? "Dead end" : String.valueOf(nextDir));
	}

	public void addFileListObserver(final FileListObserver observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeFileListObserver(final FileListObserver observer) {
		observers.remove(observer);
	}

	private void fireFileListChanged() {
		for (final FileListObserver observer : observers) {
			observer.fileListChanged(this);
		}
	}

	public void saveProperties(final Properties customProps) {
		dirList.saveProperties(customProps);
		versatileComboBox.saveYourProperties(customProps);
	}

	public int dirQty() {
		return fileListModel.dirQty();
	}

	public int fileQty() {
		return fileListModel.fileQty();
	}

	public int otherQty() {
		return fileListModel.otherQty();
	}

	public File elementAt(final int index) {
		return fileListModel.elementAt(index);
	}

	public int elementCount() {
		return fileListModel.size();
	}
}
