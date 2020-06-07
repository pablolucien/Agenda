// ******************************** package
package org.pclg.compdel;


// ******************************** imports

import org.pclg.tools.GUITools;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridLayout;

/**
 * CompDelOptions <BR>.
 *
 * @author El Coyote Cojo
 * @version 2003.ago.20 19:54:15, CEST
 */
final class CompDelOptions extends JPanel {
    private static final long serialVersionUID = -914895466335193328L;
    // ******************************** Variables de clase

	// ******************************** Variables de instancia
	/** Indica si de veras vamos a cargarnos los ficheros. */
    final JCheckBox deleteBox  = new JCheckBox("Delete Files", false);

	/**
     * Indica que si no borramos los archivos, de todos modos consirerarlos
     * borrados (digest = null) para no volver a compararlos despues en el proceso.
     */
    final JCheckBox simulateDeleteBox  = new JCheckBox("simulateDelete Files", true);

	/** Indica si lo vamos a hacer recursivo. */
    final JCheckBox recurseBox = new JCheckBox("Recurse", true);

	/** Indica si vamos a borrar los direcorios vacios. */
    final JCheckBox delDirBox = new JCheckBox("Del Empty Dirs", false);

	/** Indica si ignoramos la base de datos. */
    final JCheckBox ignoreBox = new JCheckBox("Ignore saved digests", false);

    /** Indica si creamos un archivo de undo (solo se activa si realmente borramos algo). */
    final JCheckBox undoBox = new JCheckBox("Create UnDo file", true);

    /** . */
    final JCheckBox ignoreExclude = new JCheckBox("Ignore exclude list", false);

    /** . */
    final JCheckBox fastHeuristicsOnly = new JCheckBox("Fast heuristics only", true);

	/** Presenta un mensaje opcionalmente. */
    private String userMsg;

	// ******************************** Inicializacion estática
	
	// ******************************** Constructores

	/**
		Constructor por omision
		--author El Coyote Cojo
		--version 2003.ago.20 19:54:15, CEST
	*/
	CompDelOptions() {
		//super(new FlowLayout(FlowLayout.LEFT));
		super(new GridLayout(2, 0));
		//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		GUITools.setEqualPreferredDimensions(recurseBox, deleteBox, simulateDeleteBox,
			delDirBox, ignoreBox, undoBox, ignoreExclude, fastHeuristicsOnly);
		add(recurseBox);
		add(deleteBox);
        deleteBox.setToolTipText("¿Realmente borrar los archivos iguales?");
        add(delDirBox);
   		delDirBox.setToolTipText("¿Borrar los directorios vacíos?");
        add(fastHeuristicsOnly);
		add(simulateDeleteBox);
		simulateDeleteBox.setToolTipText("Indica que si no borramos los archivos, de todos modos consirerarlos borrados (digest = null) para no volver a compararlos despues en el proceso ");
		add(ignoreBox);
		add(undoBox);
		add(ignoreExclude);
	}

	// ******************************** Metodos de instancia
	boolean acceptOptions() {
		final StringBuilder msg = new StringBuilder("Las opciones son:\n");
		if(!deleteBox.isSelected()) {
			msg.append("NO ");
		}
		msg.append("BORRAR ARCHIVOS\n");

		if(!delDirBox.isSelected()) {
			msg.append("NO ");
		}
		msg.append("BORRAR DIRECTORIOS\n");

		if(!ignoreBox.isSelected()) {
			msg.append("NO ");
		}
		msg.append("IGNORAR DIGESTS\n");

		if(!recurseBox.isSelected()) {
			msg.append("NO ");
		}
		msg.append("HACERLO RECURSIVO\n");

		if(!undoBox.isSelected()) {
			msg.append("NO ");
		}
		msg.append("CREAR ARCHIVO UNDO\n");

        if(ignoreExclude.isSelected()) {
            msg.append("Exclude list (userMsg) ignored\n");
        } else {
            if(userMsg != null && userMsg.trim().length() != 0) {
                msg.append(">>>").append(userMsg).append('\n');
            }
        }

		msg.append("\n\t¿Continuamos?");
		final int res = JOptionPane.showConfirmDialog(GUITools.getFirstParent(this),
            msg.toString(), "Confirmar el proceso", JOptionPane.YES_NO_OPTION);

		return res == JOptionPane.YES_OPTION;
	}

	void setUserMsg(final String msg) {
		userMsg = msg;
	}
	
	// ******************************** Metodos estaticos

}
