package org.pclg.alter;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @since 09/01/2018.
 */
public class AlterUtilTest {
    @Test
    public void testInsertCharBeforeExtension() {
        assertEquals("name_.java", AlterUtil.insertCharsBeforeExtension("name.java", "_"));
        assertEquals("name_.gif", AlterUtil.insertCharsBeforeExtension("name.gif", "_"));
        assertEquals("name_.ts", AlterUtil.insertCharsBeforeExtension("name.ts", "_"));
        assertEquals("name_.c", AlterUtil.insertCharsBeforeExtension("name.c", "_"));
        assertEquals("name_.", AlterUtil.insertCharsBeforeExtension("name.", "_"));
        assertEquals("name_", AlterUtil.insertCharsBeforeExtension("name", "_"));
    }
}
