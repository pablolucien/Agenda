package org.pclg.fortunes;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

/**
 */
public class FortuneTeller {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final String NO_FORTUNE = "42 - No fortune for you.";
	private static final String FORTUNE_TITLE = "Fortune";
	private static final String[] OPTIONS = {"Copy & Close", "Just Close"};
	private int count;
	private final Random random;
	private final Component parentComponent;
	private final Manager manager;
    private static final Color BACKGROUND = new Color(240, 242, 177);
    /** Para cut -n- paste con el clipboard global. */
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();


    public FortuneTeller(final Component parentComponent, final Manager manager) {
		this.parentComponent = parentComponent;
		this.manager = manager;
		try {
            if (manager != null) {
                count = manager.getFortunesCount();
            }
        } catch (final SQLException ex) {
			LOGGER.error("Error", ex);
		}
		random = new Random(System.currentTimeMillis());
    }

    public void showRandomFortune() {
		try {
			final String fortune = count > 0 ?
				manager.getRandomFortune(random.nextInt(count)) : null;
			final JTextArea textArea =
				new JTextArea(fortune == null ? NO_FORTUNE :
					fortune.replaceAll("\t", "    "));
			textArea.setEditable(false);
            textArea.setBackground(BACKGROUND);

            final JOptionPane pane = new JOptionPane(textArea, fortune == null ?
            	JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null, OPTIONS, OPTIONS[1]);
            final JDialog dialog = pane.createDialog(parentComponent, FORTUNE_TITLE);
            dialog.setModalityType(Dialog.ModalityType.MODELESS);
            pane.addPropertyChangeListener(evt -> {
				if (pane.getValue() == OPTIONS[0]) {
					clipboard.setContents(
						new StringSelection(textArea.getText()), null);
				}
			});
            dialog.setVisible(true);
		} catch (final SQLException ex) {
			LOGGER.error(LoggerFactory.ERROR_TAG, ex);
		}
	}
	
	public static void main(final String[] args) throws ClassNotFoundException,
			SQLException, InstantiationException, IOException,
			IllegalAccessException {
		final JFrame frame = new JFrame("Testing FortuneTeller");
		final FortuneTeller teller = new FortuneTeller(frame, new Manager());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		teller.showRandomFortune();
	}
}
