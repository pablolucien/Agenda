package org.pclg.tools;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @since 27/08/2019.
 */
public class NumberParserTest {
    @DataProvider(name = "testData")
    public static Object[][] testData() {
        return new Object[][] {
                {"tres mil quinientos ventisiete", 3_527L},
                {"dos millones cinco mil trescientos ventisiete", 2_005_327L},
//                {"cuatro millones treinta y siete mil trescientos ventisiete", 4_037_327L},
//                {"cuatro millones treinta y siete mil ochocientos cuarenta y cinco", 4_037_845L},
//                {"cuatro mil millones treinta y siete mil ochocientos cuarenta y cinco", 4_000_037_845L},
        };
    }

    @Test(dataProvider = "testData")
    public void testParsing(final String testExpression, final Long expectedResult) {
        final NumberParser numberParser = new NumberParser();
        assertEquals(numberParser.parseLong(testExpression), (long) expectedResult);
    }
}