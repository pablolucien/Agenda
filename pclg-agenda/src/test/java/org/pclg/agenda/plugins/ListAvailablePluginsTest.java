package org.pclg.agenda.plugins;

import org.pclg.runtime.RuntimeTools;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class ListAvailablePluginsTest {
    ListAvailablePlugins plugin = new ListAvailablePlugins();

    @Test
    public void testExecute() {
        plugin.execute(null, null, null);
    }
}