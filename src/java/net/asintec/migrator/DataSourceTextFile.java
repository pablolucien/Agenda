// ********************* package
package net.asintec.migrator;


// ********************* imports

import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
	DataSource
	Provee una fuente de datos para Migrator a partir de un archivo de texto

	@author El Coyote Cojo
	@version 2001.ago.01 17:52:07, CEST
*/
public class DataSourceTextFile extends DataSourceFile {
	/**
		En realidad, sólo una estructura para almacenar la definicion del campo
	*/
	class FieldDesc {
		/** El nombre del campo */
		private final String name;

		/** El inicio del campo */
		private final int start;

		/** El final del campo */
		private final int end;

		/** El tipo del campo */
		private int type;
		
		/**
			--author El Coyote Cojo
			@since 2002.06.06
			@throws IllegalArgumentException si el tipo no es correcto
		*/
		FieldDesc(final String name, final int start, final int end, final String type) {
			this.name = name;
			this.start = start;
			this.end = end;
			if(type.equalsIgnoreCase("Integer")) {
				this.type = 1;
			}
			else if(type.equalsIgnoreCase("Text")) {
				this.type = 2;
			}
			else if(type.equalsIgnoreCase("DateTime")) {
				this.type = 3;
			}
			else if(type.equalsIgnoreCase("Double")) {
				this.type = 4;
			}
			else if(type.equalsIgnoreCase("Boolean")) {
				this.type = 5;
			}
			else {
				throw new IllegalArgumentException("Tipo desconocido: " + type);
			}
		}

		public String toString() {
			return(getClass().getName() + ": name = " + name + ", start = " + start + ", end = " + end + ", type = " + type);
		}
	}

	// ********************* Variables de instancia
	/** De aqui leemos los datos */
	private BufferedReader bufferedReader;

	/** Las posiciones de inicio de cada campo (basado en 1 para hacerle el trabajo más facil al configurador) */
	private int[] fieldStarts;
	
	/** Las posiciones de fin de cada campo (basado en 1 para hacerle el trabajo más facil al configurador) */
	private int[] fieldEnds;


	/** Constructor por omision: establece datos iniciales */
	public DataSourceTextFile() {
		dataSourceFileExtension = ".struct";
	}
	
	// ********************* Metodos de instancia
	/**
		@return Una descripcion lo que puede hacer esta clase
	*/
	@Override
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t Provee una fuente de datos para Migrator a partir de un archivo de texto"
									;
		return(description);
	}

	/**
		Cierra la base de datos.
	*/
	@Override
	public void closeDatabase() {
		//debug("closeDatabase()");
		try {
			if(bufferedReader != null) {
				bufferedReader.close();
				bufferedReader = null;
			}
		}
		catch(final IOException ex) {
			ToolBox.showInfo(ex);
		}
	}

	// la definicion de los campos está en el fichero struct
	public void openTable(final String tableName) {
		//debug("openTable(): " + tableName);
		this.tableName = tableName;
		String dataFileName = null;
		try {
			if(bufferedReader != null) {
				bufferedReader.close();
				bufferedReader = null;
			}
			bufferedReader = new BufferedReader(new FileReader(new File(dir, tableName)));
//bufferedReader = new BufferedReader(new FileReader(new File("C:/migrator/test/tickets.struct")));
			String line;
			final List<FieldDesc> fields = new ArrayList<>();
			while((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				//debug(line);
				if(line.startsWith("#") || line.equals("")) {
					continue;
				}

				if(line.startsWith("FileName=")) {
					//debug("FileName=" + line.substring(line.indexOf('=') + 1));
					//this.tableName = line.substring(line.indexOf('=') + 1);
					dataFileName = line.substring(line.indexOf('=') + 1);
				}
				else if(line.startsWith("FileType=")) {
					//debug("FileType=" + line.substring(line.indexOf('=') + 1));
				}
				else if(line.startsWith("FilePath=")) {
					//debug("FilePath=" + line.substring(line.indexOf('=') + 1));
				}
				else {
					final StringTokenizer st = new StringTokenizer(line, " \t");
					final FieldDesc fieldDesc = new FieldDesc(st.nextToken().trim(), Integer.parseInt(st.nextToken().trim()),
												Integer.parseInt(st.nextToken().trim()), st.nextToken().trim());
					fields.add(fieldDesc);
					//debug(fieldDesc.toString());
				}
			}
			bufferedReader.close();
			if(dataFileName == null) {
				throw new RuntimeException("Error en el archivo: " + tableName);
			}
			//bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dir, this.tableName)), "cp850"));	// Este lee las Ñ's, etc.
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dir, dataFileName)), "cp850"));	// Este lee las Ñ's, etc.

			final int fieldCount = fields.size();
			fieldNames = new String[fieldCount];
			fieldStarts = new int[fieldCount];
			fieldEnds = new int[fieldCount];
			for(int i = 0; i < fieldNames.length; i++) {
				final FieldDesc fieldDesc = fields.get(i);
				fieldNames[i] = ToolBox.normalizeName(fieldDesc.name);
				fieldStarts[i] = fieldDesc.start - 1;
				fieldEnds[i] = fieldDesc.end;
			}
//			debug(fieldStarts);
//			debug(fieldEnds);
//			debugFields();

		}
		catch(final IOException ex) {
			ToolBox.showInfo(ex);
		}
