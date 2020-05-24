package org.pclg.migrator.clarion;

import net.asintec.migrator.preprocessors.Preprocessor;

/**
 * Este preprocesador elimina los espacios sobrantes a la derecha de un memo.
 *
 * @author El Coyote Cojo
 * @version 2014.04.12
 */
public final class PreprocessorClarionMemo implements Preprocessor {
	private int cols;
	private int rows;

    /**
     * informa de lo que es capaz de hacer este señor.
     * @return una descripcion de o que es capaz de hacer este señor.
     */
    @Override
	public String getDescription() {
        return getClass().getName()
			+ "\n\t Elimina los espacios sobrantes a la derecha de un memo"
			+ "\n\t Parámetros: ancho y alto del memo en formato cols:rows";
    }

    /**
     * Obtiene los posibles parametros que utilizará este preprocesador
     * En esta clase no tiene utilidad.
     *
     * @param param Los parametros.
     */
    @Override
	public void setParameters(final String param) {
		final String[] vals = param.split(":");
		cols = Integer.parseInt(vals[0]);
		rows = Integer.parseInt(vals[1]);
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
		final StringBuilder builder = new StringBuilder(cols * rows);
        final String memo = (String) obj;
		char[] line;
		for (int row = 0; row < rows; row++) {
			line = memo.substring(row * cols, (row + 1) * cols).toCharArray();
			for (int ii = cols - 1; ii >= 0 ; ii--) {
				if (line[ii] != ' ') {
					builder.append(line, 0, ii + 1).append('\n');
					break;
				}
			}
		}

        return builder.toString();
    }
}
