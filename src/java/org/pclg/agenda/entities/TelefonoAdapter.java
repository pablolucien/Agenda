package org.pclg.agenda.entities;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @since 10/05/2017.
 */
public class TelefonoAdapter extends XmlAdapter<String, Telefono> {
    @Override
    public Telefono unmarshal(final String telAndType) throws Exception {
        return new Telefono("321", "13213", 1);
    }

    @Override
    public String marshal(final Telefono telefono) throws Exception {
        return telefono.toString();
    }
}
