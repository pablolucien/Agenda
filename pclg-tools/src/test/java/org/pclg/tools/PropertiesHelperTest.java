package org.pclg.tools;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @since 16/10/2018.
 */
public class PropertiesHelperTest {
    private static final Properties PROPERTIES = new Properties();
    private static final String GOOD_VAL_KEY = "good_val";
    private static final String BAD_VAL_KEY = "bad_val";
    private static final String INVALID_KEY = "invalid_key";
    private static final int GOOD_VALUE = 10;
    private static final int DEFAULT_VALUE = 20;

    @BeforeClass
    public static void init() throws IOException {
        // This is already a test of method loadPropertiesFromClasspath() :)
        PropertiesHelper.loadPropertiesFromClasspath(PROPERTIES, "PropertiesHelperTest.properties");
    }

    @Test
    public void getIntFromPropertiesGoodVal() {
        final int goodVal = PropertiesHelper.getIntFromProperties(PROPERTIES, GOOD_VAL_KEY, DEFAULT_VALUE);
        assertEquals(goodVal, GOOD_VALUE);
    }

    @Test
    public void getIntFromPropertiesBadVal() {
        final int badVal = PropertiesHelper.getIntFromProperties(PROPERTIES, BAD_VAL_KEY, DEFAULT_VALUE);
        assertEquals(badVal, DEFAULT_VALUE);
    }

    @Test
    public void getintfrompropertiesInvalidKey() {
        final int badVal = PropertiesHelper.getIntFromProperties(PROPERTIES, INVALID_KEY, DEFAULT_VALUE);
        assertEquals(badVal, DEFAULT_VALUE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getIntFromPropertiesNullKey() {
        PropertiesHelper.getIntFromProperties(PROPERTIES, null, DEFAULT_VALUE);
    }

    @Test
    public void testGetPropertiesToPersist_with_property_set() {
        final var propertiesToPersist = PropertiesHelper.getPropertiesToPersist(PROPERTIES,
            "PropertiesHelperTest.customPropertiesToPersist");
        assertNotNull(propertiesToPersist);
        assertEquals(propertiesToPersist.size(), 3);
        final var stringPair1 = propertiesToPersist.get(0);
        assertEquals(stringPair1.first(), "PropertiesHelperTest.oldestDate");
        assertEquals(stringPair1.second(), "2020-01-01");

        final var stringPair2 = propertiesToPersist.get(1);
        assertEquals(stringPair2.first(), "PropertiesHelperTest.PortfolioGraphWidth");
        assertEquals(stringPair2.second(), "600");

        final var stringPair3 = propertiesToPersist.get(2);
        assertEquals(stringPair3.first(), "PropertiesHelperTest.importPrices.lastDir");
        assertEquals(stringPair3.second(), "");
    }

    @Test
    public void testGetPropertiesToPersist_without_property_set() {
        final var propertiesToPersist = PropertiesHelper.getPropertiesToPersist(PROPERTIES,
            "PropertiesHelperTest.this.does.not.exist");
        assertNotNull(propertiesToPersist);
        assertEquals(propertiesToPersist.size(), 0);
    }
}