// ******************************** package
package net.asintec.migrator;

/**
	MigratorConstants
	Esta interfaz sirve sólo para encapsular alguans constantes utiles
	@version 1.00
	@author El Coyote Cojo
*/
interface MigratorConstants {
	/** El string que indica cuando el campo debe quedar con el contenido que tiene (en el caso de sobreescritura) */
	String NO_TOCAR = "<NO_TOCAR>";

	/** El string que indica cuando el campo debe estar vacio. */
	String VACIO = "<VACIO>";

	/** El string que indica cuando el campo lleva un valor constante. */
	String CONSTANTE = "<CONSTANTE>";

	/** El string que indica cuando el campo autoincrementable. */
	String AUTO = "<AUTO>";

	/** QUERY AD HOC */
	String QUERY_AD_HOC = "QUERY AD HOC";

	/** El caracter (debe ser sólo uno) usado por el StringTokenizer en los ficheros de configuracion */
	String DELIMITER = "|";

}
