package org.pclg.gui;

import org.apache.log4j.Logger;
import org.pclg.annotations.QuickAndDirty;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ImageTools;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@QuickAndDirty
public class ListSelector<E extends Comparable<E>> extends JPanel {
	/**	 */
	private static final long serialVersionUID = 1L;
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private final boolean keepSorted;
	private final List<E> listaElementos = new ArrayList<>();
	private final ListSelectorModel listaElementosAsignablesModel = new ListSelectorModel();
	private final JList<E> listaElementosAsignables = new JList<>(listaElementosAsignablesModel);
	private final ListSelectorModel listaElementosAsignadosModel = new ListSelectorModel();
	private final JList<E> listaElementosAsignados = new JList<>(listaElementosAsignadosModel);

	public ListSelector(final boolean keepSorted) {
		this.keepSorted = keepSorted;
		final JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
        toolbar.setFloatable(false);

		final Action actionAddElement = new AbstractAction("<", ImageTools.getImageIcon(
    			"/resources/images/16x16/go-previous-6.png").orElse(null)) {
            private static final long serialVersionUID = 3883626389293608349L;

            @Override
            public void actionPerformed(final ActionEvent e) {
				asignSelected();
			}
        };

		final Action actionRemoveElement = new AbstractAction(">", ImageTools.getImageIcon(
				"/resources/images/16x16/go-next-6.png").orElse(null)) {
            private static final long serialVersionUID = 8634236785711973431L;

            @Override
            public void actionPerformed(final ActionEvent e) {
				unasignSelected();
			}
        };
		final Action actionAddAllElements = new AbstractAction("<<", ImageTools.getImageIcon(
				"/resources/images/16x16/arrow-left-double-2.png").orElse(null)) {
            private static final long serialVersionUID = 8861297840651875692L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                final Enumeration<E> enumeration =
                    listaElementosAsignablesModel.elements();
                while (enumeration.hasMoreElements()) {
                    listaElementosAsignadosModel.addElement(
                        enumeration.nextElement());
                }
				if (keepSorted) {
					sortElements(listaElementosAsignadosModel);
				}
                listaElementosAsignablesModel.clear();
            }
        };

		final Action actionRemoveAllElements = new AbstractAction(">>", ImageTools.getImageIcon(
				"/resources/images/16x16/arrow-right-double-2.png").orElse(null)) {
            private static final long serialVersionUID = -4990162720788527775L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                final Enumeration<E> enumeration =
                    listaElementosAsignadosModel.elements();
                while (enumeration.hasMoreElements()) {
                    listaElementosAsignablesModel.addElement(
                        enumeration.nextElement());
                }
				if (keepSorted) {
					sortElements(listaElementosAsignablesModel);
				}
                listaElementosAsignadosModel.clear();
            }
        };

		listaElementosAsignados.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() > 1) {
					unasignSelected();
				}
			}
		});

		listaElementosAsignables.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() > 1) {
					asignSelected();
				}
			}
		});

        toolbar.add(actionAddElement);
        toolbar.add(actionAddAllElements);
        toolbar.add(actionRemoveAllElements);
        toolbar.add(actionRemoveElement);
		final JScrollPane scrollPane1 = new JScrollPane(listaElementosAsignados);
		final JScrollPane scrollPane2 = new JScrollPane(
			listaElementosAsignables);

		final GridBagLayout gridbag = new GridBagLayout();
	    final GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(scrollPane1, c);
    	add(scrollPane1);

		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(toolbar, c);
		add(toolbar);

		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(scrollPane2, c);
		add(scrollPane2);
		
		setBorder(new BevelBorder(BevelBorder.RAISED));
	}

	/**
	 * Asigns the selected elements.
	 */
	private void asignSelected() {
		final List<E> selectedValues =
			listaElementosAsignables.getSelectedValuesList();
		for (final E selectedValue : selectedValues) {
			listaElementosAsignadosModel.addElement(selectedValue);
			listaElementosAsignablesModel.removeElement(selectedValue);
		}
		if (keepSorted) {
			sortElements(listaElementosAsignadosModel);
		}
	}

	/**
	 * Unasigns the selected elements.
	 */
	private void unasignSelected() {
		final List<E> selectedValues =
			listaElementosAsignados.getSelectedValuesList();
		for (final E selectedValue : selectedValues) {
			listaElementosAsignablesModel.addElement(selectedValue);
			listaElementosAsignadosModel.removeElement(selectedValue);
		}
		if (keepSorted) {
			sortElements(listaElementosAsignablesModel);
		}
	}

	private void sortElements(final ListSelectorModel model) {
		final List<E> list = model.getElements();
		list.sort((E o1, E o2) -> o1.compareTo(o2));
		model.clear();
		model.setElements(list);
	}

	public List<E> getListaElementos() {
		return listaElementosAsignablesModel.getElements();
	}

	public void setListaElementos(final List<E> listaElementos) {
		try {
			synchronized (this) {
				this.listaElementos.clear();
				this.listaElementos.addAll(listaElementos);
				updateAsignables();
			}
		} catch (final Exception ex) {
			LOGGER.error("Error estableciendo elementos", ex);
		}
	}

    public void setListaElementosAsignados(final List<E> listaElementosAsignados) {
		listaElementosAsignadosModel.setElements(listaElementosAsignados);
        updateAsignables();
	}
	
    public List<E> getListaElementosAsignados() {
		return listaElementosAsignadosModel.getElements();
	}

    private void updateAsignables() {
		synchronized (this) {
			final List<E> temp = new ArrayList<>(listaElementos);
			temp.removeAll(listaElementosAsignadosModel.getElements());
			listaElementosAsignablesModel.setElements(temp);
			if (keepSorted) {
				sortElements(listaElementosAsignablesModel);
			}
		}
	}

	private class ListSelectorModel extends DefaultListModel<E> {
        private static final long serialVersionUID = -2232683145410630086L;

        public List<E> getElements() {
			final List<E> elements = new ArrayList<>(size());
            final Enumeration<E> enumeration = elements();
            while (enumeration.hasMoreElements()) {
                elements.add(enumeration.nextElement());
            }
			return elements;
		}

		@Override
		public E getElementAt(final int index) {
			try {
				return super.getElementAt(index);
			} catch (final Exception ex) {
				LOGGER.error("Error obteniendo elemento", ex);
			}
			return null;
		}

		public void setElements(final List<E> elements) {
            removeAllElements();
			elements.forEach(this::addElement);
		}
	}
}
