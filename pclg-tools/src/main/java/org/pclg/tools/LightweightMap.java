package org.pclg.tools;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author El Coyote Cojo.
 * @since 19-oct-2007 12:40:37
 */
final class LightweightMap<K, V> implements Map<K, V> {
	private final K[] keys;
	private final V[] values;
    public static final String MSG_SIZE_MISMATCH =
            "Size of keys differ from size of values";

    /**
	 * Construye un LightweightMap que asocia a cada elemento en <code> keys </code>
	 * el correspondiente elemento en <code> values </code>. Los elementos en
	 *  <code> keys </code> deben estar ordenados.
	 * @param keys las claves del Map.
	 * @param values los valores asociados a cada clave.
	 */
	public LightweightMap(final K[] keys, final V[] values) {
		if (keys.length != values.length) {
			throw new IllegalArgumentException(MSG_SIZE_MISMATCH);
		}
		this.keys = keys.clone();
		this.values = values.clone();
	}

	/** {@inheritDoc} */
	@Override
    public int size() {
		return keys.length;
	}

	/** {@inheritDoc} */
	@Override
    public boolean isEmpty() {
		return keys.length == 0;
	}

	/** {@inheritDoc} */
	@Override
    public boolean containsKey(final Object key) {
		return Arrays.binarySearch(keys, key) >= 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsValue(final Object value) {
		for (final V v : values) {
			if (value == null && v == null || value != null && value.equals(v)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
    public V get(final Object key) {
		final int index = Arrays.binarySearch(keys, key);
		return index >= 0 && index < keys.length ? values[index] : null;
	}

	/** {@inheritDoc} */
	@Override
	public V put(final K key, final V value) {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public V remove(final Object key) {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public Set<Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}
}
