package misc.ingedigit;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


class FrameWithEvents extends Frame implements WindowListener {
    private static final long serialVersionUID = -4141159938481894253L;
    private static int instanceCount;	// Podemos tener mas de una ventana en la aplicacion
    private final String myName = getClass().getName();

    FrameWithEvents() {
        this("");
        setTitle(myName);
	}

	FrameWithEvents(final String s) {
		super(s);
		instanceCount++;
        addWindowListener(this);    // Me interesan los eventos
	}

	/**
		Punto de salida de la aplicacion: Libera los recursos y se suicida
	*/
	void exit() {
		setVisible(false);
		dispose();
		if(--instanceCount == 0) {
			System.exit(0);
		}
	}

    public void exitAll() {
		setVisible(false);
		dispose();
        System.exit(0);
	}

    public void start() {
       pack();
       setVisible(true);
    }

    // Todos estos putos metodos hay que "implementarlos" para poder
    // tener WindowClosing()
    @Override
	public void windowOpened(final WindowEvent e) { }
    @Override
	public void windowClosed(final WindowEvent e) { }
    @Override
	public void windowIconified(final WindowEvent e) { }
    @Override
	public void windowDeiconified(final WindowEvent e) { }
    @Override
	public void windowActivated(final WindowEvent e) { }
    @Override
	public void windowDeactivated(final WindowEvent e) { }

    @Override
	public void windowClosing(final WindowEvent e) {
        exit();
    }
}
