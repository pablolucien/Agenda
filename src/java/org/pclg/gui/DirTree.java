package org.pclg.gui;

import org.pclg.log.LoggerFactory;
import org.pclg.tools.Chrono;
import org.pclg.tools.FileTools;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.pclg.tools.ImageTools.getImageIcon;

/**
 * A directory tree.
 *
 * @author El Coyote cojo.
 * @version Unknown.
 */
@SuppressWarnings("serial")
public final class DirTree extends JPanel {
    /** El logger. */
    private static final Logger LOGGER = LoggerFactory.make();

    /** The root of the tree. */
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("(root)");

    /** The model of the tree. */
	private final DefaultTreeModel model = new DefaultTreeModel(root);

	/** The graphical tree instance. */
    private JTree tree;

	/** The scrollable view of the tree. */
	private JScrollPane scrollView;

    /**
     * The component where this DirTree lies (was java.awt.Window previously).
     */
    private Container myParent;

    /** Accept Button. */
    private final JButton acceptBt = new JButton("OK Man");

    /** Scan Button. */
    private final JButton scanBt = new JButton("Rescan");

    /** Cancel Button. */
    private final JButton cancelBt = new JButton("Abbrechen");

    /** Was the dialog accepted?. */
    private boolean fueAceptado;

    /** Default width for the window. */
    private static final int DEFAULT_WIDTH = 800;

    /** Default height for the window. */
    private static final int DEFAULT_HEIGHT = 600;

    /**
     * Creates a DirTree.
     *
     * @param parent The component where this DirTree lies.
     */
    public DirTree(final Container parent) {
        this();
        myParent = parent;
        parent.add(this, BorderLayout.CENTER);
        parent.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Creates a DirTree.
     *
     * @param parent The component where this DirTree lies.
     */
    public DirTree(final JFrame parent) {
        this();
        myParent = parent;
        parent.getContentPane().add(this, BorderLayout.CENTER);
        parent.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Creates a DirTree.
     *
     * @param parent The component where this DirTree lies.
     */
    public DirTree(final JDialog parent) {
        this();
        myParent = parent;
        parent.getContentPane().add(this, BorderLayout.CENTER);
        parent.pack();
//        parent.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//                LOGGER.info("keyTyped");
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//                LOGGER.info("keyPressed");
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                LOGGER.info("keyReleased");
//                if (e.getKeyCode() == VK_ESCAPE) {
//                    setVisible(false);
//                }
//            }
//        });
    }

    /**
     * Creates a DirTree.
     */
	private DirTree() {
        setLayout(new BorderLayout());

        final JPanel buttonPanel = new JPanel();
        buttonPanel.add(acceptBt);
        final ActionListener listener = new DelegateActionListener();
        acceptBt.addActionListener(listener);

        scanBt.addActionListener(listener);
        buttonPanel.add(scanBt);

        cancelBt.addActionListener(listener);
        buttonPanel.add(cancelBt);

        add(buttonPanel, BorderLayout.SOUTH);

        createNewTree();
        tree.addTreeSelectionListener(new DelegateTreeSelectionListener());
        addTreeComponent();

        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent event) {
                final int eventX = event.getX();
                final int eventY = event.getY();
                final int selRow = tree.getRowForLocation(eventX, eventY);
                final TreePath selPath = tree.getPathForLocation(eventX, eventY);
                if(selPath != null && selRow != -1) {
                    tree.expandRow(selRow);
                        if (event.getClickCount() == 2) {
                            if (LOGGER.isLoggable(Level.INFO)) {
                                LOGGER.info(String.format("Two clicks at row %d: path %s", selRow, selPath));
                            }
                            final Object pathComponent = selPath.getLastPathComponent();
                        if (pathComponent instanceof DirTreeNode) {
                            final DirTreeNode node = (DirTreeNode) pathComponent;
                            populateTree(node, node.getDir());
                            if (node.getChildCount() == 0) {
                                acceptSelection();
                            }
                        }
                    }
                }
            }
        };
        tree.addMouseListener(ml);


        final JTextField dirField = new JTextField();
        dirField.addActionListener(ev -> setFile(new File(dirField.getText())));
        add(dirField, BorderLayout.NORTH);
    }

    /**
     * Sets the visibility of this component.
	 *
     * @param visibility the desired visibility.
     */
    @Override
	public void setVisible(final boolean visibility) {
        if (myParent != null) {
            myParent.setVisible(visibility);
        }
    }

    /**
     * an Optional of the file selected in this DirTree (Optional.empty() if none was selected).
	 *
     * @return an Optional of the file selected in this DirTree (Optional.empty() if none was selected).
     */
    public Optional<File> getFile() {
        final TreePath path = tree.getSelectionPath();
        if (path != null) {
            final DirTreeNode node = (DirTreeNode) path.getLastPathComponent();
            if (node != null) {
                return Optional.ofNullable(node.getDir());
            }
        }
        return Optional.empty();
    }

