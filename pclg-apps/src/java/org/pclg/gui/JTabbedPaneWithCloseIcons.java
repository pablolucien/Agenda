// http://www.developpez.net/forums/d278627/java/communaute-java/contribuez/jtabbedpane-bouton-fermeture/
package org.pclg.gui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

/**
 *
 * @author  alain
 */
@SuppressWarnings("serial")
public class JTabbedPaneWithCloseIcons extends JTabbedPane {
	private static final int MAX_TAB_TITLE_LEN = 20;
	private String closeButtonToolTip;

	/** 
	 * Adds a no-frills tab.
	 * @param title the title of this tab.
	 * @param component the component to add to this tab.
	 */
	public void addPlainTab(final String title, final Component component) {
		super.addTab(title, component);
	}

	@Override
	public void addTab(final String title, final Component component) {
		addTab(title, null, component);
	}

	@Override
	public void addTab(final String title, final Icon icon,
			final Component component) {
		addTab(title, icon, component, null);
	}

	@Override
	public void addTab(final String title, final Icon icon, 
			final Component component, final String tip) {
		super.addTab(title, icon, component, tip);
		final CloseTabPanel tab = new CloseTabPanel(title);
		setTabComponentAt(getTabCount() - 1, tab);
		tab.setCloseButtonToolTip(closeButtonToolTip);
	}

	@Override
	public void setTitleAt(final int index, final String title) {
		final Component tabComponent = getTabComponentAt(index);
		final boolean isLargeTitle = title != null && title.length() > MAX_TAB_TITLE_LEN;
		final String title2Set = isLargeTitle ? title.substring(0, MAX_TAB_TITLE_LEN) + " ..." : title;
		final String toolTip2Set = isLargeTitle ? title : null;
		if (tabComponent instanceof CloseTabPanel) {
			final CloseTabPanel tab = (CloseTabPanel) tabComponent;
			tab.setTitle(title2Set);
		} else {
			super.setTitleAt(index, title2Set);
		}
		setToolTipTextAt(index, toolTip2Set);
	}

	//fonction qui permet d'affiché le bouton close
	public void afficheIconAt(final int endroit) {
		((CloseTabPanel) getTabComponentAt(endroit)).afficheIcon(true);
	}

	//fonction qui permet d'enlever le bouton close
	public void cacheIconAt(final int endroit) {
		((CloseTabPanel) getTabComponentAt(endroit)).afficheIcon(false);
	}

	public void setCloseButtonToolTip(final String closeButtonToolTip) {
		this.closeButtonToolTip = closeButtonToolTip;
		for (int ii = 0, count = getTabCount(); ii < count; ii++) {
			final Component tabComponentAt = getTabComponentAt(ii);
				if (tabComponentAt instanceof CloseTabPanel) {
					((CloseTabPanel) tabComponentAt).setCloseButtonToolTip(closeButtonToolTip);
				}

		}
	}

	public class CloseTabPanel extends JPanel {
		final JButton button;
		final JLabel label;
		private String closeButtonToolTip;

		//constructeur sans boolean  qui de base met un bouton close
		public CloseTabPanel(final String titre) {
			this(titre, true);
		}

		//constructeur avec boolean  qui permet de choisir si oui ou non on veux un bouton close
		public CloseTabPanel(final String titre, final boolean withCloseButton) {
			super(new FlowLayout(FlowLayout.LEFT, 0, 0));
			setOpaque(false);
			label = new JLabel(titre);
			add(label);
			if (withCloseButton) {
				button = new TabButton();
				button.setToolTipText(closeButtonToolTip);
				add(button);
			} else {
				button = null;
			}
			//add more space to the top of the component
			setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		}

		public void setCloseButtonToolTip(final String closeButtonToolTip) {
			this.closeButtonToolTip = closeButtonToolTip;
			button.setToolTipText(closeButtonToolTip);
		}

		public void setTitle(final String title) {
			label.setText(title);
		}

		//permet d'afficher ou cacher le bouton close
		public void afficheIcon(final boolean show) {
			if (show) {
				if (getComponentCount() == 1) {
					add(button);
				}
			} else {
				if (getComponentCount() > 1) {
					remove(button);
				}
			}
		}

		public void addActionListener(final ActionListener actionListener) {
			button.addActionListener(actionListener);
		}
	}

	class TabButton extends JButton {
		TabButton() {
			final int size = 17;
			setPreferredSize(new Dimension(size, size));
			//Make the button looks the same for all Laf's
			setUI(new BasicButtonUI());
			//Rends le bouton transparent
			setContentAreaFilled(false);
			//pas besoin d'avoir le focus
			setFocusable(false);
			setBorder(BorderFactory.createEtchedBorder());
			setBorderPainted(false);
		}

		//we don't want to update UI for this button
		@Override
		public void updateUI() {
		}

		//dessine la croix dans le bouton
		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			final Graphics2D g2 = (Graphics2D) g.create();
			//shift the image for pressed buttons
			if (getModel().isPressed()) {
				g2.translate(1, 1);
			}
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.BLACK);
			if (getModel().isRollover()) {
				g2.setColor(Color.MAGENTA);
			}
			final int delta = 6;
			g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight()
					- delta - 1);
			g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight()
					- delta - 1);
			g2.dispose();
		}
	}
}