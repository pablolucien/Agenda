package org.pclg.agenda;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;
import ezvcard.parameter.ImageType;
import ezvcard.property.Address;
import ezvcard.property.Birthday;
import ezvcard.property.Categories;
import ezvcard.property.Gender;
import ezvcard.property.Note;
import ezvcard.property.Organization;
import ezvcard.property.Photo;
import ezvcard.property.RawProperty;
import ezvcard.property.SimpleProperty;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import ezvcard.util.PartialDate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.AgendaRecordImpl;
import org.pclg.agenda.entities.Grupo;
import org.pclg.agenda.entities.Pais;
import org.pclg.agenda.entities.Telefono;
import org.pclg.gui.InfoPanel;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;
import org.pclg.tools.GUITools;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.StringTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.pclg.agenda.entities.Pais.getPaises;
import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * Premature optimization is the root of all evil.
 * ?Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 02-oct-2007 13:57:59
 */
public final class AgendaUtil {
    private static final String EXTENDED_F_ACTUALIZACION = "X-F-ACTUALIZACION";
    private static final String EXTENDED_F_CREACION = "X-F-CREACION";
    private static final String EXTENDED_LISTAR = "X-LISTAR";
    private static final String EXTENDED_IMAGEN = "X-IMAGEN";
    private static final String EXTENDED_MARCA = "X-MARCA";
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final int BUFFER_SIZE = 1024;

    private static final String COUNTRY_CODE_INDICATOR = "+";
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+(\\d+)\\D(.+)");
    private static final Collection<Pais> PAISES = getPaises();

    public static class AgeInfo {
        public static final AgeInfo NULL_AGE_INFO = new AgeInfo(-1, -1);
        private final int age;
        private final int days;

        AgeInfo(final int age, final int days) {
            this.age = age;
            this.days = days;
        }

        public int getAge() {
            return age;
        }

        public int getDays() {
            return days;
        }

        @Override
        public String toString() {
            return getClass().getName() + "{age = " + age + ", days = " + days + "}";
        }
    }

    private AgendaUtil() {
    }

    /**
     * Computes the full years and days elapsed since the provided date until the provided reference date.
     *
     * @param day           the day of the date (1-31)
     * @param month         the month of the date (0-11) 0 based.
     * @param year          the year.
     * @param referenceDate the reference date.
     * @return an AgeInfo object with the information.
     */
    static AgeInfo computeAgeAndDays(int day, int month, final int year,
        final Calendar referenceDate) {
        final AgeInfo info;
        boolean inexact = false;
        if (day < 1) {
            day = 1;
            inexact = true;
        }
        if (month < 0) {
            month = 0;
            inexact = true;
        }
        if (day > 31 || month > 11 || year == 0) {
            info = AgeInfo.NULL_AGE_INFO;
        } else {
            final Period period = Period.between(LocalDate.of(year, month + 1, day),
                LocalDate.of(referenceDate.get(Calendar.YEAR), referenceDate.get(Calendar.MONTH) + 1, referenceDate.get(Calendar.DAY_OF_MONTH)));
            final Duration duration = Duration.between(LocalDateTime.of(year, month + 1, day, 0, 0),
                LocalDateTime.of(referenceDate.get(Calendar.YEAR), referenceDate.get(Calendar.MONTH) + 1, referenceDate.get(Calendar.DAY_OF_MONTH), 0, 0));
            info = new AgeInfo(period.getYears(), inexact ? 0 : (int) duration.toDays());
        }

        return info;
    }

    /**
     * Computes the full years and days elapsed since the provided date until the current day.
     *
     * @param day   the day of the date (1-31)
     * @param month the month of the date (0-11) 0 based.
     * @param year  the year.
     * @return an AgeInfo object with the information.
     */
    public static AgeInfo computeAgeAndDays(final int day, final int month, final int year) {
        final Calendar rightNow = Calendar.getInstance();
        return computeAgeAndDays(day, month, year, rightNow);
    }

