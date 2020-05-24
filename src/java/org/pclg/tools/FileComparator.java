package org.pclg.tools;

import java.io.File;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementa una forma de comparar objetos de tipo File.
 *
 * @author El Coyote Cojo.
 * @version 2.0: 2004.08.18 (Cumpleaños de Chelo, Camilo y Manolo Michelena) -
 * Agregada la posibilidad de hacer el sort por distintos criterios.
 */
public final class FileComparator implements Comparator<File> {//implements ConfigurableComparator<File> {
	public enum SortCriterium {
		/**
		 * Indica que la comparación se hará por orden alfabético del nombre, sin tomar en cuenta la extensión.
		 */
		ALPHABETIC_EXTENSIONS_APART("Alfabético extensiones aparte", false),

	    /**
	     * Indica que la comparación se hará por orden alfabético del nombre.
	     */
	    ALPHABETIC("Alfabético", false),

		/**
   		 * Indica que la comparación se hará por orden alfabético inverso del nombre, sin tomar en cuenta la extensión.
   		 */
		REVERSE_ALPHABETIC_EXTENSIONS_APART("Alfabético inverso extensiones aparte", true),

	    /**
	     * Indica que la comparación se hará por orden alfabético inverso del nombre.
	     */
	    REVERSE_ALPHABETIC("Alfabético inverso", true),
	
	    /**
	     * Indica que la comparación se hará por tamaño creciente.
	     */
	    SIZE("Tamaño", false),
	
	    /**
	     * Indica que la comparación se hará por tamaño decreciente.
	     */
	    SIZE_DECR("Tamaño descendente", true),
	
	    /**
	     * Indica que la comparación se hará por fecha creciente.
	     */
	    DATE("Fecha", false),
	
	    /**
	     * Indica que la comparación se hará por fecha decreciente.
	     */
	    DATE_DECR("Fecha descendente", true),
	
	    /**
	     * Indica que la comparación se hará por orden numérico del nombre (los nombres no numéricos van al final).
	     */
	    NUMERIC("Numérico", false),
	
	    /**
	     * Indica que la comparación se hará por orden numérico inverso del nombre (los nombres no numéricos van al principio).
	     */
	    REVERSE_NUMERIC("Numérico descendente", true);
	    
	    private final String descripcion;
	    private final boolean isReverse;
	    
	    SortCriterium(final String descripcion, final boolean isReverse) {
	    	this.descripcion = descripcion;
	    	this.isReverse = isReverse;
	    }

		/**
		 * @return the descripcion
		 */
		public String getDescripcion() {
			return descripcion;
		}

		/**
		 * @return the isReverse
		 */
		public boolean isReverse() {
			return isReverse;
		}


		@Override
		public String toString() {
			return descripcion;
		}
	}

    /**
     * El criterio del sort.
     */
    private SortCriterium sortCritery = SortCriterium.REVERSE_NUMERIC;

    /** Mensaje sobre un criterios imposible. */
    private static final String IMPOSSIBLE_CRITERY =
        "El criterio debe ser uno de los definidos en esta clase (... y no se cómo llegamos a esta situación)";

    /** Regex de sólo números. */
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("(\\d+).*");

    @Override
	public int compare(final File f1, final File f2) {
        // Si los dos archivos son del mismo tipo, uso el criterio del sort
        if ((f1.isFile() && f2.isFile()) || (f1.isDirectory() && f2.isDirectory())) {
            int retVal;
            // primero comprarlos ...
            switch (sortCritery) {
            case ALPHABETIC:
            case REVERSE_ALPHABETIC:
                retVal = f1.getName().compareToIgnoreCase(f2.getName());
                break;
            case ALPHABETIC_EXTENSIONS_APART:
            case REVERSE_ALPHABETIC_EXTENSIONS_APART:
				final String name1 = f1.getName();
                final String name2 = f2.getName();
				final String baseName1, baseName2, ext1, ext2;
				int indexOfDot;
				if ((indexOfDot = name1.lastIndexOf('.')) >= 0) {
					baseName1 = name1.substring(0, indexOfDot);
					ext1 = name1.substring(indexOfDot);
				} else {
					baseName1 = name1;
					ext1 = "";
				}

				if ((indexOfDot = name2.lastIndexOf('.')) >= 0) {
					baseName2 = name2.substring(0, indexOfDot);
					ext2 = name2.substring(indexOfDot);
				} else {
					baseName2 = name2;
					ext2 = "";
				}

				retVal = baseName1.compareToIgnoreCase(baseName2);
                if (retVal == 0) {
                	retVal = ext1.compareToIgnoreCase(ext2);
                }
                break;
            case SIZE:
            case SIZE_DECR:
                final long s1 = f1.length();
                final long s2 = f2.length();
				retVal = (int) Math.signum(s1 - s2);
                break;
            case DATE:
            case DATE_DECR:
                final long d1 = f1.lastModified();
                final long d2 = f2.lastModified();
				retVal = (int) Math.signum(d1 - d2);
                break;
            case NUMERIC:
            case REVERSE_NUMERIC:
                final long n1 = numericValue(f1);
                final long n2 = numericValue(f2);
				retVal = (int) Math.signum(n1 - n2);
                break;
            default:
                throw new InternalError(IMPOSSIBLE_CRITERY);
            }

            // ... y ahora ver si hay que invertir el orden
			return sortCritery.isReverse() ? -retVal : retVal;
        }

        // Si los dos archivos NO son del mismo tipo, van primero los directorios (y otras cosas)
		return f1.isFile() ? 1 : -1;
    }

    /**
     * Devuelve el valor numérico del nombre del archivo (sin la extensión). Si
     * el nombre no es numérico devuelve .
     * @param file el archivo a considerar.
     * @return el valor numérico de su nombre.
     */
    private long numericValue(final File file) {
        String name = file.getName();
        final int indexOfDot = name.lastIndexOf('.');
        if (indexOfDot >= 0) {
            name = name.substring(0, indexOfDot);
        }
        final Matcher matcher = NUMERIC_PATTERN.matcher(name);
        return matcher.matches() ? Long.parseLong(matcher.group(1))
            : Long.MAX_VALUE;
    }

    /**
     * Establece el criterio del sort.
     *
     * @param critery el criterio a usar.
     * @since 2004.08.18
	 * @throws IllegalArgumentException si el argumento no es valido.
     */
    //@Override
	public void setSortCritery(final SortCriterium critery) {
    	sortCritery = critery;
    }

    /**
     * Ver la documentacion de Comparator. de momento el criterio es muy simple:
     * todos los 'FileComparator' son iguales en la obscuridad.
     * 2004.08.18, Ahora consideramos que son iguales si tienen el mismo
     * criterio de sort.
     *
     * @param obj - the reference object with which to compare.
     * @return true only if the specified object is also a comparator and it
     * imposes the same ordering as this comparator.
     */
    @Override
	public boolean equals(final Object obj) {
        return obj != null && getClass() == obj.getClass()
                && sortCritery == ((FileComparator) obj).sortCritery;
    }

    /**
     * Returns a hash code value for the object.
     * @return a hash code value for this object.
     * @see Object#equals(Object)
     * @see Object#hashCode()
     * @see java.util.Hashtable
     */
    @Override
	public int hashCode() {
        return sortCritery.hashCode();
    }
}
