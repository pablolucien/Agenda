package org.pclg.gui;

import java.util.Properties;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 11/10/2018.
 */
public class I18NManagerTest {

    @Test
    public void getInstance() {
        final Properties propertiesA = new Properties();
        propertiesA.setProperty("aa", "aaaaaaH");
        propertiesA.setProperty("oo", "ooooooH");
        final Properties propertiesB = new Properties();
        propertiesB.setProperty("aa", "aaaaaaH");
        propertiesB.setProperty("oo", "ooooooH");
        final Properties propertiesC = new Properties();
        propertiesC.setProperty("oo", "aaaaaaH");
        propertiesC.setProperty("aa", "ooooooH");
        I18NManager.usePool = true;
        final I18NManager managerA1 = I18NManager.getInstance(propertiesA);
        final I18NManager managerA2 = I18NManager.getInstance(propertiesA);
        final I18NManager managerB1 = I18NManager.getInstance(propertiesB);
        final I18NManager managerB2 = I18NManager.getInstance(propertiesB);
        final I18NManager managerC1 = I18NManager.getInstance(propertiesC);
        final I18NManager managerC2 = I18NManager.getInstance(propertiesC);
        assertSame(managerA1, managerA2);
        assertSame(managerB1, managerB2);
        assertSame(managerC1, managerC2);
        assertSame(managerA1, managerB1);
        assertNotSame(managerA1, managerC1);
    }
}