/*
		finally {
			try {
				if(bufferedReader != null) {
					bufferedReader.close();
					bufferedReader = null;
				}
			}
			catch(IOException ex) {
				ToolBox.showInfo(ex);
			}
		}
*/
	}

	/**
		Abre una tabla de la base de datos
		@param tableName El nombre de la tabla que se desea abrir
	*/
	public void old_openTable(final String tableName) {
		debug("old_openTable(): " + tableName);
		this.tableName = tableName;
		try {
			if(bufferedReader != null) {
				bufferedReader.close();
				bufferedReader = null;
			}
			//bufferedReader = new BufferedReader(new FileReader(new File(dir, tableName)));
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dir, tableName)), "cp850"));	// Este lee las Ñ's, etc.
			final String line1 = bufferedReader.readLine();										// La primera linea debe tener los nombres de los campos
			final String line2 = bufferedReader.readLine();										// La segunda linea debe tener los tamaños de los campos
			//debug(line1);
			//debug(line2);
			final StringTokenizer stringTokenizer1 = new StringTokenizer(line1);
			final StringTokenizer stringTokenizer2 = new StringTokenizer(line2);
			final int fieldCount = stringTokenizer1.countTokens();
			fieldNames = new String[fieldCount];
			fieldStarts = new int[fieldCount];
			fieldEnds = new int[fieldCount];
			for(int i = 0; i < fieldNames.length; i++) {
				fieldNames[i] = ToolBox.normalizeName(stringTokenizer1.nextToken());
				final String size;
				size = stringTokenizer2.nextToken();
				fieldStarts[i] = Integer.parseInt(size.substring(0, size.indexOf('-')).trim()) - 1;
				fieldEnds[i] = Integer.parseInt(size.substring(size.indexOf('-') + 1).trim());
			}
			//debug(fieldStarts);
			//debug(fieldEnds);
			//debugFields();
		}
		catch(final IOException ex) {
			ToolBox.showInfo(ex);
		}
	}

	/**
		@return Un arreglo con los tipos de los campos de una tabla
	*/
	public int[] getFieldTypes() {
		final int[] fieldTypes = new int[fieldNames.length];
		for(int i = 0; i < fieldNames.length; i++) {
			fieldTypes[i] = Types.VARCHAR;
		}
		return(fieldTypes);
	}

	/**
		@return Un arreglo con los nombres de los tipos de los campos de una tabla
	*/
	public String[] getFieldTypeNames() {
		final String[] fieldTypeNames = new String[fieldNames.length];
		for(int i = 0; i < fieldNames.length; i++) {
			fieldTypeNames[i] = "VARCHAR";
		}
		return(fieldTypeNames);
	}

	/**
		Devuelve un arreglo con los tamaños de los campos de la tabla que está abierta
		@return Un arreglo con los tamaños de los campos de la tabla que está abierta
	*/
	public int[] getFieldSizes() {
		final int[] fieldSizes = new int[fieldNames.length];
		for(int i = 0; i < fieldNames.length; i++) {
			fieldSizes[i] = fieldEnds[i] - fieldStarts[i] + 1;
		}
		return(fieldSizes);
	}

//int kkkBorrame = 0;

	/** Devuelve el siguente registro o null */
	public Object[] getNextRecord() {

//if(kkkBorrame++ > 10) { kkkBorrame = 0; return(null); }

		final Object[] data = new Object[selectedFields.length];
//		debug("getNextRecord()");
		try {
			final String dataRecord = bufferedReader.readLine();
			if(dataRecord == null) {
				return(null);
			}

			for(int i = 0; i < selectedFields.length; i++) {
				// Buscamos la posicion del campo real de la 'base de datos'
				boolean found = false;
				for(int j = 0; j < fieldNames.length; j++) {
					if(selectedFields[i].equals(fieldNames[j])) {
						data[i] = dataRecord.substring(fieldStarts[j], fieldEnds[j]);
						found = true;
						break;
					}
				}
				// si no es un campo real, debe ser una constante
				if(!found) {
					// Segun como funciona el Arbeiter, la constante viene entre comillas simples, que hay que eliminar
					data[i] = selectedFields[i].substring(1, selectedFields[i].length() - 1);
				}
			}
//			debug(selectedFields);
//			debug(data);
			return(data);
		}
		catch(final IOException ex) {
			ToolBox.showInfo(ex);
			return(null);
		}
	}

	/** @return true si estamos en el final de la tabla */
	public boolean eof() {
		return(false);
	}

	@Override
	public Connection getConnection() {
		return null;
	}

	@Override
	public void setConnection(final Connection connection) {
	}

	@Override
	public boolean addRecord(final Object[] record) {
		throw new UnsupportedOperationException();
	}

	/**
		Imprime los campos con sus posiciones
		<BR>
		<b>
		Ojo: lee un registro, por lo que en la migración faltará si se llama a este método
		<BR>
		</b>
	*/
	private void debugFields() {
		final String[] old_selectedFields = selectedFields;	// Para dejar todo como estaba
		selectedFields = fieldNames;					// Seleccionamos todos los campos
		final Object[] data = getNextRecord();				// y leemos un registro
		for(int i = 0; i < fieldNames.length; i++) {
			System.out.println(ToolBox.pad(fieldNames[i], 20) + fieldStarts[i] + "-" + fieldEnds[i] + " [" + data[i] + "]");
		}
		selectedFields = old_selectedFields;			// Restauramos la configuracion
	}
}
