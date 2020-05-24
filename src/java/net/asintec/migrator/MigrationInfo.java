// ******************************** package
package net.asintec.migrator;


// ******************************** imports
/**
	MigrationInfo <BR>
	Provee informacion sobre la migracion: campos origen, campos destino, preprocesadores, etc
	@author El Coyote Cojo
	@version 2002.may.31 12:56:28, CEST
*/
interface MigrationInfo {
	// ******************************** Variables de clase


	// ******************************** Metodos de instancia

	/**
		Devuelve la cantidad de campos en esta migracion
		@return La cantidad de campos en esta migracion
		@since 2002.05.31
	*/
	int getRowCount();

	/**
		Devuelve el i-esimo campo origen
		@param index El campo que queremos
		@return El i-esimo campo origen
		@since 2002.05.31
	*/
	String getSourceField(int index);

	/**
		Devuelve el i-esimo campo destino
		@param index El campo que queremos
		@return El i-esimo campo destino
		@since 2002.05.31
	*/
	String getTargetField(int index);

	/**
		Devuelve el i-esimo valor constante o el inicial de autoincremento.
		@param index El campo que queremos
		@return El i-esimo valor constante o el inicial de autoincremento.
		@since 2002.05.31
	*/
	String getConstantOrAutoIncrementValue(int index);

	/**
		Devuelve el i-esimo preprocesador
		@param index El campo que queremos
		@return El i-esimo preprocesador
		@since 2002.05.31
	*/
	String getPreprcessorName(int index);

	/**
		Devuelve Los parámetros del i-esimo preprocesador
		@param index El campo que queremos
		@return Los parámetros del i-esimo preprocesador
		@since 2002.05.31
	*/
	String getPreprcessorParams(int index);
}
