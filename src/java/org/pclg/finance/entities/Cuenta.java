package org.pclg.finance.entities;

import java.math.BigDecimal;

/**
 * Cuenta<BR>
 *
 * @author El Coyote Cojo
 * @since 18-jun-2010 18:02:42
 */
class Cuenta {
    enum Tipo {}   // TODO: usarlo

    private final long id;
    private final String name;
    private final String description;
    private final String tipo;
    private final BigDecimal limit;

    public Cuenta(final long id, final String name, final String description, final String tipo, final BigDecimal limit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tipo = tipo;
        this.limit = limit;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tipo='" + tipo + '\'' +
                ", limit=" + limit +
                '}';
    }
}
