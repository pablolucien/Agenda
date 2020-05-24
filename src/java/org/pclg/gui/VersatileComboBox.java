package org.pclg.gui;

import java.awt.Dialog;
import java.awt.event.ComponentListener;
import javax.swing.JDialog;
import org.pclg.tools.ImageTools;
import org.pclg.tools.PropertiesHelper;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.util.Properties;
import java.util.Vector;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * Class that shows a combobox with options that are stored in a properties object.
 * The options can be added and retrieved, and a limit to the quantity of items can be set.
 *
 * @since 01/08/2018.
 */
public class VersatileComboBox extends JComboBox<String> {
    private static final long serialVersionUID = -3729565099618367038L;
    private static final String ITEM_SELECTOR = ".item.";
    private static final String DIALOG_IMAGE_SELECTOR = ".dialogImage";
    private static final String DIALOG_OK_OPTION = ".OK";
    private static final String DIALOG_KO_OPTION = ".KO";
    private static final String PROMPT_SELECTOR = ".prompt";
    private final String[] optionPaneOptions = new String[2];
    private final String key;
    private final Vector<String> itemsList;
    private int numberOfItems;
	private final JOptionPane optionPane;
    private final JDialog dialog;

    public VersatileComboBox(final Component parent, final Properties properties, final String key,
		final int numberOfItems) {
        this.key = key;
		this.numberOfItems = numberOfItems;
        final String prompt = PropertiesHelper.getStringFromProperties(properties, key + PROMPT_SELECTOR);
        final String imagePath = PropertiesHelper.getStringFromProperties(properties, key + DIALOG_IMAGE_SELECTOR);
        itemsList = new Vector<>(numberOfItems);
        setModel(new DefaultComboBoxModel<>(itemsList));
        for (int ii = 0; ii < numberOfItems; ii++) {
            final String item = properties.getProperty(key + ITEM_SELECTOR + ii);
            if (!isEmptyOrBlank(item) && !itemsList.contains(item)) {
                itemsList.add(item);
            }
        }
        setEditable(true);
        if (getItemCount() > 0) {
            setSelectedIndex(0);
        }

        optionPaneOptions[0] = PropertiesHelper.getStringFromProperties(properties,key + DIALOG_OK_OPTION);
        optionPaneOptions[1] = PropertiesHelper.getStringFromProperties(properties,key + DIALOG_KO_OPTION);
        optionPane = new JOptionPane(this, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION,
            ImageTools.getImageIcon(imagePath).orElse(null), optionPaneOptions, optionPaneOptions[0]);
        dialog = optionPane.createDialog(parent, prompt);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
	}

	public int getLimit() {
        return numberOfItems;
    }

    public void setLimit(final int limit) {
        numberOfItems = limit;
    }

    public boolean accept() {
		dialog.setVisible(true);
		if (optionPane.getValue() == optionPaneOptions[0]) {
            final String selectedItem = getSelectedItem();
            if (!isEmptyOrBlank(selectedItem)) {
                if (itemsList.contains(selectedItem)) {
                    removeItem(selectedItem);
                }
                insertItemAt(selectedItem, 0);
                setSelectedIndex(0);
            }
            return true;
        }
		return false;
    }

    @Override
    public synchronized void addComponentListener(final ComponentListener listener) {
        if (dialog != null) {
            dialog.addComponentListener(listener);
        }
    }

    @Override
    public String getSelectedItem() {
        return (String) super.getSelectedItem();
    }

    public void saveYourProperties(final Properties guiProperties) {
        guiProperties.stringPropertyNames().stream()
            .filter(k -> k.startsWith(key + ITEM_SELECTOR))
            .forEach(guiProperties::remove);
        for (int ii = 0, nn = itemsList.size(); ii < numberOfItems && ii < nn; ii++) {
            final String query = itemsList.get(ii);
            if (!isEmptyOrBlank(query)) {
                guiProperties.setProperty(key + ITEM_SELECTOR + ii, query);
            }
        }
    }
}
