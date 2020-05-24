package org.pclg.alter;

import org.pclg.tools.GUITools;
import org.pclg.tools.PropertiesHelper;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Properties;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 15/05/17 14:11
 */
final class ButonPanel extends JPanel {
    private static final long serialVersionUID = -2590644749753317320L;
    /**
     * Indica si el ordenamiento es acendente o no.
     */
    private boolean ascendente = true;

    /**
     * Indica si el ordenamiento es alfabetico o no (numerico).
     */
    private boolean alfabetico;

    /**
     * Para seleccionar el tipo de cambio de capitalizacion.
     */
    private final CaseChoicer caseChoice = new CaseChoicer();

    /**
     * Lo que introdude _Bt.
     */
    private final JTextField _BtChars = new JTextField("_");

	/**
	 * Para seleccionar el valor de inicio de las ordenaciones.
	 */
	private final JTextField orderStart = new JFormattedTextField(Integer.valueOf(1));

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
	 */
    ButonPanel(final ActionListener actionListener, final Properties properties) {
        super(new GridLayout(1, 0));
        setUpButtons(actionListener, properties);
    }

    private void setUpButtons(final ActionListener listener, final Properties properties) {
		final ListenerHelper listenerHelper = new ListenerHelper(listener);
        //
        final JButton lowerBt = GUITools.addButton(e -> listenerHelper.notifyEvent(e,
			AlterCommand.LOWER), this,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.lowerBt.text"));
        final JButton orderBt = GUITools.addButton(e -> listenerHelper.notifyEvent(e,
			AlterCommand.ORDER), this,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.orderBt.text"));
        final JButton _Bt = GUITools.addButton(e -> listenerHelper.notifyEvent(e,
			AlterCommand.UNDERSCORE), this,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel._Bt.text"), Color.blue,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel._Bt.tooltip"));
        final JButton __Bt = GUITools.addButton(e -> listenerHelper.notifyEvent(e,
			AlterCommand.UNDERSCORE2), this,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.__Bt.text"), Color.magenta, "");
        final JButton replaceBt = GUITools.addButton(e -> listenerHelper.notifyEvent(e,
			AlterCommand.REPLACE), this, PropertiesHelper.getStringFromProperties(properties, "ButonPanel.replaceBt.text"));
        final JButton ezrepBt = GUITools.addButton(e -> listenerHelper.notifyEvent(e,
			AlterCommand.DIR_NAME), this,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.ezrepBt.text"), Color.blue,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.ezrepBt.tooltip"));
        final JButton compBt = GUITools.addButton(e -> listenerHelper.notifyEvent(e,
			AlterCommand.COMPARE), this,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.compBt.text"), Color.blue,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.compBt.tooltip"));
        final JButton viewBt = GUITools.addButton(null, this,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.viewBt.text"), Color.darkGray,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.viewBt.tooltip"));
        final JButton quitBt = GUITools.addButton(e -> listenerHelper.notifyEvent(e,
			AlterCommand.QUIT), this,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.quitBt.text"), Color.red,
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.quitBt.tooltip"));

        /* Para seleccionar el tipo de ordenamiento. */
        final JComboBox<String> orderChoice = new JComboBox<>(new String[] {
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.orderChoice.asc"),
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.orderChoice.desc"),
			PropertiesHelper.getStringFromProperties(properties, "ButonPanel.orderChoice.alfa")
		});

        // Poner los choices en los botones
        orderChoice.addItemListener(event -> {
            switch (orderChoice.getSelectedIndex()) {
                case 0:
                    ascendente =  true;
                    alfabetico =  false;
                    break;
                case 1:
                    ascendente =  false;
                    alfabetico =  false;
                    break;
                case 2:
                    ascendente =  true;
                    alfabetico =  true;
                    break;
                default:
                    throw new IllegalStateException();
            }
        });

        final JPanel orderConfigPane = new JPanel(new GridLayout());
        orderConfigPane.add(orderChoice);
        orderConfigPane.add(orderStart);
        addInternalComponent(orderBt, orderConfigPane);
        addInternalComponent(lowerBt, caseChoice);
        addInternalComponent(_Bt, _BtChars);

        GUITools.setEqualPreferredDimensions(lowerBt, orderBt, _Bt, __Bt, replaceBt, ezrepBt, compBt, viewBt, quitBt);
    }

    private static void addInternalComponent(final AbstractButton container, final Component child) {
        container.setLayout(new BorderLayout());
        container.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(child, BorderLayout.EAST);
    }

    boolean isAscendente() {
        return ascendente;
    }

    int getOrderStart() {
        return Integer.valueOf(orderStart.getText());
    }

    boolean isAlfabetico() {
        return alfabetico;
    }

    CaseChoicer.Option getCaseChoice() {
        final CaseChoicer.Option selectedItem = (CaseChoicer.Option) caseChoice.getSelectedItem();
        // por seguridad, que quede siempre sólo la extension
        caseChoice.setSelectedItem(CaseChoicer.Option.LOWER_EXT);
        return selectedItem;
    }

    String getRelleno() {
        return _BtChars.getText();
    }
}
