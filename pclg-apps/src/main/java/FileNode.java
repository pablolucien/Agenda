import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

public final class FileNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1505152190525780003L;
    private String volumeName;
	private File theFile;
	
	public FileNode(final String volumeName) {
		super(volumeName);
		this.volumeName = volumeName;
	}

	public FileNode(final File file) {
		super(file);
		theFile = file;
	}

	public boolean represents(final File file) {
		return(theFile != null && theFile.equals(file));
	}

	public String toString() {
		if(volumeName != null) {
			return(volumeName);
		}
		return(theFile.getName());
	}
}
