package org.pclg.agenda.entities;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @since 10/05/2017.
 */
public class PaisAdapter extends XmlAdapter<String, Pais> {
    @Override
    public Pais unmarshal(final String nombre) {
        return Pais.valueOf(nombre);
    }

    @Override
    public String marshal(final Pais pais) {
        return pais.toString();
    }
}
