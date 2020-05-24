// ******************************** package
package net.asintec.migrator.preprocessors;

// ******************************** imports

/**
	Devuelve una parte de un String
	@author El Coyote Cojo
	@version 2001.12.28
	@see Migrator
*/
public class PreprocessorSubString implements Preprocessor {
	/** El inicio de la substring */
	private int start = -1;
	
	/** El fin de la substring */
	private int end = -1;

	/** informa de lo que es capaz de hacer este señor */
	@Override
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t Devuelve una parte de un String"
									+ "\n\t Parametros: inicio y fin separados por ','"
									;
		return(description);
	}

	/**
		Obtiene los posibles parametros que utilizará este preprocesador
		@param param Los parametros
	*/
	@Override
	public void setParameters(final String param) {
		//System.out.println(getClass().getName() + " parametros: [" + param + "]");
		final int indexOfComma = param.indexOf(',');
		if(indexOfComma == -1) {
			return;
		}
		try {
			start = Integer.parseInt(param.substring(0, indexOfComma).trim());
		}
		catch(final NumberFormatException ex) {
			start = -1;
		}

		try {
			end = Integer.parseInt(param.substring(indexOfComma + 1, param.length()).trim());
		}
		catch(final NumberFormatException ex) {
			end = -1;
		}
		//System.out.println(getClass().getName() + " start: [" + start + "]" + " end: [" + end + "]");
	}

	/**
		Procesa a 'o', presumiblemente segun lo que informa getDescription()
		@param o El objeto a procesar
		@return El resultado de procesar 'o'
	*/
	@Override
	public Object process(final Object o) {
		if(o == null || start == -1 || end == -1) {
			return (null);
		}
		final String s = ((String) o);

		//System.out.println("[" + s + "]");
		if(s.length() < start || s.length() < end) {
			return(null);
		}

		//System.out.println("[" + s.substring(start, end + 1) + "]\n------------------------------------------");
		return(s.substring(start, end + 1));
	}
}