    /**
     * Selecciona el path de este directorio.
	 *
     * @param dir the path to set.
     */
    public void setFile(final File dir) {
        final List<File> files = FileTools.splitInComponents(dir);
		DefaultMutableTreeNode startpoint = root;

		for (final File file : files) {
			String name = file.getName();
			if (name.length() == 0) {
				name = file.getAbsolutePath();
			}
			final Optional<DefaultMutableTreeNode> optionalNode = searchNode(startpoint, name);
			
			if (optionalNode.isPresent()) {
				startpoint = optionalNode.get();
				//make the node visible by scroll to it
				final TreeNode[] nodes = model.getPathToRoot(startpoint);
				final TreePath path = new TreePath(nodes);
				tree.scrollPathToVisible(path);
				tree.setSelectionPath(path);
			} else {
				JOptionPane.showMessageDialog(this,
				    "Node with string " + name + " not found",
				    "Node not found", JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
    }

	private static Optional<DefaultMutableTreeNode> searchNode(
		final DefaultMutableTreeNode startpoint, final String nodeStr) {
		@SuppressWarnings("unchecked")
		final Enumeration<DefaultMutableTreeNode> enumer = startpoint.breadthFirstEnumeration();

		while(enumer.hasMoreElements()) {
			final DefaultMutableTreeNode node = enumer.nextElement();
			if (nodeStr.equalsIgnoreCase(node.getUserObject().toString())) {
				return Optional.of(node);
			}
		}
		return Optional.empty();
	}

    /**
     * Crea el arbol y lo agrega a la GUI. Hecha el 2004.08.19 para ver si
     * rescan funciona de una perra vez.
     */
    private void addTreeComponent() {
        scrollView = new JScrollPane(tree);
		add(scrollView, BorderLayout.CENTER);
		final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        getImageIcon("/resources/images/16x16/folder-grey.png").ifPresent(renderer::setLeafIcon);
        getImageIcon("/resources/images/16x16/folder-cyan_open.png").ifPresent(renderer::setOpenIcon);
        getImageIcon("/resources/images/16x16/folder-cyan.png").ifPresent(renderer::setClosedIcon);
		tree.setCellRenderer(renderer);
        tree.setEditable(true);
        populateTree();
        tree.expandRow(0);
        tree.setRootVisible(false);
    }

    public boolean accepted() {
        return fueAceptado;
    }

    private void acceptSelection() {
        fueAceptado = true;
        setVisible(false);
    }

    private void createNewTree() {
        tree = new JTree(model) {
            @Override
            public String getToolTipText(MouseEvent event) {
                final JTree source = (JTree) event.getSource();
                final TreePath pathForLocation = source.getPathForLocation(event.getX(), event.getY());
                return pathForLocation == null ? null : ((DirTreeNode) pathForLocation.getLastPathComponent()).getTooltip();
            }
        };
        ToolTipManager.sharedInstance().registerComponent(tree);
    }

    private void populateTree() {
        final File[] roots = File.listRoots();
        final int cronHandle = Chrono.getChrono();
        for (final File currentRoot : roots) {
            final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
            final String systemDisplayName = fileSystemView.getSystemDisplayName(currentRoot);
            final String systemTypeDescription = fileSystemView.getSystemTypeDescription(currentRoot);
            final Icon systemIcon = fileSystemView.getSystemIcon(currentRoot);
//            fileSystemView.isDrive();
            final String absolutePath = currentRoot.getAbsolutePath();
            final DirTreeNode rootNode =
                new DirTreeNode(absolutePath,
                    currentRoot.getAbsoluteFile());
            rootNode.setTooltip(systemDisplayName + (systemTypeDescription == null ? "" : " - " + systemTypeDescription));
            root.add(rootNode);
            new Thread(() -> populateTree(rootNode, currentRoot)).start();
        }
        if (LOGGER.isLoggable(Level.INFO)) {
            Chrono.mark(cronHandle);
            LOGGER.info(Chrono.timeDetail(Chrono.elapsed(cronHandle)));
        }
        tree.setSelectionRow(1);
    }

    private void populateTree(final DefaultMutableTreeNode startPoint, final File dir) {
        startPoint.removeAllChildren();
        final File[] cwdContents = dir.listFiles(File::isDirectory);
        if (cwdContents != null && cwdContents.length > 0) {
            Arrays.sort(cwdContents);
            for (final File cwdContent : cwdContents) {
                startPoint.add(new DirTreeNode(cwdContent.getName(), cwdContent.getAbsoluteFile()));
            }
        }
    }

    private class DelegateActionListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent ev) {
            fueAceptado = false;
            final Object source = ev.getSource();
            if (source == acceptBt) {
                acceptSelection();
            } else if (source == scanBt) {
                getFile().ifPresent(current -> {
                    remove(scrollView);
                    root.removeAllChildren();
                    DirTree.this.createNewTree();
                    addTreeComponent();
                    setFile(current);
                    validate();
                });
            } else if (source == cancelBt) {
                setVisible(false);
            }
        }
    }

    private class DelegateTreeSelectionListener implements TreeSelectionListener {
        @Override
        public void valueChanged(final TreeSelectionEvent ev) {
            final TreePath sel = ev.getNewLeadSelectionPath();
            if (sel != null) {
                final Object pathComponent = sel.getLastPathComponent();
                if (pathComponent instanceof DirTreeNode) {
                    final DirTreeNode node = (DirTreeNode) pathComponent;
                    populateTree(node, node.getDir());
                }
            }
        }
    }
}
