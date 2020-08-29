// ******************************** imports
//import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import org.pclg.tools.ToolBox;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

public class FileTreeTest {
	//static FileNode root = new FileNode(new File("C:/"));
	//static FileNode root = new FileNode("C:/");
	private static final FileNode root1 = new FileNode("Todo");
	private static FileNode root = new FileNode("La cantante calva");

	private final String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=c:/databases/FileDataBaseVacia.mdb;PWD=";
	private Connection conn;
	private PreparedStatement pstmtSelect;
	
	private FileTreeTest() {
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			conn = DriverManager.getConnection(url);
			//pstmtSelect = conn.prepareStatement("SELECT VolumeLabel, FilePath FROM CD_Catalog ORDER BY VolumeLabel, FilePath");
			pstmtSelect = conn.prepareStatement("SELECT CD_Catalog_Volumes.VolumeLabel, CD_Catalog_Files.FilePath "
															+ " FROM CD_Catalog_Files, CD_Catalog_Volumes "
															+ " WHERE CD_Catalog_Files.Volume = CD_Catalog_Volumes.Codigo"
															+ " ORDER BY VolumeLabel, FilePath");
			final ResultSet rset = pstmtSelect.executeQuery();
			String lastVolumeLabel = "";
			while(rset.next()) {
				final String volumeLabel = rset.getString(1);
				final String dirEntry = rset.getString(2);
				if(!lastVolumeLabel.equals(volumeLabel)) {
					lastVolumeLabel = volumeLabel;
					root = new FileNode(volumeLabel);
					root1.add(root);
				}
				final File f = new File(dirEntry);
				addFile(root, f);
			}
			pstmtSelect.close();
			conn.close();
		} catch(final SQLException ex) {
			ToolBox.showInfo(ex, true);
		} catch (final ClassNotFoundException ex) {
			ToolBox.showInfo(ex, true);
		}
		final FileTree tree = new FileTree(null, root1);
		tree.setVisible(true);
	}

    public static void main(final String[] args)throws Exception {
	 	//javax.swing.UIManager.setLookAndFeel(new SkinLookAndFeel());
		new FileTreeTest();
/*
		String[] files = {
			"c:/pepito/ito/kk1.txt",
			"c:/pepito/file.dat",
			"c:/pepito/ito/kk3.txt",
			"c:/pepito/bbb/kk4.txt",
			"c:/pepito/bbb/kk5.txt",
			"c:/luisito/lib/ext/test.jar",
			"c:/luisito/lib/ext/classes.jar"
		};

		for(int i = 0; i < files.length; i++) {
			File f = new File(files[i]);
			addFile(root, f);
		}
		root1.add(root);
		
		root = new FileNode("Juanito la lagartija");
		for(int i = files.length - 1; i >= 0 ; i--) {
			File f = new File(files[i]);
			addFile(root, f);
		}
		root1.add(root);
		FileTree tree = new FileTree(null, root1);
		tree.setVisible(true);
*/		
    }

	private static FileNode addFile(FileNode node, final File file) {
		final File dad = file.getParentFile();
		if(dad != null) {
			FileNode anotherNode = find(root, dad);
			if(anotherNode == null) {
				anotherNode = addFile(root, dad);
			}
			node = new FileNode(file);
			anotherNode.add(node);
		}
		return(node);
	}

	/**
		Busca en el arbol un nodo que corresponda al archivo
	*/
	private static FileNode find(final FileNode tree, final File file) {
		final Enumeration<TreeNode> nodes = tree.depthFirstEnumeration();
		while(nodes.hasMoreElements()) {
			final FileNode node = (FileNode) nodes.nextElement();
			if(node.represents(file)) {
				return node;
			}
		}
		return null;
	}
}
