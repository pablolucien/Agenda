package org.pclg.gui;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Permite seleccionar "true", "false" o "" mediante RadioButton's.
* @author El Coyote Cojo
* @since 2016.04.02 16:18
*/
public final class RadioButtonBooleanChooser extends JPanel
		implements BooleanChooser {
	public static final String TRUE = "true";
	public static final String FALSE = "false";
    private static final long serialVersionUID = 6412340598282953292L;
    private final JRadioButton porSupuestoQueSi = new JRadioButton(TRUE);
	private final JRadioButton absolutamenteNo = new JRadioButton(FALSE);
	private final JRadioButton niSiniNo = new JRadioButton();

	private final class RadioButtonBooleanChooserMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(final MouseEvent e) {
			if (e.isControlDown()) {
				final JRadioButton source = (JRadioButton) e.getSource();
				if (source.isSelected()) {
					niSiniNo.setSelected(true);
				}
			}
		}
	}

	public RadioButtonBooleanChooser(final String value) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		porSupuestoQueSi.setBorderPainted(false);
		absolutamenteNo.setBorderPainted(false);
		add(porSupuestoQueSi);
		add(absolutamenteNo);
		setBorder(new EtchedBorder());
		final ButtonGroup group = new ButtonGroup();
		group.add(porSupuestoQueSi);
		group.add(absolutamenteNo);
		group.add(niSiniNo);
		porSupuestoQueSi.setSelected(TRUE.equalsIgnoreCase(value));
		absolutamenteNo.setSelected(FALSE.equalsIgnoreCase(value));
		porSupuestoQueSi.addMouseListener(new RadioButtonBooleanChooserMouseAdapter());
		absolutamenteNo.addMouseListener(new RadioButtonBooleanChooserMouseAdapter());
	}

	@Override
	public String getValue() {
		return porSupuestoQueSi.isSelected() ? TRUE
			: absolutamenteNo.isSelected() ? FALSE : "";
	}
}
