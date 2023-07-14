package org.pclg.tools;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.assertEquals;

public class DateToolsTest {

    @Test
    public void testGuessDate() throws Exception {

    }

    @Test
    public void testParseDate() throws Exception {

    }

    @DataProvider
    public static Object[][] currentQuarterComputationProvider() {
        return new Object[][]{
            {LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 31)},
            {LocalDate.of(2020, 2, 15), LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 31)},
            {LocalDate.of(2020, 3, 31), LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 31)},
            {LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 1), LocalDate.of(2020, 6, 30)},
            {LocalDate.of(2020, 5, 15), LocalDate.of(2020, 4, 1), LocalDate.of(2020, 6, 30)},
            {LocalDate.of(2020, 6, 30), LocalDate.of(2020, 4, 1), LocalDate.of(2020, 6, 30)},
            {LocalDate.of(2020, 7, 1), LocalDate.of(2020, 7, 1), LocalDate.of(2020, 9, 30)},
            {LocalDate.of(2020, 8, 15), LocalDate.of(2020, 7, 1), LocalDate.of(2020, 9, 30)},
            {LocalDate.of(2020, 9, 30), LocalDate.of(2020, 7, 1), LocalDate.of(2020, 9, 30)},
            {LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 1), LocalDate.of(2020, 12, 31)},
            {LocalDate.of(2020, 11, 15), LocalDate.of(2020, 10, 1), LocalDate.of(2020, 12, 31)},
            {LocalDate.of(2020, 12, 31), LocalDate.of(2020, 10, 1), LocalDate.of(2020, 12, 31)},
        };
    }

    @Test(dataProvider = "currentQuarterComputationProvider")
    public void testGetCorrespondingQuarter(final LocalDate date, final LocalDate beginQuarter, final LocalDate endQuarter) {
        final Pair<LocalDate, LocalDate> quarter = DateTools.getCorrespondingQuarter(date);
        assertEquals(quarter.first(), beginQuarter);
        assertEquals(quarter.second(), endQuarter);
    }

    @DataProvider
    public static Object[][] previousQuarterComputationProvider() {
        return new Object[][]{
            {LocalDate.of(2020, 1, 1), LocalDate.of(2019, 10, 1), LocalDate.of(2019, 12, 31)},
            {LocalDate.of(2020, 2, 15), LocalDate.of(2019, 10, 1), LocalDate.of(2019, 12, 31)},
            {LocalDate.of(2020, 3, 31), LocalDate.of(2019, 10, 1), LocalDate.of(2019, 12, 31)},
            {LocalDate.of(2020, 4, 1), LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 31)},
            {LocalDate.of(2020, 5, 15), LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 31)},
            {LocalDate.of(2020, 6, 30), LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 31)},
            {LocalDate.of(2020, 7, 1), LocalDate.of(2020, 4, 1), LocalDate.of(2020, 6, 30)},
            {LocalDate.of(2020, 8, 15), LocalDate.of(2020, 4, 1), LocalDate.of(2020, 6, 30)},
            {LocalDate.of(2020, 9, 30), LocalDate.of(2020, 4, 1), LocalDate.of(2020, 6, 30)},
            {LocalDate.of(2020, 10, 1), LocalDate.of(2020, 7, 1), LocalDate.of(2020, 9, 30)},
            {LocalDate.of(2020, 11, 15), LocalDate.of(2020, 7, 1), LocalDate.of(2020, 9, 30)},
            {LocalDate.of(2020, 12, 31), LocalDate.of(2020, 7, 1), LocalDate.of(2020, 9, 30)},
        };
    }

    @Test(dataProvider = "previousQuarterComputationProvider")
    public void testGetPreviousQuarter(final LocalDate date, final LocalDate beginQuarter, final LocalDate endQuarter) {
        final Pair<LocalDate, LocalDate> quarter = DateTools.getPreviousQuarter(date);
        assertEquals(quarter.first(), beginQuarter);
        assertEquals(quarter.second(), endQuarter);
    }
}