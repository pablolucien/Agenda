package dbinfo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
	TableInfo <BR>
	Encapsula la informacion de una tabla de la base de datos.
	@author El Coyote Cojo
	@version 2002.jun.29 15:30:08, CEST
*/
final class TableInfo {
	// ******************************** Variables de clase

	// ******************************** Variables de instancia
	/** La conexion con la db. */
	private Connection conn;

	/** Los campos de la tabla. */
	private String[] fieldNames;

	/** Almacena la informacion de los campos de esta tabla. */
	private final Map fieldsMap = new HashMap();


	// ******************************** Constructores

	/**
	 * Constructor por omision.
	 */
	public TableInfo() {
	}


	// ******************************** Metodos de instancia

	/**
	 * Devuelve Un String[] con los nombres de los campos de esta tabla.
	 *
	 * @return Un String[] con los nombres de los campos de esta tabla.
	 */
	public String[] getFields() {
		final String[] dummy = new String[fieldNames.length];
		System.arraycopy(fieldNames, 0, dummy, 0, fieldNames.length);
		return dummy;
	}



	// ******************************** Metodos estaticos

}
