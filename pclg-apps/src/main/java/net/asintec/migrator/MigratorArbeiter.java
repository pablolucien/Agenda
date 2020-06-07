// ******************************** package
package net.asintec.migrator;

import net.asintec.migrator.preprocessors.Preprocessor;
import org.pclg.migrator.preprocessors.PreprocessorAutoIncrement;
import org.pclg.tools.ArrayTools;
import org.pclg.tools.DateTools;
import org.pclg.tools.ToolBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import static org.pclg.tools.StringTools.EMPTY_STRING_ARRAY;


/**
	MigratorArbeiter
	Esta clase se encarga de hacer la migracion
	@author El Coyote Cojo
	@since 2001.ago.01 14:09:50, CEST
	@version 2002.oct.02
*/
abstract class MigratorArbeiter extends Thread implements MigratorConstants {
	// Variables de clase
	/**
		Controla un poco la cantidad de mensajes que damos 
		@since 2002.oct.02
	*/
	private int debugLevel;
	
	/** Utilizada para evitar que se ejecuten mas de una migracion al mismo tiempo */
	public static boolean migrating;

	// Variables de instancia
	/** La conexion a la base de datos origen */
	DataSource dataSource;

	/** La conexion a la base de datos destino */
	private DataSource dataSink;

	/** La conexion con la base de datos. No es usada aqu�, pero seguramente s� por quien extienda esta clase */
	Connection targetConn;

	/** La tabla origen */
	String sourceTable;

	/** La tabla destino */
	private String targetTable;

	/** Los tipos de datos de los campos origen */
	private int[] tiposOrg;

	/** Para cada campo destino, indica si admite repeticiones */
	boolean[] isUnique;

	/**	La cantidad de campos a insertar */
	int nrFields;

	/**	La cantidad de indices */
	int nrIndexes;

	/** Los tipos de datos de los campos origen solo los que en realidad se van a usar */
	int[] tipos;

	/** La instruccion SELECT a utilizar por la fuente de datos */
	private String selectSentence;

	/** La instruccion INSERT a utilizar */
	String insertSentence;

	/** La instruccion SELECT a utilizar por el destino de datos para saber si tiene que hacer un update o un insert */
	String selectForUpdateSentence;

	/** La instruccion UPDATE a utilizar */
	String updateSentence;

	/** La clausula WHERE a utilizar */
	private String whereClause;

	/** Los nombres de las columnas involucradas en la operacion, porque pueden no ser todas */
	List<String> columnas;

	/** Los preprocesadores de los datos de origen */
	Preprocessor[] preprocesadores;


	/** Una banderita para saber si hubo errores */
	boolean errors;

	/** Posibles formas de decir que si (debr�a ser un Set o algo parecido) */
	private static final Collection<String> afirmaciones = new HashSet<>();

	/** Inicializacion del vector de afirmaciones */
	static {
		afirmaciones.add("true");
		afirmaciones.add("s");
		afirmaciones.add("si");
		afirmaciones.add("s�");
		afirmaciones.add("oui");
		afirmaciones.add("yes");
		afirmaciones.add("ja");
		afirmaciones.add("1");
		afirmaciones.add("sim");
		afirmaciones.add("igen");
		afirmaciones.add("ne");
		afirmaciones.add("bai");
		afirmaciones.add("hai");
	}

	/** Es necesario para dar mensajes y usar callbacks */
	MigratorUserInterface parentComponent;

	/**
		Determina de un modo bastante na�f si un texto es una afirmacion
		@author El Coyote Cojo
		@version 2001.ago.01 14:09:50, CEST
	*/
	public static boolean isAffimation(final String s) {
		if(afirmaciones.contains(s.toLowerCase())) {
			return(true);
		}
		return(false);
	}



