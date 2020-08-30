package org.pclg.runtime;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class RuntimeToolsTest {

    @Test
    public void testGetExecutionPath() throws IOException {
        final File executionPath = RuntimeTools.getExecutionPath(getClass());
        assertNotNull(executionPath);
        assertTrue(executionPath.exists());
        assertEquals(executionPath.getName(), "pclg-tools");
    }

    @Test(enabled = false)
    public void testGetExecutionPathInJar() throws IOException {
        final File executionPath = RuntimeTools.getExecutionPath(String.class);
        assertNotNull(executionPath);
        assertTrue(executionPath.exists());
        assertEquals(executionPath.getName(), "rt.jar");
    }
}