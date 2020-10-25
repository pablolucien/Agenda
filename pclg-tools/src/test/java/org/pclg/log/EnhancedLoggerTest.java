package org.pclg.log;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class EnhancedLoggerTest {
    @DataProvider
    public Object[][] positionsProvider() {
        return new Object[][] {
            {"pepe {} {}", new Object[] {"a", "b"}, "pepe a b"},
            {"{} pepe {}", new Object[] {"a", "b"}, "a pepe b"},
            {"{} pepe {}", new Object[] {1, true}, "1 pepe true"},
            {"{} {} pepe", new Object[] {"a", "b"}, "a b pepe"},
            {"{} {} pepe", new Object[] {"a"}, "a {} pepe"},
            {"pepe {}", new Object[] {"a", "b"}, "pepe a"},
        };
    }

    @Test(dataProvider = "positionsProvider")
    public void testGetPosition(final String format, final Object[] args, final String expected) {
        assertEquals(EnhancedLogger.resolve(format, args), expected);
    }
}