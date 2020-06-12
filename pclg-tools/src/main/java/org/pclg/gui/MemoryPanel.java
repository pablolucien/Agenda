// package
package org.pclg.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.text.MessageFormat;

/**
 * @since 2002.08.06
 */
public final class MemoryPanel extends JPanel {
	private static final int MILLIS_IN_SECOND = 1000;
    private static final long serialVersionUID = -6345611051183347013L;
    private final JLabel memDisplay = new JLabel();
    private final JProgressBar memoryBar = new JProgressBar();
    private static final MessageFormat msgMemory =
            new MessageFormat("Usados {2} bytes de un total de {0} (Libre {1})");
    private final Runtime runtime = Runtime.getRuntime();

    public MemoryPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(memDisplay, BorderLayout.SOUTH);
        panel.add(memoryBar, BorderLayout.CENTER);
        new Timer(MILLIS_IN_SECOND, ev -> updateMemoryStatus()).start();
    }

    private void updateMemoryStatus() {
        long free = runtime.freeMemory();
        long total = runtime.totalMemory();

        // when bigger than integer then divide by two
        while (total > Integer.MAX_VALUE) {
            total >>= 1;
            free >>= 1;
        }

        final int taken = (int) (total - free);
        memoryBar.setMaximum((int) total);
        memoryBar.setValue(taken);

        memDisplay.setText(msgMemory.format(new Object[]{
            new Long(total),
            new Long(free),
            new Integer(taken)
        }));
        memDisplay.invalidate();
        validate();
    }
}
