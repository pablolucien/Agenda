package org.pclg.filesystem.synchonizer;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Immutable Map<String, String> that wraps a Properties object.
 * @since 05/03/2019.
 */
class PropertiesMap implements Map<String, String> {
    private static final String IMMUTABLE = "This Map is immutable";
    private final Properties properties;

    PropertiesMap(final Properties properties) {
        this.properties = properties;
    }

    @Override
    public int size() {
        return properties.size();
    }

    @Override
    public boolean isEmpty() {
        return properties.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return properties.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return properties.containsValue(value);
    }

    @Override
    public String get(final Object key) {
        return properties.getProperty(String.valueOf(key));
    }

    @Override
    public String put(final String key, final String value) {
        throw new UnsupportedOperationException(IMMUTABLE);
    }

    @Override
    public String remove(final Object key) {
        throw new UnsupportedOperationException(IMMUTABLE);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> map) {
        throw new UnsupportedOperationException(IMMUTABLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(IMMUTABLE);
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<String> values() {
        return null;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return null;
    }
}