    /**
     * Loguea el stack trace de una SQLException y devuelve el mensaje
     * de la causa primigenia.
     *
     * @param e la SQLException en cuestion.
     * @return el mensaje de la causa primigenia.
     */
    public static String printSQLError(final SQLException e) {
        SQLException ex = e;
        final StringBuilder builder = new StringBuilder(BUFFER_SIZE);
        while (ex != null) {
            builder.setLength(0);
            builder.append(LoggerFactory.getRandomErrorMessage()).append("\n\n")
                .append("ex.getSQLState() = ").append(ex.getSQLState())
                .append(" ex.getErrorCode() = ").append(ex.getErrorCode())
                .append(". ").append(ex.getLocalizedMessage()).append('\n');
            LOGGER.log(Level.ERROR, builder.toString(), ex);
            ex = ex.getNextException();
        }
        return builder.toString();
    }

    public static String concatenarTelefonos(final List<Telefono> telefonos,
        final String mask) {
        final StringBuilder builder =
            new StringBuilder(AgendaRecord.TELEPHONE_FIELD_LEN * 3);
        telefonos.stream().filter(Objects::nonNull)
            .forEach(telefono -> builder.append(GUITools.formatNumberWithMask(
                telefono.getNumero(), mask)).append(" - "));
        final int length = builder.length();
        if (length > 3 && builder.substring(length - 3).equals(" - ")) {
            builder.delete(length - 3, length);
        }
        return builder.toString();
    }

