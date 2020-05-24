package org.pclg.tools;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

/**
 * A node in a directory tree.
 *
 * @author El Coyote cojo.
 * @version Unknown.
 */
@SuppressWarnings("serial")
final class DirTreeNode extends DefaultMutableTreeNode {
    /** the name of this node. */
    private final String nombre;

    /** the File that this node represents. */
    private final File dir;
    private String tooltip;

    /**
     * Creates a  DirTreeNode with a name and a File.
     *
     * @param nombre the name of this node.
     * @param dir    the File that this node represents.
     */
    DirTreeNode(final String nombre, final File dir) {
        super(nombre);
        assert nombre != null;
        assert dir != null;
        this.nombre = nombre;
        this.dir = dir;
    }

    /**
     * Returns the File that this node represents.
     *
     * @return the File that this node represents.
     */
    public File getDir() {
        return dir;
    }

    /**
     * Returns the name of this node.
     *
     * @return the name of this node.
     */
    public String getName() {
        return nombre;
    }

    public void setTooltip(final String tooltip) {
        this.tooltip = tooltip;
    }

    public String getTooltip() {
        return tooltip;
    }
}
