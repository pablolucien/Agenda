package org.pclg.gui;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ChangeObserver;
import org.pclg.tools.CompositeKey;
import org.pclg.tools.ImageTools;
import org.pclg.tools.ObservableProperties;
import org.pclg.tools.PropertiesHelper;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

@SuppressWarnings("HardCodedStringLiteral")
public class I18NManager implements ChangeObserver<ObservableProperties> {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final String ERROR_TAG = "Error";
	//FIXME: El problema de usar un pool es que aunque el objeto que lo llama
	//(pe un DataEntry) desaparezca, se siguen manteniendo referencias a los 
	//objetos en los Map, de modo que el Garbage Collector no los libera.
	private static final Map<Properties, I18NManager> POOL = new HashMap<>();
	static boolean usePool;		// Temporal para los tests hasta que resuelva el tema anterior.

	private final Map<String, Component> componentsMap = new HashMap<>();
	private final Map<CompositeKey<String, String>, AbstractButton> buttonsMap = new HashMap<>();
	private final Map<CompositeKey<String, String>, Action> actionsMap = new HashMap<>();
	private final Properties properties;

	private I18NManager(final Properties properties) {
		this.properties = properties;
		LOGGER.debug("Creando I18NManager");
	}
	
	public static final synchronized I18NManager getInstance(final Properties properties) {
		LOGGER.debug("Tratando de obtener I18NManager");
		I18NManager instance = POOL.get(properties);
		if (usePool) {
			if (instance == null) {
                instance = new I18NManager(properties);
                if (properties instanceof ObservableProperties) {
                    ((ObservableProperties) properties).addChangeObserver(instance);
                }
                POOL.put(properties, instance);
            }
		} else {
			instance = new I18NManager(properties);
		}
		if (properties instanceof ObservableProperties) {
			((ObservableProperties) properties).addChangeObserver(instance);
		}
		return instance;
	}

	/**
	 *
	 * @param component
	 * @param key
	 * @return lo que recibe, pero configurado.
	 */
	public Component configureI18NComponent(final Component component,
			final String key) {
		componentsMap.put(key, component);
		setLabel(component, key);
		return component;
	}

	/**
	 *
	 * @param button
	 * @param keyLabel
	 * @param keyIcon
	 * @return lo que recibe, pero configurado.
	 */
	public AbstractButton configureI18NButton(final AbstractButton button,
			final String keyLabel, final String keyIcon) {
		buttonsMap.put(new CompositeKey<>(keyLabel, keyIcon), button);
		setLabel(button, keyLabel, keyIcon);
		return button;
	}

	/**
	 *
	 * @param action
	 * @param keyLabel
	 * @param keyIcon
	 * @return lo que recibe, pero configurado.
	 */
	public Action configureI18NAction(final Action action,
			final String keyLabel, final String keyIcon) {
		actionsMap.put(new CompositeKey<>(keyLabel, keyIcon), action);
		setLabel(action, keyLabel, keyIcon);
		return action;
	}

	/**
	 * Establece el texto y el icono de un botón.
	 * 
	 * @param button el botón cuyas propiedades se van a establecer.
	 * @param keyLabel la clave del texto.
	 * @param keyIcon la clave del icono.
	 */
	private void setLabel(final AbstractButton button, final String keyLabel,
			final String keyIcon) {
		button.setText(
			PropertiesHelper.getStringFromProperties(properties, keyLabel));
		ImageTools.getImageIcon(PropertiesHelper
			.getStringFromProperties(properties, keyIcon))
			.ifPresent(button::setIcon);
	}

	/**
	 * Establece el texto y el icono de una Action.
	 * 
	 * @param action La Action cuyas propiedades se van a establecer.
	 * @param keyLabel la clave del texto.
	 * @param keyIcon la clave del icono.
	 */
	private void setLabel(final Action action, 
			final String keyLabel, final String keyIcon) {
		final Class<?> clazz = action.getClass();
		try {
			final Method putValue = clazz.getMethod("putValue", String.class, Object.class);
			putValue.invoke(action, Action.NAME, PropertiesHelper
				.getStringFromProperties(properties, keyLabel));
			putValue.invoke(action, Action.LARGE_ICON_KEY, ImageTools.getImageIcon(
				PropertiesHelper.getStringFromProperties(properties, keyIcon)).orElse(null));
			putValue.invoke(action, Action.SMALL_ICON, ImageTools.getImageIcon(
                PropertiesHelper.getStringFromProperties(properties, keyIcon)).orElse(null));
		} catch (final NoSuchMethodException ex) {
			LOGGER.warn("Method putValue(String, Object) non-existant in class " + clazz, ex);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
			LOGGER.error(ERROR_TAG, ex);
		}
	}

	/**
	 * Establece el texto de un componente.
	 * 
	 * @param component el componente cuyo texto se va a establecer.
	 * @param key la clave del texto.
	 */
	private void setLabel(final Component component, final String key) {
		final Class<?> clazz = component.getClass();
		try {
			final Method setText = clazz.getMethod("setText", String.class);
			setText.invoke(component, PropertiesHelper
				.getStringFromProperties(properties, key));
		} catch (final NoSuchMethodException e) {
			LOGGER.warn("Method setText(String) non-existant in class " + clazz);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
			LOGGER.error(ERROR_TAG, ex);
		}
	}


	@Override
	public void objectChanged(final ObservableProperties observableProperties) {
		SwingUtilities.invokeLater(() -> {
			for (final Entry<String, Component> entry : componentsMap.entrySet()) {
				setLabel(entry.getValue(), entry.getKey());
			}

			for (final Entry<CompositeKey<String, String>, AbstractButton> entry : buttonsMap.entrySet()) {
				final CompositeKey<String, String> key = entry.getKey();
				final String keyLabel = key.getPartA();
				final String keyIcon = key.getPartB();
				setLabel(entry.getValue(), keyLabel, keyIcon);
			}

	        for (final Entry<CompositeKey<String, String>, Action> entry : actionsMap.entrySet()) {
				final CompositeKey<String, String> key = entry.getKey();
				final String keyLabel = key.getPartA();
				final String keyIcon = key.getPartB();
				setLabel(entry.getValue(), keyLabel, keyIcon);
			}
		});
	}
}
