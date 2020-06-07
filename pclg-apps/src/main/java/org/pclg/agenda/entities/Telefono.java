package org.pclg.agenda.entities;

import ezvcard.parameter.TelephoneType;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa la información del teléfono de un contacto. Clase inmutable, como
 * corresponde a gente de bien.
 *
 * @author El Coyote Cojo.
 * @since 1/08/14 8:37
 */
public class Telefono implements Serializable {
    private static final long serialVersionUID = 3474643837544140904L;
    /** El número del teléfono. */
	private final String countryPrefix;

    /** El número del teléfono. */
	private final String numero;

	/** El tipo del teléfono. 0 = null. */
	private final int tipo;

	/** To be used for unknown telephones. */
	public static final Telefono NULL_VALUE = new Telefono("", "", 0);

	public Telefono(final String countryPrefix, final String numero, final int tipo) {
		this.countryPrefix = Objects.requireNonNull(countryPrefix);
		this.numero = Objects.requireNonNull(numero);
		this.tipo = tipo;
	}

	public String getNumero() {
		return numero;
	}

	public Telefono setNumero(final String numero) {
		return new Telefono(countryPrefix, numero, tipo);
	}

	public int getTipo() {
		return tipo;
	}

	public Telefono setTipo(final int tipo) {
		return new Telefono(countryPrefix, numero, tipo);
	}

	public String getCountryPrefix() {
		return countryPrefix;
	}

	public Telefono setCountryPrefix(final String countryPrefix) {
		return new Telefono(countryPrefix, numero, tipo);
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}

		final Telefono telefono = (Telefono) other;

        return tipo == telefono.tipo && numero.equals(telefono.numero) && countryPrefix.equals(telefono.countryPrefix);
    }

    @Override
    public int hashCode() {
        int result = countryPrefix.hashCode();
        result = 31 * result + numero.hashCode();
        result = 31 * result + tipo;
        return result;
    }

    @Override
	public String toString() {
		return countryPrefix + "::" + numero + "::" + tipo;
	}

	/**
	 * Devuelve un objeto de tipo Telefono a partir de su representación
	 * como String
	 * @param strTel la representación tal como la da toString()
	 * @return un objeto de tipo Telefono a partir de su representación
	 * como String
	 */
	public static Telefono valueOf(final String strTel) {
		final String[] strings = strTel.split("::");
		return new Telefono(strings[0], strings[1], Integer.parseInt(strings[2]));
	}

    public static TelephoneType getTypes() {
        return TelephoneType.HOME;  // FIXME: devolder algo real
    }

    public String getNumber() {
        return numero.replaceAll("[^+0-9]", "");
    }
}