	/**
		Hace toda la preparaci�n necesaria para una migraci�n (asignaci�n de variables, construccion de las sentencias SQL, etc.)
		<br>????? este metodo y run deber�an ser synchronized
	*/
	public void migrate(final MigratorUserInterface parentComponent,
			final DataSource sourceDataSource, final DataSource targetDataSource,
			final String sourceTable, final String targetTable,
			final int[] tiposOrg, final boolean[] isUnique,
			final MigrationInfo migrationInfo) {
		this.parentComponent = parentComponent;
//Ojo: revisar el comportamiento de esto cuando es una migracion en batch (aunque ahora los batch los deber�a hacer MigratorDaemon)
		if(migrating) {
			parentComponent.showInfo("Hay una migracion en proceso");
			return;
		}

		migrating = true;
		this.dataSource = sourceDataSource;
		this.dataSink = targetDataSource;
		this.targetConn = targetDataSource.getConnection();
		this.sourceTable = sourceTable;
		this.targetTable = targetTable;
		this.tiposOrg = tiposOrg;				// tiposOrg es los tipos de datos de la tabla destino, usado para los insert
//		debug(this.tiposOrg);
//		this.tiposOrg = sourceDataSource.getFieldTypes(targetTable);
//		debug(this.tiposOrg);
		// FIXME: los campos unicos que se toman en cuenta, son los de la tabla
		// destino, pero Migrator no los crea unicos, de modo que tiene que
		// hacerse a mano cuando se hace una duplicaci�n
		this.isUnique = isUnique;

		errors = false;		// En principio todo debe ir como una seda
		nrFields = 0;		// cantidad de campos a insertar y contador del campo actual, porque los campos vacios nos los saltamos
		nrIndexes = 0;
		tipos = new int[this.tiposOrg.length];
		selectSentence = "SELECT ";

		// 2002.05.29 Access permite espacios el los nombres 
		if(targetTable.indexOf(' ') == -1) {
			insertSentence = "INSERT INTO " + targetTable + " (";
			updateSentence = "UPDATE " + targetTable + " SET ";
			selectForUpdateSentence = "SELECT 1 FROM " + targetTable;
		}
		else {
			insertSentence = "INSERT INTO \"" + targetTable + "\" (";
			updateSentence = "UPDATE \"" + targetTable + "\" SET ";
			selectForUpdateSentence = "SELECT 1 FROM \"" + targetTable + "\"";
		}

		whereClause    = " WHERE ";

		// Obtener los nombres de las columnas involucradas en la operacion, porque pueden no ser todas
		columnas = new Vector();

		// los nombres de los campos origen
		final Vector sourceFields = new Vector();

		// los famosos preprocesadores
		preprocesadores = new Preprocessor[migrationInfo.getRowCount()];

		// Armamos las sentencias SQL
		for(int i = 0; i < migrationInfo.getRowCount(); i++) {
			final String trgField = migrationInfo.getTargetField(i);
			String srcField = migrationInfo.getSourceField(i);

			if(srcField == null || srcField.length() == 0 || srcField.equals(VACIO))
			{
				continue;
			}

			columnas.add(trgField);
			if (srcField.equals(CONSTANTE)) {
				final String constantValue =
					migrationInfo.getConstantOrAutoIncrementValue(i);
				srcField += "{" + constantValue + "}";
				selectSentence += "'" + constantValue + "'";
				sourceFields.add("'" + constantValue + "'");
			} else if(srcField.equals(AUTO)) {   //FIXME:
				final String autoIncrementValue =
					migrationInfo.getConstantOrAutoIncrementValue(i);
				selectSentence += "'" + autoIncrementValue + "'";
				sourceFields.add("'" + autoIncrementValue + "'");
			} else {
				// 2002.05.20 Access permite espacios el los nombres y tambien permite el gui�n
				if(sourceTable.indexOf(' ') == -1 && sourceTable.indexOf('-') == -1) {
					selectSentence += srcField;
				}
				else {
					selectSentence += "\"" + srcField + "\"";
				}
				sourceFields.add(srcField);
			}
			selectSentence += ", ";
			insertSentence += trgField + ", ";
			if(!isUnique[i]) {		// Si no es clave primaria, es posible el UPDATE
				updateSentence += trgField + " = ?, ";
			}
			else {						// Si s� lo es, va en el WHERE
				whereClause += trgField + " = ? AND ";
				nrIndexes++;
			}
			tipos[nrFields] = this.tiposOrg[i];
// FIXME: Lo de los tipos hay que arreglarlo porque no funciona si los
//campos de la migracion est�n en un �rden distinto a la bd. por
//ejemplo: C:\home\development\modules\src\java\org\pclg\migrator\MySQL-MySQL_Cifrar_contactos.conf
			// Agregamos los preprocesadores donde haga falta
			final String preprocessorName = migrationInfo.getPreprcessorName(i);
			final String preprocessorParam = migrationInfo.getPreprcessorParams(i);
			preprocesadores[nrFields] = null;
			if(srcField.equals(AUTO)) {	// FIXME: Esto se puede eliminar ? Agregamos un preprocesador para gestionar esto. FIXME:
				preprocesadores[nrFields] = new PreprocessorAutoIncrement();
				preprocesadores[nrFields].setParameters(
					migrationInfo.getConstantOrAutoIncrementValue(i));
			} else
			if (preprocessorName != null && !preprocessorName.equals("")) {
				try {
					final Preprocessor preprocessor = (Preprocessor) Class.forName(preprocessorName).newInstance();		// ??? aqui deber�a haber una factory
					if(preprocessorParam != null && !preprocessorParam.equals("")) {
						preprocessor.setParameters(preprocessorParam);
					}
					preprocesadores[nrFields] = preprocessor;
				}
				catch(final ClassNotFoundException ex) {
					parentComponent.showAlert("No existe la clase " + preprocessorName);
					ToolBox.showInfo(ex);
				}
				catch(final InstantiationException ex) {
					parentComponent.showAlert("No puedo crear un " + preprocessorName);
					ToolBox.showInfo(ex);
				}
				catch(final IllegalAccessException ex) {
					parentComponent.showAlert("No existe la clase " + preprocessorName);
					ToolBox.showInfo(ex);
				}
			}

			debug(trgField + " " + srcField + " preprocesador = " + preprocessorName);
			nrFields++;
		}

		sourceDataSource.setSelectedFields((String[]) sourceFields.toArray(EMPTY_STRING_ARRAY));

		insertSentence = insertSentence.substring(0, insertSentence.length() - 2);	// Eliminar una ',' que	me sobra.
		insertSentence += ") VALUES (";
		for(int i = 0; i < nrFields; i++) {
			insertSentence += "?, ";
		}
		insertSentence = insertSentence.substring(0, insertSentence.length() - 2);	// Eliminar una ',' que	me sobra.
		insertSentence += ") ";

		updateSentence = updateSentence.substring(0, updateSentence.length() - 2);	// Eliminar una ',' que	me sobra.
		if(whereClause.endsWith(" AND ")) {
			whereClause = whereClause.substring(0, whereClause.length() - 5);		// Eliminar un ' AND ' que	me sobra.
		}
		else {
			whereClause = " WHERE 1 = 0 ";											// Mo hay claves unicas, luego quiero que el select siempre falle
		}
		updateSentence += whereClause;
		selectForUpdateSentence += whereClause;

		selectSentence = selectSentence.substring(0, selectSentence.length() - 2);	// Eliminar una ',' que	me sobra.

		// 2002.05.20 Access permite espacios el los nombres 
		if(sourceTable.indexOf(' ') == -1) {
			selectSentence += " FROM " + sourceTable;
		}
		else {
			selectSentence += " FROM [" + sourceTable + "]";
		}

		if(sourceTable.startsWith(DatabaseSelector.QUERY_AD_HOC)) {
			selectSentence = sourceTable.substring(DatabaseSelector.QUERY_AD_HOC.length());
		}

		debug(selectSentence);
		sourceDataSource.setQuery(selectSentence);
		debug(insertSentence);
		debug(updateSentence);
		debug(selectForUpdateSentence);

		// Ahora que est� todo armado, comenzamos la migraci�n
		start();
	}


