package org.pclg.agenda.entities;

import javax.swing.Icon;
import java.sql.Timestamp;
import java.util.List;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 26/08/17 23:07
 */
public final class DummyAgendaRecord implements AgendaRecord {
    private static final long serialVersionUID = 5194124407610349938L;

    @Override
    public String toXML() {
        return "";
    }

    @Override
    public String XX_toXML() {
        return "";
    }

    @Override
    public boolean equalsTelephones(final AgendaRecord record) {
        return false;
    }

    @Override
    public boolean equalsGroups(final AgendaRecord record) {
        return false;
    }

    @Override
    public boolean equalsAddresses(final AgendaRecord record) {
        return false;
    }

    @Override
    public boolean equalsEmails(final AgendaRecord record) {
        return false;
    }

    @Override
    public boolean equalsNotes(final AgendaRecord record) {
        return false;
    }

    @Override
    public boolean equalsImages(final AgendaRecord record) {
        return false;
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public AgendaRecord setKey(final int key) {
        return this;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public AgendaRecord setVersion(final int version) {
        return this;
    }

    @Override
    public Pais getCountry() {
        return null;
    }

    @Override
    public AgendaRecord setCountry(final Pais country) {
        return this;
    }

    @Override
    public int getVersionNotes() {
        return 0;
    }

    @Override
    public AgendaRecord setVersionNotes(final int versionNotes) {
        return this;
    }

    @Override
    public int getVersionGroup() {
        return 0;
    }

    @Override
    public AgendaRecord setVersionGroup(final int versionGroup) {
        return this;
    }

    @Override
    public String getFirstname() {
        return "";
    }

    @Override
    public AgendaRecord setFirstname(final String firstname) {
        return this;
    }

    @Override
    public String getLastname() {
        return "";
    }

    @Override
    public AgendaRecord setLastname(final String lastname) {
        return this;
    }

    @Override
    public String getImagePath() {
        return "";
    }

    @Override
    public AgendaRecord setImagePath(final String imagePath) {
        return this;
    }

    @Override
    public int getVersionImage() {
        return 0;
    }

    @Override
    public AgendaRecord setVersionImage(final int versionImage) {
        return this;
    }

    @Override
    public int getVersionTelephone() {
        return 0;
    }

    @Override
    public AgendaRecord setVersionTelephone(final int versionTelephone) {
        return this;
    }

    @Override
    public List<Telefono> getTelephones() {
        return null;
    }

    @Override
    public AgendaRecord setTelephones(final List<Telefono> telephones) {
        return this;
    }

    @Override
    public int getVersionAddress() {
        return 0;
    }

    @Override
    public AgendaRecord setVersionAddress(final int versionDireccion) {
        return this;
    }

    @Override
    public String getAddress() {
        return "";
    }

    @Override
    public AgendaRecord setAddress(final String address) {
        return this;
    }

    @Override
    public int getVersionEmail() {
        return 0;
    }

    @Override
    public AgendaRecord setVersionEmail(final int versionEmail) {
        return this;
    }

    @Override
    public List<String> getEmails() {
        return null;
    }

    @Override
    public AgendaRecord addEmail(final String email) {
        return this;
    }

    @Override
    public AgendaRecord setEmails(final List<String> emailsList) {
        return this;
    }

    @Override
    public int getDay() {
        return 0;
    }

    @Override
    public AgendaRecord setDay(final int day) {
        return this;
    }

    @Override
    public int getMonth() {
        return 0;
    }

    @Override
    public AgendaRecord setMonth(final int month) {
        return this;
    }

    @Override
    public int getYear() {
        return 0;
    }

    @Override
    public AgendaRecord setYear(final int year) {
        return this;
    }

    @Override
    public String getMark() {
        return "";
    }

    @Override
    public AgendaRecord setMark(final String mark) {
        return this;
    }

    @Override
    public Timestamp getUpdateTimestamp() {
        return null;
    }

    @Override
    public AgendaRecord setUpdateTimestamp(final Timestamp updateTimestamp) {
        return this;
    }

    @Override
    public Timestamp getCreationTimestamp() {
        return null;
    }

    @Override
    public AgendaRecord setCreationTimestamp(final Timestamp creationTimestamp) {
        return this;
    }

    @Override
    public boolean isListTelephones() {
        return false;
    }

    @Override
    public AgendaRecord setListTelephones(final boolean listar) {
        return this;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public AgendaRecord setDeleted(final boolean deleted) {
        return this;
    }

    @Override
    public String getNotes() {
        return "";
    }

    @Override
    public AgendaRecord setNotes(final String notes) {
        return this;
    }

    @Override
    public String getSex() {
        return "";
    }

    @Override
    public AgendaRecord setSex(final String sex) {
        return this;
    }

    @Override
    public List<Grupo> getGroups() {
        return null;
    }

    @Override
    public AgendaRecord setGroups(final List<Grupo> groups) {
        return this;
    }

    @Override
    public boolean isHighlighted() {
        return false;
    }

    @Override
    public AgendaRecord setHighlighted(final boolean highlighted) {
        return this;
    }

    @Override
    public List<String> getAddresses() {
        return null;
    }

    @Override
    public boolean isImageDirty() {
        return false;
    }

    @Override
    public AgendaRecord setImageDirty(final boolean imageDirty) {
        return this;
    }

    @Override
    public AgendaRecord setTemporaryImage(final String temporaryImage) {
        return this;
    }

    @Override
    public String getTemporaryImage() {
        return null;
    }

    @Override
    public AgendaRecord setThumbnail(final Icon thumbnail) {
        return this;
    }

    @Override
    public Icon getThumbnail() {
        return null;
    }
}
