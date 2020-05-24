package org.pclg.gui;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * @author El Coyote Cojo
 * @since 14/04/18 8:11
 */
public final class JLabeledFieldGroup extends JPanel {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private int leftSize;
	private final List<JLabeledField> children = new ArrayList<>();

	public JLabeledFieldGroup() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	@Override
	public Component add(final Component comp) {
		final JLabeledField field = (JLabeledField) comp;
		adjustGroupSize(field);
		return super.add(comp);
	}

	/**
	 * Adds to this component list the field and adjust the label size to the minimum accordingly
	 * to the other children.
	 *
	 * @param field the field to add and adjust.
	 */
	public void adjustGroupSize(final JLabeledField field) {
		children.add(field);
		field.setLayout(new BoxLayout(field, BoxLayout.LINE_AXIS));
		final Dimension leftComponentSize = field.getLeftComponentSize();
		if (leftComponentSize.width > leftSize) {
			leftSize = leftComponentSize.width;
		}
		LOGGER.debug("leftSize = " + leftSize);
		for (final JLabeledField child : children) {
			final JLabel labelComponent = child.getLabelComponent();
			final Dimension preferredSize = labelComponent.getPreferredSize();
			preferredSize.width = leftSize;
			labelComponent.setPreferredSize(preferredSize);
			LOGGER.debug("labelComponent.getPreferredSize().width = " + labelComponent
				.getPreferredSize().width);
		}
	}
}
