package org.pclg.agenda.gui;

import org.pclg.gui.MapEditor;
import org.pclg.gui.OptionsChooserValues;
import org.pclg.tools.PropertiesHelper;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Para Encapsular la edici�n de los settings, y si me acuerdo en el futuro, reutilizarlo.
 *
 * @author El Coyote.
 * @since 5/02/16 0:52
 */
class SettingsEditor {
	@OptionsChooserValues({"de", "en", "es", "fr", "it"})
	private interface LocaleChooser {
	}

	private static final MapEditor.Property[] EDITABLE_PROPERTIES = {
		new MapEditor.Property("Application.ForceLanguage", LocaleChooser.class),
		new MapEditor.Property("Agenda.FortunesAtStartup", Boolean.class),
		new MapEditor.Property("Agenda.InterestingDatesAtStartup", Boolean.class),
		new MapEditor.Property("Agenda.show.SplashWindow", Boolean.class),
		new MapEditor.Property("Agenda.default.country", null),
		new MapEditor.Property("AgendaDb.daysFork4Birthdays", Integer.class),
		new MapEditor.Property("AgendaDb.daysFork4InterestingDates", Integer.class),
		new MapEditor.Property("AgendaDb.daysFork4RecentlyModified", Integer.class),
		new MapEditor.Property("AgendaDb.interestingDatesDivisor", Integer.class),
		new MapEditor.Property("AgendaDb.maxBackupHistory", Integer.class),
		new MapEditor.Property("PluginAction.maxHistory", Integer.class),
		new MapEditor.Property("QueryAction.maxHistory", Integer.class),
	};

	private final Component parent;
	private final Properties properties;
	private final Properties guiCustomProperties;

	SettingsEditor(final Component parent, final Properties properties,
			final Properties guiCustomProperties) {
		this.parent = parent;
		this.properties = properties;
		this.guiCustomProperties = guiCustomProperties;
	}

	void editSettings() {
		final Map<MapEditor.Property, String> map = new LinkedHashMap<>();
		for (final MapEditor.Property editableProperty : EDITABLE_PROPERTIES) {
			map.put(editableProperty, properties.getProperty(editableProperty.getName()));
		}
		final MapEditor propertiesPanel = new MapEditor(parent, map);
		if (propertiesPanel.editMap(PropertiesHelper.getStringFromProperties(
				properties, "Properties.editor.title"))) {
			final Map<String, String> newMap = propertiesPanel.getMap();
			for (final MapEditor.Property editableProperty : EDITABLE_PROPERTIES) {
				// Ponerla aqu� para que sea tomada en cuenta ahora.
				properties.setProperty(editableProperty.getName(), newMap.get(editableProperty.getName()));
				// Ponerla aqu� para que sea guardada para pr�ximas ejecuciones.
				guiCustomProperties.setProperty(editableProperty.getName(),
					newMap.get(editableProperty.getName()));
			}
		}
	}
}
