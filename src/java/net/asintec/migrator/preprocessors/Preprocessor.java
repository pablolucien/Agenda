// ******************************** package
package net.asintec.migrator.preprocessors;

// ******************************** imports

/**
	Quien implementa esta interfaz se encarga de preprocesar un campo 
	origen de datos antes de ponerlo en el destino.
	Pensado para usarlo en Migrator.
	@author El Coyote Cojo
	@version 2001.03.02
	@see net.asintec.migrator.Migrator
*/
public interface Preprocessor {
	/**
		Obtiene los posibles parametros que utilizará este preprocesador.
		@param param Los parametros
	*/
	void setParameters(final String param);

	/**
		Informa de lo que es capaz de hacer este señor.
		@return Una descripcion de lo que puede hacer esta clase
	*/
	String getDescription();

	/**
		Procesa a 'o', presumiblemente segun lo que informa getDescription().
		@param o El objeto a procesar
		@return El resultado de procesar 'o'
	*/
	Object process(final Object o);
}
