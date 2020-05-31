package org.pclg.gui;

import org.testng.annotations.Test;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SplashWindowTest {
    private static final String[] messages = {
        "Abriendo base de datos",
        "corta",
        "Etiqueta larga larga y extensa wide estilo aleman con muchas letras inutiles"
    };
    private static int ii;

    @Test
    public void testSplashWindow() throws Exception {
        final SplashWindow splashWindow = new SplashWindow(
            new ImageIcon(SplashWindowTest.class.getResource("/images/unknown-man.png")),
            "/sounds/success_sound.wav"
        );
        splashWindow.setStatusColor(Color.red);

        splashWindow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent event) {
                if (!cycleStatus(splashWindow)) {
                    System.exit(0);
                }
            }
        });

        splashWindow.setVisible(true);
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (reader.readLine() != null) {
                if (!cycleStatus(splashWindow)) {
                    System.exit(0);
                }
            }
        }
    }

    private static boolean cycleStatus(final SplashWindow splashWindow) {
        if (ii >= messages.length) {
            return false;
        }
        splashWindow.setStatus(messages[ii++]);
        return true;
    }
}