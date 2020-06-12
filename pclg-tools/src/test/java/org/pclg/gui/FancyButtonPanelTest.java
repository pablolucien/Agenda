package org.pclg.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

import static org.pclg.gui.FancyButtonPanel.Orientation;
import static org.pclg.gui.FancyButtonPanel.Orientation.HORIZONTAL;
import static org.pclg.gui.FancyButtonPanel.Orientation.VERTICAL;

/**
 * @since 23/07/2019.
 */
public class FancyButtonPanelTest {
    private FancyButtonPanelTest() {
    }

    //@Test
    public static void testResizeButtons(final Orientation orientation) {
        final JFrame frame = new JFrame("testResizeButtons");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final JButton adios = new JButton("Adi�s");
        final JButton y = new JButton("y");
        final JButton hola = new JButton("Hola");
        final FancyButtonPanel fancyButtonPanel = new FancyButtonPanel(orientation, hola, y, adios);
        y.addActionListener(ev -> y.setText(y.getText().equals("y") ? "es decir" : "y"));
        adios.addActionListener(ev -> System.exit(0));
        frame.add(new JLabel("pepito"), BorderLayout.NORTH);
        frame.add(fancyButtonPanel, orientation == VERTICAL ? BorderLayout.WEST : BorderLayout.SOUTH);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    public static void main(final String[] args) {
        testResizeButtons(HORIZONTAL);
        testResizeButtons(VERTICAL);
    }
}