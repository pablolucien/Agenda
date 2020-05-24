// ******************************** package
package net.asintec.migrator.preprocessors;

// ******************************** imports

import java.util.Date;

/**
 * Este preprocesador no hace ninguna operacion.
 *
 * @author El Coyote Cojo
 * @version 2002.09.27
 * @see net.asintec.migrator.Migrator
 */
public class PreprocessorToDate implements Preprocessor {
    /**
     * Indica que en todas las llamadas a process se devolverá la
     * fecha de la primera llamada.
     */
    private final int FIRST = 0;

    /**
     * Indica que en cada llamada a process se devolverá la fecha de esa llamada.
     */
    private final int CURRENT = 1;

    /**
     * El tipo de fecha que estamos devolviendo FIRST o CURRENT.
     */
    private int type = CURRENT;

    /**
     * La fecha a devolver (debería ser inmutable o hacer un clon en process
     * por si la modifica alguien cuando estamos en FIRST.
     */
    private Date now;

    /**
     * informa de lo que es capaz de hacer este señor.
     */
    @Override
	public String getDescription() {
        final String description = getClass().getName()
                + "\n\t Convierte un String a Date"
                + "\n\t Parametros:"
                + "\n\t FIRST: Siempre devuelve la fecha y hora de la primera llamada"
                + "\n\t CURRENT: Devuelve la fecha y hora de la llamada actual";
        return (description);
    }

    /**
     * Obtiene los posibles parametros que utilizará este preprocesador
     * puede ser "FIRST": Siempre devuelve la fecha y hora de la primera llamada
     * o "CURRENT": Devuelve la fecha y hora de la llamada actual.
     *
     * @param param Los parametros
     */
    @Override
	public void setParameters(final String param) {
        if (param == null) {
            throw new IllegalArgumentException(
                    "El parámetro es null. Debe ser \"FIRST\" o \"CURRENT\"");
        }

        if (param.equalsIgnoreCase("FIRST")) {
            type = FIRST;
        } else if (param.equalsIgnoreCase("CURRENT")) {
            type = CURRENT;
        } else {
            throw new IllegalArgumentException("El parámetro es: <" + param
                    + ">. Debe ser \"FIRST\" o \"CURRENT\"");
        }
    }

    /**
     Procesa a 'o', presumiblemente segun lo que informa getDescription().
     @param o No es utilizado para nada
     @return Una fecha que puede ser la actual o la de la primera llamada
     */
    @Override
	public Object process(final Object o) {
        if (now == null || type == CURRENT) {
            now = new Date();
        }
        return(now);
	}
}
