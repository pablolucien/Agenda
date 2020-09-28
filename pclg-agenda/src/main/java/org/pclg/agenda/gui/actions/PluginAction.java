package org.pclg.agenda.gui.actions;

import org.apache.logging.log4j.Logger;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.plugins.Plugin;
import org.pclg.gui.VersatileComboBox;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;
import org.pclg.tools.StringTools;

import javax.swing.AbstractAction;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Properties;

public final class PluginAction extends AbstractAction {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = 165616677733412729L;
    private final Frame parent;
    private final transient AgendaDb agendaDb;

	/** �ltimos plugins usados. */
	private static final String LAST_PLUGIN_KEY_BASE_NAME = "PluginAction";
    private final transient ActionCallback callback;
	private final VersatileComboBox pluginsValues;
    private final Properties properties;

    public PluginAction(final Frame parent, final Properties properties,
		    final AgendaDb agendaDb, final int numberOfPlugins, final ActionCallback callback) {
        this.parent = parent;
        this.agendaDb = agendaDb;
        this.callback = callback;
        this.properties = properties;
		pluginsValues = new VersatileComboBox(parent, properties, LAST_PLUGIN_KEY_BASE_NAME, numberOfPlugins);
	}

	@Override
	public void actionPerformed(final ActionEvent ev) {
	    try {
	    	if (pluginsValues.accept()) {
				final String selectedItem = pluginsValues.getSelectedItem();
				if (StringTools.isEmptyOrBlank(selectedItem)) {
                    return;
                }
				final String[] args = selectedItem.split("\\s+");
				final Plugin plugin = (Plugin) Class.forName(args[0]).newInstance();
                GUITools.executeWithWaitCursor(parent, () -> {
                    try {
                        if (args.length > 1) {
                            final String[] args2 = new String[args.length - 1];
                            System.arraycopy(args, 1, args2, 0, args2.length);
                            agendaDb.executePlugin(plugin, properties, args2);
                        } else {
                            agendaDb.executePlugin(plugin, properties);
                        }
                    } catch (SQLException e) {
                        LOGGER.error(LoggerFactory.ERROR_TAG, e);
                    }
                });
            }
	    } catch (InstantiationException
			| IllegalAccessException | ClassNotFoundException ex) {
	        	LOGGER.error(LoggerFactory.ERROR_TAG, ex);
	    } finally {
            callback.call();
        }
    }

    public void saveYourProperties(final Properties guiProperties) {
		pluginsValues.saveYourProperties(guiProperties);
	}
}