	/** Este es el metodo que hay que implementar para hacer la migracion */
	public abstract void run();

	/** Este es el metodo que hay que implementar para abortar una migracion */
	public abstract void abort();

	/** informa de lo que es capaz de hacer este se�or */
	public abstract String getDescription();


	/**
		Rellena los datos de un PreparedStatement
	*/
	void fillStatement(final PreparedStatement pstmt, final int index,
		final Object data, final int tipo, final String columna)
										throws SQLException, IOException, ParseException {
		debug("fillStatement(): Preparando " + index + " " + columna + " = " + data);

		if(data == null) {
			//logError("Valor nulo en la posici�n " + index + " (" + columna + ")");
			pstmt.setNull(index, tipo);
			return;
		}

		switch(tipo) {
		case Types.BIT:
		case Types.BOOLEAN:
			debug("Types.BIT");
			pstmt.setBoolean(index, isAffimation((String) data));
			break;
		case Types.TINYINT:
			debug("Types.TINYINT");
			try {
				pstmt.setByte(index, Byte.parseByte(String.valueOf(data)));
			}
			catch(final NumberFormatException ex) {
				logError("Types.TINYINT: el valor <" + (String)data + "> es incorrecto (" + columna + ")");
				pstmt.setNull(index, Types.TINYINT);
			}
			break;
		case Types.SMALLINT:
			debug("Types.SMALLINT");
			try {
				pstmt.setShort(index, Short.parseShort((String)data));
			}
			catch(final NumberFormatException ex) {
				logError("Types.SMALLINT: el valor <" + (String)data + "> es incorrecto (" + columna + ")");
				pstmt.setNull(index, Types.SMALLINT);
			}
			break;
		case Types.INTEGER:
			debug("Types.INTEGER");
			try {
				pstmt.setInt(index, Integer.parseInt((String)data));
			}
			catch(final NumberFormatException ex) {
				logError("Types.INTEGER: el valor <" + (String)data + "> es incorrecto (" + columna + ")");
				pstmt.setNull(index, Types.INTEGER);
			}
			break;
		case Types.BIGINT:
			debug("Types.BIGINT");
			try {
				pstmt.setLong(index, Long.parseLong((String)data));
			}
			catch(final NumberFormatException ex) {
				logError("Types.BIGINT: el valor <" + (String)data + "> es incorrecto (" + columna + ")");
				pstmt.setNull(index, Types.BIGINT);
			}
			break;
		case Types.REAL:
			debug("Types.REAL");
			try {
				//pstmt.setFloat(index, Float.parseFloat((String)data));
				pstmt.setDouble(index, Float.parseFloat((String)data));
			}
			catch(final NumberFormatException ex) {
				logError("Types.REAL: el valor <" + (String)data + "> es incorrecto (" + columna + ")");
				pstmt.setNull(index, Types.REAL);
			}
			break;
		case Types.NUMERIC:										// ??? Oracle
		case Types.DOUBLE:
			debug("Types.DOUBLE");
			try {
				pstmt.setDouble(index, Double.parseDouble((String)data));
			}
			catch(final NumberFormatException ex) {
				logError("Types.DOUBLE: el valor <" + (String)data + "> es incorrecto (" + columna + ")");
				pstmt.setNull(index, Types.DOUBLE);
			}
			break;
		case Types.DATE:
			debug("Types.DATE");
			//break;
		case Types.TIME:
			debug("Types.TIME");
			//break;
		case Types.TIMESTAMP:
			debug("Types.TIMESTAMP");
			if(data instanceof Date) {
				//pstmt.setDate(index, new java.sql.Date(((java.util.Date)data).getTime()));
				pstmt.setTimestamp(index, new Timestamp(((Date)data).getTime()));
			}
			else if(data == null || data.equals("")) {
				pstmt.setNull(index, Types.TIMESTAMP);
			}
			else {
				try {
					//pstmt.setDate(index, new java.sql.Date(ToolBox.parseDate((String)data).getTime()));
					pstmt.setTimestamp(index, new Timestamp(
							DateTools.parseDate((String) data).getTime()));
				}
				catch (final ParseException ex) {
					pstmt.setNull(index, Types.TIMESTAMP);
					ToolBox.showInfo(ex);
				}
			}
			break;
		case Types.NULL:
			debug("Types.NULL");
			pstmt.setNull(index, Types.NULL);
			break;
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
		case Types.CHAR:
		case Types.CLOB:
			final String s = (String) data;
			debug("Types.TEXTO: [" + s + "]");
//System.err.println("Types.TEXTO: [" + s + "]");
			if(s.length() > 0) {							// sigue dando problemas el campo memo
				pstmt.setObject(index, s, Types.LONGVARCHAR);
			}
			else {
				pstmt.setNull(index, tipo);
			}
			break;
		case Types.BLOB:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			if(data != null) {
				final byte[] newData = (byte[]) data;
				pstmt.setBinaryStream(index, new ByteArrayInputStream(newData), newData.length);
			}
			else {
				pstmt.setNull(index, Types.VARBINARY);
			}
			break;
		default:
			throw new RuntimeException("Tipo de dato desconocido: " + tipo + " en " + columna);
		}
	}


	/**
		Controla un poco la cantidad de mensajes que damos 
		@since 2002.oct.02
	*/
	public void setDebugLevel(final int level) {
		debugLevel = level;
	}
	
	/** Muestra un mensaje dependiendo del nivel de debug en que estemos */
	void debug(final String s) {
		if(debugLevel > 1) {
			System.err.println(s);
		}
	}

	/** Muestra un array de int dependiendo del nivel de debug en que estemos */
	protected void debug(final int[] array) {
		if(debugLevel > 0) {
			ArrayTools.printArray(System.err, array);
		}
	}


	/** Muestra un mensaje de error */
	void logError(final String s) {
		System.err.println(s);
	}
}
