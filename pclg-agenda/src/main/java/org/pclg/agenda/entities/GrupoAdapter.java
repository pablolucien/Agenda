package org.pclg.agenda.entities;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @since 10/05/2017.
 */
public class GrupoAdapter extends XmlAdapter<String, Grupo> {
    @Override
    public Grupo unmarshal(final String nombre) {
        return Grupo.valueOf(nombre);
    }

    @Override
    public String marshal(final Grupo grupo) {
        return grupo.getNombre();
    }
}
