package org.pclg.finance.entities;

/**
 * @author paceLucien
 * @since 18-jun-2010 11:19:41
 */
class Categorie {
    enum Tipo {}   // TODO: usarlo
    private final long id;
    private final String name;
    private final String description;
    private final char tipo;
    private final boolean taxRelated;

    public Categorie(final long id, final String name, final String description, final char tipo,
            final boolean taxRelated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tipo = tipo;
        this.taxRelated = taxRelated;
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

    public char getTipo() {
        return tipo;
    }

    public boolean isTaxRelated() {
        return taxRelated;
    }

    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tipo=" + tipo +
                '}';
    }
}
