package org.pclg.agenda.jdbc;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.agenda.Agenda;
import org.pclg.agenda.AgendaDb;
import org.pclg.agenda.AgendaDbException;
import org.pclg.agenda.AgendaDbFactory;
import org.pclg.agenda.AgendaLogin;
import org.pclg.agenda.AgendaUtil;
import org.pclg.agenda.PropertiesManager;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.Grupo;
import org.pclg.agenda.entities.Pais;
import org.pclg.agenda.entities.TipoTelefono;
import org.pclg.agenda.plugins.Plugin;
import org.pclg.dbutil.DbManager;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ChangeObserver;
import org.pclg.tools.ObservableProperties;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.ToolBox;
import org.pclg.xtras.DriverPropertiesPanel;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.pclg.tools.PropertiesHelper.getStringFromProperties;
import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * Clase encargada de la interfaz con la base de datos.
 *
 * @author El Coyote
 * @since 11-sep-2007 10:42:11
 */
@SuppressWarnings(
	{"FeatureEnvy", "ClassWithTooManyMethods", "OverlyComplexClass"})
public final class AgendaDbJDBC implements AgendaDb, ChangeObserver<ObservableProperties> {
    /** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	// Si fuera posible eliminar estas dependencias de derby :(
	private static final String DERBY_SHUTDOWN_SENTENCE = "jdbc:derby:;shutdown=true";

	private static final List<AgendaRecord> NULL_LIST = Collections.emptyList();

	private static final char RIGHT_PARENTHESIS = ')';

    /** Lista de los campos de la tabla contacto. */
	private static final String CONTACTO_FIELDS_LIST =
		"CLAVE, VERSION, NOMBRE, APELLIDO, SEXO, PAIS, "
            + "VersionTelefono, VersionDireccion, VersionEmail, VersionGrupo, "
            + "VersionImagen, DIA, MES, ANO, "
            + "MARCA, FechaActualizacion, FechaCreacion, LISTAR, VersionNotas, DELETED ";

	/** Sentencia de selecci�n de registros en la tabla sin condiciones ni orden. */
	static final String BASE_SELECT_RECORDS_SENTENCE =
		"SELECT " + CONTACTO_FIELDS_LIST + "FROM ROOT.CONTACTO CTOS ";

    private static final String GROUP_BY_CLAUSE =
        " GROUP BY " + CONTACTO_FIELDS_LIST;

    /** Cl�usula having para seleccionar la �ltima version de cada registro. */
	private static final String BASE_HAVING_CLAUSE =
        " HAVING VERSION = (SELECT MAX(VERSION) FROM ROOT.CONTACTO CTOS2 "
            + " WHERE CTOS2.CLAVE = CTOS.CLAVE) ";

	private static final String ORDER_BY_NAME_CLAUSE = " ORDER BY NOMBRE, APELLIDO ";
	private static final String ORDER_BY_SPECIAL_CLAUSE = " ORDER BY MARCA ";
	private static final String ORDER_BY_DATE_CLAUSE = " ORDER BY MES, DIA, ANO ";

	/** Sentencia para listarlos buscando el que tenga mayor numero de version. */
	private static final String SELECT_PREVAILING_RECORD_SENTENCE =
		BASE_SELECT_RECORDS_SENTENCE
            + "WHERE NOT Deleted "
            + GROUP_BY_CLAUSE
            + BASE_HAVING_CLAUSE;

	/** Sentencia para listarlos buscando el que tenga mayor numero de version,
	 * pero s�lo los marcados. */
	private static final String SELECT_PREVAILING_SPECIAL_RECORD_SENTENCE =
		BASE_SELECT_RECORDS_SENTENCE
            + "WHERE marca IS NOT NULL AND marca > ''"
            + GROUP_BY_CLAUSE
            + BASE_HAVING_CLAUSE;

	/** Sentencia para listarlos buscando el que tenga mayor numero de version,
	 * pero s�lo los borrados. */
	private static final String SELECT_PREVAILING_DELETED_RECORD_SENTENCE =
		BASE_SELECT_RECORDS_SENTENCE
    		+ "WHERE Deleted"
            + GROUP_BY_CLAUSE
	    	+ BASE_HAVING_CLAUSE;

	/** Sentencia para listarlos buscando el que tenga mayor n�mero de version,
	 * pero s�lo los que tienen cumplea�os. */
	private static final String SELECT_PREVAILING_BY_DATE_RECORD_SENTENCE =
		BASE_SELECT_RECORDS_SENTENCE
		    + "WHERE NOT Deleted AND dia > 0 AND mes > 0 "
            + GROUP_BY_CLAUSE
            + BASE_HAVING_CLAUSE
            + ORDER_BY_DATE_CLAUSE;

	/** Sentencia para listarlos buscando el que tenga mayor numero de version,
	 * pero s�lo los que tienen cumplea�os cercanos cuando el rango de fechas est�
	 * todo en el mismo mes.
	 */
	private static final String SELECT_PREVAILING_BY_NEAR_DATE_SAME_MONTH_RECORD_SENTENCE =
		BASE_SELECT_RECORDS_SENTENCE
    		+ "WHERE NOT Deleted AND dia >= ? AND dia <= ? AND mes = ? "
            + GROUP_BY_CLAUSE
            + BASE_HAVING_CLAUSE
            + ORDER_BY_DATE_CLAUSE;

