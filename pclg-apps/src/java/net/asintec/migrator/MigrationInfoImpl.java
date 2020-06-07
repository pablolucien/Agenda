// ******************************** package
package net.asintec.migrator;


// ******************************** imports

import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
	MigrationInfoImpl <BR>
	una implementacion sin interfaz grafica, para migracion automática
	@author El Coyote Cojo
	@version 2002.may.31 13:40:14, CEST
*/
public class MigrationInfoImpl implements MigrationInfo, MigratorConstants {
	// ******************************** Variables de clase

	// ******************************** Variables de instancia
	private String[][] data;

	private String dataSource;
	private String dataSink;
	private String sourceTable;
	private String targetTable;
	private String sourceDatabase;
	private String targetDatabase;

	// ******************************** Constructores

	/**
		Constructor por omision
		@param confFile El archivo de configuracion a utilizar
		@author El Coyote Cojo
		@version 2002.may.31 13:40:14, CEST
	*/
	public MigrationInfoImpl(final String confFile) {
		loadConfiguration(confFile);
	}

	// ******************************** Metodos de instancia


	/**
		esto repite el codigo de Migrator. Ponerlo en un sitio común
	*/
	private void loadConfiguration(final String fileName) {
		BufferedReader in = null;
		final List<String[]> dataList = new ArrayList<>();
		try {
			final File file = new File(fileName);
			in = new BufferedReader(new FileReader(file));
			String linea;
			//myGrid.clear();
			int row = 0;
			while((linea = in.readLine()) != null) {
				//System.out.println(linea);
				if(linea.startsWith("dataSource=")) {
					dataSource = linea.substring(linea.indexOf('=') + 1);
					continue;
				}
				if(linea.startsWith("dataSink=")) {
					dataSink = linea.substring(linea.indexOf('=') + 1);
					continue;
				}
				if(linea.startsWith("sourceDatabase=")) {
					sourceDatabase = linea.substring(linea.indexOf('=') + 1);
					continue;
				}
				if(linea.startsWith("targetDatabase=")) {
					targetDatabase = linea.substring(linea.indexOf('=') + 1);
					continue;
				}
				if(linea.startsWith("sourceTable=")) {
					sourceTable = linea.substring(linea.indexOf('=') + 1);
					continue;
				}
				if(linea.startsWith("targetTable=")) {
					targetTable = linea.substring(linea.indexOf('=') + 1);
					continue;
				}

				if(linea.indexOf(DELIMITER) == -1) {		// Estamos leyendo un archivo de configuracion de la primera version
					/* version 1 */
//					String trgField = linea.substring(0, linea.indexOf(' '));
//					String srcField = linea.substring(linea.indexOf(' ') + 1);
//					myGrid.setValueAt(trgField, row, 0);
//					if(srcField.startsWith(CONSTANTE)) {
//						myGrid.setValueAt(srcField.substring(CONSTANTE.length() + 1, srcField.length() - 1), row, 2);
//						srcField = CONSTANTE;
//					}
//					myGrid.setValueAt(srcField, row, 1);
					row++;
				}
				else {										// Estamos leyendo un archivo de configuracion de la version 2
					/* version 2 (2001.11.15) */
					final org.pclg.xtras.EnhancedStringTokenizer st = new org.pclg.xtras.EnhancedStringTokenizer(linea, DELIMITER, org.pclg.xtras.EnhancedStringTokenizer.NO_CONSECUTIVE_DELIMS);
					int col = 0;
					final String[] datum = new String[5];
					while(st.hasMoreTokens()) {
						datum[col] = st.nextToken();
						col++;
					}
					dataList.add(datum);
				}
			}

			data = new String[dataList.size()][5];
			dataList.toArray(data);
		}
		catch(final IOException ex) {
			ToolBox.showInfo(ex);
		}
		catch(final Exception ex) {
			ToolBox.showInfo(ex);
		}
		finally{
			try {
				if(in != null) {
					in.close();
				}
			}
			catch(final IOException ex) {
				System.err.println(ex);
			}
		}
	}

	//------------ Implementacion de MigrationInfo ------------------

	/**
		Devuelve la cantidad de campos en esta migracion
		@return La cantidad de campos en esta migracion
		@since 2002.05.31
	*/
	@Override
	public int getRowCount() {
		return(data.length);
	}

	/**
		Devuelve el i-esimo campo destino
		@param index El campo que queremos
		@return El i-esimo campo destino
		@since 2002.05.31
	*/
	@Override
	public String getTargetField(final int index) {
		return(data[index][0]);
	}

	/**
		Devuelve el i-esimo campo origen
		@param index El campo que queremos
		@return El i-esimo campo origen
		@since 2002.05.31
	*/
	@Override
	public String getSourceField(final int index) {
		return(data[index][1]);
	}

	/**
		Devuelve el i-esimo valor constante
		@param index El campo que queremos
		@return El i-esimo valor constante
		@since 2002.05.31
	*/
	@Override
	public String getConstantOrAutoIncrementValue(final int index) {
		return(data[index][2]);
	}

	/**
		Devuelve el i-esimo preprocesador
		@param index El campo que queremos
		@return El i-esimo preprocesador
		@since 2002.05.31
	*/
	@Override
	public String getPreprcessorName(final int index) {
		return(data[index][3]);
	}

	/**
		Devuelve Los parámetros del i-esimo preprocesador
		@param index El campo que queremos
		@return Los parámetros del i-esimo preprocesador
		@since 2002.05.31
	*/
	@Override
	public String getPreprcessorParams(final int index) {
		return(data[index][4]);
	}

	public String getSourceTable() {
		return(sourceTable);
	}

	public String getTargetTable() {
		return(targetTable);
	}

	public String getSourceDatabase() {
		return(sourceDatabase);
	}

	public String getTargetDatabase() {
		return(targetDatabase);
	}

	public String getDataSource() {
		return(dataSource);
	}

	public String getDataSink() {
		return(dataSink);
	}
	// ******************************** Metodos estaticos

}
