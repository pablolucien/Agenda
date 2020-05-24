import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
    Ventana que muestra un FileTree
*/
class FileTree extends JFrame {
    private static final long serialVersionUID = 6580296459827488074L;
    private final JTree tree;
	private final JScrollPane scrollpane;


	public FileTree(final JFrame parent, final DefaultMutableTreeNode root) {
//		super(parent);
		super(root.toString());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent ev) {
				dispose();
				setVisible(false);
System.exit(0);
			}
		});

		final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(new ImageIcon("new1.gif"));
		renderer.setOpenIcon(new ImageIcon("icon2.gif"));
		renderer.setClosedIcon(new ImageIcon("icon1.gif"));
		tree = new JTree(root);
//		tree.setCellRenderer(renderer);
		tree.setEditable(true);
		tree.setShowsRootHandles(true);
/*
		tree.expandRow(0);
		tree.expandRow(1);
		tree.expandRow(2);
		tree.expandRow(3);
		tree.expandRow(4);
*/
		tree.setRootVisible(false);
		scrollpane = new JScrollPane(tree);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
		pack();
	}
}
