// ******************************** package
package net.asintec.migrator.preprocessors;

// ******************************** imports

/**
	Devuelve una String sin espacios en blanco al principio ni al final
	@author El Coyote Cojo
	@version 2001.11.13
	@see Migrator
*/
public class PreprocessorClipString implements Preprocessor {
	/** informa de lo que es capaz de hacer este señor */
	@Override
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t Devuelve una String sin espacios en blanco al principio ni al final"
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
		//System.out.println(getClass().getName() + " parametros: [" + param + "]");
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
		final String s = ((String) o).trim();
		//System.out.println("[" + s + "]");
		return(s);
	}
}
