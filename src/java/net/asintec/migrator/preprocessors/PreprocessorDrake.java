// ******************************** package
package net.asintec.migrator.preprocessors;

// ******************************** imports

import org.pclg.tools.ToolBox;

/**
	Devuelve una String con los ' 1/2' convertidos a 'M'
	@author El Coyote Cojo
	@version 2001.13.11
*/
public class PreprocessorDrake implements Preprocessor {
	/** */
	private final String oldString = "z:";

	/** */
	private final String newString = "/almacen01/httpd";

	/** informa de lo que es capaz de hacer este señor */
	@Override
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t conviere los '\\' a '/' y cambia la z: a /almacen01/httpd"
									+ "\n\t Los parametros van asi:"
									+ "\n\t string antiguo&string nuevo (AUN NO IMPLEMENTADO)"
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
		s = ToolBox.replaceAll(s, oldString, newString);
		s = ToolBox.replaceAll(s, oldString.toUpperCase(), newString);
//System.out.println("En medio [" + s + "]");
		s = ToolBox.replaceAll(s, "\\", "/");
//System.out.println("Despues [" + s + "]");
		return(s);
	}
}
