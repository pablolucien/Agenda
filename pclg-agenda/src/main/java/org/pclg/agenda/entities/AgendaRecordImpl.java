package org.pclg.agenda.entities;

import org.apache.logging.log4j.Logger;
import org.pclg.agenda.AgendaDbException;
import org.pclg.agenda.TimestampAdapter;
import org.pclg.agenda.jdbc.FieldManagerHelper;
import org.pclg.agenda.jdbc.NoteHelper;
import org.pclg.agenda.jdbc.TelephoneHelper;
import org.pclg.log.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.pclg.tools.StringTools.equalEmptyOrBlank;

/**
 * @author El Coyote Cojo
 * @since 14-sep-2007 13:42:54
 */
@SuppressWarnings({ "ClassWithTooManyFields","SerializableHasSerializationMethods" })
@XmlRootElement( name = "AgendaRecord" )
//@XmlType( propOrder = { "firstname", "lastname", "sex", "country" } )
public final class AgendaRecordImpl implements AgendaRecord {
	private static final long serialVersionUID = 7813309582392710335L;
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

    @XmlAttribute
	private volatile int key;
	@XmlAttribute
	private volatile int version;
	//@XmlMimeType("text/plain")
	private Timestamp creationTimestamp;
	//@XmlMimeType("text/plain")
	private Timestamp updateTimestamp;
	@XmlTransient
	private volatile boolean highlighted;

	private String firstname;
	private String lastname;
	private String sex;

	@XmlElement
	@XmlJavaTypeAdapter(PaisAdapter.class)
	private Pais country = Pais.NULL_INSTANCE;

	private int day;
	private int month;
	private int year;
	private String mark;
	private boolean listTelephones;
	private boolean deleted;

	private String imagePath;
	private int versionImage;
	/** Used to store images loaded from vCards, etc. */
	private String temporaryImage;


	private final List<Telefono> telephones = new ArrayList<>(3);
	private int versionTelephone;
	@XmlTransient
	private boolean isTelephonePopulated;
	@XmlTransient
	private TelephoneHelper telephoneHelper;

	private String address;

	@XmlElementWrapper
	@XmlElement(name="addresses_item")
	private final List<String> addresses = new ArrayList<>(3);
	private int versionAddress;

	private final List<String> emails = new ArrayList<>(1);
	private int versionEmail;

	private String notes;
	private int versionNotes;
	@XmlTransient
	private boolean isNotesPopulated;
	@XmlTransient
	private NoteHelper noteHelper;

	private final List<Grupo> groups = new ArrayList<>();
	private int versionGroup;

	/**
	 * Usado para cuando se lee la imagen de una vCard (o otro sitio) y aun no
	 * se ha grabado en el sitio definitivo.
	 */
	@XmlTransient
	private volatile boolean imageDirty;

	@XmlTransient
	private ImageIcon thumbnail;	// NB: Using ImageIcon because "javax.swing.Icon es una interfaz y JAXB no puede manejar interfaces."

    @XmlTransient
   	private boolean isPopulated;
	@XmlTransient
	private final Set<FieldManagerHelper> otherFieldManagerHelpers;

    public AgendaRecordImpl() {
        otherFieldManagerHelpers = Collections.emptySet();
    }

    public AgendaRecordImpl(final TelephoneHelper telephoneHelper, final NoteHelper noteHelper,
			final Set<FieldManagerHelper> otherFieldManagerHelpers) {
		this.telephoneHelper = telephoneHelper;
		this.noteHelper = noteHelper;
		this.otherFieldManagerHelpers = otherFieldManagerHelpers;
	}

	@Override
	public void populate() {
		if (!isPopulated) {
			LOGGER.debug(String.format("Populating record %d/%d", key, version));
			otherFieldManagerHelpers.forEach(helper -> {
				try {
					helper.retrieve(this);
				} catch (final SQLException ex) {
					throw new AgendaDbException(ex);
				}
			});
			isPopulated = true;
		}
	}

