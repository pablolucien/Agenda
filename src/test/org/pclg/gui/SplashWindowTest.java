package org.pclg.gui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.ImageIcon;

public class SplashWindowTest {
    private static final String[] messages = {
        "Abriendo base de datos",
        "corta",
        "Etiqueta larga larga y extensa wide estilo aleman con muchas letras inutiles"
    };
    private static int ii;

    private SplashWindowTest() {
    }

    public static void main(final String[] args) throws Exception {
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
        try (BufferedReader d = new BufferedReader(new InputStreamReader(System.in))) {
            while (d.readLine() != null) {
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