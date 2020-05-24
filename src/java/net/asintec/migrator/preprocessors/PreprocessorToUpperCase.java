// ******************************** package
package net.asintec.migrator.preprocessors;

// ******************************** imports

/**
	devuelve una String en mayusculas
	@author El Coyote Cojo
	@version 2001.13.11
	@see Migrator
*/
public class PreprocessorToUpperCase implements Preprocessor {
	/** informa de lo que es capaz de hacer este señor */
	@Override
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t devuelve una String en mayusculas"
									+ "\n\t No necesita parametros"
									;
		return(description);
	}

	/**
		Obtiene los posibles parametros que utilizará este preprocesador
		En esta clase no tiene utilidad
		@param param Los parametros
	*/
	@Override
	public void setParameters(final String param) {
	}

	/**
		Procesa a 'o', presumiblemente segun lo que informa getDescription()
		@param o El objeto a procesar
		@return El resultado de procesar 'o'
	*/
	@Override
	public Object process(final Object o) {
		if(o == null) {
			return (null);
		}
		return(((String) o).toUpperCase());
	}
}