    @Override
    public String toXML() {
		try {
			populate();
			/* init jaxb marshaler */
			final JAXBContext jaxbContext = JAXBContext.newInstance(AgendaRecordImpl.class);
			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			/* set this flag to true to format the output */
	        jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
			/* marshaling of java objects in xml (output to standard output) */
			final StringWriter stringWriter = new StringWriter();
	        jaxbMarshaller.marshal(this, stringWriter);
			return stringWriter.toString();
		} catch (final JAXBException ex) {
			LOGGER.error(LoggerFactory.ERROR_TAG, ex);
			return ex.getMessage();
		}
	}

	@Override
    public String XX_toXML() {
		String xml;
		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			final DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();

			// root elements
			final Document doc = docBuilder.newDocument();
			final Element rootElement = doc.createElement("AgendaRecord");
			rootElement.setAttribute("key", String.valueOf(key));
			rootElement.setAttribute("version", String.valueOf(version));
			doc.appendChild(rootElement);

			// Tiene sentido hacer esto por refexi�n en vez de
			// currarmelo una vez y hacerlo a tiempo de desarrollo?
			// O quiz� mejor hacer un util que lo haga?
			final Class<? extends AgendaRecord> recordClass = getClass();
			final Field[] declaredFields = recordClass.getDeclaredFields();
			Arrays.sort(declaredFields, Comparator.comparing(Field::getName));
			for (final Field field : declaredFields) {
				final int modifiers = field.getModifiers();
				if (Modifier.isVolatile(modifiers)
						|| Modifier.isStatic(modifiers)	|| Modifier.isTransient(modifiers)) {
					continue;
				}
				final String name = field.getName();
				final Element element = doc.createElement(name);
				final Object value = field.get(this);
				if (value instanceof List) {
					rootElement.appendChild(listToXML(doc,
							(List<Object>) value, name));
				} else {
					if (value != null) {
						// FIXME: Ver que pasa con las cosas que no se pueden
						// convertir en String
						element.appendChild(doc.createTextNode(String
								.valueOf(value)));
					}
					rootElement.appendChild(element);
				}
			}

			/*
			 * // firstname elements Element firstname = doc.createElement("firstname");
			 * firstname
			 * .appendChild(doc.createTextNode(String.valueOf(this.firstname)));
			 * rootElement.appendChild(firstname);
			 * 
			 * // lastname elements Element lastname =
			 * doc.createElement("lastname");
			 * lastname.appendChild(doc.createTextNode
			 * (String.valueOf(this.lastname)));
			 * rootElement.appendChild(lastname);
			 * 
			 * rootElement.appendChild(listToXML(doc, telephones, "telefono"));
			 * rootElement.appendChild(listToXML(doc, groups, "grupo"));
			 */
			// write the content into xml stream
			final TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(doc);
			final StringWriter stringWriter = new StringWriter();
			final StreamResult result = new StreamResult(stringWriter);
			transformer.transform(source, result);
			xml = stringWriter.toString();
		} catch (final ParserConfigurationException | TransformerException | IllegalAccessException ex) {
			LOGGER.error(LoggerFactory.ERROR_TAG, ex);
			xml = ex.getMessage();
		}

		return xml;
	}