    public static void saveVCard(final AgendaRecord record, final File dir) throws IOException {
        final VCard vcard = new VCard();
        record.populate();

        final StructuredName name = new StructuredName();
        name.setGiven(record.getFirstname());
        name.setFamily(record.getLastname());
        vcard.setStructuredName(name);

        for (final Telefono telefono : record.getTelephones()) {
            String number = telefono.getNumber();
            if (!number.isEmpty() && !number.startsWith(COUNTRY_CODE_INDICATOR)) {
                number = COUNTRY_CODE_INDICATOR + telefono.getCountryPrefix() + ' ' + number;
            }
            vcard.addTelephoneNumber(number, Telefono.getTypes());
        }

        final List<String> emails = record.getEmails();
        if (emails != null) {
            emails.forEach(email -> vcard.addEmail(email.trim()));
        }

        final Address address = new Address();
        address.setStreetAddress(record.getAddress());
        final Pais pais = record.getCountry();
        if (pais != null) {
            address.setCountry(pais.getCountryName());
        }
        vcard.addAddress(address);

        final PartialDate.Builder builder = PartialDate.builder();
        if (record.getYear() != 0) {
            builder.year(Integer.valueOf(record.getYear()));
        }
        if (record.getMonth() != 0) {
            builder.month(Integer.valueOf(record.getMonth()));
        }
        if (record.getDay() != 0) {
            builder.date(Integer.valueOf(record.getDay()));
        }
        vcard.setBirthday(new Birthday(builder.build()));

        final String sexo = record.getSex();
        final Gender gender = "F".equals(sexo) ? Gender.female() :
            "M".equals(sexo) ? Gender.male() :
                "E".equals(sexo) ? Gender.none() :
                    Gender.unknown();
        vcard.setGender(gender);

        if (!isEmptyOrBlank(record.getNotes())) {
            vcard.addNote(new Note(record.getNotes()));
        }

        final Categories categories = new Categories();
        for (final Grupo grupo : record.getGroups()) {
            categories.getValues().add(grupo.getNombre());
        }
        vcard.setCategories(categories);
        vcard.setOrganization(record.getGroups().stream().map(Grupo::getNombre).toList()
            .toArray(StringTools.EMPTY_STRING_ARRAY));

        if (!isEmptyOrBlank(record.getMark())) {
            vcard.addExtendedProperty(EXTENDED_MARCA, record.getMark());
        }

        final String imagePath = record.getImagePath();
        if (!isEmptyOrBlank(imagePath)) {
            vcard.addExtendedProperty(EXTENDED_IMAGEN, imagePath);
            final File file = new File(imagePath);
            if (file.exists()) {
                final String ext = FileTools.splittName(imagePath).extension.toLowerCase();
                final ImageType type = ImageType.get(null, null, ext);
                final Photo photo = new Photo(file.toPath(), type);
                vcard.addPhoto(photo);
            } else {
                LOGGER.warn(String.format("The file [%s] doesn't exist.", file));
            }
        }

        vcard.addExtendedProperty(EXTENDED_LISTAR, record.isListTelephones() ? "1" : "0");

        final Timestamp fechaCreacion = record.getCreationTimestamp();
        if (fechaCreacion != null) {
            vcard.addExtendedProperty(EXTENDED_F_CREACION, String.valueOf(fechaCreacion));
        }

        final Timestamp fechaActualizacion = record.getUpdateTimestamp();
        if (fechaActualizacion != null) {
            vcard.addExtendedProperty(EXTENDED_F_ACTUALIZACION, String.valueOf(fechaActualizacion));
        }

        final VCardVersion version = VCardVersion.V4_0;    // Para partial date y gender.
        int counter = 0;
        final String fileName = FileTools.sanitizeWindowsFilename(record.fullName(), "_").trim();
        File file = new File(dir, fileName + ".vcf");
        while (file.exists()) {
            file = new File(dir, fileName + '_' + counter++ + ".vcf");
        }
        try (final VCardWriter writer = new VCardWriter(new FileWriter(file, true),  version)) {
            writer.write(vcard);
        } catch (final IOException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
    }

    /**
     * Lee los datos de un fichero de VCards y los agrega a una lista de
     * AgendaRecord.
     *
     * @param file el fichero de dónde leer.
     * @return una lista de AgendaRecord.
     */
    public static List<AgendaRecord> readVCards(final File file) {
        final List<AgendaRecord> records = new ArrayList<>();
        try {
            final List<VCard> vcards;
            try (final InputStreamReader reader = new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8)) {
                vcards = Ezvcard.parse(reader).all();
            }
            for (final VCard vcard : vcards) {
                final AgendaRecord record = new AgendaRecordImpl();
                readName(vcard, record);
                readEmails(vcard, record);
                readAddresses(vcard, record);
                readTelephones(vcard, record);
                readBirthday(vcard, record);
                readGender(vcard, record);
                readNotas(vcard, record);
                readCategories(vcard, record);
                readOrganizations(vcard, record);
                readMarca(vcard, record);
                readImagen(vcard, record);
                readListar(vcard, record);
                readPhotos(vcard, record);
//                readFechaCreacion(vcard, record);
//                readFechaActualizacion(vcard, record);
                adjustCountry(record);
                records.add(record);
            }
        } catch (final IOException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
        return records;
    }

    private static void adjustCountry(final AgendaRecord record) {
        if (Pais.getUnknownCountry().equals(record.getCountry())) {
            final List<String> countryPrefixes = record.getTelephones().stream().map(Telefono::getCountryPrefix).distinct().toList();
            if (countryPrefixes.size() == 1) {
                record.setCountry(Pais.fromCountryCode(countryPrefixes.getFirst()));
            }
        }
    }

    /**
     * Lee el nombre de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readName(final VCard vcard, final AgendaRecord record) {
        final StructuredName structuredName = vcard.getStructuredName();
        if (structuredName != null) {
            record.setLastname(structuredName.getFamily());
            final StringBuilder givenName =
                new StringBuilder(structuredName.getGiven());
            for (final String more : structuredName.getAdditionalNames()) {
                givenName.append(' ').append(more);
            }
            if (givenName.length() > AgendaRecord.NAME_FIELD_LEN) {
                givenName.setLength(AgendaRecord.NAME_FIELD_LEN);
            }
            record.setFirstname(givenName.toString());
        }
    }

    /**
     * Lee los teléfonos de una VCard y los agrega al AgendaRecord.
     * WARNING: readTelephones() usa record.getCountry(), de modo que
     * debe ser llamado después de readAddresses() o al menos después de que
     * record.pais tenga un valor.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readTelephones(final VCard vcard, final AgendaRecord record) {
        final List<Telephone> telephoneNumbers = vcard.getTelephoneNumbers();
        if (telephoneNumbers != null) {
            final Pais pais = record.getCountry();
            if (pais == null) {
                readAddresses(vcard, record);
            }
            assert pais != null;
            final List<Telefono> telefonos = new ArrayList<>(telephoneNumbers.size());
            for (final Telephone telephone : telephoneNumbers) {
                final String telephoneText = telephone.getText();
                if (!actuallyContained(telephoneText, telefonos)) {
                    final Matcher matcher = PHONE_PATTERN.matcher(telephoneText);
                    if (matcher.matches()) {
                        telefonos.add(new Telefono(matcher.group(1), matcher.group(2), 0));
                    } else {
                        telefonos.add(parseTelefono(telephoneText));
                    }
                }
                //	telephone.getTypes().toString();
            }
            record.setTelephones(telefonos);
        }
    }

    // TODO: Add tests
    private static Telefono parseTelefono(final String telephoneText) {
        if (telephoneText.startsWith(COUNTRY_CODE_INDICATOR)) {
            final String telephoneWithoutCountryCodeIndicator = telephoneText.substring(1);
            for (final Pais pais : PAISES) {
                final String countryCode = pais.getCountryCode();
                if (isEmptyOrBlank(countryCode)) {
                    continue;
                }
                if (telephoneWithoutCountryCodeIndicator.startsWith(countryCode)) {
                    return new Telefono(countryCode, telephoneWithoutCountryCodeIndicator.substring(countryCode.length()), 0);
                }
            }
        }
        LOGGER.warn("Incorrect telephone found: " + telephoneText);
        return new Telefono("0", telephoneText, 0);
    }

    /**
     * Verifica si un telefono está realmente contenido en la lista aunque separado en país y número formateado.
     */
    private static boolean actuallyContained(final String telephoneText, final List<Telefono> telefonos) {
        for (final Telefono telefono : telefonos) {
            final String containedText = telefono.getCountryPrefix() + telefono.getNumber();
            if (containedText.equals(telephoneText.replaceAll("\\D", ""))) {
                LOGGER.debug(String.format("Coinciden %s y %s", containedText, telephoneText));
                return true;
            }
        }
        return false;
    }

    /**
     * Lee los emails de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readEmails(final VCard vcard, final AgendaRecord record) {
        record.setEmails(vcard.getEmails().stream().map(SimpleProperty::getValue).collect(Collectors.toList()));
    }

    /**
     * Lee las fotos de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readPhotos(final VCard vcard, final AgendaRecord record) {
        final List<Photo> photos = vcard.getPhotos();
        if (!photos.isEmpty()) {
            // Si hay varias fotos predomina la última (todas son escritas en HD)
            for (final Photo photo : photos) {
                final byte[] data = photo.getData();
                try {
                    final String type = photo.getContentType().getExtension();
                    final String suffix = type == null ? "unknown_image_type" : type;
                    final File photoFile = File.createTempFile("Agenda_", "." + suffix);
                    FileTools.writeToFile(data, photoFile, true);
                    record.setTemporaryImage(photoFile.getAbsolutePath());
                    record.setImageDirty(true);
                } catch (final IOException ex) {
                    LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                }
            }
        }
    }

    /**
     * Lee las direcciones de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readAddresses(final VCard vcard, final AgendaRecord record) {
        record.setCountry(Pais.getUnknownCountry());
        final int length;
        final StringBuilder addresses = new StringBuilder(AgendaRecord.ADDRESS_FIELD_LEN);
        for (final Address address : vcard.getAddresses()) {
            final String streetAddress = address.getStreetAddress();
            if (streetAddress != null) {
                addresses.append(streetAddress).append('\n');
            }
            final List<String> extendedAddresses = address.getExtendedAddresses();
            if (extendedAddresses != null) {
                extendedAddresses.forEach(addr -> addresses.append(addr).append('\n'));
            }
            record.setCountry(Pais.getValue(address.getCountry()));    // Solo vale uno
        }
        length = addresses.length();
        if (length > 0) {
            addresses.deleteCharAt(length - 1);
        }
        record.setAddress(addresses.toString());
    }

    /**
     * Lee el cumpleaños de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readBirthday(final VCard vcard, final AgendaRecord record) {
        final Birthday birthday = vcard.getBirthday();
        if (birthday != null) {
            final Temporal date = birthday.getDate();
            if (date instanceof final LocalDate localDate) {
                record.setYear(localDate.getYear())
                    .setMonth(localDate.getMonthValue())
                    .setDay(localDate.getDayOfMonth());
            } else {
                final PartialDate partialDate = birthday.getPartialDate();
                if (partialDate != null) {
                    final Integer year = partialDate.getYear();
                    final Integer month = partialDate.getMonth();
                    final Integer day = partialDate.getDate();
                    record.setYear(year == null ? 0 : year.intValue())
                        .setMonth(month == null ? 0 : month.intValue())
                        .setDay(day == null ? 0 : day.intValue());
                }
            }
        }
    }

    /**
     * Lee la opción de listar de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readListar(final VCard vcard, final AgendaRecord record) {
        final RawProperty listar = vcard.getExtendedProperty(EXTENDED_LISTAR);
        record.setListTelephones(listar == null || "1".equals(listar.getValue()));
    }

    /**
     * Lee la fecha de creación de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readFechaCreacion(final VCard vcard, final AgendaRecord record) {
        final RawProperty fechaCreacion = vcard.getExtendedProperty(EXTENDED_F_CREACION);
        if (fechaCreacion != null) {
            record.setCreationTimestamp(Timestamp.valueOf(fechaCreacion.getValue()));
        }
    }

    /**
     * Lee la fecha de actualizacion de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readFechaActualizacion(final VCard vcard, final AgendaRecord record) {
        final RawProperty fechaActualizacion = vcard.getExtendedProperty(EXTENDED_F_ACTUALIZACION);
        if (fechaActualizacion != null) {
            record.setUpdateTimestamp(Timestamp.valueOf(fechaActualizacion.getValue()));
        }
    }

    /**
     * Lee la imagen de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readImagen(final VCard vcard, final AgendaRecord record) {
        final RawProperty imagen = vcard.getExtendedProperty(EXTENDED_IMAGEN);
        if (imagen != null) {
            record.setImagePath(imagen.getValue());
        }
    }

    /**
     * Lee la marca de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readMarca(final VCard vcard, final AgendaRecord record) {
        final RawProperty marca = vcard.getExtendedProperty(EXTENDED_MARCA);
        if (marca != null) {
            record.setMark(marca.getValue());
        }
    }

    /**
     * Lee el sexo de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readGender(final VCard vcard, final AgendaRecord record) {
        final Gender gender = vcard.getGender();
        if (gender == null) {
            record.setSex("?");
        } else {
            record.setSex(gender.isFemale() ? "F" :
                gender.isMale() ? "M" : gender.isNone() ? "E" : "?");
        }
    }

    /**
     * Lee las notas de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readNotas(final VCard vcard, final AgendaRecord record) {
        final int length;
        final StringBuilder notas = new StringBuilder(AgendaRecord.NOTES_FIELD_LEN);
        for (final Note note : vcard.getNotes()) {
            notas.append(note.getValue()).append('\n');
        }
        length = notas.length();
        if (length > 0) {
            notas.deleteCharAt(length - 1);
        }
        record.setNotes(notas.toString());
    }

    /**
     * Lee los grupos de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readCategories(final VCard vcard, final AgendaRecord record) {
        final Categories categories = vcard.getCategories();
        if (categories != null) {
            final List<String> values = categories.getValues();
            setGroups(record, values);
        }
    }

    /**
     * Lee los grupos de una VCard y los agrega al AgendaRecord.
     *
     * @param vcard  de dónde leer los datos.
     * @param record dónde poner los datos.
     */
    private static void readOrganizations(final VCard vcard, final AgendaRecord record) {
        final List<Organization> organizations = vcard.getOrganizations();
        if (organizations != null) {
            organizations.forEach(organization -> {
                final List<String> values = organization.getValues();
                setGroups(record, values);
            });
        }
    }

    private static void setGroups(final AgendaRecord record, final List<String> values) {
        final List<Grupo> grupos;
        final List<Grupo> existingGroups = record.getGroups();
        if (existingGroups == null) {
            grupos = new ArrayList<>(values.size());
        } else {
            grupos = new ArrayList<>(existingGroups);
        }
        for (final String value : values) {
            Grupo grupo = Grupo.valueOf(value);
            if (grupo == null) {
                grupo = new Grupo(Grupo.INVALID_KEY, value);
                Grupo.add(grupo);
            }
            if (!grupos.contains(grupo)) {
                grupos.add(grupo);
            }
        }
        record.setGroups(grupos);
    }

    /**
     * Copia la base de datos dejando la antigua como backup.
     *
     * @param appProperties para obtener los mensajes i18n.
     * @param srcPath       de dónde copiar.
     * @param tgtPath       a dónde copiar.
     * @param dbDate        la fecha de la bd antigua (para el nombre a usar).
     * @throws IOException  si se producen errores en la copia.
     */
    public static void copyDatabase(final Properties appProperties,
        final String srcPath, final String tgtPath, final Date dbDate)
        throws IOException {
        final File dest = new File(tgtPath);
        final SimpleDateFormat format = new SimpleDateFormat("_yyyy-MM-dd_HH-mm-ss");
        final File bkpFile = new File(tgtPath + format.format(dbDate));
        final String msgRename = MessageFormat.format(PropertiesHelper.getStringFromProperties(appProperties,
            "AgendaGUI.InfoPanel.msgRename"), tgtPath, bkpFile);
        LOGGER.info(msgRename);
        final InfoPanel infoPanel = new InfoPanel(PropertiesHelper.getStringFromProperties(appProperties,
            "AgendaGUI.InfoPanel.title"), msgRename);
        infoPanel.setVisible(true);
        if (!dest.renameTo(bkpFile)) {
            final String msgError = MessageFormat.format(PropertiesHelper.getStringFromProperties(appProperties,
                "AgendaGUI.InfoPanel.msgError"), tgtPath, bkpFile);
            LOGGER.warn(msgError);
        }
        final String msgCopy = MessageFormat.format(PropertiesHelper.getStringFromProperties(appProperties,
            "AgendaGUI.InfoPanel.msgCopy"), srcPath, tgtPath);
        LOGGER.info(msgCopy);
        infoPanel.setMessage(msgCopy);
        FileTools.copyFolder(new File(srcPath), dest);
        infoPanel.setVisible(false);
    }

    /**
     * Elimina los backups más antiguos, dejando a lo sumo la cantidad pedida.
     *
     * @param tgtPath          adónde copiar.
     * @param maxBackupHistory la fecha de la bd antigua (para el nombre a usar).
     */
    public static void deleteDatabaseBackups(final String tgtPath, final int maxBackupHistory) {
        final File tgtFile = new File(tgtPath);
        final File baseDir = tgtFile.getParentFile();
        final String baseName = tgtFile.getName();
        final String regex = baseName + "_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}";     // _yyyy-MM-dd_HH-mm-ss
        final Pattern pattern = Pattern.compile(regex);
        final File[] files = baseDir.listFiles(f -> pattern.matcher(f.getName()).matches());
        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Backups existentes -> ");
                for (final File file : files) {
                    LOGGER.debug(file);
                }
            }
            LOGGER.warn("A borrar los siguientes backups -> ");
            for (int ii = 0; ii < files.length - maxBackupHistory; ii++) {
                LOGGER.warn(files[ii]);
                FileTools.delTree(files[ii], true, true);
            }
        }
    }
}
