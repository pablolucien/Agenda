package tests;
// Autor: moschops
// http://forum.java.sun.com/thread.jsp?forum=57&thread=285792

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
* <p>Title: </p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2002</p>
* <p>Company: </p>
* @author unascribed
* @version 1.0
*/

public class LineSplitter extends JFrame implements MouseMotionListener
{

    private static final long serialVersionUID = -3781447260014744966L;

    private LineSplitter()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setupArea();
		setupFrame();
		initializeSizeInfo();
		
		mArea.addMouseMotionListener(this);
		mArea.addMouseListener(mMouse);
	}
	
	/**
	*
	*/
	public static void main(final String[] args)
	{
		new LineSplitter();
	}
	
	@Override
	public void mouseDragged(final MouseEvent m)
	{
		final Point p = m.getPoint();
		mDivider.setPosition((p.x / sCharWidth) + 1);
		mArea.repaint();
	}
	
	@Override
	public void mouseMoved(final MouseEvent m)
	{
		// jack
	}
	
	private void drawDivider(final Graphics g)
	{
		g.setColor(Color.black);
		final int offset = mDivider.getPosition();
		g.fillRect((offset * sCharWidth), 0, 3, mArea.getSize().height);
	}
	
	private void processRelease()
	{
		// use mDivider.getPosition() to get the split pos,
		// then use methods in JTextArea to divide up the lines
		// then perhaps create two new JTextAreas to hold the new
		// blocks of text?
		
		// i leave this to you - i must do some work now
	}
	
	private void initializeSizeInfo()
	{
		sMetrics = getFontMetrics(sEqualFont);
		sCharWidth = sMetrics.charWidth('t'); // whichever one you like - they're all the same width
	}
	
	private void setupArea()
	{
		mArea.setLineWrap(true);
		mArea.setFont(sEqualFont);
		mArea.setSelectionColor(mArea.getBackground()); // so you can't see the highlighter
	}
	
	private void setupFrame()
	{
		final Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		c.add(mArea, BorderLayout.CENTER);
		
		setBounds(600,600,200,200);
		setVisible(true);
	}
	
	// members
	private final JTextArea mArea = new SpecialTextArea();
	private final Divider mDivider = new Divider(0);
	private boolean mMouseDown;
	private static final Font sEqualFont = new Font("monospaced", Font.ITALIC, 14); // must use monospaced font
	private static FontMetrics sMetrics;
	private static int sCharWidth;
	
	private final MouseListener mMouse = new MouseAdapter()
	{
		public void mousePressed(final MouseEvent m)
		{
			mMouseDown = true;
			final Point p = m.getPoint();
			mDivider.setPosition((p.x / sCharWidth) + 1);
			mArea.repaint();
		}
		public void mouseReleased(final MouseEvent m)
		{
			mMouseDown = false;
			final Point p = m.getPoint();
			mDivider.setPosition((p.x / sCharWidth) + 1);
			mArea.repaint();
			processRelease();
		}
	};
	
	private class SpecialTextArea extends JTextArea
	{
        private static final long serialVersionUID = 7857951908960704147L;

        public void paint(final Graphics g)
		{
			super.paint(g);
			
			if(mMouseDown) {
				drawDivider(g);
			}
		}
	}

	private class Divider
	{
		Divider(final int initialPos)
		{
			position = initialPos;
		}

		void setPosition(final int pos)
		{
			position = pos;
		}

		int getPosition()
		{
			return position;
		}
		//
		private int position;
	}
}
