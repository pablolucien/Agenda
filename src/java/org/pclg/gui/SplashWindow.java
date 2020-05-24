package org.pclg.gui;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Muestra una ventana "Splash" y opcionalmente ejecuta un clip de audio
 *
 * @author El Zorro
 * @version 1.00 2000/04/13
 */
public class SplashWindow extends JFrame {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = 7718125751788425954L;
    private final JLabel status = new JLabel();

    /**
     * Construye una SplashWindow sin audio
     *
     * @param icon La imagen a mostrar
     */
    public SplashWindow(final Icon icon) {
        this(icon, null);
    }

    /**
     * Construye una SplashWindow con audio
     *
     * @param icon La imagen a mostrar
     * @param clipName  Path del clip a ejecutar
     */
    public SplashWindow(final Icon icon, final String clipName) {
        setUndecorated(true);
        final JLabel imageLabel = new JLabel(icon);
        imageLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        getContentPane().add(imageLabel, BorderLayout.CENTER);
        imageLabel.setLayout(new BorderLayout());
        imageLabel.add(status, BorderLayout.SOUTH);
        pack();
        final Dimension size = getSize();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (size.height > screenSize.height || size.width > screenSize.width) {
            setSize(screenSize);
        }
        GUITools.center(this, null);   // Centrar en la pantalla

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent event) {
                LOGGER.debug("mouseClicked:" + event.getX() + " - Y: " + event.getY());
            }

            @Override
            public void mouseDragged(final MouseEvent event) {
                setStatus("X:" + event.getX() + " - Y: " + event.getY());
                LOGGER.debug("mouseDragged -> X:" + event.getX() + " - Y: " + event.getY());
            }

            @Override
            public void mouseMoved(final MouseEvent event) {
                LOGGER.debug("mouseMoved:" + event.getX() + " - Y: " + event.getY());
            }
        });

        if (clipName != null) {
//            final File clipFile = new File(clipName);
//            if (clipFile.exists()) {
//            if (clipFile.exists()) {
            final URL resource = getClass().getResource(clipName);
            if (resource != null) {
                AudioClip theSound;
//                try {
//                    theSound = Applet.newAudioClip(clipFile.toURL());
                    theSound = Applet.newAudioClip(resource);
                    theSound.play();
//                } catch (final MalformedURLException e) {
                    // Ignore;
//                }
            }
        }
    }

    /**
     * Determina el color en el que se muestra la linea de status
     *
     * @param c El color a usar
     */
    public void setStatusColor(final Color c) {
        status.setForeground(c);
    }

    /**
     * Muestra una linea de informacion
     *
     * @param s El texto a mostrar
     */
    public void setStatus(final String s) {
        status.setText(s);
    }

}
