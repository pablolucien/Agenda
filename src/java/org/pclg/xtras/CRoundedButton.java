// http://forum.java.sun.com/thread.jsp?forum=31&thread=294117
// author smg123
package org.pclg.xtras;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;

public class CRoundedButton extends JButton implements MouseListener {
	public static final int BUTTON_ROUNDEDGES = 0;
	public static final int BUTTON_ROUND = 1;
	public static final int BUTTON_TRIANGLE = 2;
    private static final long serialVersionUID = -8935457570111528694L;

    private String text;
	private boolean mouseIn;
	private int m_nMode;//default

	private CRoundedButton(final String s) {
		super(s);
		text = s;
		setBorderPainted(false);
		addMouseListener(this);
		setContentAreaFilled(false);
	}

	public CRoundedButton() {
		setBorderPainted(false);
		addMouseListener(this);
		setContentAreaFilled(false);
	}

	void setMode(final int nMode)	// No está implementado
	{
		m_nMode = nMode;
	}

	public void paintComponent(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;

		if (getModel().isPressed()) {
			g.setColor(Color.pink);
			g2.fillRect(3, 3, getWidth() - 6, getHeight() - 6);
		}

		super.paintComponent(g);

		if (mouseIn) {
			g2.setColor(Color.red);
		} else {
			g2.setColor(new Color(128, 0, 128));
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(1.2f));
		g2.draw(new RoundRectangle2D.Double(1, 1, (getWidth() - 3),
				(getHeight() - 3), 12, 8));
		g2.setStroke(new BasicStroke(1.5f));
		g2.drawLine(4, getHeight() - 3, getWidth() - 4, getHeight() - 3);
		g2.dispose();
	}

	public void mouseClicked(final MouseEvent e) {
	}

	public void mouseEntered(final MouseEvent e) {
		mouseIn = true;
	}

	public void mouseExited(final MouseEvent e) {
		mouseIn = false;
	}

	public void mousePressed(final MouseEvent e) {
	}

	public void mouseReleased(final MouseEvent e) {
	}

	public static void main(final String[] args) {
		final JFrame fr = new JFrame();
		final CRoundedButton button = new CRoundedButton("Salir");
		button.setMode(2);	// No está implementado
		fr .getContentPane().add(button, BorderLayout.NORTH);
		fr.pack();
		fr.setVisible(true);
	}
}
