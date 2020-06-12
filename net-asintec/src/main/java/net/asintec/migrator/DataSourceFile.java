// ********************* package
package net.asintec.migrator;


// ********************* imports
import org.pclg.tools.ArrayTools;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Types;

/**
	DataSourceFile
	Esta clase es superclass de aquellas que se basan en un determinado tipo
	de archivos como si fueran tablas y el directorio donde estan, la base de datos,
	por ejemplo: texto, clarion, dbf, etc

	@author El Coyote Cojo
	@version 2002.ene.17 21:06:07, CEST
*/
public abstract class DataSourceFile implements DataSource {
	// ********************* Variables de instancia
	/** La 'base de datos' (El directorio donde estan las 'tablas') */
	protected File dir;

	/** Las 'tablas' del sistema (archivos de cietro tipo) */
	private String[] tables;

	/** El nombre del archivo de datos con el que estamos trabajando */
	protected String tableName;

	/** Los nombres de los campos */
	protected String[] fieldNames;

	/** Los campos seleccionados para el query */
	protected String[] selectedFields;

	
	protected String dataSourceFileExtension;

	// ********************* Metodos de instancia
	/**
		Indica la base de datos con la que se desea trabajar.
		En nuestro caso una base de datos en un directorio
		@param databaseName Un identificador de la base de datos con la que se desea trabajar
		@param password La contraseña a usar
	*/
	@Override
	public void setDatabase(final String databaseName, final String password) {
		//debug("setDatabase(): " + databaseName);
		if(databaseName != null) {
			dir = new File(databaseName);
		}
		if(!dir.exists() || !dir.isDirectory()) {
			throw new IllegalArgumentException(databaseName + " no es un diectorio");
		}
	}

	/**
		Establece una conexion a la "base de datos" (directorio con archivos de texto)
		@return un identificador de la base de datos que abrió (a título de información)
	*/
	@Override
	public void openDatabase() {
		//debug("openDatabase()");
		if(dir != null && dir.isDirectory()) {
			tables = dir.list(new FilenameFilter() {
				@Override
				public boolean accept(final File dir, final String name) {
					if(name.toLowerCase().endsWith(dataSourceFileExtension)) {
						return (true);
					}
					return(false);
				}
			});
			return;
		}
		return;
	}

	/**
		Cierra la base de datos. Cada implementacion sabrá qué debe hacer
	*/
	@Override
	public void closeDatabase() {
		debug("closeDatabase()");
		// No es necesario hacer nada
	}

	/**
		@return Un arreglo con los nombres de las tablas en la base de datos
	*/
	@Override
	public String[] getTables() {
		//debug(getClass().getName() + " getTables()");
		return(tables);
	}

	/**
		@return Un arreglo con los nombres de los campos de una tabla
	*/
	@Override
	public String [] getFieldNames() {
		return(fieldNames);
	}

	/**
		@return Un arreglo con los tipos de los campos de una tabla
	*/
	@Override
	public int [] getFieldTypes() {
		final int[] fieldTypes = new int[fieldNames.length];
		for(int i = 0; i < fieldNames.length; i++) {
			fieldTypes[i] = Types.VARCHAR;
		}
		return(fieldTypes);
	}

	/**
		@return Un arreglo con los nombres de los tipos de los campos de una tabla
	*/
	@Override
	public String [] getFieldTypeNames() {
		final String[] fieldTypeNames = new String[fieldNames.length];
		for(int i = 0; i < fieldNames.length; i++) {
			fieldTypeNames[i] = "VARCHAR";
		}
		return(fieldTypeNames);
	}

	/** Indica sobre que tabla vamos a trabajar */
	@Override
	public void setSelectedTable(final String table) {
		//debug("setSelectedTable(): " + table);
	}

	/** Indica sobre que campos vamos a trabajar */
	@Override
	public void setSelectedFields(final String[] fields) {
		selectedFields = fields;
		//debug("setSelectedFields(): ");
		//debug(selectedFields);
	}

	/** @return true si estamos en el final de la tabla */
	@Override
	public boolean eof() {
		return(false);
	}

	/** Establece el query a usar para acceder a la base de datos */
	@Override
	public void setQuery(final String query) {
	}

	/** Inicia el proceso de obtencion de datos */
	@Override
	public boolean executeQuery() {
		openTable(tableName);				// Volvemos a ponernos en el inicio del archivo
		return(true);
	}

	/** Devuelve un arreglo con los campos que son clave unica */
	@Override
	public boolean[] getUniqueFields() {
		return(null);
	}

	/**
		Devuelve un arreglo con los nombres de los campos de la tabla que forman la clave primaria o null
		@return Un arreglo con los nombres de los campos de la tabla que forman la clave primaria o null
		@since 2003.09.21 00:24
	*/
	@Override
	public String[] getPrimaryKey() {
		return(null);
	}
	

	/** */
	protected void debug(final String s) {	
		System.out.println(getClass().getName() + " -> " + s);
	}

	/** */
	protected void debug(final int[] array) {
		System.out.print(getClass().getName() + " -> ");
		ArrayTools.printArray(System.out, array);
	}

	/** */
	protected void debug(final Object[] array) {
		System.out.print(getClass().getName() + " -> ");
		ArrayTools.printArray(System.out, array);
	}
}
