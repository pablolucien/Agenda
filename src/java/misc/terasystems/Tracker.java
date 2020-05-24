package misc.terasystems;
/**
	Tracker.java
	Applet que muestra una imagen
*/

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

/**
	Applet que muestra una imagen y va marcando una posicion sobre ella
*/
public class Tracker extends Applet implements Runnable {
	private static final int MAX_POINTS = 100;
    private static final long serialVersionUID = 4236245987364293768L;
    private Image myImage;
	private Image carrito;
	private int coordX;
	private int coordY;
	private final int RADIUS = 6;
	private Thread updater;
	Image offImage;
	Graphics offGC;
	private boolean withTrail;
	private final int [] trailX = new int[MAX_POINTS];
	private final int [] trailY = new int[MAX_POINTS];
	private int currPos;

	public String getAppletInfo() {
        return(getClass().getName() + ": escrita por El Zorro, copyright, etc.");
	}

	public void init() {
		String imagen;

        System.out.println("Inicializando applet " + getClass().getName());
		imagen = getParameter("imagen");
		if(imagen == null)
			imagen = "images/logojava.gif";
		myImage = getImage(getCodeBase(), imagen);
		carrito = getImage(getCodeBase(), "carro.gif");
		final MediaTracker tracker = new MediaTracker(this);
		tracker.addImage(myImage, 0);
		try {
			tracker.waitForID(0);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
		}
		coordX = getSize().width / 2;
		coordY = getSize().height / 2;
/*
		offImage = createImage(myImage.width, myImage.height);
		offGC = offImage.getGraphics();
*/
		setLayout(new BorderLayout());
		final Choice tipo = new Choice();
		tipo.addItem("Snapshot");
		tipo.addItem("Trail");
		final Panel p = new Panel();
		p.add(tipo);
		add("South", p);
		// Coordenadas iniciales fuera del area de dibujo
		for(int i = 0; i < MAX_POINTS; i++) {
			trailX[i] = -10;
			trailY[i] = -10;
		}
		updater = new Thread(this);
		updater.start();
	}

	public void start() {
		updater.resume();
	}

	public void stop() {
		updater.suspend();
	}

	public void destroy() {
		updater.stop();
	}

	public void update(final Graphics g) {
		if(myImage != null)
			g.drawImage(myImage, 0, 0, this);
		if(withTrail) {
			g.setColor(Color.black);
			for(int i = currPos; i < MAX_POINTS; i++)	// eventualmente de currPos en adelante estaran los puntos mas antiguos
				g.fillOval(trailX[i] - RADIUS / 2, trailY[i] - RADIUS / 2, RADIUS, RADIUS);
			for(int i = 0; i < currPos; i++)	// eventualmente de 0 hasta currPos en adelante estaran los puntos mas recientes
				g.fillOval(trailX[i] - RADIUS / 2, trailY[i] - RADIUS / 2, RADIUS, RADIUS);
		}
		g.setColor(Color.red);
//		g.fillOval(coordX - RADIUS, coordY - RADIUS, RADIUS * 2, RADIUS * 2);
		g.drawImage(carrito, coordX, coordY, this);
	}

	public void run() {
		// Intentamos modificar la imagen periodicamente
		while(true) {
			try { Thread.sleep(5000); }
			catch(final InterruptedException ex) {Thread.currentThread().interrupt();}
/*
			coordX = (int)(Math.random() * size().width);
			coordY = (int)(Math.random() * size().height);
*/
			coordX += 10 - (int)(Math.random() * 20);
			coordY += 10 - (int)(Math.random() * 20);
// los guardamos aunque no los mostremos			if(withTrail) {
				trailX[currPos] = coordX;
				trailY[currPos] = coordY;
				currPos++;
				currPos %= MAX_POINTS;
// los guardamos aunque no los mostremos			}
			repaint();
		}

	}

	public boolean action(final Event e, final Object o) {
		if(e.target instanceof Choice) {
			if(((String) o).equals("Trail"))
				withTrail = true;
			else
				withTrail = false;
			repaint();
			return(true);
		}
		return(false);
	}

	public boolean mouseDown(final Event e, final int x, final int y) {
		if(e.target instanceof Choice) {
			return(false);
		}

		// Modificamos la imagen
		final int [] myArray = new int[myImage.getWidth(this) * myImage.getHeight(this)];
		final PixelGrabber pg = new PixelGrabber(myImage, 0, 0, myImage.getWidth(this), myImage.getHeight(this), myArray, 0, myImage.getWidth(this));
		try {
			pg.grabPixels();
		} catch (final InterruptedException ex) {
			System.err.println("interrupted waiting for pixels!");
			Thread.currentThread().interrupt();
			return(false);
		}
		for(int i = 0; i < myArray.length; i++)
			myArray[i] += 30;
		final MemoryImageSource mis = new MemoryImageSource(myImage.getWidth(this), myImage.getHeight(this),myArray, 0, myImage.getWidth(this));
		myImage = createImage(mis);
		repaint();

		return(true);
	}
}
