package org.pclg.agenda.entities;

import org.jpatterns.plopd.NullObjectPattern;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

/**
 * Representa los tipos de telefono.
 *
 * @author El Coyote
 * @since 7/09/13 7:56
 */
public class TipoTelefono implements Comparable<TipoTelefono> {
    private static List<TipoTelefono> values;
    private final int clave;
    private final String nombre;
    private final Color foreGroundColor;
    /**
     * Instante de la última actualizacion de la lista de TipoTelefono.
     */
    private static long lastUpdated;
    /**
     * To be used for unknown types.
     */
    @NullObjectPattern
    private static final TipoTelefono NULL_VALUE =
        new TipoTelefono(0, "", 0);

    public TipoTelefono(final int clave, final String nombre,
        final int foreGroundColorCode) {
        this.clave = clave;
        this.nombre = nombre;
        foreGroundColor = new Color(foreGroundColorCode);
    }

    public static void add(final TipoTelefono tipoTelefono) {
        values.add(tipoTelefono);
        lastUpdated = System.currentTimeMillis();
    }

    public static TipoTelefono getTipoTelefono(final int clave) {
        for (final TipoTelefono tipoTelefono : values) {
            if (tipoTelefono.clave == clave) {
                return tipoTelefono;
            }
        }
        return NULL_VALUE;
    }

    public static TipoTelefono valueOf(final Object nombre) {
        for (final TipoTelefono tipoTelefono : values) {
            if (tipoTelefono.nombre.equals(nombre)) {
                return tipoTelefono;
            }
        }
        return NULL_VALUE;
    }

	public static TipoTelefono getNullValue() {
		return NULL_VALUE;
	}

	public int getClave() {
        return clave;
    }

    public String getNombre() {
        return nombre;
    }

    public Color getForeGroundColor() {
        return foreGroundColor;
    }

    public static List<TipoTelefono> getValues() {
        return Collections.unmodifiableList(values);
    }

    public static long getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TipoTelefono tipoTelefono = (TipoTelefono) o;

        return clave == tipoTelefono.clave;
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
     * Inicializa los valores de los tipos de teléfono.
     *
     * @param listaTiposTelefono los datos.
     */
    public static void init(final List<TipoTelefono> listaTiposTelefono) {
        values = listaTiposTelefono;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param other the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(final TipoTelefono other) {
        return clave - other.clave;
    }
}
