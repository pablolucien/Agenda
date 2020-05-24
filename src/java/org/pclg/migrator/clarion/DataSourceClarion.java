// ******************************** package
package org.pclg.migrator.clarion;

// ******************************** imports

import net.asintec.migrator.DataSourceFile;

import java.io.BufferedReader;
import java.sql.Connection;

//import java.util.StringTokenizer;
//import java.util.Date;

/**
 * DataSourceClarion:
 * Provee una fuente de datos para Migrator a partir de un archivo de Clarion
 * La informacion fue tomada de http://www.wotsit.org/search.asp?s=CLARION
 * y está en ClarionFileFormats.zip.
 *
 * @author El Coyote Cojo
 * @version 2002.ene.12 18:15:07, CEST
 */
public final class DataSourceClarion extends DataSourceFile {
    // ********************* Variables de instancia
    /**
     * De aqui leemos los datos.
     */
    private BufferedReader bufferedReader;

    /**
     * Este colega tiene la informacion del registro.
     */
    private ClarionRecord record;

    /**
     * El registro actual.
     */
    private int currRec;

    /**
     * La cantidad de registros.
     */
    private long numrecs;

    /**
     * Constructor por omision: establece datos iniciales.
     */
    public DataSourceClarion() {
        dataSourceFileExtension = ".dat";
    }


    // ********************* Metodos de instancia
    /**
     * Devuelve una descripcion lo que puede hacer esta clase.
     * @return Una descripcion lo que puede hacer esta clase.
     */
    @Override
	public String getDescription() {
        return getClass().getName()
                + "\n\t Provee una fuente de datos para Migrator a partir de "
                + "un archivo de Clarion"
                ;
    }

    /**
     * Abre una tabla de la base de datos.
     *
     * @param tableName El nombre de la tabla que se desea abrir
     *                  ?????????????????????????????????????????????????????????????? Ojo: una cosa es abrir y otra cosa es ir al primer registro
     *                  o ejecutar el query
     */
    @Override
	public void openTable(final String tableName) {
        //debug("openTable(): " + tableName);
        final ClarionFileHeader fileHeader = new ClarionFileHeader(dir + "/" + tableName);
        record = fileHeader.getRecord();
        fieldNames = fileHeader.getFieldNames();
        this.tableName = tableName;
        numrecs = fileHeader.getNumrecs();
        currRec = 0;
    }

    /**
     * Devuelve un arreglo con los nombres de los campos de una tabla.
     * @return Un arreglo con los nombres de los campos de una tabla.
     */
    @Override
	public String[] getFieldNames() {
        return fieldNames;
    }

    /**
     * Devuelve un arreglo con los tipos de los campos de una tabla.
     * @return Un arreglo con los tipos de los campos de una tabla.
     */
    @Override
	public int[] getFieldTypes() {
        return record.getFieldTypes();
    }

    /**
     * Devuelve un arreglo con los nombres de los tipos de los campos de una tabla.
     * @return Un arreglo con los nombres de los tipos de los campos de una tabla.
     */
    @Override
	public String[] getFieldTypeNames() {
        return record.getFieldTypeNames();
    }

    /**
     * Devuelve un arreglo con los tamaños de los campos de la tabla que está abierta.
     *
     * @return Un arreglo con los tamaños de los campos de la tabla que está abierta
     */
    @Override
	public int[] getFieldSizes() {
        return record.getFieldSizes();
    }

    /**
     * Indica sobre que tabla vamos a trabajar.
     * @param table la tabla sobre la que vamos a trabajar.
     */
    @Override
	public void setSelectedTable(final String table) {
        debug("setSelectedTable(): " + table);
    }

    /**
     * Indica sobre que campos vamos a trabajar.
     * @param fields los campos sobre los que vamos a trabajar.
     */
    @Override
	public void setSelectedFields(final String[] fields) {
        selectedFields = fields;
    }

    /**
     * Devuelve el siguente registro o null.
     * @return un registro dentro de un Object[] o null si no hay mas registros.
     */
    @Override
	public Object[] getNextRecord() {
        if (currRec >= numrecs) {
            return null;
        }
        return record.get(++currRec, selectedFields);
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
}
