package org.pclg.gui;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.ToolBox;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Properties;

/**
 * Provides services for the management of the Look-n-Feel of swing applications.
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 19/08/17 13:10
 */
public final class LnFController {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    private LnFController() {
        /*
        try {
          //com.incors.plaf.alloy.AlloyTheme theme = new com.incors.plaf.alloy.themes.bedouin.BedouinTheme();
          //com.incors.plaf.alloy.AlloyTheme theme = new com.incors.plaf.alloy.themes.glass.GlassTheme();
          com.incors.plaf.alloy.AlloyTheme theme = new com.incors.plaf.alloy.themes.acid.AcidTheme();
          javax.swing.LookAndFeel alloyLnF = new com.incors.plaf.alloy.AlloyLookAndFeel(theme);
          //javax.swing.LookAndFeel alloyLnF = new com.incors.plaf.alloy.AlloyLookAndFeel();
          javax.swing.UIManager.setLookAndFeel(alloyLnF);
          com.incors.plaf.alloy.AlloyLookAndFeel.updateAllUIs();
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
          // You may handle the exception here
        }
        //*/

    }

    public static JMenu getLnFMenu(final Component targetComponent) {
        final JMenu lafMenu = new JMenu("Presentacion");
        final ButtonGroup lafGroup = new ButtonGroup();
        GUITools.addRadioButtonMenuItem(e -> setLAF(targetComponent, "com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
            lafMenu, lafGroup, "&Motif").setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_MASK));
        GUITools.addRadioButtonMenuItem(e -> setLAF(targetComponent, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"),
            lafMenu, lafGroup, "&Windows").setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK));
        GUITools.addRadioButtonMenuItem(e -> setLAF(targetComponent, "it.unitn.ing.swing.plaf.macos.MacOSLookAndFeel"),
            lafMenu, lafGroup, "M&ac OS").setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
        //	JMenu oyoahaSubMenu                     = GUITools.addMenu(lafMenu, "&OyoahaLook Look'n'Feel", null);
        //	JRadioButtonMenuItem tgangThemeMenuItem = ToolBox.addRadioButtonMenuItem(this, oyoahaSubMenu, lafGroup, "TgangTheme");

        final Properties plainLafProperties = new Properties();
        try {
            PropertiesHelper.loadPropertiesFromClasspath(plainLafProperties, "LnFController_PlainLafs.properties");
        } catch (final IOException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
        plainLafProperties.entrySet().forEach(lafEntry -> GUITools.addRadioButtonMenuItem(e -> setLAF(targetComponent, (String) lafEntry.getKey()),
                lafMenu, lafGroup, (String) lafEntry.getValue()));

        add2LAFMenu(targetComponent, lafMenu, lafGroup);

        lafMenu.setMnemonic(KeyEvent.VK_P);

        return lafMenu;
    }

    /**
     * agrega estas cosillas al menu
     */
    private static void add2LAFMenu(final Component targetComponent, final JMenu parent,
        final ButtonGroup buttonGroup) {
        final Properties lafProperties = new Properties();
        final Properties themesProperties = new Properties();
        try {
            PropertiesHelper.loadPropertiesFromClasspath(lafProperties, "LnFController_MetalLafs.properties");
            PropertiesHelper.loadPropertiesFromClasspath(themesProperties, "LnFController_Themes.properties");
        } catch (final IOException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
        lafProperties.entrySet().forEach(lafEntry -> {
            final JMenu subMenu = GUITools.addMenu(parent, (String) lafEntry.getValue(), null);
            themesProperties.entrySet().forEach(themeEntry ->
                GUITools.addRadioButtonMenuItem(e -> {
						parent.setSelected(true);
						setLAF(targetComponent, (String) lafEntry.getKey(), (String) themeEntry.getKey());
					},
                    subMenu, buttonGroup, (String) themeEntry.getValue()));
        });
    }

    /**
     * Cambia el look and feel
     */
    public static void setLAF(final Component targetComponent, final String laf) {
        setLAF(targetComponent, laf, null);
    }

    /**
     * Cambia el look and feel
     */
    public static void setLAF(final Component c, final LookAndFeel laf) {
        try {
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(c);
        } catch (final Exception ex) {
            LOGGER.error("No pude cargar el LookAndFeel: " + laf);
            ToolBox.showInfo(ex);
        }
    }

    /**
     * Cambia el look and feel
     */
    public static void setLAF(final Component c, final String laf, final String tema) {
        try {
            if (tema != null) {
                final Object theLAF = Class.forName(laf).newInstance();
                if (theLAF instanceof MetalLookAndFeel) {
                    final MetalTheme theme = (MetalTheme) Class.forName(tema)
                        .newInstance();
                    MetalLookAndFeel.setCurrentTheme(theme);
                    UIManager.setLookAndFeel((LookAndFeel) theLAF);
                    //LOGGER.error(laf + " -> " + tema);
                } else {
                    UIManager.setLookAndFeel(laf);
                }
            } else {
                UIManager.setLookAndFeel(laf);
            }
            SwingUtilities.updateComponentTreeUI(c);
        } catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ToolBox.showInfo(ex);
        }
    }

    /**
     * Cambia el look and feel
     */
    static void setXXXLAF(final Component targetComponent, final String laf, final String tema) {
        setLAF(targetComponent, laf, tema);
// 		if(tema != null) {
//			saveProperty("lafThemeName", tema);
//		}
        SwingUtilities.updateComponentTreeUI(targetComponent);
        // FIXME: Actualizar los subcomponentes (Pasarselos)
        //		SwingUtilities.updateComponentTreeUI(fileChooser);
        //		SwingUtilities.updateComponentTreeUI(sourceSelector);
        //		SwingUtilities.updateComponentTreeUI(targetSelector);
        //		if(out != null) {
        //			out.updateLAF();
        //		}
        //		if(err != null) {
        //			err.updateLAF();
        //		}
//		saveProperty("lafName", laf);
    }
}
