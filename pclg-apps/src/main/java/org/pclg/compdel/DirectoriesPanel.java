package org.pclg.compdel;

import org.pclg.gui.DirSelectorField;
import org.pclg.tools.GUITools;
import org.pclg.tools.ImageTools;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Optional;


/**
 * @since 22/05/2017.
 */
class DirectoriesPanel extends JPanel {
    private static final long serialVersionUID = -4382214475762959690L;
    private final DirSelectorField firstDir = new DirSelectorField("Borrar de aquí");
    private final DirSelectorField secondDir = new DirSelectorField("Lo que hay aquí");

    DirectoriesPanel() {
        super(new BorderLayout());
        final JPanel directoriesInnerPanel = new JPanel(new GridLayout(2, 1));
        GUITools.setEqualPreferredDimensions(firstDir.getButton(), secondDir.getButton());
        directoriesInnerPanel.add(firstDir);
        directoriesInnerPanel.add(secondDir);
        final JButton swapButton = new JButton();
        final Optional<ImageIcon> iconOptional = ImageTools.getImageIcon("/toolbarButtonGraphics/general/Refresh16.gif");
        if (iconOptional.isPresent()) {
            swapButton.setIcon(iconOptional.get());
        } else {
            swapButton.setText("Swap");
        }
        swapButton.setToolTipText("Swap directories");
        swapButton.addActionListener(event -> {
            final String text = firstDir.getText();
            firstDir.setText(secondDir.getText());
            secondDir.setText(text);
        });
        add(swapButton, BorderLayout.WEST);
        directoriesInnerPanel.setPreferredSize(new Dimension(firstDir.getPreferredSize().width,
            firstDir.getPreferredSize().height << 1));
        add(directoriesInnerPanel, BorderLayout.CENTER);
    }

    String getFirstDir() {
        return firstDir.getText();
    }

    String getSecondDir() {
        return secondDir.getText();
    }

    void setFirstDir(String dir) {
        firstDir.setText(dir);
    }

    void setSecondDir(String dir) {
        secondDir.setText(dir);
    }
}
