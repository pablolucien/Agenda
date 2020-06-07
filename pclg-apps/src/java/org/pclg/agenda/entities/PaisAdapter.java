package org.pclg.agenda.entities;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @since 10/05/2017.
 */
public class PaisAdapter extends XmlAdapter<String, Pais> {
    @Override
    public Pais unmarshal(final String nombre) throws Exception {
        return Pais.valueOf(nombre);
    }

    @Override
    public String marshal(final Pais pais) throws Exception {
        return pais.toString();
    }
}
