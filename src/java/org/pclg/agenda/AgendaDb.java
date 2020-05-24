package org.pclg.agenda;

import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.Grupo;
import org.pclg.agenda.entities.Pais;
import org.pclg.agenda.entities.TipoTelefono;
import org.pclg.agenda.plugins.Plugin;

import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


/**
 * @author El Coyote
 * @since 11-sep-2007 12:55:16
 */
public interface AgendaDb {
	/** Name of the "contacto" table. */
	String CONTACTO_TABLE = "CONTACTO";

	void initDb(Properties properties, boolean checkTables,
        boolean forceCreateTables) throws InstantiationException, 
        IllegalAccessException, ClassNotFoundException, SQLException;

	void stopDb(boolean killDerby, String shutdownUrl) throws SQLException;

	/** Select all contacts. */
	List<AgendaRecord> selectAllContactsByName() throws SQLException;

	/** Select all deleted contacts. */
	List<AgendaRecord> selectAllDeletedContactsByName() throws SQLException;

	List<AgendaRecord> selectAllContactsByBirthday() throws SQLException;

	List<AgendaRecord> selectContactsByNearBirthday() throws SQLException;

	List<AgendaRecord> selectAllContactsForTelephoneList(List<Pais> countries)
		throws SQLException;

	/** Select contacts which have 'Marca' set. */
	List<AgendaRecord> selectSpecialContacts() throws SQLException;
	
	List<AgendaRecord> selectRecentlyModifiedContacts() throws SQLException;

	List<AgendaRecord> selectFilteredContacts(String filter) throws SQLException;

	void insertRecord(AgendaRecord record) throws SQLException;

	void updateRecord(AgendaRecord record) throws SQLException;

	void markAsDeleted(int key) throws SQLException;

	void showInfo(String title) throws SQLException;

	void loadCountries() throws SQLException;

	AgendaRecord getContacto(int clave, int version) throws SQLException;

    /**
     * Returns the number of contacts in the database or 0 if an error occurs.
     * @return  the number of contacts in the database.
     */
	int getRecordCount();

	int saveTelephones(AgendaRecord record) throws SQLException;

	int saveDirecciones(AgendaRecord record) throws SQLException;

	int saveEmails(AgendaRecord record) throws SQLException;

	int saveNotes(AgendaRecord record) throws SQLException;

	List<Grupo> getListaGrupos() throws SQLException;

	List<TipoTelefono> getListaTiposTelefono() throws SQLException;

	int saveGrupos(AgendaRecord record) throws SQLException;

	int addGrupo(String groupName) throws SQLException;

	int addTipoTelefono(String typeName, int fgColor) throws SQLException;

	List<AgendaRecord> selectContactsByGroups(List<Grupo> groups,
        boolean useIntersection, boolean negated) throws SQLException;

	int saveImages(AgendaRecord record);

	Optional<Timestamp> lastUpdated() throws SQLException;

	void addCountry(String code, String name, final String phoneMask) throws SQLException;

	void updateCountry(String code, String name, final String phoneMask) throws SQLException;

    void executePlugin(Plugin plugin, String... args) throws SQLException;
    
    /**
     * Verifica la fecha de última actulización comparándola con la del backup y
     * hace la copia si es necesario.
     * 
     * @param properties Las propiedades de la aplicación.
     * @param parent El padre de los diálogos que se muestren.
     * @return <code>true</code> Either if the database was copied or not,
     * but the operation was not disregarded. <code>false</code> if the 
     * operation was cancelled; useful to determine the subsequent behaviour
     * of the application. 
     * @throws SQLException Si hay problemas con la base de datos.
     */
    boolean checkLastUpdated(final Properties properties, final Component parent) throws SQLException;

    void changeDatabase(final Properties properties, final Component parent) throws SQLException;

	/**
	 * Busca los registros cuyas fechas de nacimiento tengan algúna
	 * característica "interesante".
	 * @return los registros cuyas fechas de nacimiento tengan algúna
	 * característica "interesante".
	 * @throws SQLException Si hay problemas.
	 */
    List<AgendaRecord> selectContactsByInterestingDates() throws SQLException;

	/**
	 * Busca los registros segun la cláusula WHERE que se le pase.
	 * @param whereClause la cláusula WHERE a usar.
	 * @param orderByClause la cláusula ORDER BY a usar.
	 * @return los registros segun la cláusula WHERE que se le pase.
	 * @throws SQLException Si hay problemas.
	 */
	List<AgendaRecord> selectContactsByCustomQuery(String whereClause, String orderByClause) throws SQLException;

    /**
     * Returns <code>true</code> if the version of this record is the last one, <code>false</code> otherwise.
     * @param record the record to check.
     * @return <code>true</code> if the version of this record is the last one, <code>false</code> otherwise.
     */
    boolean isLastVersion(AgendaRecord record);

    void updateImagePath(int recordId, String toString, String toString1) throws SQLException;
}
