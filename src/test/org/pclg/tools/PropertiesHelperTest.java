package org.pclg.tools;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.IllegalArgumentException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * @since 16/10/2018.
 */
public class PropertiesHelperTest {
    private static final Properties PROPERTIES = new Properties();
    private static final String GOOD_VAL_KEY = "good val";
    private static final String BAD_VAL_KEY = "bad val";
    private static final String INVALID_KEY = "invalid key";
    private static final int GOOD_VALUE = 10;
    private static final int DEFAULT_VALUE = 20;

    @BeforeClass
    public static void init() {
        PROPERTIES.setProperty(GOOD_VAL_KEY, String.valueOf(GOOD_VALUE));
        PROPERTIES.setProperty(BAD_VAL_KEY, BAD_VAL_KEY);
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
}