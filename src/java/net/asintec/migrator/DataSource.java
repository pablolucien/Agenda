// ********************* package
package net.asintec.migrator;


// ********************* imports

import java.sql.Connection;

/**
	DataSource
	Esta interfaz describe los metodos que debe proveer una fuente de datos para Migrator

	@author El Coyote Cojo
	@version 2001.nov.28 17:52:07, CEST
*/
public interface DataSource {
	/**
		Provee una descripcion de lo que puede hacer esta clase
		@return Una descripcion de lo que puede hacer esta clase
	*/
	String getDescription();

	/**
		Indica la base de datos con la que se desea trabajar. Cada implementacion sabrá qué debe hacer
		@param databaseName Un identificador de la base de datos con la que se desea trabajar
		@param password La contraseña a usar
	*/
	void setDatabase(String databaseName, String password);

	/**
		Devuelve la Connection a la base de datos con la que se está trabajando
		@return la Connection a la base de datos con la que se está trabajando
		@since 2002.06.14
	*/
	Connection getConnection();

	/**
		Abre la base de datos. Cada implementacion sabrá qué debe hacer
	*/
	void openDatabase();

	/**
		Cierra la base de datos. Cada implementacion sabrá qué debe hacer
	*/
	void closeDatabase();

	/**
		Devuelve un arreglo con los nombres de las tablas en la base de datos
		@return Un arreglo con los nombres de las tablas en la base de datos
	*/
	String[] getTables();

	/**
		Abre una tabla de la base de datos
		@param tableName El nombre de la tabla que se desea abrir
	*/
	void openTable(String tableName);

	/**
		Devuelve un arreglo con los nombres de los campos de la tabla que está abierta
		@return Un arreglo con los nombres de los campos de la tabla que está abierta
	*/
	String[] getFieldNames();

	/**
		Devuelve un arreglo con los tipos de los campos de la tabla que está abierta
		@return Un arreglo con los tipos de los campos de la tabla que está abierta
	*/
	int[] getFieldTypes();

	/**
		Devuelve un arreglo con los nombres de los tipos de los campos de la tabla que está abierta
		@return Un arreglo con los nombres de los tipos de los campos de la tabla que está abierta
	*/
	String[] getFieldTypeNames();

	/**
		Devuelve un arreglo con los tamaños de los campos de la tabla que está abierta
		@return Un arreglo con los tamaños de los campos de la tabla que está abierta
	*/
	int[] getFieldSizes();

	/** Devuelve los campos que son indices sin duplicados */
	boolean[] getUniqueFields();

	/**
		Devuelve un arreglo con los nombres de los campos de la tabla que forman la clave primaria o null
		@return Un arreglo con los nombres de los campos de la tabla que forman la clave primaria o null
		@since 2003.09.21 00:24
	*/
	String[] getPrimaryKey();

	/** Indica sobre que tabla vamos a trabajar */
	void setSelectedTable(String table);

	/** Indica sobre que campos vamos a trabajar */
	void setSelectedFields(String[] fields);

	/**
		Devuelve el siguente registro o null si no hay más
	*/
	Object[] getNextRecord();

	/**
		Indica si estamos en el final de la tabla
		@return true si estamos en el final de la tabla
	*/
	boolean eof();

	/**
		Establece el query a usar para acceder a la base de datos
	*/
	void setQuery(String query);

	/**
		Inicia el proceso de obtencion de datos
	*/
	boolean executeQuery();

	void setConnection(Connection connection);

	/**
		Agrega un registro a la base de datos.
		@return true si la operación fué exitosa
	*/
	boolean addRecord(Object[] record);

}
