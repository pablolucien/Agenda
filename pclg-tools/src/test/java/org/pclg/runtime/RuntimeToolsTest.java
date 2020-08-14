package org.pclg.runtime;

import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RuntimeToolsTest {

    @Test
    public void testGetExecutionPath() {
        final File executionPath = RuntimeTools.getExecutionPath(getClass());
        assertNotNull(executionPath);
        assertEquals(executionPath.getName(), "pclg-tools");
    }

}