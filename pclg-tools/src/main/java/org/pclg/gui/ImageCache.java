package org.pclg.gui;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.awt.Image;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase usada para cachear im�genes (usando SoftReference's).
 * De momento (2016.01.25) s�lo es usada por ImageButton y la idea es no tener
 * un Map por cada instancia de ImageButtonen uso, sino una sola por aplicaci�n.
 * @author Pablo
 * @since 25/01/16 20:08
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ImageCache {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final ImageCache INSTANCE = new ImageCache();
	private final Map<Object, SoftReference<Image>> imageIconMap =
		new HashMap<>();

	private ImageCache() {}

	/**
	 * Devuelve una instancia de esta clase. De momento (2016.01.25) es un
	 * singleton, pero podr�a variar.
	 *
	 * @return una instancia de esta clase.
	 */
	public static final ImageCache getInstance() {
		return INSTANCE;
	}

	public Image getImageFromCache(final Object key) {
		final SoftReference<Image> ref = imageIconMap.get(key);
		Image cachedImage = null;
		if (ref != null) {
			LOGGER.debug("Obtaining reference to " + key + " from cache");
			cachedImage = ref.get();
			if (cachedImage == null) {
				LOGGER.debug("... but the object was gone");
			} else {
				LOGGER.debug(key + " obtained from cache");
			}
		}
		return cachedImage;
	}

	public void putImageInCache(final Object key, final Image image) {
		LOGGER.debug("Putting reference to " + key + " in cache");
		imageIconMap.put(key, new SoftReference<>(image));
	}
}
