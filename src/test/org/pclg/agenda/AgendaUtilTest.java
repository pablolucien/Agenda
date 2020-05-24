package org.pclg.agenda;

import org.apache.log4j.Logger;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.AgendaRecordImpl;
import org.pclg.agenda.entities.Grupo;
import org.pclg.agenda.entities.Pais;
import org.pclg.agenda.entities.Telefono;
import org.pclg.log.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;

/**
 * @author Pablo
 * @since 18/04/15 11:23
 */
public class AgendaUtilTest {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	@Test
	public void testVcards() throws IOException {
		final File dir = Files.createTempDirectory(null).toFile();
		dir.deleteOnExit();
		final File testFile = new File(dir, "kkk.vcf");
		testFile.deleteOnExit();
		if (testFile.exists() && !testFile.delete()) {
			LOGGER.warn("Couldn't delete " + testFile);
		}
        final AgendaRecord record = getAgendaRecord();
		AgendaUtil.saveVCard(record, dir);
		final List<AgendaRecord> agendaRecords = AgendaUtil.readVCards(testFile);
		assertEquals(1, agendaRecords.size());
		final AgendaRecord agendaRecord = agendaRecords.get(0);
		assertNotSame(record, agendaRecord);
		assertEquals(record, agendaRecord);
		// Clean the temporary image created by readVCards
		final String temporaryImagePath = agendaRecord.getTemporaryImage();
		assertNotNull(temporaryImagePath);
		final File imageFile = new File(temporaryImagePath);
		assertTrue(imageFile.exists());
		assertTrue(imageFile.delete());
	}

    private AgendaRecord getAgendaRecord() {
        final AgendaRecord record = new AgendaRecordImpl();
        record.setFirstname("kkk");
        record.setSex("E");
        record.setDay(11);
        record.setMonth(9);
        record.setYear(2001);
        record.addEmail("pepe@isv.com");
        record.addEmail("DonJos�@mail_provider.com");
        final List<Telefono> telephones = new ArrayList<>(2);
        telephones.add(new Telefono("34", "1234567", 0));     // usando 0 porque el lector de vCards no tiene el tipo (de Agenda)
        telephones.add(new Telefono("58", "9876543", 0));
        record.setTelephones(telephones);
        final List<Grupo> grupos = new ArrayList<>(2);
        grupos.add(new Grupo(-1, "Abeliano"));          // usando -1 porque el lector de vCards no tiene la clave
        grupos.add(new Grupo(-1, "De simetr�as"));
        record.setGroups(grupos);
        record.setMark("42");
        record.setAddress("Calle Luna, calle Sol");
        final String path = getClass().getResource("/unknown-man.jpg").getPath();
        record.setImagePath(path);
        record.setCountry(Pais.getUnknownCountry());
        return record;
    }

    @DataProvider(name = "testData")
	public static Object[][] testData() {
		return new Object[][] {
            // Current date 15 apr. 2015
            // {day, month, year, expected age, expected days},
            {14, 4, 1960, 	55,  20089},
            {15, 4, 1960, 	55,  20088},
            {16, 4, 1960, 	54,  20087},
            {15, 4, 1965, 	50,  18262},
            {15, 4, 1968, 	47,  17166},
            {15, 4, 1970, 	45,  16436},
            { 1, 5,    1, 2013, 735582},
            { 1, 1,    1, 2014, 735702},
            {14, 4, 2014, 	 1,    366},
            {15, 4, 2014, 	 1,    365},
            {16, 4, 2014, 	 0,    364},
            {14, 4, 2015, 	 0,      1},
            {15, 4, 2015, 	 0,      0},
            { 0, 0, 1973,   42,      0},
            { 0, 4, 1973,   42,      0},
            { 0, 5, 1973,   41,      0},
		};
	}

	@Test(dataProvider = "testData")
	public void testComputeAgeAndDays(final int day, final int month, final int year, final int expectedAge, final int expectedDays) {
		final Calendar rightNow = Calendar.getInstance();
		rightNow.set(Calendar.YEAR, 2015);
		rightNow.set(Calendar.MONTH, Calendar.APRIL);
		rightNow.set(Calendar.DAY_OF_MONTH, 15);
		rightNow.set(Calendar.MINUTE, 0);
		rightNow.set(Calendar.MINUTE, 0);
		rightNow.set(Calendar.SECOND, 0);
		rightNow.set(Calendar.MILLISECOND, 0);
		final AgendaUtil.AgeInfo ageInfo = AgendaUtil.computeAgeAndDays(day, month - 1, year, rightNow);
		assertEquals(ageInfo.getAge(), expectedAge);
		assertEquals(ageInfo.getDays(), expectedDays);
	}

	@Test(enabled = false)
	public void testDeleteDatabaseBackups() {
		AgendaUtil.deleteDatabaseBackups("/some path/_Agenda/agendaDB", 5);
	}
}
