package org.pclg.agenda.entities;

import org.apache.log4j.Logger;
import org.pclg.agenda.AgendaRecordUtil;
import org.pclg.log.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author El Coyote Cojo
 * @since 06-sep-2014
 */
public final class AgendaRecordTest {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final Grupo grupo1 = new Grupo(1, "Grupo 1");
    private final Grupo grupo42 = new Grupo(42, "Grupo 42");

    @BeforeClass
    public void setUp() {
        Grupo.add(grupo1);
        Grupo.add(grupo42);
    }

    private AgendaRecord getRecord() {
        final AgendaRecord record = new AgendaRecordImpl();
        final List<Telefono> telefonos = new ArrayList<>(2);
        telefonos.add(new Telefono("34", "123456789", 1));
        telefonos.add(new Telefono("34", "987654321", 2));
        record.setTelephones(telefonos);
        final List<String> direcciones = new ArrayList<>(2);
        direcciones.add("direccion 1");
        direcciones.add("direccion 2");
        record.setAddress(direcciones.stream().collect(Collectors.joining()));
        final List<String> emails = new ArrayList<>(2);
        emails.add("pepito@mirror.com");
        emails.add("pepito@domail.org");
        record.setEmails(emails);
        final List<Grupo> grupos = new ArrayList<>(2);
        grupos.add(grupo1);
        grupos.add(grupo42);
        record.setGroups(grupos);
        record.setKey(1)
            .setVersion(2).setCreationTimestamp(new Timestamp(0L))
            .setUpdateTimestamp(new Timestamp(new Date().getTime())).setHighlighted(true)
            .setFirstname("Pepito").setLastname("La Lagartija").setSex("M")
            .setCountry(Pais.NULL_INSTANCE).setDay(31).setMonth(1).setYear(1980).setMark("ZZ")
            .setListTelephones(true).setDeleted(true).setImagePath("c:/images/foto1.png").setVersionImage(1)
            .setVersionTelephone(1).setAddress("En un lugar de La Mancha").setVersionAddress(1)
            .setVersionEmail(1).setNotes("Este es un registro de pruebas").setVersionNotes(1)
            .setVersionGroup(1);

        return record;
    }

    @Test
    public void testEquals() {
        final AgendaRecord record1 = getRecord();
        assertTrue(record1.equals(record1));

        AgendaRecord record2 = getRecord();
        assertTrue(record1.equals(record2));

        record2.setVersionImage(2).setVersionTelephone(2).setVersionAddress(2).setVersionEmail(2)
                .setVersionNotes(2).setVersionGroup(2);
        assertTrue(record1.equals(record2));

        record2.setCreationTimestamp(null).setUpdateTimestamp(null);
        assertTrue(record1.equals(record2));

        record2.setFirstname("Luisito");
        assertFalse(record1.equals(record2));

        record2 = getRecord().setLastname("");
        assertFalse(record1.equals(record2));

        record2 = getRecord().setImagePath("");
        assertFalse(record1.equals(record2));

        record2 = getRecord();
        final List<Telefono> telefonoList = new ArrayList<>(4);
        record2.setTelephones(telefonoList);
        assertFalse(record1.equals(record2), "Should be different");
        telefonoList.add(new Telefono("34", "123456789", 1));
        record2.setTelephones(telefonoList);
        assertFalse(record1.equals(record2), "Should be different");
        telefonoList.add(new Telefono("34", "987654321", 2));
        record2.setTelephones(telefonoList);
        assertTrue(record1.equals(record2), "Should be equal");

        record2 = getRecord().setAddress("");
        assertFalse(record1.equals(record2));

        record2 = getRecord();
        record2.setEmails(new ArrayList<>(4));
        assertFalse(record1.equals(record2), "Should be different");

        record2 = getRecord();
        record2.setNotes("");
        assertFalse(record1.equals(record2), "Should be different");

        record2 = getRecord();
        final List<Grupo> grupos = new ArrayList<>(4);
        record2.setGroups(grupos);
        assertFalse(record1.equals(record2), "Should be different");
        grupos.add(new Grupo(1, "Grupo 1"));
        record2.setGroups(grupos);
        assertFalse(record1.equals(record2), "Should be different");
        grupos.add(new Grupo(42, "Grupo 42"));
        record2.setGroups(grupos);
        assertTrue(record1.equals(record2), "Should be equal");

        assertFalse(record1.equals(null), "Should be different");

        assertFalse(record1.equals("record2"), "Should be different");
    }

    @Test
    public void testHashcode() {
        final AgendaRecord record1 = getRecord();
        final AgendaRecord record2 = getRecord();
        assertEquals(record1.hashCode(), record2.hashCode());

        record2.setCreationTimestamp(null).setUpdateTimestamp(null).setVersionImage(2)
                .setVersionTelephone(2).setVersionAddress(2).setVersionEmail(2).setVersionNotes(2)
                .setVersionGroup(2);
        assertEquals(record1.hashCode(), record2.hashCode());
    }

    @Test
    public void testXML() {
        final AgendaRecord record1 = getRecord();
        final String xml = record1.toXML();
        LOGGER.debug(xml);
        try {
            final AgendaRecord record2 = AgendaRecordUtil.xml2AgendaRecord(xml);
            assertTrue(record1.equals(record2));
            record1.setMark("XX");
            assertFalse(record1.equals(record2));
        } catch (final Exception ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            fail(ex.toString());
        }
    }

    @DataProvider(name = "testFullNameData")
    public Object[][] testFullNameData() {
        return new Object[][] {
            {"Erg�e", "Vito", "Erg�e Vito"},
            {""     , "Vito", "Vito"      },
            {"   "  , "Vito", "Vito"      },
            {null   , "Vito", "Vito"      },
            {"Erg�e", ""    , "Erg�e"     },
            {"Erg�e", "    ", "Erg�e"     },
            {"Erg�e", null  , "Erg�e"     },
            {""     , ""    , ""          },
            {"  "   , "  "  , ""          },
            {null   , null  , ""          },
        };
    }

    @Test(dataProvider = "testFullNameData")
    public void testFullName(final String first, final String last, final String full) {
        final AgendaRecord agendaRecord = new AgendaRecordImpl();
        agendaRecord.setFirstname(first);
        agendaRecord.setLastname(last);
        assertEquals(agendaRecord.fullName(), full, "Shounl be equal");
    }
}
