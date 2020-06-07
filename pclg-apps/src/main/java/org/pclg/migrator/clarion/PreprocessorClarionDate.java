// ******************************** package
package org.pclg.migrator.clarion;

// ******************************** imports

//import net.asintec.migrator.*;
import net.asintec.migrator.preprocessors.Preprocessor;

/**
 * Este preprocesador convierte en java.util.Date un LONG de Clarion.
 *
 * @author El Coyote Cojo
 * @version 2001.13.11
 * @see Migrator
 */
public final class PreprocessorClarionDate implements Preprocessor {
    /**
     * informa de lo que es capaz de hacer este señor.
     * @return una descripcion de o que es capaz de hacer este señor.
     */
    @Override
	public String getDescription() {
        return getClass().getName() + "\n\t Convierte en Date un LONG de Clarion";
    }

    /**
     * Obtiene los posibles parametros que utilizará este preprocesador
     * En esta clase no tiene utilidad.
     *
     * @param param Los parametros.
     */
    @Override
	public void setParameters(final String param) {
        //System.out.println(getClass().getName() + " parametros: [" + param + "]");
    }


    /**
     * Procesa a 'obj', presumiblemente segun lo que informa getDescription().
     *
     * @param obj El objeto a procesar
     * @return El resultado de procesar 'obj'
     */
    @Override
	public Object process(final Object obj) {
        if (obj == null) {
            return null;
        }

        final long absdate = Long.parseLong((String) obj);

        if (absdate == 0) {
            return null;
        }

        return ClarionTools.clarionLong2Date(absdate);
    }
}
