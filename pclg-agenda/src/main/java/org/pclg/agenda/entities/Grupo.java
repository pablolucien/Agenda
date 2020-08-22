package org.pclg.agenda.entities;

import org.pclg.tools.ChangeObserver;
import org.pclg.tools.ChangeableList;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Representa los grupos a los grupos a los que pertenece un usuario.
 * Clase inmutable, como toda clase que se respete.
 *
 * @author Pablo
 * @since 7/09/13 7:56
 */
public class Grupo implements Comparable<Grupo>, Serializable {

	/**
	 * Use for the key of a group that may be used temporally but does not
	 * exist in permanent storage.
	 */
	public static final int INVALID_KEY = -1;
	public static final Grupo NULL_VALUE = new Grupo(INVALID_KEY, "");
	public static final int NAME_FIELD_LEN = 40;

	static final Comparator<Grupo> GROUP_COMPARATOR = (o1, o2) -> {
		if (o1 == null && o2 == null) {
			return 0;
		}
		if (o1 == null) {
			return 1;
		}
		if (o2 == null) {
			return -1;
		}
		return o1.compareTo(o2);
	};

	private static final ChangeableList<Grupo> values
		= new ChangeableList<>();
	private static final long serialVersionUID = 6422010567371430538L;
	private final int clave;
	private final String nombre;
	/** Instante de la �ltima actualizacion de la lista de grupos. */
	private static long lastUpdated = Long.MIN_VALUE;

	public Grupo(final int clave, final String nombre) {
		this.clave = clave;
		this.nombre = nombre;
	}

	public static void add(final Grupo grupo) {
		values.add(grupo);
		lastUpdated = System.currentTimeMillis();
	}

	public static Grupo getGrupo(final int clave) {
		final int index = values.indexOf(new Grupo(clave, null));
		return index >= 0 ? values.get(index) : null;
	}

	public static Grupo valueOf(final String nombre) {
		for (final Grupo grupo : values) {
			if (grupo.nombre.equals(nombre)) {
				return grupo;
			}
		}
		return null;
	}

	public int getClave() {
		return clave;
	}

	public String getNombre() {
		return nombre;
	}

	public static List<Grupo> getValues() {
		return Collections.unmodifiableList(values);
	}

	public static int size() {
		return values.size();
	}

	public static long getLastUpdated() {
		return lastUpdated;
	}

	@Override
	public boolean equals(final Object o) {
		return this == o || o != null && getClass() == o.getClass() && clave == ((Grupo) o).clave;
	}

	@Override
	public int hashCode() {
		return clave;
	}

	@Override
	public String toString() {
		return nombre;
	}

	/**
	 * Inicializa los valores de los grupos.
	 *
	 * @param listaGrupos los datos.
	 */
	public static void init(final List<Grupo> listaGrupos) {
		values.clear();
		values.addAll(listaGrupos);
        lastUpdated = System.currentTimeMillis();
	}

	/**
	 * Compares this object with the specified object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * @param other the object to be compared.
	 *
	 * @return a negative integer, zero, or a positive integer as this object
	 * is less than, equal to, or greater than the specified object.
	 *
	 * @throws NullPointerException if the specified object is null
	 * @throws ClassCastException   if the specified object's type prevents it
	 *                              from being compared to this object.
	 */
	@Override
	public int compareTo(final Grupo other) {
		if (other == null) {
			return -1;
		}
		final String otroNombre = other.nombre;
		if (nombre == null && otroNombre == null) {
			return 0;
		}
		if (nombre == null) {
			return 1;
		}
		if (otroNombre == null) {
			return -1;
		}
		return nombre.compareToIgnoreCase(otroNombre);
	}

	public static void addChangeObserver(final ChangeObserver<ChangeableList<Grupo>> object) {
		values.addChangeObserver(object);
	}
}
