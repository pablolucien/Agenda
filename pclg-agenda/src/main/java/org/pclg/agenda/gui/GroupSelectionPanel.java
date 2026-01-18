package org.pclg.agenda.gui;

import org.pclg.agenda.entities.Grupo;
import org.pclg.tools.GUITools;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Pablo
 * @since 19/10/13 19:35
 */
class GroupSelectionPanel extends JPanel {
    private static final String UNION_SYMBOL = "\u222a";
    private static final String INTERSECTION_SYMBOL = "\u2229";
    private static final String NEGATION_SYMBOL = "\u00ac";
    @Serial
    private static final long serialVersionUID = -9021255104997816640L;
    private final JList<Grupo> gList =
		new JList<>(Grupo.getValues().toArray(new Grupo[Grupo.size()]));
	private final JRadioButton chkIntersection =
        new JRadioButton(INTERSECTION_SYMBOL, false);
	private final JCheckBox chkNegation = new JCheckBox(NEGATION_SYMBOL, false);
    private final List<Grupo> selectedGroups = new ArrayList<>();
    /** Instante de la última actualizacion de la lista de grupos. */
    private long lastUpdated = Long.MIN_VALUE;

	GroupSelectionPanel() {
		super(new BorderLayout());
		add(new JScrollPane(gList), BorderLayout.CENTER);
		final JPanel buttonPanel = new JPanel();
		buttonPanel.add(chkNegation);
		buttonPanel.add(chkIntersection);
        final JRadioButton chkUnion = new JRadioButton(UNION_SYMBOL, true);
        buttonPanel.add(chkUnion);
		final ButtonGroup group = new ButtonGroup();
		group.add(chkIntersection);
		group.add(chkUnion);
		add(buttonPanel, BorderLayout.NORTH);
		// Es más cómodo para el usuario que el gList tenga el foco inicialmente.
		GUITools.requestFocusInDialog(gList);
	}

	boolean isSelectionIntersection() {
		return chkIntersection.isSelected();
	}

	boolean isNegation() {
		return chkNegation.isSelected();
	}

	List<Grupo> getSelectedValues() {
		selectedGroups.clear();
        selectedGroups.addAll(gList.getSelectedValuesList());
		return Collections.unmodifiableList(selectedGroups);
	}

	@Override
	public synchronized void addMouseListener(
		final MouseListener mouseListener) {
		gList.addMouseListener(mouseListener);
	}

	public void refresh() {
        final long lastUpdatedGrupo = Grupo.getLastUpdated();
        if (lastUpdated < lastUpdatedGrupo) {
            final List<Grupo> grupos = Grupo.getValues();
            final DefaultListModel<Grupo> model = new DefaultListModel<>();
			grupos.forEach(model::addElement);
            gList.setModel(model);
            lastUpdated = lastUpdatedGrupo;
        }
    }
}