	/** Sentencia para listarlos buscando el que tenga mayor numero de version,
	 * pero s�lo los que tienen cumplea�os cercanos cuando el rango de fechas 
	 * abarca diferentes meses pero est�n en el mismo a�o (M0 < M1). */
	private static final String SELECT_PREVAILING_BY_NEAR_DATE_DIFF_MONTH_SAME_YEAR_RECORD_SENTENCE =
		BASE_SELECT_RECORDS_SENTENCE
	    	+ "WHERE NOT Deleted AND dia > 0 AND mes > 0 AND ((dia >= ? AND mes = ?) OR (dia <= ? AND mes = ?) OR (mes > ? AND mes < ?)) "
            + GROUP_BY_CLAUSE
            + BASE_HAVING_CLAUSE
            + ORDER_BY_DATE_CLAUSE;

	/** Sentencia para listarlos buscando el que tenga mayor numero de version,
	 * pero s�lo los que tienen cumplea�os cercanos cuando el rango de fechas 
	 * abarca diferentes meses pero est�n en diferentes a�os (M1 < M0). */
	private static final String SELECT_PREVAILING_BY_NEAR_DATE_DIFF_MONTH_DIFF_YEAR_RECORD_SENTENCE =
		BASE_SELECT_RECORDS_SENTENCE
	    	+ "WHERE NOT Deleted AND dia > 0 AND mes > 0 AND ((dia >= ? AND mes = ?) OR (dia <= ? AND mes = ?) OR (mes > ? OR mes < ?)) "
            + GROUP_BY_CLAUSE
            + BASE_HAVING_CLAUSE
            + ORDER_BY_DATE_CLAUSE;

	/** Sentencia para listarlos buscando el que tenga mayor numero de version,
	 * pero s�lo los que han sido creados o modificados a partir de cierta fecha. */
	private static final String SELECT_PREVAILING_RECENTLY_MODIFIED_RECORDS_SENTENCE =
		BASE_SELECT_RECORDS_SENTENCE
            + ", "
            + "(select distinct CAST(FechaActualizacion AS DATE) Fecha from root.CONTACTO where FechaActualizacion is not null "
            + "union "
            + "select distinct CAST(FechaCreacion AS DATE) Fecha from root.CONTACTO where FechaCreacion is not null "
            + "order by 1 desc fetch first ? rows only) T2 "
            + "WHERE NOT Deleted AND CAST(FechaCreacion AS DATE) in (T2.Fecha) or CAST(FechaActualizacion AS DATE) in (T2.Fecha) "
            + GROUP_BY_CLAUSE + ", T2.Fecha"
    		+ BASE_HAVING_CLAUSE
		    + "ORDER BY T2.Fecha DESC";

	/** Sentencia para listarlos seg�n un filtro. */
	private static final String SELECT_FILTERED_RECORDS_SENTENCE =
		BASE_SELECT_RECORDS_SENTENCE
			+ "WHERE NOT Deleted AND ("
			+ "Nombre LIKE ? OR Apellido LIKE ?  "
			+ "OR EXISTS (SELECT * FROM root.Email E WHERE Email LIKE ? AND CTOS.Clave = E.Clave AND CTOS.VersionEmail = E.Version) "
			+ "OR EXISTS (SELECT * FROM root.DIRECCION D WHERE Direccion LIKE ? AND CTOS.Clave = D.Clave AND CTOS.VersionDireccion = D.Version) "
			+ "OR EXISTS (SELECT * FROM root.Nota N WHERE Nota LIKE ? AND CTOS.Clave = N.Clave AND CTOS.VersionNotas = N.Version) "
			+ "OR EXISTS (SELECT * FROM root.Telefono T WHERE Numero LIKE ? AND CTOS.Clave = T.Clave AND CTOS.VersionTelefono = T.Version) "
			+ ") "
            + GROUP_BY_CLAUSE
            + BASE_HAVING_CLAUSE
            + ORDER_BY_NAME_CLAUSE;

	/** Sentencia para seleccionar un registro en particular. */
	private static final String SELECT_ONE_CONTACT_SENTENCE =
		BASE_SELECT_RECORDS_SENTENCE
    		+ " WHERE CLAVE = ? AND VERSION = ?";

	/** Sentencia para insertar un contacto. */
	private static final String INSERT_SENTENCE = "INSERT INTO ROOT.CONTACTO ("
		+ CONTACTO_FIELDS_LIST
		+ ") VALUES "
		+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String DELETE_SENTENCE =
		"UPDATE ROOT.CONTACTO SET Deleted = ? WHERE Clave = ?";

    private Connection dbConnection;
	private Properties appProperties;
	private int daysFork4Birthdays;
	private int daysFork4RecentlyModified;
	private int maxBackupHistory;
	private int daysFork4InterestingDates;
    private GroupHelper groupHelper;
    private TelephoneHelper telephoneHelper;
    private CountryHelper countryHelper;
    private AddressHelper addressHelper;
    private ImageHelper imageHelper;
    private GeneralHelper generalHelper;
    private EmailHelper emailHelper;
    private NoteHelper noteHelper;
    private DbEngine dbEngine;

    @Override
	public void initDb(final Properties appProperties, final boolean checkTables,
            final boolean forceCreateTables)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		this.appProperties = appProperties;
		if (appProperties instanceof ObservableProperties) {
			((ObservableProperties) appProperties).addChangeObserver(this);
		}
		final String password = appProperties.getProperty("AgendaDb.password");
		final String originalConnectString = appProperties.getProperty("AgendaDb.resolvedUrl");
		final String connectString = originalConnectString.replace(Agenda.PWD_PLACEHOLDER, password);
		final DbManager dbManager = new DbManager("AgendaDb");
		dbConnection = dbManager.getConnection(appProperties.getProperty("AgendaDb.driver"),
				connectString, appProperties.getProperty("AgendaDb.user"), password);
		LOGGER.log(Level.INFO, "Connected to database: " + originalConnectString);
		dbConnection.setAutoCommit(false);
        initializeMutableProperties();
        generalHelper = new GeneralHelper(dbConnection);
        groupHelper = new GroupHelper(dbConnection, generalHelper);
        telephoneHelper = new TelephoneHelper(dbConnection, generalHelper);
        countryHelper = new CountryHelper(dbConnection);
        addressHelper = new AddressHelper(dbConnection, generalHelper);
        imageHelper = new ImageHelper(dbConnection, generalHelper);
        emailHelper = new EmailHelper(dbConnection, generalHelper);
        noteHelper = new NoteHelper(dbConnection, generalHelper);
        dbEngine = new DbEngine(telephoneHelper, noteHelper, groupHelper, addressHelper, imageHelper, emailHelper);

