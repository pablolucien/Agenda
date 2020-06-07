package org.pclg.compdel;

import org.apache.log4j.Logger;
import org.pclg.gui.ManagedScrollPane;
import org.pclg.gui.MemoryPanel;
import org.pclg.gui.ScrollBarManager;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.BoundsInfo;
import org.pclg.tools.DigestTools;
import org.pclg.tools.GUITools;
import org.pclg.tools.ToolBox;
import org.pclg.xtras.Colortable;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author El Coyote Cojo
 * @since 2/12/18 10:26
 */
final class CompDelFrame extends JFrame {
    final DirectoriesPanel directoriesPanel = new DirectoriesPanel();
   
	// Configuracion de las acciones
	private final JPanel northPanel = new JPanel();
	/*private*/ final CompDelOptions optionPanel = new CompDelOptions();
    final Output out = new Output();
  	final Output err = new Output();
  
    final ManagedScrollPane scrollOut = new ManagedScrollPane(out);
      final ManagedScrollPane scrollErr = new ManagedScrollPane(err);
    /** Comportamiento de los scrollbars. */
    String errScrollBarPosition;
 	String outScrollBarPosition;
    /** posicion y tamaño de la ventana de la aplicacion. */
    BoundsInfo applicationBounds;
    /** the master object. */
    private final CompDel master;


    final ControlPanel controlPanel = new ControlPanel(new CallbackListener());

    CompDelFrame(final CompDel master) {
        this.master = master;
    }

    void setUpGUI(final String dir1, final String dir2) {
        directoriesPanel.setFirstDir(dir1);
        directoriesPanel.setSecondDir(dir2);
        final JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEADING));
        Arrays.stream(optionPanel.getComponents()).forEach(child -> child.setBackground(Colortable.getColor("lemon chiffon")));
        leftContainer.setBackground(Colortable.getColor("lemon chiffon"));
        leftContainer.add(optionPanel);
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
        northPanel.add(leftContainer);

        northPanel.add(directoriesPanel);

        final Container contentPane = getContentPane();
        contentPane.add(northPanel, BorderLayout.NORTH);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            scrollErr, scrollOut);
        splitPane.setOneTouchExpandable(true);
        contentPane.add(splitPane, BorderLayout.CENTER);
        if (errScrollBarPosition != null) {
            scrollErr.setScrollBarPosition(ScrollBarManager.Position.valueOf(errScrollBarPosition));
        }
        if (outScrollBarPosition != null) {
            scrollOut.setScrollBarPosition(ScrollBarManager.Position.valueOf(outScrollBarPosition));
        }
        final JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(controlPanel.getButtonPanel(), BorderLayout.NORTH);
        southPanel.add(new MemoryPanel(), BorderLayout.CENTER);
        southPanel.add(master.excludeListField, BorderLayout.SOUTH);

        contentPane.add(southPanel, BorderLayout.SOUTH);

        GUITools.setBounds(this, applicationBounds);
        setVisible(true);
        splitPane.setDividerLocation(0.5);        // Debe ir despues del setVisible(true)
    }

    class CallbackListener {
   		void doAction(final ControlPanel.ACTION action) {
   			switch (action) {
   			case NOOP:
   				break;
   			case CLEAR:
                   out.clear();
                   err.clear();
   				break;
   			case REFRESH:
                master.slave.resetFields();
                   err.println("Refrescando");
   				break;
   			case STOP:
                master.slave.terminate();
   				break;
   			case EXIT:
   				if (master.standAlone) {
   					GUITools.exitApplication(CompDelFrame.this, false);
   				} else {
                    dispose();
   				}
   				break;
   			case SAVE:
   				ReportManager.saveResults(CompDelFrame.this, directoriesPanel.getFirstDir(),
                       err.getText(), out.getText(), master.errConsole.getText());
   				break;
   			case CLEAN:
   				//myGlassPane.setVisible(true);		// desactivar el frame
   				try {
   					System.err.println("inicio");
   					DigestTools.cleanDigestDB(master.supportsCompactTable);
   					System.err.println("fin");
   				} catch (final SQLException ex) {
   					ToolBox.showInfo(ex, true);
   				}
   				break;
   			case START:
   			case PAUSE:
   			case RESUME:
   				processControlButton(action);
   				break;
   			default:
   				throw new IllegalStateException();
   			}
   		}
   
   		/**
   		 * Determines what action to take depending on the status of controlBt.
   		 * @param action the action to execute.
   		 */
   		private void processControlButton(final ControlPanel.ACTION action) {
   			if (action == ControlPanel.ACTION.START) {
                   setTitle(master.className + ": iniciando");
                master.init();
                master.slave.setExcudeList(master.excludeListField.getText());
                master.slave.start();
   			} else if (action == ControlPanel.ACTION.PAUSE) {
                master.slave.suspendActivities();
   			} else if (action == ControlPanel.ACTION.RESUME) {
                master.slave.resumeActivities();
   			}
   		}
   	}
}
