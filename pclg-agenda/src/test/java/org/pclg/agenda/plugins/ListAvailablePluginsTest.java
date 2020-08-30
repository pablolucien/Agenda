package org.pclg.agenda.plugins;

import org.testng.annotations.Test;

public class ListAvailablePluginsTest {
    ListAvailablePlugins plugin = new ListAvailablePlugins();

    @Test
    public void testExecute() {
        plugin.execute(null, null, (String) null);
    }
}