//	/**
//	 * Returns an XML representation of this list.
//	 * @return an XML representation of this list.
//	 */
//	private <T> Node dateToXML(final Document doc, final Date date, final String firstname) {
//		final Element element = doc.createElement(firstname);
//		element.setAttribute("class", Date.class.toString());
//		element.setAttribute("format", DATE_FORMAT);
//		element.appendChild(doc.createTextNode(dateFormatStd.format(date)));
//		return element;
//	}

	/**
	 * Returns an XML representation of this list.
	 * 
	 * @return an XML representation of this list.
	 */
	private static <T> Node listToXML(final Document doc, final List<T> list,
			final String nombre) {
		final Element root = doc.createElement(nombre);
		for (final T t : list) {
			final Element hoja = doc.createElement(nombre + "_item");
			hoja.appendChild(doc.createTextNode(String.valueOf(t)));
			root.appendChild(hoja);
		}

		return root;
	}

	@Override
    public boolean equalsTelephones(final AgendaRecord record) {
		final int size = telephones.size();
		final List<Telefono> recordTelephones = record.getTelephones();
		if (size != recordTelephones.size()) {
			return false;
		}
		for (int ii = 0; ii < size; ii++) {
			if (!telephones.get(ii).equals(recordTelephones.get(ii))) {
				return false;
			}
		}
		return true;
	}

	@Override
    public boolean equalsGroups(final AgendaRecord record) {
		final int size = groups.size();
		final List<Grupo> recordGroups = record.getGroups();
		if (size != recordGroups.size()) {
			return false;
		}
		final Grupo[] misGrupos = groups.toArray(new Grupo[size]);
		final Grupo[] susGrupos = recordGroups.toArray(new Grupo[size]);
		Arrays.sort(misGrupos, Grupo.GROUP_COMPARATOR);
		Arrays.sort(susGrupos, Grupo.GROUP_COMPARATOR);
		return Arrays.equals(misGrupos, susGrupos);
	}

	@Override
    public boolean equalsAddresses(final AgendaRecord record) {
		return equalEmptyOrBlank(address, record.getAddress());
	}

	@Override
    public boolean equalsEmails(final AgendaRecord record) {
        final int size = emails.size();
		final List<String> recordEmails = record.getEmails();
		if (size != recordEmails.size()) {
            return false;
        }
        for (int ii = 0; ii < size; ii++) {
            if (!equalEmptyOrBlank(emails.get(ii), recordEmails.get(ii))) {
                return false;
            }
        }
        return true;
	}

	@Override
    public boolean equalsNotes(final AgendaRecord record) {
		return equalEmptyOrBlank(notes, record.getNotes());
	}

	@Override
    public boolean equalsImages(final AgendaRecord record) {
		return equalEmptyOrBlank(imagePath, record.getImagePath());
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final AgendaRecord record = (AgendaRecord) o;

		return deleted == record.isDeleted()
			&& listTelephones == record.isListTelephones()
			&& day == record.getDay()
			&& month == record.getMonth()
			&& year == record.getYear()
			&& country.equals(record.getCountry())
			&& equalEmptyOrBlank(lastname, record.getLastname())
			&& equalEmptyOrBlank(mark, record.getMark())
			&& equalEmptyOrBlank(firstname, record.getFirstname())
			&& equalEmptyOrBlank(sex, record.getSex())
			&& equalsAddresses(record)
			&& equalsEmails(record)
			&& equalsImages(record)
			&& equalsNotes(record)
			&& equalsTelephones(record)
			&& equalsGroups(record);
	}

	@Override
	public int hashCode() {
		int result = firstname != null ? firstname.hashCode() : 0;
		result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
		result = 31 * result + (address != null ? address.hashCode() : 0);
		result = 31 * result + (imagePath != null ? imagePath.hashCode() : 0);
		result = 31 * result + (sex != null ? sex.hashCode() : 0);
		result = 31 * result + (country != null ? country.hashCode() : 0);
		result = 31 * result + emails.hashCode();
		result = 31 * result + day;
		result = 31 * result + month;
		result = 31 * result + year;
		result = 31 * result + (mark != null ? mark.hashCode() : 0);
		result = 31 * result + (listTelephones ? 1 : 0);
		result = 31 * result + (deleted ? 1 : 0);
		result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + telephones.hashCode();
        result = 31 * result + groups.hashCode();
		return result;
	}

	/**
	 * Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that "textually represents"
	 * this object. The result should be a concise but informative
	 * representation that is easy for a person to read. It is recommended that
	 * all subclasses override this method. The <code>toString</code> method for
	 * class <code>Object</code> returns a string consisting of the name of the
	 * class of which the object is an instance, the at-sign character `
	 * <code>@</code>', and the unsigned hexadecimal representation of the hash
	 * code of the object. In other words, this method returns a string equal to
	 * the value of: <blockquote>
	 * 
	 * <pre>
	 * getClass().getName() + '@' + Integer.toHexString(hashCode())
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return "AgendaRecord{" +
				"key='" + key + '\'' +
				", version='" + version + '\'' +
				", firstname=" + firstname +
				", lastname=" + lastname +
				", sex='" + sex + '\'' +
				", country='" + country + '\'' +
				", listTelephones=" + listTelephones +
				", mark='" + mark + '\'' +
				", address=" + address +
				", updateTimestamp=" + updateTimestamp +
				", imagePath=" + imagePath +
				", telephones=" + telephones +
				", groups=" + groups +
				'}';
	}

	@Override
    @XmlTransient
	public int getKey() {
		return key;
	}

	@Override
    public AgendaRecord setKey(final int key) {
		this.key = key;
		return this;
	}

	@Override
    @XmlTransient
	public int getVersion() {
		return version;
	}

	@Override
    public AgendaRecord setVersion(final int version) {
		this.version = version;
		return this;
	}

	@Override
    @XmlTransient
	public Pais getCountry() {
		return country;
	}

	@Override
    public AgendaRecord setCountry(final Pais country) {
		this.country = country;
		return this;
	}

	@Override
    public int getVersionNotes() {
		return versionNotes;
	}

	@Override
    public AgendaRecord setVersionNotes(final int versionNotes) {
		this.versionNotes = versionNotes;
		return this;
	}

	@Override
    public int getVersionGroup() {
		return versionGroup;
	}

	@Override
    public AgendaRecord setVersionGroup(final int versionGroup) {
		this.versionGroup = versionGroup;
		return this;
	}

    @Override
    public String getFirstname() {
        return firstname;
    }

    @Override
    public AgendaRecord setFirstname(final String firstname) {
        this.firstname = firstname;
        return this;
    }

    @Override
    public String getLastname() {
        return lastname;
    }

    @Override
    public AgendaRecord setLastname(final String lastname) {
        this.lastname = lastname;
        return this;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public AgendaRecord setImagePath(final String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    @Override
    public int getVersionImage() {
        return versionImage;
    }

    @Override
    public AgendaRecord setVersionImage(final int versionImage) {
        this.versionImage = versionImage;
        return this;
    }

    @Override
    public int getVersionTelephone() {
        return versionTelephone;
    }

    @Override
    public AgendaRecord setVersionTelephone(final int versionTelephone) {
        this.versionTelephone = versionTelephone;
        return this;
    }

	@XmlElementWrapper
	@XmlElement(name="telefonos_item")
	@XmlJavaTypeAdapter(TelefonoAdapter.class)
    @Override
    public List<Telefono> getTelephones() {
		populateTelephones();
        return Collections.unmodifiableList(telephones);
    }

	private void populateTelephones() {
		if (!isTelephonePopulated && telephoneHelper != null) {
			LOGGER.debug(String.format("Populating telephones for record %d/%d", key, version));
			try {
				telephoneHelper.retrieve(this);
				isTelephonePopulated = true;
			} catch (final SQLException ex) {
				throw new AgendaDbException(ex);
			}
		}
	}

	@Override
    public AgendaRecord setTelephones(final List<Telefono> telephones) {
        this.telephones.clear();
        this.telephones.addAll(telephones);
        return this;
    }

    @Override
    public int getVersionAddress() {
        return versionAddress;
    }

    @Override
    public AgendaRecord setVersionAddress(final int versionDireccion) {
        this.versionAddress = versionDireccion;
        return this;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public AgendaRecord setAddress(final String address) {
        this.address = address;
        return this;
    }

    @Override
    public int getVersionEmail() {
        return versionEmail;
    }

    @Override
    public AgendaRecord setVersionEmail(final int versionEmail) {
        this.versionEmail = versionEmail;
        return this;
    }

	@XmlElementWrapper
	@XmlElement(name="emails_item")
    @Override
    public List<String> getEmails() {
        return Collections.unmodifiableList(emails);
    }

    @Override
   	public AgendaRecord addEmail(final String email) {
   		emails.add(email);
   		return this;
   	}

    @Override
    public AgendaRecord setEmails(final List<String> emails) {
        this.emails.clear();
        this.emails.addAll(emails);
        return this;
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public AgendaRecord setDay(final int day) {
        this.day = day;
        return this;
    }

    @Override
    public int getMonth() {
        return month;
    }

    @Override
    public AgendaRecord setMonth(final int month) {
        this.month = month;
        return this;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public AgendaRecord setYear(final int year) {
        this.year = year;
        return this;
    }

    @Override
    public String getMark() {
        return mark;
    }

    @Override
    public AgendaRecord setMark(final String mark) {
        this.mark = mark;
        return this;
    }

    @Override
    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

	@Override
    @XmlJavaTypeAdapter( TimestampAdapter.class )
    public AgendaRecord setUpdateTimestamp(final Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
        return this;
    }

    @Override
    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

	@Override
    @XmlJavaTypeAdapter( TimestampAdapter.class )
    public AgendaRecord setCreationTimestamp(final Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    @Override
    public boolean isListTelephones() {
        return listTelephones;
    }

    @Override
    public AgendaRecord setListTelephones(final boolean listar) {
        this.listTelephones = listar;
        return this;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public AgendaRecord setDeleted(final boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    @Override
    public String getNotes() {
        populateNotes();
        return notes;
    }

	private void populateNotes() {
		if (!isNotesPopulated && noteHelper != null) {
			LOGGER.debug(String.format("Populating notes for record %d/%d", key, version));
			try {
				noteHelper.retrieve(this);
				isNotesPopulated = true;
			} catch (final SQLException ex) {
				throw new AgendaDbException(ex);
			}
		}
	}

    @Override
    public AgendaRecord setNotes(final String notes) {
        this.notes = notes;
        return this;
	}

	@Override
    public String getSex() {
		return sex;
	}

	@Override
    public AgendaRecord setSex(final String sex) {
		this.sex = sex;
		return this;
	}

	@XmlElementWrapper
	@XmlElement(name="grupos_item")
	@XmlJavaTypeAdapter(GrupoAdapter.class)
	@Override
    public List<Grupo> getGroups() {
		return Collections.unmodifiableList(groups);
	}

    @Override
    public AgendaRecord setGroups(final List<Grupo> groups) {
	    this.groups.clear();
	    this.groups.addAll(groups);
        return this;
    }

    @Override
    public boolean isHighlighted() {
		return highlighted;
	}

	@Override
    public AgendaRecord setHighlighted(final boolean highlighted) {
		this.highlighted = highlighted;
		return this;
    }

	@Override
    public List<String> getAddresses() {
		return Collections.unmodifiableList(addresses);
    }

	@Override
    public boolean isImageDirty() {
		return imageDirty;
	}

	@Override
    public AgendaRecord setImageDirty(final boolean imageDirty) {
		this.imageDirty = imageDirty;
		return this;
	}

	@Override
	public AgendaRecord setTemporaryImage(final String temporaryImage) {
		this.temporaryImage = temporaryImage;
		return this;
	}

	@Override
	public String getTemporaryImage() {
		return temporaryImage;
	}

	@Override
	public AgendaRecord setThumbnail(final Icon thumbnail) {
		this.thumbnail = (ImageIcon) thumbnail;	// NB: Using ImageIcon because "javax.swing.Icon es una interfaz y JAXB no puede manejar interfaces."
		return this;
	}

	@Override
	public ImageIcon getThumbnail() {     	// NB: Using ImageIcon because "javax.swing.Icon es una interfaz y JAXB no puede manejar interfaces."
		return thumbnail;
	}
}
