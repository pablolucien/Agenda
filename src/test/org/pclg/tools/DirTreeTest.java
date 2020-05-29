package org.pclg.tools;

import org.pclg.log.LoggerFactory;
import org.testng.annotations.Test;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Dialog;
import java.io.File;
import java.util.logging.Logger;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 05-ago-2012 13:16:35
 */
public final class DirTreeTest {
    /** El logger. */
    private static final Logger LOGGER = LoggerFactory.make();

    @Test(enabled = false)
    public void testDirTree() {
        final JDialog parent = new JDialog(new JFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        parent.setAlwaysOnTop(true);
        GUITools.center(parent, null);
        final DirTree dirTree = new DirTree(parent);
        dirTree.setFile(new File(System.getProperty("user.dir")));
        dirTree.setVisible(true);
        dirTree.requestFocusInWindow();
        if (dirTree.accepted()) {
        	dirTree.getFile().ifPresent(file -> LOGGER.info("dirTree.getFile() = " + file));
        } else {
            LOGGER.info("Cancelled");
        }
    }
}
