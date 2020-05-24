package org.pclg.agenda.entities;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @since 10/05/2017.
 */
public class GrupoAdapter extends XmlAdapter<String, Grupo> {
    @Override
    public Grupo unmarshal(final String nombre) throws Exception {
        return Grupo.valueOf(nombre);
    }

    @Override
    public String marshal(final Grupo grupo) throws Exception {
        return grupo.getNombre();
    }
}
