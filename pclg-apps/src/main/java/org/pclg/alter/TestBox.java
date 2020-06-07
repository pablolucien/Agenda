package org.pclg.alter;

import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @since 12/01/2018.
 */
final class TestBox extends JCheckBox implements ItemListener {
    private static final long serialVersionUID = 2129654312240026818L;
	private final String realThingLbl;
	private final String testLbl;

	TestBox(final String realThingLbl, final String testLbl, final boolean selected) {
        super("", selected);
		this.realThingLbl = realThingLbl;
		this.testLbl = testLbl;
		addItemListener(this);
		// Todo esto para que se ponga el color correcto al inicio. (¡Que ganas de trabajar!)
		final ItemEvent event = new ItemEvent(this, ItemEvent.ITEM_FIRST,
			null, selected ? ItemEvent.SELECTED : ItemEvent.DESELECTED);
		fireItemStateChanged(event);
    }

    @Override
    public void itemStateChanged(final ItemEvent event) {
        if (isSelected()) {
            setText(testLbl);
            setBackground(Color.yellow);
        } else {
            setText(realThingLbl);
            setBackground(Color.red);
        }

    }
}
