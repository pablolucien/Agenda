package org.pclg.finance.entities;

/**
 * @author paceLucien
 * @since 18-jun-2010 10:42:10
 */
public class Class {
    private final long id;
    private final String name;
    private final String description;

    public Class(final long id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    @Override
    public String toString() {
        return "Class{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
