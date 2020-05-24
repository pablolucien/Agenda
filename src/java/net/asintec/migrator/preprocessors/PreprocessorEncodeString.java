// ******************************** package
package net.asintec.migrator.preprocessors;

// ******************************** imports

import org.pclg.tools.ToolBox;
/**
	Devuelve una String sin espacios en blanco al principio ni al final
	@author El Coyote Cojo
	@version 2001.11.13
*/
public class PreprocessorEncodeString implements Preprocessor {
	/** La codificacion del String (original) */
	private String enc;

	/** informa de lo que es capaz de hacer este señor */
	@Override
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t Devuelve una String ASCII de MSDOS en Unicode"
									+ "\n\t Parametro: el nombre de la codificacion"
									+ "\n\t\t vg US-ASCII, ISO-8859-1 (a.k.a. ISO-LATIN-1), UTF-8, etc."
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
		System.out.println(getClass().getName() + " parametros: [" + param + "]");
		enc = param;
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
		final String s = ((String) o);
		try {
			final String t = new String(s.getBytes(enc));
			//String t = new String(s.getBytes(), enc);

			System.out.println(s + " = " + t);
			return(t);
		}
		catch(final java.io.UnsupportedEncodingException ex) {
			ToolBox.showInfo(ex);
		}
		return(s);

	}
}
