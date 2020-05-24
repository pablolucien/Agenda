package org.pclg.gui;

import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import java.awt.Adjustable;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Pablo
 * @since 16/01/16 14:41
 */
public class ScrollBarManager {
	public enum Position {
		TOP("Siempre arriba"), BOTTOM("Siempre abajo"), NOTHING("Nada");
		private final String text;

		Position(final String text) {
			this.text = text;
		}

		String getText() {
			return text;
		}
	}

	/** Menu para el control de los ScrollBars. */
	private final JPopupMenu popupMenu = new JPopupMenu("");
	private final JRadioButtonMenuItem topScrollMenuItem =
		new JRadioButtonMenuItem(Position.TOP.getText());
	private final JRadioButtonMenuItem nothingScrollMenuItem =
		new JRadioButtonMenuItem(Position.NOTHING.getText());
	private final JRadioButtonMenuItem bottomScrollMenuItem =
		new JRadioButtonMenuItem(Position.BOTTOM.getText());

	private Position scrollBarPosition;

	public ScrollBarManager() {
		final ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(topScrollMenuItem);
		popupMenu.add(topScrollMenuItem);
		buttonGroup.add(nothingScrollMenuItem);
		popupMenu.add(nothingScrollMenuItem);
		buttonGroup.add(bottomScrollMenuItem);
		popupMenu.add(bottomScrollMenuItem);
		nothingScrollMenuItem.setSelected(true);
	}

	public void setUpScrollBar(final JScrollPane scrollPane) {
		if(scrollBarPosition != null) {
			if(scrollBarPosition == Position.TOP) {
				topScrollMenuItem.setSelected(true);
			} else if(scrollBarPosition == Position.BOTTOM) {
				bottomScrollMenuItem.setSelected(true);
			}
		}
		final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		};
		scrollPane.getVerticalScrollBar().addMouseListener(mouseAdapter);

		final AdjustmentListener adjustmentListener =
			ev -> {
                if(bottomScrollMenuItem.isSelected()) {
                    final Adjustable sb = ev.getAdjustable();
                    sb.setValue(sb.getMaximum());
                    scrollBarPosition =  Position.BOTTOM;
                }
                else if(topScrollMenuItem.isSelected()) {
                    final Adjustable sb = ev.getAdjustable();
                    sb.setValue(sb.getMinimum());
                    scrollBarPosition =  Position.TOP;
                }
                else {
                    scrollBarPosition =  Position.NOTHING;
                }
            };

		scrollPane.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);
	}

	public void setScrollBarPosition(final Position scrollBarPosition) {
        this.scrollBarPosition = scrollBarPosition == null ? Position.NOTHING : scrollBarPosition;
		switch (this.scrollBarPosition) {
			case TOP:
				topScrollMenuItem.setSelected(true);
				break;
			case NOTHING:
				nothingScrollMenuItem.setSelected(true);
				break;
			case BOTTOM:
				bottomScrollMenuItem.setSelected(true);
				break;
		}
	}

	public String getScrollBarPosition() {
		return scrollBarPosition.name();
	}
}
