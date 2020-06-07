package org.pclg.agenda.gui;

import org.apache.log4j.Logger;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.Telefono;
import org.pclg.agenda.entities.TipoTelefono;
import org.pclg.gui.TextFieldLimiter;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.StringTools;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;

import static org.pclg.agenda.entities.AgendaRecord.COUNTRY_CODE_FIELD_LEN;

/**
 * @author El Coyote
 * @since 2/08/14 2:23
 */
public class TelefonoCellEditor extends AbstractCellEditor implements TableCellEditor {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    /**
     * El componente para editar las cosas.
     */
    private final JPanel editorComponent = new JPanel();
    private final JTextField prefixEditorComponent = new JTextField();
    private final JTextField numberEditorComponent = new JTextField();
    private final transient EditorDelegate delegate;
    private String defaultCountryPrefix;
    private boolean inPopupMenu;

    TelefonoCellEditor(final Properties properties) {
        delegate = new EditorDelegate();

        editorComponent.setLayout(new BoxLayout(editorComponent, BoxLayout.X_AXIS));
        editorComponent.add(prefixEditorComponent);
        editorComponent.add(Box.createGlue());
        editorComponent.add(numberEditorComponent);

        final JPopupMenu popupMenu = createPopupMenu(properties);

        final FocusAdapter focusAdapter = new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent focusEvent) {
                final Component oppositeComponent = focusEvent.getOppositeComponent();
                final JTextComponent textComponent = (JTextComponent) focusEvent.getComponent();
                final boolean emptyNumber = StringTools.isEmptyOrBlank(textComponent.getText());
                if (oppositeComponent != prefixEditorComponent && oppositeComponent != numberEditorComponent && !inPopupMenu) {
                    if (emptyNumber) {
                        fireEditingCanceled();
                        LOGGER.warn("canceled");
                    } else {
                        fireEditingStopped();
                        LOGGER.warn("stopped");
                    }
                }
            }
        };

        prefixEditorComponent.setDocument(new TextFieldLimiter(COUNTRY_CODE_FIELD_LEN));
        prefixEditorComponent.setColumns(COUNTRY_CODE_FIELD_LEN);
        prefixEditorComponent.setHorizontalAlignment(JTextField.RIGHT);
        prefixEditorComponent.addActionListener(delegate);
        prefixEditorComponent.addFocusListener(focusAdapter);

        numberEditorComponent.setHorizontalAlignment(JTextField.RIGHT);
        numberEditorComponent.setDocument(new TextFieldLimiter(AgendaRecord.TELEPHONE_FIELD_LEN));
        numberEditorComponent.setColumns(AgendaRecord.TELEPHONE_FIELD_LEN);
        numberEditorComponent.addActionListener(delegate);
        numberEditorComponent.addFocusListener(focusAdapter);


        numberEditorComponent.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent ev) {
                    if (SwingUtilities.isRightMouseButton(ev)) {
                        final Telefono telefono = getCellEditorValue();
                        for (final MenuElement menuElement : popupMenu.getSubElements()) {
                            final TipoTelefonoMenuItem menuItem = (TipoTelefonoMenuItem) menuElement;
                            menuItem.setSelected(menuItem.getClaveTipoTelefono() == telefono.getTipo());
                        }
                        final Point popupMenuPoint = ev.getPoint();
                        popupMenu.show(numberEditorComponent, popupMenuPoint.x, popupMenuPoint.y);
                    }
                }
            }
        );

        numberEditorComponent.setBackground(Color.yellow);
    }

    private JPopupMenu createPopupMenu(final Properties properties) {
        final JPopupMenu popupMenu = new JPopupMenu(){
            @Override
            public void setVisible(final boolean visible) {
                inPopupMenu = visible;
                super.setVisible(visible);
            }
        };
        final ActionListener listener = actionEvent -> {
            final TipoTelefonoMenuItem src = (TipoTelefonoMenuItem) actionEvent.getSource();
            final Telefono telefono = delegate.getCellEditorValue().setTipo(src.getClaveTipoTelefono());
            delegate.setValue(telefono);
        };

        final ButtonGroup group = new ButtonGroup();
        final TipoTelefonoMenuItem menuItem = addMenuItem(popupMenu,
            listener, group, TipoTelefono.getNullValue());
        menuItem.setText(PropertiesHelper
            .getStringFromProperties(properties, "TipoTelefono.desconocido"));
        for (final TipoTelefono item : TipoTelefono.getValues()) {
            addMenuItem(popupMenu, listener, group, item);
        }
        return popupMenu;
    }


    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
            final int row, final int column) {
        if (value instanceof Telefono) {
            final Telefono telefono = (Telefono) value;
            delegate.setValue(telefono);
            numberEditorComponent.setForeground(
                TipoTelefono.getTipoTelefono(
                    telefono.getTipo()).getForeGroundColor());
        }
        return editorComponent;
    }

    private static TipoTelefonoMenuItem addMenuItem(final JPopupMenu popupMenu,
            final ActionListener listener, final ButtonGroup group, final TipoTelefono item) {
        final TipoTelefonoMenuItem menuItem = new TipoTelefonoMenuItem();
        GUITools.addRadioButtonMenuItem(listener, popupMenu, group, item.getNombre(), null, menuItem);
        menuItem.setForeground(item.getForeGroundColor());
        menuItem.setClaveTipoTelefono(item.getClave());
        return menuItem;
    }

    @Override
    public Telefono getCellEditorValue() {
        Telefono telefono = delegate.getCellEditorValue();
        final String number = numberEditorComponent.getText();
        if (!number.equals(telefono.getNumero())) {
            telefono = telefono.setNumero(number);
        }
        final String prefix = prefixEditorComponent.getText();
        if (!prefix.equals(telefono.getCountryPrefix())) {
            telefono = telefono.setCountryPrefix(prefix);
        }
        return telefono;
    }

    void setDefaultCountryPrefix(final String defaultCountryPrefix) {
        this.defaultCountryPrefix = defaultCountryPrefix;
    }

    static class TipoTelefonoMenuItem extends JRadioButtonMenuItem {
        private static final long serialVersionUID = -3077752025083789970L;
        int claveTipoTelefono;

        int getClaveTipoTelefono() {
            return claveTipoTelefono;
        }

        void setClaveTipoTelefono(final int claveTipoTelefono) {
            this.claveTipoTelefono = claveTipoTelefono;
        }
    }

    private class EditorDelegate implements ActionListener {
        private Telefono telefono;

        private void setValue(final Telefono telefono) {
            this.telefono = telefono;
            final String countryPrefix = telefono.getCountryPrefix();
            prefixEditorComponent.setText(StringTools.isEmptyOrBlank(countryPrefix) ? defaultCountryPrefix : countryPrefix);
            numberEditorComponent.setText(telefono.getNumero());
        }

        private Telefono getCellEditorValue() {
            //return telefono = telefono.setCountryPrefix(prefixEditorComponent.getText()).setNumero(numberEditorComponent.getText());
            return telefono;
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            if (StringTools.isEmptyOrBlank(prefixEditorComponent.getText()) || StringTools.isEmptyOrBlank(numberEditorComponent.getText())) {
                fireEditingCanceled();
            } else {
                fireEditingStopped();
            }
        }
    }
}