        if (forceCreateTables) {
			dbManager.createTables(appProperties);	// Crea las tablas aunque existan.
        } else if (checkTables) {
			dbManager.checkAndCreateTables(appProperties);	// Las crea si no existen.
      }
	}

	/** Inicializa ciertas variables a partir de las properties. */
	private void initializeMutableProperties() {
		daysFork4Birthdays = initializeNonNegativeIntFromProperties(
            "AgendaDb.daysFork4Birthdays", 7);
		daysFork4InterestingDates = initializeNonNegativeIntFromProperties(
            "AgendaDb.daysFork4InterestingDates", 10);
		daysFork4RecentlyModified = initializeNonNegativeIntFromProperties(
            "AgendaDb.daysFork4RecentlyModified", 1);
        maxBackupHistory = initializeNonNegativeIntFromProperties(
            "AgendaDb.maxBackupHistory", 5);
	}

	/**
	 * Utility method to get a positive integer value from the properties of
	 * the application.
	 *
	 * @param key the key to search in the properties.
	 * @param defaultValue a default value to return if the key is not found.
	 *
	 * @return a positive integer value from the properties or a default value
	 * if the key's not found.
	 */
	private int initializeNonNegativeIntFromProperties(final String key, final int defaultValue) {
        final String value = appProperties.getProperty(key, String.valueOf(defaultValue));
        final int retVal = ToolBox.isInteger(value) ? Integer.parseInt(value) : defaultValue;
        return retVal < 0 ? defaultValue : retVal;
	}

	/**
     * This method is called whenever the observed object is changed. 
     * @param observableProperties the object that changed.
     */
    @Override
	public void objectChanged(final ObservableProperties observableProperties) {
		if (observableProperties == appProperties) {
	        LOGGER.log(Level.TRACE, "appProperties modificada");
			initializeMutableProperties();
		}
    }

	/**
	 * Muestra informaci�n sobre la conexi�n.
	 * @throws SQLException si hay errores de acceso a la base de datos.
	 */
	@Override
	public void showInfo(final String title) throws SQLException {
		DriverPropertiesPanel.showStandalone(dbConnection, title);
	}

	@Override
	public AgendaRecord getContacto(final int clave, final int version)
			throws SQLException {
		try (final PreparedStatement pstmt = dbConnection.prepareStatement(
	            SELECT_ONE_CONTACT_SENTENCE)) {
			int index = 0;
			pstmt.setInt(++index, clave);
			pstmt.setInt(++index, version);
			final ResultSet rset = pstmt.executeQuery();
            final AgendaRecord record = dbEngine.getNextRecord(rset);
            record.populate();
            return record;
		}
	}



    @Override
	public void stopDb(final boolean killDerby, final String shutdownUrl) throws SQLException {
    	if (dbConnection != null) {
			dbConnection.commit();
			dbConnection.close();
			//noinspection HardCodedStringLiteral
			LOGGER.log(Level.INFO, "Committed transaction and closed connection");
    	}
		try {
	    	if (!isEmptyOrBlank(shutdownUrl)) {
				try (final Connection connection = DriverManager.getConnection(shutdownUrl)) {
					// Just need the call to close the database.
				}
			}
		} catch (final SQLException se) {
			// The XJ015 error (successful shutdown of the Derby engine)
			// and the 08006 error (successful shutdown of a single database)
			// are the only exceptions thrown by Derby that might indicate
			// that an operation succeeded. All other exceptions indicate
			// that an operation failed. You should check the log file
			// to be certain.
			final String sqlState = se.getSQLState();
			if (sqlState.equals("08006")) {
				LOGGER.info("Database shut down normally");
			} else {
				LOGGER.error("Database did not shut down normally. SQLState = "
					+ sqlState, se);
			}
		}
		if (killDerby) {
			shutDown();
		}
		//noinspection HardCodedStringLiteral
		LOGGER.log(Level.INFO, getClass().getName() + " stopped");
	}

	/**
	 * In embedded mode, an application should shut down Derby.
	 * If the application fails to shut down Derby explicitly,
	 * the Derby does not perform a checkpoint when the JVM shuts down, which
	 * means that the next connection will be slower.
	 * Explicitly shutting down Derby with the URL is preferred.
	 * This style of shutdown will always throw an "exception".
	 */
	private static void shutDown() {
		try (final Connection connection = DriverManager.getConnection(DERBY_SHUTDOWN_SENTENCE)) {
			// Just need the call to close the database.
		} catch (final SQLException se) {
			// The XJ015 error (successful shutdown of the Derby engine)
			// and the 08006 error (successful shutdown of a single database)
			// are the only exceptions thrown by Derby that might indicate
			// that an operation succeeded. All other exceptions indicate
			// that an operation failed. You should check the log file
			// to be certain.
			final String sqlState = se.getSQLState();
			if (sqlState.equals("XJ015")) {
				LOGGER.info("Derby shut down normally");
			} else {
				LOGGER.error("Derby did not shut down normally. SQLState = "
					+ sqlState, se);
			}
		}
	}

	@Override
	public List<AgendaRecord> selectAllContactsByName() throws SQLException {
		try (final PreparedStatement statement = dbConnection.prepareStatement(
				SELECT_PREVAILING_RECORD_SENTENCE + ORDER_BY_NAME_CLAUSE)) {
			return dbEngine.executeTheQuery(statement);
		}
	}

	@Override
	public List<AgendaRecord> selectAllDeletedContactsByName() throws SQLException {
		try (final PreparedStatement statement = dbConnection.prepareStatement(
				SELECT_PREVAILING_DELETED_RECORD_SENTENCE + ORDER_BY_NAME_CLAUSE)) {
			return dbEngine.executeTheQuery(statement);
		}
	}

	@Override
	public List<AgendaRecord> selectAllContactsByBirthday() throws SQLException {
		try (final PreparedStatement statement = dbConnection.prepareStatement(
				SELECT_PREVAILING_BY_DATE_RECORD_SENTENCE)) {
			return dbEngine.executeTheQuery(statement);
		}
	}

	@Override
	public List<AgendaRecord> selectRecentlyModifiedContacts() throws SQLException {
		try (final PreparedStatement statement = dbConnection.prepareStatement(
				SELECT_PREVAILING_RECENTLY_MODIFIED_RECORDS_SENTENCE)) {
			statement.setInt(1, daysFork4RecentlyModified);
			return dbEngine.executeTheQuery(statement);
		}
	}

	@Override
	public List<AgendaRecord> selectFilteredContacts(final String filter) throws SQLException {
		final String theFilter = '%' + filter + '%';
		int ii = 0;
		try (final PreparedStatement statement = dbConnection.prepareStatement(
				SELECT_FILTERED_RECORDS_SENTENCE)) {
			statement.setString(++ii, theFilter);
			statement.setString(++ii, theFilter);
			statement.setString(++ii, theFilter);
			statement.setString(++ii, theFilter);
			statement.setString(++ii, theFilter);
			statement.setString(++ii, theFilter);
			return dbEngine.executeTheQuery(statement);
		}
	}

	@Override
	public List<AgendaRecord> selectContactsByNearBirthday() throws SQLException {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -daysFork4Birthdays);
		final int diaIni = calendar.get(Calendar.DAY_OF_MONTH);
		final int mesIni = calendar.get(Calendar.MONTH) + 1;
		calendar.add(Calendar.DAY_OF_MONTH, 2 * daysFork4Birthdays);
		final int diaFin = calendar.get(Calendar.DAY_OF_MONTH);
		final int mesFin = calendar.get(Calendar.MONTH) + 1;
		try (final PreparedStatement statement =
			dbConnection.prepareStatement(
				mesIni == mesFin ? SELECT_PREVAILING_BY_NEAR_DATE_SAME_MONTH_RECORD_SENTENCE
				: mesIni < mesFin ? SELECT_PREVAILING_BY_NEAR_DATE_DIFF_MONTH_SAME_YEAR_RECORD_SENTENCE
				: SELECT_PREVAILING_BY_NEAR_DATE_DIFF_MONTH_DIFF_YEAR_RECORD_SENTENCE)) {
			if (mesIni == mesFin) {
				statement.setInt(1, diaIni);
				statement.setInt(2, diaFin);
				statement.setInt(3, mesIni);
			} else {
//				if (mesFin < mesIni) {
//					mesFin ^= mesIni; mesIni ^= mesFin; mesFin ^= mesIni;
//					diaFin ^= diaIni; diaIni ^= diaFin; diaFin ^= diaIni;
//				}
				statement.setInt(1, diaIni);
				statement.setInt(2, mesIni);
				statement.setInt(3, diaFin);
				statement.setInt(4, mesFin);
				statement.setInt(5, mesIni);
				statement.setInt(6, mesFin);
			}
			return dbEngine.executeTheQuery(statement);
		}
	}

	@Override
	public List<AgendaRecord> selectContactsByGroups(final List<Grupo> groups,
		final boolean useIntersection, final boolean negated) throws SQLException {
		final int groupsSize = groups.size();
		if (groupsSize == 0) {
			return NULL_LIST;
		}

		final String byGroupsWhereClause;
		if (useIntersection) {
			byGroupsWhereClause =
				createGroupsIntersectionWhereClause(groups, negated);
		} else {
			byGroupsWhereClause = createGroupsUnionWhereClause(groups, negated);
		}
		final int builderCapacity = BASE_SELECT_RECORDS_SENTENCE.length()
			+ byGroupsWhereClause.length()
            + GROUP_BY_CLAUSE.length()
			+ BASE_HAVING_CLAUSE.length()
			+ ORDER_BY_NAME_CLAUSE.length();
		final StringBuilder builder = new StringBuilder(builderCapacity);
		builder.append(BASE_SELECT_RECORDS_SENTENCE).append(byGroupsWhereClause)
		    .append(GROUP_BY_CLAUSE).append(BASE_HAVING_CLAUSE)
            .append(ORDER_BY_NAME_CLAUSE);
		assert builderCapacity == builder.length();
		final String sql = builder.toString();
		LOGGER.debug("SQL : " + sql);
		try (final PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
			for (int ii = 0; ii < groupsSize; ii++) {
				final int codigo = groups.get(ii).getClave();
				LOGGER.debug(String.format("codigo : %d", codigo));
				stmt.setInt(ii + 1, codigo);
			}
			return dbEngine.executeTheQuery(stmt);
		}
	}

	private static String createGroupsIntersectionWhereClause(
			final List<Grupo> groups, final boolean negated) {
		final String whereClause = "WHERE NOT Deleted ";
		final String inGroupClause = "AND " + (negated ? "NOT " : "")
			+ " EXISTS (SELECT 1 FROM CONTACTOGRUPO cg "
			+ "WHERE cg.clave = ctos.clave and "
			+ "cg.Version = ctos.VersionGrupo and ClaveGrupo = ?) ";
		final int groupsSize = groups.size();
		final int builderCapacity = whereClause.length()
			+ inGroupClause.length() * groupsSize;
		final StringBuilder builder = new StringBuilder(builderCapacity);
		builder.append(whereClause);
        //noinspection ForLoopReplaceableByForEach
        for (int ii = 0; ii < groupsSize; ii++) {
			builder.append(inGroupClause);
		}
		assert builderCapacity == builder.length();
		return builder.toString();
	}

	private static String createGroupsUnionWhereClause(
			final List<Grupo> groups, final boolean negated) {
		final String byGroupsWhereClause =
			"WHERE NOT Deleted AND " + (negated ? "NOT " : "")
				+ " EXISTS (SELECT 1 FROM CONTACTOGRUPO cg "
				+ "WHERE cg.clave = ctos.clave and "
				+ "cg.Version = ctos.VersionGrupo and ClaveGrupo in (";
		final int groupsSize = groups.size();
		final int builderCapacity = byGroupsWhereClause.length()
			+ groupsSize * 2 + 1;
		final StringBuilder builder = new StringBuilder(builderCapacity);
		builder.append(byGroupsWhereClause);
        //noinspection ForLoopReplaceableByForEach
        for (int ii = 0; ii < groupsSize; ii++) {
			builder.append("?,");
		}
		builder.setCharAt(builder.length() - 1, RIGHT_PARENTHESIS);
		builder.append(RIGHT_PARENTHESIS);
		assert builderCapacity == builder.length();
		return builder.toString();
	}

	@Override
	public List<AgendaRecord> selectAllContactsForTelephoneList(
			final List<Pais> countries) throws SQLException {
		final int countriesSize = countries.size();
		if (countriesSize == 0) {
			return NULL_LIST;
		}
		final String byCountriesWhereClause = "WHERE NOT Deleted AND PAIS IN (";
		final String existsTelefonosClause =
			" AND Listar AND EXISTS (SELECT Numero from TELEFONO TEL WHERE "
				+ "TEL.Clave = CTOS.Clave AND TEL.Version = CTOS.VersionTelefono)";
		final int builderCapacity = BASE_SELECT_RECORDS_SENTENCE.length()
			+ byCountriesWhereClause.length()
			+ existsTelefonosClause.length()
			+ countriesSize * 2 + GROUP_BY_CLAUSE.length()
	        + BASE_HAVING_CLAUSE.length()
			+ ORDER_BY_NAME_CLAUSE.length();
		final StringBuilder builder = new StringBuilder(builderCapacity);
			builder.append(BASE_SELECT_RECORDS_SENTENCE).append(
				byCountriesWhereClause);
	        //noinspection ForLoopReplaceableByForEach
	        for (int ii = 0; ii < countriesSize; ii++) {
			builder.append("?,");
		}
		builder.setCharAt(builder.length() - 1, RIGHT_PARENTHESIS);
		builder.append(existsTelefonosClause).append(GROUP_BY_CLAUSE)
	        .append(BASE_HAVING_CLAUSE).append(ORDER_BY_NAME_CLAUSE);
		assert builderCapacity == builder.length();
		final String sql = builder.toString();
		LOGGER.debug("SQL : " + sql);
		try (final PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
			for (int ii = 0; ii < countriesSize; ii++) {
				final String codigo = countries.get(ii).getCountryCode();
				LOGGER.debug(String.format("codigo : %s", codigo));
				stmt.setString(ii + 1, codigo);
			}
			return dbEngine.executeTheQuery(stmt);
		}
	}

	@Override
	public List<AgendaRecord> selectSpecialContacts() throws SQLException {
		try (final PreparedStatement stmt = dbConnection.prepareStatement(
            SELECT_PREVAILING_SPECIAL_RECORD_SENTENCE
                + ORDER_BY_SPECIAL_CLAUSE)) {
			return dbEngine.executeTheQuery(stmt);
		}
	}


    @Override
    public Optional<Timestamp> lastUpdated() throws SQLException {
		try (final PreparedStatement pstmt = dbConnection.prepareStatement(
      			"SELECT FechaActualizacion FROM ROOT.CONTROL");
			 final ResultSet resultSet = pstmt.executeQuery()) {
			return resultSet.next() ? Optional.ofNullable(resultSet.getTimestamp(1)) : Optional.empty();
		}
    }

	/**
	 * Inserta un registro
	 *
	 * La clave, la version y la fecha de creaci�n de record se modifican.

	 * @param record El registro a insertar.
	 * @throws SQLException si problemas haber
	 */
	@Override
	public void insertRecord(final AgendaRecord record) throws SQLException {
		record.setKey(generalHelper.obtainNextKey(CONTACTO_TABLE))
            .setVersion(1)
            .setCreationTimestamp(new Timestamp(new Date().getTime()))
            .setVersionGroup(saveGrupos(record))
            .setVersionTelephone(saveTelephones(record))
            .setVersionAddress(saveDirecciones(record))
            .setVersionEmail(saveEmails(record))
            .setVersionNotes(saveNotes(record))
            .setVersionImage(saveImages(record));
		putRecord(record);
	}

	/**
	 * 	Con el concepto de version, no debe haber update, sino un insert
	 * con incremento de la misma.
	 *
	 * La version y la fecha de actualizacion de record se modifican.
	 *
	 * @param record El registro a actualizar.
	 * @throws SQLException si problemas haber
	 */
	@Override
	public void updateRecord(final AgendaRecord record) throws SQLException {
		record.setVersion(generalHelper.obtainNextVersion(CONTACTO_TABLE, record.getKey()))
		    .setUpdateTimestamp(new Timestamp(new Date().getTime()));
		putRecord(record);
	}

	/**
	 * No debe haber un borrado; simplemente se debe marcar como tal (aplicado
	 * a todo el hist�rico del registro)
	 * @param key la clave del registro a borrar
	 * @throws SQLException si hay problemas
	 */
	@Override
	public void markAsDeleted(final int key) throws SQLException {
		try (final PreparedStatement pstmt = dbConnection.prepareStatement(
				DELETE_SENTENCE)) {
			LOGGER.debug("'Borrando' el registro " + key);
			pstmt.setBoolean(1, true);
			pstmt.setInt(2, key);
			pstmt.execute();
			dbConnection.commit();
		}
	}

	private void putRecord(final AgendaRecord record) throws SQLException {
		try (final PreparedStatement pstmt = dbConnection.prepareStatement(
				INSERT_SENTENCE)) {
			int index = 0;
			pstmt.setInt(++index, record.getKey());
			pstmt.setInt(++index, record.getVersion());
			pstmt.setString(++index, record.getFirstname());
			pstmt.setString(++index, record.getLastname());
			pstmt.setString(++index, record.getSex());
			pstmt.setString(++index, record.getCountry().getCountryCode());
			pstmt.setInt(++index, record.getVersionTelephone());
			pstmt.setInt(++index, record.getVersionAddress());
			pstmt.setInt(++index, record.getVersionEmail());
			pstmt.setInt(++index, record.getVersionGroup());
			pstmt.setInt(++index, record.getVersionImage());
			pstmt.setInt(++index, record.getDay());
			pstmt.setInt(++index, record.getMonth());
			pstmt.setInt(++index, record.getYear());
			pstmt.setString(++index, record.getMark());
			pstmt.setTimestamp(++index, record.getUpdateTimestamp());
			pstmt.setTimestamp(++index, record.getCreationTimestamp());
			pstmt.setBoolean(++index, record.isListTelephones());
			pstmt.setInt(++index, record.getVersionNotes());
			pstmt.setBoolean(++index, record.isDeleted());
			pstmt.execute();
			dbConnection.commit();
		}
	}


    @Override
    public boolean isLastVersion(final AgendaRecord record) {
        try {
            return record.getVersion() ==
                generalHelper.obtainNextVersion(CONTACTO_TABLE, record.getKey()) - 1;
        } catch (final SQLException ex) {
            throw new AgendaDbException(ex);
        }
    }

    /**
     * Returns the number of contacts in the database.
     *
     * @return the number of contacts in the database.
     */
    @Override
    public int getRecordCount() {
        int nextKey = 0;
        try {
            nextKey = generalHelper.obtainNextKey(AgendaDb.CONTACTO_TABLE);
        } catch (final SQLException ex) {
            LOGGER.log(Level.WARN, "Error tratando de obtener el n�mero de registros", ex);
        }
        return nextKey - 1;
    }

	@Override
	public void executePlugin(final Plugin plugin, final String... args) throws SQLException {
		plugin.execute(appProperties, dbConnection, args);
	}

	// Use this date if last updated is unknown: assume last updated before the use of table 'CONTROL'.
	private final Timestamp theEpoch = new Timestamp(0L);

    /**
     * Verifica la fecha de �ltima actulizaci�n compar�ndola con la del backup y
     * hace la copia si es necesario.
     * 
     * @param properties Las propiedades de la aplicaci�n.
     * @param parent El padre de los di�logos que se muestren.
     * @return <code>true</code> Either if the database was copied or not,
     * but the operation was not disregarded. <code>false</code> if the 
     * operation was cancelled; useful to determine the subsequent behaviour
     * of the application. 
     * @throws SQLException Si hay problemas con la base de datos.
     */
	@Override
	public boolean checkLastUpdated(final Properties properties,
			final Component parent) throws SQLException {
		final boolean parentVisible = parent.isVisible();
        final Date date1 = lastUpdated().orElse(theEpoch);
		try {
            final Date date2 = obtainSecondaryLastUpdated();
            if (date2 == null) {
                LOGGER.warn("Not checking last updated");
                return true;
            }
			final int compareTo = date1.compareTo(date2);
			if (compareTo < 0) {
                final int response = askForCopyConfirmation(properties, parent, date1, date2, true);
                final String srcPath = properties.getProperty("AgendaDb.secondary.path");
           		final String tgtPath = properties.getProperty("AgendaDb.path");
                return processCopyRequest(properties, date1, response, tgtPath, srcPath);
            } else if (compareTo > 0) {
                final int response = askForCopyConfirmation(properties, parent, date1, date2, false);
                final String srcPath = properties.getProperty("AgendaDb.path");
				final String tgtPath = properties.getProperty("AgendaDb.secondary.path");
                return processCopyRequest(properties, date2, response, tgtPath, srcPath);
            }
	    } catch (final IOException ex) {
	        LOGGER.error(LoggerFactory.ERROR_TAG, ex);
		} catch (final Exception ex) {
			LOGGER.warn(LoggerFactory.ERROR_TAG, ex);
	    } finally {
			parent.setVisible(parentVisible);
		}
		return true;
	}

    private boolean processCopyRequest(final Properties appProperties,
            final Date dbDate, final int response, final String tgtPath, final String srcPath)
            throws Exception {
        switch (response) {
        case JOptionPane.CANCEL_OPTION:
            return false;
        case JOptionPane.NO_OPTION:
            return true;
        case JOptionPane.YES_OPTION:
            stopDb(false, appProperties.getProperty("AgendaDb.shutdown.resolvedUrl"));
            AgendaUtil.copyDatabase(appProperties, srcPath, tgtPath, dbDate);
            AgendaUtil.deleteDatabaseBackups(tgtPath, maxBackupHistory);
            initDb(appProperties, false, false);
            return true;
        default:
            throw new InternalError("The option <" + response + "> is impossible");
        }
    }

    @Override
    public void changeDatabase(final Properties properties, final Component parent) throws SQLException {
        try {
            stopDb(false, properties.getProperty("AgendaDb.shutdown.resolvedUrl"));
            setNewDbParameters(properties, parent);
            initDb(properties, true, false);

            // FIXME: C�digo repetido en Agenda::initDbPhase2
            Pais.setUnknownCountry(properties.getProperty("AgendaGUI.paisDesconocido"));
            loadCountries();
            Pais.setDefaultCountry(Pais.getInstance(getStringFromProperties(properties, "Agenda.default.country")));
            Grupo.init(getListaGrupos());
            TipoTelefono.init(getListaTiposTelefono());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
    }

    private static void setNewDbParameters(final Properties properties, final Component parent) {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setCurrentDirectory(new File(properties.getProperty("AgendaDb.path")).getParentFile());
		if (fileChooser.showOpenDialog( parent) == JFileChooser.APPROVE_OPTION) {
			properties.setProperty("AgendaDb.path", fileChooser.getSelectedFile().getAbsolutePath());
			PropertiesManager.resolveUrls(properties);
			// FIXME: Is this OK?
			properties.remove("AgendaDb.secondary.url");
			properties.remove("AgendaDb.secondary.shutdown.url");
        } else {
			LOGGER.warn("Nothing selected");
            showMessageDialog(parent, "No pain no gain",
                getStringFromProperties(properties, "Agenda.alert.title"),
                ERROR_MESSAGE);
        }
    }

    private static int askForCopyConfirmation(final Properties properties, final Component parent,
        final Date date1, final Date date2, final boolean isNewer) {
        parent.setVisible(true);
		final SimpleDateFormat  dateFormatStd = new SimpleDateFormat(PropertiesHelper
				.getStringFromProperties(properties, "Agenda.datePattern"), parent.getLocale());
		return JOptionPane.showConfirmDialog(parent,
            MessageFormat.format(PropertiesHelper.getStringFromProperties(
                properties, isNewer ? "Agenda.newer.database" : "Agenda.older.database"),
                    dateFormatStd.format(date2), dateFormatStd.format(date1)),
            PropertiesHelper.getStringFromProperties(properties,
                "Agenda.alert.title"), JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);
    }

    private Date obtainSecondaryLastUpdated() throws Exception {
        final Properties secondaryProperties = new Properties();
        final String secondaryClassName = appProperties.getProperty("AgendaDb.secondary.className");
        final String secondaryDriver = appProperties.getProperty("AgendaDb.secondary.driver");
        final String secondaryURL = appProperties.getProperty("AgendaDb.secondary.url");
        if (secondaryClassName == null || secondaryDriver == null
                || secondaryURL == null) {
            return null;
        }
        secondaryProperties.put("AgendaDb.className", secondaryClassName);
        secondaryProperties.put("AgendaDb.driver", secondaryDriver);
        secondaryProperties.put("AgendaDb.resolvedUrl", secondaryURL);
        final AgendaDb secondaryAgendaDb = AgendaDbFactory.getAgendaDb(
             secondaryProperties);
        secondaryProperties.setProperty("AgendaDb.user", AgendaLogin.getUsername());
        secondaryProperties.setProperty("AgendaDb.password", AgendaLogin.getPassword());
        try {
            secondaryAgendaDb.initDb(secondaryProperties, false, false);
            final Date lastUpdated = secondaryAgendaDb.lastUpdated().orElse(theEpoch);
            secondaryAgendaDb.stopDb(false,
                appProperties.getProperty("AgendaDb.secondary.shutdown.url"));
            return lastUpdated;
        } catch (final SQLException ignored) {
            return null;
        }
    }

    /**
	 * Busca los registros cuyas fechas de nacimiento tengan alg�na
	 * caracter�stica "interesante". De momento las que tengan un m�ltiplo de
	 * 1.000 d�as a la fecha de ejecuci�n + o - <code>daysFork4InterestingDates</code>
	 * en los �ltimos 40.000 d�as.
	 *
	 * @return los registros cuyas fechas de nacimiento tengan alg�na
	 * caracter�stica "interesante".
	 *
	 * @throws SQLException Si hay problemas.
	 */
	@Override
	public List<AgendaRecord> selectContactsByInterestingDates() throws SQLException {
		final List<AgendaRecord> todos = new ArrayList<>();
		int interestingDatesGap;
		try {
			interestingDatesGap = Integer
                .parseInt(appProperties.getProperty("AgendaDb.interestingDatesDivisor", "1000"));
		} catch (final NumberFormatException e) {
			LOGGER.warn("Property interestingDatesDivisor not set in properties file; using default (1000)");
			interestingDatesGap = 1000;
		}
		final int limit = 40_000 / interestingDatesGap;
		final String whereClause = "WHERE NOT Deleted AND (";
		final String inDatesClause = "(dia = ? AND mes = ? AND ano = ?) OR ";
		final int builderCapacity = BASE_SELECT_RECORDS_SENTENCE.length()
			+ whereClause.length()
			+ inDatesClause.length() * limit - 3	// El �ltimo "OR "
	        + GROUP_BY_CLAUSE.length()
			+ 1										// el ") "
			+ BASE_HAVING_CLAUSE.length()
			+ ORDER_BY_NAME_CLAUSE.length();
		final StringBuilder builder = new StringBuilder(builderCapacity);
		builder.append(BASE_SELECT_RECORDS_SENTENCE).append(whereClause);
		for (int ii = 0; ii < limit; ii++) {
			builder.append(inDatesClause);
		}
        int length = builder.length();
        builder.delete(length - 4, length);
		builder.append(") ").append(GROUP_BY_CLAUSE).append(BASE_HAVING_CLAUSE)
	        .append(ORDER_BY_NAME_CLAUSE);
        length = builder.length();
		assert builderCapacity == length : "capacity = " + builderCapacity
			+ ", length = " + length;

        try (final PreparedStatement statement = dbConnection.prepareStatement(builder.toString())) {
			// N.B.: Hago todas esta queries en vez de una sola porque una sola
			// es demasiado grande dependiendo de limit y daysInAdvance
			for (int jj = -daysFork4InterestingDates + 1;
				    jj < daysFork4InterestingDates; jj++) {
				final Calendar rightNow = Calendar.getInstance();
				rightNow.add(Calendar.DAY_OF_MONTH, jj);
				for (int ii = 0; ii < limit * 3; ii += 3) {
					rightNow.add(Calendar.DAY_OF_MONTH, - interestingDatesGap);
					statement.setInt(ii + 1, rightNow.get(Calendar.DAY_OF_MONTH));
					statement.setInt(ii + 2, rightNow.get(Calendar.MONTH) + 1);
					statement.setInt(ii + 3, rightNow.get(Calendar.YEAR));
				}
				todos.addAll(dbEngine.executeTheQuery(statement));
			}
			return todos;
        }
	}

	@Override
	public List<AgendaRecord> selectContactsByCustomQuery(final String whereClause, final String orderByClause) throws SQLException {
		final String sql = BASE_SELECT_RECORDS_SENTENCE
		    	+ "WHERE " + whereClause
	            + GROUP_BY_CLAUSE
	            + BASE_HAVING_CLAUSE
	            + (isEmptyOrBlank(orderByClause) ? ORDER_BY_DATE_CLAUSE : " ORDER BY " + orderByClause);
		LOGGER.debug(whereClause);
		try (final PreparedStatement statement = dbConnection.prepareStatement(sql)) {
			return dbEngine.executeTheQuery(statement);
		}
	}

/////////////////////////////////////// M�todos delegados a clases auxiliares ///////////////////////////////////////////////////////

    @Override
   	public void loadCountries() throws SQLException {
        countryHelper.loadCountries();
   	}

	@Override
	public int addGrupo(final String groupName) throws SQLException {
        return groupHelper.addGroup(groupName);
	}

    @Override
	public List<Grupo> getListaGrupos() throws SQLException {
        return groupHelper.getGroups();
	}

	@Override
	public void addCountry(final String code, final String name, final String phoneMask)
			throws SQLException {
        countryHelper.addCountry(code, name, phoneMask);
	}

	@Override
	public void updateCountry(final String code, final String name, final String phoneMask)
			throws SQLException {
        countryHelper.updateCountry(code, name, phoneMask);
	}

	@Override
	public int addTipoTelefono(final String typeName, final int fgColor) throws SQLException {
        return telephoneHelper.addTipoTelefono(typeName, fgColor);
	}

    @Override
   	public List<TipoTelefono> getListaTiposTelefono() throws SQLException {
           return telephoneHelper.getTelephoneTypes();
   	}

    /** Graba los grupos de un contacto.
   	 * @param record el registro a actualizar.
   	 * @return el nuevo nro de version de los grupos o 0 si no hay actualizaci�n.
   	 */
   	@Override
   	public int saveGrupos(final AgendaRecord record) throws SQLException {
           return groupHelper.persist(record);
   	}

	/** Graba los tel�fonos de un contacto.
	 * @param record el registro a actualizar.
	 * @return el nuevo nro de version de los telefonos o 0 si no hay actualizaci�n.
	 */
	@Override
	public int saveTelephones(final AgendaRecord record)
			throws SQLException {
        return telephoneHelper.persist(record);
	}

	/** Graba las direcciones de un contacto.
	 * @param record el registro a actualizar.
	 * @return el nuevo nro de version de las direcciones o 0 si no hay actualizaci�n.
	 */
	@Override
	public int saveDirecciones(final AgendaRecord record)
			throws SQLException {
        return addressHelper.persist(record);
	}

	/** Graba los emails de un contacto.
	 * @param record el registro a actualizar.
	 * @return el nuevo nro de version de los emails o 0 si no hay actualizaci�n.
	 */
	@Override
	public int saveEmails(final AgendaRecord record)
			throws SQLException {
        return emailHelper.persist(record);
	}

	/** Graba las Notas de un contacto.
	 * @param record el registro a actualizar.
	 * @return el nuevo nro de version de las Notas o 0 si no hay actualizaci�n.
	 */
	@Override
	public int saveNotes(final AgendaRecord record)
			throws SQLException {
        return noteHelper.persist(record);
	}

	/** Graba las im�genes de un contacto.
	 * @param record el registro a actualizar.
	 * @return el nuevo nro de version de las im�genes o 0 si no hay actualizaci�n.
	 */
	@Override
	public int saveImages(final AgendaRecord record) {
        return imageHelper.persist(record);
	}

	@Override
	public void updateImagePath(final int recordId, final String sourcePath, final String targetPath) throws SQLException {
		try (final PreparedStatement statement = dbConnection.prepareStatement(
            "UPDATE root.imagen SET imagePath = ? WHERE clave = ? AND imagePath = ?")) {
			statement.setString(1, targetPath);
			statement.setInt(2, recordId);
			statement.setString(3, sourcePath);
			statement.executeUpdate();
		}
	}
}
