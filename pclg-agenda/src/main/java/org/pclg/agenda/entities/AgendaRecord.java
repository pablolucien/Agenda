package org.pclg.agenda.entities;

import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.pclg.agenda.TimestampAdapter;

import javax.swing.Icon;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 26/08/17 22:27
 */
public interface AgendaRecord extends Serializable {
    int NAME_FIELD_LEN = 30;
    int SURNAME_FIELD_LEN = 30;
    int ADDRESS_FIELD_LEN = 100;
    int NOTES_FIELD_LEN = 150;
    int TELEPHONE_FIELD_LEN = 15;
    int COUNTRY_CODE_FIELD_LEN = 3;
    AgendaRecord NULL_AGENDA_RECORD = new DummyAgendaRecord();

    /**
     * Returns an XML representation of this record.
     *
     * @return an XML representation of this record.
     */
    String toXML();

    /**
     * Returns an XML representation of this record.
     *
     * @return an XML representation of this record.
     */
    String XX_toXML();

    /**
     * Verifica si los telefonos de un registro coinciden con los del otro.
     *
     * @param record
     *            el registro a comparar.
     * @return <code>true</code> si son iguales, <code>false</code> si no.
     */
    boolean equalsTelephones(AgendaRecord record);

    /**
     * Verifica si los grupos de un registro coinciden con los del otro.
     *
     * @param record
     *            el registro a comparar.
     * @return <code>true</code> si son iguales, <code>false</code> si no.
     */
    boolean equalsGroups(AgendaRecord record);

    /**
     * Verifica si las direcciones de un registro coinciden con los del otro.
     * (De momento sólo hay una, pero en un futuro ya veremos.
     *
     * @param record
     *            el registro a comparar.
     * @return <code>true</code> si son iguales, <code>false</code> si no.
     */
    boolean equalsAddresses(AgendaRecord record);

    /**
     * Verifica si los emails de un registro coinciden con los del otro. (De
     * momento sólo hay uno, pero en un futuro ya veremos.
     *
     * @param record
     *            el registro a comparar.
     * @return <code>true</code> si son iguales, <code>false</code> si no.
     */
    boolean equalsEmails(AgendaRecord record);

    /**
     * Verifica si las notas de un registro coinciden con los del otro. (De
     * momento sólo hay una, pero en un futuro ya veremos.
     *
     * @param record
     *            el registro a comparar.
     * @return <code>true</code> si son iguales, <code>false</code> si no.
     */
    boolean equalsNotes(AgendaRecord record);

    /**
     * Verifica si las imagenes de un registro coincide con las del otro. (De
     * momento sólo hay una, pero en un futuro ya veremos.
     *
     * @param record
     *            el registro a comparar.
     * @return <code>true</code> si son iguales, <code>false</code> si no.
     */
    boolean equalsImages(AgendaRecord record);

    @XmlTransient
    int getKey();

    AgendaRecord setKey(int key);

    @XmlTransient
    int getVersion();

    AgendaRecord setVersion(int version);

    @XmlTransient
    Pais getCountry();

    AgendaRecord setCountry(Pais country);

    int getVersionNotes();

    AgendaRecord setVersionNotes(int versionNotes);

    int getVersionGroup();

    AgendaRecord setVersionGroup(int versionGroup);

    String getFirstname();

    AgendaRecord setFirstname(String firstname);

    String getLastname();

    AgendaRecord setLastname(String lastname);

    default String fullName() {
		final String firstname = getFirstname();
		final String lastname = getLastname();
		return ((firstname == null ? "" : firstname) + ' ' + (lastname == null ? "" : lastname)).trim();
    }

    String getImagePath();

    AgendaRecord setImagePath(String image);

    int getVersionImage();

    AgendaRecord setVersionImage(int versionImage);

    int getVersionTelephone();

    AgendaRecord setVersionTelephone(int versionTelephone);

    List<Telefono> getTelephones();

    AgendaRecord setTelephones(List<Telefono> telephones);

    int getVersionAddress();

    AgendaRecord setVersionAddress(int versionDireccion);

    String getAddress();

    AgendaRecord setAddress(String address);

    List<String> getAddresses();

    int getVersionEmail();

    AgendaRecord setVersionEmail(int versionEmail);

    List<String> getEmails();

    AgendaRecord addEmail(String email);

    AgendaRecord setEmails(List<String> emailsList);

    int getDay();

    AgendaRecord setDay(int day);

    int getMonth();

    AgendaRecord setMonth(int month);

    int getYear();

    AgendaRecord setYear(int year);

    String getMark();

    AgendaRecord setMark(String mark);

    Timestamp getUpdateTimestamp();

    @XmlJavaTypeAdapter( TimestampAdapter.class )
    AgendaRecord setUpdateTimestamp(Timestamp updateTimestamp);

    Timestamp getCreationTimestamp();

    @XmlJavaTypeAdapter( TimestampAdapter.class )
    AgendaRecord setCreationTimestamp(Timestamp creationTimestamp);

    boolean isListTelephones();

    AgendaRecord setListTelephones(boolean listar);

    boolean isDeleted();

    AgendaRecord setDeleted(boolean deleted);

    String getNotes();

    AgendaRecord setNotes(String notes);

    String getSex();

    AgendaRecord setSex(String sex);

    List<Grupo> getGroups();

    AgendaRecord setGroups(List<Grupo> groups);

    boolean isHighlighted();

    AgendaRecord setHighlighted(boolean highlighted);

    boolean isImageDirty();

    AgendaRecord setImageDirty(boolean imageDirty);

    AgendaRecord setTemporaryImage(String temporaryImage);

    String getTemporaryImage();

	AgendaRecord setThumbnail(Icon thumbnail);

	Icon getThumbnail();

    default void populate() {}
}
