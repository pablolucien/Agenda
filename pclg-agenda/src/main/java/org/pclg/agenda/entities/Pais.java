package org.pclg.agenda.entities;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Representa los pa�ses para el combo box.
* @author Pablo
* @since 3/08/13 10:28
*/
public class Pais implements Serializable {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private static final long serialVersionUID = -1436198872032766292L;
	private static final Map<String, Pais> ATLAS = new TreeMap<>();
	private static final Pais unknownCountry = new Pais("0", "Erewhon", "");
	static final Pais NULL_INSTANCE = new Pais("", "", "");
	private static Pais defaultCountry;
	private final String countryCode;
	private String countryName;
	private String formatoTelefono;

	/**
     * El constructor lo pone en ATLAS. �qu� cosas!
	 */
	public Pais(final String countryCode, final String countryName, final String formatoTelefono) {
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.formatoTelefono = formatoTelefono;
		ATLAS.put(countryCode, this);
	}

	public static Pais getDefaultCountry() {
		return defaultCountry;
	}

	public static void setDefaultCountry(final Pais defaultCountry) {
		Pais.defaultCountry = defaultCountry;
	}

    public static Pais getUnknownCountry() {
        return unknownCountry;
    }

    public String getCountryName() {
		return countryName;
	}

    void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

	public String getCountryCode() {
		return countryCode;
	}

	public String getFormatoTelefono() {
		return formatoTelefono;
	}

	public void setFormatoTelefono(final String formatoTelefono) {
		this.formatoTelefono = formatoTelefono;
	}

	public static void setUnknownCountry(final String unknownCountryName) {
        unknownCountry.setCountryName(unknownCountryName);
	}

	public static Pais getInstance(final String countrycode) {
        final Pais pais = countrycode == null ? null : ATLAS.get(countrycode);
		return pais == null ? unknownCountry : pais;
	}

	public static Pais getValue(final String countryName) {
		LOGGER.debug(countryName);
		for (final Pais pais : ATLAS.values()) {
			if (pais.countryName.equalsIgnoreCase(countryName)) {
				return pais;
			}
		}
		return unknownCountry;
	}

	/**
	 * Devuelve la lista de los pa�ses.
	 * @return  la lista de los pa�ses conocidos por esta clase.
	 */
	public static Collection<Pais> getPaises() {
		return Collections.unmodifiableCollection(ATLAS.values());
	}
	

	public static Pais valueOf(final String value) {
		for (final Pais pais : ATLAS.values()) {
			final String string = pais.toString();
			LOGGER.debug(string);
			if (string.equals(value)) {
				return pais;
			}
		}
		return null;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Pais pais = (Pais) o;

        return !(countryCode != null ? !countryCode.equals(pais.countryCode)
            : pais.countryCode != null);

    }

	@Override
	public int hashCode() {
		return countryCode != null ? countryCode.hashCode() : 0;
	}

	@Override
	public String toString() {
		return countryCode + " - " + countryName;
	}
}
