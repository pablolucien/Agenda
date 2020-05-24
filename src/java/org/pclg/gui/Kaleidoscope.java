package org.pclg.gui;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Draws a Kaleidoscope in a component. Can't remenmber where the hell I found it.
 */
public class Kaleidoscope extends EmptyBorder {
    private static final int MAX_BYTE_SIZE = 255;
    private static final int ALPHA = 128;
	private final JComponent component;
	private static final int SIDE = 300;
	private final int slices;
	private final BufferedImage[] images;
	private double rotationAngle;
	private final Timer timer;

	public Kaleidoscope(final JComponent component, final int requestedSlices) {
		super(0, 0, 0, 0);
		this.component = component;
        slices = requestedSlices % 2 == 0 ? requestedSlices : requestedSlices + 1;    // slices must be even
        images = prepareImages();
        final Graphics2D g2 = adjust(images[slices - 1].createGraphics());
		g2.scale(1.0, -1.0);
		g2.drawImage(images[0], 0, -SIDE, null);
		g2.dispose();
		AffineTransform at = AffineTransform.getRotateInstance(-4 * Math.PI / slices, SIDE / 2, SIDE / 2);
		for (int i = 2; i < slices; i += 2) {
			createImage(images[i], images[i - 2], at);
		}
		at = AffineTransform.getRotateInstance(4 * Math.PI / slices, SIDE / 2, SIDE / 2);
		for (int i = slices - 3; i >= 1; i -= 2) {
			createImage(images[i], images[i + 2], at);
		}
		timer = new Timer(50, evt -> {
			rotationAngle += 2 * Math.PI / 400;
			this.component.repaint();
		});
		timer.start();
	}

    private BufferedImage[] prepareImages() {
        final BufferedImage[] imgs = new BufferedImage[slices];
        final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        for (int i = 0; i != slices; ++i) {
            imgs[i] = gc.createCompatibleImage(SIDE, SIDE, Transparency.TRANSLUCENT);
        }
        final Graphics2D g2 = adjust(imgs[0].createGraphics());
        final Random rnd = new Random();
        final RectangularShape[] shapes = {new Ellipse2D.Double(), new Rectangle2D.Double()};
        for (int i = 0, j = 0; i < 40; ++i, j = i % shapes.length) {
            shapes[j].setFrame(rnd.nextDouble() * SIDE, rnd.nextDouble() * SIDE, rnd.nextDouble() * SIDE / 3, rnd.nextDouble() * SIDE / 3);
            final Color clr1 = new Color(rnd.nextInt(MAX_BYTE_SIZE), rnd.nextInt(MAX_BYTE_SIZE), rnd.nextInt(MAX_BYTE_SIZE), ALPHA);
            final Color clr2 = new Color(rnd.nextInt(MAX_BYTE_SIZE), rnd.nextInt(MAX_BYTE_SIZE), rnd.nextInt(MAX_BYTE_SIZE), ALPHA);
            g2.setPaint(new GradientPaint(0, 0, clr1, 50, 75, clr2, true));
            g2.fill(shapes[j]);
        }
        g2.dispose();
        return imgs;
    }

    public void stop() {
		timer.stop();
	}

	public void start() {
		timer.start();
	}

	public boolean isRunning() {
		return timer.isRunning();
	}

	private static void createImage(final BufferedImage target,
            final BufferedImage source, final AffineTransform at) {
		final Graphics2D g2 = adjust(target.createGraphics());
		g2.drawRenderedImage(source, at);
		g2.dispose();
	}

	private static Graphics2D adjust(final Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		return g2;
	}

	@Override
	public void paintBorder(final Component c, final Graphics g, final int x,
			final int y, final int width, final int height) {
		//g.setColor(new Color(0, 0, 0, 0));
		//g.fillRect(50, 100, SIDE, SIDE);
		final double delta = 360.0 / slices; //degress
		double angle = 0;
		Arc2D arc = new Arc2D.Double(0, 0, SIDE, SIDE, 0, delta, Arc2D.PIE);
		final Graphics2D g2 = adjust((Graphics2D) g);
		final Shape oldClip = g2.getClip();
		g2.clip(arc);
		g2.rotate(rotationAngle, SIDE / 2, SIDE / 2);
		for (int ii = 0; ii < slices; ii++) {
			final int factor = ii % 2 == 0 ? -1 : 1;
			g2.drawRenderedImage(images[ii], null);
			g2.rotate(factor * rotationAngle, SIDE / 2, SIDE / 2);
			g2.setClip(oldClip);
			arc = new Arc2D.Double(0, 0, SIDE, SIDE, angle += delta, delta, Arc2D.PIE);
			g2.clip(arc);
			g2.rotate(factor * rotationAngle, SIDE / 2, SIDE / 2);
		}
	}

	public static void main(final String[] args) {
		final JFrame f = new JFrame("Kaleidoscope");
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		final JTree t = new JTree();
		t.setBorder(new Kaleidoscope(t, 6));
		f.getContentPane().add(t);
		f.setSize(800, 600);
		f.setVisible(true);
	}
}



