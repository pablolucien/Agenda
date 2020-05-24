package org.pclg.disquisiciones;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Random;

import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

/**
 * Choose three points A, B, C that form an equilateral triangle and a random point
 * (this will be the starting point S). Throw a die; if it's 1 or 2 draw a point half way
 * between S and A; similarly if it's 3 or 4 draw it between S an B, if 5 or 6 then
 * between S and C. This will be the new starting point. Repeat ad libitum.
 * <p>
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 15/08/17 8:32
 */
public final class GameOfChaos extends JFrame {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final int PLOTTER_SIZE = 1000;
    private static final int NR_POINTS = 100_000;
    private static final long serialVersionUID = 6582475037240164158L;
    private final Point[] points = new Point[NR_POINTS];
    //private final Canvas canvas = new Canvas();
    private final JPanel canvas = new JPanel();
    private final JCheckBox highlightTick = new JCheckBox("highlight", true);
    private final JCheckBox parallelTick = new JCheckBox("parallel", true);
    private final JCheckBox cycleColorsTick = new JCheckBox("cycleColors", false);
    private int pointPointer;
    private int nrVertices = 3;
    private Point[] vertices;
    private final Color[] colors = {Color.red, Color.yellow, Color.green};
    private int canvasHalfWidth = canvas.getWidth() / 2;
    private int canvasHalfHeight = canvas.getHeight() / 2;

    private GameOfChaos() {
        setTitle("Sierpinski triangle");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                canvasHalfWidth = canvas.getWidth() / 2;
                canvasHalfHeight = canvas.getHeight() / 2;
                super.componentResized(e);
            }
        });
        canvas.setBackground(Color.lightGray);
        add(canvas, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        setSize(PLOTTER_SIZE, PLOTTER_SIZE);
        setUpVertices();
    }

    private JPanel createControlPanel() {
        final JPanel panel = new JPanel();
        final JButton button = new JButton("GO!");
        button.addActionListener(e -> {
            playTheGame();
            repaint();
        });
        highlightTick.addActionListener(e -> repaint());
        cycleColorsTick.addActionListener(e -> repaint());
        parallelTick.addActionListener(e -> repaint());
        panel.add(highlightTick);
        panel.add(cycleColorsTick);
        panel.add(parallelTick);
        final JSpinner nrVerticesChooser = new JSpinner();
        nrVerticesChooser.setValue(Integer.valueOf(nrVertices));
        nrVerticesChooser.addChangeListener(e -> {
            nrVertices = Integer.valueOf(nrVerticesChooser.getValue().toString());
            setUpVertices();
        });
        panel.add(nrVerticesChooser);
        panel.add(button);
        return panel;
    }

    private void setUpVertices() {
        vertices = new Point[nrVertices];
        final double angle = 2 * Math.PI / nrVertices;
        final int radius = 500;
        for (int ii = 0; ii < nrVertices; ii++) {
            vertices[ii] = new Point((int) (radius * cos(ii * angle)), (int) (radius * sin(ii * angle)));
        }
    }

    private void playTheGame() {
        final Cursor previousCursor = getCursor();
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            pointPointer = 0;
            for (final Point vertice : vertices) {
                plot(vertice);
            }
            final Random random = new Random();
            Point startingPoint = new Point(random.nextInt(canvas.getSize().width) - canvas.getSize().width / 2,
                random.nextInt(canvas.getSize().width) - canvas.getSize().width / 2);
            for (int ii = 0; ii < NR_POINTS - nrVertices; ii++) {
                startingPoint = plotAndGetNextPoint(startingPoint, random);
            }
        } finally {
            setCursor(previousCursor);
        }
    }

    private Point plotAndGetNextPoint(final Point startingPoint, final Random random) {
        final int diceValue = random.nextInt(nrVertices);
        final Point nextPoint = middlePoint(startingPoint, vertices[diceValue]);
        plot(startingPoint);
        return nextPoint;
    }

    private void plot(final Point point) {
        points[pointPointer++] = point;
    }

    private void drawAxis(final Graphics graphics) {
        final Dimension canvasSize = canvas.getSize();
        final int height = canvasSize.height;
        final int half_height = height / 2;
        final int width = canvasSize.width;
        final int half_width = width / 2;
        graphics.setColor(Color.black);
        graphics.drawLine(0, half_height, width, half_height);
        graphics.drawLine(half_width, 0, half_width, height);
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        drawAxis(graphics);
        graphics.setColor(Color.black);
        if (parallelTick.isSelected()) {
            plotPointsParallel(graphics);
        } else {
            plotPoints(graphics);
        }
        if (highlightTick.isSelected()) {
            highlightPoint(graphics, colors[0], points[nrVertices]);
            highlightPoint(graphics, colors[1], points[nrVertices + 1]);
            highlightPoint(graphics, colors[2], points[nrVertices + 2]);
        }
        setCursor(Cursor.getDefaultCursor());
    }

    private void plotPointsParallel(final Graphics graphics) {
        final int[] ii = {0};
        Arrays.stream(points).parallel().forEach(point -> {
            //LOGGER.debug("ii = " + ii + ": " + Thread.currentThread());
            final Point pointInScreen = new Point(
                convertCartesian2ScreenX(point.x),
                convertCartesian2ScreenY(point.y));
            if (cycleColorsTick.isSelected()) {
                graphics.setColor(colors[ii[0]++ % colors.length]);
            }
            graphics.drawLine(pointInScreen.x, pointInScreen.y, pointInScreen.x, pointInScreen.y);
        });
    }

    private void plotPoints(final Graphics graphics) {
        for (int ii = 0; ii < pointPointer; ii++) {
            //LOGGER.debug("ii = " + ii + ": " + Thread.currentThread());
            final Point pointInScreen = new Point(
                convertCartesian2ScreenX(points[ii].x),
                convertCartesian2ScreenY(points[ii].y));
            if (cycleColorsTick.isSelected()) {
                graphics.setColor(colors[ii % colors.length]);
            }
            graphics.drawLine(pointInScreen.x, pointInScreen.y, pointInScreen.x, pointInScreen.y);
        }
    }

    private void highlightPoint(final Graphics graphics, final Color color, final Point point) {
        graphics.setColor(color);
        final Point pointInScreen = new Point(
            convertCartesian2ScreenX(point.x),
            convertCartesian2ScreenY(point.y));
        graphics.fillOval(pointInScreen.x, pointInScreen.y, 10, 10);
    }

    private static Point middlePoint(final Point s, final Point t) {
        return new Point(s.x + t.x >> 1, s.y + t.y >> 1);
    }

    private int convertCartesian2ScreenX(final int x) {
        return canvasHalfWidth + x;
    }

    private int convertCartesian2ScreenY(final int y) {
        return canvasHalfHeight - y;
    }

    public static void main(final String[] args) {
        LOGGER.setLevel(Level.ALL);
        final GameOfChaos plotter = new GameOfChaos();
        GUITools.center(plotter, null);
        plotter.setVisible(true);
        plotter.playTheGame();
    }
}
