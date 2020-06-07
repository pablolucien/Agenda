package org.pclg.agenda.gui;

import org.pclg.agenda.entities.Pais;
import org.pclg.agenda.entities.Telefono;
import org.pclg.agenda.entities.TipoTelefono;
import org.pclg.tools.GUITools;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 2/08/14 20:57
 */
final class TelephoneCellRenderer implements TableCellRenderer {
	private final JPanel rendererComponent = new JPanel();
    private final JLabel prefixRendererComponent = new JLabel("", SwingConstants.LEFT);
    private final JLabel numberRendererComponent = new JLabel("", SwingConstants.RIGHT);

    TelephoneCellRenderer() {
        rendererComponent.setLayout(new BoxLayout(rendererComponent, BoxLayout.X_AXIS));
        rendererComponent.add(prefixRendererComponent);
        rendererComponent.add(Box.createGlue());
        rendererComponent.add(numberRendererComponent);
    }

    /**
     * Returns the default table cell renderer.
     * <p/>
     * During a printing operation, this method will be called with
     * <code>isSelected</code> and <code>hasFocus</code> values of
     * <code>false</code> to prevent selection and focus from appearing
     * in the printed output. To do other customization based on whether
     * or not the table is being printed, check the return value from
     * {@link javax.swing.JComponent#isPaintingForPrint()}.
     *
     * @param table      the <code>JTable</code>
     * @param value      the value to assign to the cell at
     *                   <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus   true if cell has focus
     * @param row        the row of the cell to render
     * @param column     the column of the cell to render
     * @return the default table cell renderer
     * @see javax.swing.JComponent#isPaintingForPrint()
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table,
            final Object value, final boolean isSelected, final boolean hasFocus, final int row,
            final int column) {
        final Telefono telefono = (Telefono) value;
        final String countryPrefix = telefono.getCountryPrefix();
		numberRendererComponent.setText(GUITools.formatNumberWithMask(telefono.getNumero(), Pais.getInstance(countryPrefix).getFormatoTelefono()));
        numberRendererComponent.setForeground(TipoTelefono.getTipoTelefono(telefono.getTipo()).getForeGroundColor());
        numberRendererComponent.setToolTipText(TipoTelefono.getTipoTelefono(telefono.getTipo()).getNombre());

        prefixRendererComponent.setText(countryPrefix);
		return rendererComponent;
    }
}
