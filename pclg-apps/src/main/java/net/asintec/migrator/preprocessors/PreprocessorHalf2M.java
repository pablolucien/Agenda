// ******************************** package
package net.asintec.migrator.preprocessors;

// ******************************** imports

/**
	Devuelve una String con los ' 1/2' convertidos a 'M'
	@author El Coyote Cojo
	@version 2001.13.11
	@see Migrator
*/
public class PreprocessorHalf2M implements Preprocessor {
	/** informa de lo que es capaz de hacer este señor */
	@Override
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t Devuelve una String con los ' 1/2' convertidos a 'M'"
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
		String s = ((String) o).trim();
//System.out.println("Antes [" + s + "]");
		int index = s.indexOf(" 1/2");			// Con espacio
		if(index > -1) {
			s = s.substring(0, index) + "M";
		}
		index = s.indexOf("1/2");				// Sin espacio
		if(index > -1) {
			s = s.substring(0, index) + "M";
		}
//System.out.println("Despues [" + s + "]");
		return(s);
	}
}
