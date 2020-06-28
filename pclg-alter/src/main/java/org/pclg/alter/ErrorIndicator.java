package org.pclg.alter;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import javax.swing.JButton;
import javax.swing.Timer;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;

/**
 * Indicador de errores.
 *
 * @author El Coyote Cojo
 * @version Unknown.
 * @since 24-nov-2004    (Refactorizado; hasta hoy era una clase interna de
 *        Alter.
 */
final class ErrorIndicator extends JButton implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = -665192275210860844L;

    /** El sonido que avisa que hay en error. */
    private AudioClip theSound;
	private static final int MILLIS_IN_SECOND = 1000;

    /** Este timer controila el 'blinking' del indicador. */
    private final Timer timer = new Timer(MILLIS_IN_SECOND, new ActionListener() {
        private boolean flip;

        @Override
		public void actionPerformed(final ActionEvent ev) {
            setForeground((flip ^= true) ? Color.blue : Color.red);
        }
    });
    /** El texto a mostrar cuando hay errores. */
    private final String textToShow;

	/**
	 * Constructor por omision.
	 * @param textToShow  el texto a mostrar cuando hay errores.
	 * @param audioClipName el sonido que avisa que hay en error.
	 */
	ErrorIndicator(final String textToShow, final String audioClipName) {
		final File audioClip = new File(audioClipName);
		this.textToShow = textToShow;
		try {
			theSound = Applet.newAudioClip(audioClip.toURI().toURL());  // ¿¡¿¡¿¡ Applet !?!?!?
		} catch (final MalformedURLException ex) {
			LOGGER.error("Not using sound", ex);
		}

		addActionListener(this);
	}

    /**
     * Activa el indicador de errores.
     */
    void activate() {
        if (theSound != null) {
            theSound.play();
        }

        setText(textToShow);
        timer.start();
    }

    /**
     * Desactiva el indicador de errores.
     */
    void deactivate() {
        timer.stop();
        setText("");
    }

    @Override
	public void actionPerformed(final ActionEvent ev) {
        deactivate();
    }
}
