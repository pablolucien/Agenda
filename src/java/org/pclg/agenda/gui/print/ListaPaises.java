package org.pclg.agenda.gui.print;

import org.pclg.agenda.entities.Pais;
import org.pclg.agenda.gui.ListPaisesListModel;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * @author El Coyote Cojo
 * @since 16/09/18 12:21
 */
final class ListaPaises extends JPanel {
    private final JList<Pais> list = new JList<>();

    ListaPaises() {
        setLayout(new BorderLayout());
        final ListModel<Pais> model = new ListPaisesListModel(Pais.getPaises());
        list.setModel(model);
      	add(new JScrollPane(list), BorderLayout.CENTER);
        final JButton button = new JButton("^");
       // button.addActionListener(event -> model.);
        add(button, BorderLayout.NORTH);
    }

    @Override
    public synchronized void addMouseListener(final MouseListener listener) {
        list.addMouseListener(listener);
    }

    public List<Pais> getSelectedValuesList() {
        return list.getSelectedValuesList();
    }
}
